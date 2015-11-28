<h2>MangaFoxDownloader</h2>
Simple script for downloading mangas from mangafox.me

This script let you easily download mangas from mangafox.me. You can also download a specific volume or a specific chapter

In order to use this script, you must have at least JRE 1.6 installed on your computer.

<h3>Syntax</h3>

Type the following in terminal
<pre>java -jar mangafox_downloader.jar [REQUIRED] &lt;manga name&gt; [OPTIONAL] -v &lt;volume number&gt; -c &lt;chapitre number&gt;</pre>

<h3>Options</h3>
<b>REQUIRED</b> <pre>&lt;manga name&gt;</pre>
Refers to the name of the manga you want to dowload as displayed on mangafox.me<br/>
Case insensitive and can contain space but not special characters<br/>
Tip : To be sure you got it right, visit the desired manga page on mangafox.me. The name of the manga appears in the URL in this form : <pre>www.mangafox.me/manga/&lt;manga name&gt;/</pre>

<h4>Examples</h4>
<pre>java -jar mangafox_downloader.jar naruto</pre>
<pre>java -jar mangafox_downloader.jar "Kangoku Gakuen" - Kangoku Gakuen refers to Prison School</pre>


<b>OPTIONAL<b/><pre>-v &lt;volume number&gt;</pre>
Use this option to download only the specified volume of a manga (= every chapter of the specified volume)<br/>
The volume number is as displayed on the manga page. Ranging from 0 to n<br/>
The volume number can also be "TBD" (refering to "Volume TBD" - To be determined)<br/>
The volume number can also be "NA" (refering to "Volume Not Available")<br/>

<h4>Examples</h4>
<pre>java -jar mangafox_downloader.jar naruto -v NA</pre>
<pre>java -jar mangafox_downloader.jar "Kangoku Gakuen" -v 18</pre>


[OPTIONAL] -v &lt;volume number&gt; -c &lt;chapter number&gt;
Use this option to download only the specified chapter of the specified volume of a manga
When using -c, -v is mandatory as some mangas' chapters numbers aren't unique (i.e. the Dective Conan manga has multiple "Chapter 1" who knows why)
Be sure the specified chapter is contained within the specified volume
The chapter number is as displayed on the manga page. Ranging from 0 to n

EXAMPLES
<pre>java -jar mangafox_downloader.jar naruto -v 60 -c 575</pre>
<pre>java -jar mangafox_downloader.jar "Kangoku Gakuen" -v TBD -c 197</pre>
