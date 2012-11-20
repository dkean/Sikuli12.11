/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.ide;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import org.python.util.PythonInterpreter;
import org.sikuli.script.ImageLocator;
import org.sikuli.script.Settings;
import org.sikuli.ide.indentation.PythonIndentation;
import org.sikuli.ide.util.Utils;
import org.sikuli.script.SikuliScriptRunner;
import org.sikuli.script.Debug;

public class EditorPane extends JTextPane implements KeyListener, CaretListener {

  private File _editingFile;
  private String _srcBundlePath = null;
  private boolean _dirty = false;
  private EditorCurrentLineHighlighter _highlighter;
  private String _tabString = "   ";
  private EditorUndoManager _undo = null;
  // TODO: move to SikuliDocument
  private PythonIndentation _indentationLogic;
  //TODO SikuliToHtmlConverter implement in Java
  final static InputStream SikuliToHtmlConverter =
          SikuliIDE.class.getResourceAsStream("/scripts/sikuli2html.py");
  static String pyConverter =
          Utils.convertStreamToString(SikuliToHtmlConverter);
  //TODO SikuliBundleCleaner implement in Java
  final static InputStream SikuliBundleCleaner =
          SikuliIDE.class.getResourceAsStream("/scripts/clean-dot-sikuli.py");
  static String pyBundleCleaner =
          Utils.convertStreamToString(SikuliBundleCleaner);
  int _caret_last_x = -1;
  boolean _can_update_caret_last_x = true;
  static Pattern patPngStr = Pattern.compile("(\"[^\"]+?\\.(?i)png\")");
  static Pattern patCaptureBtn = Pattern.compile("(\"__SIKULI-CAPTURE-BUTTON__\")");
  static Pattern patPatternStr = Pattern.compile(
          "\\b(Pattern\\s*\\(\".*?\"\\)(\\.\\w+\\([^)]*\\))*)");
  static Pattern patRegionStr = Pattern.compile(
          "\\b(Region\\s*\\([\\d\\s,]+\\))");

  //<editor-fold defaultstate="collapsed" desc="Initialization">
  public EditorPane() {
    PreferencesUser pref = PreferencesUser.getInstance();
    setEditorKitForContentType("text/python", new EditorKit());
    setContentType("text/python");
    initKeyMap();
    setTransferHandler(new MyTransferHandler());
    _highlighter = new EditorCurrentLineHighlighter(this);
    addCaretListener(_highlighter);
    setFont(new Font(pref.getFontName(), Font.PLAIN, pref.getFontSize()));
    setMargin(new Insets(3, 3, 3, 3));
    //setTabSize(4);
    setBackground(Color.WHITE);
    if (!Settings.isMac()) {
      setSelectionColor(new Color(170, 200, 255));
    }
    updateDocumentListeners();

    _indentationLogic = new PythonIndentation();
    _indentationLogic.setTabWidth(pref.getTabWidth());
    pref.addPreferenceChangeListener(new PreferenceChangeListener() {
      @Override
      public void preferenceChange(PreferenceChangeEvent event) {
        if (event.getKey().equals("TAB_WIDTH")) {
          _indentationLogic.setTabWidth(Integer.parseInt(event.getNewValue()));
        }
      }
    });
    initEditorPane();
  }

  private void initEditorPane() {
    addKeyListener(this);
    addCaretListener(this);
  }

  private void updateDocumentListeners() {
    getDocument().addDocumentListener(new DirtyHandler());
    getDocument().addUndoableEditListener(_undo);
  }

  public EditorUndoManager getUndoManager() {
    if (_undo == null) {
      _undo = new EditorUndoManager();
    }
    return _undo;
  }

  public PythonIndentation getIndentationLogic() {
    return _indentationLogic;
  }

