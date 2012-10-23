/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.utility;

public interface Subject {

  public void addObserver(Observer o);

  public void notifyObserver();
}
