package com.ckula.mangafoxdownloader.utils;

import java.io.File;

public class FileUtils {

    private FileUtils() {
    }

    public static void forceDelete(File element) {
	if (element.isDirectory()) {
	    for (File sub : element.listFiles()) {
		forceDelete(sub);
	    }
	}
	element.delete();
    }
}
