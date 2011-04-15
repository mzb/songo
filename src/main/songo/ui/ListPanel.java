package songo.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import songo.Application;

public class ListPanel extends JScrollPane {
  protected Application app;
  protected JList list;
  protected DefaultListModel listModel;
  
  protected List<Long> rowsIds = new ArrayList<Long>();
  
  public ListPanel(final Application app) {
    this.app = app;
    
    setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    
    listModel = new DefaultListModel();
    list = new JList(listModel);
    
    setViewportView(list);
    
    list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            onSelected();
          }
        });
      }
    });
  }
  
  protected void onSelected() {
  }
  
  public void clear() {
    rowsIds.clear();
    listModel.removeAllElements();
    list.clearSelection();
  }
  
  public void select(int index) {
    list.setSelectedIndex(index);
  }
  
  public long getSelectedId() {
    return rowsIds.get(list.getSelectedIndex());
  }
}
