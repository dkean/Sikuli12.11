/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.utility;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import org.sikuli.script.Settings;
import org.sikuli.script.Debug;
import org.sikuli.script.FileManager;

public class AutoUpdater {

  private String version, details;

  public AutoUpdater() {}

  public String getVersion() {
    return version;
  }

  public String getDetails() {
    return details;
  }

  public boolean checkUpdate() {
    for (String s : Settings.ServerList) {
      try {
        if (checkUpdate(s)) {
          if (isNewer(version, Settings.SikuliVersion)) {
            return true;
          }
        }
      } catch (Exception e) {
        Debug.error("Can't get version info from " + s + "\n" + e);
      }
    }
    return false;
  }

  private boolean checkUpdate(String s) throws IOException, MalformedURLException {
    URL url = new URL(s);
    url.openConnection();
    URLConnection conn = url.openConnection();
    BufferedReader in = new BufferedReader(
            new InputStreamReader(conn.getInputStream()));
    String line;
    if ((line = in.readLine()) != null) {
      version = line.trim();
      details = "";
      while ((line = in.readLine()) != null) {
        details += line;
      }
      return true;
    }
    return false;
  }

  private boolean isNewer(String v1, String v2) {
    int rev1 = getRev(v1);
    int rev2 = getRev(v2);
    if (rev1 > 0 && rev2 > 0) {
      return rev1 > rev2;
    } else {
      return v1.compareTo(v2) > 0;
    }
  }

  private int getRev(String ver) {
    if (ver.matches("r\\d+")) {
      return Integer.parseInt(ver.substring(1));
    }
    int r_pos = ver.lastIndexOf("(r");
    if (r_pos > 0) {
      int end_pos = ver.lastIndexOf(")");
      return Integer.parseInt(ver.substring(r_pos + 2, end_pos));
    }
    return -1;
  }

  public void showUpdateFrame(String title, String text) {
    UpdateFrame f = new UpdateFrame(title, text);
  }
}

class UpdateFrame extends JFrame {
  public UpdateFrame(String title, String text) {
    setTitle(title);
    setSize(300, 200);
    setLocationRelativeTo(getRootPane());
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    final JEditorPane p = new JEditorPane("text/html", text);
    p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    p.setEditable(false);
    p.addHyperlinkListener(new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
          try {
            FileManager.openURL(e.getURL().toString());
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      }
    });
    cp.add(new JScrollPane(p), BorderLayout.CENTER);
    JPanel buttonPane = new JPanel();
    JButton btnOK = new JButton("ok");
    btnOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        UpdateFrame.this.dispose();
      }
    });
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
    buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
    buttonPane.add(Box.createHorizontalGlue());
    buttonPane.add(btnOK);
    buttonPane.add(Box.createHorizontalGlue());
    getRootPane().setDefaultButton(btnOK);

    cp.add(buttonPane, BorderLayout.PAGE_END);
    cp.doLayout();
    pack();

    setVisible(true);
    btnOK.requestFocus();
  }
}
