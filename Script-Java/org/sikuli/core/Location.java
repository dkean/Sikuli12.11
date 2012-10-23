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

  public Location(int x, int y) {
    super(x, y);
  }

  public Location(Location loc) {
    super(loc.x, loc.y);
  }

  public Location(Point point) {
    super(point);
  }

  public Location negative() {
    return new Location(-x, -y);
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

  @Override
  public String toString() {
    return "(" + x + "," + y + ")";
  }
}
