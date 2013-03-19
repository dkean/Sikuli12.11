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
	${OBJECTDIR}/_ext/292282698/WinUtil.o


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
LDLIBSOPTIONS=-L/C/Program\ Files/Java/jdk1.6.0_39/jre/bin /C/Program\ Files/Java/jdk1.6.0_39/lib/jawt.lib

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/WinUtil.${CND_DLIB_EXT}

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/WinUtil.${CND_DLIB_EXT}: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.cc} -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/WinUtil.${CND_DLIB_EXT} ${OBJECTFILES} ${LDLIBSOPTIONS} -ljawt -Wl,--subsystem,windows,--kill-at -shared

${OBJECTDIR}/_ext/292282698/WinUtil.o: /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Win/OSUtil/WinUtil.cc 
	${MKDIR} -p ${OBJECTDIR}/_ext/292282698
	${RM} $@.d
	$(COMPILE.cc) -O2 -D_WIN32_WINNT=0x0500 -I/C/Program\ Files/Java/jdk1.6.0_39/include -I/C/Program\ Files/Java/jdk1.6.0_39/include/win32  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/292282698/WinUtil.o /C/Users/Raimund\ Hocke/Documents/GitHub/Sikuli12.11/Resources/natives/Win/OSUtil/WinUtil.cc

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/WinUtil.${CND_DLIB_EXT}

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
