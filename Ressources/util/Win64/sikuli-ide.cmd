@echo off
REM C:\MinGW\bin;C:\MinGW\msys\1.0\bin
SETLOCAL
set JAVA=%ProgramFiles%\Java\jre6\bin\java.exe
echo +++ checking Java at %JAVA%
IF EXIST "%JAVA%" (
  echo +++ Java found
  "%JAVA%" -version	
) ELSE (
  echo +++ Java not found
  goto STOPIT
)
echo.
IF "%SIKULI_HOME%"=="" (
  set SIKULI_HOME=%ProgramFiles%\SikuliX
)
echo +++ using as SIKULI_HOME: %SIKULI_HOME%
IF NOT EXIST "%SIKULI_HOME%\libs\VisionProxy.dll" (
  echo +++ SIKULI_HOME seems not to be valid
  GOTO STOPIT
)
PATH=%SIKULI_HOME%\libs;%PATH%
echo.
echo +++ trying to start Sikuli IDE

IF "%1%"=="" (
  "%JAVA%" -cp "%SIKULI_HOME%" -jar "%SIKULI_HOME%\sikuli-ide.jar"
) ELSE (
  cd "C:\Users\Raimund Hocke\Documents\NetBeansProjects\Sikuli-IDE\dist"
  "%JAVA%" -Dsikuli.Debug=3 -jar sikuli-ide.jar
)
GOTO FINALLY
:STOPIT
echo.+++ ended with some errors
:FINALLY
ENDLOCAL