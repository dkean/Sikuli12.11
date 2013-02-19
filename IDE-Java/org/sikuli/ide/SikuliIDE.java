/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.ide;

//TODO
// dirty pane handling: on individual tab
import com.explodingpixels.macwidgets.MacUtils;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import org.apache.commons.cli.CommandLine;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.sikuli.ide.EditorKit;
import org.sikuli.ide.extmanager.ExtensionManagerFrame;
import org.sikuli.ide.sikuli_test.*;
import org.sikuli.ide.util.AutoUpdater;
import org.sikuli.ide.util.Utils;
import org.sikuli.script.CommandArgs;
import org.sikuli.script.Debug;
import org.sikuli.script.EventObserver;
import org.sikuli.script.EventSubject;
import org.sikuli.script.FileManager;
import org.sikuli.script.HotkeyEvent;
import org.sikuli.script.HotkeyListener;
import org.sikuli.script.HotkeyManager;
import org.sikuli.script.OverlayCapturePrompt;
import org.sikuli.script.ScreenImage;
import org.sikuli.script.Settings;
import org.sikuli.script.SikuliScript;
import org.sikuli.script.SikuliScriptRunner;
import org.sikuli.script.Location;
import org.sikuli.script.Screen;

public class SikuliIDE extends JFrame {

  // RaiMan not used final static boolean ENABLE_RECORDING = false;
  final static boolean ENABLE_UNIFIED_TOOLBAR = true;
  final static Color COLOR_SEARCH_FAILED = Color.red;
  final static Color COLOR_SEARCH_NORMAL = Color.black;
  final static int WARNING_CANCEL = 2;
  final static int WARNING_ACCEPTED = 1;
  final static int WARNING_DO_NOTHING = 0;
  static boolean _runningSkl = false;
  private static NativeLayer _native;
  private Dimension _windowSize = null;
  private Point _windowLocation = null;
  private boolean smallScreen = false;
  private int commandBarHeight = 800;
  private CloseableTabbedPane _mainPane;
  private EditorLineNumberView lineNumberColumn;
  private JSplitPane _mainSplitPane;
  private JTabbedPane msgPane;
  private EditorConsolePane _console;
  private JXCollapsiblePane _cmdList;
  private SikuliIDEStatusBar _status;
  private ButtonCapture _btnCapture;
  private ButtonRun _btnRun, _btnRunViz;
  private JXSearchField _searchField;
  private JMenuBar _menuBar = new JMenuBar();
  private JMenu _fileMenu = new JMenu(_I("menuFile"));
  private JMenu _editMenu = new JMenu(_I("menuEdit"));
  private UndoAction _undoAction = new UndoAction();
  private RedoAction _redoAction = new RedoAction();
  private FindAction _findHelper;
  private JMenu _runMenu = new JMenu(_I("menuRun"));
  private JMenu _viewMenu = new JMenu(_I("menuView"));
  private JMenu _toolMenu = new JMenu(_I("menuTool"));
  private JMenu _helpMenu = new JMenu(_I("menuHelp"));
  private JXCollapsiblePane _sidePane;
  private JPanel _unitPane;
  private JCheckBoxMenuItem _chkShowUnitTest;
  private UnitTestRunner _testRunner;
  private static CommandLine _cmdLine;
  private static boolean _useStderr = false;
  private static SikuliIDE _instance = null;
  private static Icon PY_SRC_ICON = getIconResource("/icons/py-src-16x16.png");
  private boolean _inited = false;
  private static boolean runMe = false;
  private int restoredScripts = 0;
  private int alreadyOpenedTab = -1;
  private static final String NL = String.format("%n");
  private Pattern pFile = Pattern.compile("File..(.*?\\.py).*?"
          + ",.*?line.*?(\\d+),.*?in(.*?)" + NL + "(.*?)" + NL);
  private int errorLine;
  private int errorColumn;
  private String errorType;
  private String errorText;
  private int errorClass;
  private String errorTrace;
  private static final int PY_SYNTAX = 0;
  private static final int PY_RUNTIME = 1;
  private static final int PY_JAVA = 2;
  private static final int PY_UNKNOWN = -1;
  private PreferencesUser prefs;

  public static String _I(String key, Object... args) {
    try {
      return SikuliIDEI18N._I(key, args);
    } catch (Exception e) {
      System.out.println("[I18N] " + key);
      return key;
    }
  }

  public static void errorMsg(String msg) {
    if (_useStderr) {
      System.err.println(msg);
    } else {
      JOptionPane.showMessageDialog(null, msg);
    }
  }

  public static ImageIcon getIconResource(String name) {
    URL url = SikuliIDE.class.getResource(name);
    if (url == null) {
      Debug.error("Warning: could not load \"" + name + "\" icon");
      return null;
    }
    return new ImageIcon(url);
  }

//TODO run only one windowed instance of IDE
  public static void main(String[] args) {
    File file;
    String fileName;

    Settings.showJavaInfo();

    CommandArgs cmdArgs = new CommandArgs("IDE");
    _cmdLine = cmdArgs.getCommandLine(args);
    boolean _newCommandline = false;

    if (_cmdLine.hasOption("h")) {
      cmdArgs.printHelp();
      return;
    }

    if (_cmdLine.hasOption("test")) {
      Debug.error("Test no longer supported! See docs for alternatives");
      return;
    }

    if (_cmdLine.hasOption("s")) {
      _useStderr = true;
    }

    if (_cmdLine.hasOption("load")) {
//TODO preload .sikuli scripts
      _newCommandline = true;
      Debug.error("Option -load: not yet working");
      return;
    }

    if (!_newCommandline && args != null && args.length >= 1) {
			int exitCode = 0;
			fileName = args[0];
			if (! fileName.startsWith("-") && fileName.endsWith(".skl")) {
				String f = FileManager.unzipSKL(fileName);
				if (f != null) {
					args[0] = f;
          _runningSkl = true;
				} else {
					System.exit(-2);
				}
        Debug.error(
            "Sikuli IDE is no longer used to run scripts from command line\n"
            + "This is delegated now to sikuli-script.jar\n"
            + "Look into the docs for more information on command line usage");
				try {
					SikuliScript.main(args);
				} catch (Exception e) {
				}
				if (! _runningSkl) {
//TODO System.exit() und return code
					System.exit(exitCode);
				} else {
					return;
				}
			}
    }

// we should open the IDE
		Settings.setArgs(CommandArgs.getPyArgs(_cmdLine));
		initNativeLayer();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Settings.isMac()) {
			_native.initApp();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
			}
		}
		SikuliIDE.getInstance(args);
  }

  //<editor-fold defaultstate="collapsed" desc="IDE setup and general">
  //TODO singleton lock
  protected SikuliIDE(String[] args) {
    super("Sikuli IDE");
    initSikuliIDE(args);
  }

  private void initSikuliIDE(String[] args) {
    Debug.log(5, "create SikuliIDE " + args);
//    new ScriptRunner(getPyArgs(null));

    prefs = PreferencesUser.getInstance();
    if (prefs.getUserType() < 0) {
      prefs.setUserType(PreferencesUser.NEWBEE);
      prefs.setDefaults(PreferencesUser.NEWBEE);
    }

    _native.initIDE(this);

    
    _windowSize = prefs.getIdeSize();
    _windowLocation = prefs.getIdeLocation();
    Rectangle s = (new Location(_windowLocation)).getScreen().getRect();
    if (_windowSize.width == 0 || _windowSize.width > s.width || 
            (new Location(_windowLocation)).getScreenContaining(false) == null) {
      if (s.width < 1025) {
        _windowSize = new Dimension(1024, 700);
        _windowLocation = new Point(0, 0);
      } else {
        _windowSize = new Dimension(s.width - 150, s.height - 100);
        _windowLocation = new Point(75, 0);
      }
    }
    if (_windowSize.getHeight() < commandBarHeight) {
      smallScreen = true;
    }
    setSize(_windowSize);
    setLocation(_windowLocation);

    initMenuBars(this);
    final Container c = getContentPane();
    c.setLayout(new BorderLayout());
    initTabPane();
    initMsgPane(prefs.getPrefMoreMessage() == PreferencesUser.HORIZONTAL);
// RaiMan not used		initSidePane(); // IDE UnitTest

    JPanel codeAndUnitPane = new JPanel(new BorderLayout(10, 10));
    codeAndUnitPane.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
    codeAndUnitPane.add(_mainPane, BorderLayout.CENTER);
// RaiMan not used		codeAndUnitPane.add(_sidePane, BorderLayout.EAST);
    if (prefs.getPrefMoreMessage() == PreferencesUser.VERTICAL) {
      _mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, codeAndUnitPane, msgPane);
    } else {
      _mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codeAndUnitPane, msgPane);
    }
    _mainSplitPane.setResizeWeight(1.0);
    _mainSplitPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

    JPanel editPane = new JPanel(new BorderLayout(0, 0));

    JComponent cp = createCommandPane();
