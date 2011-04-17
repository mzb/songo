package songo.ui;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import songo.ApplicationController;
import songo.model.Artist;

public class ArtistsPanel extends ListPanel {
  public ArtistsPanel(final ApplicationController app) {
    super(app);
  }
  
  @Override
  protected void onSelected() {
    app.onArtistSelected();
  }
  
  public void setData(List<Artist> artists) {
    clear();
    for (Artist a : artists) {
      listModel.addElement(a.name);
      rowsIds.add(a.id);
    }
  }
}
