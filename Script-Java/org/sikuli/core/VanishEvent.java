/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.core;

import org.sikuli.core.Match;
import org.sikuli.core.Region;

public class VanishEvent extends SikuliEvent {
   public VanishEvent(Object ptn, Match m, Region r){
      super(ptn, m, r);
      type = Type.VANISH;
   }
}