// RaiMan not used
    if (PreferencesUser.getInstance().getPrefMoreCommandBar()) {
      editPane.add(cp, BorderLayout.WEST);
    } // Command List leftside

    editPane.add(_mainSplitPane, BorderLayout.CENTER);
    c.add(editPane, BorderLayout.CENTER);

    JToolBar tb = initToolbar();
    c.add(tb, BorderLayout.NORTH); // the buttons

    c.add(initStatusbar(), BorderLayout.SOUTH);
    c.doLayout();

    initShortcutKeys();
    initHotkeys();
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    initWindowListener();
    initTooltip();
    restoreSession();

    // RaiMan not used		autoCheckUpdate();

    if (_mainPane.getTabCount() == 0) {
      (new FileAction()).doNew(null);
    }

    _inited = true;
    getCurrentCodePane().requestFocus();
    setVisible(true);
    return; // as breakpoint
  }

  public static synchronized SikuliIDE getInstance(String args[]) {
    if (_instance == null) {
      _instance = new SikuliIDE(args);
    }
    return _instance;
  }

  public static synchronized SikuliIDE getInstance() {
    return getInstance(null);
  }

  @Override
  public void setTitle(String title) {
    super.setTitle("Sikuli " + SikuliIDESettings.SikuliVersion + " - " + title);
  }

  static private void initNativeLayer() {
    String os = "unknown";
    if (Settings.isWindows()) {
      os = "Windows";
    } else if (Settings.isMac()) {
      os = "Mac";
    } else if (Settings.isLinux()) {
      os = "Linux";
    }
    String className = "org.sikuli.ide.NativeLayerFor" + os;

    try {
      Class c = Class.forName(className);
      Constructor constr = c.getConstructor();
      _native = (NativeLayer) constr.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean saveSession(int action, boolean quitting) {
    int nTab = _mainPane.getTabCount();
    StringBuilder sbuf = new StringBuilder();
    for (int tabIndex = 0; tabIndex < nTab; tabIndex++) {
      try {
        JScrollPane scrPane = (JScrollPane) _mainPane.getComponentAt(tabIndex);
        EditorPane codePane = (EditorPane) scrPane.getViewport().getView();
        if (action == WARNING_DO_NOTHING) {
          if (quitting) {
            codePane.setDirty(false);
          }
          if (codePane.getCurrentFilename() == null) {
            continue;
          }
        } else if (codePane.isDirty()) {
          if (!(new FileAction()).doSaveIntern(tabIndex)) {
            if (quitting) {
              codePane.setDirty(false);
            }
            continue;
          }
        }
        File f = codePane.getCurrentFile();
        if (f != null) {
          String bundlePath = codePane.getSrcBundle();
          Debug.log(5, "save session: " + bundlePath);
          if (tabIndex != 0) {
            sbuf.append(";");
          }
          sbuf.append(bundlePath);
        }
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
    }
    PreferencesUser.getInstance().setIdeSession(sbuf.toString());
    return true;
  }

  private void restoreSession() {
    String session_str = PreferencesUser.getInstance().getIdeSession();
    if (session_str == null) {
      return;
    }
    String[] filenames = session_str.split(";");
    for (int i = 0; i < filenames.length; i++) {
      Debug.log(5, "restore session: " + filenames[i]);
      File f = new File(filenames[i]);
      if (f.exists()) {
        if (loadFile(filenames[i])) {
          restoredScripts += 1;
        }
      }
    }
  }

  //</editor-fold>
  //<editor-fold defaultstate="collapsed" desc="Support SikuliIDE">
  public CloseableTabbedPane getTabPane() {
    return _mainPane;
  }

  public EditorPane getCurrentCodePane() {
    if (_mainPane.getSelectedIndex() == -1) {
      return null;
    }
    JScrollPane scrPane = (JScrollPane) _mainPane.getSelectedComponent();
    EditorPane ret = (EditorPane) scrPane.getViewport().getView();
    return ret;
  }

  public void setCurrentFileTabTitle(String fname) {
    int tabIndex = _mainPane.getSelectedIndex();
    setFileTabTitle(fname, tabIndex);
  }

  public void setFileTabTitle(String fname, int tabIndex) {
    if (fname.endsWith("/")) {
      fname = fname.substring(0, fname.length() - 1);
    }
    fname = fname.substring(fname.lastIndexOf("/") + 1);
    fname = fname.substring(0, fname.lastIndexOf("."));
    _mainPane.setTitleAt(tabIndex, fname);
    this.setTitle(fname);
  }

  public void setCurrentFileTabTitleDirty(boolean isDirty) {
    int i = _mainPane.getSelectedIndex();
    String title = _mainPane.getTitleAt(i);
    if (!isDirty && title.startsWith("*")) {
      title = title.substring(1);
	    _mainPane.setTitleAt(i, title);
    }
		else if (isDirty && ! title.startsWith("*")) {
      title = "*" + title;
      _mainPane.setTitleAt(i, title);
	  }
  }

  public boolean loadFile(String file) {
    (new FileAction()).doNew(null);
    try {
      setCurrentFileTabTitle(file);
      getCurrentCodePane().loadFile(file);
      return true;
    } catch (IOException e) {
//TODO close tab again on error
			Debug.error("Can't load file " + file);
      Debug.error(e.getMessage());
      return false;
    }
  }

  /**
   * filename of current script to run
   *
   * @return absolute path
   * @deprecated (used by IDE Unitest)
   */
  @Deprecated
  public String getCurrentFilename() {
    EditorPane pane = getCurrentCodePane();
    String fname = pane.getCurrentFile().getAbsolutePath();
    return fname;
  }

  public ArrayList<String> getOpenedFilenames() {
    int nTab = _mainPane.getTabCount();
    File file = null;
    String filePath;
    ArrayList<String> filenames = new ArrayList<String>(0);
    if (nTab > 0) {
      for (int i = 0; i < nTab; i++) {
        JScrollPane scrPane = (JScrollPane) _mainPane.getComponentAt(i);
        EditorPane codePane = (EditorPane) scrPane.getViewport().getView();
        file = codePane.getCurrentFile();
        if (file != null) {
          filePath = Utils.slashify(file.getAbsolutePath(), false);
          filePath = filePath.substring(0, filePath.lastIndexOf("/"));
          filenames.add(filePath);
        } else {
          filenames.add("");
        }
      }
    }
    return filenames;
  }

  public int isAlreadyOpen(String filename) {
    alreadyOpenedTab = getOpenedFilenames().indexOf(filename);
    return alreadyOpenedTab;
  }

  private void autoCheckUpdate() {
    PreferencesUser pref = PreferencesUser.getInstance();
    if (!pref.getCheckUpdate()) {
      return;
    }
    long last_check = pref.getCheckUpdateTime();
    long now = (new Date()).getTime();
    if (now - last_check > 1000 * 86400) {
      Debug.log(3, "check update");
      (new HelpAction()).checkUpdate(true);
    }
    pref.setCheckUpdateTime();
  }

  protected boolean doBeforeRun() {
    int action;
    if (checkDirtyPanes()) {
      if (prefs.getPrefMoreRunSave()) {
        action = WARNING_ACCEPTED;
      } else {
        action = askForSaveAll("Run");
        if (action < 0) {
          return false;
        }
      }
      saveSession(action, false);
    }
    Settings.ActionLogs = prefs.getPrefMoreLogActions();
    Settings.DebugLogs = prefs.getPrefMoreLogDebug();
    Settings.InfoLogs = prefs.getPrefMoreLogInfo();
    Settings.Highlight = prefs.getPrefMoreHighlight();
    Settings.OcrTextSearch = prefs.getPrefMoreTextSearch();
    Settings.OcrTextRead = prefs.getPrefMoreTextOCR();
    return true;
  }

  protected boolean doBeforeQuit() {
    if (checkDirtyPanes()) {
      int action = askForSaveAll("Quit");
      if (action < 0) {
        return false;
      }
      return saveSession(action, true);
    }
    return saveSession(WARNING_DO_NOTHING, true);
  }

  private int askForSaveAll(String typ) {
//TODO I18N
		String warn = "Some scripts are not saved yet!";
    String title = SikuliIDEI18N._I("dlgAskCloseTab");
    String[] options = new String[3];
    options[WARNING_DO_NOTHING] = typ + " immediatly";
    options[WARNING_ACCEPTED] = "Save all and " + typ;
    options[WARNING_CANCEL] = SikuliIDEI18N._I("cancel");
    int ret = JOptionPane.showOptionDialog(this, warn, title, 0, JOptionPane.WARNING_MESSAGE, null, options, options[2]);
    if (ret == WARNING_CANCEL || ret == JOptionPane.CLOSED_OPTION) {
      return -1;
    }
    return ret;
  }
  //</editor-fold>
  //<editor-fold defaultstate="collapsed" desc="isInited --- RaiMan not used">

  public boolean isInited() {
    return _inited;
  }
  //</editor-fold>

  private JMenuItem createMenuItem(JMenuItem item, KeyStroke shortcut, ActionListener listener) {
    if (shortcut != null) {
      item.setAccelerator(shortcut);
    }
    item.addActionListener(listener);
    return item;
  }

  private JMenuItem createMenuItem(String name, KeyStroke shortcut, ActionListener listener) {
    JMenuItem item = new JMenuItem(name);
    return createMenuItem(item, shortcut, listener);
  }

  class MenuAction implements ActionListener {

    protected Method actMethod = null;
    protected String action;

    public MenuAction() {
    }

    public MenuAction(String item) throws NoSuchMethodException {
      Class[] params = new Class[0];
      Class[] paramsWithEvent = new Class[1];
      try {
        paramsWithEvent[0] = Class.forName("java.awt.event.ActionEvent");
        actMethod = this.getClass().getMethod(item, paramsWithEvent);
        action = item;
      } catch (ClassNotFoundException cnfe) {
        Debug.error("Can't find menu action: " + cnfe);
      }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (actMethod != null) {
        try {
          Debug.log(3, "MenuAction." + action);
          Object[] params = new Object[1];
          params[0] = e;
          actMethod.invoke(this, params);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  //<editor-fold defaultstate="collapsed" desc="Init FileMenu">
  private void initFileMenu() throws NoSuchMethodException {
    int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    _fileMenu.setMnemonic(java.awt.event.KeyEvent.VK_F);

    _fileMenu.add(createMenuItem(_I("menuFileNew"),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, scMask),
            new FileAction(FileAction.NEW)));

    _fileMenu.add(createMenuItem(_I("menuFileOpen"),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, scMask),
            new FileAction(FileAction.OPEN)));

    _fileMenu.add(createMenuItem(_I("menuFileSave"),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, scMask),
            new FileAction(FileAction.SAVE)));

    _fileMenu.add(createMenuItem(_I("menuFileSaveAs"),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
            InputEvent.SHIFT_MASK | scMask),
            new FileAction(FileAction.SAVE_AS)));

     _fileMenu.add(createMenuItem(_I("menuFileExport"),
     KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E,
     InputEvent.SHIFT_MASK | scMask),
     new FileAction(FileAction.EXPORT)));

		 _fileMenu.add(createMenuItem(_I("menuFileCloseTab"),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, scMask),
            new FileAction(FileAction.CLOSE_TAB)));

    if (!Settings.isMac()) {
      _fileMenu.addSeparator();
      _fileMenu.add(createMenuItem(_I("menuFilePreferences"),
              KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, scMask),
              new FileAction(FileAction.PREFERENCES)));
    }

    if (!Settings.isMac()) {
      _fileMenu.addSeparator();
      _fileMenu.add(createMenuItem(_I("menuFileQuit"),
              null, new FileAction(FileAction.QUIT)));
    }
  }

  class FileAction extends MenuAction {

    static final String NEW = "doNew";
    static final String OPEN = "doLoad";
    static final String SAVE = "doSave";
    static final String SAVE_AS = "doSaveAs";
    static final String EXPORT = "doExport";
    static final String CLOSE_TAB = "doCloseTab";
    static final String PREFERENCES = "doPreferences";
    static final String QUIT = "doQuit";

    public FileAction() {
      super();
    }

    public FileAction(String item) throws NoSuchMethodException {
      super(item);
    }

    public void doQuit(ActionEvent ae) {
      SikuliIDE ide = SikuliIDE.getInstance();
			if (!doBeforeQuit()) {
				return;
			}
      while (true) {
        EditorPane codePane = ide.getCurrentCodePane();
        if (codePane == null) {
          break;
        }
        if (!ide.closeCurrentTab()) {
          return;
        }
      }
      System.exit(0);
    }

    public void doPreferences(ActionEvent ae) {
      SikuliIDE.getInstance().showPreferencesWindow();
    }

    public void doNew(ActionEvent ae) {
      EditorPane codePane = new EditorPane();
      JScrollPane scrPane = new JScrollPane(codePane);
      lineNumberColumn = new EditorLineNumberView(codePane);
      scrPane.setRowHeaderView(lineNumberColumn);
      _mainPane.addTab(_I("tabUntitled"), scrPane);
      _mainPane.setSelectedIndex(_mainPane.getTabCount() - 1);
      codePane.addCaretListener(new CaretListener() {
        @Override
        public void caretUpdate(CaretEvent evt) {
          EditorPane comp = (EditorPane) evt.getSource();
          int line = comp.getLineAtCaret();
          int col = comp.getColumnAtCaret();
          if (_status != null) {
            _status.setCaretPosition(line, col);
          }
        }
      });
      codePane.requestFocus();
    }

    public void doLoad(ActionEvent ae) {
      alreadyOpenedTab = _mainPane.getSelectedIndex();
      try {
        doNew(ae);
        EditorPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
        String fname = codePane.loadFile();
        if (fname != null) {
          SikuliIDE.getInstance().setCurrentFileTabTitle(fname);
        } else {
          doCloseTab(ae);
          _mainPane.setSelectedIndex(alreadyOpenedTab);
        }
      } catch (IOException eio) {
        eio.printStackTrace();
      }
    }

    public void doSave(ActionEvent ae) {
      try {
        EditorPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
        String fname = codePane.saveFile();
        if (fname != null) {
          SikuliIDE.getInstance().setCurrentFileTabTitle(fname);
        }
      } catch (IOException eio) {
        eio.printStackTrace();
      }
    }

    public boolean doSaveIntern(int tabIndex) {
      JScrollPane scrPane = (JScrollPane) _mainPane.getComponentAt(tabIndex);
      EditorPane codePane = (EditorPane) scrPane.getViewport().getView();
      try {
        String fname = codePane.saveFile();
        if (fname != null) {
          SikuliIDE.getInstance().setFileTabTitle(fname, tabIndex);
        } else {
          return false;
        }
      } catch (IOException eio) {
        eio.printStackTrace();
        return false;
      }
      return true;
    }

    public void doSaveAs(ActionEvent ae) {
      try {
        EditorPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
        String fname = codePane.saveAsFile();
        if (fname != null) {
          SikuliIDE.getInstance().setCurrentFileTabTitle(fname);
        }
      } catch (IOException eio) {
        eio.printStackTrace();
      }
    }

    public void doExport(ActionEvent ae) {
      try {
        EditorPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
        String fname = codePane.exportAsZip();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    public void doCloseTab(ActionEvent ae) {
      EditorPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
      try {
        if (codePane.close()) {
          _mainPane.remove(_mainPane.getSelectedIndex());
        }
      } catch (IOException e) {
        Debug.info("Can't close this tab: " + e.getStackTrace());
      }
      codePane = SikuliIDE.getInstance().getCurrentCodePane();
      if (codePane != null) {
        codePane.requestFocus();
      } else if (ae != null) {
        (new FileAction()).doNew(null);
      }
    }
  }

  public void showPreferencesWindow() {
    PreferencesWin pwin = new PreferencesWin();
    pwin.setAlwaysOnTop(true);
    pwin.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		if (!Settings.isJava7()) {
			pwin.setLocation(SikuliIDE.getInstance().getLocation());
		}
    pwin.setVisible(true);
  }

  public boolean closeCurrentTab() {
    EditorPane pane = getCurrentCodePane();
    (new FileAction()).doCloseTab(null);
    if (pane == getCurrentCodePane()) {
      return false;
    }
    return true;
  }

  protected boolean quit() {
    SikuliIDE ide = SikuliIDE.getInstance();
    (new FileAction()).doQuit(null);
    if (ide.getCurrentCodePane() == null) {
      return true;
    } else {
      return false;
    }
  }

  protected boolean checkDirtyPanes() {
    for (int i = 0; i < _mainPane.getTabCount(); i++) {
      try {
        JScrollPane scrPane = (JScrollPane) _mainPane.getComponentAt(i);
        EditorPane codePane = (EditorPane) scrPane.getViewport().getView();
        if (codePane.isDirty()) {
          //RaiMan not used: getRootPane().putClientProperty("Window.documentModified", true);
          return true;
        }
      } catch (Exception e) {
        Debug.error("checkDirtyPanes: " + e.getMessage());
      }
    }
    //RaiMan not used: getRootPane().putClientProperty("Window.documentModified", false);
    return false;

  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Init EditMenu">
  private void initEditMenu() throws NoSuchMethodException {
    int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    _editMenu.setMnemonic(java.awt.event.KeyEvent.VK_E);
    JMenuItem undoItem = _editMenu.add(_undoAction);
    undoItem.setAccelerator(
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, scMask));
    JMenuItem redoItem = _editMenu.add(_redoAction);
    redoItem.setAccelerator(
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, scMask | InputEvent.SHIFT_MASK));
    _editMenu.addSeparator();

    _editMenu.add(createMenuItem(_I("menuEditCut"),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, scMask),
            new EditAction(EditAction.CUT)));
    _editMenu.add(createMenuItem(_I("menuEditCopy"),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, scMask),
            new EditAction(EditAction.COPY)));
    _editMenu.add(createMenuItem(_I("menuEditPaste"),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, scMask),
            new EditAction(EditAction.PASTE)));
    _editMenu.add(createMenuItem(_I("menuEditSelectAll"),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, scMask),
            new EditAction(EditAction.SELECT_ALL)));
    _editMenu.addSeparator();

    JMenu findMenu = new JMenu(_I("menuFind"));
    _findHelper = new FindAction();
    findMenu.setMnemonic(KeyEvent.VK_F);
    findMenu.add(createMenuItem(_I("menuFindFind"),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, scMask),
            new FindAction(FindAction.FIND)));
    findMenu.add(createMenuItem(_I("menuFindFindNext"),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, scMask),
            new FindAction(FindAction.FIND_NEXT)));
    findMenu.add(createMenuItem(_I("menuFindFindPrev"),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, scMask | InputEvent.SHIFT_MASK),
            new FindAction(FindAction.FIND_PREV)));
    _editMenu.add(findMenu);
    _editMenu.addSeparator();
    _editMenu.add(createMenuItem(_I("menuEditIndent"),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_TAB, 0),
            new EditAction(EditAction.INDENT)));
    _editMenu.add(createMenuItem(_I("menuEditUnIndent"),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_TAB, InputEvent.SHIFT_MASK),
            new EditAction(EditAction.UNINDENT)));
  }

  class EditAction extends MenuAction {

    static final String CUT = "doCut";
    static final String COPY = "doCopy";
    static final String PASTE = "doPaste";
    static final String SELECT_ALL = "doSelectAll";
    static final String INDENT = "doIndent";
    static final String UNINDENT = "doUnindent";

    public EditAction() {
      super();
    }

    public EditAction(String item) throws NoSuchMethodException {
      super(item);
    }

    private void performEditorAction(String action, ActionEvent ae) {
      SikuliIDE ide = SikuliIDE.getInstance();
      EditorPane pane = ide.getCurrentCodePane();
      pane.getActionMap().get(action).actionPerformed(ae);
    }

    public void doCut(ActionEvent ae) {
      performEditorAction(DefaultEditorKit.cutAction, ae);
    }

    public void doCopy(ActionEvent ae) {
      performEditorAction(DefaultEditorKit.copyAction, ae);
    }

    public void doPaste(ActionEvent ae) {
      performEditorAction(DefaultEditorKit.pasteAction, ae);
    }

    public void doSelectAll(ActionEvent ae) {
      performEditorAction(DefaultEditorKit.selectAllAction, ae);
    }

    public void doIndent(ActionEvent ae) {
      SikuliIDE ide = SikuliIDE.getInstance();
      EditorPane pane = ide.getCurrentCodePane();
      (new EditorKit.InsertTabAction()).actionPerformed(pane);
    }

    public void doUnindent(ActionEvent ae) {
      SikuliIDE ide = SikuliIDE.getInstance();
      EditorPane pane = ide.getCurrentCodePane();
      (new EditorKit.DeindentAction()).actionPerformed(pane);
    }
  }

  class FindAction extends MenuAction {

    static final String FIND = "doFind";
    static final String FIND_NEXT = "doFindNext";
    static final String FIND_PREV = "doFindPrev";

    public FindAction() {
      super();
    }

    public FindAction(String item) throws NoSuchMethodException {
      super(item);
    }

    public void doFind(ActionEvent ae) {
      _searchField.selectAll();
      _searchField.requestFocus();
    }

    public void doFindNext(ActionEvent ae) {
      findNext(_searchField.getText());
    }

    public void doFindPrev(ActionEvent ae) {
      findPrev(_searchField.getText());
    }

    private boolean _find(String str, int begin, boolean forward) {
      EditorPane codePane = getCurrentCodePane();
      int pos = codePane.search(str, begin, forward);
      Debug.log(7, "find \"" + str + "\" at " + begin + ", found: " + pos);
      if (pos < 0) {
        return false;
      }
      return true;
    }

    public boolean findStr(String str) {
      if (getCurrentCodePane() != null) {
        return _find(str, getCurrentCodePane().getCaretPosition(), true);
      }
      return false;
    }

    public boolean findPrev(String str) {
      if (getCurrentCodePane() != null) {
        return _find(str, getCurrentCodePane().getCaretPosition(), false);
      }
      return false;
    }

    public boolean findNext(String str) {
      if (getCurrentCodePane() != null) {
        return _find(str,
                getCurrentCodePane().getCaretPosition() + str.length(),
                true);
      }
      return false;
    }

    public void setFailed(boolean failed) {
      Debug.log(7, "search failed: " + failed);
      _searchField.setBackground(Color.white);
      if (failed) {
        _searchField.setForeground(COLOR_SEARCH_FAILED);
      } else {
        _searchField.setForeground(COLOR_SEARCH_NORMAL);
      }
    }
  }

  class UndoAction extends AbstractAction {

    public UndoAction() {
      super(_I("menuEditUndo"));
      setEnabled(false);
    }

    public void updateUndoState() {
      if (getCurrentCodePane() != null
              && getCurrentCodePane().getUndoManager().canUndo()) {
        setEnabled(true);
      } else {
        setEnabled(false);
      }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      EditorUndoManager undo = getCurrentCodePane().getUndoManager();
      try {
        undo.undo();
      } catch (CannotUndoException ex) {
      }
      updateUndoState();
      _redoAction.updateRedoState();
    }
  }

  class RedoAction extends AbstractAction {

    public RedoAction() {
      super(_I("menuEditRedo"));
      setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      EditorUndoManager undo = getCurrentCodePane().getUndoManager();
      try {
        undo.redo();
      } catch (CannotRedoException ex) {
      }
      updateRedoState();
      _undoAction.updateUndoState();
    }

    protected void updateRedoState() {
      if (getCurrentCodePane() != null
              && getCurrentCodePane().getUndoManager().canRedo()) {
        setEnabled(true);
      } else {
        setEnabled(false);
      }
    }
  }

  public void updateUndoRedoStates() {
    _undoAction.updateUndoState();
    _redoAction.updateRedoState();
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Init Run Menu">
  private void initRunMenu() throws NoSuchMethodException {
    int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    _runMenu.setMnemonic(java.awt.event.KeyEvent.VK_R);
    _runMenu.add(createMenuItem(_I("menuRunRun"),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, scMask),
            new RunAction(RunAction.RUN)));
    _runMenu.add(createMenuItem(_I("menuRunRunAndShowActions"),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R,
            InputEvent.ALT_MASK | scMask),
            new RunAction(RunAction.RUN_SHOW_ACTIONS)));

    PreferencesUser pref = PreferencesUser.getInstance();
    JMenuItem stopItem = createMenuItem(_I("menuRunStop"),
            KeyStroke.getKeyStroke(
            pref.getStopHotkey(), pref.getStopHotkeyModifiers()),
            new RunAction(RunAction.RUN_SHOW_ACTIONS));
    stopItem.setEnabled(false);
    _runMenu.add(stopItem);
  }

  class RunAction extends MenuAction {

    static final String RUN = "run";
    static final String RUN_SHOW_ACTIONS = "runShowActions";

    public RunAction() {
      super();
    }

    public RunAction(String item) throws NoSuchMethodException {
      super(item);
    }

    public void run(ActionEvent ae) {
      doRun(_btnRun);
    }

    public void runShowActions(ActionEvent ae) {
      doRun(_btnRunViz);
    }

    private void doRun(ButtonRun btn) {
      btn.runCurrentScript();
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Init View Menu">
  private void initViewMenu() throws NoSuchMethodException {
    int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    _viewMenu.setMnemonic(java.awt.event.KeyEvent.VK_V);
    /* RaiMan not used
     _chkShowUnitTest = new JCheckBoxMenuItem(_I("menuViewUnitTest"));
     _viewMenu.add(createMenuItem(_chkShowUnitTest,
     KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, scMask),
     new ViewAction(ViewAction.UNIT_TEST)));
     * RaiMan not used */

    JMenuItem chkShowCmdList = new JCheckBoxMenuItem(_I("menuViewCommandList"), true);
    _viewMenu.add(createMenuItem(chkShowCmdList,
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, scMask),
            new ViewAction(ViewAction.CMD_LIST)));
//TODO Message Area show/hide
//TODO Message Area clear
//TODO Message Area bottom/right
//TODO Message Area LineBreak
  }

  class ViewAction extends MenuAction {

    static final String UNIT_TEST = "toggleUnitTest";
    static final String CMD_LIST = "toggleCmdList";

    public ViewAction() {
      super();
    }

    public ViewAction(String item) throws NoSuchMethodException {
      super(item);
    }

    public void toggleCmdList(ActionEvent ae) {
      _cmdList.setCollapsed(!_cmdList.isCollapsed());
    }

    public void toggleUnitTest(ActionEvent ae) {
      if (_chkShowUnitTest.getState()) {
        _sidePane.setCollapsed(false);
      } else {
        _sidePane.setCollapsed(true);
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Init ToolMenu">
  private void initToolMenu() throws NoSuchMethodException {
    int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    _toolMenu.setMnemonic(java.awt.event.KeyEvent.VK_T);

		if (Settings.SikuliRepo != null) {
			_toolMenu.add(createMenuItem(_I("menuToolExtensions"),
							null,
							new ToolAction(ToolAction.EXTENSIONS)));
		}
  }

  class ToolAction extends MenuAction {

    static final String EXTENSIONS = "extensions";

    public ToolAction() {
      super();
    }

    public ToolAction(String item) throws NoSuchMethodException {
      super(item);
    }

    public void extensions(ActionEvent ae) {
      showExtensionsFrame();
    }
  }

  public void showExtensionsFrame() {
    String warn = "You might proceed, if you\n" +
            "- have some programming skills\n" +
            "- read the docs about extensions\n" +
            "- know what you are doing\n\n" +
            "Otherwise you should press Cancel!";
    String title = "Need your attention!";
    String[] options = new String[3];
    options[WARNING_DO_NOTHING] = "OK";
    options[WARNING_ACCEPTED] = "Be quiet!";
    options[WARNING_CANCEL] = "Cancel";
    int ret = JOptionPane.showOptionDialog(this, warn, title, 0, JOptionPane.WARNING_MESSAGE, null, options, options[2]);
    if (ret == WARNING_CANCEL || ret == JOptionPane.CLOSED_OPTION) {
      return;
    }
    if (ret == WARNING_ACCEPTED) {
      //TODO set prefs to be quiet on extensions warning
    };
    ExtensionManagerFrame extmg = ExtensionManagerFrame.getInstance();
    extmg.setVisible(true);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Init Help Menu">
  private void initHelpMenu() throws NoSuchMethodException {
    int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    _helpMenu.setMnemonic(java.awt.event.KeyEvent.VK_H);

    //<editor-fold defaultstate="collapsed" desc="HelpMenu --- RaiMan not used">
		/*
     _helpMenu.add(createMenuItem(_I("menuHelpCheckUpdate"),
     null, new HelpAction(HelpAction.CHECK_UPDATE)));
     _helpMenu.addSeparator();
     */
    //</editor-fold>

    _helpMenu.add(createMenuItem(_I("menuHelpQuickStart"),
            null, new HelpAction(HelpAction.QUICK_START)));
    _helpMenu.addSeparator();

    _helpMenu.add(createMenuItem(_I("menuHelpGuide"),
            null, new HelpAction(HelpAction.OPEN_DOC)));
    _helpMenu.add(createMenuItem(_I("menuHelpDocumentations"),
            null, new HelpAction(HelpAction.OPEN_GUIDE)));
    _helpMenu.add(createMenuItem(_I("menuHelpFAQ"),
            null, new HelpAction(HelpAction.OPEN_FAQ)));
    _helpMenu.add(createMenuItem(_I("menuHelpAsk"),
            null, new HelpAction(HelpAction.OPEN_ASK)));
    _helpMenu.add(createMenuItem(_I("menuHelpBugReport"),
            null, new HelpAction(HelpAction.OPEN_BUG_REPORT)));

    //<editor-fold defaultstate="collapsed" desc="HelpMenu --- RaiMan not used">
		/*
     _helpMenu.add(createMenuItem(_I("menuHelpTranslation"),
     null, new HelpAction(HelpAction.OPEN_TRANSLATION)));
     */
    //</editor-fold>

    _helpMenu.addSeparator();
    _helpMenu.add(createMenuItem(_I("menuHelpHomepage"),
            null, new HelpAction(HelpAction.OPEN_HOMEPAGE)));
  }

  class HelpAction extends MenuAction {

    static final String CHECK_UPDATE = "doCheckUpdate";
    static final String QUICK_START = "openQuickStart";
    static final String OPEN_DOC = "openDoc";
    static final String OPEN_GUIDE = "openTutor";
    static final String OPEN_FAQ = "openFAQ";
    static final String OPEN_ASK = "openAsk";
    static final String OPEN_BUG_REPORT = "openBugReport";
    static final String OPEN_TRANSLATION = "openTranslation";
    static final String OPEN_HOMEPAGE = "openHomepage";

    public HelpAction() {
      super();
    }

    public HelpAction(String item) throws NoSuchMethodException {
      super(item);
    }

    public void openQuickStart(ActionEvent ae) {
      openURL("http://sikuli.org/");
    }

    public void openDoc(ActionEvent ae) {
      openURL("http://doc.sikuli.org");
    }

    public void openTutor(ActionEvent ae) {
      openURL("http://www.sikuli.org/videos.html");
    }

    public void openFAQ(ActionEvent ae) {
      openURL("https://answers.launchpad.net/sikuli/+faqs");
    }

    public void openAsk(ActionEvent ae) {
      openURL("https://answers.launchpad.net/sikuli");
    }

    public void openBugReport(ActionEvent ae) {
      openURL("https://bugs.launchpad.net/sikuli/+filebug");
    }

    public void openTranslation(ActionEvent ae) {
      openURL("https://translations.launchpad.net/sikuli/sikuli-x/+translations");
    }

    public void openHomepage(ActionEvent ae) {
      openURL("http://sikuli.org");
    }

    public boolean checkUpdate(boolean isAutoCheck) {
      AutoUpdater au = new AutoUpdater();
      PreferencesUser pref = PreferencesUser.getInstance();
      Debug.log("Check update");
      if (au.checkUpdate()) {
        String ver = au.getVersion();
        String details = au.getDetails();
        if (isAutoCheck && pref.getLastSeenUpdate().equals(ver)) {
          return false;
        }
        au.showUpdateFrame(_I("dlgUpdateAvailable", ver), details);
        PreferencesUser.getInstance().setLastSeenUpdate(ver);
        return true;
      }
      return false;
    }

    public void doCheckUpdate(ActionEvent ae) {
      if (!checkUpdate(false)) {
        JOptionPane.showMessageDialog(null,
                _I("msgNoUpdate"), "Sikuli " + SikuliIDESettings.SikuliVersion,
                JOptionPane.INFORMATION_MESSAGE);
      }
    }
  }

  public static void openURL(String url) {
    try {
      URL u = new URL(url);
      java.awt.Desktop.getDesktop().browse(u.toURI());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  //</editor-fold>

  private void initMenuBars(JFrame frame) {
    try {
      initFileMenu();
      initEditMenu();
      initRunMenu();
      initViewMenu();
      initToolMenu();
      initHelpMenu();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }

    _menuBar.add(_fileMenu);
    _menuBar.add(_editMenu);
    _menuBar.add(_runMenu);
    _menuBar.add(_viewMenu);
    _menuBar.add(_toolMenu);
    _menuBar.add(_helpMenu);
    frame.setJMenuBar(_menuBar);
  }

  //<editor-fold defaultstate="collapsed" desc="Init LeftBar Commands">
  private String[] getCommandCategories() {
    String[] CommandCategories = {
      _I("cmdListFind"),
      _I("cmdListMouse"),
      _I("cmdListKeyboard"),
      _I("cmdListObserver")
    };
    return CommandCategories;
  }

  private String[][] getCommandsOnToolbar() {
    String[][] CommandsOnToolbar = {
      {"find"}, {"PATTERN"},
      {_I("cmdFind")},
      {"findAll"}, {"PATTERN"},
      {_I("cmdFindAll")},
      {"wait"}, {"PATTERN", "[timeout]"},
      {_I("cmdWait")},
      {"waitVanish"}, {"PATTERN", "[timeout]"},
      {_I("cmdWaitVanish")},
      {"exists"}, {"PATTERN", "[timeout]"},
      {_I("cmdExists")},
      {"----"}, {}, {},
      {"click"}, {"PATTERN", "[modifiers]"},
      {_I("cmdClick")},
      {"doubleClick"}, {"PATTERN", "[modifiers]"},
      {_I("cmdDoubleClick")},
      {"rightClick"}, {"PATTERN", "[modifiers]"},
      {_I("cmdRightClick")},
      {"hover"}, {"PATTERN"},
      {_I("cmdHover")},
      {"dragDrop"}, {"PATTERN", "PATTERN", "[modifiers]"},
      {_I("cmdDragDrop")},
      /* RaiMan not used
       * {"drag"}, {"PATTERN"},
       * {"dropAt"}, {"PATTERN", "[delay]"},
       * RaiMan not used */
      {"----"}, {}, {},
      {"type"}, {"_text", "[modifiers]"},
      {_I("cmdType")},
      {"type"}, {"PATTERN", "_text", "[modifiers]"},
      {_I("cmdType2")},
      {"paste"}, {"_text", "[modifiers]"},
      {_I("cmdPaste")},
      {"paste"}, {"PATTERN", "_text", "[modifiers]"},
      {_I("cmdPaste2")},
      {"----"}, {}, {},
      {"onAppear"}, {"PATTERN", "_hnd"},
      {_I("cmdOnAppear")},
      {"onVanish"}, {"PATTERN", "_hnd"},
      {_I("cmdOnVanish")},
      {"onChange"}, {"_hnd"},
      {_I("cmdOnChange")},
      {"observe"}, {"[time]", "[background]"},
      {_I("cmdObserve")},};
    return CommandsOnToolbar;
  }

  private JComponent createCommandPane() {
    JXTaskPaneContainer con = new JXTaskPaneContainer();

    PreferencesUser pref = PreferencesUser.getInstance();
    JCheckBox chkAutoCapture =
            new JCheckBox(_I("cmdListAutoCapture"),
            pref.getAutoCaptureForCmdButtons());
    chkAutoCapture.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(javax.swing.event.ChangeEvent e) {
        boolean flag = ((JCheckBox) e.getSource()).isSelected();
        PreferencesUser pref = PreferencesUser.getInstance();
        pref.setAutoCaptureForCmdButtons(flag);
      }
    });
    JXTaskPane setPane = new JXTaskPane();
    setPane.setTitle(_I("cmdListSettings"));
    setPane.add(chkAutoCapture);
    setPane.setCollapsed(true);
    con.add(setPane);
    int cat = 0;
    JXTaskPane taskPane = new JXTaskPane();
    taskPane.setTitle(getCommandCategories()[cat++]);
    con.add(taskPane);
    String[][] CommandsOnToolbar = getCommandsOnToolbar();
    boolean collapsed;
    for (int i = 0; i < CommandsOnToolbar.length; i++) {
      String cmd = CommandsOnToolbar[i++][0];
      String[] params = CommandsOnToolbar[i++];
      String[] desc = CommandsOnToolbar[i];
//TODO: more elegeant way, to handle special cases
      if (cmd.equals("----")) {
        if (cat == 2) {
          collapsed = true;
        } else {
          collapsed = false;
        }
        if (cat == 3) {
          if (prefs.getUserType() == PreferencesUser.NEWBEE) {
            break;
          } else {
            collapsed = true;
          }
        }
        taskPane = new JXTaskPane();
        taskPane.setTitle(getCommandCategories()[cat++]);
        con.add(taskPane);
        taskPane.setCollapsed(collapsed);
      } else {
        taskPane.add(new ButtonGenCommand(cmd, desc[0], params));
      }
    }
    Dimension conDim = con.getSize();
    con.setPreferredSize(new Dimension(250, 1000));
    _cmdList = new JXCollapsiblePane(JXCollapsiblePane.Direction.LEFT);
    _cmdList.setMinimumSize(new Dimension(0, 0));
    _cmdList.add(new JScrollPane(con));
    _cmdList.setCollapsed(false);
    return _cmdList;
  }

  //<editor-fold defaultstate="collapsed" desc="RaiMan obsolete">
  private JToolBar initCmdToolbar() {
    JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
    toolbar.add(createCommandPane());
    return toolbar;
  }
  //</editor-fold>
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Init ToolBar Buttons">
  private JToolBar initToolbar() {
    if (ENABLE_UNIFIED_TOOLBAR) {
      MacUtils.makeWindowLeopardStyle(this.getRootPane());
    }

    JToolBar toolbar = new JToolBar();
    JButton btnInsertImage = new ButtonInsertImage();
    _btnCapture = new ButtonCapture();
    JButton btnSubregion = new ButtonSubregion();
    toolbar.add(_btnCapture);
    toolbar.add(btnInsertImage);
    toolbar.add(btnSubregion);
    toolbar.add(Box.createHorizontalGlue());
    /* RaiMan not used
     * if( ENABLE_RECORDING ){
     * JToggleButton btnRecord = new ButtonRecord();
     * toolbar.add(btnRecord);
     * }
     * RaiMan not used */
    _btnRun = new ButtonRun();
    toolbar.add(_btnRun);
    _btnRunViz = new ButtonRunViz();
    toolbar.add(_btnRunViz);
    toolbar.add(Box.createHorizontalGlue());
    toolbar.add(createSearchField());
    toolbar.add(Box.createRigidArea(new Dimension(7, 0)));
    toolbar.setFloatable(false);
    //toolbar.setMargin(new Insets(0, 0, 0, 5));
    return toolbar;
  }

  class ButtonInsertImage extends ButtonOnToolbar implements ActionListener {

    public ButtonInsertImage() {
      super();
      URL imageURL = SikuliIDE.class.getResource("/icons/insert-image-icon.png");
      setIcon(new ImageIcon(imageURL));
      setText(SikuliIDE._I("btnInsertImageLabel"));
      //setMaximumSize(new Dimension(26,26));
      setToolTipText(SikuliIDE._I("btnInsertImageHint"));
      addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
      EditorPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
      File file = new SikuliIDEFileChooser(SikuliIDE.getInstance()).loadImage();
      if (file == null) {
        return;
      }
      String path = Utils.slashify(file.getAbsolutePath(), false);
      Debug.info("load image: " + path);
      PatternImageButton icon = new PatternImageButton(codePane,
              codePane.copyFileToBundle(path).getAbsolutePath());
      codePane.insertComponent(icon);
    }
  }

  class ButtonSubregion extends ButtonOnToolbar implements ActionListener, EventObserver {

    public ButtonSubregion() {
      super();
      URL imageURL = SikuliIDE.class.getResource("/icons/region-icon.png");
      setIcon(new ImageIcon(imageURL));
      setText(SikuliIDE._I("btnRegionLabel"));
      //setMaximumSize(new Dimension(26,26));
      setToolTipText(SikuliIDE._I("btnRegionHint"));
      addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
      SikuliIDE ide = SikuliIDE.getInstance();
      EditorPane codePane = ide.getCurrentCodePane();
      ide.setVisible(false);
      OverlayCapturePrompt prompt = new OverlayCapturePrompt(null, this);
      prompt.prompt(SikuliIDE._I("msgCapturePrompt"), 500);
    }

    @Override
    public void update(EventSubject s) {
      if (s instanceof OverlayCapturePrompt) {
        OverlayCapturePrompt cp = (OverlayCapturePrompt) s;
        ScreenImage r = cp.getSelection();
        cp.close();
        if (r != null) {
          Rectangle roi = r.getROI();
          complete((int) roi.getX(), (int) roi.getY(),
                  (int) roi.getWidth(), (int) roi.getHeight());
        }
        SikuliIDE.getInstance().setVisible(true);
      }
    }

    public void complete(int x, int y, int w, int h) {
      Debug.log(7, "Region: %d %d %d %d", x, y, w, h);
      SikuliIDE ide = SikuliIDE.getInstance();
      EditorPane codePane = ide.getCurrentCodePane();
      ide.setVisible(false);
      JButton icon = new ButtonRegion(codePane, x, y, w, h);
      codePane.insertComponent(icon);
      ide.setVisible(true);
      codePane.requestFocus();
    }
  }

  class ButtonRun extends ButtonOnToolbar implements ActionListener {

    private Thread _runningThread = null;

    public ButtonRun() {
      super();

      URL imageURL = SikuliIDE.class.getResource("/icons/run_big_green.png");
      setIcon(new ImageIcon(imageURL));
      initTooltip();
      addActionListener(this);
      setText(_I("btnRunLabel"));
      //setMaximumSize(new Dimension(45,45));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
      runCurrentScript();
    }

    public void runCurrentScript() {
      if (!SikuliIDE.getInstance().doBeforeRun()) {
        return;
      }
      _runningThread = new Thread() {
        @Override
        public void run() {
          EditorPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
          File tmpFile;
          try {
            tmpFile = File.createTempFile("sikuli-tmp", ".py");
            tmpFile.deleteOnExit();
            try {
              BufferedWriter bw = new BufferedWriter(
                      new OutputStreamWriter(
                      new FileOutputStream(tmpFile),
                      "UTF8"));
              codePane.write(bw);
              SikuliIDE.getInstance().setVisible(false);
              _console.clear();
              resetErrorMark();
              runPython(tmpFile);
            } catch (Exception e) {
              java.util.regex.Pattern p =
                      java.util.regex.Pattern.compile("SystemExit: ([0-9]+)");
              Matcher matcher = p.matcher(e.toString());
//TODO error stop I18N
              if (matcher.find()) {
                Debug.info(_I("msgExit", matcher.group(1)));
              } else {
                //Debug.error(_I("msgStopped"));
                findErrorSource(e, tmpFile.getAbsolutePath());
              }
            } finally {
              SikuliIDE.getInstance().setVisible(true);
              _runningThread = null;
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      };
      _runningThread.start();
    }

    protected void runPython(File f) throws Exception {
      String path = SikuliIDE.getInstance().getCurrentBundlePath();
      SikuliScriptRunner srunner = new SikuliScriptRunner(Settings.getArgs(), "IDE");
      addPythonCode(srunner);
      try {
        srunner.runPython(path, f);
        srunner.close();
      } catch (Exception e) {
        srunner.close();
        throw e;
      }
    }

    protected void addPythonCode(SikuliScriptRunner srunner) {
    }

    public void stopRunning() {
      if (_runningThread != null) {
        _runningThread.interrupt();
        _runningThread.stop();
      }
    }

    private void initTooltip() {
      PreferencesUser pref = PreferencesUser.getInstance();
      String strHotkey = Utils.convertKeyToText(
              pref.getStopHotkey(), pref.getStopHotkeyModifiers());
      String stopHint = _I("btnRunStopHint", strHotkey);
      setToolTipText(_I("btnRun", stopHint));
    }

    private void findErrorSource(Throwable thr, String filename) {
      String err = thr.toString();
//      Debug.error("------------- Traceback -------------\n" + err +
//              "------------- Traceback -------------\n");
      errorLine = -1;
      errorColumn = -1;
      errorClass = PY_UNKNOWN;
      errorType = "--UnKnown--";
      errorText = "--UnKnown--";

      String msg;
      Matcher mFile = null;

      if (err.startsWith("Traceback")) {
        Pattern pError = Pattern.compile(NL + "(.*?):.(.*)$");
        mFile = pFile.matcher(err);
        if (mFile.find()) {
          Debug.log(2, "Runtime error line: " + mFile.group(2)
                  + "\n in function: " + mFile.group(3)
                  + "\n statement: " + mFile.group(4));
          errorLine = Integer.parseInt(mFile.group(2));
          errorClass = PY_RUNTIME;
          Matcher mError = pError.matcher(err);
          if (mError.find()) {
            Debug.log(2, "Error:" + mError.group(1));
            Debug.log(2, "Error:" + mError.group(2));
            errorType = mError.group(1);
            errorText = mError.group(2);
          } else {
//org.sikuli.core.FindFailed: FindFailed: can not find 1352647716171.png on the screen
            Pattern pFF = Pattern.compile(": FindFailed: (.*?)" + NL);
            Matcher mFF = pFF.matcher(err);
            if (mFF.find()) {
              errorType = "FindFailed";
              errorText = mFF.group(1);
            } else {
              errorClass = PY_UNKNOWN;
            }
          }
        }
      } else if (err.startsWith("SyntaxError")) {
        Pattern pLineS = Pattern.compile(", (\\d+), (\\d+),");
        java.util.regex.Matcher mLine = pLineS.matcher(err);
        if (mLine.find()) {
          Debug.log(4, "SyntaxError error line: " + mLine.group(1));
          Pattern pText = Pattern.compile("\\((.*?)\\(");
          java.util.regex.Matcher mText = pText.matcher(err);
          mText.find();
          errorText = mText.group(1) == null ? errorText : mText.group(1);
          Debug.log(4, "SyntaxError: " + errorText);
          errorLine = Integer.parseInt(mLine.group(1));
          errorColumn = Integer.parseInt(mLine.group(2));
          errorClass = PY_SYNTAX;
          errorType = "SyntaxError";
        }
      }

      msg = "script [ " + _mainPane.getTitleAt(_mainPane.getSelectedIndex());
      if (errorLine != -1) {
        //Debug.error(_I("msgErrorLine", srcLine));
        msg += " ] stopped with error in line " + errorLine;
        if (errorColumn != -1) {
          msg += " at column " + errorColumn;
        }
        addErrorMark(errorLine);
      } else {
        msg += "] stopped with error at line --unknown--";
      }

      if (errorClass == PY_RUNTIME || errorClass == PY_SYNTAX) {
        Debug.error(msg);
        Debug.error(errorType + " ( " + errorText + " )");
        if (errorClass == PY_RUNTIME) {
          errorClass = findErrorSourceWalkTrace(mFile, filename);
          if (errorTrace.length() > 0) {
            Debug.error("--- Traceback --- error source first\n"
                    + "line: module ( function ) statement \n" + errorTrace);
          }
        }
      } else if (errorClass == PY_JAVA) {
      } else {
        Debug.error(msg);
        Debug.error("Could not evaluate error source nor reason. Analyze StackTrace!");
        Debug.error(err);
      }
    }

    private int findErrorSourceWalkTrace(Matcher m, String filename) {
//[error] Traceback (most recent call last):
//File "/var/folders/wk/pcty7jkx1r5bzc5dvs6n5x_40000gn/T/sikuli-tmp3464751893408897244.py", line 2, in
//sub.hello()
//File "/Users/rhocke/NetBeansProjects/RaiManSikuli2012-Script/sub.sikuli/sub.py", line 4, in hello
//print "hello from sub", 1/0
//ZeroDivisionError: integer division or modulo by zero
      Pattern pModule = Pattern.compile(".*/(.*?).py");
      //Matcher mFile = pFile.matcher(etext);
      String mod;
      String modIgnore = "SikuliImporter,";
      StringBuilder trace = new StringBuilder();
      String telem;
      while (m.find()) {
        if (m.group(1).equals(filename)) {
          mod = "main";
        } else {
          Matcher mModule = pModule.matcher(m.group(1));
          mModule.find();
          mod = mModule.group(1);
          if (modIgnore.contains(mod + ",")) {
            continue;
          }
        }
        telem = m.group(2) + ": " + mod + " ( "
                + m.group(3) + " ) " + m.group(4) + NL;
        //Debug.log(2,telem);
        trace.insert(0, telem);
//        Debug.log(2,"Rest of Trace ----\n" + etext.substring(mFile.end()));
      }
      Debug.log(2, "------------- Traceback -------------\n" + trace);
      errorTrace = trace.toString();
      return errorClass;
    }

    private void findErrorSourceFromJavaStackTrace(Throwable thr, String filename) {
      Debug.error("seems to be an error in the Java API supporting code");
      StackTraceElement[] s;
      Throwable t = thr;
      while (t != null) {
        s = t.getStackTrace();
        Debug.log(2, "stack trace:");
        for (int i = s.length - 1; i >= 0; i--) {
          StackTraceElement si = s[i];
          Debug.log(2, si.getLineNumber() + " " + si.getFileName());
          if (si.getLineNumber() >= 0 && filename.equals(si.getFileName())) {
            errorLine = si.getLineNumber();
          }
        }
        t = t.getCause();
        Debug.log(2, "cause: " + t);
      }
    }

    public void addErrorMark(int line) {
      JScrollPane scrPane = (JScrollPane) _mainPane.getSelectedComponent();
      EditorLineNumberView lnview = (EditorLineNumberView) (scrPane.getRowHeader().getView());
      lnview.addErrorMark(line);
      EditorPane codePane = SikuliIDE.this.getCurrentCodePane();
//			codePane.setErrorHighlight(line);
      try {
        codePane.jumpTo(line);
      } catch (BadLocationException be) {
      }
      codePane.requestFocus();
    }

    public void resetErrorMark() {
      JScrollPane scrPane = (JScrollPane) _mainPane.getSelectedComponent();
      EditorLineNumberView lnview = (EditorLineNumberView) (scrPane.getRowHeader().getView());
      lnview.resetErrorMark();
      EditorPane codePane = SikuliIDE.this.getCurrentCodePane();
//      codePane.setErrorHighlight(-1);
    }
  }

  class ButtonRunViz extends ButtonRun {

    public ButtonRunViz() {
      super();
      URL imageURL = SikuliIDE.class.getResource("/icons/run_big_yl.png");
      setIcon(new ImageIcon(imageURL));
      setToolTipText(_I("menuRunRunAndShowActions"));
      setText(_I("btnRunSlowMotionLabel"));
    }

    @Override
    protected void addPythonCode(SikuliScriptRunner srunner) {
      srunner.runSlowMotion();
    }
  }

  public String getCurrentBundlePath() {
    EditorPane pane = getCurrentCodePane();
    return pane.getSrcBundle();
  }

  //<editor-fold defaultstate="collapsed" desc="ButtonRecord --- RaiMan not used">
  class ButtonRecord extends JToggleButton implements ActionListener {

    public ButtonRecord() {
      super();
      URL imageURL = SikuliIDE.class.getResource("/icons/record.png");
      setIcon(new ImageIcon(imageURL));
      setMaximumSize(new Dimension(26, 26));
      setBorderPainted(false);
      setToolTipText("Record");
      addActionListener(this);
    }

    private void initSikuliGenerator() {
    }

    public void startSikuliGenerator() {
      try {
        String args[] = {"/tmp/sikuli-video.mov", "/tmp/sikuli-event.log"};

        //FIXME: test if this works..
        Class c = Class.forName("SikuliGenerator");
        Class[] t_params = {
          String[].class, EditorPane.class
        };
        Constructor constr = c.getConstructor(t_params);
        constr.newInstance(new Object[]{
                  args, getCurrentCodePane()
                });

        //				 SikuliGenerator sg = new SikuliGenerator(args,
        //				 getCurrentCodePane());
      } catch (Exception e) {
        Debug.error("Error in starting up SikuliGenerator...");
        e.printStackTrace();
      }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
      if (getModel().isSelected()) {
        Debug.info("start recording");
        Thread recordThread = new Thread() {
          @Override
          public void run() {
            Utils.runRecorder();
            Debug.info("recording completed");
            getModel().setSelected(false);
            startSikuliGenerator();
          }
        };
        recordThread.start();
      } else {
        Debug.info("stop recording...");
        Utils.stopRecorder();
      }
    }
  }
  //</editor-fold>

  private JComponent createSearchField() {
    /*
     * if(Utils.isMacOSX()){
     * _searchField = new JTextField();
     * _searchField.putClientProperty("JTextField.variant", "search");
     * }
     * else{
     * _searchField = new JXSearchField();
     * }
     */
    _searchField = new JXSearchField("Find");
    _searchField.setUseNativeSearchFieldIfPossible(true);
    //_searchField.setLayoutStyle(JXSearchField.LayoutStyle.MAC);
    _searchField.setMinimumSize(new Dimension(220, 30));
    _searchField.setPreferredSize(new Dimension(220, 30));
    _searchField.setMaximumSize(new Dimension(380, 30));
    _searchField.setMargin(new Insets(0, 3, 0, 3));

    _searchField.setCancelAction(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        getCurrentCodePane().requestFocus();
        _findHelper.setFailed(false);
      }
    });
    _searchField.setFindAction(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        //FIXME: On Linux the found selection disappears somehow
        if (!Settings.isLinux()) //HACK
        {
          _searchField.selectAll();
        }
        boolean ret = _findHelper.findNext(_searchField.getText());
        _findHelper.setFailed(!ret);
      }
    });
    _searchField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(java.awt.event.KeyEvent ke) {
        boolean ret;
        if (ke.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
          //FIXME: On Linux the found selection disappears somehow
          if (!Settings.isLinux()) //HACK
          {
            _searchField.selectAll();
          }
          ret = _findHelper.findNext(_searchField.getText());
        } else {
          ret = _findHelper.findStr(_searchField.getText());
        }
        _findHelper.setFailed(!ret);
      }
    });
    return _searchField;
  }
  //</editor-fold>

  private void initTabPane() {
    _mainPane = new CloseableTabbedPane();
    _mainPane.setUI(new AquaCloseableTabbedPaneUI());
    _mainPane.addCloseableTabbedPaneListener(
            new CloseableTabbedPaneListener() {
              @Override
              public boolean closeTab(int i) {
                try {
                  JScrollPane scrPane = (JScrollPane) _mainPane.getComponentAt(i);
                  EditorPane codePane = (EditorPane) scrPane.getViewport().getView();
                  int count = _mainPane.getComponentCount();
                  Debug.log(8, "close tab " + i + " n:" + _mainPane.getComponentCount());
                  boolean ret = codePane.close();
                  Debug.log(8, "after close tab n:" + _mainPane.getComponentCount());
                  if (ret && _mainPane.getTabCount() < 2) {
                    (new FileAction()).doNew(null);
                  }
                  return ret;
                } catch (Exception e) {
                  Debug.info("Can't close this tab: " + e.getStackTrace());
                  e.printStackTrace();
                  return false;
                }
              }
            });

    _mainPane.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(javax.swing.event.ChangeEvent e) {
        JTabbedPane tab = (JTabbedPane) e.getSource();
        int i = tab.getSelectedIndex();
        if (i >= 0) {
          SikuliIDE.this.setTitle(tab.getTitleAt(i));
        }
        updateUndoRedoStates();
      }
    });

  }

  private void initMsgPane(boolean atBottom) {
    msgPane = new JTabbedPane();
    _console = new EditorConsolePane();
    msgPane.addTab(_I("paneMessage"), _console);
    if (Settings.isWindows() || Settings.isLinux()) {
      msgPane.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
    }
    if (atBottom) {
      msgPane.setMinimumSize(new Dimension(0, (int) (_windowSize.height * 0.3)));
    } else {
      msgPane.setMinimumSize(new Dimension((int) (_windowSize.width * 0.4), 0));
    }
  }

  private SikuliIDEStatusBar initStatusbar() {
    _status = new SikuliIDEStatusBar();
    return _status;
  }

  private void initWindowListener() {
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        SikuliIDE.this.quit();
      }
    });
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        PreferencesUser.getInstance().setIdeSize(SikuliIDE.this.getSize());
      }

      @Override
      public void componentMoved(ComponentEvent e) {
        PreferencesUser.getInstance().setIdeLocation(SikuliIDE.this.getLocation());
      }
    });
  }

  private void initTooltip() {
    ToolTipManager tm = ToolTipManager.sharedInstance();
    tm.setDismissDelay(30000);
  }

  //<editor-fold defaultstate="collapsed" desc="Init ShortCuts HotKeys">
  private void nextTab() {
    int i = _mainPane.getSelectedIndex();
    int next = (i + 1) % _mainPane.getTabCount();
    _mainPane.setSelectedIndex(next);
  }

  private void prevTab() {
    int i = _mainPane.getSelectedIndex();
    int prev = (i - 1 + _mainPane.getTabCount()) % _mainPane.getTabCount();
    _mainPane.setSelectedIndex(prev);
  }

  private void initShortcutKeys() {
    final int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
      private boolean isKeyNextTab(java.awt.event.KeyEvent ke) {
        if (ke.getKeyCode() == java.awt.event.KeyEvent.VK_TAB
                && ke.getModifiers() == InputEvent.CTRL_MASK) {
          return true;
        }
        if (ke.getKeyCode() == java.awt.event.KeyEvent.VK_CLOSE_BRACKET
                && ke.getModifiers() == (InputEvent.META_MASK | InputEvent.SHIFT_MASK)) {
          return true;
        }
        return false;
      }

      private boolean isKeyPrevTab(java.awt.event.KeyEvent ke) {
        if (ke.getKeyCode() == java.awt.event.KeyEvent.VK_TAB
                && ke.getModifiers() == (InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK)) {
          return true;
        }
        if (ke.getKeyCode() == java.awt.event.KeyEvent.VK_OPEN_BRACKET
                && ke.getModifiers() == (InputEvent.META_MASK | InputEvent.SHIFT_MASK)) {
          return true;
        }
        return false;
      }

      public void eventDispatched(AWTEvent e) {
        java.awt.event.KeyEvent ke = (java.awt.event.KeyEvent) e;
        //Debug.log(ke.toString());
        if (ke.getID() == java.awt.event.KeyEvent.KEY_PRESSED) {
          if (isKeyNextTab(ke)) {
            nextTab();
          } else if (isKeyPrevTab(ke)) {
            prevTab();
          }
        }
      }
    }, AWTEvent.KEY_EVENT_MASK);

  }

  public void removeCaptureHotkey(int key, int mod) {
    HotkeyManager.getInstance()._removeHotkey(key, mod);
  }

  public void installCaptureHotkey(int key, int mod) {
    HotkeyManager.getInstance()._addHotkey(key, mod, new HotkeyListener() {
      @Override
      public void hotkeyPressed(HotkeyEvent e) {
        onQuickCapture();
      }
    });
  }

  public void onQuickCapture() {
    onQuickCapture(null);
  }

  public void onQuickCapture(String arg) {
    Debug.log(2, "QuickCapture");
    _btnCapture.capture(0);
  }

  public void removeStopHotkey(int key, int mod) {
    HotkeyManager.getInstance()._removeHotkey(key, mod);
  }

  public void installStopHotkey(int key, int mod) {
    HotkeyManager.getInstance()._addHotkey(key, mod, new HotkeyListener() {
      @Override
      public void hotkeyPressed(HotkeyEvent e) {
        onStopRunning();
      }
    });
  }

  public void onStopRunning() {
    Debug.log(2, "StopRunning");
    this.setVisible(true);
    _btnRun.stopRunning();
    _btnRunViz.stopRunning();
  }

  private void initHotkeys() {
    PreferencesUser pref = PreferencesUser.getInstance();
    int key = pref.getCaptureHotkey();
    int mod = pref.getCaptureHotkeyModifiers();
    installCaptureHotkey(key, mod);

    key = pref.getStopHotkey();
    mod = pref.getStopHotkeyModifiers();
    installStopHotkey(key, mod);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="IDE Unit Testing --- RaiMan not used">
  private void initSidePane() {
    initUnitPane();
    _sidePane = new JXCollapsiblePane(JXCollapsiblePane.Direction.RIGHT);
    _sidePane.setMinimumSize(new Dimension(0, 0));
    CloseableTabbedPane tabPane = new CloseableTabbedPane();
    _sidePane.getContentPane().add(tabPane);
    tabPane.setMinimumSize(new Dimension(0, 0));
    tabPane.addTab(_I("tabUnitTest"), _unitPane);
    tabPane.addCloseableTabbedPaneListener(new CloseableTabbedPaneListener() {
      public boolean closeTab(int tabIndexToClose) {
        _sidePane.setCollapsed(true);
        _chkShowUnitTest.setState(false);
        return false;
      }
    });
    _sidePane.setCollapsed(true);
  }

  private void initUnitPane() {
    _testRunner = new UnitTestRunner();
    _unitPane = _testRunner.getPanel();
    _chkShowUnitTest.setState(false);
    addAuxTab(_I("paneTestTrace"), _testRunner.getTracePane());
  }

  public void addAuxTab(String tabName, JComponent com) {
    msgPane.addTab(tabName, com);
  }

  public void jumpTo(String funcName) throws BadLocationException {
    EditorPane pane = SikuliIDE.getInstance().getCurrentCodePane();
    pane.jumpTo(funcName);
    pane.grabFocus();
  }

  public void jumpTo(int lineNo) throws BadLocationException {
    EditorPane pane = SikuliIDE.getInstance().getCurrentCodePane();
    pane.jumpTo(lineNo);
    pane.grabFocus();
  }
  //</editor-fold>
}