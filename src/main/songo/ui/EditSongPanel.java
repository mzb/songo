package songo.ui;

import songo.Application;
import songo.model.Song;

public class EditSongPanel extends AddSongPanel {
  public EditSongPanel(Song song, Application app) {
    super(app);
    titleField.setText(song.title);
    artistField.setText(song.artist.name);
    albumField.setText(song.album.title);
    trackNumberField.setText(String.valueOf(song.trackNumber));
    // TODO: Konwersja do 00:00:00
    durationField.setText(String.valueOf(song.duration));
    fileField.setText(song.file.getAbsolutePath());
  }  
}
