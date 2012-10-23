/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.system;

import org.sikuli.utility.SikuliException;

public class AppNotFound extends SikuliException {

  public AppNotFound(String msg) {
    super(msg);
    _name = "AppNotFound";
  }
}
