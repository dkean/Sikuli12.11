/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.core;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import org.sikuli.system.App;
import org.sikuli.text.TextRecognizer;
import org.sikuli.utility.Debug;

/**
 * A Region always lies completely inside its parent screen
 *
 * @author RaiMan
 */
public class Region {

	final static float DEFAULT_HIGHLIGHT_TIME = Settings.DefaultHighlightTime;
	static final int PADDING = 50;
	private Screen scr;
	private ScreenHighlighter overlay = null;
	public int x, y, w, h;
	protected FindFailedResponse findFailedResponse =
					Settings.defaultFindFailedResponse;
	protected boolean throwException = Settings.ThrowException;
	protected double autoWaitTimeout = Settings.AutoWaitTimeout;
	protected boolean observing = false;
	protected EventManager evtMgr = null;
	protected Match lastMatch;
	protected Iterator<Match> lastMatches;

	@Override
	public String toString() {
		return String.format("R[%d,%d %dx%d]@%s E:%s, T:%.1f",
						x, y, w, h, (scr == null ? "Screen???" : scr.toStringShort()),
						throwException ? "Y" : "N", autoWaitTimeout);
	}

	//<editor-fold defaultstate="collapsed" desc="Initialization">
	private Region initialize(int X, int Y, int W, int H, Screen parentScreen) {
		x = X;
		y = Y;
		w = W;
		h = H;
		if (parentScreen != null) {
			scr = parentScreen;
		}
		initScreen();
		return this;
	}

