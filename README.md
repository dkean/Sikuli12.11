Sikuli12.11
===========

revised Sikuli X-1.0rc3 (r930)

BE AWARE
========
- this is work in progress
- currently there are only sources 
- no docs on how to build and get working jar's
- until mentioned, it does not make sense to clone or fork
- or to post pull requests

Nevertheless: Comments are always welcome.

Motivation
==========
from Launchpad/sikuli faq https://answers.launchpad.net/sikuli/+faq/2106

per end of 2012-10: 
Status of Sikuli development --- we are waiting for a new version
---------------------------

So I decided, to revise the Sikuli version in its current shape, fix some bugs and implement some requests.

My plan: Part 1 and 2 ready per end of November 2012.

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
IDE
---
- revised the main window
 - as standard: no command bar, vertically split (left code, right messages)
 - eliminated menus/features: export, extensions (might come back later), unit test
- revised the Preview window (only one at a time, apply button added to do more than one change, ...)
- revised the dirty-handling (script is edited with changes):
 - aware of changes in Preview 
 - scripts with changes are marked in its tab: *myScript
 - new option: autosave before run and warning
- new options: no HTML creation, do not delete unused images
- possibility to put IDE options in a script at the beginning as special comment

Script API
----------
- constantly adding javadocs to all public (and important protected) methods
- totally revised the ImagePath feature and the usage of ImageLocator (including the usage with Jython import)
- revising the command line usage to make it equal for ide.jar and script.jar running Sikuli scripts
- each physical monitor has only one Screen object, only one Robot for all and better support for multi monitor
- Region: added methods to make it functinally compatible with AWT Rectangle and added more methods to create
new Regions easily based on existing Regions and Locations
- click() and similar, click the lastMatch()


