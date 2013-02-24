package songo.model;

import songo.db.Database;
import songo.db.Database.Error;
import songo.db.ModelManager;
import songo.db.QueryResults;

/**
 * Klasa zarządzająca albumami w kolekcji.
 * @see ModelManager
 */
public class AlbumsManager extends ModelManager<Album> {

  public AlbumsManager(Database db) {
    super(db);
  }

  @Override 
  public String getTableName() {
    return "albums";
  }

  @Override 
  public Album build(QueryResults row) throws Database.Error {
    Album album = build((String)row.get("title"));
    album.id = row.getLong("id");
    return album;
  }
  
  @Override
  public Album build(String title) {
    return new Album(title);
  }
  
  @Override
  public void create(Album album) throws Database.Error {
    album.id = db.insert(
        "insert into albums (id, title) values (null, ?)", 
        album.title); 
  }

  @Override
  public void update(Album album) throws Error {

  }

  public Album findOrCreateByTitle(String title) throws Database.Error {
    return findOrCreateBy("title", title);
  }

}
