/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

public class Key {

  //<editor-fold defaultstate="collapsed" desc="KeyNames to UniCode">
  public static final String SPACE = " ";
  public static final String ENTER = "\n";
  public static final String BACKSPACE = "\b";
  public static final String TAB = "\t";
  public static final String ESC = "\u001b";
  public static final char C_ESC = '\u001b';
  public static final String UP = "\ue000";
  public static final char C_UP = '\ue000';
  public static final String RIGHT = "\ue001";
  public static final char C_RIGHT = '\ue001';
  public static final String DOWN = "\ue002";
  public static final char C_DOWN = '\ue002';
  public static final String LEFT = "\ue003";
  public static final char C_LEFT = '\ue003';
  public static final String PAGE_UP = "\ue004";
  public static final char C_PAGE_UP = '\ue004';
  public static final String PAGE_DOWN = "\ue005";
  public static final char C_PAGE_DOWN = '\ue005';
  public static final String DELETE = "\ue006";
  public static final char C_DELETE = '\ue006';
  public static final String END = "\ue007";
  public static final char C_END = '\ue007';
  public static final String HOME = "\ue008";
  public static final char C_HOME = '\ue008';
  public static final String INSERT = "\ue009";
  public static final char C_INSERT = '\ue009';
  public static final String F1 = "\ue011";
  public static final char C_F1 = '\ue011';
  public static final String F2 = "\ue012";
  public static final char C_F2 = '\ue012';
  public static final String F3 = "\ue013";
  public static final char C_F3 = '\ue013';
  public static final String F4 = "\ue014";
  public static final char C_F4 = '\ue014';
  public static final String F5 = "\ue015";
  public static final char C_F5 = '\ue015';
  public static final String F6 = "\ue016";
  public static final char C_F6 = '\ue016';
  public static final String F7 = "\ue017";
  public static final char C_F7 = '\ue017';
  public static final String F8 = "\ue018";
  public static final char C_F8 = '\ue018';
  public static final String F9 = "\ue019";
  public static final char C_F9 = '\ue019';
  public static final String F10 = "\ue01A";
  public static final char C_F10 = '\ue01A';
  public static final String F11 = "\ue01B";
  public static final char C_F11 = '\ue01B';
  public static final String F12 = "\ue01C";
  public static final char C_F12 = '\ue01C';
  public static final String F13 = "\ue01D";
  public static final char C_F13 = '\ue01D';
  public static final String F14 = "\ue01E";
  public static final char C_F14 = '\ue01E';
  public static final String F15 = "\ue01F";
  public static final char C_F15 = '\ue01F';
  public static final String SHIFT = "\ue020";
  public static final char C_SHIFT = '\ue020';
  public static final String CTRL = "\ue021";
  public static final char C_CTRL = '\ue021';
  public static final String ALT = "\ue022";
  public static final char C_ALT = '\ue022';
  public static final String META = "\ue023";
  public static final char C_META = '\ue023';
  public static final String CMD = "\ue023";
  public static final char C_CMD = '\ue023';
  public static final String WIN = "\ue023";
  public static final char C_WIN = '\ue023';
  public static final String PRINTSCREEN = "\ue024";
  public static final char C_PRINTSCREEN = '\ue024';
  public static final String SCROLL_LOCK = "\ue025";
  public static final char C_SCROLL_LOCK = '\ue025';
  public static final String PAUSE = "\ue026";
  public static final char C_PAUSE = '\ue026';
  public static final String CAPS_LOCK = "\ue027";
  public static final char C_CAPS_LOCK = '\ue027';
  public static final String NUM0 = "\ue030";
  public static final char C_NUM0 = '\ue030';
  public static final String NUM1 = "\ue031";
  public static final char C_NUM1 = '\ue031';
  public static final String NUM2 = "\ue032";
  public static final char C_NUM2 = '\ue032';
  public static final String NUM3 = "\ue033";
  public static final char C_NUM3 = '\ue033';
  public static final String NUM4 = "\ue034";
  public static final char C_NUM4 = '\ue034';
  public static final String NUM5 = "\ue035";
  public static final char C_NUM5 = '\ue035';
  public static final String NUM6 = "\ue036";
  public static final char C_NUM6 = '\ue036';
  public static final String NUM7 = "\ue037";
  public static final char C_NUM7 = '\ue037';
  public static final String NUM8 = "\ue038";
  public static final char C_NUM8 = '\ue038';
  public static final String NUM9 = "\ue039";
  public static final char C_NUM9 = '\ue039';
  public static final String SEPARATOR = "\ue03A";
  public static final char C_SEPARATOR = '\ue03A';
  public static final String NUM_LOCK = "\ue03B";
  public static final char C_NUM_LOCK = '\ue03B';
  public static final String ADD = "\ue03C";
  public static final char C_ADD = '\ue03C';
  public static final String MINUS = "\ue03D";
  public static final char C_MINUS = '\ue03D';
  public static final String MULTIPLY = "\ue03E";
  public static final char C_MULTIPLY = '\ue03E';
  public static final String DIVIDE = "\ue03F";
  public static final char C_DIVIDE = '\ue03F';
  //</editor-fold>

