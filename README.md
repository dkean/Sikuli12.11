I have to postpone a generally useable new version to January 2013.
---------------

**You can download the current working bundles (all beta and work in progress)** 

- Mac: https://dl.dropbox.com/u/42895525/SikuliX/SikuliX-1.0MacBeta.zip (version 2013-January-24)
- Windows: not yet available
- Linux: not yet available

**Unzip to any folder, look into the contained readme.txt and Have Fun ;-)**

----------------------------

I am sorry about that, but
- I have problems to get running versions outside of Netbeans for environements with Java 6 or Java 7 or both
- the build of the Windows native stuff is not yet really working
- not all new features are tested in all aspects
- multi monitor behavior not yet tested seriously

Sikuli12.11
===========

revised Sikuli X-1.0rc3 (r930)  planned to be released as Sikuli Script X 1.0

BE AWARE
========
- this is work in progress
- currently there are only sources 
- no docs on how to build and get working jar's
- until mentioned, it does not make sense to clone or fork
- or to post pull requests

Nevertheless: Comments are always welcome.

Content
-------
- **Docs** --- Sikuli Script Jython scripting and the JavaDocs (Java API)
- **IDE-Java** --- Java Sources for the IDE (sikuli-ide.jar)
- **Lib-Jython** --- sources to support Sikuli Jython scripting
- **Ressources** --- Java and native stuff needed to build and run the jars
- **Script-Java** --- Java sources for the script run support and Java API (sikuli-script.jar)

Motivation
==========
from Launchpad/sikuli faq https://answers.launchpad.net/sikuli/+faq/2106

per end of 2012-10: 
Status of Sikuli development --- we are waiting for a new version
---------------------------

So I decided, to revise the Sikuli version in its current shape, fix some bugs and implement some requests.

In parallel I am cooperating with Tom Yeh on his plans to implement a completely new Sikuli.

I will continue to support the currently latest version of Sikuli at https://launchpad.net/sikuli

Part 1 (on Mac)
---------------
- reduce it to the really needed basic features and goodies that really work
- make it lean and robust, so it comes into a shape, that it can be further developed
  (see https://blueprints.launchpad.net/sikuli/+spec/sikuli-revise-api-and-structure)
- revise the system specific interfaces
- revise and augment the logging feature
- fix some major bugs (I will not touch the OCR/searchText area currently, but isolate it better)
- implement some requests, that fit with what i am already revising
- get it working with Java 7 AND Java 6
- make it working with OpenCV 2.4 and Tesseract 3 (without adding features)
- assure, that everything (nearly ;-) still works, that works with X-1.0rc3-r930

Part 2:
-------
- Get it working on Windows 8 and Ubuntu 12 using Netbeans
- get the whole stuff into one makeSikuli.jar for all systems, so you just run that, and get a working environment on your system

Part 3: (as support for further developement)
-------
- revise, cluster and prioritize the bugs and requests
- revise the tests for Sikuli IDE and sikuli-script
- revise and complete the JavaDocs
- based on the Netbeans build.xml make Ant scripts and use Maven, that allow to build Sikuli without having Netbeans

---------------------------------------------------------

Information on fixes and improvements
=====================================

I have made up a release information for X-1.0 on launchpad, so you can see the bugs, that will be fixed or are at least in progress: https://launchpad.net/sikuli/sikuli-x/x1.0

IDE
---

-----
Alternative IDE: **jEdit (http://www.jedit.org)** 

Besides fixing some major bugs and adding some slight improvements in the current IDE, I just decided, to develop a plugin for jEdit, so it can be used as a more advanced Sikuli Script IDE, that in the standard has many features and plugins, that in Sikuli IDE are still missing. 

Since the main specific features of the Sikuli IDE like Image capture, Preview window and others are rather well encapsulated, it should be rather easy, to implement these as jEdit Beans, commands or macros. Furthermore this would allow, to easily reuse this Sikuli plugin for other scripting languages, that are already supported by jEdit plugins (JRuby, Scala, Groovy, Closure, ...). 

And it might be possible to implement a synthetic language, that allows, to implement typical Sikuli workflows rather easy, without the need to implement every convenience feture in the API itself.

I have made up a new Git Repo (https://github.com/RaiMan/Sikuli-jEdit) and I will try to make it ready latest shortly after end of November.

-----

Changes/Fixes in current IDE
- Windows: no more .exe, only .cmd batch files and better support to avoid start-up problems
- revised the main window
 - as standard: no command bar, vertically split (left code, right messages) (optional horizontally)
 - eliminated menus/features: export, extensions (might come back later), unit test
- revised the Preview window (only one at a time, apply button added to do more than one change, ...)
 - added additional window for more options temporarilly (prefs have to be revised generally)
 - new options: no HTML creation, do not delete unused images, image path for IDE to store images
 - possibility to put IDE options in a script at the beginning as special comment
 - added: warning to restart IDE after having changed prefs
- revised the dirty-handling (script is edited with changes):
 - aware of changes in Preview 
 - scripts with changes are marked in its tab: *myScript
 - new option: autosave before run
 - warning at run (with no autosave) and quit if unsaved scripts
- implement alternative for command list in tools menu (as standard + option)
- add a help topic QuickStart for newcomers
- better display of errors in message area
- revised the extension feature and made it functional again

Script API
----------
Changes/Fixes to the Scripting API
- constantly adding javadocs to all public (and important protected) methods
- totally revised the ImagePath feature and the usage of ImageLocator (including the usage with Jython import)
- revising the command line usage to make it equal for ide.jar and script.jar running Sikuli scripts
- each physical monitor has only one Screen object, only one Robot for all and better support for multi monitor
- feature to show monitor configuration
- Region: added methods to make it functinally compatible with AWT Rectangle and added more methods to create
new Regions easily based on existing Regions and Locations
- click() and similar, click the lastMatch()
- convenience version of highlight() using last match
- a new option, to always highlight() a match
- revise the observe API (Jython callbacks in Java possible with Jython 2.5.3)
- short version of FindFailed message in Jython and better error messages


