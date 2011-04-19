package songo.ui;

import java.awt.Dimension;

import javax.swing.JPanel;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JDesktopPane;
import javax.swing.JSplitPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import javax.swing.ScrollPaneConstants;
import javax.swing.AbstractListModel;
import javax.swing.table.DefaultTableModel;

import songo.ApplicationController;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

/**
 * Główny panel aplikacji. Definiuje layout poszczególnych paneli.
 */
public class ContentPanel extends JDesktopPane implements ActionListener {
  JButton addButton;
  JButton editButton;
  JButton deleteButton;
  ArtistsPanel artistsPanel;
  AlbumsPanel albumsPanel;
  SongsPanel songsPanel;
  SearchPanel searchPanel;
  final ApplicationController app;

  /**
   * Konstruktor.
   * @param app Kontroler aplikacji
   */
  public ContentPanel(ApplicationController app) {
    this.app = app;
    load();
  }

  protected void load() {
    final Dimension size = new Dimension(800, 700); 
    setSize(size);
    setPreferredSize(size);
    setFocusable(true);
    
    searchPanel = new SearchPanel(app);
    
    JSplitPane collectionPanel = new JSplitPane();
    collectionPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
    collectionPanel.setResizeWeight(0.5);
    
    addButton = new JButton("+ Dodaj");
    addButton.addActionListener(this);
    
    editButton = new JButton("Edytuj");
    editButton.setEnabled(false);
    editButton.addActionListener(this);
    
    deleteButton = new JButton("- Usuń");
    deleteButton.addActionListener(this);
    deleteButton.setEnabled(false);
    
    GroupLayout groupLayout = new GroupLayout(this);
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
              .addComponent(addButton)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(editButton)
              .addPreferredGap(ComponentPlacement.RELATED, 524, Short.MAX_VALUE)
              .addComponent(deleteButton))
            .addComponent(collectionPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
            .addComponent(searchPanel, GroupLayout.PREFERRED_SIZE, 776, Short.MAX_VALUE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGap(6)
          .addComponent(searchPanel, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(collectionPanel, GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(addButton)
            .addComponent(editButton)
            .addComponent(deleteButton))
          .addContainerGap())
    );
    
    JSplitPane artistsAlbumsPanel = new JSplitPane();
    artistsAlbumsPanel.setResizeWeight(0.5);
    collectionPanel.setLeftComponent(artistsAlbumsPanel);
    
    artistsPanel = new ArtistsPanel(app);
    artistsAlbumsPanel.setLeftComponent(artistsPanel);
    
    albumsPanel = new AlbumsPanel(app);
    artistsAlbumsPanel.setRightComponent(albumsPanel);
    
    songsPanel = new SongsPanel(app);
    collectionPanel.setRightComponent(songsPanel);
    
    setLayout(groupLayout);
  }
  
  public void actionPerformed(ActionEvent e) {
    if (addButton == e.getSource()) {
      app.addSong();
      return;
    }
    if (editButton == e.getSource()) {
      app.editSong();
      return;
    }
    if (deleteButton == e.getSource()) {
      app.deleteSong();
      return;
    }
  }
  
  /**
   * Aktywuje przyciski modyfikujące utwory (Edytuj, Usuń)
   */
  public void enableSongModificationButtons() {
    editButton.setEnabled(true);
    deleteButton.setEnabled(true);
  }
  
  /**
   * Deaktywuje przyciski Edytuj i Usuń
   */
  public void disableSongModificationButtons() {
    editButton.setEnabled(false);
    deleteButton.setEnabled(false);
  }
  
  /**
   * @return panel wykonawców
   */
  public ArtistsPanel getArtistsPanel() {
    return artistsPanel;
  }
  
  /**
   * @return panel albumów
   */
  public AlbumsPanel getAlbumsPanel() {
    return albumsPanel;
  }
  
  /**
   * @return panel utworów
   */
  public SongsPanel getSongsPanel() {
    return songsPanel;
  }
  
  /**
   * @return panel wyszukiwania
   */
  public SearchPanel getSearchPanel() {
    return searchPanel;
  }

  /**
   * Aktywuje przycisk Usuń
   */
  public void enableDeleteSongButton() {
    deleteButton.setEnabled(true);
  }
  
  /**
   * Deaktywuje przycisk Usuń
   */
  public void disableEditSongButton() {
    editButton.setEnabled(false);
  }

  /**
   * Aktywuje przycisk Edytuj
   */
  public void enableEditSongButton() {
    editButton.setEnabled(true);
  }
}