  /**
   * Convert Sikuli Key to Java virtual key code
   */
  protected static int[] toJavaKeyCode(String key) {
    if (key.length() > 0) {
      return toJavaKeyCode(key.charAt(0));
    }
    return null;
  }

  /**
   * Convert Sikuli Key to Java virtual key code
   */
  protected static int[] toJavaKeyCode(char key) {
    switch (key) {
//Lowercase
      case 'a': return new int[]{KeyEvent.VK_A};
      case 'b': return new int[]{KeyEvent.VK_B};
      case 'c': return new int[]{KeyEvent.VK_C};
      case 'd': return new int[]{KeyEvent.VK_D};
      case 'e': return new int[]{KeyEvent.VK_E};
      case 'f': return new int[]{KeyEvent.VK_F};
      case 'g': return new int[]{KeyEvent.VK_G};
      case 'h': return new int[]{KeyEvent.VK_H};
      case 'i': return new int[]{KeyEvent.VK_I};
      case 'j': return new int[]{KeyEvent.VK_J};
      case 'k': return new int[]{KeyEvent.VK_K};
      case 'l': return new int[]{KeyEvent.VK_L};
      case 'm': return new int[]{KeyEvent.VK_M};
      case 'n': return new int[]{KeyEvent.VK_N};
      case 'o': return new int[]{KeyEvent.VK_O};
      case 'p': return new int[]{KeyEvent.VK_P};
      case 'q': return new int[]{KeyEvent.VK_Q};
      case 'r': return new int[]{KeyEvent.VK_R};
      case 's': return new int[]{KeyEvent.VK_S};
      case 't': return new int[]{KeyEvent.VK_T};
      case 'u': return new int[]{KeyEvent.VK_U};
      case 'v': return new int[]{KeyEvent.VK_V};
      case 'w': return new int[]{KeyEvent.VK_W};
      case 'x': return new int[]{KeyEvent.VK_X};
      case 'y': return new int[]{KeyEvent.VK_Y};
      case 'z': return new int[]{KeyEvent.VK_Z};
//Uppercase
      case 'A': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_A};
      case 'B': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_B};
      case 'C': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_C};
      case 'D': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_D};
      case 'E': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_E};
      case 'F': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_F};
      case 'G': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_G};
      case 'H': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_H};
      case 'I': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_I};
      case 'J': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_J};
      case 'K': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_K};
      case 'L': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_L};
      case 'M': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_M};
      case 'N': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_N};
      case 'O': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_O};
      case 'P': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_P};
      case 'Q': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Q};
      case 'R': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_R};
      case 'S': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_S};
      case 'T': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_T};
      case 'U': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_U};
      case 'V': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_V};
      case 'W': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_W};
      case 'X': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_X};
      case 'Y': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Y};
      case 'Z': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Z};
