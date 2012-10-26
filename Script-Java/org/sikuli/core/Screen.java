/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.core;

import java.awt.*;
import org.sikuli.utility.Debug;
import org.sikuli.utility.Observer;
import org.sikuli.utility.Subject;

public class Screen extends Region implements Observer, IScreen {

  static GraphicsEnvironment genv = null;
  static GraphicsDevice[] gdevs;
  static DesktopRobot[] robots;
  static Screen[] screens;
  protected static int primaryScreen = -1;
  protected static DesktopRobot actionRobot;
  private int curID = 0;
  private GraphicsDevice curGD;
  private Region curROI;
  protected boolean waitPrompt;
  protected CapturePrompt prompt;
  protected ScreenImage lastScreenImage;

  //<editor-fold defaultstate="collapsed" desc="Initialization">
  private static void initScreens() {
    if (genv != null) {
      return;
    }
    genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
    gdevs = genv.getScreenDevices();
    robots = new DesktopRobot[gdevs.length];
    screens = new Screen[gdevs.length];
    for (int i = 0; i < gdevs.length; i++) {
      screens[i] = new Screen(i, true);
      screens[i].initScreen();
      try {
        robots[i] = new DesktopRobot(screens[i]);
      } catch (AWTException e) {
        Debug.error("Can't initialize Java Robot " + i);
        robots[i] = null;
        screens[i] = null;
      }
      //robots[i].setAutoWaitForIdle(false); //TODO: make sure we don't need this
      robots[i].setAutoDelay(10);
    }
    primaryScreen = 0;
    for (int i = 0; i < getNumberScreens(); i++) {
      if (getBounds(i).x == 0 && getBounds(i).y == 0) {
        primaryScreen = i;
        break;
      }
    }
    try {
      actionRobot = new DesktopRobot(screens[primaryScreen]);
      actionRobot.setAutoDelay(10);
    } catch (AWTException e) {
      Debug.error("Can't initialize Java Robot " + primaryScreen);
      actionRobot = null;
      screens[primaryScreen] = null;
    }
    Debug.log(2, "Screen static initScreens");
  }

  private Screen(int id, boolean init) {
    curID = id;
  }

  public Screen(int id) {
    initScreens();
    curID = id;
    if (id < 0 || id >= gdevs.length) {
      curID = getPrimaryId();
    }
    initScreen();
  }

  /**
   *
   */
  public Screen() {
    initScreens();
    curID = getPrimaryId();
    initScreen();
  }

