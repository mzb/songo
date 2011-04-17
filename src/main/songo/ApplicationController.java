package songo;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.filechooser.FileFilter;

import songo.db.Database;
import songo.db.Database.Error;
import songo.db.ModelManager.ValidationErrors;
import songo.model.Album;
import songo.model.AlbumsManager;
import songo.model.Artist;
import songo.model.ArtistsManager;
import songo.model.Song;
import songo.model.SongsManager;
import songo.ui.EditSongPanel;
import songo.ui.AlbumsPanel;
import songo.ui.ArtistsPanel;
import songo.ui.ContentPanel;
import songo.ui.Frame;
import songo.ui.SearchPanel;
import songo.ui.SongsPanel;
import songo.utils.Utils;

public class ApplicationController {
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
  EditSongPanel editSongPanel;
  SearchPanel searchPanel;
  Frame frame;
  
  public ApplicationController(String[] args) {
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
    
    search();
  }

  private void loadUI() {
    log.info("Creating GUI");
    contentPanel = new ContentPanel(this);
    artistsPanel = contentPanel.getArtistsPanel();
    albumsPanel = contentPanel.getAlbumsPanel();
    songsPanel = contentPanel.getSongsPanel();
    searchPanel = contentPanel.getSearchPanel();
    frame = new Frame("Songo", contentPanel, new WindowAdapter() {
      public void windowClosed(WindowEvent e) {
        close();
      }
    });
  }
  
  public void loadArtistsAndSelect(String conditions, int index) {
    loadArtists(conditions);
    artistsPanel.select(0);
  }

  protected void loadArtists(String conditions) {
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
  }
  
  public void loadAlbumsAndSelect(String conditions, int index) {
    loadAlbums(conditions);
    albumsPanel.select(0);
  }

  protected void loadAlbums(String conditions) {
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
  }
  
  public void loadSongs(String conditions) {
    try {
      List<Song> songs = songsManager.find(conditions, 
          "artist_name, album_title, track_number", null);
      songsPanel.setData(songs);
    } catch (Database.Error e) {
      log.log(Level.WARNING, "", e);
      frame.exception(e);
    }
  }
  
  public void addSong() {
    openEditSongPanel(null);
  }
  
  public void editSong() {
    Long songId = songsPanel.getSelectedId();
    try {
      Song song = songsManager.findById(songId);
      openEditSongPanel(song);
    } catch (Database.Error e) {
      log.log(Level.WARNING, "", e);
      frame.exception(e);
    }
  }
  
  public void saveSong(Long id, Map<String, String> attrs) {
    try {
      songsManager.save(id, attrs);
      closeEditSongPanel();
      search();
    } catch (ValidationErrors e) {
      frame.error("Edycja utworu", e.getErrors());
    } catch (Database.Error e) {
      log.log(Level.SEVERE, "", e);
      frame.exception(e);
    }
  }
  
  public void deleteSong() {
    List<Long> selectedIds = songsPanel.getSelectedIds();
    try {
      String idsCondition = String.format("songs.id in (%s)", Database.sqlize(selectedIds));
      List<Song> songs = songsManager.find(idsCondition, null, null);
      List<String> titles = new ArrayList<String>();
      for (Song s : songs) {
        titles.add(String.format("%s (%s)", s.title, s.getArtistName()));
      }
      boolean confirmed = frame.confirmation("Usuwanie utworów", 
          "Czy na pewno chcesz usunąć zaznaczone utwory:\n%s ?", 
          "Tak, usuń", "Nie usuwaj", Utils.join(titles, "\n", "* "));
      if (confirmed) {
        songsManager.delete(idsCondition);
        search();
      }
    } catch (Database.Error e) {
      log.log(Level.WARNING, "", e);
      frame.exception(e);
    }
  }
  
  public void search() {
    try {
      String searchQuery = getSearchQuery();
      List<Long> artistsIds = songsManager.getArtistsIds(searchQuery);
      loadArtistsAndSelect(artistsIds.isEmpty() ? 
          "FALSE" : String.format("id in (%s)", Database.sqlize(artistsIds)), 
          0);
    } catch (Database.Error e) {
      log.log(Level.WARNING, "", e);
      frame.exception(e);
    }
  }

  private String getSearchQuery() {
    List<String> conditions = new ArrayList<String>();
    Map<String, Boolean> scope = searchPanel.getScope();
    if (scope.get("artists")) {
      conditions.add(String.format("artists.name like '%%%s%%'", searchPanel.getQueryString()));
    }
    if (scope.get("albums")) {
      conditions.add(String.format("albums.title like '%%%s%%'", searchPanel.getQueryString()));
    }
    if (scope.get("songs")) {
      conditions.add(String.format("songs.title like '%%%s%%'", searchPanel.getQueryString()));
    }
    return conditions.isEmpty() ? null : "(" + Database.sqlize("OR", conditions) + ")";
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
  
  protected void openEditSongPanel(Song song) {
    if (editSongPanel != null && !editSongPanel.isClosed()) {
      return;
    }
    editSongPanel = new EditSongPanel(this, song);
  }
  
  protected void closeEditSongPanel() {
    if (editSongPanel != null) {
      editSongPanel.close();
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
    List<String> conditions = new ArrayList<String>();
    conditions.add(getSearchQuery());
    
    Long albumId = albumsPanel.getSelectedId();
    if (albumId != null) {
      conditions.add(String.format("album_id = %d", albumId));
    }
    
    Long artistId = artistsPanel.getSelectedId();
    if (artistId != null) {
      conditions.add(String.format("artist_id = %d", artistId));
    }
    
    loadSongs(Database.sqlize("AND", conditions));
  }
  
  public void artistSelected() {
    List<String> conditions = new ArrayList<String>();
    conditions.add(getSearchQuery());
    
    Long artistId = artistsPanel.getSelectedId();
    if (artistId != null) {
      conditions.add(String.format("(artist_id = %d)", artistId));
    }
    
    try {
      List<Long> albumsIds = songsManager.getAlbumsIds(Database.sqlize("AND", conditions));
      loadAlbumsAndSelect(albumsIds.isEmpty() ? 
          "FALSE" : String.format("id in (%s)", Database.sqlize(albumsIds)), 
          0);
    } catch (Database.Error e) {
      log.log(Level.WARNING, "", e);
      frame.exception(e);
    }
  }
  
  public void songsSelected(int number) {
    contentPanel.enableDeleteSongButton();
    if (number == 1) {
      contentPanel.enableEditSongButton();
    } else {
      contentPanel.disableEditSongButton();
    }
  }
  
  public void noSongSelected() {
    contentPanel.disableSongModificationButtons();
  }
  
  public ContentPanel getContentPanel() {
    return contentPanel;
  }
}
