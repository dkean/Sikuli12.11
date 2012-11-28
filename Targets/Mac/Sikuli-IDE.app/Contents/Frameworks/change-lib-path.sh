#!/bin/sh
for dylib in *.dylib *.jnilib
do
   echo ----------- $dylib
   chmod u+w $dylib
   for ref in `otool -L $dylib | grep "lib.*.dylib[^:]" | awk '{print $1'} | grep ^/usr/local/.*libopencv`
   do
      echo ----- current: $ref
      install_name_tool -change $ref @loader_path/`basename $ref` $dylib
   done
   echo ----------------------- changed
   oTool -L $dylib
done
