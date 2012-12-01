#!/bin/sh
# source base
source=RaiManSikuli2012
target=Sikuli12.11
base=`pwd`/..
# native folder
odir=$base/$target/Ressources/natives/Mac/$1
# native module
mod=$2
# java class folder
indir=$base/$source-Script/build/classes
class=org.sikuli.$3.$mod

#echo -jni -classpath $indir -d $odir $class
javah -jni -classpath $indir -d $odir $class
