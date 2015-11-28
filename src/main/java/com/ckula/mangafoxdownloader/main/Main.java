package com.ckula.mangafoxdownloader.main;

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
import org.jsoup.select.Elements;

import com.ckula.mangafoxdownloader.model.Chapter;
import com.ckula.mangafoxdownloader.utils.Help;
import com.ckula.mangafoxdownloader.utils.StringUtils;

public class Main {
    private static final int TIME_OUT_IN_MILLIS = 5000;

    private static final Connection JSOUP_CONNECTION = Jsoup.connect("http://mangafox.me").timeout(TIME_OUT_IN_MILLIS);
    private static final Map<Chapter, String> CHAPTERS_WITH_ERRORS = new HashMap<Chapter, String>();

    private static final String MANGA_XML_URL = "http://mangafox.me/rss/";
    private static final String NOT_AVAILABLE = "NA";

    private static String MANGA_NAME = "";
    private static String MANGA_URL_NAME = "";

    public static void main(String[] args) {
	switch (args.length) {
	case 1: {
	    if ("-help".equalsIgnoreCase(args[0])) {
		Help.printHelp();
		return;
	    }
	    List<Chapter> chapters = getAllChapters(args[0]);

	    if (chapters.size() > 0) {
		if (isMangaUpToDate(chapters)) {
		    System.out.println("[UP TO DATE] The manga " + MANGA_NAME.toUpperCase() + " is up to date\n");
		} else {
		    downloadChapters(chapters);
		}
	    } else {
		System.err.println("No chapters have been found. Nothing has been downloaded");
	    }
	}
	    break;
	case 3: {
	    if ("-v".equalsIgnoreCase(args[1]) && args[2].matches("(?i)([0-9]+|NA|TBD){1}")) {

		List<Chapter> chapters = getAllChapters(args[0]);
		chapters = getSpecificVolume(chapters, args[2]);
		if (chapters.size() > 0) {
		    downloadChapters(chapters);
		} else {
		    System.err.println("No chapters have been found for the volume " + args[2]);
		}

	    } else {
		System.err.println("Option(s) unknown. Use -help options to get help on how to use this script.");
	    }
	}
	    break;

	case 5: {
	    if ("-v".equalsIgnoreCase(args[1]) && "-c".equalsIgnoreCase(args[3])
		    && args[2].matches("(?i)([0-9]+|NA|TBD){1}") && args[4].matches("[0-9]+")) {

		List<Chapter> chapters = getAllChapters(args[0]);

		chapters = getSpecificChapter(chapters, args[2], args[4]);
		if (chapters.size() > 0) {
		    downloadChapters(chapters);
		} else {
		    System.err.println("No chapters " + args[4] + " have been found for the volume " + args[2]);
		}

	    } else {
		System.err.println("Option(s) unknown. Use -help options to get help on how to use this script.");
	    }
	}
	    break;

	default:
	    System.err.println("Option(s) unknown. Use -help options to get help on how to use this script.");
	    break;

	}
	return;

    }

    private static void downloadChapters(List<Chapter> chapters) {
	long start = System.nanoTime();
	System.out.println("----------------------------------------------------------");
	System.out.println("[START] Starting downloading manga : " + MANGA_NAME.toUpperCase());
	System.out.println();
	for (int i = 0; i < chapters.size(); i++) {
	    Chapter chapter = chapters.get(i);

	    System.out.println("[DL] Downloading chapter " + chapter.getChapterNumber() + " ...");

	    downloadOneChapter(chapter);

	}
	long end = System.nanoTime();
	System.out.println("[END] " + MANGA_NAME.toUpperCase() + " download completed in " + (end - start) / 1000000000
		+ " sec !\n");
	if (CHAPTERS_WITH_ERRORS.size() > 0) {
	    System.err.println("----------------------------------------------------------");
	    System.err.println("CHAPTERS WITH ERRORS");
	    System.err.println();
	    Iterator<Map.Entry<Chapter, String>> it = CHAPTERS_WITH_ERRORS.entrySet().iterator();
	    while (it.hasNext()) {
		Map.Entry<Chapter, String> pair = (Map.Entry<Chapter, String>) it.next();
		Chapter chapter = (Chapter) pair.getKey();
		System.err.println("Chapter " + chapter.getChapterNumber() + ", volume " + chapter.getAssociatedVolume()
			+ " - Reason : " + pair.getValue());
		it.remove();
	    }
	    System.err.println("----------------------------------------------------------");

	}
	System.out.println("----------------------------------------------------------");
    }

    private static boolean isMangaUpToDate(List<Chapter> chapters) {
	Chapter lastChapter = chapters.get(chapters.size() - 1);

	Document chapterFirstPage;
	try {
	    chapterFirstPage = JSOUP_CONNECTION.url(lastChapter.getLink()).get();
	} catch (IOException e1) { 
	    System.err.println("Couldn't determine if the manga is up to date.");
	    return false;
	}
	Elements e = chapterFirstPage.getElementsByTag("option");
	lastChapter.setPagesCount((e.size() / 2) - 1);

	String lastChapterUrl = MANGA_URL_NAME + "/v_" + lastChapter.getAssociatedVolume() + "/ch_"
		+ lastChapter.getChapterNumber();

	File lastChapterDirectory = new File(lastChapterUrl);

	if (lastChapterDirectory.isDirectory()) {
	    for (int i = 0; i < lastChapterDirectory.list().length; i++) {
		if (lastChapterDirectory.list()[i].equalsIgnoreCase("Thumbs.db")) {
		    new File(lastChapterUrl + "/Thumbs.db").delete();
		}
	    }
	}

	return lastChapterDirectory.isDirectory() && lastChapterDirectory.list().length == lastChapter.getPagesCount();
    }

