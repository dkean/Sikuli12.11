#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=gfortran
AS=as

# Macros
CND_PLATFORM=GNU-MacOSX
CND_DLIB_EXT=dylib
CND_CONF=Release
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/_ext/1240717019/imgdb.o \
	${OBJECTDIR}/_ext/1240717019/vision.o \
	${OBJECTDIR}/_ext/1240717019/sikuli-debug.o \
	${OBJECTDIR}/_ext/1240717019/tessocr.o \
	${OBJECTDIR}/_ext/1240717019/cvgui.o \
	${OBJECTDIR}/_ext/1240717019/TimingBlock.o \
	${OBJECTDIR}/_ext/1240717019/pyramid-template-matcher.o \
	${OBJECTDIR}/_ext/1240717019/finder.o \
	${OBJECTDIR}/_ext/1240717019/visionJAVA_wrap.o


# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=-dynamic /usr/local/lib/libopencv_core.dylib /usr/local/lib/libopencv_features2d.dylib /usr/local/lib/libopencv_highgui.dylib /usr/local/lib/libopencv_imgproc.dylib /usr/local/lib/libopencv_flann.dylib /usr/local/lib/libtesseract.dylib

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libVisionProxy.dylib

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libVisionProxy.dylib: /usr/local/lib/libopencv_core.dylib

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libVisionProxy.dylib: /usr/local/lib/libopencv_features2d.dylib

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libVisionProxy.dylib: /usr/local/lib/libopencv_highgui.dylib

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libVisionProxy.dylib: /usr/local/lib/libopencv_imgproc.dylib

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libVisionProxy.dylib: /usr/local/lib/libopencv_flann.dylib

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libVisionProxy.dylib: /usr/local/lib/libtesseract.dylib

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libVisionProxy.dylib: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.cc} -dynamiclib -install_name libVisionProxy.dylib -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libVisionProxy.dylib -Wl,-S -fPIC ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/_ext/1240717019/imgdb.o: ../../Resources/natives/Vision/imgdb.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O3 -s -I/usr/local/include -I/usr/local/include/opencv -I/usr/local/include/tesseract -I/System/Library/Frameworks/JavaVM.framework/Headers -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/imgdb.o ../../Resources/natives/Vision/imgdb.cpp

${OBJECTDIR}/_ext/1240717019/vision.o: ../../Resources/natives/Vision/vision.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O3 -s -I/usr/local/include -I/usr/local/include/opencv -I/usr/local/include/tesseract -I/System/Library/Frameworks/JavaVM.framework/Headers -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/vision.o ../../Resources/natives/Vision/vision.cpp

${OBJECTDIR}/_ext/1240717019/sikuli-debug.o: ../../Resources/natives/Vision/sikuli-debug.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O3 -s -I/usr/local/include -I/usr/local/include/opencv -I/usr/local/include/tesseract -I/System/Library/Frameworks/JavaVM.framework/Headers -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/sikuli-debug.o ../../Resources/natives/Vision/sikuli-debug.cpp

${OBJECTDIR}/_ext/1240717019/tessocr.o: ../../Resources/natives/Vision/tessocr.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O3 -s -I/usr/local/include -I/usr/local/include/opencv -I/usr/local/include/tesseract -I/System/Library/Frameworks/JavaVM.framework/Headers -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/tessocr.o ../../Resources/natives/Vision/tessocr.cpp

${OBJECTDIR}/_ext/1240717019/cvgui.o: ../../Resources/natives/Vision/cvgui.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O3 -s -I/usr/local/include -I/usr/local/include/opencv -I/usr/local/include/tesseract -I/System/Library/Frameworks/JavaVM.framework/Headers -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/cvgui.o ../../Resources/natives/Vision/cvgui.cpp

${OBJECTDIR}/_ext/1240717019/TimingBlock.o: ../../Resources/natives/Vision/TimingBlock.cc 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O3 -s -I/usr/local/include -I/usr/local/include/opencv -I/usr/local/include/tesseract -I/System/Library/Frameworks/JavaVM.framework/Headers -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/TimingBlock.o ../../Resources/natives/Vision/TimingBlock.cc

${OBJECTDIR}/_ext/1240717019/pyramid-template-matcher.o: ../../Resources/natives/Vision/pyramid-template-matcher.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O3 -s -I/usr/local/include -I/usr/local/include/opencv -I/usr/local/include/tesseract -I/System/Library/Frameworks/JavaVM.framework/Headers -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/pyramid-template-matcher.o ../../Resources/natives/Vision/pyramid-template-matcher.cpp

${OBJECTDIR}/_ext/1240717019/finder.o: ../../Resources/natives/Vision/finder.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O3 -s -I/usr/local/include -I/usr/local/include/opencv -I/usr/local/include/tesseract -I/System/Library/Frameworks/JavaVM.framework/Headers -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/finder.o ../../Resources/natives/Vision/finder.cpp

${OBJECTDIR}/_ext/1240717019/visionJAVA_wrap.o: ../../Resources/natives/Vision/visionJAVA_wrap.cxx 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O3 -s -I/usr/local/include -I/usr/local/include/opencv -I/usr/local/include/tesseract -I/System/Library/Frameworks/JavaVM.framework/Headers -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/visionJAVA_wrap.o ../../Resources/natives/Vision/visionJAVA_wrap.cxx

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libVisionProxy.dylib

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
