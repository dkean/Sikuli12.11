@echo off
SETLOCAL
if "%JAVA_HOME%" == "" (
  set JAVA=%ProgramFiles%\Java\jre6\bin\java.exe
) else (
  set JAVA=%JAVA_HOME%\bin\java.exe
)

echo +++ checking Java at %JAVA%
IF EXIST "%JAVA%" (
  echo +++ Java found
  "%JAVA%" -version	
) ELSE (
  echo +++ Java not found
  goto STOPIT
)
echo.SikuliHome is %SIKULI_HOME%
IF "%SIKULI_HOME%" == "" (
  set SIKULI_HOME=%ProgramFiles%\SikuliX
)
echo +++ using as SIKULI_HOME: %SIKULI_HOME%
IF NOT EXIST "%SIKULI_HOME%\libs\VisionProxy.dll" (
  echo +++ SIKULI_HOME seems not to be valid
  GOTO STOPIT
)
PATH=%SIKULI_HOME%\libs;%PATH%
echo.
echo +++ trying to start Sikuli Script
"%JAVA%" -Dsikuli.Debug=3 -jar "%SIKULI_HOME%\sikuli-script.jar" %*

GOTO FINALLY
:STOPIT
echo.+++ ended with some errors
:FINALLY
ENDLOCAL