    private static List<Chapter> getSpecificVolume(final List<Chapter> manga, final String volumeNumber) {
	final List<Chapter> volume = new ArrayList<Chapter>();
	for (final Chapter chapter : manga) {
	    if (volumeNumber.equalsIgnoreCase(chapter.getAssociatedVolume())) {
		volume.add(chapter);
	    }
	}
	return volume;
    }

    private static List<Chapter> getSpecificChapter(List<Chapter> allChapters, String volumeNumber,
	    String chapterNumber) {
	final List<Chapter> chapters = new ArrayList<Chapter>();
	for (final Chapter chapter : allChapters) {
	    if (volumeNumber.equalsIgnoreCase(chapter.getAssociatedVolume())
		    && chapterNumber.equalsIgnoreCase(chapter.getChapterNumber())) {
		chapters.add(chapter);
	    }
	}

	return chapters;

    }

    private static List<Chapter> getAllChapters(String mangaName) {
	List<Chapter> chapters = new ArrayList<Chapter>();
	MANGA_NAME = mangaName;
	MANGA_URL_NAME = StringUtils.transformToMangaFoxUrlName(MANGA_NAME);
	Element rss;
	try {
	    rss = JSOUP_CONNECTION.url(MANGA_XML_URL + MANGA_URL_NAME + ".xml").get().getElementsByTag("rss").get(0);
	} catch (IOException e) {
	    System.err.println(
		    "[ERROR] Couldn't connect to mangafox.me. Check your Internet connection.\r\nKeep in mind that mangafox.me may be also down.");
	    return chapters;
	} catch (IndexOutOfBoundsException ioobe) {
	    System.err.println("[ERROR] The manga " + MANGA_NAME.toUpperCase()
		    + " does not exist (or it is refered differently by MangaFox)");
	    return chapters;
	}
	Elements rssChapters = rss.getElementsByTag("item");

	for (int i = 0; i < rssChapters.size(); i++) {
	    Chapter chapter = new Chapter();
	    Element elem_chapter = rssChapters.get(i);

	    chapter.setLink(elem_chapter.getElementsByTag("link").get(0).ownText());

	    String title = elem_chapter.getElementsByTag("title").get(0).ownText();
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

    private static void saveImage(String imageUrl, String volumeNumber, String chapterNumber, int pageNumber) {
	String chapterDirectory = MANGA_URL_NAME + "/v_" + volumeNumber + "/ch_" + chapterNumber + "/";

	File file = new File(chapterDirectory);
	file.mkdirs();

	String fileLocation = chapterDirectory + "0" + pageNumber + ".jpg";
	file = new File(fileLocation);
	OutputStream os;

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
	    } catch (IOException e) {
		return;
	    }
	}
    }

    private static void downloadOneChapter(Chapter chapter) {
	Document chapterFirstPage;
	try {
	    chapterFirstPage = JSOUP_CONNECTION.url(chapter.getLink()).get();
	} catch (IOException e1) {
	    String error = "Couldn't retrieve the number of pages of this chapter";
	    System.err.println(error);
	    CHAPTERS_WITH_ERRORS.put(chapter, error);
	    return;
	}
	Elements e = chapterFirstPage.getElementsByTag("option");
	chapter.setPagesCount((e.size() / 2) - 1);
	String model = StringUtils.getPageLinkModel(chapter.getLink());

	File chapterDirectory = new File(
		MANGA_URL_NAME + "/v_" + chapter.getAssociatedVolume() + "/ch_" + chapter.getChapterNumber() + "/");

	if (!chapterDirectory.exists() || !chapterDirectory.isDirectory()
		|| chapterDirectory.list().length < chapter.getPagesCount()) {
	    for (int i = 0; i < chapter.getPagesCount(); i++) {
		String pageURL = String.format(model, i + 1);
		Document page;
		try {
		    page = JSOUP_CONNECTION.url(pageURL).get();
		} catch (IOException e1) {
		    String error = "Couldn't retrieve the page " + (i + 1) + " of this chapter";
		    System.err.println(error);
		    CHAPTERS_WITH_ERRORS.put(chapter, error);
		    return;
		}
		try {
		    String imgURL = page.getElementById("viewer").getElementsByTag("img").get(0).attr("src");
		    saveImage(imgURL, chapter.getAssociatedVolume(), chapter.getChapterNumber(), i + 1);
		} catch (IndexOutOfBoundsException ioobe) {
		    String imgURL = page.getElementById("viewer").getElementsByTag("img").get(1).attr("src");
		    saveImage(imgURL, chapter.getAssociatedVolume(), chapter.getChapterNumber(), i + 1);
		}

	    }

	    if (chapterDirectory.list().length >= chapter.getPagesCount()) {
		System.out.println("[SUCCESS] Chapter " + chapter.getChapterNumber() + " downloaded - "
			+ chapter.getPagesCount() + " pages.");
	    } else {
		System.out.println("[WARNING] Some pages of the chapter " + chapter.getChapterNumber()
			+ " haven't been downloaded correctly, rerun the script to get every pages.");
	    }
	} else {
	    System.out.println("[SKIP] Skipping chapter " + chapter.getChapterNumber() + " : already downloaded");
	}
	System.out.println();
    }
}
