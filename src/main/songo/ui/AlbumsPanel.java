package songo.ui;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class AlbumsPanel extends JScrollPane {
  JList list;
  
  public AlbumsPanel() {
    setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    
    list = new JList();
    list.setModel(new AbstractListModel() {
      String[] values = new String[] {"-- Wszystkie -- (3567)", "Pandemonic Incantations", "Satanica", "Zos Kia Cvltvs", "Demigod"};
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
