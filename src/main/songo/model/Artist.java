package songo.model;

import songo.db.Model;

public class Artist extends Model {
  public String name;
  
 public Artist(String name) {
   this.name = name;
 }
}
