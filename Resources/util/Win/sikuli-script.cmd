@echo off
SETLOCAL

if "%1"=="-h" (
  echo +++ usage of sikuli-script.cmd
  echo     [] = default
  echo     JRE 7 is used if present 
  echo -j6 as 1st parm assure to use JRE6 if JRE7 is present
  echo     either -i or r:...sikuli must be specified
  echo -i  to start interactive Jython session
  echo -r  script.sikuli run the Sikuli script script.sikuli 
  echo d:n set debug level to n [1]
  echo -f  logs to SikuliLog.txt in working dir
  echo -u  user logs go to UserLog.txt in working dir
  echo     either -i or r:...sikuli must be specified as last parameter
  echo -i  to start interactive Jython session
  echo -r  script.sikuli run the Sikuli script script.sikuli 
  echo.
  echo if you want to store something in sys.argv for later script runs
  echo put --args to the end followed by your parameters you want in sys.argv 
  goto FINALLY
)

IF "%SIKULI_HOME%"=="" set SIKULI_HOME=%~dp0
set PARMS=-Xms64M -Xmx512M -Dfile.encoding=UTF-8

for %%p in ( %* ) do (
if defined PSTOP goto LOOPCONT
if "%%~dp"=="D:" set DEBUG=-Dsikuli.Debug=%%~np
if "%%p"=="-j6" set J6=YES
if "%%p"=="-f" set LOGFILE=-Dsikuli.Logfile
if "%%p"=="-u" set USERLOG=-Dsikuli.LogfileUser
if "%%p"=="-i" set PSTOP=YES
if "%%p"=="-r" set PSTOP=YES
:LOOPCONT
if "%%p"=="--" set PSTOP=YES
)

for %%p in ( %* ) do (
if defined PSTOP1 goto LOOPCONT1
if "%%p"=="-i" goto LOOPCONT1
if "%%p"=="-r" goto LOOPCONT1
SHIFT
goto LOOPCONT2
:LOOPCONT1
set PSTOP1=YES
:LOOPCONT2
set NONE=NULL
)

set PARMS=%PARMS% %DEBUG% %LOGFILE% %USERLOG%
echo %PARMS%
set SPARMS=%1 %2 %3 %4 %5 %6 %7 %8 %9
echo %SPARMS%

echo +++ using as SIKULI_HOME: %SIKULI_HOME%
IF NOT EXIST "%SIKULI_HOME%libs" (
  echo +++ SIKULI_HOME seems not to be valid
  GOTO STOPIT
)

set PROGRAMS=%ProgramFiles%
if not exist "%SIKULI_HOME%libs\MadeFor32Bit.txt" goto ARCH64
echo +++ this Sikuli version is 32-Bit
if defined ProgramFiles(x86) set PROGRAMS=%ProgramFiles(x86)%
goto ARCHOK
:ARCH64
echo +++ this Sikuli version is 64-Bit

:ARCHOK
set JAVA6=%PROGRAMS%\Java\jre6\bin
set JAVA7=%PROGRAMS%\Java\jre7\bin

if "%1"=="-j6" (
  shift
  goto JAVA6
)

IF not EXIST "%JAVA7%" goto JAVA6
set JAVA=%JAVA7%
goto JAVA_OK

:JAVA6
IF not EXIST "%JAVA6%" goto JAVANO
set JAVA=%JAVA6%
goto JAVA_OK

:JAVANO
echo +++ Java not found
goto STOPIT

:JAVA_OK 
PATH=%SIKULI_HOME%libs;%JAVA%;%PATH%
echo +++ trying to start Sikuli Script
rem TODO: running as jar: java.lang.NoClassDefFoundError: org/sikuli/script/SikuliScript
rem "%JAVA%\java.exe" %PARMS% %SIKULI_PARM% -jar "%SIKULI_HOME%sikuli-script.jar" %SPARMS% %SIKULI_USERPARMS%
"%JAVA%\java.exe" %PARMS% %SIKULI_PARM% -cp "%SIKULI_HOME%sikuli-script.jar" org.sikuli.script.SikuliScript %SPARMS% %SIKULI_USERPARMS%

GOTO FINALLY
:STOPIT
echo.+++ ended with some errors
:FINALLY
ENDLOCAL