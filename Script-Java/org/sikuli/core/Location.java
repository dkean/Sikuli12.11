/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.core;

import java.awt.Point;

/**
 * A point like AWT.Point using global coordinates, hence modifications might
 * move location out of any screen (not checked as is done with region)

 * @author RaiMan
 */
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

  /**
	 *
	 * @param loc
	 */
	public Location(Location loc) {
    super(loc.x, loc.y);
  }

  /**
	 *
	 * @param point
	 */
	public Location(Point point) {
    super(point);
  }

	/**
	 * to allow AWT features
	 * @return
	 */
	public Point getPoint() {
		return new Point(this);
	}

  /**
   * the offset of given point as (x,y) relative to this point
   * @param loc1
   * @param loc2
   * @return relative offset
   */
  public Location getOffset(Location loc) {
    return(new Location(loc.x - x, loc.y - y));
  }

  /**
   * the offset of second point as (x,y) relative to first point
   * @param loc1
   * @param loc2
   * @return relative offset
   */
  public static Location getOffset(Location loc1, Location loc2) {
    return(new Location(loc2.x - loc1.x, loc2.y - loc1.y));
  }

  /**
   * create a region with this point as center and the given size
   *
   * @param loc the center point
   * @param w the width
   * @param h the height
   * @return the new region
   */
  public Region grow(int w, int h) {
    return Region.create(0, 0, w, h).setCenter(this);
  }

  /**
   * create a minimal symetric region at this point as center with size 3x3
   *
   * @param loc the center point
   * @return the new region
   */
  public Region grow() {
    return grow(3, 3);
  }

  /**
   * create a region with a corner at this point<br />as specified with x
   * y<br /> 0 0 top left<br /> 0 1 bottom left<br /> 1 0 top right<br /> 1 1
   * bottom right<br />
   *
   * @param loc the refence point
   * @param x ==0 is left side !=0 is right side
   * @param y ==0 is top side !=0 is bottom side
   * @param w the width
   * @param h the height
   * @return the new region
   */
  public Region grow(int x, int y, int w, int h) {
    Region r = Region.create(0, 0, w, h);
    if (x == 0) {
      if (y == 0) {
        r.setLocation(this);
      } else {
        r.setBottomLeft(this);
      }
    } else {
      if (y == 0) {
        r.setTopRight(this);
      } else {
        r.setBottomRight(this);
      }
    }
    return r;
  }

	/**
	 * moves the point the given amounts in the x and y direction, might be negative
	 * <br />might move point outside of any screen, not checked
	 *
	 * @param dx
	 * @param dy
	 * @return the location itself modified
	 */
	public Location moveFor(int dx, int dy) {
		super.translate(dx, dy);
		return this;
	}

	/**
	 * changes the locations x and y value to the given values (moves it)
	 * <br />might move point outside of any screen, not checked
	 *
	 * @param X
	 * @param Y
	 * @return the location itself modified
	 */
	public Location moveTo(int X, int Y) {
		super.move(X, Y);
		return this;
	}

  /**
	 * creates a point at the given offset, might be negative
	 * <br />might create a point outside of any screen, not checked
	 *
	 * @param dx
	 * @param dy
	 * @return new location
	 */
	public Location offset(int dx, int dy) {
    return new Location(x + dx, y + dy);
  }

  /**
	 * creates a point at the given offset to the left, might be negative
	 * <br />might create a point outside of any screen, not checked
	 *
	 * @param dx
	 * @return new location
	 */
	public Location left(int dx) {
    return new Location(x - dx, y);
  }

  /**
	 * creates a point at the given offset to the right, might be negative
	 * <br />might create a point outside of any screen, not checked
	 *
	 * @param dx
	 * @return new location
	 */
	public Location right(int dx) {
    return new Location(x + dx, y);
  }

  /**
	 * creates a point at the given offset above, might be negative
	 * <br />might create a point outside of any screen, not checked
	 *
	 * @param dy
	 * @return new location
	 */
	public Location above(int dy) {
    return new Location(x, y - dy);
  }

  /**
	 * creates a point at the given offset below, might be negative
	 * <br />might create a point outside of any screen, not checked
	 *
	 * @param dy
	 * @return new location
	 */
	public Location below(int dy) {
    return new Location(x, y + dy);
  }

	/**
	 *
	 * @return the screen containing this point, the primary screen if outside of any screen
	 */
	public Screen getScreen() {
		return Screen.getScreenContaining(this);
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
   * new point with same offset to current screen's top left <br />on the given
   * region's screen <br />mainly to support Jython Screen objects
   *
   * @param screen new parent screen
   * @return new point
   */
  public Location copyTo(Region reg) {
    return copyTo(reg.getScreen());
  }



  @Override
  public String toString() {
    return "L(" + x + "," + y + ") on "+getScreen().toStringShort();
  }
}
