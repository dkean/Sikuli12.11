/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified 2012 by RaiMan
 */
package org.sikuli.script;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.Properties;
import org.sikuli.system.OSUtil;

public class Settings {

  public static boolean makeJythonRegion = false;

	/**
	 * Mac: standard place for native libs
	 */
	public static String libPathMac = "/Applications/SikuliX.app/Contents/libs";
  public static String libPath = null;
  public static String libPathStd = null;
  private static final String sikhomeEnv = System.getenv("SIKULI_HOME");
  private static final String sikhomeProp = System.getProperty("sikuli.Home");
	/**
	 * Win: standard place for native libs
	 */
  public static final String libSub = slashify("SikuliX/libs", false);
	public static final String libPathWin = slashify(System.getenv("ProgramFiles"), true)+libSub;
	public static final String libPathWin32 = slashify(System.getenv("ProgramFiles(x86)"), true)+libSub;
	/**
	 * location of folder Tessdata
	 */
	public static String OcrDataPath;
	/**
	 * standard place in the net to get information about extensions<br />
	 * needs a file extensions.json with content<br />
	 * {"extension-list":<br />
   * &nbsp;{"extensions":<br />
   * &nbsp;&nbsp;[<br />
   * &nbsp;&nbsp;&nbsp;{<br />
   * &nbsp;&nbsp;&nbsp;&nbsp;"name":"SikuliGuide",<br />
   * &nbsp;&nbsp;&nbsp;&nbsp;"version":"0.3",<br />
   * &nbsp;&nbsp;&nbsp;&nbsp;"description":"visual annotations",<br />
   * &nbsp;&nbsp;&nbsp;&nbsp;"imgurl":"somewhere in the net",<br />
   * &nbsp;&nbsp;&nbsp;&nbsp;"infourl":"http://doc.sikuli.org",<br />
   * &nbsp;&nbsp;&nbsp;&nbsp;"jarurl":"---extensions---"<br />
   * &nbsp;&nbsp;&nbsp;},<br />
   * &nbsp;&nbsp;]<br />
   * &nbsp;}<br />
   * }<br />
	 * imgurl: to get an icon from<br />
	 * infourl: where to get more information<br />
	 * jarurl: where to download the jar from (no url: this standard place)<br />
	 */
  public static String SikuliRepo;
  private static String[] args = new String[0];

 	public final static String SikuliVersionDefault = "SikuliX-1.0";
  public final static int SikuliVersionBetaN = 351;
 	public static String SikuliVersionBeta = SikuliVersionDefault + "-Beta" + SikuliVersionBetaN;
	public static String SikuliVersion = "#sikuliversion#";
  public static String[] ServerList = {"https://dl.dropbox.com/u/42895525/SikuliX"};

	static {
    Properties props = System.getProperties();
    if (sikhomeProp != null) {
      libPath = slashify(sikhomeProp, true) + "libs";
    } else if (sikhomeEnv != null) {
      libPath = Settings.slashify(sikhomeEnv, true) + "libs";
    }

    if (libPath == null) {
      File wd = new File(System.getProperty("user.dir"));
      File wdp = new File(System.getProperty("user.dir")).getParentFile();
      wd = new File(slashify(wd.getAbsolutePath(), true) + libSub);
      wdp = new File(slashify(wdp.getAbsolutePath(), true) + libSub);
      if (wd.exists()) {
        libPath = wd.getAbsolutePath();
      } else if (wdp.exists()) {
        libPath = wdp.getAbsolutePath();
      }
      if (libPath == null) {
        wd = new File(slashify(System.getProperty("user.home"), true) + libSub);
        if (wd.exists()) {
          libPath = wd.getAbsolutePath();
        }
      }
    }

    if (isMac() && libPath == null) {
      if (!(new File(libPathMac)).exists()) {
        libPath = slashify("/Applications/" + libSub, true);
      }
    }

    if (isWindows() && libPath == null) {
      if ((new File(libPathWin)).exists()) {
        libPath = libPathWin;
      } else if ((new File(libPathWin32)).exists()) {
        libPath = libPathWin32;
      }
    }

    SikuliRepo = null;
		if (isWindows()) {
			OcrDataPath = libPathWin;
		} else if (isMac()) {
			OcrDataPath = libPathMac;
		} else {
			OcrDataPath = "/usr/local/share";
		}
    if (SikuliVersionBetaN > 0) {
      SikuliVersion = SikuliVersionBeta;
    } else if ("#sikuliversion#".equals(SikuliVersion)) {
      SikuliVersion = SikuliVersionDefault;
	}
	}

	public static final int ISWINDOWS = 0;
	public static final int ISMAC = 1;
	public static final int ISLINUX = 2;
	public static final int ISNOTSUPPORTED = 3;
	public static final float FOREVER = Float.POSITIVE_INFINITY;

	public static final int JavaVersion = Integer.parseInt(java.lang.System.getProperty("java.version").substring(2, 3));
	public static final String JREVersion = java.lang.System.getProperty("java.runtime.version");

