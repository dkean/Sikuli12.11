/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.script;

public class SExAppNotFound extends SException {

  public SExAppNotFound(String msg) {
    super(msg);
    _name = "AppNotFound";
  }
}
