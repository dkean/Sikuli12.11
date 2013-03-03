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
CND_CONF=Debug
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/_ext/513528463/MacHotkeyManager.o


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
LDLIBSOPTIONS=-dynamic

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libMacUtil.jnilib

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libMacUtil.jnilib: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.cc} -framework Carbon -framework JavaVM -dynamiclib -install_name libMacUtil.jnilib -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libMacUtil.jnilib -fPIC ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/_ext/513528463/MacHotkeyManager.o: ../../Resources/natives/Mac/Hotkey/MacHotkeyManager.cc 
	${MKDIR} -p ${OBJECTDIR}/_ext/513528463
	${RM} $@.d
	$(COMPILE.cc) -g -I/usr/local/include -I/System/Library/Frameworks/JavaVM.framework/Headers -I../RaiManSikuli2012/Native/Vision -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/513528463/MacHotkeyManager.o ../../Resources/natives/Mac/Hotkey/MacHotkeyManager.cc

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libMacUtil.jnilib

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
