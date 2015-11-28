package com.ckula.mangafoxdownloader.utils;

public class StringUtils {

	public static String transformToMangaFoxUrlName(String mangaName) {
		return mangaName.replace(" ", "_").replace("[^0-9a-zA-Z_]", "").toLowerCase();
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
