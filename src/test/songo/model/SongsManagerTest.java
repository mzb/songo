package songo.model;

import java.io.File;

import org.junit.*;
import static org.junit.Assert.*;

import songo.db.Database;
import songo.db.Database.Error;

public class SongsManagerTest {
  static Database db;
  SongsManager manager;
  
  @BeforeClass public static void before() throws Exception {
    db = new Database("config/test.properties");
    db.connect();
  }
  
  @AfterClass public static void after() throws Exception {
    db.disconnect();
  }
  
  @Before public void beforeEach() throws Exception {
    manager = new SongsManager(db);
  }
  
  @After public void afterEach() throws Exception {
    db.update("truncate table songs");
  }

  @Test public void savesSong() throws Exception {
    Song song = new Song(new File("foo.mp3"));
    manager.save(song);
    assertTrue(song.id > 0);
  }
  
  @Test public void findsSongs() throws Exception {
    Song song = new Song(new File("bar.mp3"));
    manager.save(song);
    Song found = manager.find("file LIKE '%bar.mp3'", null, null).get(0);
    assertEquals(song.id, found.id);
    assertEquals(song.file.getAbsoluteFile(), found.file.getAbsoluteFile());
  }
}
