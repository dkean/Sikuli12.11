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

  static final GraphicsEnvironment genv;
  static final GraphicsDevice[] gdevs;
  static DesktopRobot[] robots;
  static Screen[] screens;
  protected static int primaryScreen = -1;
  private int curID = 0;
  private GraphicsDevice curGD;
  protected boolean waitPrompt;
  protected CapturePrompt prompt;
  protected ScreenImage lastScreenImage;

  //<editor-fold defaultstate="collapsed" desc="Initialization">
  static {
    genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
    gdevs = genv.getScreenDevices();
    initScreens();
  }

  private static void initScreens() {
    try {
      robots = new DesktopRobot[gdevs.length];
      screens = new Screen[gdevs.length];
      for (int i = 0; i < gdevs.length; i++) {
        screens[i] = new Screen(i);
        robots[i] = new DesktopRobot(screens[i]);
        //robots[i].setAutoWaitForIdle(false); //TODO: make sure we don't need this
        robots[i].setAutoDelay(10);
      }
      Debug.log(2, "Screen static initScreens");
    } catch (AWTException e) {
      Debug.error("Can't initiate Java Robot: " + e);
    }
  }

  private Screen(int id) {
    if (id < gdevs.length) {
      curGD = gdevs[id];
      curID = id;
    } else {
      initGD();
    }
    initBounds();
  }

  public Screen() {
    new Screen(getPrimaryId());
  }

  private void initGD() {
    curGD = genv.getDefaultScreenDevice();
    for (int i = 0; i < gdevs.length; i++) {
      if (gdevs[i] == curGD) {
        curID = i;
      }
    }
  }

  private void initBounds() {
    Rectangle bounds = getBounds();
    x = (int) bounds.getX();
    y = (int) bounds.getY();
    w = (int) bounds.getWidth();
    h = (int) bounds.getHeight();
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="getters setters">
  boolean useFullscreen() {
    return false;
  }

  public static int getNumberScreens() {
    return gdevs.length;
  }

  public static int getPrimaryId() {
    if (primaryScreen < 0) {
      primaryScreen = 0;
      for (int i = 0; i < getNumberScreens(); i++) {
        if (getBounds(i).x == 0 && getBounds(i).y == 0) {
          primaryScreen = i;
          break;
        }
      }
    }
    return primaryScreen;
  }

  public static Screen getPrimaryScreen() {
    return screens[getPrimaryId()];
  }

  public static Screen getScreen(int id) {
    return screens[id];
  }

  public static Screen getScreenContaining(Rectangle rect) {
    for (int i = 0; i < Screen.getNumberScreens(); i++) {
      Rectangle sb = Screen.getBounds(i);
      if (sb.contains(rect.getLocation())) {
        return getScreen(i);
      }
    }
    return null;
  }

  public static Screen getScreenContaining(Location point) {
    for (int i = 0; i < Screen.getNumberScreens(); i++) {
      Rectangle sb = Screen.getBounds(i);
      if (sb.contains(point)) {
        return getScreen(i);
      }
    }
    return null;
  }

  public static Rectangle getBounds(int id) {
    return gdevs[id].getDefaultConfiguration().getBounds();
  }

  public static IRobot getRobot(int id) {
    return robots[id];
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

  @Override
  public Region newRegion(Rectangle rect) {
    return Region.create(rect, this);
  }

  @Override
  public Rectangle getROI() {
    return new Rectangle(x, y, w, h);
  }

  @Override
  public void setROI(int x, int y, int w, int h) {
    setROI(new Rectangle(x, y, w, h));
  }

  @Override
  public void setROI(Rectangle r) {
    r = getBounds().intersection(r);
    x = (int) r.getX();
    y = (int) r.getY();
    w = (int) r.getWidth();
    h = (int) r.getHeight();
    if (w == 0 || h == 0) {
      initBounds();
    }
  }

  @Override
  public void setROI(Region roi) {
    setROI(roi.getRect());
  }

  public void resetROI() {
    initBounds();
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
    return String.format("S(%d)[%d,%d %dx%d] E:%s, T:%.1f",
            curID, (int) r.getX(), (int) r.getY(),
            (int) r.getWidth(), (int) r.getHeight(),
            throwException ? "Y" : "N", autoWaitTimeout);
  }

  @Override
  public String toStringShort() {
    Rectangle r = getBounds();
    return String.format("S(%d)[%d,%d %dx%d]",
            curID, (int) r.getX(), (int) r.getY(),
            (int) r.getWidth(), (int) r.getHeight());
  }
}
