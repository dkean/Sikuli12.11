/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sikuli.guide;

import java.awt.Point;
import org.sikuli.script.*;
import java.awt.image.BufferedImage;

/**
 *
 * @author rhocke
 */
public class AAARunGuide {
  public static BufferedImage b = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
  private static SikuliGuide g;
  private static SikuliGuideComponent ggc;

  public static void main(String[] args) throws FindFailed {
    Screen s = new Screen();
    gTest(s);
  }

  public static void gTest(Screen s) throws FindFailed {
    g = new SikuliGuide();
    Region r1 = s.getCenter().grow(200);
    Region r2 = r1.grow(100);

    gR(r1);
    gCo(r1, "Ein langer Text", 0);
    gC(r2);
    gT(r2, "Ein Text", 24);
    gB(r2, "ClickMe");
    Point f = ggc.getLocation();
    Point t = r2.getTopRight();
    gAr(f, t);

    String ret = g.showNow(20);
    System.out.println("GuideTest: " + ret);
  }

  private static void gR(Region t) {
    SikuliGuideRectangle gc = new SikuliGuideRectangle(t);
    gc.setLocationRelativeToRegion(t, SikuliGuideComponent.Layout.OVER);
    g.addToFront(gc);
    ggc = gc;
  }

  private static void gC(Region t) {
    SikuliGuideCircle gc = new SikuliGuideCircle(t);
    gc.setLocationRelativeToRegion(t, SikuliGuideComponent.Layout.OVER);
    g.addToFront(gc);
    ggc = gc;
  }

  private static void gB(Region t, String name) {
    SikuliGuideButton gc = new SikuliGuideButton(name);
    gc.setLocationRelativeToRegion(t, SikuliGuideComponent.Layout.LEFT);
    g.addToFront(gc);
    ggc = gc;
  }

  private static void gT(Region t, String text, int fSize) {
    SikuliGuideText gc = new SikuliGuideText(text);
    gc.setFontSize(fSize);
    gc.setLocationRelativeToRegion(t, SikuliGuideComponent.Layout.BOTTOM);
    g.addToFront(gc);
    ggc = gc;
  }

  private static void gCo(Region t, String text, int fSize) {
    SikuliGuideCallout gc = new SikuliGuideCallout(text);
    //gc.setFontSize(fSize);
    gc.setLocationRelativeToRegion(t, SikuliGuideComponent.Layout.RIGHT);
    g.addToFront(gc);
    ggc = gc;
  }

  private static void gAr(Point from, Point to) {
    SikuliGuideArrow gc = new SikuliGuideArrow(from, to);
    g.addToFront(gc);
    ggc = gc;
  }
}
