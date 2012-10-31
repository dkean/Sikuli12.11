/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.ide;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.Locale;
import javax.imageio.*;
import javax.swing.*;
import org.sikuli.core.Location;
import org.sikuli.ide.util.Utils;
import org.sikuli.utility.Debug;

class PatternImageButton extends JButton implements ActionListener, Serializable /*, MouseListener*/ {

	static final int DEFAULT_NUM_MATCHES = 10;
	static final float DEFAULT_SIMILARITY = 0.7f;
	private String _imgFilename, _thumbFname;
	private EditorPane _pane;
	private float _similarity;
	private int _numMatches = DEFAULT_NUM_MATCHES;
	private boolean _exact;
	private Location _offset = new Location(0, 0);
	private int _imgW, _imgH;
	private float _scale = 1f;
	private static PatternWindow pwin = null;
	private static Font textFont = new Font("arial", Font.BOLD, 12);

	protected PatternImageButton(EditorPane pane) {
		init(pane, null);
	}

	public PatternImageButton(EditorPane pane, String imgFilename) {
		init(pane, imgFilename);
	}

	private void init(EditorPane pane, String imgFilename) {
		_pane = pane;
		_exact = false;
		_similarity = DEFAULT_SIMILARITY;
		_numMatches = DEFAULT_NUM_MATCHES;
		if (imgFilename != null) {
			setFilename(imgFilename);
		}
		setBorderPainted(true);
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		addActionListener(this);
		setToolTipText(this.toString());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Debug.log("open Pattern Settings");
		if (pwin == null) {
			pwin = new PatternWindow(this, _exact, _similarity, _numMatches);
			pwin.setTargetOffset(_offset);
		} else {
			pwin.requestFocus();
		}
	}

	public PatternWindow getWindow() {
		return pwin;
	}

	public void resetWindow() {
		pwin = null;
	}

	public String getFilename() {
		File img = new File(_imgFilename);
		String oldBundle = img.getParent();
		String newBundle = _pane.getSrcBundle();
		Debug.log("ImageButton.getFilename: " + oldBundle + " " + newBundle);
		if (oldBundle == newBundle) {
			return _imgFilename;
		}
		setFilename(newBundle + File.separatorChar + img.getName());
		return _imgFilename;
	}

	public void setFilename(String newFilename) {
		_imgFilename = newFilename;
		_thumbFname = createThumbnail(_imgFilename);
		setIcon(new ImageIcon(_thumbFname));
		setToolTipText(this.toString());
	}

	public boolean setParameters(boolean exact, float similarity, int numMatches) {
		boolean dirty = false;
		Debug.log(3, "setParameters: " + exact + "," + similarity + "," + numMatches);
		dirty |= setExact(exact);
		dirty |= setSimilarity(similarity);
		setToolTipText(this.toString());
		return dirty;
	}

	public boolean setExact(boolean exact) {
		if (_exact != exact) {
			_exact = exact;
			return true;
		}
		return false;
	}

	public boolean setSimilarity(float val) {
		float sim;
		if (val < 0) {
			sim = 0;
		} else if (val > 1) {
			sim = 0.99f;
		} else {
			sim = val;
		}
		if (sim != _similarity) {
			_similarity = sim;
			return true;
		}
		return false;
	}

	public boolean setTargetOffset(Location offset) {
		Debug.log("setTargetOffset: " + offset);
		if (!_offset.equals(offset)) {
			_offset = offset;
			setToolTipText(this.toString());
			return true;
		}
		return false;
	}

	public Location getTargetOffset() {
		return _offset;
	}

	public BufferedImage createThumbnailImage(int maxHeight) {
		return createThumbnailImage(_imgFilename, maxHeight);
	}

	private BufferedImage createThumbnailImage(String imgFname, int maxHeight) {
		try {
			BufferedImage img = ImageIO.read(new File(imgFname));
			int w = img.getWidth(null), h = img.getHeight(null);
			_imgW = w;
			_imgH = h;
			if (maxHeight >= h) {
				return img;
			}
			_scale = (float) maxHeight / h;
			w *= _scale;
			h *= _scale;
			BufferedImage thumb = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = thumb.createGraphics();
			g2d.drawImage(img, 0, 0, w, h, null);
			g2d.dispose();
			return thumb;
		} catch (IOException e) {
			Debug.error("Can't read file: " + e.getMessage());
			return null;
		}
	}

	private String createThumbnail(String imgFname, int maxHeight) {
		BufferedImage thumb = createThumbnailImage(imgFname, maxHeight);
		return Utils.saveTmpImage(thumb);
	}

	private String createThumbnail(String imgFname) {
		final int max_h = PreferencesUser.getInstance().getDefaultThumbHeight();
		return createThumbnail(imgFname, max_h);
	}

	private boolean useThumbnail() {
		return !_imgFilename.equals(_thumbFname);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		drawText(g2d);
		if (useThumbnail()) {
			g2d.setColor(new Color(0, 128, 128, 128));
			g2d.drawRoundRect(3, 3, getWidth() - 7, getHeight() - 7, 5, 5);
		}
	}

