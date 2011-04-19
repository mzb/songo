package songo.model;

import songo.db.Model;

/**
 * Klasa reprezentująca pojedyńczego wykonawcę w kolekcji.
 */
public class Artist extends Model {
  /** Nazwa */
  public String name;
  
 public Artist(String name) {
   this.name = name;
 }
 
 public String toString() {
   return name;
 }
}
