package com.ckula.mangafoxdownloader.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.ckula.mangafoxdownloader.model.Chapter;
import com.ckula.mangafoxdownloader.utils.FileUtils;
import com.ckula.mangafoxdownloader.utils.StringUtils;

public class MangaDowloadService {
    private final String MANGAFOX_URL = "http://mangafox.me";
    private final String MANGAFOR_XML_URL = "http://mangafox.me/rss/";

    private final String NOT_AVAILABLE = "NA";

    private final String MANGA_NAME;
    private final String MANGA_URL_NAME;

    private final String VOLUME_FOLDER = "v_";
    private final String CHAPTER_FOLDER = "ch_";

    private final int MAX_TRIES = 3;
    private final int TIME_OUT_IN_MILLIS = 3000;

    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36";

    private final Connection JSOUP_CONNECTION = Jsoup.connect(MANGAFOX_URL).timeout(TIME_OUT_IN_MILLIS)
	    .ignoreContentType(true).parser(Parser.htmlParser()).userAgent(USER_AGENT);

    private final Connection JSOUP_XML_CONNECTION = Jsoup.connect(MANGAFOX_URL).timeout(TIME_OUT_IN_MILLIS)
	    .parser(Parser.xmlParser()).userAgent(USER_AGENT);

    private final Map<Chapter, String> CHAPTERS_WITH_ERRORS = new HashMap<Chapter, String>();

    private boolean isMangaLicensed = false;

    public MangaDowloadService(String mangaName) {
	this.MANGA_NAME = mangaName;
	this.MANGA_URL_NAME = StringUtils.transformToMangaFoxUrlName(MANGA_NAME);
    }

    public void downloadManga(String mangaName) {
	List<Chapter> chapters = getAllChapters(mangaName);
	System.out.println("----------------------------------------------------------");
	System.out.println("[START] Starting downloading manga : " + MANGA_NAME.toUpperCase());
	System.out.println();
	if (chapters.size() > 0) {
	    List<Chapter> chaptersToUpdate = getChaptersToUpdate(chapters);
	    if (chaptersToUpdate.size() == 0) {
		System.out.println("[UP TO DATE] The manga " + getMangaName().toUpperCase() + " is up to date");
	    } else {
		downloadChapters(chaptersToUpdate);
	    }
	} else {
	    System.err.println("No chapters have been found. Nothing has been downloaded");
	}

    }

    public void downloadSpecificVolume(String option, String argument) {
	if ("-v".equalsIgnoreCase(option) && argument.matches("(?i)([0-9]+|NA|TBD){1}")) {

	    List<Chapter> chapters = getAllChapters(option);
	    chapters = getSpecificVolume(chapters, argument);
	    if (chapters.size() > 0) {
		downloadChapters(chapters);
	    } else {
		System.err.println("No chapters have been found for the volume " + argument);
	    }

	} else {
	    printUnknowOptions();
	}
    }

    public void downloadSpecificChapter(String volumeOption, String volumeArgument, String chapitreOption,
	    String chapitreArgument) {
	if ("-v".equalsIgnoreCase(volumeOption) && "-c".equalsIgnoreCase(chapitreOption)
		&& volumeArgument.matches("(?i)([0-9]+|NA|TBD){1}") && chapitreArgument.matches("[0-9.]+")) {

	    List<Chapter> chapters = getAllChapters(MANGA_NAME);

	    chapters = getSpecificChapter(chapters, volumeArgument, chapitreArgument);
	    if (chapters.size() > 0) {
		downloadChapters(chapters);
	    } else {
		System.err.println(
			"No chapters " + chapitreArgument + " have been found for the volume " + volumeArgument);
	    }

	} else {
	    printUnknowOptions();
	}

    }

    private List<Chapter> getAllChapters(String mangaName) {
	List<Chapter> chapters = new ArrayList<Chapter>();

	Element rss;
	try {
	    rss = JSOUP_XML_CONNECTION.url(MANGAFOR_XML_URL + MANGA_URL_NAME + ".xml").get().getElementsByTag("rss")
		    .get(0);
	} catch (IOException e) {
	    System.err.println(
		    "[ERROR] Couldn't connect to mangafox.me. Check your Internet connection. Keep in mind that mangafox.me may be down.");
	    return chapters;
	} catch (IndexOutOfBoundsException ioobe) {
	    System.err.println("[ERROR] The manga " + MANGA_NAME.toUpperCase()
		    + " does not exist (or it is refered differently by MangaFox)");
	    return chapters;
	}
	Elements rssChapters = rss.getElementsByTag("item");

	for (Element elem : rssChapters) {
	    Chapter chapter = new Chapter();

	    chapter.setLink(elem.getElementsByTag("link").get(0).ownText());

	    String title = elem.getElementsByTag("title").get(0).ownText();
	    title = title.replaceAll("[^0-9.\bTBD\b]+", " ").trim();

	    String volume;
	    String chap;

	    String[] volAndChap = title.split(" ");

	    if (volAndChap.length < 2) {
		volume = NOT_AVAILABLE;
	    } else {
		volume = volAndChap[volAndChap.length - 2].replaceFirst("^0+(?!$)", "");
	    }

	    chap = volAndChap[volAndChap.length - 1].replaceFirst("^0+(?!$)", "");

	    chapter.setChapterNumber(chap);
	    chapter.setAssociatedVolume(volume);

	    chapters.add(chapter);
	}

	Collections.sort(chapters);
	return chapters;
    }

