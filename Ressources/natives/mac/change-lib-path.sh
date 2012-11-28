#!/bin/sh
echo ------------------ switching to @loader_path
#for dylib in *.jnilib
for dylib in $*
do
   otool -L $dylib
   echo -------------------- changes
   for ref in `otool -L $dylib | grep "lib.*.dylib[^:]" | awk '{print $1'} | grep -v '^/usr/lib' | grep -v '^/System/' | grep -v '^@loader'`
   do
      echo $ref
      install_name_tool -change $ref @loader_path/`basename $ref` $dylib
   done
   echo -------------------- changes
   otool -L $dylib
done
