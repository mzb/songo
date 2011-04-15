package songo.model;

import songo.db.Model;

public class Artist extends Model {
  public String name;
  
 public Artist(String name) {
   this.name = name;
 }
 
 public String toString() {
   StringBuilder sb = new StringBuilder("<Artist ");
   sb.append(" id=").append(this.id).
      append(" name=").append(this.name).
      append(">");
   return sb.toString();
 }
}
