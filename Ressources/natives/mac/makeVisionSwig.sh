#!/bin/sh
base=`pwd`/../$1
odir=$base/Script-Java/org/sikuli/script/natives
icv=/usr/local/include/opencv
ivision=$base/Ressources/natives/Vision
sysJava=/System/Library/Frameworks/JavaVM.framework/Headers

/usr/local/bin/swig -java -package org.sikuli.script.natives -outdir $odir -c++ -I$sysJava -I$icv/opencv -I$icv -I$ivision -I/usr/local/include -o $ivision/visionJAVA_wrap.cxx $ivision/vision.swig
