package org.sikuli.guide;
import java.util.ArrayList;
import java.util.Enumeration;


import org.sikuli.guide.SikuliGuideComponent.Layout;
import org.sikuli.guide.model.GUIModel;
import org.sikuli.guide.model.GUINode;
import org.sikuli.script.Debug;
import org.sikuli.script.Match;
import org.sikuli.script.Screen;


public class TreeSearchDialog extends SearchDialog{

   GUIModel gui;

   class TreeSearchEntry extends SearchEntry{

      GUINode node;
      public TreeSearchEntry(String key, GUINode node){
         super(null,null);
         this.node = node;
         this.name = node.getName();
         this.key = node.getName();
      }

      // this determines what to be displayed in the list
      public String toString(){
         return node.getPathString();
      }
   }

   public TreeSearchDialog(SikuliGuide guide, GUIModel gui) {
      super(guide, null);
      this.gui = gui;

      GUINode r = (GUINode) gui.getRoot();
      Enumeration<GUINode> e = r.breadthFirstEnumeration();

      e.nextElement(); // skip root

      while (e.hasMoreElements()){
         GUINode node = (GUINode) e.nextElement();
         addEntry(new TreeSearchEntry(null, node));
      }
   }

   protected void candidateEntriesUpdated(SikuliGuide guide, ArrayList<SearchEntry> candidateEntries){
      for (SearchEntry se : candidateEntries){
         //TreeSearchEntry tse = (TreeSearchEntry) se;
         //Debug.info("e: " + tse.node);
      }

      if (candidateEntries.isEmpty()){
         guide.clear();
         guide.repaint();
      }
   }



   protected void candidateEntrySelected(SikuliGuide guide, ArrayList<SearchEntry> candidateEntries, SearchEntry e){

      guide.setVisible(false);
      repaint();

      Debug.info("selected: " + ((TreeSearchEntry) e).node);

      GUINode node =  ((TreeSearchEntry) e).node;

      Debug.info("trying to find: " + node);
      Match m = node.findOnScreen();

      if (m == null){

         Screen s = new Screen();
         GUINode ancestor = node.findAncestorOnScreen();
         m = ancestor.getMatch();

         node.drawPathFromAncestor(guide, ancestor);

         guide.setVisible(true);
         guide.repaint();
      }


//      if (m != null){
//         guide.setVisible(true);
//         guide.clear();
//         guide.addComponent(new SikuliGuideRectangle(guide,m));
//
//         SikuliGuideFlag flag = new SikuliGuideFlag(guide, "      ");
//         flag.setLocationRelativeToRegion(m, SikuliGuideComponent.LEFT);
//
//         guide.addComponent(flag);
//         guide.startAnimation();
//         guide.repaint();
//      }


   }

   protected void candidateEntrySelected1(SikuliGuide guide, ArrayList<SearchEntry> candidateEntries, SearchEntry e){

      guide.setVisible(false);
      repaint();

      Debug.info("selected: " + ((TreeSearchEntry) e).node);

      GUINode node =  ((TreeSearchEntry) e).node;

      Debug.info("trying to find: " + node);
      Match m = node.findOnScreen();

      if (m == null){

         Screen s = new Screen();
         GUINode ancestor = node.findAncestorOnScreen();
         m = ancestor.getMatch();

         try {
            guide.focusBelow();
            s.click(m,0);
         } catch (Exception e1) {
         }
         try {
            m = s.wait(node.getPattern());
         } catch (Exception e1) {
         }

         toFront();
         requestFocus();
      }


      if (m != null){
         guide.setVisible(true);
         guide.clear();
         guide.addToFront(new SikuliGuideRectangle(m));

         SikuliGuideFlag flag = new SikuliGuideFlag("      ");
         flag.setLocationRelativeToRegion(m, Layout.LEFT);

         guide.addToFront(flag);
         guide.startAnimation();
         guide.repaint();
      }


   }


   protected void entrySelected(SearchEntry selectedEntry){
   }



}

