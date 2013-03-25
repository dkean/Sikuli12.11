/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sikuli.guide;

import java.awt.Color;
import java.awt.Point;
import org.sikuli.script.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 * @author rhocke
 */
public class AAARunGuide {
  public static BufferedImage b = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
  private static SikuliGuide g = new SikuliGuide();
  private static SikuliGuideComponent ggc;

  static SikuliGuideComponent.Layout LO = SikuliGuideComponent.Layout.OVER;
  static SikuliGuideComponent.Layout LT = SikuliGuideComponent.Layout.TOP;
  static SikuliGuideComponent.Layout LB = SikuliGuideComponent.Layout.BOTTOM;
  static SikuliGuideComponent.Layout LL = SikuliGuideComponent.Layout.LEFT;
  static SikuliGuideComponent.Layout LR = SikuliGuideComponent.Layout.RIGHT;
  static Screen s = new Screen();
  static String images = "/Users/rhocke/Dropbox/Public/SikuliX/Guide/test.sikuli";

  public static void main(String[] args) throws FindFailed {
    //gTest(s);
    animTest(s);
  }

  public static String getImage(String img, String dir) {
    if (dir == null) {
      dir = images;
    }
    return (new File(dir, img + ".png")).getAbsolutePath();
  }

  public static String getImage(String img) {
    return  getImage(img, null);
  }

  public static void animTest(Screen s) throws FindFailed {
    s.hover(getImage("nbbtns1"));
    Region r1 = Region.create(s.getLastMatch());
    gCo(r1, "hallo", 16, LR);
    gCo(getImage("nbbtns1"), "hallo", 16, LB);
    gB(r1.below(100), "ClickMe", LB);
    String ret = g.showNow(20);
    System.out.println("GuideTest: " + ret);
  }

  public static void gTest(Screen s) throws FindFailed {
    Region r1 = s.getCenter().grow(200);
    Region r2 = r1.grow(100);

    gR(r1);
    gCo(r1, "Ein langer Text", 0, LB);
    gCo(r1, "Ein langer Text", 0, LT);
    gC(r2);
    gCo(r1, "Ein sehr sehr langer langer langer Text", 36, LR);
    gT(r2, "Ein Text", 36, LB);
    gB(r2, "ClickMe", LL);
    Point f = ggc.getRegion().getCenter();
    Point t = r2.getTopRight();
    gAr(f, t);

    String ret = g.showNow(20);
    System.out.println("GuideTest: " + ret);
  }

  private static void gR(Region t) {
    SikuliGuideRectangle gc = new SikuliGuideRectangle(t);
    gc.setLocationRelativeToRegion(t, LO);
    g.addToFront(gc);
    ggc = gc;
  }

  private static void gC(Region t) {
    SikuliGuideCircle gc = new SikuliGuideCircle(t);
    gc.setLocationRelativeToRegion(t, LO);
    g.addToFront(gc);
    ggc = gc;
  }

  private static void gB(Region t, String name, SikuliGuideComponent.Layout l) {
    SikuliGuideButton gc = new SikuliGuideButton(name);
    gc.setLocationRelativeToRegion(t, l);
    g.addToFront(gc);
    ggc = gc;
  }

  private static void gT(Region t, String text, int fSize, SikuliGuideComponent.Layout l) {
    SikuliGuideText gc = new SikuliGuideText(text);
    gc.setFontSize(fSize);
    gc.setLocationRelativeToRegion(t, l);
    gc.updateComponent();
    g.addToFront(gc);
    ggc = gc;
  }

  private static void gCo(Region t, String text, int fSize, SikuliGuideComponent.Layout l) {
    SikuliGuideCallout gc = new SikuliGuideCallout(text);
    gc.setFontSize(fSize);
    gc.setColors(null, null, Color.BLUE, null, null);
    gc.setLocationRelativeToRegion(t, l);
    g.addToFront(gc);
    ggc = gc;
  }

  private static void gCo(String image, String text, int fSize, SikuliGuideComponent.Layout l) {
    SikuliGuideCallout gc = new SikuliGuideCallout(text);
    gc.setFontSize(fSize);
    gc.setColors(null, null, Color.BLUE, null, null);
    SikuliGuideComponent ga = new SikuliGuideAnchor(new Pattern(image));
    gc.setLocationRelativeToComponent(ga, l);
    g.addToFront(gc);
    ggc = gc;
  }

  private static SikuliGuideComponent gAn(Pattern p) {
    SikuliGuideAnchor c = new SikuliGuideAnchor(p);
    g.addToFront(c);
    return c;
  }

  private static void gAr(Point from, Point to) {
    SikuliGuideArrow gc = new SikuliGuideArrow(from, to);
    g.addToFront(gc);
    ggc = gc;
  }
}
