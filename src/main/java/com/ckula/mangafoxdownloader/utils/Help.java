package com.ckula.mangafoxdownloader.utils;

public class Help {

    private Help() {
    }

    public static void printHelp() {
	StringBuilder help = new StringBuilder();

	help.append("This script downloads mangas from mangafox.me. You must have the JRE 1.6 (or higher) installed.");
	help.append(System.lineSeparator());

	help.append(System.lineSeparator());
	help.append("SYNTAX");
	help.append(System.lineSeparator());
	help.append(
		"java -jar mangafox_downloader.jar [REQUIRED] <manga name> [OPTIONAL] -v <volume number> -c <chapitre number>");
	help.append(System.lineSeparator());

	help.append(System.lineSeparator());
	help.append(System.lineSeparator());

	help.append("OPTIONS");
	help.append(System.lineSeparator());
	help.append(System.lineSeparator());

	help.append(
		"[REQUIRED] <manga name> \r\nRefers to the name of the manga you want to dowload as displayed on mangafox.me");
	help.append(System.lineSeparator());

	help.append("Case insensitive and can contain space but not special characters");
	help.append(System.lineSeparator());

	help.append(
		"Tip : To be sure you got it right, visit the desired manga page on mangafox.me. The name of the manga appears in the URL in this form : www.mangafox.me/manga/<manga name>/");
	help.append(System.lineSeparator());

	help.append(System.lineSeparator());

	help.append("EXAMPLES");
	help.append(System.lineSeparator());
	help.append("java -jar mangafox_downloader.jar naruto");
	help.append(System.lineSeparator());
	help.append("java -jar mangafox_downloader.jar \"Kangoku Gakuen\" - Kangoku Gakuen refers to Prison School");
	help.append(System.lineSeparator());

	help.append(System.lineSeparator());
	help.append(System.lineSeparator());

	help.append(
		"[OPTIONAL] -v <volume number> \r\nUse this option to download only the specified volume of a manga (= every chapter of the specified volume)");

	help.append(System.lineSeparator());
	help.append("The volume number is as displayed on the manga page. Ranging from 0 to n");
	help.append("The volume number can also be \"TBD\" (refering to \"Volume TBD\" - To be determined)");
	help.append(System.lineSeparator());
	help.append("The volume number can also be \"NA\" (refering to \"Volume Not Available\")");
	help.append(System.lineSeparator());
	help.append(System.lineSeparator());
	help.append("EXAMPLES");
	help.append(System.lineSeparator());
	help.append("java -jar mangafox_downloader.jar naruto -v NA");
	help.append(System.lineSeparator());
	help.append("java -jar mangafox_downloader.jar \"Kangoku Gakuen\" -v 18");

	help.append(System.lineSeparator());
	help.append(System.lineSeparator());

	help.append(
		"[OPTIONAL] -v <volume number> -c <chapter number>\r\nUse this option to download only the specified chapter of the specified volume of a manga");
	help.append(System.lineSeparator());
	help.append(
		"When using -c, -v is mandatory as some mangas' chapters numbers aren't unique (i.e. the Dective Conan manga has multiple \"Chapter 1\" who knows why)");
	help.append(System.lineSeparator());
	help.append("Be sure the specified chapter is contained within the specified volume");
	help.append(System.lineSeparator());
	help.append("The chapter number is as displayed on the manga page. Ranging from 0 to n");
	help.append(System.lineSeparator());
	help.append(System.lineSeparator());
	help.append("EXAMPLES");
	help.append(System.lineSeparator());
	help.append(System.lineSeparator());
	help.append("java -jar mangafox_downloader.jar naruto -v 60 -c 575");
	help.append(System.lineSeparator());
	help.append("java -jar mangafox_downloader.jar \"Kangoku Gakuen\" -v TBD -c 197");

	System.out.println(help);
    }
}