	public static FindFailedResponse defaultFindFailedResponse = FindFailedResponse.ABORT;
	public static final FindFailedResponse PROMPT = FindFailedResponse.PROMPT;
	public static final FindFailedResponse RETRY = FindFailedResponse.RETRY;
	public static final FindFailedResponse SKIP = FindFailedResponse.SKIP;
	public static final FindFailedResponse ABORT = FindFailedResponse.ABORT;
	public static boolean ThrowException = true; // throw FindFailed exception
	public static float AutoWaitTimeout = 3f; // in seconds
	public static float WaitScanRate = 3f; // frames per second
	public static float ObserveScanRate = 3f; // frames per second
	public static int ObserveMinChangedPixels = 50; // in pixels
	public static double MinSimilarity = 0.7;

	public static float MoveMouseDelay = 0.5f; // in seconds
	public static double DelayBeforeDrop = 0.3;
	public static double DelayAfterDrag = 0.3;

	public static String BundlePath = null;

	/**
	 * in-jar folder to load other ressources from
	 */
	public static String jarResources = "META-INF/res/";
	/**
	 * in-jar folder to load native libs from
	 */
	public static String libSource = "META-INF/lib/";
	public static boolean OcrTextSearch = false;
	public static boolean OcrTextRead = false;

	/**
	 * true = start slow motion mode, false: stop it (default: false)
	 * show a visual for SlowMotionDelay seconds (default: 2)
	 */
	public static boolean ShowActions = false;
	public static float SlowMotionDelay = 2.0f; // in seconds

	/**
	 * true = highlight every match (default: false) (show red rectangle around)
	 * for DefaultHighlightTime seconds (default: 2)
	 */
	public static boolean Highlight = false;
	public static float DefaultHighlightTime = 2f;
  public static float WaitAfterHighlight = 0.3f;

	public static boolean ActionLogs = true;
	public static boolean InfoLogs = true;
	public static boolean DebugLogs = false;
	public static boolean ProfileLogs = false;
	public static boolean UserLogs = true;
	public static String UserLogPrefix = "user";
	public static boolean UserLogTime = true;

	/**
	 * default pixels to add around with nearby() and grow()
	 */
	public static final int DefaultPadding = 50;

	static OSUtil osUtil = null;

	public static boolean isJava7() {
		return JavaVersion > 6;
	}

  public static void showJavaInfo() {
    Debug.log(1, "Running on Java " + JavaVersion + " (" + JREVersion + ")");
  }

	public static String getFilePathSeperator() {
		return File.separator;
	}

	public static String getPathSeparator() {
		if (isWindows()) {
			return ";";
		}
		return ":";
	}

	public static String getSikuliDataPath() {
		String home, sikuliPath;
		if (isWindows()) {
			home = System.getenv("APPDATA");
			sikuliPath = "Sikuli";
		} else if (isMac()) {
			home = System.getProperty("user.home")
							+ "/Library/Application Support";
			sikuliPath = "Sikuli";
		} else {
			home = System.getProperty("user.home");
			sikuliPath = ".sikuli";
		}
		File fHome = new File(home, sikuliPath);
		return fHome.getAbsolutePath();
	}

	/**
	 * returns the absolute path to the user's extension path
	 */
	public static String getUserExtPath() {
		String ret = getSikuliDataPath() + File.separator + "extensions";
		File f = new File(ret);
		if (!f.exists()) {
			f.mkdirs();
		}
		return ret;
	}

	public static int getOS() {
		int osRet = ISNOTSUPPORTED;
		String os = System.getProperty("os.name").toLowerCase();
		if (os.startsWith("mac")) {
			osRet = ISMAC;
		} else if (os.startsWith("windows")) {
			osRet = ISWINDOWS;
		} else if (os.startsWith("linux")) {
			osRet = ISLINUX;
		}
		return osRet;
	}

	public static boolean isWindows() {
		return getOS() == ISWINDOWS;
	}

	public static boolean isLinux() {
		return getOS() == ISLINUX;
	}

	public static boolean isMac() {
		return getOS() == ISMAC;
	}

	public static String getOSVersion() {
		return System.getProperty("os.version");
	}

  public static String getVersion() {
    return SikuliVersion;
  }

	static String getOSUtilClass() {
		String pkg = "org.sikuli.system.";
		switch (getOS()) {
			case ISMAC:
				return pkg + "MacUtil";
			case ISWINDOWS:
				return pkg + "WinUtil";
			case ISLINUX:
				return pkg + "LinuxUtil";
			default:
				Debug.error("Warning: Sikuli doesn't fully support your OS.");
				return pkg + "DummyUtil";
		}
	}

	public static OSUtil getOSUtil() {
		if (osUtil == null) {
			try {
				Class c = Class.forName(getOSUtilClass());
				Constructor constr = c.getConstructor();
				osUtil = (OSUtil) constr.newInstance();
			} catch (Exception e) {
				Debug.error("Can't create OS Util: " + e.getMessage());
			}
		}
		return osUtil;
	}

  public static String slashify(String path, boolean isDirectory) {
    String p;
    if (path == null) {
      p = "";
    } else {
      p = path;
      if (File.separatorChar != '/') {
        p = p.replace(File.separatorChar, '/');
      }
      if (!p.endsWith("/") && isDirectory) {
        p = p + "/";
      }
    }
    return p;
  }

  public static void setArgs(String[] args) {
    Settings.args = args;
  }

  public static String[] getArgs() {
    return Settings.args;
  }

  public static String getTimestamp() {
    return (new Date()).getTime() + "";
}
}
