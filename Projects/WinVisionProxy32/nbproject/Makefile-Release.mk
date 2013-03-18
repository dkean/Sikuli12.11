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
CND_PLATFORM=MinGW-Windows
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
	${OBJECTDIR}/_ext/1743690777/TimingBlock.o \
	${OBJECTDIR}/_ext/1743690777/cvgui.o \
	${OBJECTDIR}/_ext/1743690777/finder.o \
	${OBJECTDIR}/_ext/1743690777/imgdb.o \
	${OBJECTDIR}/_ext/1743690777/pyramid-template-matcher.o \
	${OBJECTDIR}/_ext/1743690777/sikuli-debug.o \
	${OBJECTDIR}/_ext/1743690777/tessocr.o \
	${OBJECTDIR}/_ext/1743690777/vision.o \
	${OBJECTDIR}/_ext/1743690777/visionJAVA_wrap.o


# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=-D__BLOB_T_DEFINED
CXXFLAGS=-D__BLOB_T_DEFINED

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=-L/C/opencv/build/x86/mingw/bin -L/C/MinGW/msys/1.0/local/bin

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/VisionProxy.${CND_DLIB_EXT}

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/VisionProxy.${CND_DLIB_EXT}: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.cc} -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/VisionProxy.${CND_DLIB_EXT} ${OBJECTFILES} ${LDLIBSOPTIONS} -llibopencv_core244 -llibopencv_features2d244 -llibopencv_flann244 -llibopencv_highgui244 -llibopencv_imgproc244 -llibtesseract-3 -Wl,--subsystem,windows,--kill-at -shared -s

${OBJECTDIR}/_ext/1743690777/TimingBlock.o: /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/TimingBlock.cc 
	${MKDIR} -p ${OBJECTDIR}/_ext/1743690777
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/opencv/build/include -I/C/MinGW/msys/1.0/local/include/tesseract -I/C/Program\ Files/Java/jdk1.6.0_39/include -I/C/Program\ Files/Java/jdk1.6.0_39/include/win32 -D__BLOB_T_DEFINED  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1743690777/TimingBlock.o /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/TimingBlock.cc

${OBJECTDIR}/_ext/1743690777/cvgui.o: /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/cvgui.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1743690777
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/opencv/build/include -I/C/MinGW/msys/1.0/local/include/tesseract -I/C/Program\ Files/Java/jdk1.6.0_39/include -I/C/Program\ Files/Java/jdk1.6.0_39/include/win32 -D__BLOB_T_DEFINED  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1743690777/cvgui.o /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/cvgui.cpp

${OBJECTDIR}/_ext/1743690777/finder.o: /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/finder.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1743690777
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/opencv/build/include -I/C/MinGW/msys/1.0/local/include/tesseract -I/C/Program\ Files/Java/jdk1.6.0_39/include -I/C/Program\ Files/Java/jdk1.6.0_39/include/win32 -D__BLOB_T_DEFINED  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1743690777/finder.o /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/finder.cpp

${OBJECTDIR}/_ext/1743690777/imgdb.o: /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/imgdb.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1743690777
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/opencv/build/include -I/C/MinGW/msys/1.0/local/include/tesseract -I/C/Program\ Files/Java/jdk1.6.0_39/include -I/C/Program\ Files/Java/jdk1.6.0_39/include/win32 -D__BLOB_T_DEFINED  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1743690777/imgdb.o /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/imgdb.cpp

${OBJECTDIR}/_ext/1743690777/pyramid-template-matcher.o: /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/pyramid-template-matcher.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1743690777
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/opencv/build/include -I/C/MinGW/msys/1.0/local/include/tesseract -I/C/Program\ Files/Java/jdk1.6.0_39/include -I/C/Program\ Files/Java/jdk1.6.0_39/include/win32 -D__BLOB_T_DEFINED  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1743690777/pyramid-template-matcher.o /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/pyramid-template-matcher.cpp

${OBJECTDIR}/_ext/1743690777/sikuli-debug.o: /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/sikuli-debug.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1743690777
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/opencv/build/include -I/C/MinGW/msys/1.0/local/include/tesseract -I/C/Program\ Files/Java/jdk1.6.0_39/include -I/C/Program\ Files/Java/jdk1.6.0_39/include/win32 -D__BLOB_T_DEFINED  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1743690777/sikuli-debug.o /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/sikuli-debug.cpp

${OBJECTDIR}/_ext/1743690777/tessocr.o: /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/tessocr.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1743690777
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/opencv/build/include -I/C/MinGW/msys/1.0/local/include/tesseract -I/C/Program\ Files/Java/jdk1.6.0_39/include -I/C/Program\ Files/Java/jdk1.6.0_39/include/win32 -D__BLOB_T_DEFINED  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1743690777/tessocr.o /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/tessocr.cpp

${OBJECTDIR}/_ext/1743690777/vision.o: /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/vision.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1743690777
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/opencv/build/include -I/C/MinGW/msys/1.0/local/include/tesseract -I/C/Program\ Files/Java/jdk1.6.0_39/include -I/C/Program\ Files/Java/jdk1.6.0_39/include/win32 -D__BLOB_T_DEFINED  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1743690777/vision.o /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/vision.cpp

${OBJECTDIR}/_ext/1743690777/visionJAVA_wrap.o: /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/visionJAVA_wrap.cxx 
	${MKDIR} -p ${OBJECTDIR}/_ext/1743690777
	${RM} $@.d
	$(COMPILE.cc) -O2 -I/C/opencv/build/include -I/C/MinGW/msys/1.0/local/include/tesseract -I/C/Program\ Files/Java/jdk1.6.0_39/include -I/C/Program\ Files/Java/jdk1.6.0_39/include/win32 -D__BLOB_T_DEFINED  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1743690777/visionJAVA_wrap.o /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Vision/visionJAVA_wrap.cxx

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
