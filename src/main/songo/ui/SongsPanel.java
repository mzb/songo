package songo.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import songo.ApplicationController;
import songo.model.Song;

public class SongsPanel extends JScrollPane implements ListSelectionListener {
  static final String[] COLUMNS = new String[] { "#", "Tytuł", "Wykonawca", "Album", "Czas" };
  
  ApplicationController app;
  JTable list;
  DefaultTableModel listModel;
  List<Long> rowsIds  = new ArrayList<Long>();

  public SongsPanel(final ApplicationController app) {
    this.app = app;
    
    setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    
    list = new JTable();
    listModel = new DefaultTableModel(new Object[][] {}, COLUMNS) {
      @SuppressWarnings("rawtypes")
      Class[] columnTypes = new Class[] {
        Integer.class, String.class, String.class, String.class, String.class
      };
      @SuppressWarnings({ "unchecked", "rawtypes" })
      public Class getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
      }
      boolean[] columnEditables = new boolean[] {
        false, false, false, false, false
      };
      public boolean isCellEditable(int row, int column) {
        return columnEditables[column];
      }
    };
    list.setModel(listModel);
    
    list.getColumnModel().getColumn(0).setResizable(false);
    list.getColumnModel().getColumn(0).setPreferredWidth(15);
    list.getColumnModel().getColumn(1).setPreferredWidth(236);
    list.getColumnModel().getColumn(2).setPreferredWidth(159);
    list.getColumnModel().getColumn(3).setPreferredWidth(151);
    list.setFillsViewportHeight(true);
    setViewportView(list);
    
    list.getSelectionModel().addListSelectionListener(this);
  }
  
  public void setData(List<Song> songs) {
    clear();
    for (Song s : songs) {
      listModel.addRow(new Object[] { 
          s.trackNumber, s.title, s.getArtistName(), s.getAlbumTitle(), s.getFormattedDuration() });
      rowsIds.add(s.id);
    }
  }

  protected void clear() {
    listModel.setRowCount(0);
    rowsIds.clear();
  }
  
  public Long getSelectedId() {
    return rowsIds.get(list.getSelectedRow());
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) return;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (list.getSelectedRow() == -1) {
          app.songUnselected();
        } else {
          app.songSelected();
        }
      }
    });
  }
}
