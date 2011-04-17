package songo.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import songo.ApplicationController;

public class SearchPanel extends JPanel implements ActionListener {
  JTextField queryField;
  JCheckBox chckbxArtists, chckbxAlbums, chckbxSongs;
  JButton clearButton;
  final ApplicationController app;
  
  public SearchPanel(ApplicationController app) {
    this.app = app;
    load();
  }
  
  public void actionPerformed(ActionEvent e) {
    
  }
  
  private void load() {
    queryField = new JTextField();
    queryField.setToolTipText("Szukaj");
    queryField.setColumns(10);
    
    chckbxArtists = new JCheckBox("Wykonawcy");
    chckbxAlbums = new JCheckBox("Albumy");
    chckbxSongs = new JCheckBox("Utwory");
    
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
          .addComponent(chckbxArtists)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(chckbxAlbums)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(chckbxSongs))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(Alignment.LEADING)
        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(queryField, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
          .addComponent(clearButton)
          .addComponent(chckbxSongs)
          .addComponent(chckbxAlbums)
          .addComponent(chckbxArtists))
    );
    setLayout(layout);
  }
}
