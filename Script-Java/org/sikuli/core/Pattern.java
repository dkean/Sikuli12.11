/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Pattern {

  private String imgURL = null;
  private float similarity = 0.7f;
  private Location offset = new Location(0, 0);

  public Pattern() {
  }

  public Pattern(Pattern p) {
    imgURL = p.imgURL;
    similarity = p.similarity;
    offset.x = p.offset.x;
    offset.y = p.offset.y;
  }

  public Pattern(String imgURL_) {
    imgURL = imgURL_;
  }

  public Pattern(BufferedImage bimg) {
    //TODO implement buffered image constructor
  }

  public Pattern similar(float sim) {
    similarity = sim;
    return this;
  }

  public Pattern exact() {
    similarity = 0.99f;
    return this;
  }

  public float getSimilar() {
    return this.similarity;
  }

  public Pattern targetOffset(int dx, int dy) {
    offset.x = dx;
    offset.y = dy;
    return this;
  }

  public Pattern targetOffset(Location off) {
    offset.x = off.x;
    offset.y = off.y;
    return this;
  }

  public Location getTargetOffset() {
    return offset;
  }

  public Pattern setFilename(String imgURL_) {
    imgURL = imgURL_;
    return this;
  }

  public String getFilename() {
    return imgURL;
  }

  public String checkFile() {
    try {
      ImageLocator.locate(imgURL);
      return imgURL;
    } catch (IOException ex) {
      return null;
    }
  }

  public BufferedImage getImage() {
    return ImageLocator.getImage(getFilename());
  }

  @Override
  public String toString() {
    String ret = "Pattern(\"" + imgURL + "\")";
    ret += ".minSimilarity(" + similarity + ")";
    if (offset.x != 0 || offset.y != 0) {
      ret += ".targetOffset(" + offset.x + "," + offset.y + ")";
    }
    return ret;
  }
}
