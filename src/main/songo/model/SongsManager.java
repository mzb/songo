package songo.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import songo.db.Database;
import songo.db.QueryResults;

public class SongsManager {
  static final String TABLE = "songs";
  
  Database db;
  
  public SongsManager(Database db) {
    this.db = db;
  }

  public void save(Song song) throws Database.Error {
    song.id = db.insert(
        "insert into songs (id, title, duration, track_number, file, artist_id, album_id) " +
    		"values (null, ?, ?, ?, ?, ?, ?)", 
    		song.title, 
    		song.duration, 
    		song.trackNumber, 
    		song.file.getAbsolutePath(), 
    		song.artist != null ? song.artist.id : null, 
    		song.album != null ? song.album.id : null);
  }
  
  public List<Song> find(String conditions, String order, String limit) throws Database.Error {
    String query = "select * from songs";
    if (conditions != null && !conditions.isEmpty()) {
      query += " where (" + conditions + ")";
    }
    if (order != null && !order.isEmpty()) {
      query += " order by " + order;
    }
    if (limit != null && !limit.isEmpty()) {
      query += " limit " + limit;
    }
    
    QueryResults rows = db.query(query);
    
    List<Song> songs = new ArrayList<Song>();
    for (; !rows.empty(); rows.next()) {
      Song song = new Song(new File((String)rows.get("file")));
      song.id = rows.getLong("id");
      songs.add(song);
    }
    
    return songs;
  }
  
  public void delete(Song song) {
    
  }
}