  private void initKeyMap() {
    InputMap map = this.getInputMap();
    int shift = InputEvent.SHIFT_MASK;
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, shift),
            EditorKit.sklDeindentAction);
  }

  @Override
  public void keyPressed(java.awt.event.KeyEvent ke) {
  }

  @Override
  public void keyReleased(java.awt.event.KeyEvent ke) {
  }

  @Override
  public void keyTyped(java.awt.event.KeyEvent ke) {
    /*
     * try{
     * //if(ke.getKeyChar() == '\t') expandTab();
     * checkCompletion(ke);
     * }
     * catch(BadLocationException e){
     * e.printStackTrace();
     * }
     */
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="fill and save pane content">
  public String loadFile() throws IOException {
    File file = new SikuliIDEFileChooser(SikuliIDE.getInstance()).load();
    if (file == null) {
      return null;
    }
    String fname = Utils.slashify(file.getAbsolutePath(), false);
    SikuliIDE ide = SikuliIDE.getInstance();
    int i = ide.isAlreadyOpen(fname);
    if (i > -1) {
      Debug.log(2, "Already open in IDE: " + fname);
      return null;
    }
    loadFile(fname);
    return fname;
  }

  public void loadFile(String filename) throws IOException {
    if (filename.endsWith("/")) {
      filename = filename.substring(0, filename.length() - 1);
    }
    setSrcBundle(filename + "/");
    _editingFile = findSourceFile(filename);
    this.read(new BufferedReader(new InputStreamReader(
            new FileInputStream(_editingFile), "UTF8")), null);
    updateDocumentListeners();
    setDirty(false);
  }

  private File findSourceFile(String sikuli_dir) {
    if (sikuli_dir.endsWith(".sikuli")
            || sikuli_dir.endsWith(".sikuli" + "/")) {
      File dir = new File(sikuli_dir);
      File[] pys = dir.listFiles(new GeneralFileFilter("py", "Python Source"));
      if (pys.length > 1) {
        String sikuli_name = dir.getName();
        sikuli_name = sikuli_name.substring(0, sikuli_name.lastIndexOf('.'));
        for (File f : pys) {
          String py_name = f.getName();
          py_name = py_name.substring(0, py_name.lastIndexOf('.'));
          if (py_name.equals(sikuli_name)) {
            return f;
          }
        }
      }
      if (pys.length >= 1) {
        return pys[0];
      }
    }
    return new File(sikuli_dir);
  }

  @Override
  public void read(Reader in, Object desc) throws IOException {
    super.read(in, desc);
    Document doc = getDocument();
    Element root = doc.getDefaultRootElement();
    parse(root);
    setCaretPosition(0);
  }

  private void writeFile(String filename) throws IOException {
    this.write(new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(filename), "UTF8")));
  }

  public boolean close() throws IOException {
    if (isDirty()) {
      Object[] options = {SikuliIDEI18N._I("yes"), SikuliIDEI18N._I("no"), SikuliIDEI18N._I("cancel")};
      int ans = JOptionPane.showOptionDialog(this,
              SikuliIDEI18N._I("msgAskSaveChanges", getCurrentShortFilename()),
              SikuliIDEI18N._I("dlgAskCloseTab"),
              JOptionPane.YES_NO_CANCEL_OPTION,
              JOptionPane.WARNING_MESSAGE,
              null,
              options, options[0]);
      if (ans == JOptionPane.CANCEL_OPTION
              || ans == JOptionPane.CLOSED_OPTION) {
        return false;
      } else if (ans == JOptionPane.YES_OPTION) {
        if (saveFile() == null) {
					return false;
				}
      }
      setDirty(false);
    }
    return true;
  }

  public boolean isDirty() {
    return _dirty;
  }

  public void setDirty(boolean flag) {
    if (_dirty == flag) {
      return;
    }
    _dirty = flag;
    //<editor-fold defaultstate="collapsed" desc="RaiMan no global dirty">
    if (flag) {
      //RaiManMod getRootPane().putClientProperty("Window.documentModified", true);
    } else {
      //SikuliIDE.getInstance().checkDirtyPanes();
    }
    //</editor-fold>
    SikuliIDE.getInstance().setCurrentFileTabTitleDirty(_dirty);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Caret handling">
  public int getLineAtCaret(int caretPosition) {
    Element root = getDocument().getDefaultRootElement();
    return root.getElementIndex(caretPosition) + 1;
  }

  public int getLineAtCaret() {
    int caretPosition = getCaretPosition();
    Element root = getDocument().getDefaultRootElement();
    return root.getElementIndex(caretPosition) + 1;
  }

  public int getColumnAtCaret() {
    int offset = getCaretPosition();
    int column;
    try {
      column = offset - Utilities.getRowStart(this, offset);
    } catch (BadLocationException e) {
      column = -1;
    }
    return column + 1;
  }

  @Override
  public void caretUpdate(CaretEvent evt) {
    if (_can_update_caret_last_x) {
      _caret_last_x = -1;
    } else {
      _can_update_caret_last_x = true;
    }
  }

  public void jumpTo(int lineNo, int column) throws BadLocationException {
    Debug.log(6, "jumpTo: " + lineNo + "," + column);
    int off = getLineStartOffset(lineNo - 1) + column - 1;
    int lineCount = getDocument().getDefaultRootElement().getElementCount();
    if (lineNo < lineCount) {
      int nextLine = getLineStartOffset(lineNo);
      if (off >= nextLine) {
        off = nextLine - 1;
      }
    }
    if (off < 0) {
      off = 0;
    }
    setCaretPosition(off);
  }

  public void jumpTo(int lineNo) throws BadLocationException {
    Debug.log(6, "jumpTo: " + lineNo);
    setCaretPosition(getLineStartOffset(lineNo - 1));
  }

  public void jumpTo(String funcName) throws BadLocationException {
    Debug.log(6, "jumpTo: " + funcName);
    Element root = getDocument().getDefaultRootElement();
    int pos = getFunctionStartOffset(funcName, root);
    if (pos >= 0) {
      setCaretPosition(pos);
    } else {
      throw new BadLocationException("Can't find function " + funcName, -1);
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="content handling">
  private int getFunctionStartOffset(String func, Element node) throws BadLocationException {
    Document doc = getDocument();
    int count = node.getElementCount();
    Pattern patDef = Pattern.compile("def\\s+" + func + "\\s*\\(");
    for (int i = 0; i < count; i++) {
      Element elm = node.getElement(i);
      if (elm.isLeaf()) {
        int start = elm.getStartOffset(), end = elm.getEndOffset();
        String line = doc.getText(start, end - start);
        Matcher matcher = patDef.matcher(line);
        if (matcher.find()) {
          return start;
        }
      } else {
        int p = getFunctionStartOffset(func, elm);
        if (p >= 0) {
          return p;
        }
      }
    }
    return -1;
  }

  public int getNumLines() {
    Document doc = getDocument();
    Element root = doc.getDefaultRootElement();
    int lineIdx = root.getElementIndex(doc.getLength() - 1);
    return lineIdx + 1;
  }

  // line starting from 0
  int getLineStartOffset(int line) throws BadLocationException {
    Element map = getDocument().getDefaultRootElement();
    if (line < 0) {
      throw new BadLocationException("Negative line", -1);
    } else if (line >= map.getElementCount()) {
      throw new BadLocationException("No such line", getDocument().getLength() + 1);
    } else {
      Element lineElem = map.getElement(line);
      return lineElem.getStartOffset();
    }
  }

  int parseRange(int start, int end) {
    try {
      end = parseLine(start, end, patCaptureBtn);
      end = parseLine(start, end, patPatternStr);
      end = parseLine(start, end, patRegionStr);
      end = parseLine(start, end, patPngStr);
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
    return end;
  }

  void parse(Element node) {
    int count = node.getElementCount();
    for (int i = 0; i < count; i++) {
      Element elm = node.getElement(i);
      Debug.log(8, elm.toString());
      if (elm.isLeaf()) {
        int start = elm.getStartOffset(), end = elm.getEndOffset();
        parseRange(start, end);
      } else {
        parse(elm);
      }
    }
  }

  int parseLine(int startOff, int endOff, Pattern ptn) throws BadLocationException {
    //System.out.println(startOff + " " + endOff);
    if (endOff <= startOff) {
      return endOff;
    }
    Document doc = getDocument();
    while (true) {
      String line = doc.getText(startOff, endOff - startOff);
      Matcher m = ptn.matcher(line);
      //System.out.println("["+line+"]");
      if (m.find()) {
        int len = m.end() - m.start();
        if (replaceWithImage(startOff + m.start(), startOff + m.end(), ptn)) {
          startOff += m.start() + 1;
          endOff -= len - 1;
        } else {
          startOff += m.end() + 1;
        }
      } else {
        break;
      }
    }
    return endOff;
  }

  boolean replaceWithImage(int startOff, int endOff, Pattern ptn)
          throws BadLocationException {
    Document doc = getDocument();
    String imgStr = doc.getText(startOff, endOff - startOff);
    Component comp = null;

    if (ptn == patPatternStr || ptn == patPngStr) {
      comp = PatternImageButton.createFromString(this, imgStr);
    } else if (ptn == patRegionStr) {
      comp = ButtonRegion.createFromString(this, imgStr);
    } else if (ptn == patCaptureBtn) {
      Element root = doc.getDefaultRootElement();
      int lineIdx = root.getElementIndex(endOff);
      Element line = root.getElement(lineIdx);
      comp = new ButtonCapture(this, line);
    }

    if (comp != null) {
      this.select(startOff, endOff);
      this.insertComponent(comp);
      return true;
    }

    return false;
  }

  void insertString(String str) {
    int sel_start = getSelectionStart();
    int sel_end = getSelectionEnd();
    if (sel_end != sel_start) {
      try {
        getDocument().remove(sel_start, sel_end - sel_start);
      } catch (BadLocationException e) {
        e.printStackTrace();
      }
    }
    int pos = getCaretPosition();
    insertString(pos, str);
    int new_pos = getCaretPosition();
    int end = parseRange(pos, new_pos);
    setCaretPosition(end);
  }

  void insertString(int pos, String str) {
    Document doc = getDocument();
    try {
      doc.insertString(pos, str, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  void appendString(String str) {
    Document doc = getDocument();
    try {
      int start = doc.getLength();
      doc.insertString(doc.getLength(), str, null);
      int end = doc.getLength();
      //end = parseLine(start, end, patHistoryBtnStr);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="feature search">
  /*
   * public int search(Pattern pattern){
   * return search(pattern, true);
   * }
   *
   * public int search(Pattern pattern, boolean forward){
   * if(!pattern.equals(_lastSearchPattern)){
   * _lastSearchPattern = pattern;
   * Document doc = getDocument();
   * int pos = getCaretPosition();
   * Debug.log("caret: "  + pos);
   * try{
   * String body = doc.getText(pos, doc.getLength()-pos);
   * _lastSearchMatcher = pattern.matcher(body);
   * }
   * catch(BadLocationException e){
   * e.printStackTrace();
   * }
   * }
   * return continueSearch(forward);
   * }
   */

  /*
   * public int search(String str){
   * return search(str, true);
   * }
   */
  public int search(String str, int pos, boolean forward) {
    int ret = -1;
    Document doc = getDocument();
    Debug.log(9, "search caret: " + pos + ", " + doc.getLength());
    try {
      String body;
      int begin;
      if (forward) {
        int len = doc.getLength() - pos;
        body = doc.getText(pos, len > 0 ? len : 0);
        begin = pos;
      } else {
        body = doc.getText(0, pos);
        begin = 0;
      }
      Pattern pattern = Pattern.compile(str);
      Matcher matcher = pattern.matcher(body);
      ret = continueSearch(matcher, begin, forward);
      if (ret < 0) {
        if (forward && pos != 0) {
          return search(str, 0, forward);
        }
        if (!forward && pos != doc.getLength()) {
          return search(str, doc.getLength(), forward);
        }
      }
    } catch (BadLocationException e) {
      Debug.log(7, "search caret: " + pos + ", " + doc.getLength()
              + e.getStackTrace());
    }
    return ret;
  }

  protected int continueSearch(Matcher matcher, int pos, boolean forward) {
    boolean hasNext = false;
    int start = 0, end = 0;
    if (!forward) {
      while (matcher.find()) {
        hasNext = true;
        start = matcher.start();
        end = matcher.end();
      }
    } else {
      hasNext = matcher.find();
      if (!hasNext) {
        return -1;
      }
      start = matcher.start();
      end = matcher.end();
    }
    if (hasNext) {
      Document doc = getDocument();
      getCaret().setDot(pos + end);
      getCaret().moveDot(pos + start);
      getCaret().setSelectionVisible(true);
      return pos + start;
    }
    return -1;
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="file handling">
  public String saveFile() throws IOException {
    if (_editingFile == null) {
      return saveAsFile();
    } else {
      writeSrcFile();
      return getCurrentShortFilename();
    }
  }

  public String saveAsFile() throws IOException {
    File file = new SikuliIDEFileChooser(SikuliIDE.getInstance()).save();
    if (file == null) {
      return null;
    }

    String bundlePath = file.getAbsolutePath();
    if (!file.getAbsolutePath().endsWith(".sikuli")) {
      bundlePath += ".sikuli";
    }
    if (Utils.exists(bundlePath)) {
      int res = JOptionPane.showConfirmDialog(
              null, SikuliIDEI18N._I("msgFileExists", bundlePath),
              SikuliIDEI18N._I("dlgFileExists"), JOptionPane.YES_NO_OPTION);
      if (res != JOptionPane.YES_OPTION) {
        return null;
      }
    }
    saveAsBundle(bundlePath);

    return getCurrentShortFilename();
  }

  public String exportAsZip() throws IOException, FileNotFoundException {
    File file = new SikuliIDEFileChooser(SikuliIDE.getInstance()).export();
    if (file == null) {
      return null;
    }

    String zipPath = file.getAbsolutePath();
    String srcName = file.getName();
    if (!file.getAbsolutePath().endsWith(".skl")) {
      zipPath += ".skl";
    } else {
      srcName = srcName.substring(0, srcName.lastIndexOf('.'));
    }
    writeFile(getSrcBundle() + srcName + ".py");
    Utils.zip(getSrcBundle(), zipPath);
    Debug.log(2, "export to executable file: " + zipPath);
    return zipPath;
  }

  void setSrcBundle(String newBundlePath) {
    _srcBundlePath = newBundlePath;
    ImageLocator.setBundlePath(_srcBundlePath);
  }

  public String getSrcBundle() {
    if (_srcBundlePath == null) {
      File tmp = Utils.createTempDir();
      setSrcBundle(Utils.slashify(tmp.getAbsolutePath(), true));
    }
    return _srcBundlePath;
  }

  public String getCurrentShortFilename() {
    if (_srcBundlePath != null) {
      File f = new File(_srcBundlePath);
      return f.getName();
    }
    return "Untitled";
  }

  public File getCurrentFile() {
    if (_editingFile == null && isDirty()) {
      try {
        saveAsFile();
        return _editingFile;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return _editingFile;
  }

	public String getCurrentFilename() {
		if (_editingFile == null) {
			return null;
		}
		return _editingFile.getAbsolutePath();
	}

  private void convertSrcToHtml(String bundle) {
    PythonInterpreter py = SikuliScriptRunner.getPythonInterpreter();
    Debug.log(2, "Convert Sikuli source code " + bundle + " to HTML");
    py.set("local_convert", true);
    py.set("sikuli_src", bundle);
    py.exec(pyConverter);
  }

  private void cleanBundle(String bundle) {
    if (!PreferencesUser.getInstance().getAtSaveCleanBundle()) {
      return;
    }
    PythonInterpreter py = SikuliScriptRunner.getPythonInterpreter();
    Debug.log(2, "Clear source bundle " + bundle);
    py.set("bundle_path", bundle);
    py.exec(pyBundleCleaner);
  }

  private void writeSrcFile() throws IOException {
    writeFile(_editingFile.getAbsolutePath());
    if (PreferencesUser.getInstance().getAtSaveMakeHTML()) {
      convertSrcToHtml(getSrcBundle());
    } else {
      (new File(_editingFile.getAbsolutePath().replaceFirst("py", "html"))).delete();
    }
    if (PreferencesUser.getInstance().getAtSaveCleanBundle()) {
      cleanBundle(getSrcBundle());
    }
//TODO: delete old .py and .html
    setDirty(false);
  }

  private void saveAsBundle(String bundlePath) throws IOException {
    bundlePath = Utils.slashify(bundlePath, true);
    if (_srcBundlePath != null) {
      Utils.xcopy(_srcBundlePath, bundlePath);
    } else {
      Utils.mkdir(bundlePath);
    }
    setSrcBundle(bundlePath);
    _editingFile = createSourceFile(bundlePath, ".py");
    Debug.log(2, "save to bundle: " + getSrcBundle());
    writeSrcFile();
    //TODO: update all bundle references in ImageButtons
  }

  private File createSourceFile(String bundlePath, String ext) {
    if (bundlePath.endsWith(".sikuli")
            || bundlePath.endsWith(".sikuli/")) {
      File dir = new File(bundlePath);
      String name = dir.getName();
      name = name.substring(0, name.lastIndexOf("."));
      return new File(bundlePath, name + ext);
    }
    return new File(bundlePath);
  }

  public File copyFileToBundle(String filename) {
    File f = new File(filename);
    String bundlePath = getSrcBundle();
    if (f.exists()) {
      try {
        File newFile = Utils.smartCopy(filename, bundlePath);
        return newFile;
      } catch (IOException e) {
        e.printStackTrace();
        return f;
      }
    }
    return null;
  }

  public File getFileInBundle(String filename) {
    try {
      String fullpath = ImageLocator.locate(filename);
      return new File(fullpath);
    } catch (IOException e) {
      return null;
    }
  }
  //</editor-fold>
  //<editor-fold defaultstate="collapsed" desc="currently not used">
  private Class _historyBtnClass;
  private Pattern _lastSearchPattern = null;
  private String _lastSearchString = null;
  private Matcher _lastSearchMatcher;

  public void setTabSize(int charactersPerTab) {
    FontMetrics fm = this.getFontMetrics(this.getFont());
    int charWidth = fm.charWidth('w');
    int tabWidth = charWidth * charactersPerTab;

    TabStop[] tabs = new TabStop[10];

    for (int j = 0; j < tabs.length; j++) {
      int tab = j + 1;
      tabs[j] = new TabStop(tab * tabWidth);
    }

    TabSet tabSet = new TabSet(tabs);
    SimpleAttributeSet attributes = new SimpleAttributeSet();
    StyleConstants.setFontSize(attributes, 18);
    StyleConstants.setFontFamily(attributes, "Osaka-Mono");
    StyleConstants.setTabSet(attributes, tabSet);
    int length = getDocument().getLength();
    getStyledDocument().setParagraphAttributes(0, length, attributes, true);
  }

  public void setTabs(int spaceForTab) {
    String t = "";
    for (int i = 0; i < spaceForTab; i++) {
      t += " ";
    }
    _tabString = t;
  }

  private void setHistoryCaptureButton(ButtonCapture btn) {
    _historyBtnClass = btn.getClass();
  }

  private void expandTab() throws BadLocationException {
    int pos = getCaretPosition();
    Document doc = getDocument();
    doc.remove(pos - 1, 1);
    doc.insertString(pos - 1, _tabString, null);
  }

  public void indent(int startLine, int endLine, int level) {
    Document doc = getDocument();
    String strIndent = "";
    if (level > 0) {
      for (int i = 0; i < level; i++) {
        strIndent += "  ";
      }
    } else {
      Debug.error("negative indentation not supported yet!!");
    }
    for (int i = startLine; i < endLine; i++) {
      try {
        int off = getLineStartOffset(i);
        if (level > 0) {
          doc.insertString(off, strIndent, null);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  void checkCompletion(java.awt.event.KeyEvent ke) throws BadLocationException {
    Document doc = getDocument();
    Element root = doc.getDefaultRootElement();
    int pos = getCaretPosition();
    int lineIdx = root.getElementIndex(pos);
    Element line = root.getElement(lineIdx);
    int start = line.getStartOffset(), len = line.getEndOffset() - start;
    String strLine = doc.getText(start, len - 1);
    Debug.log(9, "[" + strLine + "]");
    if (strLine.endsWith("find") && ke.getKeyChar() == '(') {
      ke.consume();
      doc.insertString(pos, "(", null);
      ButtonCapture btnCapture = new ButtonCapture(this, line);
      insertComponent(btnCapture);
      doc.insertString(pos + 2, ")", null);
    }

  }

  public void setErrorHighlight(int lineNo) {
    _highlighter.setErrorLine(lineNo);
    try {
      if (lineNo > 0) {
        jumpTo(lineNo);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    repaint();
  }
  //</editor-fold>

  private class DirtyHandler implements DocumentListener {

    @Override
    public void changedUpdate(DocumentEvent ev) {
      Debug.log(9, "change update");
      //setDirty(true);
    }

    @Override
    public void insertUpdate(DocumentEvent ev) {
      Debug.log(9, "insert update");
      setDirty(true);
    }

    @Override
    public void removeUpdate(DocumentEvent ev) {
      Debug.log(9, "remove update");
      setDirty(true);
    }
  }
}

//<editor-fold defaultstate="collapsed" desc="Transfer code incl. images between code panes">
class MyTransferHandler extends TransferHandler {

  static Map<String, String> _copiedImgs;

  static {
    _copiedImgs = new HashMap<String, String>();
  }

  public MyTransferHandler() {
  }

  @Override
  public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
    super.exportToClipboard(comp, clip, action);
  }

  @Override
  protected void exportDone(JComponent source,
          Transferable data,
          int action) {
    if (action == TransferHandler.MOVE) {
      JTextPane aTextPane = (JTextPane) source;
      int sel_start = aTextPane.getSelectionStart();
      int sel_end = aTextPane.getSelectionEnd();
      Document doc = aTextPane.getDocument();
      try {
        doc.remove(sel_start, sel_end - sel_start);
      } catch (BadLocationException e) {
        e.printStackTrace();

      }
    }

  }

  @Override
  public int getSourceActions(JComponent c) {
    return COPY_OR_MOVE;
  }

  @Override
  protected Transferable createTransferable(JComponent c) {
    JTextPane aTextPane = (JTextPane) c;

    EditorKit kit = ((EditorKit) aTextPane.getEditorKit());
    Document doc = aTextPane.getDocument();
    int sel_start = aTextPane.getSelectionStart();
    int sel_end = aTextPane.getSelectionEnd();

    StringWriter writer = new StringWriter();
    try {
      _copiedImgs.clear();
      kit.write(writer, doc, sel_start, sel_end - sel_start, _copiedImgs);
      return new StringSelection(writer.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
    for (int i = 0; i < transferFlavors.length; i++) {
      //System.out.println(transferFlavors[i]);
      if (transferFlavors[i].equals(DataFlavor.stringFlavor)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean importData(JComponent comp, Transferable t) {
    DataFlavor htmlFlavor = DataFlavor.stringFlavor;
    if (canImport(comp, t.getTransferDataFlavors())) {
      try {
        String transferString = (String) t.getTransferData(htmlFlavor);
        EditorPane targetTextPane = (EditorPane) comp;
        for (Map.Entry<String, String> entry : _copiedImgs.entrySet()) {
          String imgName = entry.getKey();
          String imgPath = entry.getValue();
          File destFile = targetTextPane.copyFileToBundle(imgPath);
          String newName = destFile.getName();
          if (!newName.equals(imgName)) {
            String ptnImgName = "\"" + imgName + "\"";
            newName = "\"" + newName + "\"";
            transferString = transferString.replaceAll(ptnImgName, newName);
            Debug.info(ptnImgName + " exists. Rename it to " + newName);
          }
        }
        targetTextPane.insertString(transferString);
      } catch (Exception e) {
        Debug.error("Can't transfer: " + t.toString());
      }
      return true;
    }
    return false;
  }
}
//</editor-fold>
