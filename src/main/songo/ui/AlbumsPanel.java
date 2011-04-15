package songo.ui;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import songo.Application;
import songo.model.Album;

public class AlbumsPanel extends ListPanel {
  public AlbumsPanel(final Application app) {
    super(app);
  }
  
  @Override
  protected void onSelected() {
    app.albumSelected();
  }
  
  public void setData(List<Album> albums) {
    System.err.println("AlbumsPanel#setData");
    clear();
    System.err.println("albums = " + albums);
    for (Album a : albums) {
      listModel.addElement(a.title);
      rowsIds.add(a.id);
    }
    System.err.println("AlbumsPanel#setData READY");
  }
}
