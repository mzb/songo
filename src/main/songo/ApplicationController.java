package songo;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.filechooser.FileFilter;

import org.farng.mp3.MP3File;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.FrameBodyTLEN;
import org.farng.mp3.id3.FrameBodyTRCK;
import org.farng.mp3.id3.ID3v2_4;

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

/**
 * Klasa reprezentująca kontroler aplikacji.
 * Odpowiada za sterowaniem przepływem logiki między poszczególnymi funkcjonalnościami.
 */
public class ApplicationController {
  static final Logger log = Logger.getLogger("Application");
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
  
  /**
   * Konstruktor.
   * @param args argumenty wywołania
   */
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
  
  /**
   * Ładuje listę wykonawców spełniajacych podane warunki oraz zaznacza wiersz o podanym numerze.
   * @param conditions fragment SQL
   * @param index numer wiersza (licząc od 0), który ma zostać zaznaczony
   */
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
  
  /**
   * Ładuje listę albumów spełniających podane kryteria oraz zaznacza podany wiersz.
   * @param conditions fragment SQL
   * @param index numer wiersza (licząc od 0), który ma zostać zaznaczony
   */
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
  
  /**
   * Ładuje listę utworów spełniających podane kryteria.
   * @param conditions fragment SQL
   */
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
  
  /**
   * Zapisuje metadane utworu o podanym ID.
   * @param id ID utworu, którego metadane mają zostać zapisane.
   * @param attrs metadane, które mają zostać zapisane.
   */
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
  
  /**
   * Usuwa utwory zaznaczone w panelu utworów.
   */
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
  
  /**
   * Wyszukuje wykonawców, których nazwa zawiera ciąg znaków wprowadzony w polu wyszukiwania.
   * Następnie zaznacza pierwszego wykonawcę na liście, co powoduje wyszukanie i zaznaczenie 
   * odpowiednich albumów, a to z kolei wyszukanie i załadowanie odpowiednich utworów.
   * @see ApplicationController#onArtistSelected()
   * @see ApplicationController#onAlbumSelected()
   */
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
  
  /**
   * Zamyka aplikację.
   */
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

  /**
   * Zwraca filtr plików mowidocznych w panelu wyboru pliku przy edycji metadanych utworu.
   * @return filtr plików.
   * @see EditSongPanel
   */
  public FileFilter getFileFilter() {
    if (fileFilter == null) {
      fileFilter = new FileFilter() {
        public String getDescription() {
          return "Pliki mp3";
        }
        public boolean accept(File f) {
          if (f.isDirectory()) return true;
          String[] name = f.getName().split("\\.");
          return name.length > 1 && name[1].equals("mp3");
        }
      }; 
    }
    return fileFilter;
  }

  /**
   * Metoda wywoływana po zaznaczeniu na liście albumów jakiegoś albumu.
   * Powoduje załadowanie odpowiedniej listy utworów.
   */
  public void onAlbumSelected() {
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
  
  /**
   * Metoda wywoływana po zaznaczeniu na liście wykonawców jakiegoś wykonawcy.
   * Powoduje załadowanie odpowiedniej listy albumów.
   */
  public void onArtistSelected() {
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
  
  /**
   * Metoda wywoływana po zaznaczeniu na liście utworów jakiegoś utworu.
   * @param number ilość zaznaczonych utworów - od niej uzależniona jest aktywność przycisków
   * Edytuj i Usuń.
   */
  public void onSongsSelected(int number) {
    contentPanel.enableDeleteSongButton();
    if (number == 1) {
      contentPanel.enableEditSongButton();
    } else {
      contentPanel.disableEditSongButton();
    }
  }
  
  /**
   * Metoda wywoływana kiedy na liście utworów nie jest wybrany żaden utwór.
   * Deaktywuje przyciski pozwalające modyfikować/usuwać utwory.
   */
  public void onNoSongSelected() {
    contentPanel.disableSongModificationButtons();
  }
  
  /**
   * Zwraca główny panel aplikacji.
   * @return główny panel aplikacji.
   */
  public ContentPanel getContentPanel() {
    return contentPanel;
  }

  /**
   * Importuje metadane zawarte w tagach ID3 podanego pliku.
   * @param file plik, z którego mają być zaimportowane metadane.
   * @return odczytane metadane
   */
  public Map<String, Object> importSongDataFromIdTags(File file) {
    Map<String, Object> data = new HashMap<String, Object>();
    try {
      MP3File mp3 = new MP3File(file);
      AbstractID3v2 tag = mp3.getID3v2Tag();
      data.put("file", file.getAbsolutePath());
      data.put("title", tag.getSongTitle());
      data.put("album", tag.getAlbumTitle());
      data.put("artist", tag.getLeadArtist());
      data.put("track_number", tag.getTrackNumberOnAlbum().split("/")[0]);
      String duration = "";
      // FIXME(mateusz): Czas trwania utworu nie zawsze jest dostepny w ten sposob
      duration = ((FrameBodyTLEN)tag.getFrame("TLEN").getBody()).getText();
      duration = duration != null ? Utils.toTime(Integer.parseInt(duration) / 1000) : "";
      data.put("duration", duration);
    } catch (Exception e) {
      log.log(Level.WARNING, "Unable to import song data from id tags: " + e);
    }
    return data;
  }
}
