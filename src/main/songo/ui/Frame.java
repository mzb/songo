package songo.ui;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import songo.utils.Utils;

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
  
  public String prompt(String title, String msg, String defaultValue) {
    return (String) JOptionPane.showInputDialog(this,
        msg, title, JOptionPane.PLAIN_MESSAGE, null, null, defaultValue);
  }
  
  public void warning(String msg, Object... params) {
    message(JOptionPane.WARNING_MESSAGE, "", msg, params);
  }
  
  public void error(String title, String msg, Object... params) {
    message(JOptionPane.ERROR_MESSAGE, title, msg, params);
  }
  
  public void error(String title, List<String> msgs) {
    error(title, Utils.join(msgs, "\n", "* "));
  }
  
  public void exception(Throwable e) {
    error(e.getClass().getSimpleName(), e.getMessage());
  }
  
  public void info(String msg, Object... params) {
    message(JOptionPane.INFORMATION_MESSAGE, "", msg, params);
  }
  
  public void message(int type, String title, String msg, Object... params) {
    JOptionPane.showMessageDialog(this, String.format(msg, params), title, type);
  }
  
  public boolean confirmation(String title, String msg, String yesOpt, String cancelOpt, 
      Object... params) {
    Object[] options = new Object[]{ yesOpt, cancelOpt };
    int chosen = JOptionPane.showOptionDialog(this, String.format(msg, params), 
        title,
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE, 
        null,
        options,
        options[0]);
    return chosen == 0;
  }
  
  public boolean confirmation(String title, String msg, Object... params) {
    return confirmation(title, msg, "OK", "Anuluj", params);
  }
}