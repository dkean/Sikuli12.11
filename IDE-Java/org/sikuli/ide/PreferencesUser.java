/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.Point;
import java.util.Date;
import java.util.Locale;
import java.util.prefs.*;
import org.sikuli.utility.Debug;

public class PreferencesUser {

  final static int yes = 1;
  final static int no = 0;
  final static int AUTO_NAMING_TIMESTAMP = 0;
	final static int AUTO_NAMING_OCR = 1;
	final static int AUTO_NAMING_OFF = 2;
  final static int HORIZONTAL = 0;
  final static int VERTICAL = 1;

	static PreferencesUser _instance = null;
	Preferences pref = Preferences.userNodeForPackage(SikuliIDE.class);

	public static PreferencesUser getInstance() {
		if (_instance == null) {
			_instance = new PreferencesUser();
		}
		return _instance;
	}

	private PreferencesUser() {
		Debug.log(2, "init user preferences");
	}

	public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
		pref.addPreferenceChangeListener(pcl);
	}

// ***** capture hot key
  public void setCaptureHotkey(int hkey) {
		pref.putInt("CAPTURE_HOTKEY", hkey);
	}

	public int getCaptureHotkey() {
		return pref.getInt("CAPTURE_HOTKEY", 50); // default: '2'
	}

	public void setCaptureHotkeyModifiers(int mod) {
		pref.putInt("CAPTURE_HOTKEY_MODIFIERS", mod);
	}

	public int getCaptureHotkeyModifiers() {
		String os = System.getProperty("os.name").toLowerCase();
		int mod = Event.SHIFT_MASK + Event.META_MASK; // mac default
		if (os.startsWith("windows") || os.startsWith("linux")) {
			mod = Event.SHIFT_MASK + Event.CTRL_MASK;
		}
		return pref.getInt("CAPTURE_HOTKEY_MODIFIERS", mod);
	}

	public void setCaptureDelay(double v) {
		pref.putDouble("CAPTURE_DELAY", v);
	}

	public double getCaptureDelay() {
		return pref.getDouble("CAPTURE_DELAY", 1.0);
	}

// ***** abort key
	public void setStopHotkey(int hkey) {
		pref.putInt("STOP_HOTKEY", hkey);
	}

	public int getStopHotkey() {
		return pref.getInt("STOP_HOTKEY", 67); // default: 'c'
	}

  public void setStopHotkeyModifiers(int mod) {
		pref.putInt("STOP_HOTKEY_MODIFIERS", mod);
	}

	public int getStopHotkeyModifiers() {
		String os = System.getProperty("os.name").toLowerCase();
		int mod = Event.SHIFT_MASK + Event.META_MASK; // mac default
		if (os.startsWith("windows") || os.startsWith("linux")) {
			mod = Event.SHIFT_MASK + Event.ALT_MASK;
		}
		return pref.getInt("GET_HOTKEY_MODIFIERS", mod);
	}

// ***** indentation support
	public void setExpandTab(boolean flag) {
		pref.putBoolean("EXPAND_TAB", flag);
	}

	public boolean getExpandTab() {
		return pref.getBoolean("EXPAND_TAB", true);
	}

	public void setTabWidth(int width) {
		pref.putInt("TAB_WIDTH", width);
	}

	public int getTabWidth() {
		return pref.getInt("TAB_WIDTH", 4);
	}

// ***** font settings
	public void setFontSize(int size) {
		pref.putInt("FONT_SIZE", size);
	}

	public int getFontSize() {
		return pref.getInt("FONT_SIZE", 18);
	}

	public void setFontName(String font) {
		pref.put("FONT_NAME", font);
	}

	public String getFontName() {
		return pref.get("FONT_NAME", "Monospaced");
	}

// ***** locale support
	public void setLocale(Locale l) {
		pref.put("LOCALE", l.toString());
	}

	public Locale getLocale() {
		String locale = pref.get("LOCALE", Locale.getDefault().toString());
		String[] code = locale.split("_");
		if (code.length == 1) {
			return new Locale(code[0]);
		} else if (code.length == 2) {
			return new Locale(code[0], code[1]);
		} else {
			return new Locale(code[0], code[1], code[2]);
		}
	}

// ***** image capture and naming
	public void setAutoNamingMethod(int m) {
		pref.putInt("AUTO_NAMING", m);
	}

	public int getAutoNamingMethod() {
		return pref.getInt("AUTO_NAMING", AUTO_NAMING_OCR);
	}

	public void setDefaultThumbHeight(int h) {
		pref.putInt("DEFAULT_THUMB_HEIGHT", h);
	}

	public int getDefaultThumbHeight() {
		return pref.getInt("DEFAULT_THUMB_HEIGHT", 50);
	}

