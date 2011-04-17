package songo.ui;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import songo.ApplicationController;
import songo.model.Album;

public class AlbumsPanel extends ListPanel {
  public AlbumsPanel(final ApplicationController app) {
    super(app);
  }
  
  @Override
  protected void onSelected() {
    app.onAlbumSelected();
  }
  
  public void setData(List<Album> albums) {
    clear();
    for (Album a : albums) {
      listModel.addElement(a.title);
      rowsIds.add(a.id);
    }
  }
}
