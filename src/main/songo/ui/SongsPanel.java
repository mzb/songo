package songo.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import songo.Application;
import songo.model.Song;

public class SongsPanel extends JScrollPane{
  static final String[] COLUMNS = new String[] { "#", "Tytuł", "Wykonawca", "Album", "Czas" };
  
  Application app;
  JTable list;
  DefaultTableModel listModel;
  List<Long> rowsIds  = new ArrayList<Long>();

  public SongsPanel(final Application app) {
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
  }
  
  public void setData(List<Song> songs) {
    listModel.setRowCount(0);
    rowsIds.clear();
    
    for (Song s : songs) {
      listModel.addRow(new Object[] { 
          s.trackNumber, s.title, s.getArtistName(), s.getAlbumTitle(), s.getFormattedDuration() });
      rowsIds.add(s.id);
    }
  }
}
