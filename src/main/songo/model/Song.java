package songo.model;

import java.io.File;
import java.util.concurrent.TimeUnit;

import songo.db.Model;

public class Song extends Model {
  public String title;
  public Artist artist;
  public Album album;
  public long duration;
  public int trackNumber;
  public File file;
  
  public Song(File file) {
    this.file = file;
  }
  
  public String toString() {
    return title;
  }
  
  public String getArtistName() {
    return artist != null ? artist.name : "";
  }
  
  public String getAlbumTitle() {
    return album != null ? album.title : "";
  }
  
  public Long getArtistId() {
    return artist != null ? artist.id : null;
  }
  
  public Long getAlbumId() {
    return album != null ? album.id : null;
  }
  
  public String getFormattedDuration() {
    long minutes = TimeUnit.SECONDS.toMinutes(duration);
    return String.format("%02d:%02d", minutes, duration - TimeUnit.MINUTES.toSeconds(minutes));
  }
}
