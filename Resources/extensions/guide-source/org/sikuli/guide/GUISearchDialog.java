package org.sikuli.guide;
import java.util.ArrayList;


import org.sikuli.guide.SikuliGuideComponent.Layout;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Location;
import org.sikuli.script.Region;


public class GUISearchDialog extends SearchDialog{



   public GUISearchDialog(SikuliGuide guide) {
      super(guide, null);
   }

   protected void candidateEntriesUpdated(SikuliGuide guide, ArrayList<SearchEntry> candidateEntries){
      ArrayList<Region> regions = new ArrayList<Region>();
      for (SearchEntry entry : candidateEntries){
         regions.add(entry.region);
      }

      guide.clear();
      for (Region cr : regions){
         SikuliGuideCircle c = new SikuliGuideCircle(cr);
         guide.addToFront(c);
      }
      guide.repaint();
   }

   protected void candidateEntrySelected(SikuliGuide guide, ArrayList<SearchEntry> candidateEntries, SearchEntry e){
      ArrayList<Region> regions = new ArrayList<Region>();
      for (SearchEntry entry : candidateEntries){
         regions.add(entry.region);
      }

      Region r = e.region;

      guide.clear();
      for (Region cr : regions){
         SikuliGuideCircle c = new SikuliGuideCircle(cr);
         guide.addToFront(c);
      }
      SikuliGuideFlag flag = new SikuliGuideFlag("This");
      flag.setLocationRelativeToRegion(r, Layout.LEFT);
      guide.addToFront(flag);
      guide.startAnimation();
      guide.repaint();
   }


   protected void entrySelected(SearchEntry selectedEntry){

      Region r = selectedEntry.region;

      try {

         Location loc = r.getCenter();
         guide.getRegion().hover(loc);
         // TODO: this will fail because it's an event dispatch thread and waitIdle can not
         // be called by click()
      } catch (FindFailed e) {
      } catch (Exception e){
      }

   }



}


