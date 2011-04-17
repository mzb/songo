package songo.model;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
  
  public void save(Long id, Map<String, String> attrs) throws Database.Error, ValidationErrors {
    List<String> errors = new ArrayList<String>();
    
    if (attrs.get("file") == null || attrs.get("file").isEmpty()) {
      errors.add("Nie wybrano pliku.");
    }
    Song song = new Song(new File(attrs.get("file")));
    song.id = id;
    song.title = attrs.get("title");
    try {
      song.trackNumber = Integer.parseInt(attrs.get("trackNumber"));
    } catch (NumberFormatException e) {
      errors.add("Numer nie jest liczbą");
    }
    
    String[] duration = attrs.get("duration").split(":");
    try {
      if (duration.length == 2) {
        int minutes = Integer.parseInt(duration[0]);
        int seconds = Integer.parseInt(duration[1]);
        song.duration = minutes * 60 + seconds;
      } else {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException e) {
      errors.add("Czas trwania powinien być w formacie hh:mm");
    }
    
    if (!attrs.get("artist").isEmpty()) {
      song.artist = artistsManager.findOrCreateByName(attrs.get("artist"));
    }
    if (!attrs.get("album").isEmpty()) {
      song.album = albumsManager.findOrCreateByTitle(attrs.get("album"));
    }
    
    if (!errors.isEmpty()) {
      throw new ValidationErrors(errors);
    }
    
    save(song);
  }
  
  @Override 
  public void create(Song song) throws Database.Error {
    System.err.println("SongsManager#create");
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
    System.err.println("SongsManager#update");
    db.update("update songs set " +
    		"title = ?, duration = ?, track_number = ?, file = ?, artist_id = ?, album_id = ? " +
    		"where (id = ?)", 
    		song.title, 
    		song.duration,
    		song.trackNumber,
    		song.file.getAbsolutePath(),
    		song.getArtistId(), 
    		song.getAlbumId(), 
    		song.id);
  }
  
  @Override
  protected void validate(Song song) throws ValidationErrors {
    List<String> errors = new ArrayList<String>();
    if (song.file == null || song.file.getName().isEmpty()) {
      errors.add("Nie wybrano pliku");
    }
    if (!errors.isEmpty()) {
      throw new ValidationErrors(errors);
    }
  }
}
