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
CC=gcc.exe
CCC=g++.exe
CXX=g++.exe
FC=gfortran
AS=as.exe

# Macros
CND_PLATFORM=MinGW64-Windows
CND_DLIB_EXT=dll
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
CFLAGS=-m64

# CC Compiler Flags
CCFLAGS=-m64
CXXFLAGS=-m64

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=-L/C/msys/src/opencv/build/bin -L/C/MinGW64/bin -L/C/msys/local/bin

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/VisionProxy.${CND_DLIB_EXT}

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/VisionProxy.${CND_DLIB_EXT}: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.cc} -llibopencv_core244 -llibopencv_features2d244 -llibopencv_flann244 -llibopencv_highgui244 -llibopencv_imgproc244 -llibtesseract-3 -shared -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/VisionProxy.${CND_DLIB_EXT} ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/_ext/1240717019/imgdb.o: ../../Resources/natives/Vision/imgdb.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/msys/src/opencv/modules/core/include -I/C/msys/src/opencv/modules/features2d/include -I/C/msys/src/opencv/modules/flann/include -I/C/msys/src/opencv/modules/highgui/include -I/C/msys/src/opencv/modules/imgproc/include -I/C/msys/src/tesseract/api -I/C/msys/src/tesseract/ccutil -I/C/msys/src/tesseract/ccstruct -I/C/msys/src/tesseract/ccmain -I/C/Program\ Files/Java/jdk1.6.0_38/include -I/C/Program\ Files/Java/jdk1.6.0_38/include/win32  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/imgdb.o ../../Resources/natives/Vision/imgdb.cpp

${OBJECTDIR}/_ext/1240717019/vision.o: ../../Resources/natives/Vision/vision.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/msys/src/opencv/modules/core/include -I/C/msys/src/opencv/modules/features2d/include -I/C/msys/src/opencv/modules/flann/include -I/C/msys/src/opencv/modules/highgui/include -I/C/msys/src/opencv/modules/imgproc/include -I/C/msys/src/tesseract/api -I/C/msys/src/tesseract/ccutil -I/C/msys/src/tesseract/ccstruct -I/C/msys/src/tesseract/ccmain -I/C/Program\ Files/Java/jdk1.6.0_38/include -I/C/Program\ Files/Java/jdk1.6.0_38/include/win32  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/vision.o ../../Resources/natives/Vision/vision.cpp

${OBJECTDIR}/_ext/1240717019/sikuli-debug.o: ../../Resources/natives/Vision/sikuli-debug.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/msys/src/opencv/modules/core/include -I/C/msys/src/opencv/modules/features2d/include -I/C/msys/src/opencv/modules/flann/include -I/C/msys/src/opencv/modules/highgui/include -I/C/msys/src/opencv/modules/imgproc/include -I/C/msys/src/tesseract/api -I/C/msys/src/tesseract/ccutil -I/C/msys/src/tesseract/ccstruct -I/C/msys/src/tesseract/ccmain -I/C/Program\ Files/Java/jdk1.6.0_38/include -I/C/Program\ Files/Java/jdk1.6.0_38/include/win32  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/sikuli-debug.o ../../Resources/natives/Vision/sikuli-debug.cpp

${OBJECTDIR}/_ext/1240717019/tessocr.o: ../../Resources/natives/Vision/tessocr.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/msys/src/opencv/modules/core/include -I/C/msys/src/opencv/modules/features2d/include -I/C/msys/src/opencv/modules/flann/include -I/C/msys/src/opencv/modules/highgui/include -I/C/msys/src/opencv/modules/imgproc/include -I/C/msys/src/tesseract/api -I/C/msys/src/tesseract/ccutil -I/C/msys/src/tesseract/ccstruct -I/C/msys/src/tesseract/ccmain -I/C/Program\ Files/Java/jdk1.6.0_38/include -I/C/Program\ Files/Java/jdk1.6.0_38/include/win32  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/tessocr.o ../../Resources/natives/Vision/tessocr.cpp

