/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.core;

import org.sikuli.core.Match;
import org.sikuli.core.Region;
import java.util.List;

public class ChangeEvent extends SikuliEvent {
   public ChangeEvent(List<Match> results, Region r){
      type = Type.CHANGE;
      changes = results;
      region = r;
   }

   public String toString(){
      return String.format("ChangeEvent on %s | %d changes",
               region, changes.size());
   }
}
