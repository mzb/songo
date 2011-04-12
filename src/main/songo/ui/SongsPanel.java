package songo.ui;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

public class SongsPanel extends JScrollPane{
  JTable list;

  public SongsPanel() {
    setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    
    list = new JTable();
    list.setModel(new DefaultTableModel(
      new Object[][] {
      },
      new String[] {
        "#", "Tytu\u0142", "Wykonawca", "Album", "Czas"
      }
    ) {
      Class[] columnTypes = new Class[] {
        Integer.class, String.class, String.class, String.class, String.class
      };
      public Class getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
      }
      boolean[] columnEditables = new boolean[] {
        false, true, true, true, true
      };
      public boolean isCellEditable(int row, int column) {
        return columnEditables[column];
      }
    });
    list.getColumnModel().getColumn(0).setResizable(false);
    list.getColumnModel().getColumn(0).setPreferredWidth(15);
    list.getColumnModel().getColumn(1).setPreferredWidth(236);
    list.getColumnModel().getColumn(2).setPreferredWidth(159);
    list.getColumnModel().getColumn(3).setPreferredWidth(151);
    list.setFillsViewportHeight(true);
    setViewportView(list);
  }
}
