package com.ckula.mangafoxdownloader.main;

import com.ckula.mangafoxdownloader.service.MangaDowloadService;
import com.ckula.mangafoxdownloader.utils.Help;

public class Main {
    private static MangaDowloadService mangaDowloadService;

    public static void main(String[] args) {
	if (args.length > 0) {
	    mangaDowloadService = new MangaDowloadService(args[0]);
	} else {
	    MangaDowloadService.printUnknowOptions();
	    return;
	}

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
	return;
    }
}
