/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.core;

import java.awt.Point;

public class Location extends Point {

  /**
   *
   * @param x
   * @param y
   * @deprecated does not make sense, handle outside
   */
  @Deprecated
  public Location(float x, float y) {
    super((int) x, (int) y);
  }

  /**
	 *
	 * @param x
	 * @param y
	 */
	public Location(int x, int y) {
    super(x, y);
  }

  public Location(Location loc) {
    super(loc.x, loc.y);
  }

  public Location(Point point) {
    super(point);
  }

	public Point getPoint() {
		return new Point(this);
	}

	public Screen getScreen() {
		return Screen.getScreenContaining(this);
	}

	public Location moveFor(int X, int Y) {
		super.translate(X, Y);
		return this;
	}

	public Location moveTo(int X, int Y) {
		super.move(X, Y);
		return this;
	}

  public Location offset(int dx, int dy) {
    return new Location(x + dx, y + dy);
  }

  public Location left(int dx) {
    return new Location(x - dx, y);
  }

  public Location right(int dx) {
    return new Location(x + dx, y);
  }

  public Location above(int dy) {
    return new Location(x, y - dy);
  }

  public Location below(int dy) {
    return new Location(x, y + dy);
  }

	  /**
   * new point with same offset to current screen's top left on primary screen
   *
   * @return new location
   */
  public Location copyTo() {
    return copyTo(Screen.getPrimaryScreen());
  }

  /**
   * new point with same offset to current screen's top left on given screen
   *
   * @param scrID number of screen
   * @return new location
   */
  public Location copyTo(int scrID) {
    return copyTo(Screen.getScreen(scrID));
  }

  /**
   * new point with same offset to current screen's top left on given screen
   *
   * @param screen new parent screen
   * @return new location
   */
  public Location copyTo(Screen screen) {
    Location o = new Location(Screen.getScreenContaining(this).getBounds().getLocation());
    Location n = new Location(screen.getBounds().getLocation());
    return new Location(n.x + x - o.x, n.y + y - o.y);
  }

  /**
   * new region with same offset to current screen's top left <br />on the given
   * region's screen <br />mainly to support Jython Screen objects
   *
   * @param screen new parent screen
   * @return new region
   */
  public Location copyTo(Region reg) {
    return copyTo(reg.getScreen());
  }



  @Override
  public String toString() {
    return "(" + x + "," + y + ")";
  }
}
