*** SikuliX-1.0Win32Beta ***
*** SikuliX-1.0Win64Beta ***
----------------------------

Be aware: 
- still work in progress - might contain bugs
- not everything is tested
- not everything is implemented / documented


*** Installation
unzip to any location you like


*** Currently ONLY usage from command line is possible (no SikuliX.exe yet)

*** Using the contained command scripts
- use either sikuli-ide.cmd (starts the IDE) or sikli-script.cmd (interactive or run scripts) this way:
. sikuli-ide  (cannot be used to run scripts from commandline): -h (help for more options)
. sikuli-script (supported options: -h (help for more options), -i (interactive), -r (run a script))


*** Java-Version:
- the stuff is compiled with Java 6 latest version
- it runs on Java 7 too
- Java 7 is used if present
- having Java 6 and 7 on your machine, use option 
  -j6 to force running with your Java 6 
with the above mentioned command scripts
- only works, if Java is installed in the standard places in %ProgramFiles% or %ProgramFiles(x86)%


*** Using sikuli-script.jar in Java programming with IDE's like Netbeans, Eclipse, ...
    or Java based scripting (like Jython, JRuby, Scala, Groovy, Clojure, ...)
- take care, that sikuli-script.jar is in the Java classpath of your project
- rename the downloaded, unzipped folder to SikuliX (it contains the lib folder and the other stuff like jars ...)
- to give Sikuli access to the native stuff in SikuliX/libs, choose one of the following:
  1. specify -Dsikuli.Home=path-to-the-folder-containing-Sikuli-stuff (relative or absolute)
  2. have SIKULI_HOME=absolute-path-to-the-folder-containing-the-libsfolder in the environement at runtime
  3. copy/move the folder SikuliX to the work folder (basedir) 
     at the time you run your project (Java system property user.dir)
     (e.g. Netbeans: to the same folder, where you store your NB projects)
  4. have SikuliX in your home directory
  
Used Images: Sikuli searches for image files in the standard in the current working directory (Java property "user.dir")
So e.g. in Netbeans you can store images in your project folder and they will be found when simply using "some-image.png". Relative pathnames are possible too (e.g. "images/some-image.png").
  
