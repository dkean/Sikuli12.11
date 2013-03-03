#!/bin/sh
# source base
base=`pwd`/../..
# native folder
odir=$base/Resources/natives/Mac/$1
# native module
mod=$2
# java class folder
indir=$base/Projects/Sikuli-script/build/classes
class=org.sikuli.$3.$mod

echo -jni -classpath $indir -d $odir $class
javah -jni -classpath $indir -d $odir $class
