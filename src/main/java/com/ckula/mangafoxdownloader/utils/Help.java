package com.ckula.mangafoxdownloader.utils;

public class Help {

    public static void printHelp() {
	System.out.println(
		"This script downloads mangas from mangafox.me. You must have the JRE 1.6 (or higher) installed.");
	System.out.println();
	System.out.println(
		"SYNTAX\r\njava -jar mangafox_downloader.jar [REQUIRED] <manga name> [OPTIONAL] -v <volume number> -c <chapitre number>");
	System.out.println();
	System.out.println();

	System.out.println("|OPTIONS|");
	System.out.println();

	System.out.println(
		"[REQUIRED] <manga name> \r\nRefers to the name of the manga you want to dowload as displayed on mangafox.me");
	System.out.println("Case insensitive and can contain space but not special characters");
	System.out.println(
		"Tip : To be sure you got it right, visit the desired manga page on mangafox.me. The name of the manga appears in the URL in this form : www.mangafox.me/manga/<manga name>/");

	System.out.println();
	System.out.println("EXAMPLES");
	System.out.println("java -jar mangafox_downloader.jar naruto");
	System.out.println(
		"java -jar mangafox_downloader.jar \"Kangoku Gakuen\" - Kangoku Gakuen refers to Prison School");

	System.out.println();
	System.out.println();

	System.out.println(
		"[OPTIONAL] -v <volume number> \r\nUse this option to download only the specified volume of a manga (= every chapter of the specified volume)");
	System.out.println("The volume number is as displayed on the manga page. Ranging from 0 to n");
	System.out.println("The volume number can also be \"TBD\" (refering to \"Volume TBD\" - To be determined)");
	System.out.println("The volume number can also be \"NA\" (refering to \"Volume Not Available\")");
	System.out.println();
	System.out.println("EXAMPLES");
	System.out.println("java -jar mangafox_downloader.jar naruto -v NA");
	System.out.println("java -jar mangafox_downloader.jar \"Kangoku Gakuen\" -v 18");

	System.out.println();
	System.out.println();

	System.out.println(
		"[OPTIONAL] -v <volume number> -c <chapter number>\r\nUse this option to download only the specified chapter of the specified volume of a manga");
	System.out.println(
		"When using -c, -v is mandatory as some mangas' chapters numbers aren't unique (i.e. the Dective Conan manga has multiple \"Chapter 1\" who knows why)");
	System.out.println("Be sure the specified chapter is contained within the specified volume");
	System.out.println("The chapter number is as displayed on the manga page. Ranging from 0 to n");
	System.out.println();
	System.out.println("EXAMPLES");
	System.out.println("java -jar mangafox_downloader.jar naruto -v 60 -c 575");
	System.out.println("java -jar mangafox_downloader.jar \"Kangoku Gakuen\" -v TBD -c 197");

    }
}