//Row 3 (below function keys)
      case '1': return new int[]{KeyEvent.VK_1};
      case '!': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_1};
      case '2': return new int[]{KeyEvent.VK_2};
      case '@': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_2};
      case '3': return new int[]{KeyEvent.VK_3};
      case '#': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_3};
      case '4': return new int[]{KeyEvent.VK_4};
      case '$': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_4};
      case '5': return new int[]{KeyEvent.VK_5};
      case '%': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_5};
      case '6': return new int[]{KeyEvent.VK_6};
      case '^': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_6};
      case '7': return new int[]{KeyEvent.VK_7};
      case '&': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_7};
      case '8': return new int[]{KeyEvent.VK_8};
      case '*': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_8};
      case '9': return new int[]{KeyEvent.VK_9};
      case '(': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_9};
      case '0': return new int[]{KeyEvent.VK_0};
      case ')': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_0};
      case '-': return new int[]{KeyEvent.VK_MINUS};
      case '_': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_MINUS};
      case '=': return new int[]{KeyEvent.VK_EQUALS};
      case '+': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_EQUALS};
//Row 2
// q w e r t y u i o p
      case '[': return new int[]{KeyEvent.VK_OPEN_BRACKET};
      case '{': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET};
      case ']': return new int[]{KeyEvent.VK_CLOSE_BRACKET};
      case '}': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET};
//Row 1
// a s d f g h j k l
      case ';': return new int[]{KeyEvent.VK_SEMICOLON};
      case ':': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON};
      case '\'': return new int[]{KeyEvent.VK_QUOTE};
      case '"': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_QUOTE};
      case '\\': return new int[]{KeyEvent.VK_BACK_SLASH};
      case '|': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH};
//RETURN, BACKSPACE, TAB
      case '\b': return new int[]{KeyEvent.VK_BACK_SPACE};
      case '\t': return new int[]{KeyEvent.VK_TAB};
      case '\r': return new int[]{KeyEvent.VK_ENTER};
      case '\n': return new int[]{KeyEvent.VK_ENTER};
//SPACE
      case ' ': return new int[]{KeyEvent.VK_SPACE};
//Row 0 (first above SPACE)
      case '`': return new int[]{KeyEvent.VK_BACK_QUOTE};
      case '~': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE};
// z x c v b n m
      case ',': return new int[]{KeyEvent.VK_COMMA};
      case '<': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_COMMA};
      case '.': return new int[]{KeyEvent.VK_PERIOD};
      case '>': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_PERIOD};
      case '/': return new int[]{KeyEvent.VK_SLASH};
      case '?': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH};
//Modifier
      case Key.C_SHIFT: return new int[]{KeyEvent.VK_SHIFT};
      case Key.C_CTRL:  return new int[]{KeyEvent.VK_CONTROL};
      case Key.C_ALT:   return new int[]{KeyEvent.VK_ALT};
      case Key.C_META:  return new int[]{KeyEvent.VK_META};
//Cursor movement
      case Key.C_UP:     return new int[]{KeyEvent.VK_UP};
      case Key.C_RIGHT:     return new int[]{KeyEvent.VK_RIGHT};
      case Key.C_DOWN:      return new int[]{KeyEvent.VK_DOWN};
      case Key.C_LEFT:      return new int[]{KeyEvent.VK_LEFT};
      case Key.C_PAGE_UP:   return new int[]{KeyEvent.VK_PAGE_UP};
      case Key.C_PAGE_DOWN: return new int[]{KeyEvent.VK_PAGE_DOWN};
      case Key.C_END:       return new int[]{KeyEvent.VK_END};
      case Key.C_HOME:      return new int[]{KeyEvent.VK_HOME};
      case Key.C_DELETE:    return new int[]{KeyEvent.VK_DELETE};
