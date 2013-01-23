/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.awt.Window;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.JFrame;

public class OverlayTransparentWindow extends JFrame {

	static Method __setWindowOpacity = null;
	static Method __isTranslucencySupported = null;
	static boolean isInit_getMethods = false;

	public OverlayTransparentWindow() {
		setUndecorated(true);
		if (Settings.JavaVersion < 7) {
			dynGetMethod();
		}
	}

	public void _setOpacity(float alpha) {
		if (Settings.JavaVersion > 6) {
			try {
				Class<?> c = Class.forName("javax.swing.JFrame");
				Method m = c.getMethod("setOpacity", float.class);
				m.invoke(this, alpha);
			} catch (Exception e) {
				Debug.error("J7: TransparentWindow.setOpacity: did not work");
			}
		} else {
			try {
				if (__setWindowOpacity != null) {
					__setWindowOpacity.invoke(null, (Window) this, alpha);
				} else {
					Debug.error("J6: TransparentWindow.setOpacity: not initialized");
				}
			} catch (Exception e) {
				Debug.error("J6: TransparentWindow.setOpacity: did not work");
			}
		}
	}

	public void close() {
		setVisible(false);
		dispose();
	}

	private static Method dynGetMethod() {
		if (!isInit_getMethods) {
			try {
				Class<?> aUC = Class.forName("com.sun.awt.AWTUtilities");
				Class<?> aUC_TL = aUC.getClasses()[0];
				Field[] enums = aUC_TL.getFields();
				Object aUC_TL_TL = null;
				for (Field e : enums) {
					String n = e.getName();
					if ("TRANSLUCENT".equals(n)) {
						aUC_TL_TL = e.get(null);
						break;
					}
				}
				__isTranslucencySupported = aUC.getMethod("isTranslucencySupported", aUC_TL);
				if ((Boolean) __isTranslucencySupported.invoke(null, aUC_TL_TL)) {
					__setWindowOpacity = aUC.getMethod("setWindowOpacity", Window.class, float.class);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			isInit_getMethods = true;
		}
		return __setWindowOpacity;
	}
}
