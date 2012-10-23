# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
# modified RaiMan 2012
##
# This module provides a Jython interface of Sikuli Script to automate GUI
# interactions.
##
from __future__ import with_statement
import time
import __builtin__
import __main__
import types
import sys
import os

from org.sikuli.core import Region as JRegion
from Region import *
from org.sikuli.core import Screen as JScreen
from Screen import *
from org.sikuli.core import Match
from org.sikuli.core import Pattern
from org.sikuli.core import FindFailed
from org.sikuli.core import SikuliEvent
from org.sikuli.core import UnionScreen
from org.sikuli.core import Finder
from org.sikuli.core import Location
from org.sikuli.core import Settings
from org.sikuli.system import OS
from org.sikuli.system import App
from org.sikuli.core import ScreenHighlighter
from org.sikuli.core import ImageLocator
from org.sikuli.core import Key
from org.sikuli.core import KeyModifier
from org.sikuli.core.KeyModifier import KEY_CTRL, KEY_SHIFT, KEY_META, KEY_CMD, KEY_WIN, KEY_ALT
from org.sikuli.core import Button
from java.awt import Rectangle
# RaiMan not used from VDict import *
from Helper import *
from Env import *
import SikuliImporter

from org.sikuli.script import SikuliScript
_si = SikuliScript()

##
# loads a Sikuli extension (.jar) from
#  1. user's sikuli data path
#  2. bundle path
#
def load(jar):
   from org.sikuli.utility import ExtensionManager
   def _load(abspath):
      if os.path.exists(abspath):
         if not abspath in sys.path:
            sys.path.append(abspath)
         return True
      return False
   
   if _load(jar):
      return True
   path = getBundlePath()
   if path:
      jarInBundle = os.path.join(path, jar)
      if _load(jarInBundle):
         return True
   path = ExtensionManager.getInstance().getUserExtPath()
   jarInExtPath = os.path.join(path, jar)
   if _load(jarInExtPath):
      return True
   return False

def addModPath(path):
   if path[-1] == Settings.getFilePathSeperator():
      path = path[:-1]
   if not path in sys.path:
      sys.path.append(path)

def addImagePath(path):
   ImageLocator.addImagePath(path)

def getImagePath():
   return [e for e in ImageLocator.getImagePath() if e]

def removeImagePath(path):
   ImageLocator.removeImagePath(path)
   
def resetImagePath(path):
   ImageLocator.resetImagePath(path)

##
# Sets the path for searching images in all Sikuli Script methods. <br/>
# Sikuli IDE sets this to the path of the bundle of source code (.sikuli)
# automatically. If you write Sikuli scripts by the Sikuli IDE, you should
# not call this method.
#
def setBundlePath(path):
   ImageLocator.setBundlePath(path)

def getBundlePath():
   return ImageLocator.getBundlePath(path)

##
# Sikuli shows actions (click, dragDrop, ... etc.) if this flag is set to <i>True</i>.
# The default setting is <i>False</i>.
#
def setShowActions(flag):
   SikuliScript.setShowActions(flag)

##
# Shows a question-message dialog requesting input from the user.
# @param msg The message to display.
# @param default The preset text of the input field.
# @return The user's input string.
#
def input(msg="", default=""):
   return SikuliScript.input(msg, default)

def capture(*args):
   scr = UnionScreen()
   if len(args) == 0:
      simg = scr.userCapture()
      if simg:
         return simg.getFilename()
      else:
         return None
   elif len(args) == 1:
      if __builtin__.type(args[0]) is types.StringType or __builtin__.type(args[0]) is types.UnicodeType:
         simg = scr.userCapture(args[0])
         if simg:
            return simg.getFilename()
         else:
            return None
      else:
         return scr.capture(args[0]).getFilename()
   elif len(args) == 4:
      return scr.capture(args[0], args[1], args[2], args[3]).getFilename()
   else:
      return None


def selectRegion(msg=None):
   if msg:
      r = UnionScreen().selectRegion(msg)
   else:
      r = UnionScreen().selectRegion()
   if r:
      return Region(r)
   else:
      return None


##
# Switches the frontmost application to the given application.
# If the given application is not running, it will be launched by openApp()
# automatically. <br/>
# Note: On Windows, Sikule searches in the text on the title bar
# instead of the application name.
# @param app The name of the application. (case-insensitive)
#
def switchApp(app):
   return SikuliScript.switchApp(app)

##
# Opens the given application. <br/>
# @param app The name of an application if it is in the environment variable PATH, or the full path to an application.
#
def openApp(app):
   return SikuliScript.openApp(app)

##
# Closes the given application. <br/>
# @param app The name of the application. (case-insensitive)
#
def closeApp(app):
   return SikuliScript.closeApp(app)

##
# Sleeps until the given amount of time in seconds has elapsed.
# @param sec The amount of sleeping time in seconds.
def sleep(sec):
   time.sleep(sec)

##
# Shows a message dialog containing the given message.
# @param msg The given message string.
def popup(msg, title="Sikuli"):
   SikuliScript.popup(msg, title)

def exit(code=0):
   ScreenHighlighter.closeAll()
   sys.exit(code)

##
# Runs the given string command.
# @param msg The given string command.
# @return Returns the output from the executed command.
def run(cmd):
    return SikuliScript.run(cmd)

############### SECRET FUNCTIONS ################

def initSikuli():
   dict = globals()
   dict['METHODCATALOG'] = sys.modules[__name__].__dict__
   dict['SCREEN'] = Screen()
   dict['SCREEN']._exposeAllMethods(__name__)
   #print "Sikuli is initialized. ", id(dict['SCREEN'])


initSikuli()
