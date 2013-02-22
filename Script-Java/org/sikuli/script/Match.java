/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.script;

import java.awt.image.BufferedImage;
import org.sikuli.script.natives.FindResult;

/**
 * holds the result of a find operation, is itself the region on the screen,
 * where the image was found and hence inherits all methods from Region<br />
 * attributes:<br /> the match score (0 ... 1.0)<br /> the click target (e.g.
 * from Pattern)<br /> the filename of the used image<br />or the text used for
 * find text
 */
public class Match extends Region implements Comparable {

  private double simScore;
  private Location target = null;
  private String image = null;
  private String ocrText = null;

  /**
   * create a copy of Match object<br />
   * to e.g. set another TargetOffset for same match
   *
   * @param m
   */
  public Match(Match m) {
    init(m.x, m.y, m.w, m.h, m.getScreen());
    copy(m);
  }

  private Match(Match m, Screen parent) {
    init(m.x, m.y, m.w, m.h, parent);
    copy(m);
  }

  /**
   * internally used constructor by TextRecognizer.listText()
   *
   * @param x
   * @param x
   * @param w
   * @param h
   * @param Score
   * @param parent
   * @param text
   */
  protected Match(int x, int y, int w, int h, double Score, Screen parent, String text) {
    init(x, y, w, h, parent);
    simScore = Score;
    ocrText = text;
  }

  private Match(int _x, int _y, int _w, int _h, double score, Screen _parent) {
    init(_x, _y, _w, _h, _parent);
    simScore = score;
  }

  /**
   * internally used constructor used by find image
   *
   * @param f
   * @param _parent
   */
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
    target = null;
    if (m.target != null) {
      target = (Location) m.target.clone();
    }
  }

  /**
   * the match score
   *
   * @return a decimal value between 0 (no match) and 1 (exact match)
   */
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

  /**
   * like Pattern.TargetOffset sets the click target by offset relative to the
   * center
   *
   * @param offset
   */
  public void setTargetOffset(Location offset) {
    target = new Location(getCenter());
    target.translate(offset.x, offset.y);
  }

  /**
   * like Pattern.TargetOffset sets the click target relative to the center
   * @param x
   * @param y
   */
  public void setTargetOffset(int x, int y) {
    setTargetOffset(new Location(x,y));
  }

  /**
   * convenience - same as for Pattern
   *
   * @return the relative offset to the center
   */
  public Location getTargetOffset() {
    return (getCenter().getOffset(getTarget()));
  }

  /**
   * internal use: set the image filename after finding with success
   * @param imageFileName
   */
  protected void setImage(String imageFileName) {
    image = imageFileName;
    if (Settings.Highlight) {
      highlight(DEFAULT_HIGHLIGHT_TIME);
    }
  }

  /**
   * get the image used for searching as in-memory image
   * @return image
   */
  public BufferedImage getImage() {
    if (image == null) {
      return null;
    } else {
      return ImageLocator.getImage(image);
    }
  }

  /**
   * get the filename of the image used for searching
   * @return filename
   */
  public String getImageFilename() {
    return image;
  }

  /**
   *
   * @return the text used for searching
   */
  public String getText() {
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
    String starget;
    Location c = getCenter();
    if (target != null && !c.equals(target)) {
      starget = String.format("Target:%d,%d", target.x, target.y);
    } else {
      starget = String.format("Center:%d,%d", c.x, c.y);
    }
    return String.format("M[%d,%d %dx%d]@%s S:%.2f %s", x, y, w, h,
              (getScreen()== null ? "Screen???" : getScreen().toStringShort()),
              simScore, starget);
  }
}
