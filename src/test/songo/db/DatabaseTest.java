package songo.db;

import org.junit.*;
import static org.junit.Assert.*;

public class DatabaseTest {
  Database db;
  
  @Before public void beforeEach() throws Exception {
    db = new Database("config/test.properties");
  }
  
  @Test public void configuration() {
    assertEquals("jdbc:mysql", db.getAdapter());
    assertEquals("localhost:3306", db.getHost());
    assertEquals("songo", db.getName());
    assertEquals("root", db.getUsername());
    assertEquals("", db.getPassword());
  }

  @Test public void connectAndDisconnect() throws Exception {
    db.connect();
    assertFalse("Connection not opened", db.getConnection().isClosed());
    
    db.disconnect();
    assertTrue("Connection not closed", db.getConnection().isClosed());
  }
}
