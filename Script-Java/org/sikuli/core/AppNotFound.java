/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.core;

import org.sikuli.core.SikuliException;

public class AppNotFound extends SikuliException {

  public AppNotFound(String msg) {
    super(msg);
    _name = "AppNotFound";
  }
}
