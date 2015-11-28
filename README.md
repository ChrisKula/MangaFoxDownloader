# MangaFoxDownloader
Simple script for downloading mangas from mangafox.me

This script let you easily download mangas from mangafox.me. You can also download a specific volume or a specific chapter

In order to use this script, you must have at least JRE 1.6 installed on your computer.

# Syntax

Type the following in terminal

```Batchfile
java -jar mangafox_downloader.jar [REQUIRED] <manga name> [OPTIONAL] -v <volume number> -c <chapitre number>
```

# Options
[REQUIRED] ```<manga name>```
Refers to the name of the manga you want to dowload as displayed on mangafox.me
Case insensitive and can contain space but not special characters
Tip : To be sure you got it right, visit the desired manga page on mangafox.me. The name of the manga appears in the URL in this form : www.mangafox.me/manga/<manga name>/

EXAMPLES
```java -jar mangafox_downloader.jar naruto```
```java -jar mangafox_downloader.jar "Kangoku Gakuen" - Kangoku Gakuen refers to Prison School```


[OPTIONAL] -v <volume number> 
Use this option to download only the specified volume of a manga (= every chapter of the specified volume)
The volume number is as displayed on the manga page. Ranging from 0 to n
The volume number can also be "TBD" (refering to "Volume TBD" - To be determined)
The volume number can also be "NA" (refering to "Volume Not Available")

EXAMPLES
```java -jar mangafox_downloader.jar naruto -v NA```
```java -jar mangafox_downloader.jar "Kangoku Gakuen" -v 18```


[OPTIONAL] -v <volume number> -c <chapter number>
Use this option to download only the specified chapter of the specified volume of a manga
When using -c, -v is mandatory as some mangas' chapters numbers aren't unique (i.e. the Dective Conan manga has multiple "Chapter 1" who knows why)
Be sure the specified chapter is contained within the specified volume
The chapter number is as displayed on the manga page. Ranging from 0 to n

EXAMPLES
```java -jar mangafox_downloader.jar naruto -v 60 -c 575```
```java -jar mangafox_downloader.jar "Kangoku Gakuen" -v TBD -c 197```
