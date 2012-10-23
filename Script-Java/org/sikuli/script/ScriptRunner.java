/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.script;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import javax.swing.*;
import org.apache.commons.cli.CommandLine;
import org.python.util.PythonInterpreter;
import org.sikuli.core.ImageLocator;
import org.sikuli.core.ScreenHighlighter;
import org.sikuli.utility.CommandArgs;
import org.sikuli.utility.Debug;
import org.sikuli.utility.Util;

public class ScriptRunner {

	private static ScriptRunner instance = null;
	private static PythonInterpreter interpreter = null;
	private String _runType;
	private static ArrayList<String> sysargv = null;
	private static ArrayList<String> syspath = null;
	private ArrayList<String> headers;
	private PythonInterpreter py;
	private Iterator<String> it;

	public static PythonInterpreter getPythonInterpreter() {
		if (interpreter == null) {
			PythonInterpreter.initialize(System.getProperties(), null, null);
			interpreter = new PythonInterpreter();
		}
		return interpreter;
	}

	public static ScriptRunner getInstance(String[] args) {
		if (instance == null) {
			instance = new ScriptRunner(args);
		}
		return instance;
	}

	public ScriptRunner(String[] args) {
		init(args, "OTHER");
	}

	public ScriptRunner(String[] args, String runType) {
		init(args, runType);
	}

	private void init(String[] args, String runType) {
		_runType = runType;
		if (interpreter == null) {
			PythonInterpreter.initialize(System.getProperties(), null, null);
			interpreter = new PythonInterpreter();
		}

		py = new PythonInterpreter();
		if (sysargv == null) {
			sysargv = new ArrayList<String>(Arrays.asList(args));
		}
/*
		if (syspath == null) {
			syspath = new ArrayList<String>(py.getSystemState().path);
		}
*/

		String[] h = new String[]{
			"# coding=utf-8",
			"import sys",
			"#for e in sys.path: print e",
			"from __future__ import with_statement",
			"from sikuli import *",
			"setThrowException(True)",
			"setShowActions(False)"
		};
		headers = new ArrayList<String>(Arrays.asList(h));
		it = headers.iterator();
		while (it.hasNext()) {
			String line = it.next();
			py.exec(line);
		}
		headers.clear();
	}

	public void runSlowMotion() {
		addTempHeader("setShowActions(True)");
	}

	public void addTempHeader(String line) {
		headers.add(line);
	}

	public void addTempHeader(int i, String line) {
		headers.add(i, line);
	}

	public void addTempHeader(String[] lines) {
		headers.addAll(new ArrayList<String>(Arrays.asList(lines)));
	}

	public void addTempHeader(int i, String[] lines) {
		headers.addAll(i, new ArrayList<String>(Arrays.asList(lines)));
	}

	public int runPython(String bundlePath) {
		if (bundlePath == null) {
			bundlePath = sysargv.get(0);
		}
		File pyFile = new File(bundlePath);
		if (!pyFile.isAbsolute()) {
			pyFile = new File(System.getProperty("user.dir"), bundlePath);
		}
		bundlePath = pyFile.getPath();
		String prefix = pyFile.getName().substring(0, pyFile.getName().lastIndexOf('.'));
		pyFile = new File(pyFile.getPath(), prefix + ".py");
		if (pyFile.exists()) {
			return runPython(bundlePath, pyFile);
		} else {
			Debug.error("No runnable script found: " + pyFile.getPath());
			return -2;
		}
	}

	public int runPython(String bundlePath, File pyFile) {
		if (CommandArgs.isIDE(_runType)) {
			if (sysargv.isEmpty()) {
				sysargv.add(bundlePath);
			} else {
				sysargv.set(0, bundlePath);
			}
		}

		// where the java command is run
		addTempHeader("addModPath(\""
						+ Util.slashify(System.getProperty("user.dir"), true) + "\")");

		// the script directory ..../foobar.sikuli
		addTempHeader("addModPath(\""
						+ Util.slashify(bundlePath, true) + "\")");

		// the directory whre the .sikuli is located
		String parent = Util.getParentPath(bundlePath);
		if (parent != null) {
			addTempHeader("addModPath(\""
							+ Util.slashify(parent, true) + "\")");
		}

		addTempHeader("sys.argv = ['' for i in range(" + sysargv.size() + ")]");
		for (int i = 0; i < sysargv.size(); i++) {
			addTempHeader("sys.argv[" + i + "]=r'" + sysargv.get(i) + "'");
		}

		it = headers.iterator();
		while (it.hasNext()) {
			String line = it.next();
			py.exec(line);
		}
		headers.clear();

		String fullpath = new File(bundlePath).getAbsolutePath();
		ImageLocator.setBundlePath(fullpath);

		int exitCode = 0;
		boolean exitDone = false;
		try {
			py.execfile(pyFile.getAbsolutePath());
		} catch (Exception e) {
			java.util.regex.Pattern p =
							java.util.regex.Pattern.compile("SystemExit: ([0-9]+)");
			Matcher matcher = p.matcher(e.toString());
			if (matcher.find()) {
				exitCode = Integer.parseInt(matcher.group(1));
				Debug.info("Exit code: " + exitCode);
			} else {
				Debug.error("Script aborted with some error:", e);
				e.printStackTrace();
				exitCode = -1;
			}
			exitDone = true;
		}
		if (!exitDone) {
			try {
				py.exec("exit(0)");
			} catch (Exception e) {
				// exit normally
				Debug.info("Exit code: 0");
			}
		}
		py.cleanup();
		return exitCode;
	}

	public void close() {
		ScreenHighlighter.closeAll();
	}

	private void runPythonAsync(final String bundlePath) throws IOException {
		Thread t = new Thread() {
			@Override
			public void run() {
				runPython(bundlePath);
			}
		};
		SwingUtilities.invokeLater(t);
	}
}
