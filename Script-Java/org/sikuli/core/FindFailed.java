/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.core;

import org.sikuli.utility.SikuliException;

public class FindFailed extends SikuliException {
   public FindFailed(String msg){
      super(msg);
      _name = "FindFailed";
   }
}