    private List<Chapter> getChaptersToUpdate(List<Chapter> chapters) {
	List<Chapter> chaptersToDownload = new ArrayList<Chapter>();

	for (Chapter chapter : chapters) {
	    File chapterDirectory = new File(
		    getChapterDirectory(MANGA_NAME, chapter.getAssociatedVolume(), chapter.getChapterNumber()));
	    if (!chapterDirectory.exists()) {
		chaptersToDownload.add(chapter);
	    }

	}
	return chaptersToDownload;
    }

    private void downloadOneChapter(Chapter chapter) {
	Document chapterFirstPage;
	try {
	    chapterFirstPage = JSOUP_CONNECTION.url(chapter.getLink()).get();
	} catch (IOException e1) {
	    String error = "Couldn't retrieve the number of pages of this chapter : " + chapter.getChapterNumber();
	    System.err.println("[ERROR] " + error);
	    removeChapterDirectory(chapter);
	    CHAPTERS_WITH_ERRORS.put(chapter, error);
	    return;
	}

	System.out.println("[DL] Downloading volume " + chapter.getAssociatedVolume() + " chapter "
		+ chapter.getChapterNumber() + " ...");

	String model = StringUtils.getPageLinkModel(chapter.getLink());
	File chapterDirectory = new File(
		getChapterDirectory(MANGA_NAME, chapter.getAssociatedVolume(), chapter.getChapterNumber()));
	Elements e = chapterFirstPage.getElementsByTag("option");
	chapter.setPagesCount((e.size() / 2) - 1);

	if (chapter.getPagesCount() <= 0) {
	    String errorMsg = chapterFirstPage.getElementById("top_bar").getElementsByTag("span").html();
	    if (errorMsg != null && (errorMsg.contains("licensed") || (errorMsg.contains("not available")))) {
		isMangaLicensed = true;
		return;
	    }

	    String error = "Couldn't retrieve the number of pages of this chapter : " + chapter.getChapterNumber()
		    + ". Keep in mind in can be a problem on mangafox.me's side.";
	    System.out.println("[ERROR] " + error);
	    removeChapterDirectory(chapter);
	    CHAPTERS_WITH_ERRORS.put(chapter, error);
	    return;
	}
	long start = System.nanoTime();
	if (!chapterDirectory.exists() || chapterDirectory.list().length < chapter.getPagesCount()) {
	    for (int i = 0; i < chapter.getPagesCount(); i++) {
		String pageURL = String.format(model, i + 1);
		Document page;
		try {
		    page = JSOUP_CONNECTION.url(pageURL).get();
		} catch (IOException e1) {
		    String error = "Couldn't retrieve the page " + (i + 1) + " of this chapter";
		    System.err.println("[ERROR] " + error);
		    removeChapterDirectory(chapter);
		    CHAPTERS_WITH_ERRORS.put(chapter, error);
		    return;
		}

		if (page.hasText()) {
		    Elements imgTags = page.getElementById("viewer").getElementsByTag("img");
		    String imgURL;
		    try {
			imgURL = imgTags.get(1).attr("src");
		    } catch (IndexOutOfBoundsException ioobe) {
			imgURL = imgTags.get(0).attr("src");
		    }
		    saveImage(imgURL, chapter.getAssociatedVolume(), chapter.getChapterNumber(), i + 1);
		} else {
		    String error = "Couldn't retrieve the page " + (i + 1)
			    + " for unknown reasons. It appears randomly and is much likely a problem from MangaFox's side. Keep trying until you succeed.";

		    System.err.println("[WARN] " + error);
		    removeChapterDirectory(chapter);
		    CHAPTERS_WITH_ERRORS.put(chapter, error);
		    return;
		}
	    }
	    if (chapterDirectory.list().length >= chapter.getPagesCount()) {
		System.out.print("[SUCCESS] Chapter " + chapter.getChapterNumber() + " downloaded - "
			+ chapter.getPagesCount() + " pages");
	    } else {
		String error = "Only " + chapterDirectory.list().length + " out of " + chapter.getPagesCount()
			+ " have been downloaded for the chapter " + chapter.getChapterNumber()
			+ ". Rerun the script to get every pages";

		System.err.println("[WARN] " + error);
		removeChapterDirectory(chapter);
		CHAPTERS_WITH_ERRORS.put(chapter, error);
	    }
	    System.out.println(" - (" + (System.nanoTime() - start) / 1000000000 + " sec)");
	} else {
	    System.out.println("[SKIP] Skipping chapter " + chapter.getChapterNumber() + " : already downloaded");
	}
	System.out.println();
    }

