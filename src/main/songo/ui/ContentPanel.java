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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

public class ContentPanel extends JDesktopPane {
  public ContentPanel(Dimension size) {
    setSize(size);
    setPreferredSize(new Dimension(800, 700));
    setFocusable(true);
    
    SearchPanel searchPanel = new SearchPanel();
    
    JSplitPane collectionPanel = new JSplitPane();
    collectionPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
    collectionPanel.setResizeWeight(0.5);
    
    JButton addButton = new JButton("+ Dodaj");
    addButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
      }
    });
    JButton editButton = new JButton("Edytuj");
    editButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        EditSongPanel editSongPanel = new EditSongPanel();
        add(editSongPanel);
        try {
          editSongPanel.setSelected(true);
        } catch (PropertyVetoException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    });
    
    JButton deleteButton = new JButton("- Usuń");
    
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
    
    ArtistsPanel artistsPanel = new ArtistsPanel();
    artistsAlbumsPanel.setLeftComponent(artistsPanel);
    
    AlbumsPanel albumsPanel = new AlbumsPanel();
    artistsAlbumsPanel.setRightComponent(albumsPanel);
    
    SongsPanel songsPanel = new SongsPanel();
    collectionPanel.setRightComponent(songsPanel);
    
    setLayout(groupLayout);
    
  }
}
