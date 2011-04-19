package songo.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import songo.ApplicationController;

/**
 * Panel zawierający listę.
 */
public class ListPanel extends JScrollPane implements ListSelectionListener {
  protected ApplicationController app;
  protected JList list;
  protected DefaultListModel listModel;
  
  /** 
   * Lista ID modeli odp. poszczególnym wierszom listy.
   */
  protected List<Long> rowsIds = new ArrayList<Long>();
  
  /**
   * @param app konotroler aplikacji
   */
  public ListPanel(final ApplicationController app) {
    this.app = app;
    
    setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    
    listModel = new DefaultListModel();
    list = new JList(listModel);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    setViewportView(list);
    
    list.getSelectionModel().addListSelectionListener(this);
  }
  
  /**
   * Usuwa elementy listy
   */
  public void clear() {
    rowsIds.clear();
    listModel.removeAllElements();
    list.clearSelection();
  }
  
  /**
   * Wybiera element listy o podanym indeksie.
   * @param index indeks elementu do zaznaczenia (licząc od 0).
   */
  public void select(int index) {
    list.setSelectedIndex(index);
  }
  
  /**
   * @return ID aktualnie zaznaczonego elementu na liście
   * lub null jeśli nic nie jest znaznaczone.
   */
  public Long getSelectedId() {
    int selectedIndex = list.getSelectedIndex();
    return selectedIndex > -1 ? rowsIds.get(selectedIndex) : null;
  }
  
  public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) return;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        onSelected();
      }
    });
  }
  
  /**
   * Metoda wywoływana przy zmianie zaznaczenia na liście.
   */
  protected void onSelected() {
  }
}
