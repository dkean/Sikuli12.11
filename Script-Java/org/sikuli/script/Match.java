/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.script;

import java.awt.image.BufferedImage;
import org.sikuli.script.natives.FindResult;

public class Match extends Region implements Comparable {

  private double simScore;
  private String ocrText = null;
  private Location target = null;
//TODO set the matches image
  private String image = null;

  public Match(Match m) {
    init(m.x, m.y, m.w, m.h, m.getScreen());
    copy(m);
  }

  private Match(Match m, Screen parent) {
    init(m.x, m.y, m.w, m.h, parent);
    copy(m);
  }

  /**
   * used by TextRecognizer.listText()
   *
   * @param x
   * @param x
   * @param w
   * @param h
   * @param Score
   * @param parent
   * @param text
   */
  public Match(int x, int y, int w, int h, double Score, Screen parent, String text) {
    init(x, y, w, h, parent);
    simScore = Score;
    ocrText = text;
  }

  private Match(int _x, int _y, int _w, int _h, double score, Screen _parent) {
    init(_x, _y, _w, _h, _parent);
    simScore = score;
  }

  protected Match(FindResult f, Screen _parent) {
    init(f.getX(), f.getY(), f.getW(), f.getH(), _parent);
    simScore = f.getScore();
  }

  private void init(int X, int Y, int W, int H, Screen parent) {
    x = X;
    y = Y;
    w = W;
    h = H;
    setScreen(parent);
  }

  private void copy(Match m) {
    simScore = m.simScore;
    ocrText = m.ocrText;
    image = m.image;
    target = m.target;
  }

  public double getScore() {
    return simScore;
  }

  @Override
  public Location getTarget() {
    if (target != null) {
      return target;
    }
    return getCenter();
  }

  public void setTargetOffset(Location offset) {
    target = new Location(getCenter());
    target.translate(offset.x, offset.y);
  }

  /**
   * convenience - returns same as for the used Pattern
   *
   * @return the relative offset
   */
  public Location getTargetOffset() {
    return(Location.getOffset(getTarget(), getCenter()));
  }

  protected void setImage(String imageFileName) {
    image = imageFileName;
  }

  public BufferedImage getImage() {
    if (image == null) {
      return null;
    } else {
      return ImageLocator.getImage(image);
    }
  }

  @Override
  public String text() {
    if (ocrText == null) {
      return super.text();
    }
    return ocrText;
  }

  @Override
  public int compareTo(Object o) {
    Match m = (Match) o;
    if (simScore != m.simScore) {
      return simScore < m.simScore ? -1 : 1;
    }
    if (x != m.x) {
      return x - m.x;
    }
    if (y != m.y) {
      return y - m.y;
    }
    if (w != m.w) {
      return w - m.w;
    }
    if (h != m.h) {
      return h - m.h;
    }
    if (equals(o)) {
      return 0;
    }
    return -1;
  }

  @Override
  public boolean equals(Object oThat) {
    if (this == oThat) {
      return true;
    }
    if (!(oThat instanceof Match)) {
      return false;
    }
    Match that = (Match) oThat;
    return x == that.x && y == that.y && w == that.w && h == that.h
            && Math.abs(simScore - that.simScore) < 1e-5 && getTarget().equals(that.getTarget());
  }

  @Override
  public String toString() {
    String target = "center";
    Location c = getCenter();
    if (target != null && !c.equals(target)) {
      target = target.toString();
    }
    return String.format("Match[%d,%d %dx%d score=%.2f target=%s]", x, y, w, h, simScore, target);
  }
}
