package songo.model;

import java.io.File;
import java.util.concurrent.TimeUnit;

import songo.db.Model;
import songo.utils.Utils;

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
    return Utils.toTime(duration);
  }
}
