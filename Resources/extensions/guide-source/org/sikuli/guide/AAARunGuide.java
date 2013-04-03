/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sikuli.guide;

import java.awt.Color;
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
  private static SikuliGuideComponent g1, g2, g3;

  static SikuliGuideComponent.Layout LO = SikuliGuideComponent.Layout.OVER;
  static SikuliGuideComponent.Layout LT = SikuliGuideComponent.Layout.TOP;
  static SikuliGuideComponent.Layout LB = SikuliGuideComponent.Layout.BOTTOM;
  static SikuliGuideComponent.Layout LL = SikuliGuideComponent.Layout.LEFT;
  static SikuliGuideComponent.Layout LR = SikuliGuideComponent.Layout.RIGHT;
  static Screen s = new Screen();
  static String images = "/Users/rhocke/Dropbox/Public/SikuliX/Guide/test.sikuli";

  public static void main(String[] args) throws FindFailed {
    /*    Settings.OcrTextRead = true;
     * String  text = s.find(getImage("sometext")).text();
     * String finaltext = text.trim();
     * System.out.println(finaltext);
     */
    //runTest(s);
    //runTestImage(s);
  }

  public static void runTestImage(Screen s) throws FindFailed {
    String img = getImage("nbbtns1");
    s.find(img);
    s.hover();
    Region r1 = Region.create(s.getLastMatch());
//    g.image(img).setTarget(r1.above(10).above(10)).setLayout(LB).setScale(0);
    g1 = g.button("OK").setTarget(img);//.setLayout(LB);
    g.callout("hallo").setTarget(img).setLayout(LL).setColor(Color.WHITE).setTextColor(Color.BLUE).setFontSize(24);
    //    g.button("Cancel").setTarget(g1).setLayout(LB);
    String ret = g.showNow(20);
    System.out.println("GuideTest: " + ret);
  }

  public static void runTest(Screen s) throws FindFailed {
    s.hover(getImage("nbbtns1"));
    Region r1 = Region.create(s.getLastMatch());
    Region r2 = s.getCenter().grow(100);
    g.rectangle().setTarget(r1).setStroke(10).setColor(Color.YELLOW);
    //g.text("ein Text").setTarget(r1).setLayout(LB).setFontSize(16).setColor(Color.RED).setTextColor(Color.WHITE);
    g.flag("hallo hallo").setLayout(LT).setTarget(r1).setColors(null, Color.DARK_GRAY, null, null, Color.WHITE);
    g2 = g.callout("hallo").setTarget(r1).setFontSize(16).setLayout(LR).setColor(Color.GREEN);
    //gCo(getImage("nbbtns1"), "hallo", 16, LB);
    g1 = g.button("OK").setTarget(r1.below(100)).setLayout(LB).setFontSize(72);
    g.circle().setTarget(g1).setColor(Color.YELLOW);
//    g.rectangle().setTarget(r2);
    g1 = g.image(getImage("nbbtns1")).setTarget(r2).setLayout(LO).setScale(0);
    g2 = g.bracket().setTarget(g1).setLayout(LB).setColor(Color.GREEN);
    g.text("a bracket").setTarget(g2).setLayout(LB);
    g.arrow(r1, g1.getRegion()).setColor(Color.BLUE);
    String ret = g.showNow(20);
    System.out.println("GuideTest: " + ret);
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

  private static SikuliGuideComponent gCo(String image, String text, int fSize, SikuliGuideComponent.Layout l) {
    SikuliGuideCallout gc = new SikuliGuideCallout(text);
    gc.setFontSize(fSize);
    SikuliGuideComponent ga = new SikuliGuideAnchor(new Pattern(image));
    gc.setLocationRelativeToComponent(ga, l);
    g.addToFront(gc);
    return gc;
  }
}
