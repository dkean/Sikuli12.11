@echo off
setlocal

rem **** ANT_HOME
if "%ANT_HOME%" == "" (
  set ANT_HOME=c:\AntHome
)
set ARCH=64
set JAVA_HOME=%ProgramFiles%\Java
if not "%1"=="-32" goto ARCH64
shift
set ARCH=32
if defined ProgramFiles(x86) set JAVA_HOME=%ProgramFiles(x86)%\Java
:ARCH64

pushd %JAVA_HOME%
if "%1"=="-7" goto JAVA7
for /D %%n in ( jdk1.6* ) do set JAVA_HOME=%JAVA_HOME%\%%n
goto BUILD
:JAVA7
for /D %%n in ( jdk1.7* ) do set JAVA_HOME=%JAVA_HOME%\%%n

:BUILD
popd

PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;%PATH%

echo *** working with %ARCH% Bit
set ANT_HOME
set JAVA_HOME

echo *** running build: build-win-app 
set BASEDIR=../Resources/build
call ant.bat -noclasspath -Darch=%ARCH% -f %BASEDIR%\build-win-app.xml -Dbasedir=%BASEDIR% -Darch=%ARCH%

:FINALLY
endlocal