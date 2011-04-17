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

import songo.ApplicationController;

public class ListPanel extends JScrollPane implements ListSelectionListener {
  protected ApplicationController app;
  protected JList list;
  protected DefaultListModel listModel;
  
  protected List<Long> rowsIds = new ArrayList<Long>();
  
  public ListPanel(final ApplicationController app) {
    this.app = app;
    
    setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    
    listModel = new DefaultListModel();
    list = new JList(listModel);
    
    setViewportView(list);
    
    list.getSelectionModel().addListSelectionListener(this);
  }
  
  public void clear() {
    rowsIds.clear();
    listModel.removeAllElements();
    list.clearSelection();
  }
  
  public void select(int index) {
    list.setSelectedIndex(index);
  }
  
  public Long getSelectedId() {
    return rowsIds.get(list.getSelectedIndex());
  }
  
  public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) return;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        onSelected();
      }
    });
  }
  
  protected void onSelected() {
  }
}