// ***** command bar
	public void setPrefMoreCommandBar(boolean flag) {
		pref.putInt("PREF_MORE_COMMAND_BAR", flag ? 1 : 0);
	}

	public boolean getPrefMoreCommandBar() {
		return pref.getInt("PREF_MORE_COMMAND_BAR", 1) != 0;
	}

	public void setAutoCaptureForCmdButtons(boolean flag) {
		pref.putInt("AUTO_CAPTURE_FOR_CMD_BUTTONS", flag ? 1 : 0);
	}

	public boolean getAutoCaptureForCmdButtons() {
		return pref.getInt("AUTO_CAPTURE_FOR_CMD_BUTTONS", 1) != 0;
	}

// ***** save options
	public void setAtSaveMakeHTML(boolean flag) {
		pref.putBoolean("AT_SAVE_MAKE_HTML", flag);
	}

	public boolean getAtSaveMakeHTML() {
		return pref.getBoolean("AT_SAVE_MAKE_HTML", false);
	}

	public void setAtSaveCleanBundle(boolean flag) {
		pref.putBoolean("AT_SAVE_CLEAN_BUNDLE", flag);
	}

	public boolean getAtSaveCleanBundle() {
		return pref.getBoolean("AT_SAVE_CLEAN_BUNDLE", true);
	}

// ***** script run options
	public void setPrefMoreRunSave(boolean flag) {
		pref.putBoolean("PREF_MORE_RUN_SAVE", flag);
	}

	public boolean getPrefMoreRunSave() {
		return pref.getBoolean("PREF_MORE_RUN_SAVE", false);
	}

	public void setPrefMoreHighlight(boolean flag) {
		pref.putBoolean("PREF_MORE_HIGHLIGHT", flag);
	}

	public boolean getPrefMoreHighlight() {
		return pref.getBoolean("PREF_MORE_HIGHLIGHT", false);
	}

// ***** auto update support
	public void setCheckUpdate(boolean flag) {
		pref.putBoolean("CHECK_UPDATE", flag);
	}

	public boolean getCheckUpdate() {
		return pref.getBoolean("CHECK_UPDATE", true);
	}

	public void setLastSeenUpdate(String ver) {
		pref.put("LAST_SEEN_UPDATE", ver);
	}

	public String getLastSeenUpdate() {
		return pref.get("LAST_SEEN_UPDATE", "0.0");
	}

  public void setCheckUpdateTime() {
		pref.putLong("LAST_CHECK_UPDATE", (new Date()).getTime());
	}

	public long getCheckUpdateTime() {
		return pref.getLong("LAST_CHECK_UPDATE", (new Date()).getTime());
	}

// ***** IDE general support
	public void setIdeSize(Dimension size) {
		String str = (int) size.getWidth() + "x" + (int) size.getHeight();
		pref.put("IDE_SIZE", str);
	}

	public Dimension getIdeSize() {
		String str = pref.get("IDE_SIZE", "1024x700");
		String[] w_h = str.split("x");
		return new Dimension(Integer.parseInt(w_h[0]), Integer.parseInt(w_h[1]));
	}

	public void setIdeLocation(Point p) {
		String str = p.x + "," + p.y;
		pref.put("IDE_LOCATION", str);
	}

	public Point getIdeLocation() {
		String str = pref.get("IDE_LOCATION", "0,0");
		String[] x_y = str.split(",");
		return new Point(Integer.parseInt(x_y[0]), Integer.parseInt(x_y[1]));
	}

  // currently: last open filenames
  public void setIdeSession(String session_str) {
		pref.put("IDE_SESSION", session_str);
	}

	public String getIdeSession() {
		return pref.get("IDE_SESSION", null);
	}

	public void setPrefMoreImages(boolean flag) {
		pref.putBoolean("PREF_MORE_IMAGES", flag);
	}

	public boolean getPrefMoreImages() {
		return pref.getBoolean("PREF_MORE_IMAGES", false);
	}

	public void setPrefMoreImagesPath(String path) {
		pref.put("PREF_MORE_IMAGES", path);
	}

	public String getPrefMoreImagesPath() {
		return pref.get("PREF_MORE_IMAGES", null);
	}

// ***** message area settings
	public void setPrefMoreMessage(int typ) {
		pref.putInt("PREF_MORE_MESSAGE", typ);
	}

	public int getPrefMoreMessage() {
		return pref.getInt("PREF_MORE_MESSAGE", HORIZONTAL);
	}

	public void setConsoleCSS(String css) {
		pref.put("CONSOLE_CSS", css);
	}

	public String getConsoleCSS() {
		return pref.get("CONSOLE_CSS",
						"body   { font-family:serif; font-size: 12px; }"
						+ ".normal{ color: black; }"
						+ ".debug { color:#505000; }"
						+ ".info  { color: blue; }"
						+ ".log   { color: #09806A; }"
						+ ".error { color: red; }");
	}

// ***** general setter getter
	public void put(String key, String val) {
		pref.put(key, val);
	}

	public String get(String key, String default_) {
		return pref.get(key, default_);
	}
}
