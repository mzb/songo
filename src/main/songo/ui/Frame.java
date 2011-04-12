package songo.ui;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Frame extends JFrame {
  public Frame(final String title, final Container content, final WindowListener listener) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(content);
        setResizable(false);
        pack();
        setVisible(true);
        
        addWindowListener(listener);
      }
    });
  }
}