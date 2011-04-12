package songo.model;

import java.io.File;

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
}
