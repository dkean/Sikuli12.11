/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.ide;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.sikuli.core.Location;
import org.sikuli.core.Region;
import org.sikuli.core.ScreenImage;
import org.sikuli.core.UnionScreen;
import org.sikuli.ide.util.Utils;
import org.sikuli.utility.Debug;

public class PatternWindow extends JFrame {

	private PatternImageButton _imgBtn;
	private PatternPaneScreenshot _screenshot;
	private PatternPaneTargetOffset _tarOffsetPane;
	private PatternPaneNaming paneNaming;
	private JTabbedPane tabPane;
	private JPanel paneTarget, panePreview;
	private JLabel[] msgApplied;
	private int tabSequence = 0;
	private static final int tabMax = 3;
	private ScreenImage _simg;
	private boolean dirty;

	static String _I(String key, Object... args) {
		return SikuliIDEI18N._I(key, args);
	}

	public PatternWindow(PatternImageButton imgBtn, boolean exact,
					float similarity, int numMatches) {
		super(_I("winPatternSettings"));
		init(imgBtn, exact, similarity, numMatches);
	}

	private void init(PatternImageButton imgBtn, boolean exact, float similarity, int numMatches) {
		_imgBtn = imgBtn;
		Point pos = imgBtn.getLocationOnScreen();
		Debug.log(4, "pattern window: " + pos);
		setLocation(pos.x + imgBtn.getWidth(), pos.y);

		takeScreenshot();
		Container c = getContentPane();
		c.setLayout(new BorderLayout());

		tabPane = new JTabbedPane();
		tabPane.setPreferredSize(new Dimension(790, 700));
		msgApplied = new JLabel[tabMax];

		msgApplied[tabSequence] = new JLabel("...");
		paneNaming = new PatternPaneNaming(_imgBtn, msgApplied[tabSequence++]);
		tabPane.addTab(_I("tabNaming"), paneNaming);

		msgApplied[tabSequence] = new JLabel("...");
		panePreview = createPreviewPanel();
		tabSequence++;
		tabPane.addTab(_I("tabMatchingPreview"), panePreview);

		msgApplied[tabSequence] = new JLabel("...");
		paneTarget = createTargetPanel();
		tabSequence++;
		tabPane.addTab(_I("tabTargetOffset"), paneTarget);

		c.add(tabPane, BorderLayout.CENTER);
		c.add(createButtons(), BorderLayout.SOUTH);
		c.doLayout();
		pack();
		try {
			_screenshot.setParameters(_imgBtn.getFilename(),
							exact, similarity, numMatches);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setDirty(false);
		setVisible(true);
	}

	public void setMessageApplied(int i, boolean flag) {
		if (flag) {
			msgApplied[i].setText("Changes have been applied");
		} else {
			msgApplied[i].setText("...");
		}
	}

	public void close() {
		_simg = null;
		_imgBtn.resetWindow();
	}

	public JTabbedPane getTabbedPane() {
		return tabPane;
	}

	void takeScreenshot() {
		SikuliIDE ide = SikuliIDE.getInstance();
		ide.setVisible(false);
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		Region match_region = new UnionScreen();
		_simg = match_region.getScreen().capture();
		ide.setVisible(true);
	}

	private JPanel createPreviewPanel() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

		createScreenshots(p);
		p.add(Box.createVerticalStrut(5));
		p.add(_screenshot.createControls());
		p.add(Box.createVerticalStrut(5));
		p.add(msgApplied[tabSequence]);
		p.doLayout();
		return p;
	}

	private void createScreenshots(Container c) {
		_screenshot = new PatternPaneScreenshot(_simg);
		//_screenshot.addObserver(this);
		createMarginBox(c, _screenshot);
	}

	private void createMarginBox(Container c, Component comp) {
		c.add(Box.createVerticalStrut(10));
		Box lrMargins = Box.createHorizontalBox();
		lrMargins.add(Box.createHorizontalStrut(10));
		lrMargins.add(comp);
		lrMargins.add(Box.createHorizontalStrut(10));
		c.add(lrMargins);
		c.add(Box.createVerticalStrut(10));
	}

	private JPanel createTargetPanel() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

