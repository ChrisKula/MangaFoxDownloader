package com.ckula.mangafoxdownloader.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtils {

    public static String transformToMangaFoxUrlName(String mangaName) {
	return deAccent(mangaName).replaceAll(" ", "_").replaceAll("[^0-9a-zA-Z_]", "").toLowerCase();
    }

    public static String deAccent(final String str) {
	final String nfdNormalizedString = Normalizer.normalize(str,
		Normalizer.Form.NFD);
	final Pattern pattern = Pattern
		.compile("\\p{InCombiningDiacriticalMarks}+");
	return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
    
    public static String getPageLinkModel(String pageUrl) {
	String model = pageUrl;
	int startIndex = 0;
	int endIndex = 0;

	for (int i = model.length() - 1; i >= 0; i--) {
	    if (model.charAt(i) == '/' && startIndex == 0) {
		startIndex = i + 1;
	    } else if (model.charAt(i) == '.' && endIndex == 0) {
		endIndex = i;
	    }
	}

	String modelStart = model.substring(0, startIndex);
	String modelEnd = model.substring(endIndex, model.length());

	model = modelStart + "%d" + modelEnd;
	return model;
    }
}
