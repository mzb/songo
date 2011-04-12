package songo.ui;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class ArtistsPanel extends JScrollPane {
  JList list;
  
  public ArtistsPanel() {
    setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    
    list = new JList();
    list.setModel(new AbstractListModel() {
      String[] values = new String[] {"-- Wszyscy (255) --", "Behemoth (7)", "Vader (13)"};
      public int getSize() {
        return values.length;
      }
      public Object getElementAt(int index) {
        return values[index];
      }
    });
    setViewportView(list);
  }
}
