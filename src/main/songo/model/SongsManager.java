package songo.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import songo.db.Database;
import songo.db.ModelManager;
import songo.db.QueryResults;

public class SongsManager extends ModelManager<Song> {
  ArtistsManager artistsManager;
  AlbumsManager albumsManager;
  
  public SongsManager(Database db, ArtistsManager artistsManager, AlbumsManager albumsManager) {
    super(db);
    this.artistsManager = artistsManager;
    this.albumsManager = albumsManager;
  }

  @Override 
  public String getTableName() {
    return "songs";
  }
  
  @Override 
  public Song build(QueryResults row) throws Database.Error {
    Song song = build((String)row.get("file"));
    song.title = (String)row.get("title");
    song.trackNumber = (Integer)row.get("track_number");
    song.duration = (Long)row.get("duration");
    song.id = row.getLong("id");
    song.artist = artistsManager.build((String)row.get("artist_name"));
    song.artist.id = row.getLong("artist_id");
    song.album = albumsManager.build((String)row.get("album_title"));
    song.album.id = row.getLong("album_id");
    return song;
  }
  
  @Override
  public Song build(String file) {
    return new Song(new File(file));
  }
  
  @Override
  public List<Song> find(String conditions, String order, String limit) throws Database.Error {
    String query = "select songs.*, artists.name as artist_name, albums.title as album_title " +
    		"from " + getTableName() + " ";
    query += "join " + artistsManager.getTableName() + " on songs.artist_id = artists.id ";
    query += "join " + albumsManager.getTableName() + " on songs.album_id = albums.id ";
    
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
    while (rows.next()) {
      songs.add(build(rows));
    }
    rows.close();
    
    return songs;
  }
  
  public List<Long> getAlbumsIds(String conditions) throws Database.Error {
    List<Song> artistSongs = find(conditions, null, null);
    List<Long> albumsIds = new ArrayList<Long>();
    for (Song s : artistSongs) {
      albumsIds.add(s.album.id);
    }
    return albumsIds;
  }
  
  public void save(Map<String, String> attrs) throws Database.Error {
    Song song = new Song(new File(attrs.get("file")));
    song.title = attrs.get("title");
    song.duration = Long.parseLong(attrs.get("duration"));
    song.trackNumber = Integer.parseInt(attrs.get("trackNumber"));
    
    song.artist = artistsManager.findOrCreateByName(attrs.get("artist"));
    song.album = albumsManager.findOrCreateByTitle(attrs.get("album"));
    
    save(song);
  }
  
  @Override 
  public void create(Song song) throws Database.Error {
    song.id = db.insert(
        "insert into songs (id, title, duration, track_number, file, artist_id, album_id) " +
        "values (null, ?, ?, ?, ?, ?, ?)", 
        song.title, 
        song.duration, 
        song.trackNumber, 
        song.file.getAbsolutePath(), 
        song.getArtistId(), 
        song.getAlbumId());
  }
  
  @Override 
  public void update(Song song) throws Database.Error {
    
  }
  
  public void delete(Song song) {
    
  }

}
