<h2>MangaFoxDownloader</h2>
Simple script for downloading mangas from mangafox.me

This script let you easily download mangas from mangafox.me. You can also download a specific volume or a specific chapter

In order to use this script, you must have at least JRE 1.6 installed on your computer.
The script can be direclty downloaded from the project directory on GitHub.

<h3>DISCLAIMER</h3>
Copyrights and trademarks for the manga, and other promotional materials are held by their respective owners and their use is allowed under the fair use clause of the Copyright Law. 

<h3>Syntax and Usage</h3>

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


<b>OPTIONAL</b><pre>-v &lt;volume number&gt;</pre>
Use this option to download only the specified volume of a manga (= every chapter of the specified volume)<br/>
The volume number is as displayed on the manga page. Ranging from 0 to n<br/>
The volume number can also be "TBD" (refering to "Volume TBD" - To be determined)<br/>
The volume number can also be "NA" (refering to "Volume Not Available")<br/>

<h4>Examples</h4>
<pre>java -jar mangafox_downloader.jar naruto -v NA</pre>
<pre>java -jar mangafox_downloader.jar "Kangoku Gakuen" -v 18</pre>


<b>OPTIONAL</b><pre>-v &lt;volume number&gt; -c &lt;chapter number&gt;</pre>
Use this option to download only the specified chapter of the specified volume of a manga<br/>
When using -c, -v is mandatory as some mangas' chapters numbers aren't unique (i.e. the Dective Conan manga has multiple "Chapter 1" who knows why)<br/>
Be sure the specified chapter is contained within the specified volume<br/>
The chapter number is as displayed on the manga page. Ranging from 0 to n

<h4>EXAMPLES</h4>
<pre>java -jar mangafox_downloader.jar naruto -v 60 -c 575</pre>
<pre>java -jar mangafox_downloader.jar "Kangoku Gakuen" -v TBD -c 197</pre>

<h3>Installation</h3>
If you want to generate the jar file yourself, download this project and open it as a Maven project in Eclipse then use the Maven goal <code>package</code> to generate the file.<br/>
In Eclipse, right click on the project -> Run as -> Maven build ... and type <code>package</code> in the goal field

You can also execute <code>mvn package</code> at the project's root.

The generated jar file will be in the target folder.