  private void initScreen() {
    curGD = gdevs[curID];
    if (screens[curID] == null) {
      x = 0;
      y = 0;
      w = -1;
      h = -1;
      curROI = Region.create(x, y, w, h);
      Debug.error("Screen cannot be used. No Robot");
      toStringShort();
    } else {
      Rectangle bounds = getBounds();
      x = (int) bounds.getX();
      y = (int) bounds.getY();
      w = (int) bounds.getWidth();
      h = (int) bounds.getHeight();
      curROI = Region.create(bounds);
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="getters setters">
  boolean useFullscreen() {
    return false;
  }

  private static int getValidID(int id) {
    initScreens();
    if (id < 0 || id >= gdevs.length) {
      return primaryScreen;
    } else {
      return id;
    }
  }

  public static int getNumberScreens() {
    initScreens();
    return gdevs.length;
  }

  public static int getPrimaryId() {
    initScreens();
    return primaryScreen;
  }

  public static Screen getPrimaryScreen() {
    return screens[getPrimaryId()];
  }

  public static Screen getScreen(int id) {
    return screens[getValidID(id)];
  }

  public static Screen getScreenContaining(Region reg) {
    for (int i = 0; i < Screen.getNumberScreens(); i++) {
      Rectangle sb = Screen.getBounds(i);
      if (sb.contains(reg.getTopLeft())) {
        return getScreen(i);
      }
    }
    return Screen.getPrimaryScreen();
  }

  public static Screen getScreenContaining(Location point) {
    for (int i = 0; i < Screen.getNumberScreens(); i++) {
      Rectangle sb = Screen.getBounds(i);
      if (sb.contains(point)) {
        return getScreen(i);
      }
    }
    return Screen.getPrimaryScreen();
  }

  public static Rectangle getBounds(int id) {
    return gdevs[getValidID(id)].getDefaultConfiguration().getBounds();
  }

  public static DesktopRobot getRobot(int id) {
    return robots[getValidID(id)];
  }

  public DesktopRobot getActionRobot() {
    return actionRobot;
  }

  public int getID() {
    return curID;
  }

  public GraphicsDevice getGraphicsDevice() {
    return curGD;
  }

  @Override
  public DesktopRobot getRobot() {
    return robots[curID];
  }

  @Override
  public Rectangle getBounds() {
    return curGD.getDefaultConfiguration().getBounds();
  }

  public Region newRegion(Location loc, int W, int H) {
			return Region.createAt(loc.copyTo(this), W, H);
  }

  public Location newLocation(Location loc) {
			return (new Location(loc)).copyTo(this);
  }

  @Override
  public Region getROI() {
    return curROI;
  }

  @Override
  public void setROI(int x, int y, int w, int h) {
    setROI(new Rectangle(x, y, w, h));
  }

  @Override
  public void setROI(Rectangle r) {
    if (!getRect().contains(r.getLocation())) {
      r.x += getBounds().x;
      r.y += getBounds().y;
    }
    r = getBounds().intersection(r);
    x = (int) r.getX();
    y = (int) r.getY();
    w = (int) r.getWidth();
    h = (int) r.getHeight();
    curROI = Region.create(r);
  }

  @Override
  public void setROI(Region roi) {
    setROI(roi.getRect());
  }

  public void resetROI() {
    initScreen();
  }

  @Override
  public void setLastScreenImage(ScreenImage simg) {
    lastScreenImage = simg;
  }
//</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Capture - SelectRegion">
  @Override
  public ScreenImage capture() {
    return capture(getBounds());
  }

  @Override
  public ScreenImage capture(int x, int y, int w, int h) {
    Rectangle rect = new Rectangle(x, y, w, h);
    return capture(rect);
  }

  @Override
  public ScreenImage capture(Rectangle rect) {
    Rectangle bounds = getBounds();
    rect.x -= bounds.x;
    rect.y -= bounds.y;
    ScreenImage simg = robots[curID].captureScreen(rect);
    simg.x += bounds.x;
    simg.y += bounds.y;
    Debug.log(2, "Screen.capture: " + rect + " Image: " + simg.getImage().toString().substring(0, 25));
    return simg;
  }

  @Override
  public ScreenImage capture(Region reg) {
    return capture(reg.getRect());
  }

  public ScreenImage userCapture() {
    return userCapture("Select a region on the screen");
  }

  public ScreenImage userCapture(final String msg) {
    waitPrompt = true;
    Thread th = new Thread() {
      @Override
      public void run() {
        prompt = new CapturePrompt(Screen.this, Screen.this);
        prompt.prompt(msg);
      }
    };
    th.start();
    try {
      int count = 0;
      while (waitPrompt) {
        Thread.sleep(100);
        if (count++ > 1000) {
          return null;
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    ScreenImage ret = prompt.getSelection();
    prompt.close();
    return ret;
  }

  public Region selectRegion() {
    return selectRegion("Select a region on the screen");
  }

  public Region selectRegion(final String msg) {
    ScreenImage sim = userCapture(msg);
    if (sim == null) {
      return null;
    }
    Rectangle r = sim.getROI();
    return Region.create((int) r.getX(), (int) r.getY(),
            (int) r.getWidth(), (int) r.getHeight());
  }

  @Override
  public void update(Subject s) {
    waitPrompt = false;
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Visual effects">
  @Override
  public void showTarget(Location loc) {
    showTarget(loc, Settings.SlowMotionDelay);
  }

  public void showTarget(Location loc, double secs) {
    if (Settings.ShowActions) {
      ScreenHighlighter overlay = new ScreenHighlighter(this);
      overlay.showTarget(loc, (float) secs);
    }
  }
  //</editor-fold>

  @Override
  public String toString() {
    Rectangle r = getBounds();
    if (curROI.getRect().equals(r)) {
      return String.format("S(%d)[%d,%d %dx%d] E:%s, T:%.1f",
              curID, (int) r.getX(), (int) r.getY(),
              (int) r.getWidth(), (int) r.getHeight(),
              throwException ? "Y" : "N", autoWaitTimeout);
    } else {
      int rx = (int) r.getX();
      int ry = (int) r.getY();
      return String.format("S(%d)[%d,%d %dx%d] ROI[%d,%d, %dx%d] E:%s, T:%.1f",
              curID, rx, ry,
              (int) r.getWidth(), (int) r.getHeight(),
              curROI.x - rx, curROI.y - ry, curROI.w, curROI.h,
              throwException ? "Y" : "N", autoWaitTimeout);
    }
  }

  public String toStringShort() {
    Rectangle r = getBounds();
    return String.format("S(%d)[%d,%d %dx%d]",
            curID, (int) r.getX(), (int) r.getY(),
            (int) r.getWidth(), (int) r.getHeight());
  }
}
