package songo.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import songo.ApplicationController;

public class SearchPanel extends JPanel implements ActionListener, ItemListener, KeyListener {
  JTextField queryField;
  JCheckBox artistsCheckbox, albumsCheckbox, songsCheckbox;
  JButton clearButton;
  final ApplicationController app;
  
  public SearchPanel(ApplicationController app) {
    this.app = app;
    load();
  }
  
  public void actionPerformed(ActionEvent e) {
    if (clearButton == e.getSource()) {
      queryField.setText("");
      runSearch();
    }
  }

  private void runSearch() {
    app.search();
  }
  
  public void itemStateChanged(ItemEvent e) {
    Object checkbox = e.getItemSelectable();
    
    // Co najmniej jedna opcja musi byc zaznaczona
    if (!artistsCheckbox.isSelected() && 
        !albumsCheckbox.isSelected() && 
        !songsCheckbox.isSelected()) {
      ((JCheckBox)checkbox).setSelected(true);
    }
    
    if (artistsCheckbox == checkbox || 
        albumsCheckbox == checkbox ||
        songsCheckbox == checkbox) {
      runSearch();
    }
  }
  
  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      runSearch();
    }
  }

  public Map<String, Boolean> getScope() {
    Map<String, Boolean> scope = new HashMap<String, Boolean>();
    scope.put("artists", artistsCheckbox.isSelected());
    scope.put("albums", albumsCheckbox.isSelected());
    scope.put("songs", songsCheckbox.isSelected());
    return scope;
  }

  @Override
  public void keyReleased(KeyEvent e) {}

  @Override
  // Instant search
  public void keyTyped(KeyEvent e) {
    runSearch();
  }
  
  public String getQueryString() {
    return queryField.getText();
  }
  
  private void load() {
    queryField = new JTextField();
    queryField.setToolTipText("Szukaj");
    queryField.setColumns(10);
    queryField.addKeyListener(this);
    
    artistsCheckbox = new JCheckBox("Wykonawcy");
    artistsCheckbox.setSelected(true);
    artistsCheckbox.addItemListener(this);
    
    albumsCheckbox = new JCheckBox("Albumy");
    albumsCheckbox.setSelected(true);
    albumsCheckbox.addItemListener(this);
    
    songsCheckbox = new JCheckBox("Utwory");
    songsCheckbox.setSelected(true);
    songsCheckbox.addItemListener(this);
    
    clearButton = new JButton("Wyczyść");
    clearButton.addActionListener(this);
    
    GroupLayout layout = new GroupLayout(this);
    layout.setHorizontalGroup(
      layout.createParallelGroup(Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addComponent(queryField, GroupLayout.PREFERRED_SIZE, 392, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(clearButton)
          .addPreferredGap(ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
          .addComponent(artistsCheckbox)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(albumsCheckbox)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(songsCheckbox))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(Alignment.LEADING)
        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(queryField, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
          .addComponent(clearButton)
          .addComponent(songsCheckbox)
          .addComponent(albumsCheckbox)
          .addComponent(artistsCheckbox))
    );
    setLayout(layout);
  }
}
