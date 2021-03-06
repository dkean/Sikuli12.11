@echo off
setlocal

if "%1"=="" (
  echo usage: -32 -7 NNN 
  echo * are the defaults if omitted
  echo -32 build 32 Bit version * 64-Bit
  echo -7 use Java JDK 7 * JDK 6
  echo NNN Beta version number
  goto FINALLY
)

set SVERSION=SikuliX-1.0

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
shift

:BUILD
if not "%1"=="" (
  set BETA=%1
  shift
)

popd
PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;%PATH%
echo +++ Building %SVERSION% with %ARCH% Bit using
set ANT_HOME
set JAVA_HOME
if not "%BETA%"=="" (
 set BETA=Beta%BETA%
 echo +++ BETA=%BETA%
)

set BASEDIR=../Resources/build
@echo on
call ant.bat -noclasspath -f %BASEDIR%\build-win-app.xml -Darch=%ARCH% -Dbasedir=%BASEDIR% -Darch=%ARCH% -Dbeta=%BETA% -Dversion=%SVERSION%

:FINALLY
endlocal