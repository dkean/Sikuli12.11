/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.ide;

import java.awt.*;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.*;

public class EditorCurrentLineHighlighter implements CaretListener {

  static final Color DEFAULT_COLOR = new Color(230, 230, 210);
  static final Color ERROR_COLOR = new Color(255, 105, 105);
  private Highlighter.HighlightPainter painter, errorPainter;
  private Object highlight;
  private int _errorLine = -1;

  public EditorCurrentLineHighlighter(JTextPane textPane) {
    this(textPane, null);
  }

  public EditorCurrentLineHighlighter(JTextPane textPane, Color highlightColor) {
    Color c = highlightColor != null ? highlightColor : DEFAULT_COLOR;
    MyHighlighter h = new MyHighlighter();
    textPane.setHighlighter(h);
    painter = new DefaultHighlighter.DefaultHighlightPainter(c);
    errorPainter = new DefaultHighlighter.DefaultHighlightPainter(ERROR_COLOR);
  }

  @Override
  public void caretUpdate(CaretEvent evt) {
    JTextComponent comp = (JTextComponent) evt.getSource();
    if (comp != null && highlight != null) {
      comp.getHighlighter().removeHighlight(highlight);
      highlight = null;
    }
    if (comp != null) {
      if (comp.getSelectionStart() != comp.getSelectionEnd()) {
        // cancel line highlighting if selection exists
        comp.repaint();
        return;
      }

      int pos = comp.getCaretPosition();
      Element elem = Utilities.getParagraphElement(comp, pos);
      int start = elem.getStartOffset();
      int end = elem.getEndOffset();

      Document doc = comp.getDocument();
      Element root = doc.getDefaultRootElement();
      int lineIdx = root.getElementIndex(pos);
      Highlighter.HighlightPainter p = painter;
      if (lineIdx == _errorLine) {
        p = errorPainter;
      }

      try {
        highlight = comp.getHighlighter().addHighlight(start, end, p);
        comp.repaint();
      } catch (BadLocationException ex) {
        ex.printStackTrace();
      }
    }
  }

  public void setErrorLine(int lineNo) {
    _errorLine = lineNo - 1;
  }
}

//<editor-fold defaultstate="collapsed" desc="class MyHighlighter extends DefaultHighlighter">
class MyHighlighter extends DefaultHighlighter {
  private JTextComponent component;

  @Override
  public final void install(final JTextComponent c) {
    super.install(c);
    this.component = c;
  }

  @Override
  public final void deinstall(final JTextComponent c) {
    super.deinstall(c);
    this.component = null;
  }

  @Override
  public final void paint(final Graphics g) {
    final Highlighter.Highlight[] highlights = getHighlights();
    final int len = highlights.length;
    for (int i = 0; i < len; i++) {
      Highlighter.Highlight info = highlights[i];
      if (info.getClass().getName().indexOf("LayeredHighlightInfo") > -1) {
        // Avoid allocing unless we need it.
        final Rectangle a = this.component.getBounds();
        final Insets insets = this.component.getInsets();
        a.x = insets.left;
        a.y = insets.top;
        // a.width -= insets.left + insets.right + 100;
        a.height -= insets.top + insets.bottom;
        //FIXME: should shift up..
        for (; i < len; i++) {
          info = highlights[i];
          if (info.getClass().getName().indexOf(
                  "LayeredHighlightInfo") > -1) {
            final Highlighter.HighlightPainter p = info
                    .getPainter();
            p.paint(g, info.getStartOffset(), info
                    .getEndOffset(), a, this.component);
          }
        }
      }
    }
  }
}

//</editor-fold>
