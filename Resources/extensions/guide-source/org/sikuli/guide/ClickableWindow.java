package org.sikuli.guide;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JPanel;

import org.sikuli.guide.SikuliGuideComponent.Layout;
import org.sikuli.script.Debug;
import org.sikuli.script.OverlayTransparentWindow;
import org.sikuli.script.Region;
import org.sikuli.script.Settings;

// TODO: Automatically move mouse cursor to the click target. The current implementation
// is problematic for non-rectangular clickable widgets, for instance, a round buttone.
// Since the highlighted region is always rectangular and is larger than the area that
// is actually cliable, users may click on the edge of the region and dismiss the window
// errorneously. This needs to be fixed.
public class ClickableWindow extends OverlayTransparentWindow
        implements MouseListener, Transition, GlobalMouseMotionListener {

  SikuliGuide guide;
  JPanel jp = null;
  ArrayList<SikuliGuideClickable> clickables = new ArrayList<SikuliGuideClickable>();
  private SikuliGuideClickable lastClicked;
  private Rectangle maxR;
  Point clickLocation;
  GlobalMouseMotionTracker mouseTracker;
  TransitionListener token;
  Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
  Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
  Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
  Cursor currentCursor = null;

  public ClickableWindow(SikuliGuide guide) {
    super(new Color(0.1f, 0f, 0f, 0.005f), null);
    this.guide = guide;

    jp = getJPanel();

    mouseTracker = GlobalMouseMotionTracker.getInstance();
    mouseTracker.addListener(this);

    setVisible(false);
    setFocusableWindowState(false);

    addMouseListener(this);

    addWindowListener(new WindowAdapter() {
      public void windowClosed(WindowEvent e) {
        // stop the global mouse tracker's timer thread
        mouseTracker.stop();
      }
    });
  }

  @Override
  public void toFront() {
    if (Settings.isMac()) {
      // this call is necessary to allow clicks to go through the window (ignoreMouse == true)
      if (Settings.JavaVersion < 7) {
        Settings.getOSUtil().bringWindowToFront(this, true);
      } else {
      }
    }
    super.toFront();
  }

  public void addClickable(SikuliGuideClickable c) {
    clickables.add(c);
    SikuliGuideClickable c1 = new SikuliGuideClickable(null);
    c1.setLocationRelativeToComponent(c, Layout.OVER);
    jp.add(c1);
  }

  public void addClickableRegion(Region region, String name) {
    SikuliGuideClickable c = new SikuliGuideClickable(region);
    c.setName(name);
    addClickable(c);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    Debug.log("[ClickableWindow] clicked on " + e.getX() + "," + e.getY());
    Point p = e.getPoint();
    lastClicked = null;
    for (SikuliGuideClickable c : clickables) {
      if (c.getActualBounds().contains(p)) {
        lastClicked = c;
        p.x -= c.getX();
        p.y -= c.getY();
        c.globalMouseClicked(p);
      }
    }
    if (lastClicked != null) {
      if (token != null) {
        setVisible(false);
        token.transitionOccurred(this);
      }
    }
  }

  //<editor-fold defaultstate="collapsed" desc="MouseEvents not used">
  @Override
  public void mouseEntered(MouseEvent arg0) {
  }

  @Override
  public void mouseExited(MouseEvent arg0) {
  }

  @Override
  public void mousePressed(MouseEvent arg0) {
  }

  @Override
  public void mouseReleased(MouseEvent arg0) {
  }

  //</editor-fold>

  @Override
  public String waitForTransition(TransitionListener token) {
    this.token = token;
    maxR = clickables.get(0).getBounds();
    if (clickables.size() > 1) {
      for (SikuliGuideClickable c : clickables.subList(1, clickables.size())) {
        maxR = maxR.union(c.getActualBounds());
      }
    }
    setBounds(maxR);
    setVisible(true);
    mouseTracker.start();
    return "Next";
  }

  @Override
  public void globalMouseMoved(int x, int y) {
    Point p = new Point(x, y);
    SikuliGuideClickable cc = null;
    for (SikuliGuideClickable c : clickables) {
      if (c.getBounds().contains(p)) {
        c.setMouseOver(true);
        cc = c;
      } else {
        c.setMouseOver(false);
      }
    }
//TODO keep moving to (0,0) to nullify the dragged move bug
    if (cc != null) {
      setLocation(0, 0);
      setSize(cc.getActualLocation().x+cc.getActualWidth(), cc.getActualLocation().y+cc.getActualHeight());
    } else {
      setBounds(maxR);
    }
  }

  @Override
  public void globalMouseIdled(int x, int y) {
  }

  public SikuliGuideClickable getLastClicked() {
    return lastClicked;
  }

  public ArrayList<SikuliGuideClickable> getClickables() {
    return clickables;
  }

  public void clear() {
    clickables.clear();
    getContentPane().removeAll();
  }
}