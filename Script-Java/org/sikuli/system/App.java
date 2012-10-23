/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.system;

import org.sikuli.core.Env;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.sikuli.core.Region;
import org.sikuli.core.Settings;
import org.sikuli.utility.Clipboard;
import org.sikuli.utility.Debug;

public class App {

  protected static OSUtil _osUtil = Settings.getOSUtil();
  protected String _appName;
  protected int _pid;

  public App(String appName) {
    _appName = appName;
    _pid = 0;
  }

  protected App(String appName, int pid) {
    _appName = appName;
    _pid = pid;
  }

  public static App open(String appName) {
    return (new App(appName)).open();
  }

  public static int close(String appName) {
    return _osUtil.closeApp(appName);
  }

  public static App focus(String appName) {
    return (new App(appName)).focus();
  }

  public static App focus(String appName, int num) {
    return (new App(appName)).focus(num);
  }

  public App focus() {
    return focus(0);
  }

  public App focus(int num) {
    Debug.history("App.focus " + this.toString() + " #" + num);
    if (_pid != 0) {
      if (_osUtil.switchApp(_pid, num) == 0) {
        Debug.error("App.focus failed: " + _appName
                + "(" + _pid + ") not found");
        return null;
      }
    } else {
      boolean failed = false;
      if (Env.isWindows()) {
        _pid = _osUtil.switchApp(_appName, num);
        if (_pid == 0) {
          failed = true;
        }
      } else {
        if (_osUtil.switchApp(_appName, num) < 0) {
          failed = true;
        }
      }
      if (failed) {
        Debug.error("App.focus failed: " + _appName + " not found");
        return null;
      }
    }
    return this;
  }

  public App open() {
    if (Env.isWindows() || Env.isLinux()) {
      int pid = _osUtil.openApp(_appName);
      _pid = pid;
      Debug.history("App.open " + this.toString());
      if (pid == 0) {
        Debug.error("App.open failed: " + _appName + " not found");
        return null;
      }
    } else {
      Debug.history("App.open " + this.toString());
      if (_osUtil.openApp(_appName) < 0) {
        Debug.error("App.open failed: " + _appName + " not found");
        return null;
      }
    }
    return this;
  }

  public int close() {
    Debug.history("App.close " + this.toString());
    if (_pid != 0) {
      int ret = _osUtil.closeApp(_pid);
      if (ret >= 0) {
        return ret;
      }
    }
    return close(_appName);
  }

  public String name() {
    return _appName;
  }

  public Region window() {
    if (_pid != 0) {
      return _osUtil.getWindow(_pid);
    }
    return _osUtil.getWindow(_appName);
  }

  public Region window(int winNum) {
    if (_pid != 0) {
      return _osUtil.getWindow(_pid, winNum);
    }
    return _osUtil.getWindow(_appName, winNum);
  }

  public static Region focusedWindow() {
    return _osUtil.getFocusedWindow();
  }

  public static String getClipboard() {
    Transferable content = Clipboard.getSystemClipboard().getContents(null);
    try {
      if (content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        return (String) content.getTransferData(DataFlavor.stringFlavor);
      }
    } catch (UnsupportedFlavorException e) {
      Debug.error("Env.getClipboard: UnsupportedFlavorException: " + content);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }

  public static void setClipboard(String text) {
    Clipboard.putText(Clipboard.PLAIN, Clipboard.UTF8,
            Clipboard.BYTE_BUFFER, text);
  }

  @Override
  public String toString() {
    return _appName + "(" + _pid + ")";
  }
}
