/**
 *
 */
package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.sikuli.script.ImageLocator;
import org.sikuli.script.Region;

public class SikuliGuideImage extends SikuliGuideComponent {

  private BufferedImage image = null;
  float scale;
  int w, h;

  public SikuliGuideImage(String filename) {
    super();
    try {
      String fname = ImageLocator.locate(filename);
      if (fname != null) {
          init(ImageIO.read(new File(fname)));
      }
    } catch (IOException iOException) {
    }
  }

  public SikuliGuideImage(BufferedImage image) {
    super();
    init(image);
  }

  private void init(BufferedImage image) {
    this.image = image;
    setScale(1.0f);
  }

  @Override
  public void updateComponent() {
    setActualBounds(getTarget().getRect());
  }

  @Override
  public SikuliGuideComponent setScale(float scale) {
    this.scale = scale;
    if (scale == 0) {
      int x = (int) (getTarget().getCenter().x - image.getWidth()/2);
      int y = (int) (getTarget().getCenter().y - image.getHeight()/2);
      setTarget(Region.create(x, y, image.getWidth(), image.getHeight()));
    } else {
      w = (int) (scale * image.getWidth());
      h = (int) (scale * image.getHeight());
    }
    return this;
  }

  @Override
  public void paintComponent(Graphics g) {
    if (image == null) {
      return;
    }
    Graphics2D g2d = (Graphics2D) g;
    int aw = w > getActualWidth() ? getActualWidth() : w;
    int ah = h> getActualHeight() ? getActualHeight() : h;
    int ay = (int) ((getActualHeight() - ah)/2);
    g2d.drawImage(image, 0, ay, aw, ah, null);
    g2d.drawRect(0, 0, getActualWidth() - 1, getActualHeight() - 1);
  }

  public void setImage(BufferedImage image) {
    this.image = image;
  }

  public BufferedImage getImage() {
    return image;
  }
}