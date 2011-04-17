package songo.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileFilter;
import javax.swing.JButton;
import javax.swing.UIManager;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;

import songo.ApplicationController;
import songo.model.Song;

public class EditSongPanel extends JInternalFrame implements ActionListener {
  final ApplicationController app;
  JTextField titleField;
  JTextField artistField;
  JTextField albumField;
  JTextField trackNumberField;
  JTextField durationField;
  JTextField fileField;
  JButton fileButton;
  JButton saveButton;
  JButton cancelButton;
  Song song;

  /**
   * @wbp.parser.constructor
   */
  public EditSongPanel(ApplicationController app, Song song) {
    this.app = app;
    load();
    open();
    if (song != null) {
      setData(song);
    }
  }
  
  public EditSongPanel(ApplicationController app) {
    this(app, null);
  }
  
  public void close() {
    try {
      setClosed(true);
    } catch (PropertyVetoException e) {}
  }
  
  public void open() {
    setVisible(true);
    app.getContentPanel().add(this);
    try {
      setSelected(true);
    } catch (PropertyVetoException e) {}
  }
  
  public void setData(Song song) {
    this.song = song;
    fileField.setText(song.file.getAbsolutePath());
    titleField.setText(song.title);
    artistField.setText(song.artist.name);
    albumField.setText(song.album.title);
    trackNumberField.setText(String.valueOf(song.trackNumber));
    durationField.setText(song.getFormattedDuration());
  }
  
  public void setData(Map<String, Object> data) {
    fileField.setText(String.valueOf(data.get("file")));
    titleField.setText(String.valueOf(data.get("title")));
    artistField.setText(String.valueOf(data.get("artist")));
    albumField.setText(String.valueOf(data.get("album")));
    trackNumberField.setText(String.valueOf(data.get("track_number")));
    durationField.setText(String.valueOf(data.get("duration")));
  }
  
  public void actionPerformed(ActionEvent e) {
    if (saveButton == e.getSource()) {
      Map<String, String> attrs = new HashMap<String, String>();
      attrs.put("title", titleField.getText());
      attrs.put("artist", artistField.getText());
      attrs.put("album", albumField.getText());
      attrs.put("trackNumber", trackNumberField.getText());
      attrs.put("duration", durationField.getText());
      attrs.put("file", fileField.getText());
      EditSongPanel.this.app.saveSong(song != null ? song.id : null, attrs);
      return;
    }
    if (cancelButton == e.getSource()) {
      close();
      return;
    }
    if (fileButton == e.getSource()) {
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(EditSongPanel.this.app.getFileFilter());
      int returnVal = fc.showOpenDialog(EditSongPanel.this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        Map<String, Object> data = app.importSongDataFromIdTags(file);
        setData(data);
      }
    }
  }

  protected void load() {
    setBorder(UIManager.getBorder("InternalFrame.paletteBorder"));
    setResizable(false);
    setTitle("Edycja utworu");
    setBounds(100, 100, 450, 255);
    setClosable(true);
    
    JLabel titleLabel = new JLabel("Tytuł");
    
    titleField = new JTextField();
    titleField.setColumns(10);
    
    JLabel artistLabel = new JLabel("Wykonawca");
    
    artistField = new JTextField();
    artistField.setColumns(10);
    
    albumField = new JTextField();
    albumField.setColumns(10);
    
    trackNumberField = new JTextField();
    trackNumberField.setColumns(10);
    
    JLabel albumLabel = new JLabel("Album");
    
    JLabel trackNumberLabel = new JLabel("Numer");
    
    durationField = new JTextField();
    durationField.setColumns(10);
    
    JLabel durationlabel = new JLabel("Czas trwania");
    
    fileField = new JTextField();
    fileField.setColumns(10);
    
    fileButton = new JButton("Wybierz");
    fileButton.addActionListener(this);
    
    JLabel fileLabel = new JLabel("Plik");
    
    saveButton = new JButton("Zapisz");
    saveButton.addActionListener(this);
    
    cancelButton = new JButton("Anuluj");
    cancelButton.addActionListener(this);
    
    GroupLayout groupLayout = new GroupLayout(getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(131)
              .addComponent(saveButton)
              .addGap(18)
              .addComponent(cancelButton))
            .addGroup(groupLayout.createSequentialGroup()
              .addContainerGap()
              .addComponent(fileLabel, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
              .addGap(20)
              .addComponent(fileField, GroupLayout.PREFERRED_SIZE, 197, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(fileButton)))
          .addGap(18))
        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                .addComponent(trackNumberLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(titleLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(albumLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(artistLabel, Alignment.TRAILING))
              .addPreferredGap(ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(artistField, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
                .addComponent(albumField, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
                .addComponent(titleField, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
                .addComponent(trackNumberField, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)))
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(durationlabel, GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(durationField, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(fileLabel)
            .addComponent(fileField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(fileButton))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(titleLabel)
            .addComponent(titleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(artistLabel)
            .addComponent(artistField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(albumField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(albumLabel))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(trackNumberField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(trackNumberLabel))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(durationField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(durationlabel))
          .addPreferredGap(ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(saveButton)
            .addComponent(cancelButton))
          .addGap(18))
    );
    getContentPane().setLayout(groupLayout);
  }
}
