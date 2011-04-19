package songo.model;

import songo.db.Database;
import songo.db.ModelManager;
import songo.db.QueryResults;

/**
 * Klasa do zarządzania wykonawcami.
 * @see ModelManager
 */
public class ArtistsManager extends ModelManager<Artist> {
  public ArtistsManager(Database db) {
    super(db);
  }
  
  @Override 
  public String getTableName() {
    return "artists";
  }
  
  @Override 
  public Artist build(QueryResults row) throws Database.Error {
    Artist artist = build((String)row.get("name"));
    artist.id = row.getLong("id");
    return artist;
  }
  
  @Override
  public Artist build(String name) {
    return new Artist(name);
  }
  
  public Artist findOrCreateByName(String name) throws Database.Error {
    return findOrCreateBy("name", name);
  }
  
  @Override 
  public void create(Artist artist) throws Database.Error {
    artist.id = db.insert(
        "insert into artists (id, name) values (null, ?)", 
        artist.name); 
  }
  
  @Override public void update(Artist artist) throws Database.Error {
    
  }
}
