/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified 2012 RaiMan
 */
package org.sikuli.script;

import java.awt.Rectangle;

public interface ScreenIF {

	public RobotIF getRobot();

	public Rectangle getBounds();

	public ScreenImage capture();

	public ScreenImage capture(int x, int y, int w, int h);

	public ScreenImage capture(Rectangle rect);

	public ScreenImage capture(Region reg);
}
