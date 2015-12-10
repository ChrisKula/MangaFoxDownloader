<h2>MangaFoxDownloader</h2>
Simple script for downloading mangas from mangafox.me

This script let you easily download mangas from mangafox.me. You can also download a specific volume or a specific chapter

In order to use this script, you must have at least <b>JRE 1.6</b> installed on your computer.<br/>
The script can be direclty downloaded from the project's target directory on GitHub.

<h3>DISCLAIMER</h3>
Copyrights and trademarks for the manga, and other promotional materials are held by their respective owners and their use is allowed under the fair use clause of the Copyright Law. 

<h3>Syntax and Usage</h3>

Type the following in terminal
<pre>java -jar mangafox_downloader.jar [REQUIRED] &lt;manga name&gt; [OPTIONAL] -v &lt;volume number&gt; -c &lt;chapitre number&gt;</pre>

The manga will be downloaded at the root where the script has been executed.

The manga folder will be like this :

&nbsp;&nbsp;|<code>manga name</code><br/>
&nbsp;&nbsp;|---<code>v_X</code><br/>
&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;|---<code>ch_Y</code><br/>
&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;|---<code>ch_Y+1</code><br/>
&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;|---<code>ch_Y+2</code><br/>
&nbsp;&nbsp;|<br/>
&nbsp;&nbsp;|---<code>v_X+1</code><br/>

<h3>Options</h3>
All options and their arguments are case-insensitive.

<b>REQUIRED</b> <pre>&lt;manga name&gt;</pre>
Refers to the name of the manga you want to dowload as displayed on mangafox.me<br/>
Case insensitive and can contain space but not special characters<br/>
Tip : To be sure you got it right, visit the desired manga page on mangafox.me. The name of the manga appears in the URL in this form : <code>www.mangafox.me/manga/&lt;manga name&gt;/</code></br>
<b>The script will only download missing chapters. If you notice that some chapters lack pages, delete the chapter folder, the script will redownload it with, hopefully, every pages</b>


<h4>Examples</h4>
<pre>java -jar mangafox_downloader.jar naruto</pre>
<pre>java -jar mangafox_downloader.jar "Kangoku Gakuen" - Kangoku Gakuen refers to Prison School</pre>


<b>OPTIONAL</b><pre>-v &lt;volume number&gt;</pre>
Use this option to download only the specified volume of a manga (= every chapter of the specified volume)<br/>
The volume number is as displayed on the manga page. Ranging from 0 to n<br/>
The volume number can also be <code>TBD</code> (refering to "Volume TBD" - To be determined)<br/>
The volume number can also be <code>NA</code> (refering to "Volume Not Available")<br/>

<h4>Examples</h4>
<pre>java -jar mangafox_downloader.jar naruto -v NA</pre>
<pre>java -jar mangafox_downloader.jar "Kangoku Gakuen" -v 18</pre>


<b>OPTIONAL</b><pre>-v &lt;volume number&gt; -c &lt;chapter number&gt;</pre>
Use this option to download only the specified chapter of the specified volume of a manga<br/>
When using <code>-c</code>, <code>-v</code> is mandatory as some mangas' chapters numbers aren't unique (i.e. the Dective Conan manga has multiple "Chapter 1" who knows why)<br/>
Be sure the specified chapter exists in the specified volume<br/>
The chapter number is as displayed on the manga page. Ranging from 0 to n

<h4>EXAMPLES</h4>
<pre>java -jar mangafox_downloader.jar naruto -v 60 -c 575</pre>
<pre>java -jar mangafox_downloader.jar "Kangoku Gakuen" -v TBD -c 197</pre>

<h3>Installation</h3>
If you want to generate the jar file yourself, download this project and open it as a Maven project in Eclipse then use the Maven goal <code>package</code> to generate the file.<br/>
In Eclipse, right click on the project -> Run as -> Maven build ... and type <code>package</code> in the goal field

You can also execute <code>mvn package</code> at the project's root.

The generated jar file will be in the target folder with the name <code>mangafox_downloader.jar</code>. Ignore the file <code>mangafox-downloader-X.X.X.jar</code>

<h3>TODO</h3>
<ul>
<li>Add the volume number in the "Downloading chpater XX" message</li>
<li>Remove chapters folders who haven't been correctly downloaded</li>
<li>Write a small error message when there's a problem when downloading a chapter</li>
</ul>
