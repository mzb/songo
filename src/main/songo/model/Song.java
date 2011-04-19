package songo.model;

import java.io.File;
import java.util.concurrent.TimeUnit;

import songo.db.Model;
import songo.utils.Utils;

/**
 * Klasa reprezentująca podstawowy element kolekcji - utwór.
 *
 */
public class Song extends Model {
  /** Tytuł */
  public String title;
  
  /** Wykonawca */
  public Artist artist;
  
  /** Album */
  public Album album;
  
  /** Czas trwania w sekundach */
  public long duration;
  
  /** Numer utworu w albumie (licząc od 1) */
  public int trackNumber;
  
  /** Plik skojarzony z albumem */
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
  
  /** 
   * @return czas trwania utworu w formacie MM:HH
   * @see Utils#toTime(long)
   */
  public String getFormattedDuration() {
    return Utils.toTime(duration);
  }
}