    private void downloadChapters(List<Chapter> chapters) {
	long start = System.nanoTime();

	for (Chapter chapter : chapters) {
	    if (isMangaLicensed) {
		break;
	    }

	    downloadOneChapter(chapter);
	}
	if (isMangaLicensed) {
	    System.err.println("Sorry, this manga is licensed, and is not available from your current location.");
	} else {
	    System.out.println("[END] " + MANGA_NAME.toUpperCase() + " download completed in "
		    + (System.nanoTime() - start) / 1000000000 + " sec !");

	    if (CHAPTERS_WITH_ERRORS.size() > 0) {
		System.err.println();
		System.err.println("------------------CHAPTERS WITH ERRORS--------------------");
		Iterator<Map.Entry<Chapter, String>> it = CHAPTERS_WITH_ERRORS.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry<Chapter, String> pair = (Map.Entry<Chapter, String>) it.next();
		    Chapter chapter = (Chapter) pair.getKey();
		    System.err.println("• Chapter " + chapter.getChapterNumber() + ", volume "
			    + chapter.getAssociatedVolume() + " - Reason : " + pair.getValue());
		    System.err.println();
		    it.remove();
		}
	    }
	    System.out.println("----------------------------------------------------------");
	}
    }

    private List<Chapter> getSpecificVolume(final List<Chapter> chapters, final String volumeNumber) {
	final List<Chapter> volume = new ArrayList<Chapter>();
	for (final Chapter chapter : chapters) {
	    if (volumeNumber.equalsIgnoreCase(chapter.getAssociatedVolume())) {
		volume.add(chapter);
	    }
	}
	return volume;
    }

    private List<Chapter> getSpecificChapter(List<Chapter> allChapters, String volumeNumber, String chapterNumber) {
	final List<Chapter> chapters = new ArrayList<Chapter>();
	for (final Chapter chapter : allChapters) {
	    if (volumeNumber.equalsIgnoreCase(chapter.getAssociatedVolume())
		    && chapterNumber.equalsIgnoreCase(chapter.getChapterNumber())) {
		chapters.add(chapter);
	    }
	}

	return chapters;
    }

    private boolean saveImage(String imageUrl, String volumeNumber, String chapterNumber, int pageNumber) {
	String chapterDirectory = getChapterDirectory(MANGA_NAME, volumeNumber, chapterNumber);
	File file = new File(chapterDirectory);
	file.mkdirs();
	String fileLocation = chapterDirectory + "0" + pageNumber + ".jpg";
	file = new File(fileLocation);
	OutputStream os;

	int currentTry = 0;
	while (currentTry < MAX_TRIES) {
	    if (!file.exists()) {
		try {
		    URL url = new URL(imageUrl);
		    InputStream is = url.openStream();

		    file.createNewFile();
		    os = new FileOutputStream(file);
		    byte[] b = new byte[2048];
		    int length;

		    while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		    }

		    is.close();
		    os.close();
		} catch (IOException ioe) {
		    currentTry++;
		}
	    } else {
		break;
	    }
	}
	return file.exists();
    }

    public String getMangaName() {
	return MANGA_NAME;
    }

    public static void printUnknowOptions() {
	System.err.println("Option(s) unknown. Use -help options to get help on how to use this script.");
    }

    private String getChapterDirectory(String mangaName, String volumeNumber, String chapterNumber) {
	return mangaName + File.separator + VOLUME_FOLDER + volumeNumber + File.separator + CHAPTER_FOLDER
		+ chapterNumber + File.separator + File.separator;
    }

    private void removeChapterDirectory(Chapter chapter) {
	FileUtils.forceDelete(
		new File(getChapterDirectory(MANGA_NAME, chapter.getAssociatedVolume(), chapter.getChapterNumber())));
    }
}
