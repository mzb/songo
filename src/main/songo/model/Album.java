package songo.model;

import songo.db.Model;

/**
 * Model reprezentujący pojedyńczy album w kolekcji.
 */
public class Album extends Model {
  /** Tytuł */
  public String title;
  
  public Album(String title) {
    this.title = title;
  }
  
  public String toString() {
    return title;
  }
}