${OBJECTDIR}/_ext/1240717019/cvgui.o: ../../Resources/natives/Vision/cvgui.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/msys/src/opencv/modules/core/include -I/C/msys/src/opencv/modules/features2d/include -I/C/msys/src/opencv/modules/flann/include -I/C/msys/src/opencv/modules/highgui/include -I/C/msys/src/opencv/modules/imgproc/include -I/C/msys/src/tesseract/api -I/C/msys/src/tesseract/ccutil -I/C/msys/src/tesseract/ccstruct -I/C/msys/src/tesseract/ccmain -I/C/Program\ Files/Java/jdk1.6.0_38/include -I/C/Program\ Files/Java/jdk1.6.0_38/include/win32  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/cvgui.o ../../Resources/natives/Vision/cvgui.cpp

${OBJECTDIR}/_ext/1240717019/TimingBlock.o: ../../Resources/natives/Vision/TimingBlock.cc 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/msys/src/opencv/modules/core/include -I/C/msys/src/opencv/modules/features2d/include -I/C/msys/src/opencv/modules/flann/include -I/C/msys/src/opencv/modules/highgui/include -I/C/msys/src/opencv/modules/imgproc/include -I/C/msys/src/tesseract/api -I/C/msys/src/tesseract/ccutil -I/C/msys/src/tesseract/ccstruct -I/C/msys/src/tesseract/ccmain -I/C/Program\ Files/Java/jdk1.6.0_38/include -I/C/Program\ Files/Java/jdk1.6.0_38/include/win32  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/TimingBlock.o ../../Resources/natives/Vision/TimingBlock.cc

${OBJECTDIR}/_ext/1240717019/pyramid-template-matcher.o: ../../Resources/natives/Vision/pyramid-template-matcher.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/msys/src/opencv/modules/core/include -I/C/msys/src/opencv/modules/features2d/include -I/C/msys/src/opencv/modules/flann/include -I/C/msys/src/opencv/modules/highgui/include -I/C/msys/src/opencv/modules/imgproc/include -I/C/msys/src/tesseract/api -I/C/msys/src/tesseract/ccutil -I/C/msys/src/tesseract/ccstruct -I/C/msys/src/tesseract/ccmain -I/C/Program\ Files/Java/jdk1.6.0_38/include -I/C/Program\ Files/Java/jdk1.6.0_38/include/win32  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/pyramid-template-matcher.o ../../Resources/natives/Vision/pyramid-template-matcher.cpp

${OBJECTDIR}/_ext/1240717019/finder.o: ../../Resources/natives/Vision/finder.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/msys/src/opencv/modules/core/include -I/C/msys/src/opencv/modules/features2d/include -I/C/msys/src/opencv/modules/flann/include -I/C/msys/src/opencv/modules/highgui/include -I/C/msys/src/opencv/modules/imgproc/include -I/C/msys/src/tesseract/api -I/C/msys/src/tesseract/ccutil -I/C/msys/src/tesseract/ccstruct -I/C/msys/src/tesseract/ccmain -I/C/Program\ Files/Java/jdk1.6.0_38/include -I/C/Program\ Files/Java/jdk1.6.0_38/include/win32  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/finder.o ../../Resources/natives/Vision/finder.cpp

${OBJECTDIR}/_ext/1240717019/visionJAVA_wrap.o: ../../Resources/natives/Vision/visionJAVA_wrap.cxx 
	${MKDIR} -p ${OBJECTDIR}/_ext/1240717019
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/msys/src/opencv/modules/core/include -I/C/msys/src/opencv/modules/features2d/include -I/C/msys/src/opencv/modules/flann/include -I/C/msys/src/opencv/modules/highgui/include -I/C/msys/src/opencv/modules/imgproc/include -I/C/msys/src/tesseract/api -I/C/msys/src/tesseract/ccutil -I/C/msys/src/tesseract/ccstruct -I/C/msys/src/tesseract/ccmain -I/C/Program\ Files/Java/jdk1.6.0_38/include -I/C/Program\ Files/Java/jdk1.6.0_38/include/win32  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1240717019/visionJAVA_wrap.o ../../Resources/natives/Vision/visionJAVA_wrap.cxx

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/VisionProxy.${CND_DLIB_EXT}

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
