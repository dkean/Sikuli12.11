@echo off
setlocal
set BASE=%SIKDEVELOP%
set ODIR=%BASE%\Script-Java\org\sikuli\script\natives
set ICV=c:\opencv\build\include
set IVISION=%BASE%\Resources\natives\Vision
set J1=%JDKHOME%\include
set J2=%JDKHOME%\include\win32
set LOCAL=c:\MinGW\msys\1.0\local
set SWIGEXE=c:\swigwin\swig.exe
echo on
%SWIGEXE% -java -package org.sikuli.script.natives -outdir "%ODIR%" -c++ "-I%J1%" "-I%J2%" "-I%ICV%" "-I%IVISION%" -I%LOCAL%\include -o "%IVISION%/visionJAVA_wrap.cxx" "%IVISION%/vision.swig"
endlocal