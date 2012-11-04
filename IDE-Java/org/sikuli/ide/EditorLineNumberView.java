/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.ide;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class EditorLineNumberView extends JComponent {

  private static ImageIcon ERROR_ICON = SikuliIDE.getIconResource("/icons/error_icon.gif");
  // This is for the border to the right of the line numbers.
  // There's probably a UIDefaults value that could be used for this.
  private static final Color BORDER_COLOR = new Color(155, 155, 155);
  private static final int WIDTH_TEMPLATE = 999;
  private static final int MARGIN = 5;
  private FontMetrics viewFontMetrics;
  private int maxNumberWidth;
  private int componentWidth;
  private int textTopInset;
  private int textFontAscent;
  private int textFontHeight;
  private JTextComponent text;
  private SizeSequence sizes;
  private int startLine = 0;
  private boolean structureChanged = true;
  private Set<Integer> errLines = new HashSet<Integer>();

  /**
   * Construct a LineNumberView and attach it to the given text component. The
   * LineNumberView will listen for certain kinds of events from the text
   * component and update itself accordingly.
   *
   * @param startLine the line that changed, if there's only one
   * @param structureChanged if <tt>true</tt>, ignore the line number and update
   * all the line heights.
   */
  public EditorLineNumberView(JTextComponent text) {
    if (text == null) {
      throw new IllegalArgumentException("Text component required! Cannot be null!");
    }
    this.text = text;
    updateCachedMetrics();

    UpdateHandler handler = new UpdateHandler();
    text.getDocument().addDocumentListener(handler);
    text.addPropertyChangeListener(handler);
    text.addComponentListener(handler);

    setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));
    setForeground(Color.GRAY);
    setBackground(new Color(241, 241, 241));
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(componentWidth, text.getHeight());
  }

  public void addErrorMark(int line) {
    errLines.add(line);
  }

  public void resetErrorMark() {
    errLines.clear();
  }

  /**
   * Cache some values that are used a lot in painting or size calculations.
   * Also ensures that the line-number font is not larger than the text
   * component's font (by point-size, anyway).
   */
  private void updateCachedMetrics() {
    Font textFont = text.getFont();
    FontMetrics fm = getFontMetrics(textFont);
    textFontHeight = fm.getHeight();
    textFontAscent = fm.getAscent();
    textTopInset = text.getInsets().top;

    Font viewFont = getFont();
    boolean changed = false;
    if (viewFont == null) {
      viewFont = UIManager.getFont("Label.font");
      viewFont = viewFont.deriveFont(Font.PLAIN);
      changed = true;
    }
    if (viewFont.getSize() > textFont.getSize()) {
      viewFont = viewFont.deriveFont(textFont.getSize2D());
      changed = true;
    }

    viewFontMetrics = getFontMetrics(viewFont);
    maxNumberWidth = viewFontMetrics.stringWidth(String.valueOf(WIDTH_TEMPLATE));
    componentWidth = 2 * MARGIN + maxNumberWidth;

    if (changed) {
      super.setFont(viewFont);
    }
  }

  @Override
  public void setFont(Font font) {
    super.setFont(font);
    updateCachedMetrics();
  }

  @Override
  public void paintComponent(Graphics g) {
    updateSizes();
    Rectangle clip = g.getClipBounds();

    g.setColor(getBackground());
    g.fillRect(clip.x, clip.y, clip.width, clip.height);

    //        System.out.println( "\n clip.x = " + clip.x );
    //        System.out.println( " clip.y = " + clip.y );
    //        System.out.println( " clip.width = " + clip.width );
    //        System.out.println( " clip.height = " + clip.height );

    g.setColor(getForeground());
    int base = clip.y - textTopInset;
    if (sizes == null) {
      return;
    }
    int first = sizes.getIndex(base);
    int last = sizes.getIndex(base + clip.height);

    String lnum;

    for (int i = first; i < last; i++) {
      lnum = String.valueOf(i + 1);
      int x = MARGIN + maxNumberWidth - viewFontMetrics.stringWidth(lnum);
      int y = (sizes.getPosition(i) + sizes.getPosition(i + 1)) / 2 + textFontAscent / 2 + textTopInset / 2;

      if (errLines.contains(i + 1)) {
        final int h = 12;
        g.drawImage(ERROR_ICON.getImage(), 0, y - h + 1, h, h, null);
        g.setColor(Color.RED);
      } else {
        g.setColor(getForeground());
      }
      g.drawString(lnum, x, y);
    }
  }

  /**
   * Update the line heights as needed.
   */
  private void updateSizes() {
    if (startLine < 0) {
      return;
    }

    if (structureChanged) {
      int count = getAdjustedLineCount();
      //            System.out.println( "lines: " + count );
      sizes = new SizeSequence(count);
      for (int i = 0; i < count; i++) {
        sizes.setSize(i, getLineHeight(i));
      }
      structureChanged = false;
    } else {
      if (sizes != null) {
        sizes.setSize(startLine, getLineHeight(startLine));
      }
    }

    startLine = -1;
  }

  /* Copied from javax.swing.text.PlainDocument */
  private int getAdjustedLineCount() {
    // There is an implicit break being modeled at the end of the
    // document to deal with boundary conditions at the end.  This
    // is not desired in the line count, so we detect it and remove
    // its effect if throwing off the count.
    Element map = text.getDocument().getDefaultRootElement();
    int n = map.getElementCount();
    Element lastLine = map.getElement(n - 1);
    if ((lastLine.getEndOffset() - lastLine.getStartOffset()) >= 1) {
      return n;
    }

    return n - 1;
  }

  /**
   * Get the height of a line from the JTextComponent.
   *
   * @param index the line number
   * @param the height, in pixels
   */
  private int getLineHeight(int index) {
    int lastPos = sizes.getPosition(index) + textTopInset;
    int height = textFontHeight;
    try {
      Element map = text.getDocument().getDefaultRootElement();
      Element l = map.getElement(index);
      int lastChar = l.getEndOffset() - 1;
      Rectangle r = text.modelToView(lastChar);
      height = (r.y - lastPos) + r.height;
    } catch (BadLocationException ex) {
      ex.printStackTrace();
    }
    return height;
  }

  /**
   * Schedule a repaint because one or more line heights may have changed.
   *
   * @param startLine the line that changed, if there's only one
   * @param structureChanged if <tt>true</tt>, ignore the line number and update
   * all the line heights.
   */
  private void viewChanged(int startLine, boolean structureChanged) {
    this.startLine = startLine;
    this.structureChanged |= structureChanged;

    revalidate();
    repaint();
  }

  class UpdateHandler extends ComponentAdapter implements PropertyChangeListener, DocumentListener {

    /**
     * The text component was resized. 'Nuff said.
     */
    @Override
    public void componentResized(ComponentEvent evt) {
      viewChanged(0, true);
    }

    /**
     * A bound property was changed on the text component. Properties like the
     * font, border, and tab size affect the layout of the whole document, so we
     * invalidate all the line heights here.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      Object oldValue = evt.getOldValue();
      Object newValue = evt.getNewValue();
      String propertyName = evt.getPropertyName();
      if ("document".equals(propertyName)) {
        if (oldValue != null && oldValue instanceof Document) {
          ((Document) oldValue).removeDocumentListener(this);
        }
        if (newValue != null && newValue instanceof Document) {
          ((Document) newValue).addDocumentListener(this);
        }
      }

      updateCachedMetrics();
      viewChanged(0, true);
    }

    /**
     * Text was inserted into the document.
     */
    @Override
    public void insertUpdate(DocumentEvent evt) {
      update(evt);
    }

    /**
     * Text was removed from the document.
     */
    @Override
    public void removeUpdate(DocumentEvent evt) {
      update(evt);
    }

    /**
     * Text attributes were changed. In a source-code editor based on
     * StyledDocument, attribute changes should be applied automatically in
     * response to inserts and removals. Since we're already listening for
     * those, this method should be redundant, but YMMV.
     */
    @Override
    public void changedUpdate(DocumentEvent evt) {
      //      update(evt);
    }

    /**
     * If the edit was confined to a single line, invalidate that line's height.
     * Otherwise, invalidate them all.
     */
    private void update(DocumentEvent evt) {
      Element map = text.getDocument().getDefaultRootElement();
      int line = map.getElementIndex(evt.getOffset());
      DocumentEvent.ElementChange ec = evt.getChange(map);
      viewChanged(line, ec != null);
    }
  }
}
