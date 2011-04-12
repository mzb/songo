package songo;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import songo.ui.ContentPanel;
import songo.ui.Frame;

public class Application {
  static final Logger log = Logger.getLogger("Application");
  
  ContentPanel content;
  Frame frame;
  
  public Application(String[] args) {
    log.info("starting");
    
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        close();
      }
    });
    
    log.info("creating GUI");
    content = new ContentPanel(new Dimension(800, 600));
    frame = new Frame("Songo", content, new WindowAdapter() {
     public void windowClosed(WindowEvent e) {
       close();
     };
    });
  }
  
  public void close() {
    log.info("closing");
  }
}
