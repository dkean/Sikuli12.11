#!/bin/sh
base=`pwd`/../$1
odir=$base/Java/org/sikuli/script/natives
icv=/usr/local/include/opencv
sysJava=/System/Library/Frameworks/JavaVM.framework/Headers

/usr/local/bin/swig -java -package org.sikuli.script.natives -outdir $odir -c++ -I$sysJava -I$icv/opencv -I$icv -I$base/Native/Vision -I/usr/local/include -o $base/Native/Vision/visionJAVA_wrap.cxx $base/Native/Vision/vision.swig
