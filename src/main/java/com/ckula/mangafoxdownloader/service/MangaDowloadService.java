package com.ckula.mangafoxdownloader.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.ckula.mangafoxdownloader.model.Chapter;
import com.ckula.mangafoxdownloader.model.Manga;
import com.ckula.mangafoxdownloader.model.MangaState;
import com.ckula.mangafoxdownloader.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MangaDowloadService {
    private Manga MANGA = null;

    private int MANGA_STATE = MangaState.UNKNOWN;

    private final String MANGAFOX_URL = "http://mangafox.me";
    private final String MANGAFOR_XML_URL = "http://mangafox.me/rss/";

    private final String NOT_AVAILABLE = "NA";
    private final String TO_BE_DETERMINED = "TBD";

    private final String VOLUME_FOLDER = "v_";
    private final String CHAPTER_FOLDER = "ch_";

    private final int MAX_TRIES = 3;
    private final int TIME_OUT_IN_MILLIS = 3000;

    private final Connection JSOUP_CONNECTION = Jsoup.connect(MANGAFOX_URL).timeout(TIME_OUT_IN_MILLIS)
	    .ignoreContentType(true).parser(Parser.htmlParser());

    private final Connection JSOUP_XML_CONNECTION = Jsoup.connect(MANGAFOX_URL).timeout(TIME_OUT_IN_MILLIS)
	    .parser(Parser.xmlParser());

    private final Map<Chapter, String> CHAPTERS_WITH_ERRORS = new HashMap<Chapter, String>();

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private List<Chapter> UPDATED_CHAPTERS = new ArrayList<Chapter>();

    public MangaDowloadService(String mangaName) {
	this.MANGA = new Manga(mangaName);

	switch (retrieveMangaState()) {
	case MangaState.VALID:
	    retrieveMangaInfo();
	    break;
	}
    }

    public void downloadManga(String mangaName) {
	System.out.println("[START] Starting downloading manga : " + MANGA.getName().toUpperCase() + " ...");
	System.out.println();

	MANGA.getChapters().addAll(getAllChapters(MANGA.getName()));

	if (MANGA.getChapters().size() > 0) {
	    Set<Chapter> chaptersToUpdate = getChaptersToUpdate(MANGA.getChapters());
	    if (chaptersToUpdate.size() == 0) {
		System.out.println("[UP TO DATE] The manga " + MANGA.getName().toUpperCase() + " is up to date !");
	    } else {
		downloadChapters(chaptersToUpdate);
	    }
	} else {
	    System.err.println("No chapters have been found. Nothing has been downloaded");
	}
	System.out.println("------------------------------------------------------------");
    }

    public void downloadSpecificVolume(String volumeOption, String volumeArgument) {
	if ("-v".equalsIgnoreCase(volumeOption) && volumeArgument.matches("(?i)([0-9]+|NA|TBD){1}")) {

	    Set<Chapter> chapters = getAllChapters(volumeOption);
	    chapters = getSpecificVolume(chapters, volumeArgument);
	    if (chapters.size() > 0) {
		downloadChapters(chapters);
	    } else {
		System.err.println("No chapters have been found for the volume " + volumeArgument);
	    }
	    System.out.println("------------------------------------------------------------");

	} else {
	    printUnknowOptions();
	}
    }

    public void downloadSpecificChapter(String volumeOption, String volumeArgument, String chapitreOption,
	    String chapitreArgument) {
	if ("-v".equalsIgnoreCase(volumeOption) && "-c".equalsIgnoreCase(chapitreOption)
		&& volumeArgument.matches("(?i)([0-9]+|NA|TBD){1}") && chapitreArgument.matches("[0-9.]+")) {

	    Set<Chapter> chapters = getAllChapters(MANGA.getName());

	    chapters = getSpecificChapter(chapters, volumeArgument, chapitreArgument);
	    if (chapters.size() > 0) {
		downloadChapters(chapters);
	    } else {
		System.err.println(
			"No chapters " + chapitreArgument + " have been found for the volume " + volumeArgument);
	    }
	    System.out.println("------------------------------------------------------------");

	} else {
	    printUnknowOptions();
	}
    }

    private Set<Chapter> getAllChapters(String mangaName) {
	return getAllChapters(mangaName, false);
    }

    private Set<Chapter> getAllChapters(String mangaName, boolean checkNumberOfPages) {
	Set<Chapter> chapters = new TreeSet<Chapter>();

	Element rss;
	try {
	    rss = JSOUP_XML_CONNECTION.url(MANGA.getXmlLink()).get().getElementsByTag("rss").get(0);
	} catch (IOException e) {
	    System.err.println(
		    "[ERROR] Couldn't connect to mangafox.me. Check your Internet connection. Keep in mind that mangafox.me may be down.");
	    return chapters;
	} catch (IndexOutOfBoundsException ioobe) {
	    System.err.println("[ERROR] The manga " + MANGA.getName()
		    + " does not exist (or it is refered differently by MangaFox)");
	    return chapters;
	}
	Elements rssChapters = rss.getElementsByTag("item");

	for (Element elem : rssChapters) {
	    Chapter chapter = new Chapter();

	    chapter.setLink(elem.getElementsByTag("link").get(0).ownText());

	    String title = elem.getElementsByTag("title").get(0).ownText();

	    String volume = "";
	    String chap = "";

	    Pattern pattern = Pattern.compile("Vol ([0-9.TBD]+)");
	    Matcher matcher = pattern.matcher(title);

	    if (matcher.find()) {
		volume = matcher.group(1).replaceFirst("^0+(?!$)", "");
	    } else {
		volume = NOT_AVAILABLE;
	    }

	    pattern = Pattern.compile("Ch ([0-9.]+)");
	    matcher = pattern.matcher(title);

	    if (matcher.find()) {
		chap = matcher.group(1).replaceFirst("^0+(?!$)", "");
	    } else {
		chap = NOT_AVAILABLE;
	    }
	    chapter.setChapterNumber(chap);
	    chapter.setAssociatedVolume(volume);

	    if (checkNumberOfPages) {
		chapter.setPagesCount(getPagesCount(chapter));
	    }
	    chapters.add(chapter);
	}

	return chapters;
    }

    private Set<Chapter> getChaptersToUpdate(Set<Chapter> chapters) {
	Set<Chapter> chaptersToDownload = new TreeSet<Chapter>();
	for (Chapter chapter : chapters) {
	    File chapterDirectory = new File(
		    getChapterDirectory(MANGA.getName(), chapter.getAssociatedVolume(), chapter.getChapterNumber()));
	    if (!chapterDirectory.exists() || chapterDirectory.list().length < chapter.getPagesCount()
		    || chapter.getPagesCount() == 0) {
		chaptersToDownload.add(chapter);
	    }
	}
	return chaptersToDownload;
    }

    private void downloadOneChapter(Chapter chapter) {
	if (chapter.getPagesCount() == 0) {
	    chapter.setPagesCount(getPagesCount(chapter));
	}

	if (chapter.getPagesCount() > 0) {
	    System.out.println("[DL] Downloading volume " + chapter.getAssociatedVolume() + " chapter "
		    + chapter.getChapterNumber() + " ...");

	    String model = StringUtils.getPageLinkModel(chapter.getLink());
	    File chapterDirectory = new File(
		    getChapterDirectory(MANGA.getName(), chapter.getAssociatedVolume(), chapter.getChapterNumber()));

	    long start = System.nanoTime();
	    if (!chapterDirectory.exists() || chapterDirectory.list().length < chapter.getPagesCount()) {
		for (int i = 0; i < chapter.getPagesCount(); i++) {
		    int pageNumber = i + 1;

		    if (!(new File(chapterDirectory.getPath() + "/0" + pageNumber + ".jpg").exists())) {
			String pageURL = String.format(model, pageNumber);
			Document page = null;

			int currentTry = 0;
			while (currentTry < MAX_TRIES) {
			    try {
				page = JSOUP_CONNECTION.url(pageURL).get();
				break;
			    } catch (IOException ioe) {
				currentTry++;
				if (currentTry >= MAX_TRIES) {
				    String error = "Couldn't retrieve the page " + pageNumber + " of this chapter";
				    System.err.println("[ERROR] " + error);
				    CHAPTERS_WITH_ERRORS.put(chapter, error);
				}
				return;
			    }
			}
			if (page != null && page.hasText()) {
			    Elements imgTags = page.getElementById("viewer").getElementsByTag("img");
			    String imgURL;
			    try {
				imgURL = imgTags.get(1).attr("src");
			    } catch (IndexOutOfBoundsException ioobe) {
				imgURL = imgTags.get(0).attr("src");
			    }
			    saveImage(imgURL, chapter.getAssociatedVolume(), chapter.getChapterNumber(), pageNumber);
			} else {
			    String error = "Couldn't retrieve the page " + pageNumber
				    + " for unknown reasons. It appears randomly and is much likely a problem from MangaFox's side. Keep trying until you succeed.";

			    System.err.println("[WARN] " + error);
			    CHAPTERS_WITH_ERRORS.put(chapter, error);
			    return;
			}
		    } else {
			continue;
		    }
		}

		if (chapterDirectory.list().length >= chapter.getPagesCount()) {
		    System.out.print("[SUCCESS] Chapter " + chapter.getChapterNumber() + " downloaded - "
			    + chapter.getPagesCount() + " pages");
		    UPDATED_CHAPTERS.add(chapter);
		} else {
		    String error = "Only " + chapterDirectory.list().length + " out of " + chapter.getPagesCount()
			    + " have been downloaded for the chapter " + chapter.getChapterNumber()
			    + ". Rerun the script to get every pages";

		    System.err.println("[WARN] " + error);
		    CHAPTERS_WITH_ERRORS.put(chapter, error);
		}
		System.out.println(" - (" + (System.nanoTime() - start) / 1000000000 + " sec)");

	    } else {
		System.out.println("[SKIP] Skipping chapter " + chapter.getChapterNumber() + " : already downloaded");
	    }
	} else if (chapter.getPagesCount() == 0) {
	    System.err.println("[ERROR] Volume " + chapter.getAssociatedVolume() + " chapter "
		    + chapter.getChapterNumber() + " has no pages.");
	} else {
	    System.err.println("[IGNORE] Volume " + chapter.getAssociatedVolume() + " chapter "
		    + chapter.getChapterNumber()
		    + " has been ignored because it doesn't exist, even though MangaFox RSS feed says otherwise.");
	}
	System.out.println();
    }

    private int getPagesCount(Chapter chapter) {
	Document chapterFirstPage;
	try {
	    chapterFirstPage = JSOUP_CONNECTION.url(chapter.getLink()).get();
	} catch (IOException e1) {
	    String error = "Couldn't retrieve the number of pages of this chapter : " + chapter.getChapterNumber();
	    System.err.println("[ERROR] " + error);
	    CHAPTERS_WITH_ERRORS.put(chapter, error);
	    return -1;
	}
	Elements e = chapterFirstPage.getElementsByTag("option");
	int pagesCount = (e.size() / 2) - 1;

	if (pagesCount <= 0) {
	    String errorMsg = chapterFirstPage.getElementById("top_bar").getElementsByTag("span").html();
	    if (errorMsg != null && (errorMsg.contains("licensed") || (errorMsg.contains("not available")))) {
		MANGA.setLicensed(true);
		return -1;
	    }

	    String error = "Couldn't retrieve the number of pages of this chapter : " + chapter.getChapterNumber()
		    + ". Keep in mind in can be a problem on mangafox.me's side.";
	    System.out.println("[ERROR] " + error);
	    CHAPTERS_WITH_ERRORS.put(chapter, error);
	    return -1;
	} else {
	    return pagesCount;
	}
    }

    private void downloadChapters(Set<Chapter> chapters) {
	long start = System.nanoTime();

	for (Chapter chapter : chapters) {
	    if (MANGA.isLicensed()) {
		break;
	    }

	    downloadOneChapter(chapter);
	}
	if (MANGA.isLicensed()) {
	    System.err.println("Sorry, this manga is licensed, and is not available from your current location.");
	} else {
	    updateJSON();
	    updateFiles();

	    System.out.println("[END] " + MANGA.getName().toUpperCase() + " download completed in "
		    + (System.nanoTime() - start) / 1000000000 + " sec !");

	    if (CHAPTERS_WITH_ERRORS.size() > 0) {
		System.err.println();
		System.err.println("------------------ CHAPTERS WITH ERRORS --------------------");
		Iterator<Map.Entry<Chapter, String>> it = CHAPTERS_WITH_ERRORS.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry<Chapter, String> pair = (Map.Entry<Chapter, String>) it.next();
		    Chapter chapter = (Chapter) pair.getKey();
		    System.err.println("Chapter " + chapter.getChapterNumber() + ", volume "
			    + chapter.getAssociatedVolume() + " - Reason : " + pair.getValue());
		    System.err.println();
		    it.remove();
		}
	    }

	}
    }

    private void updateJSON() {
	File mangaJsonFile = new File(MANGA.getName() + File.separator+ MANGA.getName() + ".json");
	try {
	    FileUtils.writeStringToFile(mangaJsonFile, GSON.toJson(MANGA));

	} catch (IOException e) {
	    System.err.println("[ERROR] Unable to create the json file.\n");
	    e.printStackTrace();
	}
    }

    private void updateFiles() {
	File naFolder = new File(MANGA.getName() + File.separator + VOLUME_FOLDER + NOT_AVAILABLE);
	File tbdFolder = new File(MANGA.getName() + File.separator + VOLUME_FOLDER + TO_BE_DETERMINED);

	List<File> files = new ArrayList<File>();

	if (naFolder.exists()) {
	    files.addAll(Arrays.asList(naFolder.listFiles()));
	}
	if (tbdFolder.exists()) {
	    files.addAll(Arrays.asList(tbdFolder.listFiles()));
	}

	for (File file : files) {
	    boolean exist = false;
	    for (Chapter chap : MANGA.getChapters()) {
		File jsonFile = new File(MANGA.getName() + File.separator + VOLUME_FOLDER + chap.getAssociatedVolume() + File.separator
			+ CHAPTER_FOLDER + chap.getChapterNumber());

		if (file.getAbsolutePath().equals(jsonFile.getAbsolutePath())) {
		    exist = true;
		    break;
		}
	    }
	    if (!exist) {
		try {
		    FileUtils.deleteDirectory(file);
		    System.out.println("[DEL] Deleted duplicate chapter : " + file.getPath());

		} catch (IOException e) {
		    System.out.println("[ERROR] Couldn't delete duplicate chapter : " + file.getPath());
		}
	    }
	}

	if (naFolder.exists() && naFolder.listFiles().length == 0) {
	    naFolder.delete();
	}
	if (tbdFolder.exists() && tbdFolder.listFiles().length == 0) {
	    tbdFolder.delete();
	}
    }

    private Set<Chapter> getSpecificVolume(final Set<Chapter> chapters, final String volumeNumber) {
	final Set<Chapter> volume = new TreeSet<Chapter>();
	for (final Chapter chapter : chapters) {
	    if (volumeNumber.equalsIgnoreCase(chapter.getAssociatedVolume())) {
		volume.add(chapter);
	    }
	}
	return volume;
    }

    private Set<Chapter> getSpecificChapter(Set<Chapter> allChapters, String volumeNumber, String chapterNumber) {
	final Set<Chapter> chapters = new TreeSet<Chapter>();
	for (final Chapter chapter : allChapters) {
	    if (volumeNumber.equalsIgnoreCase(chapter.getAssociatedVolume())
		    && chapterNumber.equalsIgnoreCase(chapter.getChapterNumber())) {
		chapters.add(chapter);
	    }
	}

	return chapters;
    }

    private boolean saveImage(String imageUrl, String volumeNumber, String chapterNumber, int pageNumber) {
	String fileLocation = getChapterDirectory(MANGA.getName(), volumeNumber, chapterNumber) + "0" + pageNumber
		+ ".jpg";
	File file = new File(fileLocation);

	int currentTry = 0;

	while (currentTry < MAX_TRIES) {
	    try {
		FileUtils.copyURLToFile(new URL(imageUrl), file, TIME_OUT_IN_MILLIS, TIME_OUT_IN_MILLIS);
	    } catch (MalformedURLException e) {
	    } catch (IOException ioe) {
		currentTry++;
		ioe.printStackTrace();
		continue;
	    }
	    break;
	}

	return file.exists();
    }

    public static void printUnknowOptions() {
	System.err.println("Option(s) unknown. Use -help options to get help on how to use this script.");
    }

    private String getChapterDirectory(String mangaName, String volumeNumber, String chapterNumber) {
	return mangaName + File.separator + VOLUME_FOLDER + volumeNumber + File.separator + CHAPTER_FOLDER
		+ chapterNumber + File.separator + File.separator;
    }

    private int retrieveMangaState() {
	MANGA_STATE = MangaState.VALID;
	try {
	    JSOUP_XML_CONNECTION.url(MANGAFOR_XML_URL + MANGA.getUrlName() + ".xml").get().getElementsByTag("rss")
		    .get(0);
	} catch (IOException e) {
	    MANGA_STATE = MangaState.UNKNOWN;

	} catch (IndexOutOfBoundsException ioobe) {
	    MANGA_STATE = MangaState.INVALID;
	}
	return MANGA_STATE;

    }

    private void retrieveMangaInfo() {
	File mangaJsonFile = new File(MANGA.getName() + File.separator + MANGA.getName() + ".json");

	int numberOfDashes = 60 - MANGA.getName().length();
	if (numberOfDashes < 0) {
	    numberOfDashes = 0;
	}
	String mangaTitle = new String();

	for (int i = 0; i < (numberOfDashes - 1) / 2; i++) {
	    mangaTitle = mangaTitle + "-";
	}
	mangaTitle = mangaTitle + " " + MANGA.getName().toUpperCase() + " ";

	for (int i = 0; i < (numberOfDashes - 1) / 2; i++) {
	    mangaTitle = mangaTitle + "-";
	}

	if (mangaTitle.length() > 60) {
	    mangaTitle = mangaTitle.substring(0, 60);
	} else if (mangaTitle.length() < 60) {
	    do {
		mangaTitle = mangaTitle + "-";
	    } while (mangaTitle.length() < 60);
	}

	System.out.println(mangaTitle);
	if (mangaJsonFile.exists()) {
	    String jsonContent = new String();

	    try {
		jsonContent = FileUtils.readFileToString(mangaJsonFile);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    MANGA = GSON.fromJson(jsonContent, Manga.class);
	} else {
	    System.err.println("[WARN] " + mangaJsonFile.getName().toUpperCase() + " not found");
	    System.err.println();
	    System.err.println("This file stores the manga information. "
		    + "It is automatically generated and updated every time you run the script. "
		    + "The first generation can take a long time depending on the manga but don't worry, it only happens once.");
	    System.out.println();
	    System.out.println("[START] Retrieving manga infos ...");
	    MANGA.setChapters(getAllChapters(MANGA.getName(), true));
	    if (MANGA.getChapters().size() > 0) {
		System.out.println("[SUCESS] Manga infos retrieved");
		System.out.println();
		updateJSON();
	    } else {
		System.err.println("[ERROR] No chapters have been found for " + MANGA.getName().toUpperCase());
	    }
	}
    }

    public int getMangaState() {
	return this.MANGA_STATE;
    }
}
