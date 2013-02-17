@echo off
setlocal
rem **** ANT_HOME
if "%ANT_HOME%" == "" (
  set ANT_HOME=c:\AntHome
)

rem **** JAVA_HOME (JDK)
set JDKHome=%ProgramFiles%\Java
if "%1" == "" (
  if "%JAVA_HOME%" == "" ( 
    echo *** using latest Java version
    for /d %%i in ("%JDKHome%\jdk*") do set JAVA_HOME=%%i
  )
) ELSE (
  set VERSION=%1
)
if "%VERSION%" == "6" (
  echo *** using latest Java 6
  for /d %%i in ("%JDKHome%\jdk1.6*") do set JAVA_HOME=%%i
)
if "%VERSION%" == "o" (
  echo *** using OpenJDK 7
  for /d %%i in ("%JDKHome%\jdk1.7*") do set JAVA_HOME=%%i
)
if "%VERSION%" == "7" (
  echo *** using latest Java 7
  for /d %%i in ("%JDKHome%\jdk1.7*") do set JAVA_HOME=%%i
)

PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;%PATH%

echo *** working with
set ANT_HOME
set JAVA_HOME

echo *** running build: build-win-app
call ant.bat -noclasspath -f build-win-app.xml
endlocal