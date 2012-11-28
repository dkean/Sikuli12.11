#!/bin/sh
# source base
version=RaiManSikuli2012
base=`pwd`/..
# native folder
odir=$base/$version/Native/$1
# native module
mod=$2
# java class folder
indir=$base/$version-Script/build/classes
class=org.sikuli.$3.$mod

#echo -jni -classpath $indir -d $odir $class
javah -jni -classpath $indir -d $odir $class
