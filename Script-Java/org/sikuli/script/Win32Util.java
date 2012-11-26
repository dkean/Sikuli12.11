/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.script;

import java.awt.Rectangle;
import java.awt.Window;
import org.sikuli.script.Region;
import org.sikuli.script.Debug;
import org.sikuli.script.FileManager;
import org.sikuli.system.OSUtil;

public class Win32Util implements OSUtil {

  static {
    FileManager.loadLibrary("Win32Util");
  }

  // compatible to the old switchApp
  @Override
  public int switchApp(String appName) {
    return switchApp(appName, 0);
  }

  @Override
  public native int switchApp(String appName, int num);

  @Override
  public native int switchApp(int pid, int num);

  @Override
  public native int openApp(String appName);

  @Override
  public native int closeApp(String appName);

  @Override
  public native int closeApp(int pid);

  @Override
  public Region getWindow(String appName) {
    return getWindow(appName, 0);
  }

  @Override
  public Region getWindow(int pid) {
    return getWindow(pid, 0);
  }

  @Override
  public Region getWindow(String appName, int winNum) {
    long hwnd = getHwnd(appName, winNum);
    return _getWindow(hwnd, winNum);
  }

  @Override
  public Region getWindow(int pid, int winNum) {
    long hwnd = getHwnd(pid, winNum);
    return _getWindow(hwnd, winNum);
  }

  protected Region _getWindow(long hwnd, int winNum) {
    Rectangle rect = getRegion(hwnd, winNum);
    Debug.log("getWindow: " + rect);
    if (rect != null) {
      return Region.create(rect);
    }
    return null;
  }

  @Override
  public Region getFocusedWindow() {
    Rectangle rect = getFocusedRegion();
    if (rect != null) {
      return Region.create(rect);
    }
    return null;
  }

  @Override
  public native void bringWindowToFront(Window win, boolean ignoreMouse);

  public static native long getHwnd(String appName, int winNum);

  public static native long getHwnd(int pid, int winNum);

  public static native Rectangle getRegion(long hwnd, int winNum);

  public static native Rectangle getFocusedRegion();
}