//Function keys
      case Key.C_ESC: return new int[]{KeyEvent.VK_ESCAPE};
      case Key.C_F1:  return new int[]{KeyEvent.VK_F1};
      case Key.C_F2:  return new int[]{KeyEvent.VK_F2};
      case Key.C_F3:  return new int[]{KeyEvent.VK_F3};
      case Key.C_F4:  return new int[]{KeyEvent.VK_F4};
      case Key.C_F5:  return new int[]{KeyEvent.VK_F5};
      case Key.C_F6:  return new int[]{KeyEvent.VK_F6};
      case Key.C_F7:  return new int[]{KeyEvent.VK_F7};
      case Key.C_F8:  return new int[]{KeyEvent.VK_F8};
      case Key.C_F9:  return new int[]{KeyEvent.VK_F9};
      case Key.C_F10: return new int[]{KeyEvent.VK_F10};
      case Key.C_F11: return new int[]{KeyEvent.VK_F11};
      case Key.C_F12: return new int[]{KeyEvent.VK_F12};
      case Key.C_F13: return new int[]{KeyEvent.VK_F13};
      case Key.C_F14: return new int[]{KeyEvent.VK_F14};
      case Key.C_F15: return new int[]{KeyEvent.VK_F15};
//Toggling kezs
      case Key.C_SCROLL_LOCK: return new int[]{KeyEvent.VK_SCROLL_LOCK};
      case Key.C_NUM_LOCK:    return new int[]{KeyEvent.VK_NUM_LOCK};
      case Key.C_CAPS_LOCK:   return new int[]{KeyEvent.VK_CAPS_LOCK};
      case Key.C_INSERT:      return new int[]{KeyEvent.VK_INSERT};
//Windows special
      case Key.C_PAUSE:       return new int[]{KeyEvent.VK_PAUSE};
      case Key.C_PRINTSCREEN: return new int[]{KeyEvent.VK_PRINTSCREEN};
//Num pad
      case Key.C_NUM0: return new int[]{KeyEvent.VK_NUMPAD0};
      case Key.C_NUM1: return new int[]{KeyEvent.VK_NUMPAD1};
      case Key.C_NUM2: return new int[]{KeyEvent.VK_NUMPAD2};
      case Key.C_NUM3: return new int[]{KeyEvent.VK_NUMPAD3};
      case Key.C_NUM4: return new int[]{KeyEvent.VK_NUMPAD4};
      case Key.C_NUM5: return new int[]{KeyEvent.VK_NUMPAD5};
      case Key.C_NUM6: return new int[]{KeyEvent.VK_NUMPAD6};
      case Key.C_NUM7: return new int[]{KeyEvent.VK_NUMPAD7};
      case Key.C_NUM8: return new int[]{KeyEvent.VK_NUMPAD8};
      case Key.C_NUM9: return new int[]{KeyEvent.VK_NUMPAD9};
