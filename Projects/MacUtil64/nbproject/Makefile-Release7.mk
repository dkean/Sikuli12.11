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
CND_CONF=Release7
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/_ext/1344807628/MacUtil.o


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
LDLIBSOPTIONS=-dynamic /Library/Java/JavaVirtualMachines/jdk1.7.0_09.jdk/Contents/Home/jre/lib/libjawt.dylib

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libMacUtil.dylib

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libMacUtil.dylib: /Library/Java/JavaVirtualMachines/jdk1.7.0_09.jdk/Contents/Home/jre/lib/libjawt.dylib

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libMacUtil.dylib: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.cc} -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libMacUtil.dylib ${OBJECTFILES} ${LDLIBSOPTIONS} -framework Cocoa -dynamiclib -install_name libMacUtil.dylib -fPIC

${OBJECTDIR}/_ext/1344807628/MacUtil.o: ../../Resources/natives/Mac/OSUtilMac/MacUtil.m 
	${MKDIR} -p ${OBJECTDIR}/_ext/1344807628
	${RM} $@.d
	$(COMPILE.cc) -g -I/usr/local/include -I/Library/Java/JavaVirtualMachines/jdk1.7.0_09.jdk/Contents/Home/include -I/Library/Java/JavaVirtualMachines/jdk1.7.0_09.jdk/Contents/Home/include/darwin -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1344807628/MacUtil.o ../../Resources/natives/Mac/OSUtilMac/MacUtil.m

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libMacUtil.dylib

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