	private void initScreen() {
		if (this instanceof Screen) {
			scr = (Screen) this;
		}
		Rectangle roi = new Rectangle(x, y, w, h);
		scr = Screen.getScreenContaining(roi);
		if (scr == null) {
			Debug.error("Region outside any screen - using primary - (x,y) set to (0,0) ---", this);
			scr = Screen.getPrimaryScreen();
			x = 0;
			y = 0;
		}
		roi = scr.getBounds().intersection(roi);
		x = (int) roi.getX();
		y = (int) roi.getY();
		w = (int) roi.getWidth();
		h = (int) roi.getHeight();
		updateSelf();
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Constructors only for Jython">
	/**
	 * Create a region with the provided coordinate / size
	 *
	 * @param X X position
	 * @param Y Y position
	 * @param W width
	 * @param H heigth
	 * @deprecated Not for use in Java. Use Region.create() instead.
	 */
	@Deprecated
	public Region(int X, int Y, int W, int H) {
		initialize(X, Y, W, H, null);
	}

	/**
	 * Create a region from a Rectangle
	 *
	 * @param r the Rectangle
	 * @deprecated Not for use in Java. Use Region.create() instead.
	 */
	@Deprecated
	public Region(Rectangle r) {
		initialize(r.x, r.y, r.width, r.height, null);
	}

	@Deprecated
	protected Region(Rectangle r, Screen parentScreen) {
		initialize(r.x, r.y, r.width, r.height, parentScreen);
	}

	/**
	 * Create a new region from another region<br />including the region's
	 * settings
	 *
	 * @param r the region
	 * @deprecated Not for use in Java. Use Region.create() instead.
	 */
	@Deprecated
	public Region(Region r) {
		autoWaitTimeout = r.autoWaitTimeout;
		findFailedResponse = r.findFailedResponse;
		throwException = r.throwException;
		initialize(r.x, r.y, r.w, r.h, r.getScreen());
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Quasi-Constructors to be used in Java">
	protected Region() {
	}

	/**
	 * Create a region with the provided top left corner and size
	 *
	 * @param X top left X position
	 * @param Y top left Y position
	 * @param W width
	 * @param H heigth
	 * @return
	 */
	public static Region create(int X, int Y, int W, int H) {
		Region reg = new Region();
		return reg.initialize(X, Y, W, H, null);
	}

	/**
	 * Create a region from a Rectangle
	 *
	 * @param r the Rectangle
	 * @return
	 */
	public static Region create(Rectangle r) {
		Region reg = new Region();
		return reg.initialize(r.x, r.y, r.width, r.height, null);
	}

	protected static Region create(Rectangle r, IScreen parentScreen) {
		Region reg = new Region();
		return reg.initialize(r.x, r.y, r.width, r.height, null);
	}

	/**
	 * Create a region from another region<br />including the region's settings
	 *
	 * @param r the region
	 * @return
	 */
	public static Region create(Region r) {
		Region reg = new Region();
		reg.autoWaitTimeout = r.autoWaitTimeout;
		reg.findFailedResponse = r.findFailedResponse;
		reg.throwException = r.throwException;
		return reg.initialize(r.x, r.y, r.w, r.h, null);
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="handle coordinates">
	/**
	 * check if current region contains given point
	 *
	 * @param point
	 * @return true/false
	 */
	public boolean contains(Location point) {
		return getRect().contains(point.x, point.y);
	}

	/**
	 * check if mouse pointer is inside current region
	 *
	 * @return true/false
	 */
	public boolean containsMouse() {
		return contains(getMouseLocation());
	}

	/**
	 * new region with same offset to current screen's top left on given screen
	 *
	 * @param scrID number of screen
	 * @return new region
	 */
	public Region copyTo(int scrID) {
		Location old = (Location) getScreen().getBounds().getLocation();
		return Screen.getScreen(scrID).newRegion(Region.create(x - old.x, y - old.y, w, h));
	}

	/**
	 * new region with same offset to current screen's top left on given screen
	 *
	 * @param screen new parent screen
	 * @return new region
	 */
	public Region copyTo(Screen screen) {
		Location old = (Location) getScreen().getBounds().getLocation();
		return screen.newRegion(Region.create(x - old.x, y - old.y, w, h));
	}

	/**
	 * used in EventManager.callChangeObserver, Finder.next to adjust region
	 * relative coordinates of matches to screen coordinates
	 *
	 * @param m
	 * @return
	 */
	protected Match toGlobalCoord(Match m) {
		m.x += x;
		m.y += y;
		return m;
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="handle Settings">
	/**
	 * true - (initial setting) should throw exception FindFailed if find
	 * unsuccessful in this region<br /> false - do not abort script on FindFailed
	 * (might leed to null pointer exceptions later)
	 *
	 * @param flag true/false
	 */
	public void setThrowException(boolean flag) {
		throwException = flag;
		if (throwException) {
			findFailedResponse = FindFailedResponse.ABORT;
		} else {
			findFailedResponse = FindFailedResponse.SKIP;
		}
	}

	/**
	 * current setting for this region (see setThrowException)
	 *
	 * @return true/false
	 */
	public boolean getThrowException() {
		return throwException;
	}

	/**
	 * the time in seconds a find operation should wait for the appearence of the
	 * target in this region<br /> initial value 3 secs
	 *
	 * @param sec
	 */
	public void setAutoWaitTimeout(double sec) {
		autoWaitTimeout = sec;
	}

	/**
	 * current setting for this region (see setAutoWaitTimeout)
	 *
	 * @return value of seconds
	 */
	public double getAutoWaitTimeout() {
		return autoWaitTimeout;
	}

	/**
	 * FindFailedResponse.<br /> ABORT - (initial value) abort script on
	 * FindFailed (= setThrowException(true) )<br /> SKIP - ignore FindFailed
	 * (same as setThrowException(false) )<br /> PROMPT - display prompt on
	 * FindFailed to let user decide how to proceed<br /> RETRY - continue to wait
	 * for appearence on FindFailed (caution: endless loop)
	 *
	 * @param response FindFailedResponse.XXX
	 */
	public void setFindFailedResponse(FindFailedResponse response) {
		findFailedResponse = response;
	}

	/**
	 *
	 * @return the current setting (see setFindFailedResponse)
	 */
	public FindFailedResponse getFindFailedResponse() {
		return findFailedResponse;
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="getters / setters / modificators">
	/**
	 *
	 * @return the Screen object containing the region
	 */
	public Screen getScreen() {
		return scr;
	}

	/**
	 *
	 * @param is the containing screen object
	 */
	protected void setScreen(Screen is) {
		scr = is;
	}

//TODO getColor
	public int getColorAt(Location loc) {
		return scr.getRobot().getColorAt(loc.x, loc.y).getRGB();
	}

	// ************************************************
	/**
	 *
	 * @return the center pixel location of the region
	 */
	public Location getCenter() {
		return new Location(x + w / 2, y + h / 2);
	}

	/**
	 * Moves the region to the area, whose center is the given location
	 *
	 * @param loc
	 * @return the region itself
	 */
	public Region setCenter(Location loc) {
		x = x + getCenter().x - loc.x;
		y = y + getCenter().y - loc.y;
		initScreen();
		return this;
	}

	/**
	 *
	 * @return top left corner Location
	 */
	public Location getTopLeft() {
		return new Location(x, y);
	}

	/**
	 * Moves the region to the area, whose top left corner is the given location
	 *
	 * @param loc
	 * @return the region itself
	 */
	public Region setTopLeft(Location loc) {
		return setLocation(loc);
	}

	/**
	 *
	 * @return top right corner Location
	 */
	public Location getTopRight() {
		return new Location(x + w, y);
	}

	/**
	 * Moves the region to the area, whose top right corner is the given location
	 *
	 * @param loc
	 * @return the region itself
	 */
	public Region setTopRight(Location loc) {
		x = x + getTopRight().x - loc.x;
		y = y + getTopRight().y - loc.y;
		initScreen();
		return this;
	}

	/**
	 *
	 * @return bottom left corner Location
	 */
	public Location getBottomLeft() {
		return new Location(x, y + h);
	}

	/**
	 * Moves the region to the area, whose bottom left corner is the given
	 * location
	 *
	 * @param loc
	 * @return the region itself
	 */
	public Region setBottomLeft(Location loc) {
		x = x + getBottomLeft().x - loc.x;
		y = y + getBottomLeft().y - loc.y;
		initScreen();
		return this;
	}

	/**
	 *
	 * @return bottom right corner Location
	 */
	public Location getBottomRight() {
		return new Location(x + w, y + h);
	}

	/**
	 * Moves the region to the area, whose bottom right corner is the given
	 * location
	 *
	 * @param loc
	 * @return the region itself
	 */
	public Region setBottomRight(Location loc) {
		x = x + getBottomRight().x - loc.x;
		y = y + getBottomRight().y - loc.y;
		initScreen();
		return this;
	}

	// ************************************************
	/**
	 *
	 * @return x of top left corner
	 * @deprecated use region.x instead
	 */
	@Deprecated
	public int getX() {
		return x;
	}

	/**
	 *
	 * @return y of top left corner
	 * @deprecated use region.y instead
	 */
	@Deprecated
	public int getY() {
		return y;
	}

	/**
	 *
	 * @return width of region
	 * @deprecated use region.w instead
	 */
	@Deprecated
	public int getW() {
		return w;
	}

	/**
	 *
	 * @return height of region
	 * @deprecated use region.w instead
	 */
	@Deprecated
	public int getH() {
		return h;
	}

	/**
	 *
	 * @param X new x position of top left corner
	 * @deprecated use region.x= instead
	 */
	@Deprecated
	public void setX(int X) {
		x = X;
	}

	/**
	 *
	 * @param Y new y position of top left corner
	 * @deprecated use region.y= instead
	 */
	@Deprecated
	public void setY(int Y) {
		y = Y;
	}

	/**
	 *
	 * @param W new width
	 * @deprecated use region.w= instead
	 */
	@Deprecated
	public void setW(int W) {
		w = W;
	}

	/**
	 *
	 * @param H new height
	 * @deprecated use region.h instead
	 */
	@Deprecated
	public void setH(int H) {
		h = H;
	}

	// ************************************************
	/**
	 *
	 * @param W new width
	 * @param H new height
	 * @return the region itself
	 */
	public Region setSize(int W, int H) {
		w = W;
		h = H;
		initScreen();
		return this;
	}

	/**
	 *
	 * @return the AWT Rectangle of the region
	 */
	public Rectangle getRect() {
		return new Rectangle(x, y, w, h);
	}

	/**
	 * set the regions position/size<br />this might move the region even to
	 * another screen
	 *
	 * @param r the AWT Rectagle to use for position/size
	 * @return the region itself
	 */
	public Region setRect(Rectangle r) {
		setRect(r.x, r.y, r.width, r.height);
		return this;
	}

	/**
	 * set the regions position/size<br />this might move the region even to
	 * another screen
	 *
	 * @param X new x of top left corner
	 * @param Y new y of top left corner
	 * @param W new width
	 * @param H new height
	 * @return the region itself
	 */
	public Region setRect(int X, int Y, int W, int H) {
		x = X;
		y = Y;
		w = W;
		h = H;
		initScreen();
		return this;
	}

	/**
	 * set the regions position/size<br />this might move the region even to
	 * another screen
	 *
	 * @param r the region to use for position/size
	 * @return the region itself
	 */
	public Region setRect(Region r) {
		setRect(r.x, r.y, r.w, r.h);
		return this;
	}

	/**
	 *
	 * @return the regions rectangle
	 * @deprecated should only be used with Screen objects (see Screen)<br />for
	 * Region objects use getRect() instead
	 */
	@Deprecated
	public Region getROI() {
		return Region.create(getScreen().getROI());
	}

	/**
	 * resets this region to the given location, size <br />sets the ROI of the
	 * containing screen to this modified region <br />this might move the region
	 * even to another screen
	 *
	 * @param X
	 * @param Y
	 * @param W
	 * @param H
	 * @deprecated should only be used with Screen objects (see Screen.setROI)<br
	 * />for Region objects use setRect() instead
	 */
	@Deprecated
	public void setROI(int X, int Y, int W, int H) {
		x = X;
		y = Y;
		w = W;
		h = H;
		initScreen();
		getScreen().setROI(X, Y, W, H);
	}

	/**
	 * resets this region to the given rectangle <br />sets the ROI of the
	 * containing screen to this modified region<br />this might move the region
	 * even to another screen
	 *
	 * @param roi
	 * @deprecated should only be used with Screen objects (see Screen.setROI)<br
	 * />for Region objects use setRect() instead
	 */
	@Deprecated
	public void setROI(Rectangle roi) {
		x = (int) roi.getX();
		y = (int) roi.getY();
		w = (int) roi.getWidth();
		h = (int) roi.getHeight();
		initScreen();
		getScreen().setROI(roi);
	}

	/**
	 * resets this region to the given region <br />sets the ROI of the containing
	 * screen to this modified region<br />this might move the region even to
	 * another screen
	 *
	 * @param reg
	 * @deprecated should only be used with Screen objects (see Screen.setROI)<br
	 * />for Region objects use setRect() instead
	 */
	@Deprecated
	public void setROI(Region reg) {
		x = reg.x;
		y = reg.y;
		w = reg.w;
		h = reg.h;
		initScreen();
		getScreen().setROI(reg);
	}

// ****************************************************
	/**
	 *
	 * @return the region itself
	 * @deprecated only for bachward compatibility
	 */
	@Deprecated
	public Region inside() {
		return this;
	}

	/**
	 * set the regions position<br />this might move the region even to another
	 * screen
	 *
	 * @param loc new top left corner
	 * @return the region itself
	 * @deprecated to be like AWT Rectangle API use setLocation()
	 */
	@Deprecated
	public Region moveTo(Location loc) {
		return setLocation(loc);
	}

	/**
	 * set the regions position<br />this might move the region even to another
	 * screen
	 *
	 * @param loc new top left corner
	 * @return the region itself
	 */
	public Region setLocation(Location loc) {
		x = loc.x;
		y = loc.y;
		initScreen();
		return this;
	}

	/**
	 * set the regions position/size<br />this might move the region even to
	 * another screen
	 *
	 * @param r
	 * @return the region itself
	 * @deprecated to be like AWT Rectangle API use setRect() instead
	 */
	@Deprecated
	public Region morphTo(Region r) {
		return setRect(r);
	}

	/**
	 * resize the region using the given padding values<br />might be negative
	 *
	 * @param l padding on left side
	 * @param r padding on right side
	 * @param t padding at top side
	 * @param b padding at bottom side
	 * @return the region itself
	 */
	public Region add(int l, int r, int t, int b) {
		x = x - l;
		y = y - t;
		w = w + l + r;
		h = h + t + b;
		initScreen();
		return this;
	}

	/**
	 * extend the region, so it contains the given region<br />but only the part
	 * inside the current screen
	 *
	 * @param r the region to include
	 * @return the region itself
	 */
	public Region add(Region r) {
		Rectangle rect = getRect();
		rect.add(r.getRect());
		setRect(rect);
		initScreen();
		return this;
	}

	/**
	 * extend the region, so it contains the given point<br />but only the part
	 * inside the current screen
	 *
	 * @param loc the point to include
	 * @return the region itself
	 */
	public Region add(Location loc) {
		Rectangle rect = getRect();
		rect.add(loc.x, loc.y);
		setRect(rect);
		initScreen();
		return this;
	}

	// ************************************************
	/**
	 * a find operation saves its match on success in the used region object<br
	 * />unchanged if not successful
	 *
	 * @return the Match object from last successful find in this region
	 */
	public Match getLastMatch() {
		return lastMatch;
	}
	//TODO do not change lastmatch on failure

	// ************************************************
	/**
	 * a findAll operation saves its matches on success in the used region
	 * object<br />unchanged if not successful
	 *
	 * @return a Match-Iterator of matches from last successful findAll in this
	 * region
	 */
	public Iterator<Match> getLastMatches() {
		return lastMatches;
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="spatial operators - new regions">
	/**
	 * check if current region contains given region
	 *
	 * @param region
	 * @return true/false
	 */
	public boolean contains(Region region) {
		return getRect().contains(region.getRect());
	}

	/**
	 * create region withsame size at top left corner offset
	 *
	 * @param loc
	 * @return the new region
	 */
	public Region offset(Location loc) {
		return Region.create(x + loc.x, y + loc.y, w, h);
	}

	/**
	 * create a region enlarged 50 pixels on each side
	 *
	 * @return the new region
	 * @deprecated to be like AWT Rectangle API use grow() instaed
	 */
	@Deprecated
	public Region nearby() {
		return grow(PADDING, PADDING);
	}

	/**
	 * create a region enlarged range pixels on each side
	 *
	 * @param range
	 * @return the new region
	 * @deprecated to be like AWT Rectangle API use grow() instaed
	 */
	@Deprecated
	public Region nearby(int range) {
		return grow(range, range);
	}

	/**
	 * create a region enlarged range pixels on each side
	 *
	 * @param range
	 * @return the new region
	 */
	public Region grow(int range) {
		return grow(range, range);
	}

	/**
	 * create a region enlarged w pixels on left and right side<br /> and h pixels
	 * at top and bottom
	 *
	 * @param w
	 * @param h
	 * @return the new region
	 */
	public Region grow(int w, int h) {
		Rectangle r = getRect();
		r.grow(w, h);
		return Region.create(r.x, r.y, r.width, r.height);
	}

	/**
	 * create a region enlarged l pixels on left and r pixels right side<br /> and
	 * t pixels at top side and b pixels at bottom side
	 *
	 * @param l
	 * @param b
	 * @param r
	 * @param t
	 * @return the new region
	 */
	public Region grow(int l, int r, int t, int b) {
		Rectangle re = getRect();
		int _x = x - l;
		int _y = y - b;
		int _w = w + l + r;
		int _h = h + t + b;
		return Region.create(_x, _y, _w, _h);
	}

	/**
	 * create a region with the given point as center and the given size
	 *
	 * @param loc the center point
	 * @param w the width
	 * @param h the height
	 * @return the new region
	 */
	public Region grow(Location loc, int w, int h) {
		return Region.create(0, 0, w, h).setCenter(loc);
	}

	/**
	 * create a region with a corner at the given point<br />as specified with x
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
	public Region grow(Location loc, int x, int y, int w, int h) {
		Region r = Region.create(0, 0, w, h);
		if (x == 0) {
			if (y == 0) {
				r.setLocation(loc);
			} else {
				r.setBottomLeft(loc);
			}
		} else {
			if (y == 0) {
				r.setTopRight(loc);
			} else {
				r.setBottomRight(loc);
			}
		}
		return r;
	}

	/**
	 * create a region right of the right side with same height<br /> the new
	 * region extends to the right screen border<br /> use grow() to include the
	 * current region
	 *
	 * @return the new region
	 */
	public Region right() {
		return right(9999999);
	}

	/**
	 * create a region right of the right side with same height and given width<br
	 * /> use grow() to include the current region
	 *
	 * @param width
	 * @return the new region
	 */
	public Region right(int width) {
		int _x = x + w;
		int _y = y;
		int _w = width;
		int _h = h;
		return Region.create(_x, _y, _w, _h);
	}

	/**
	 * create a region left of the left side with same height<br /> the new region
	 * extends to the left screen border<br /> use grow() to include the current
	 * region
	 *
	 * @return the new region
	 */
	public Region left() {
		return left(9999999);
	}

	/**
	 * create a region left of the left side with same height and given width<br
	 * /> use grow() to include the current region
	 *
	 * @param width
	 * @return the new region
	 */
	public Region left(int width) {
		Rectangle bounds = scr.getBounds();
		int _x = x - width < bounds.x ? bounds.x : x - width;
		int _y = y;
		int _w = x - _x;
		int _h = h;
		return Region.create(_x, _y, _w, _h);
	}

	/**
	 * create a region above the top side with same width<br /> the new region
	 * extends to the top screen border<br /> use grow() to include the current
	 * region
	 *
	 * @return the new region
	 */
	public Region above() {
		return above(9999999);
	}

	/**
	 * create a region above the top side with same width and given height<br />
	 * use grow() to include the current region
	 *
	 * @param height
	 * @return the new region
	 */
	public Region above(int height) {
		Rectangle bounds = getScreen().getBounds();
		int _x = x;
		int _y = y - height < bounds.y ? bounds.y : y - height;
		int _w = w;
		int _h = y - _y;
		return Region.create(_x, _y, _w, _h);
	}

	/**
	 * create a region below the bottom side with same width<br /> the new region
	 * extends to the bottom screen border<br /> use grow() to include the current
	 * region
	 *
	 * @return the new region
	 */
	public Region below() {
		return below(999999);
	}

	/**
	 * create a region below the bottom side with same width and given height<br
	 * /> use grow() to include the current region
	 *
	 * @param height
	 * @return the new region
	 */
	public Region below(int height) {
		int _x = x;
		int _y = y + h;
		int _w = w;
		int _h = height;
		return Region.create(_x, _y, _w, _h);
	}

	/**
	 * create a new region containing both regions<br /> the region is cropped to
	 * fit into the current screen<br /> like AWT Rectangle API
	 *
	 * @param ur region to unite with
	 * @return the new region
	 */
	public Region union(Region ur) {
		Rectangle r = getRect().union(ur.getRect());
		return Region.create(r.x, r.y, r.width, r.height);
	}

	/**
	 * create a region that is the intersection of the given regions
	 *
	 * @param ir the region to intersect with like AWT Rectangle API
	 * @return the new region
	 */
	public Region intersection(Region ir) {
		Rectangle r = getRect().intersection(ir.getRect());
		return Region.create(r.x, r.y, r.width, r.height);
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="highlight">
	protected void updateSelf() {
		if (overlay != null) {
			highlight(false);
			highlight(true);
		}
	}

	/**
	 * Toggle the regions Highlight visibility (currently red frame)
	 *
	 * @return the region itself
	 */
	public Region highlight() {
		if (!(scr instanceof Screen)) {
			Debug.error("highlight only works on the physical desktop screens.");
			return this;
		}
		if (overlay == null) {
			highlight(true);
		} else {
			highlight(false);
		}
		return this;
	}

	private void highlight(boolean toEnable) {
		Debug.history("toggle highlight " + toString() + ": " + toEnable);
		if (toEnable) {
			overlay = new ScreenHighlighter(scr);
			overlay.highlight(this);
		} else {
			if (overlay != null) {
				overlay.close();
				overlay = null;
			}
		}
	}

	/**
	 * show the regions Highlight for the given time in seconds (currently red
	 * frame) if 0 - use the global Settings.SlowMotionDelay
	 *
	 * @param secs time in seconds
	 * @return the region itself
	 */
	public Region highlight(float secs) {
		Debug.history("highlight " + toString() + " for " + secs + " secs");
		if (!(scr instanceof Screen)) {
			Debug.error("highlight only work on the physical desktop screens.");
			return this;
		}
		ScreenHighlighter _overlay = new ScreenHighlighter(scr);
		_overlay.highlight(this, secs);
		return this;
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="find public methods">
	//WARNING: wait(long timeout) is taken by Java Object as final
	public void wait(double timeout) {
		try {
			Thread.sleep((long) (timeout * 1000L));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * return false to skip return true to try again throw FindFailed to abort
	 */
	private <PSC> boolean handleFindFailed(PSC target) throws FindFailed {

		FindFailedResponse response;
		if (findFailedResponse == FindFailedResponse.PROMPT) {
			FindFailedDialog fd = new FindFailedDialog(target);
			fd.setVisible(true);
			response = fd.getResponse();

		} else {
			response = findFailedResponse;
		}


		if (response == FindFailedResponse.SKIP) {
			return false;
		} else if (response == FindFailedResponse.RETRY) {
			return true;
		} else if (response == FindFailedResponse.ABORT) {
			throw new FindFailed("can not find " + target + " on the screen.");
		}

		return false;
	}

	/**
	 * Match find( Pattern/String/PatternClass ) finds the given pattern on the
	 * screen and returns the best match. If AutoWaitTimeout is set, this is
	 * equivalent to wait().
	 *
	 * @param target A search criteria
	 * @return If found, the element. null otherwise
	 * @throws FindFailed if the Find operation failed
	 */
	public <PSC> Match find(final PSC target) throws FindFailed {
		if (autoWaitTimeout > 0) {
			return wait(target, autoWaitTimeout);
		}
		while (true) {
			try {
				lastMatch = doFind(target);
			} catch (Exception e) {
				throw new FindFailed(e.getMessage());
			}

			if (lastMatch != null) {
				return lastMatch;
			}

			if (!handleFindFailed(target)) {
				return null;
			}
		}
	}

	/**
	 * Iterator<Match> findAll( Pattern/String/PatternClass ) finds the given
	 * pattern on the screen and returns the best match. If AutoWaitTimeout is
	 * set, this is equivalent to wait().
	 *
	 * @param target A search criteria
	 * @return All elements matching
	 * @throws FindFailed if the Find operation failed
	 */
	public <PSC> Iterator<Match> findAll(PSC target) throws FindFailed {
		while (true) {
			try {
				if (autoWaitTimeout > 0) {
					RepeatableFindAll rf = new RepeatableFindAll(target);
					rf.repeat(autoWaitTimeout);
					lastMatches = rf.getMatches();
				} else {
					lastMatches = doFindAll(target);
				}
			} catch (Exception e) {
				throw new FindFailed(e.getMessage());
			}
			if (lastMatches != null) {
				return lastMatches;
			}
			if (!handleFindFailed(target)) {
				return null;
			}
		}
	}

	public <PSC> Match wait(PSC target) throws FindFailed {
		return wait(target, autoWaitTimeout);
	}

	/**
	 * Match wait(Pattern/String/PatternClass target, timeout-sec) waits until
	 * target appears or timeout (in second) is passed
	 *
	 * @param target A search criteria
	 * @param timeout Timeout in seconds
	 * @return All elements matching
	 * @throws FindFailed if the Find operation failed
	 */
	public <PSC> Match wait(PSC target, double timeout) throws FindFailed {

		while (true) {
			try {
				Debug.log(2, "waiting for " + target + " to appear");
				RepeatableFind rf = new RepeatableFind(target);
				rf.repeat(timeout);
				lastMatch = rf.getMatch();

			} catch (Exception e) {
				throw new FindFailed(e.getMessage());
			}

			if (lastMatch != null) {
				Debug.log(2, "" + target + " has appeared.");
				break;
			}

			Debug.log(2, "" + target + " has not appeared.");

			if (!handleFindFailed(target)) {
				return null;
			}
		}

		return lastMatch;
	}

	/**
	 * Check if target exists (with the default autoWaitTimeout)
	 *
	 * @param target A search criteria
	 * @return The element matching
	 */
	public <PSC> Match exists(PSC target) {
		return exists(target, autoWaitTimeout);
	}

	/**
	 * Check if target exists with a specified timeout
	 *
	 * @param target A search criteria
	 * @param timeout Timeout in second
	 * @return The element matching
	 */
	public <PSC> Match exists(PSC target, double timeout) {
		try {
			RepeatableFind rf = new RepeatableFind(target);
			if (rf.repeat(timeout)) {
				lastMatch = rf.getMatch();
				return lastMatch;
			}
		} catch (Exception ff) {
			// TODO: This should throw an exception since
			// it is likely caused by not able to read the input
			// image.
		}
		return null;
	}

	/**
	 * boolean waitVanish(Pattern/String/PatternClass target, timeout-sec) waits
	 * until target vanishes or timeout (in second) is passed
	 *
	 * @return true if the target vanishes, otherwise returns false.
	 */
	public <PSC> boolean waitVanish(PSC target) {
		return waitVanish(target, autoWaitTimeout);
	}

	/**
	 * boolean waitVanish(Pattern/String/PatternClass target, timeout-sec) waits
	 * until target vanishes or timeout (in second) is passed
	 *
	 * @return true if the target vanishes, otherwise returns false.
	 */
	public <PSC> boolean waitVanish(PSC target, double timeout) {
		try {
			Debug.log(2, "waiting for " + target + " to vanish");
			RepeatableVanish r = new RepeatableVanish(target);
			if (r.repeat(timeout)) {
				// target has vanished before timeout
				Debug.log(2, "" + target + " has vanished");
				return true;
			} else {
				// target has not vanished before timeout
				Debug.log(2, "" + target + " has not vanished before timeout");
				return false;
			}

		} catch (Exception e) {
			// TODO: This should throw an error (IOException caused by target
			// image not readable).
			Debug.error(e.getMessage());
		}

		return false;
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="find internal methods">
	/**
	 * Match findNow( Pattern/String/PatternClass ) finds the given pattern on the
	 * screen and returns the best match without waiting.
	 */
	private <PSC> Match doFind(PSC ptn) throws IOException {
		ScreenImage simg = getScreen().capture(x, y, w, h);
		scr.setLastScreenImage(simg);
		Finder f = new Finder(simg, this);
		f.find(ptn);
		if (f.hasNext()) {
			return f.next();
		}
		return null;
	}

	/**
	 *
	 * @param ptn
	 * @return the match if successful
	 * @throws FindFailed
	 * @deprecated should not be used anymore - use find() instead
	 */
	@Deprecated
	public <PSC> Match findNow(PSC ptn) throws FindFailed {
		Debug.log("capture: " + x + "," + y);
		ScreenImage simg = scr.capture(x, y, w, h);
		Debug.log("ScreenImage: " + simg.getROI());
		scr.setLastScreenImage(simg);
		Finder f = new Finder(simg, this);
		Match ret = null;
		try {
			f.find(ptn);
			if (f.hasNext()) {
				ret = f.next();
			}
			f.destroy();
		} catch (IOException e) {
			throw new FindFailed(e.getMessage());
		}
		return ret;
	}

	/**
	 * Match findAllNow( Pattern/String/PatternClass ) finds the given pattern on
	 * the screen and returns the best match without waiting.
	 */
	private <PSC> Iterator<Match> doFindAll(PSC ptn) throws IOException {
		ScreenImage simg = getScreen().capture(x, y, w, h);
		scr.setLastScreenImage(simg);
		Finder f = new Finder(simg, this);
		f.findAll(ptn);
		if (f.hasNext()) {
			return f;
		}
		return null;
	}

	/**
	 *
	 * @param ptn
	 * @return the itreator of matches if successful
	 * @throws FindFailed
	 * @deprecated should not be used anymore - use findAll() instead
	 */
	@Deprecated
	public <PSC> Iterator<Match> findAllNow(PSC ptn)
					throws FindFailed {
		ScreenImage simg = scr.capture(x, y, w, h);
		scr.setLastScreenImage(simg);
		Finder f = new Finder(simg, this);
		try {
			f.findAll(ptn);
			if (f.hasNext()) {
				return f;
			}
			f.destroy();
		} catch (IOException e) {
			throw new FindFailed(e.getMessage());
		}
		return null;
	}

	/**
	 *
	 * @param target
	 * @param timeout
	 * @return the itreator of matches if successful
	 * @throws FindFailed
	 * @deprecated does not really make sense - use findAll() instead
	 */
	@Deprecated
	public <PSC> Iterator<Match> waitAll(PSC target, double timeout)
					throws FindFailed {

		while (true) {
			try {

				RepeatableFindAll rf = new RepeatableFindAll(target);
				rf.repeat(timeout);
				lastMatches = rf.getMatches();

			} catch (Exception e) {
				throw new FindFailed(e.getMessage());
			}

			if (lastMatches != null) {
				break;
			}

			if (!handleFindFailed(target)) {
				return null;
			}
		}

		return lastMatches;
	}

//TODO getRegionFromPSRM, getLocationFromPSRML
	private <PSRM> Region getRegionFromPSRM(PSRM target)
					throws FindFailed {
		if (target instanceof Pattern || target instanceof String) {
			Match m = find(target);
			if (m != null) {
				return m;
			}
			return null;
		}
		if (target instanceof Region) {
			return (Region) target;
		}
		return null;
	}

	private <PSRML> Location getLocationFromPSRML(PSRML target)
					throws FindFailed {
		if (target instanceof Pattern || target instanceof String) {
			Match m = find(target);
			if (m != null) {
				return m.getTarget();
			}
			return null;
		}
		if (target instanceof Match) {
			return ((Match) target).getTarget();
		}
		if (target instanceof Region) {
			return ((Region) target).getCenter();
		}
		if (target instanceof Location) {
			return (Location) target;
		}
		return null;
	}

	// Repeatable Find ////////////////////////////////
	private abstract class Repeatable {

		abstract void run() throws Exception;

		abstract boolean ifSuccessful();

		// return TRUE if successful before timeout
		// return FALSE if otherwise
		// throws Exception if any unexpected error occurs
		boolean repeat(double timeout) throws Exception {

			int MaxTimePerScan = (int) (1000.0 / Settings.WaitScanRate);
			long begin_t = (new Date()).getTime();
			do {
				long before_find = (new Date()).getTime();

				run();
				if (ifSuccessful()) {
					return true;
				} else if (timeout == 0) {
					return false;
				}

				long after_find = (new Date()).getTime();
				if (after_find - before_find < MaxTimePerScan) {
					scr.getRobot().delay((int) (MaxTimePerScan - (after_find - before_find)));
				} else {
					scr.getRobot().delay(10);
				}
			} while (begin_t + timeout * 1000 > (new Date()).getTime());

			return false;
		}
	}

	private class RepeatableFind extends Repeatable {

		Object _target;
		Match _match = null;

		public <PSC> RepeatableFind(PSC target) {
			_target = target;
		}

		public Match getMatch() {
			return _match;
		}

		@Override
		public void run() throws IOException {
			_match = doFind(_target);
		}

		@Override
		boolean ifSuccessful() {
			return _match != null;
		}
	}

	private class RepeatableVanish extends RepeatableFind {

		public <PSC> RepeatableVanish(PSC target) {
			super(target);
		}

		@Override
		boolean ifSuccessful() {
			return _match == null;
		}
	}

	private class RepeatableFindAll extends Repeatable {

		Object _target;
		Iterator<Match> _matches = null;

		public <PSC> RepeatableFindAll(PSC target) {
			_target = target;
		}

		public Iterator<Match> getMatches() {
			return _matches;
		}

		@Override
		public void run() throws IOException {
			_matches = doFindAll(_target);
		}

		@Override
		boolean ifSuccessful() {
			return _matches != null;
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Observer">
	private EventManager getEventManager() {
		if (evtMgr == null) {
			evtMgr = new EventManager(this);
		}
		return evtMgr;
	}

	public <PSC> void onAppear(PSC target, SikuliEventObserver observer) {
		getEventManager().addAppearObserver(target, observer);
	}

	public <PSC> void onVanish(PSC target, SikuliEventObserver observer) {
		getEventManager().addVanishObserver(target, observer);
	}

	public void onChange(int threshold, SikuliEventObserver observer) {
		getEventManager().addChangeObserver(threshold, observer);
	}

	public void onChange(SikuliEventObserver observer) {
		getEventManager().addChangeObserver(Settings.ObserveMinChangedPixels,
						observer);
	}

	public void observe() {
		observe(Float.POSITIVE_INFINITY);
	}

	public void observeInBackground(final double secs) {
		Thread th = new Thread() {
			@Override
			public void run() {
				observe(secs);
			}
		};
		th.start();
	}

	public void stopObserver() {
		observing = false;
	}

	public void observe(double secs) {
		if (evtMgr == null) {
			return;
		}
		int MaxTimePerScan = (int) (1000.0 / Settings.ObserveScanRate);
		long begin_t = (new Date()).getTime();
		observing = true;
		while (observing && begin_t + secs * 1000 > (new Date()).getTime()) {
			long before_find = (new Date()).getTime();
			ScreenImage simg = scr.capture(x, y, w, h);
			scr.setLastScreenImage(simg);
			evtMgr.update(simg);
			long after_find = (new Date()).getTime();
			try {
				if (after_find - before_find < MaxTimePerScan) {
					Thread.sleep((int) (MaxTimePerScan - (after_find - before_find)));
				}
			} catch (Exception e) {
			}
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Mouse actions - clicking">
	/**
	 * move the mouse pointer to the region's last successful match same as
	 * mouseMove<br />
	 *
	 * @return 1 if possible, 0 otherwise
	 */
	public int hover() {
		if (lastMatch != null) {
			try {
				return hover(lastMatch);
			} catch (FindFailed ex) {
				return 0;
			}
		} else {
			return 0;
		}
	}

	/**
	 * move the mouse pointer to the given target location<br /> same as
	 * mouseMove<br /> Pattern or Filename - do a find before and use the match<br
	 * /> Region - position at center<br /> Match - position at match's
	 * targetOffset<br /> Location - position at that point<br />
	 *
	 * @param <PatternFilenameRegionMatchLocation> target
	 * @return 1 if possible, 0 otherwise
	 * @throws FindFailed for Pattern or Filename
	 */
	public <PatternFilenameRegionMatchLocation> int hover(PatternFilenameRegionMatchLocation target)
					throws FindFailed {
		return mouseMove(target);
	}

	/**
	 * left click at the region's last successful match
	 *
	 * @return 1 if possible, 0 otherwise
	 */
	public int click() {
		if (lastMatch != null) {
			try {
				return click(lastMatch, 0);
			} catch (FindFailed ex) {
				return 0;
			}
		} else {
			return 0;
		}
	}

	/**
	 * left click at the given target location<br /> Pattern or Filename - do a
	 * find before and use the match<br /> Region - position at center<br /> Match
	 * - position at match's targetOffset<br /> Location - position at that
	 * point<br />
	 *
	 * @param <PatternFilenameRegionMatchLocation> target
	 * @return 1 if possible, 0 otherwise
	 * @throws FindFailed for Pattern or Filename
	 */
	public <PatternFilenameRegionMatchLocation> int click(PatternFilenameRegionMatchLocation target)
					throws FindFailed {
		return click(target, 0);
	}

	/**
	 * left click at the given target location<br /> holding down the given
	 * modifier keys<br /> Pattern or Filename - do a find before and use the
	 * match<br /> Region - position at center<br /> Match - position at match's
	 * targetOffset<br /> Location - position at that point<br />
	 *
	 * @param <PatternFilenameRegionMatchLocation> target
	 * @param modifiers the value of the resulting bitmask (see KeyModifier)
	 * @return 1 if possible, 0 otherwise
	 * @throws FindFailed for Pattern or Filename
	 */
	public <PatternFilenameRegionMatchLocation> int click(PatternFilenameRegionMatchLocation target, int modifiers)
					throws FindFailed {
		Location loc = getLocationFromPSRML(target);
		int ret = _click(loc, InputEvent.BUTTON1_MASK, modifiers, false);

		//TODO      SikuliActionManager.getInstance().clickTarget(this, target, _lastScreenImage, _lastMatch);
		return ret;
	}

	/**
	 * double click at the region's last successful match
	 *
	 * @return 1 if possible, 0 otherwise
	 */
	public int doubleClick() {
		if (lastMatch != null) {
			try {
				return doubleClick(lastMatch, 0);
			} catch (FindFailed ex) {
				return 0;
			}
		} else {
			return 0;
		}
	}

	/**
	 * double click at the given target location<br /> Pattern or Filename - do a
	 * find before and use the match<br /> Region - position at center<br /> Match
	 * - position at match's targetOffset<br /> Location - position at that
	 * point<br />
	 *
	 * @param <PatternFilenameRegionMatchLocation> target
	 * @return 1 if possible, 0 otherwise
	 * @throws FindFailed for Pattern or Filename
	 */
	public <PatternFilenameRegionMatchLocation> int doubleClick(PatternFilenameRegionMatchLocation target)
					throws FindFailed {
		return doubleClick(target, 0);
	}

	/**
	 * double click at the given target location<br /> holding down the given
	 * modifier keys<br /> Pattern or Filename - do a find before and use the
	 * match<br /> Region - position at center<br /> Match - position at match's
	 * targetOffset<br /> Location - position at that point<br />
	 *
	 * @param <PatternFilenameRegionMatchLocation> target
	 * @param modifiers the value of the resulting bitmask (see KeyModifier)
	 * @return 1 if possible, 0 otherwise
	 * @throws FindFailed for Pattern or Filename
	 */
	public <PatternFilenameRegionMatchLocation> int doubleClick(PatternFilenameRegionMatchLocation target, int modifiers)
					throws FindFailed {
		Location loc = getLocationFromPSRML(target);
		int ret = _click(loc, InputEvent.BUTTON1_MASK, modifiers, true);

		//TODO      SikuliActionManager.getInstance().doubleClickTarget(this, target, _lastScreenImage, _lastMatch);
		return ret;
	}

	/**
	 * right click at the region's last successful match
	 *
	 * @return 1 if possible, 0 otherwise
	 */
	public int rightClick() {
		if (lastMatch != null) {
			try {
				return rightClick(lastMatch, 0);
			} catch (FindFailed ex) {
				return 0;
			}
		} else {
			return 0;
		}
	}

	/**
	 * right click at the given target location<br /> Pattern or Filename - do a
	 * find before and use the match<br /> Region - position at center<br /> Match
	 * - position at match's targetOffset<br /> Location - position at that
	 * point<br />
	 *
	 * @param <PatternFilenameRegionMatchLocation> target
	 * @return 1 if possible, 0 otherwise
	 * @throws FindFailed for Pattern or Filename
	 */
	public <PatternFilenameRegionMatchLocation> int rightClick(PatternFilenameRegionMatchLocation target)
					throws FindFailed {
		return rightClick(target, 0);
	}

	/**
	 * right click at the given target location<br /> holding down the given
	 * modifier keys<br /> Pattern or Filename - do a find before and use the
	 * match<br /> Region - position at center<br /> Match - position at match's
	 * targetOffset<br /> Location - position at that point<br />
	 *
	 * @param <PatternFilenameRegionMatchLocation> target
	 * @param modifiers the value of the resulting bitmask (see KeyModifier)
	 * @return 1 if possible, 0 otherwise
	 * @throws FindFailed for Pattern or Filename
	 */
	public <PatternFilenameRegionMatchLocation> int rightClick(PatternFilenameRegionMatchLocation target, int modifiers)
					throws FindFailed {
		Location loc = getLocationFromPSRML(target);
		int ret = _click(loc, InputEvent.BUTTON3_MASK, modifiers, false);

		//TODO      SikuliActionManager.getInstance().rightClickTarget(this, target, _lastScreenImage, _lastMatch);
		return ret;
	}

	private int _click(Location loc, int buttons, int modifiers,
					boolean dblClick) {
		if (loc == null) {
			return 0;
		}
		Debug.history(getClickMsg(loc, buttons, modifiers, dblClick));
		scr.showTarget(loc);
		scr.getRobot().pressModifiers(modifiers);
		scr.getRobot().smoothMove(loc);
		scr.getRobot().mouseDown(buttons);
		scr.getRobot().mouseUp(buttons);
		if (dblClick) {
			scr.getRobot().mouseDown(buttons);
			scr.getRobot().mouseUp(buttons);
		}
		scr.getRobot().releaseModifiers(modifiers);
		scr.getRobot().waitForIdle();
		return 1;
	}

	private String getClickMsg(Location loc, int buttons, int modifiers,
					boolean dblClick) {
		String msg = "";
		if (modifiers != 0) {
			msg += KeyEvent.getKeyModifiersText(modifiers) + "+";
		}
		if (buttons == InputEvent.BUTTON1_MASK && !dblClick) {
			msg += "CLICK";
		}
		if (buttons == InputEvent.BUTTON1_MASK && dblClick) {
			msg += "DOUBLE CLICK";
		}
		if (buttons == InputEvent.BUTTON3_MASK) {
			msg += "RIGHT CLICK";
		} else if (buttons == InputEvent.BUTTON2_MASK) {
			msg += "MID CLICK";
		}
		msg += " on " + loc;
		return msg;
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Mouse actions - drag & drop">
	/**
	 * Drag from region's last match and drop at given target <br />applying
	 * Settings.DelayAfterDrag and DelayBeforeDrop <br /> using left mouse button
	 *
	 * @param <PatternFilenameRegionMatchLocation> target destination position
	 * @return 1 if possible, 0 otherwise
	 * @throws FindFailed if the Find operation failed
	 */
	public <PatternFilenameRegionMatchLocation> int dragDrop(PatternFilenameRegionMatchLocation target)
					throws FindFailed {
		return dragDrop(lastMatch, target);
	}

	/**
	 * Drag from a position and drop to another using left mouse button<br
	 * />applying Settings.DelayAfterDrag and DelayBeforeDrop
	 *
	 * @param <PatternFilenameRegionMatchLocation> t1 source position
	 * @param <PatternFilenameRegionMatchLocation> t2 destination position
	 * @return 1 if possible, 0 otherwise
	 * @throws FindFailed if the Find operation failed
	 */
	public <PatternFilenameRegionMatchLocation> int dragDrop(PatternFilenameRegionMatchLocation t1,
					PatternFilenameRegionMatchLocation t2)
					throws FindFailed {
		Location loc1 = getLocationFromPSRML(t1);
		Location loc2 = getLocationFromPSRML(t2);
		if (loc1 != null && loc2 != null) {
			scr.showTarget(loc1);
			scr.getRobot().smoothMove(loc1);
			scr.getRobot().mouseDown(InputEvent.BUTTON1_MASK);
			scr.getRobot().delay((int) (Settings.DelayAfterDrag * 1000));
			scr.showTarget(loc2);
			scr.getRobot().smoothMove(loc2);
			scr.getRobot().delay((int) (Settings.DelayBeforeDrop * 1000));
			scr.getRobot().mouseUp(InputEvent.BUTTON1_MASK);
			return 1;
		}
		return 0;
	}

	/**
	 * Prepare a drag action: move mouse to given target <br />press and hold left
	 * mouse button <br />wait Settings.DelayAfterDrag
	 *
	 * @param <PatternFilenameRegionMatchLocation> target
	 * @return 1 if possible, 0 otherwise
	 * @throws FindFailed
	 */
	public <PatternFilenameRegionMatchLocation> int drag(PatternFilenameRegionMatchLocation target)
					throws FindFailed {
		Location loc = getLocationFromPSRML(target);
		if (loc != null) {
			scr.showTarget(loc);
			scr.getRobot().smoothMove(loc);
			scr.getRobot().mouseDown(InputEvent.BUTTON1_MASK);
			scr.getRobot().delay((int) (Settings.DelayAfterDrag * 1000));
			scr.getRobot().waitForIdle();
			return 1;
		}
		return 0;
	}

	/**
	 * finalize a drag action with a drop: move mouse to given target <br />wait
	 * Settings.DelayBeforeDrop <br />release the left mouse button
	 *
	 * @param <PatternFilenameRegionMatchLocation> target
	 * @return 1 if possible, 0 otherwise
	 * @throws FindFailed
	 */
	public <PatternFilenameRegionMatchLocation> int dropAt(PatternFilenameRegionMatchLocation target)
					throws FindFailed {
		Location loc = getLocationFromPSRML(target);
		if (loc != null) {
			scr.showTarget(loc);
			scr.getRobot().smoothMove(loc);
			scr.getRobot().delay((int) (Settings.DelayBeforeDrop * 1000));
			scr.getRobot().mouseUp(InputEvent.BUTTON1_MASK);
			scr.getRobot().waitForIdle();
			return 1;
		}
		return 0;
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Mouse actions - low level + Wheel">
	/**
	 * press and hold the specified buttons - use + to combine Button.LEFT left
	 * mouse button Button.MIDDLE middle mouse button Button.RIGHT right mouse
	 * button
	 *
	 * @param buttons
	 */
	public void mouseDown(int buttons) {
		scr.getRobot().mouseDown(buttons);
	}

	/**
	 * release all currently held buttons
	 */
	public void mouseUp() {
		mouseUp(0);
	}

	/**
	 * release the specified mouse buttons (see mouseDown) if buttons==0, all
	 * currently held buttons are released
	 *
	 * @param buttons
	 */
	public void mouseUp(int buttons) {
		scr.getRobot().mouseUp(buttons);
	}

	/**
	 * move the mouse pointer to the region's last successful match<br />same as
	 * hover<br />
	 *
	 * @return 1 if possible, 0 otherwise
	 */
	public int mouseMove() {
		if (lastMatch != null) {
			try {
				return mouseMove(lastMatch);
			} catch (FindFailed ex) {
				return 0;
			}
		} else {
			return 0;
		}
	}

	/**
	 * move the mouse pointer to the given target location<br /> same as hover<br
	 * /> Pattern or Filename - do a find before and use the match<br /> Region -
	 * position at center<br /> Match - position at match's targetOffset<br />
	 * Location - position at that point<br />
	 *
	 * @param <PatternFilenameRegionMatchLocation> target
	 * @return 1 if possible, 0 otherwise
	 * @throws FindFailed for Pattern or Filename
	 */
	public <PatternFilenameRegionMatchLocation> int mouseMove(PatternFilenameRegionMatchLocation target)
					throws FindFailed {
		Location loc = getLocationFromPSRML(target);
		if (loc != null) {
			scr.showTarget(loc);
			scr.getRobot().smoothMove(loc);
			scr.getRobot().waitForIdle();
			return 1;
		}
		return 0;
	}

	/**
	 * Move the wheel at the current mouse position<br /> the given steps in the
	 * given direction: <br />Button.WHEEL_DOWN, Button.WHEEL_UP
	 *
	 * @param direction to move the wheel
	 * @param steps the number of steps
	 * @return 1 in any case
	 */
	public int wheel(int direction, int steps) {
		for (int i = 0; i < steps; i++) {
			scr.getRobot().mouseWheel(direction);
			scr.getRobot().delay(50);
		}
		return 1;
	}

	/**
	 * move the mouse pointer to the given target location<br /> and move the
	 * wheel the given steps in the given direction: <br />Button.WHEEL_DOWN,
	 * Button.WHEEL_UP
	 *
	 * @param <PatternFilenameRegionMatchLocation> target
	 * @param direction to move the wheel
	 * @param steps the number of steps
	 * @return 1 if possible, 0 otherwise
	 * @throws FindFailed if the Find operation failed
	 */
	public <PatternFilenameRegionMatchLocation> int wheel(PatternFilenameRegionMatchLocation target, int direction, int steps)
					throws FindFailed {
		if (target == null || mouseMove(target) != 0) {
			return wheel(direction, steps);
		}
		return 0;
	}

	/**
	 *
	 * @return the current mouse pointer Location
	 * @throws HeadlessException
	 */
	public static Location getMouseLocation() throws HeadlessException {
		Point loc = MouseInfo.getPointerInfo().getLocation();
		return new Location(loc.x, loc.y);
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Keyboard actions + paste">
	/**
	 * press and hold the given key use a constant from java.awt.event.KeyEvent
	 * which might be special in the current machine/system environment
	 *
	 * @param keycode
	 */
	public void keyDown(int keycode) {
		scr.getRobot().keyDown(keycode);
	}

	/**
	 * press and hold the given keys including modifier keys <br />use the key
	 * constants defined in class Key, <br />which only provides a subset of a
	 * US-QWERTY PC keyboard layout <br />might be mixed with simple characters
	 * <br />use + to concatenate Key constants
	 *
	 * @param keys
	 */
	public void keyDown(String keys) {
		scr.getRobot().keyDown(keys);
	}

	/**
	 * release all currently pressed keys
	 */
	public void keyUp() {
		keyUp("");
	}

	/**
	 * release the given keys (see keyDown(keycode) )
	 *
	 * @param keycode
	 */
	public void keyUp(int keycode) {
		scr.getRobot().keyUp(keycode);
	}

	/**
	 * release the given keys (see keyDown(keys) )
	 *
	 * @param keys
	 */
	public void keyUp(String keys) {
		scr.getRobot().keyUp(keys);
	}

	/**
	 * enters the given text one character/key after another using keyDown/keyUp
	 * <br />about the usable Key constants see keyDown(keys) <br />Class Key only
	 * provides a subset of a US-QWERTY PC keyboard layout<br />the text is
	 * entered at the current position of the focus/carret
	 *
	 * @param text containing characters and/or Key constants
	 * @return 1 if possible, 0 otherwise
	 */
	public int type(String text) {
		try {
			return keyin(null, text, 0);
		} catch (FindFailed ex) {
			return 0;
		}
	}

	/**
	 * enters the given text one character/key after another using
	 * keyDown/keyUp<br />while holding down the given modifier keys <br />about
	 * the usable Key constants see keyDown(keys) <br />Class Key only provides a
	 * subset of a US-QWERTY PC keyboard layout<br />the text is entered at the
	 * current position of the focus/carret
	 *
	 * @param text containing characters and/or Key constants
	 * @param modifiers constants according to class KeyModifiers
	 * @return 1 if possible, 0 otherwise
	 */
	public int type(String text, int modifiers) {
		try {
			return keyin(null, text, modifiers);
		} catch (FindFailed findFailed) {
			return 0;
		}
	}

	/**
	 * enters the given text one character/key after another using
	 * keyDown/keyUp<br />while holding down the given modifier keys <br />about
	 * the usable Key constants see keyDown(keys) <br />Class Key only provides a
	 * subset of a US-QWERTY PC keyboard layout<br />the text is entered at the
	 * current position of the focus/carret
	 *
	 * @param text containing characters and/or Key constants
	 * @param modifiers constants according to class Key - combine using +
	 * @return 1 if possible, 0 otherwise
	 */
	public int type(String text, String modifiers) {
		int modifiersNew = Key.convertModifiers(modifiers);
		try {
			return keyin(null, text, modifiersNew);
		} catch (FindFailed findFailed) {
			return 0;
		}
	}

	/**
	 * first does a click(target) at the given target position to gain
	 * focus/carret <br />enters the given text one character/key after another
	 * using keyDown/keyUp <br />about the usable Key constants see keyDown(keys)
	 * <br />Class Key only provides a subset of a US-QWERTY PC keyboard layout
	 *
	 * @param <PatternFilenameRegionMatchLocation> target
	 * @param text containing characters and/or Key constants
	 * @return 1 if possible, 0 otherwise
	 * @throws FindFailed
	 */
	public <PatternFilenameRegionMatchLocation> int type(PatternFilenameRegionMatchLocation target, String text)
					throws FindFailed {
		return keyin(target, text, 0);
	}

	/**
	 * first does a click(target) at the given target position to gain
	 * focus/carret <br />enters the given text one character/key after another
	 * using keyDown/keyUp <br />while holding down the given modifier keys<br
	 * />about the usable Key constants see keyDown(keys) <br />Class Key only
	 * provides a subset of a US-QWERTY PC keyboard layout
	 *
	 * @param <PatternFilenameRegionMatchLocation> target
	 * @param text containing characters and/or Key constants
	 * @param modifiers constants according to class KeyModifiers
	 * @return 1 if possible, 0 otherwise
	 */
	public <PatternFilenameRegionMatchLocation> int type(PatternFilenameRegionMatchLocation target, String text, int modifiers)
					throws FindFailed {
		return keyin(target, text, modifiers);
	}

	/**
	 * first does a click(target) at the given target position to gain
	 * focus/carret <br />enters the given text one character/key after another
	 * using keyDown/keyUp <br />while holding down the given modifier keys<br
	 * />about the usable Key constants see keyDown(keys) <br />Class Key only
	 * provides a subset of a US-QWERTY PC keyboard layout
	 *
	 * @param <PatternFilenameRegionMatchLocation> target
	 * @param text containing characters and/or Key constants
	 * @param modifiers constants according to class Key - combine using +
	 * @return 1 if possible, 0 otherwise
	 */
	public <PatternFilenameRegionMatchLocation> int type(PatternFilenameRegionMatchLocation target, String text, String modifiers)
					throws FindFailed {
		int modifiersNew = Key.convertModifiers(modifiers);
		return keyin(target, text, modifiersNew);
	}

	public <PatternFilenameRegionMatchLocation> int keyin(PatternFilenameRegionMatchLocation target, String text, int modifiers)
					throws FindFailed {
		if (0 == click(target, 0)) {
			return 0;
		}
		if (text != null && !"".equals(text)) {
			Debug.history(
							(modifiers != 0 ? KeyEvent.getKeyModifiersText(modifiers) + "+" : "")
							+ "TYPE \"" + text + "\"");
			for (int i = 0; i < text.length(); i++) {
				scr.getRobot().pressModifiers(modifiers);
				scr.getRobot().typeChar(text.charAt(i), IRobot.KeyMode.PRESS_RELEASE);
				scr.getRobot().releaseModifiers(modifiers);
				scr.getRobot().delay(20);
			}
			scr.getRobot().waitForIdle();
			return 1;
		}
		return 0;
	}

	/**
	 * pastes the text at the current position of the focus/carret <br />using the
	 * clipboard and strg/ctrl/cmd-v (paste keyboard shortcut)
	 *
	 * @param text a string, which might contain unicode characters
	 * @return 0 if possible, 1 otherwise
	 */
	public int paste(String text) {
		try {
			return paste(null, text);
		} catch (FindFailed ex) {
			return 1;
		}
	}

	/**
	 * first does a click(target) at the given target position to gain
	 * focus/carret <br /> and then pastes the text <br /> using the clipboard and
	 * strg/ctrl/cmd-v (paste keyboard shortcut)
	 *
	 * @param <PatternFilenameRegionMatchLocation> target
	 * @param text a string, which might contain unicode characters
	 * @return 0 if possible, 1 otherwise
	 * @throws FindFailed
	 */
	public <PatternFilenameRegionMatchLocation> int paste(PatternFilenameRegionMatchLocation target, String text)
					throws FindFailed {
		click(target, 0);
		if (text != null) {
			App.setClipboard(text);
			int mod = Key.getHotkeyModifier();
			scr.getRobot().keyDown(mod);
			scr.getRobot().keyDown(KeyEvent.VK_V);
			scr.getRobot().keyUp(KeyEvent.VK_V);
			scr.getRobot().keyUp(mod);
			return 1;
		}
		return 0;
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="TODO OCR">
	public String text() {
		ScreenImage simg = getScreen().capture(x, y, w, h);
		scr.setLastScreenImage(simg);
		return TextRecognizer.getInstance().recognize(simg);
	}

	public List<Match> listText() {
		ScreenImage simg = getScreen().capture(x, y, w, h);
		return TextRecognizer.getInstance().listText(simg, this);
	}
	//</editor-fold>
}
