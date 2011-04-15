package songo;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileFilter;

import songo.db.Database;
import songo.db.Database.Error;
import songo.model.Album;
import songo.model.AlbumsManager;
import songo.model.Artist;
import songo.model.ArtistsManager;
import songo.model.Song;
import songo.model.SongsManager;
import songo.ui.AddSongPanel;
import songo.ui.AlbumsPanel;
import songo.ui.ArtistsPanel;
import songo.ui.ContentPanel;
import songo.ui.Frame;
import songo.ui.SongsPanel;

public class Application {
  static final Logger log = Logger.getLogger("Application");
  // TODO: => Arg wywolania
  static final String CONFIG_FILE = "config/test.properties";
  
  Database database;
  SongsManager songsManager;
  ArtistsManager artistsManager;
  AlbumsManager albumsManager;
  FileFilter fileFilter;
  
  ContentPanel contentPanel;
  ArtistsPanel artistsPanel;
  AlbumsPanel albumsPanel;
  SongsPanel songsPanel;
  Frame frame;
  
  AddSongPanel songPanel;
  
  public Application(String[] args) {
    log.info("Starting");
    
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        close();
      }
    });
    
    loadUI();
    loadModels();
  }

  private void loadModels() {
    log.info("Connecting to database");
    try {
      database = new Database(CONFIG_FILE);
      database.connect();
      artistsManager = new ArtistsManager(database);
      albumsManager = new AlbumsManager(database);
      songsManager = new SongsManager(database, artistsManager, albumsManager);
    } catch (Database.Error e) {
      log.log(Level.SEVERE, "", e);
      frame.exception(e);
    }
    
    loadArtists(null);
  }

  private void loadUI() {
    log.info("Creating GUI");
    contentPanel = new ContentPanel(this);
    artistsPanel = contentPanel.getArtistsPanel();
    albumsPanel = contentPanel.getAlbumsPanel();
    songsPanel = contentPanel.getSongsPanel();
    frame = new Frame("Songo", contentPanel, new WindowAdapter() {
      public void windowClosed(WindowEvent e) {
        close();
      }
    });
  }
  
  public void loadArtists(String conditions) {
    List<Artist> artists = new ArrayList<Artist>();
    try {
      long count = artistsManager.count(conditions);
      artists.add(new Artist(String.format("-- Wykonawcy (%d) --", count)));
      artists.addAll(artistsManager.find(conditions, "name", null));
    } catch (Database.Error e) {
      log.log(Level.WARNING, "", e);
      frame.exception(e);
    }
    artistsPanel.setData(artists);
    artistsPanel.select(0);
  }
  
  public void loadAlbums(String conditions) {
    List<Album> albums = new ArrayList<Album>();
    try {
      long count = albumsManager.count(conditions);
      albums.add(new Album(String.format("-- Albumy (%d) --", count)));
      albums.addAll(albumsManager.find(conditions, "title", null));
    } catch (Database.Error e) {
      log.log(Level.WARNING, "", e);
      frame.exception(e);
    }
    albumsPanel.setData(albums);
    albumsPanel.select(0);
  }
  
  public void loadSongs(String conditions) {
    try {
      songsPanel.setData(songsManager.find(conditions, "artist_name, album_title, track_number", null));
    } catch (Database.Error e) {
      log.log(Level.WARNING, "", e);
      frame.exception(e);
    }
  }
  
  public void addSong() {
    openAddSongPanel();
  }
  
  public void saveSong(Map<String, String> attrs) {
    log.info(attrs.toString());
    try {
      songsManager.save(attrs);
      closeSongPanel();
    } catch (Database.Error e) {
      log.log(Level.SEVERE, "", e);
      frame.exception(e);
    }
  }
  
  public ContentPanel getContentPanel() {
    return contentPanel;
  }
  
  public void close() {
    log.info("closing");
    if (database != null) {
      try {
        database.disconnect();
      } catch (Database.Error e) {
        log.log(Level.WARNING, "", e);
      }
    }
  }
  
  protected void openAddSongPanel() {
    if (songPanel != null && !songPanel.isClosed()) {
      return;
    }
    songPanel = new AddSongPanel(this);
  }
  
  protected void closeSongPanel() {
    if (songPanel != null) {
      songPanel.close();
    }
  }

  public FileFilter getFileFilter() {
    if (fileFilter == null) {
      fileFilter = new FileFilter() {
        public String getDescription() {
          return "Pliki mp3";
        }
        public boolean accept(File f) {
          if (f.isDirectory()) return true;
          String[] name = f.getName().split("\\.");
          if (name.length > 1 && name[1].equals("mp3")) {
            return true;
          }
          return false;
        }
      }; 
    }
    return fileFilter;
  }

  public void albumSelected() {
    long albumId = albumsPanel.getSelectedId();
    long artistId = artistsPanel.getSelectedId();
    
    List<String> conditions = new ArrayList<String>();
    if (albumId > 0) {
      conditions.add(String.format("album_id = %d", albumId));
    }
    if (artistId > 0) {
      conditions.add(String.format("artist_id = %d", artistId));
    }
    loadSongs(Database.sqlize("AND", conditions));
  }
  
  public void artistSelected() {
    long artistId = artistsPanel.getSelectedId();
    String conditions = artistId > 0 ? String.format("artist_id = %d", artistId) : null;
    
    try {
      List<Long> albumsIds = songsManager.getAlbumsIds(conditions);
      conditions = albumsIds.isEmpty() ? "FALSE" : 
          String.format("id in (%s)", Database.sqlize(albumsIds));
      loadAlbums(conditions);
    } catch (Database.Error e) {
      log.log(Level.WARNING, "", e);
      frame.exception(e);
    }
  }
}
