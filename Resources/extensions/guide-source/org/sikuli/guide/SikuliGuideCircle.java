/**
 *
 */
package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import org.sikuli.script.Region;

public class SikuliGuideCircle extends SikuliGuideComponent {

  public SikuliGuideCircle(Region region) {
    super();
    if (region != null) {
      Rectangle rect = region.getRect();
      rect.grow(stroke, stroke);
      setActualBounds(rect);
    }
    init();
  }

  public SikuliGuideCircle() {
    super();
    init();
  }

  private void init() {
    setColors(null, color, null, null, null);
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
    Stroke pen = new BasicStroke((float) stroke);
    g2d.setStroke(pen);
    Rectangle r = new Rectangle(getActualBounds());
    r.grow(-2, -2);
    g2d.translate(2, 2);
    Ellipse2D.Double ellipse =
            new Ellipse2D.Double(0, 0, r.width - 1, r.height - 1);
    g2d.draw(ellipse);
  }
}