		_tarOffsetPane = new PatternPaneTargetOffset(
						_simg, _imgBtn.getFilename(), _imgBtn.getTargetOffset());
		//p.addObserver(this);
		createMarginBox(p, _tarOffsetPane);
		p.add(Box.createVerticalStrut(5));
		p.add(_tarOffsetPane.createControls());
		p.add(Box.createVerticalStrut(5));
		p.add(msgApplied[tabSequence]);
		p.doLayout();
		return p;
	}

	private JComponent createButtons() {
		JPanel pane = new JPanel(new GridBagLayout());

		JButton btnOK = new JButton(_I("ok"));
		btnOK.addActionListener(new ActionOK(this));
		JButton btnApply = new JButton(_I("apply"));
		btnApply.addActionListener(new ActionApply(this));
		JButton btnCancel = new JButton(_I("cancel"));
		btnCancel.addActionListener(new ActionCancel(this));

		GridBagConstraints c = new GridBagConstraints();

		c.gridy = 3;
		c.gridx = 0;
		c.insets = new Insets(5, 0, 10, 0);
		c.anchor = GridBagConstraints.LAST_LINE_END;
		pane.add(btnOK, c);
		c.gridx = 1;
		pane.add(btnApply, c);
		c.gridx = 2;
		pane.add(btnCancel, c);

		return pane;
	}

	public void setTargetOffset(Location offset) {
		if (offset != null) {
			_tarOffsetPane.setTarget(offset.x, offset.y);
		}
	}

	private void actionPerformedUpdates(Window _parent) {
		boolean tempDirty = isDirty();
		if (isDirty()) {
			setDirty(false);
			int i = _imgBtn.getWindow().getTabbedPane().getSelectedIndex();
			_imgBtn.getWindow().setMessageApplied(i, false);
		}
		if (paneNaming.isDirty()) {
			String filename = paneNaming.getAbsolutePath();
			String oldFilename = _imgBtn.getFilename();
			if (Utils.exists(filename)) {
				String name = Utils.getName(filename);
				int ret = JOptionPane.showConfirmDialog(
								_parent,
								SikuliIDEI18N._I("msgFileExists", name),
								SikuliIDEI18N._I("dlgFileExists"),
								JOptionPane.WARNING_MESSAGE,
								JOptionPane.YES_NO_OPTION);
				if (ret != JOptionPane.YES_OPTION) {
					return;
				}
			}
			try {
				Utils.xcopy(oldFilename, filename);
				_imgBtn.setFilename(filename);
			} catch (IOException ioe) {
				Debug.error("renaming failed: " + oldFilename + " " + filename);
				Debug.error(ioe.getMessage());
			}
			paneNaming.updateFilename();
			addDirty(true);
		}
		addDirty(_imgBtn.setParameters(
						_screenshot.isExact(), _screenshot.getSimilarity(),
						_screenshot.getNumMatches()));
		addDirty(_imgBtn.setTargetOffset(_tarOffsetPane.getTargetOffset()));
		Debug.log("update: " + _imgBtn.toString());
		if (isDirty()) {
			int i = _imgBtn.getWindow().getTabbedPane().getSelectedIndex();
			_imgBtn.getWindow().setMessageApplied(i, true);
		} else {
			setDirty(tempDirty);
		}
	}

	class ActionOK implements ActionListener {

		private Window _parent;

		public ActionOK(JFrame parent) {
			_parent = parent;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			actionPerformedUpdates(_parent);
			_imgBtn.getWindow().close();
			_parent.dispose();
			SikuliIDE.getInstance().getCurrentCodePane().setDirty(setDirty(false));
		}
	}

	class ActionApply implements ActionListener {

		private Window _parent;

		public ActionApply(Window parent) {
			_parent = parent;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			actionPerformedUpdates(_parent);
			_imgBtn.getWindow().getTabbedPane().getSelectedComponent().transferFocus();
		}
	}

	class ActionCancel implements ActionListener {

		private Window _parent;

		public ActionCancel(Window parent) {
			_parent = parent;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			_imgBtn.getWindow().close();
			_parent.dispose();
		}
	}

	protected boolean isDirty() {
		return dirty;
	}

	private boolean setDirty(boolean flag) {
		boolean xDirty = dirty;
		dirty = flag;
		return xDirty;
	}

	protected void addDirty(boolean flag) {
		dirty |= flag;
	}
}
