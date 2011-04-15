package songo.model;

import java.util.Date;
import java.util.List;

import songo.db.Model;

public class Album extends Model {
  public String title;
  public Date releaseDate;
  public List<Artist> artists;
  public List<Song> songs;
  
  public Album(String title) {
    this.title = title;
  }
  
  public String toString() {
    return title;
  }
}