	private void drawText(Graphics2D g2d) {
		String strSim = null, strOffset = null;
		if (_similarity != DEFAULT_SIMILARITY) {
			if (_exact) {
				strSim = "99";
			} else {
				strSim = String.format("%d", (int) (_similarity * 100));
			}
		}
		if (_offset != null && (_offset.x != 0 || _offset.y != 0)) {
			strOffset = _offset.toString();
		}
		if (strOffset == null && strSim == null) {
			return;
		}

		final int fontH = g2d.getFontMetrics().getMaxAscent();
		final int x = getWidth(), y = 0;
		drawText(g2d, strSim, x, y);
		if (_offset != null) {
			drawCross(g2d);
		}
	}

	private void drawCross(Graphics2D g2d) {
		int x, y;
		final String cross = "+";
		final int w = g2d.getFontMetrics().stringWidth(cross);
		final int h = g2d.getFontMetrics().getMaxAscent();
		if (_offset.x > _imgW / 2) {
			x = getWidth() - w;
		} else if (_offset.x < -_imgW / 2) {
			x = 0;
		} else {
			x = (int) (getWidth() / 2 + _offset.x * _scale - w / 2);
		}
		if (_offset.y > _imgH / 2) {
			y = getHeight() + h / 2 - 3;
		} else if (_offset.y < -_imgH / 2) {
			y = h / 2 + 2;
		} else {
			y = (int) (getHeight() / 2 + _offset.y * _scale + h / 2);
		}
		g2d.setFont(textFont);
		g2d.setColor(new Color(0, 0, 0, 180));
		g2d.drawString(cross, x + 1, y + 1);
		g2d.setColor(new Color(255, 0, 0, 180));
		g2d.drawString(cross, x, y);
	}

	private void drawText(Graphics2D g2d, String str, int x, int y) {
		if (str == null) {
			return;
		}
		final int w = g2d.getFontMetrics().stringWidth(str);
		final int fontH = g2d.getFontMetrics().getMaxAscent();
		final int borderW = 3;
		g2d.setFont(textFont);
		g2d.setColor(new Color(0, 128, 0, 128));
		g2d.fillRoundRect(x - borderW * 2 - w - 1, y, w + borderW * 2 + 1, fontH + borderW * 2 + 1, 3, 3);
		g2d.setColor(Color.white);
		g2d.drawString(str, x - w - 3, y + fontH + 3);
	}

	public static PatternImageButton createFromString(EditorPane parentPane, String str) {
		if (!str.startsWith("Pattern")) {
			if (str.charAt(0) == '\"' && str.charAt(str.length() - 1) == '\"') {
				String filename = str.substring(1, str.length() - 1);
				File f = parentPane.getFileInBundle(filename);
				if (f != null) {
					return new PatternImageButton(parentPane, f.getAbsolutePath());
				}
			}
			return null;
		}
		PatternImageButton btn = new PatternImageButton(parentPane);
		String[] tokens = str.split("\\)\\s*\\.?");
		for (String tok : tokens) {
			//System.out.println("token: " + tok);
			if (tok.startsWith("exact")) {
				btn.setExact(true);
				btn.setSimilarity(1.f);
			} else if (tok.startsWith("Pattern")) {
				String filename = tok.substring(
								tok.indexOf("\"") + 1, tok.lastIndexOf("\""));
				File f = parentPane.getFileInBundle(filename);
				if (f != null && f.exists()) {
					btn.setFilename(f.getAbsolutePath());
				} else {
					return null;
				}
			} else if (tok.startsWith("similar")) {
				String strArg = tok.substring(tok.lastIndexOf("(") + 1);
				try {
					btn.setSimilarity(Float.valueOf(strArg));
				} catch (NumberFormatException e) {
					return null;
				}
			} else if (tok.startsWith("firstN")) { // FIXME: replace with limit/max
				String strArg = tok.substring(tok.lastIndexOf("(") + 1);
				btn._numMatches = Integer.valueOf(strArg);
			} else if (tok.startsWith("targetOffset")) {
				String strArg = tok.substring(tok.lastIndexOf("(") + 1);
				String[] args = strArg.split(",");
				try {
					Location offset = new Location(0, 0);
					offset.x = Integer.valueOf(args[0]);
					offset.y = Integer.valueOf(args[1]);
					btn.setTargetOffset(offset);
				} catch (NumberFormatException e) {
					return null;
				}
			}
		}
		btn.setToolTipText(btn.toString());
		return btn;
	}

	@Override
	public String toString() {
		if (_imgFilename == null) {
			return "null";
		}
		String img = new File(_imgFilename).getName();
		String pat = "Pattern(\"" + img + "\")";
		String ret = "";
		if (_exact) {
			ret += ".exact()";
		} else if (_similarity != DEFAULT_SIMILARITY) {
			ret += String.format(Locale.ENGLISH, ".similar(%.2f)", _similarity);
		}
		if (_offset != null && (_offset.x != 0 || _offset.y != 0)) {
			ret += ".targetOffset(" + _offset.x + "," + _offset.y + ")";
		}
		if (!ret.equals("")) {
			ret = pat + ret;
		} else {
			ret = "\"" + img + "\"";
		}
		return ret;
	}
}
