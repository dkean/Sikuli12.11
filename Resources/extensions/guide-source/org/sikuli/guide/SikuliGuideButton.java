/**
 *
 */
package org.sikuli.guide;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JLabel;
import org.sikuli.script.Region;

public class SikuliGuideButton extends SikuliGuideClickable {

  Font f = new Font("sansserif", Font.BOLD, 18);
  JLabel label;

  public SikuliGuideButton(String name) {
    super(new Region(0, 0, 0, 0));
    init(name);
  }

  private void init(String name) {
    setName(name);
    normalColor = Color.magenta;
    mouseOverColor = new Color(0.3f, 0.3f, 0.3f);
  }

  @Override
  public void setName(String name) {
    super.setName(name);
    this.label = new JLabel(name);
    label.setFont(f);
    label.setForeground(Color.white);
    add(label);
    Dimension s = label.getPreferredSize();
    label.setLocation(5, 5);
    label.setSize(s);
    s.height += 10;
    s.width += 10;
    setActualSize(s);
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    if (isMouseOver()) {
      g2d.setColor(mouseOverColor);
    } else {
      g2d.setColor(normalColor);
    }
    RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, getActualWidth() - 1, getActualHeight() - 1, 10, 10);
    g2d.fill(roundedRectangle);
    g2d.setColor(Color.white);
    g2d.draw(roundedRectangle);
    roundedRectangle = new RoundRectangle2D.Float(1, 1, getActualWidth() - 3, getActualHeight() - 3, 10, 10);
    g2d.setColor(Color.black);
    g2d.draw(roundedRectangle);
    label.paintComponents(g);
  }
}