//Num pad special
      case Key.C_SEPARATOR: return new int[]{KeyEvent.VK_SEPARATOR};
      case Key.C_ADD:       return new int[]{KeyEvent.VK_ADD};
      case Key.C_MINUS:     return new int[]{KeyEvent.VK_MINUS};
      case Key.C_MULTIPLY:  return new int[]{KeyEvent.VK_MULTIPLY};
      case Key.C_DIVIDE:    return new int[]{KeyEvent.VK_DIVIDE};

      default:
        throw new IllegalArgumentException("Cannot convert character " + key);
    }
  }

  /**
   *
   * @param key
   * @return a printable version of a special key
   */
  protected static String toJavaKeyCodeText(char key) {
    switch (key) {
//RETURN, BACKSPACE, TAB
      case '\b': return "#BACK.";
      case '\t': return "#TAB.";
      case '\r': return "#ENTER.";
      case '\n': return "#ENTER.";
//Cursor movement
      case Key.C_UP: return "#UP.";
      case Key.C_RIGHT: return "#RIGHT.";
      case Key.C_DOWN: return "#DOWN.";
      case Key.C_LEFT: return "#LEFT.";
      case Key.C_PAGE_UP: return "#P_UP.";
      case Key.C_PAGE_DOWN: return "#P_DOWN.";
      case Key.C_END: return "#END.";
      case Key.C_HOME: return "#HOME.";
      case Key.C_DELETE: return "#DEL";
//Function keys
      case Key.C_ESC: return "#ESC.";
      case Key.C_F1: return "#F1.";
      case Key.C_F2: return "#F2.";
      case Key.C_F3: return "#F3.";
      case Key.C_F4: return "#F4.";
      case Key.C_F5: return "#F5.";
      case Key.C_F6: return "#F6.";
      case Key.C_F7: return "#F7.";
      case Key.C_F8: return "#F8.";
      case Key.C_F9: return "#F9.";
      case Key.C_F10: return "#F10.";
      case Key.C_F11: return "#F11.";
      case Key.C_F12: return "#F12.";
      case Key.C_F13: return "#F13.";
      case Key.C_F14: return "#F14.";
      case Key.C_F15: return "#F15.";
//Toggling kezs
      case Key.C_SCROLL_LOCK: return "#SCROLL_LOCK.";
      case Key.C_NUM_LOCK: return "#NUM_LOCK.";
      case Key.C_CAPS_LOCK: return "#CAPS_LOCK.";
      case Key.C_INSERT: return "#INS.";
//Windows special
      case Key.C_PAUSE: return "#PAUSE.";
      case Key.C_PRINTSCREEN: return "#PRINTSCREEN.";
//Num pad
      case Key.C_NUM0: return "#NUM0.";
      case Key.C_NUM1: return "#NUM1.";
      case Key.C_NUM2: return "#NUM2.";
      case Key.C_NUM3: return "#NUM3.";
      case Key.C_NUM4: return "#NUM4.";
      case Key.C_NUM5: return "#NUM5.";
      case Key.C_NUM6: return "#NUM6.";
      case Key.C_NUM7: return "#NUM7.";
      case Key.C_NUM8: return "#NUM8.";
      case Key.C_NUM9: return "#NUM9.";
//Num pad special
      case Key.C_SEPARATOR: return "#NSEP.";
      case Key.C_ADD: return "#NADD.";
      case Key.C_MINUS: return "#NSUB.";
      case Key.C_MULTIPLY: return "#NMUL";
      case Key.C_DIVIDE: return "#NDIV.";

      default:
        return "" + key;
    }
  }

  protected static int convertModifiers(String mod) {
    int modNew = 0;
    for (int i = 0; i < mod.length(); i++) {
      if (Key.CTRL.equals(mod.substring(i))) {
        modNew |= KeyModifier.CTRL;
      } else if (Key.ALT.equals(mod.substring(i))) {
        modNew |= KeyModifier.ALT;
      } else if (Key.SHIFT.equals(mod.substring(i))) {
        modNew |= KeyModifier.SHIFT;
      } else if (Key.CMD.equals(mod.substring(i))) {
        modNew |= KeyModifier.CMD;
      } else if (Key.META.equals(mod.substring(i))) {
        modNew |= KeyModifier.META;
      } else if (Key.WIN.equals(mod.substring(i))) {
        modNew |= KeyModifier.WIN;
      }
    }
    return modNew;
  }

  /**
   * get the lock state of the given key
   *
   * @param key
   * @return true/false
   */
  public static boolean isLockOn(char key) {
    Toolkit tk = Toolkit.getDefaultToolkit();
    switch (key) {
      case '\ue025':
        return tk.getLockingKeyState(KeyEvent.VK_SCROLL_LOCK);
      case '\ue027':
        return tk.getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
      case '\ue03B':
        return tk.getLockingKeyState(KeyEvent.VK_NUM_LOCK);
      default:
        return false;
    }
  }

  public static int getHotkeyModifier() {
    if (Settings.isMac()) {
      return KeyEvent.VK_META;
    } else {
      return KeyEvent.VK_CONTROL;
    }
  }

  public static boolean addHotkey(String key, int modifiers, HotkeyListener listener) {
    return HotkeyManager.getInstance().addHotkey(key, modifiers, listener);
  }

  public static boolean addHotkey(char key, int modifiers, HotkeyListener listener) {
    return HotkeyManager.getInstance().addHotkey(key, modifiers, listener);
  }

  public static boolean removeHotkey(String key, int modifiers) {
    return HotkeyManager.getInstance().removeHotkey(key, modifiers);
  }

  public static boolean removeHotkey(char key, int modifiers) {
    return HotkeyManager.getInstance().removeHotkey(key, modifiers);
  }
}
