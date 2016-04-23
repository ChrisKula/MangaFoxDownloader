package com.ckula.mangafoxdownloader.main;

import com.ckula.mangafoxdownloader.model.MangaState;
import com.ckula.mangafoxdownloader.service.MangaDowloadService;
import com.ckula.mangafoxdownloader.utils.Help;

public class Main {
    private static MangaDowloadService mangaDowloadService;

    public static void main(String[] args) {
	if (args.length > 0) {
	    if ("-help".equalsIgnoreCase(args[0])) {
		MangaDowloadService.printUnknowOptions();
	    } else {
		mangaDowloadService = new MangaDowloadService(args[0]);

		switch (mangaDowloadService.getMangaState()) {
		case MangaState.VALID:
		    switch (args.length) {
		    case 1: {
			if ("-help".equalsIgnoreCase(args[0])) {
			    Help.printHelp();
			    return;
			} else {
			    mangaDowloadService.downloadManga(args[0]);
			}
		    }
			break;
		    case 3: {
			mangaDowloadService.downloadSpecificVolume(args[1], args[2]);
		    }
			break;

		    case 5: {
			mangaDowloadService.downloadSpecificChapter(args[1], args[2], args[3], args[4]);
		    }
			break;

		    default:
			MangaDowloadService.printUnknowOptions();
			break;
		    }
		    break;

		case MangaState.INVALID:
		    System.err.println("[ERROR] The manga " + args[0]
			    + " does not exist (or it is refered differently by MangaFox)");
		    break;

		case MangaState.UNKNOWN:
		    System.err.println(
			    "[ERROR] Couldn't connect to mangafox.me. Check your Internet connection. Keep in mind that mangafox.me may be down.");
		    break;
		}
	    }
	} else {
	    MangaDowloadService.printUnknowOptions();
	    return;
	}

	System.out.println();
	return;
    }
}
