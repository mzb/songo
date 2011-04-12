package songo.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.UIManager;

public class EditSongPanel extends JInternalFrame {
  private JTextField titleField;
  private JTextField artistField;
  private JTextField albumField;
  private JTextField trackNumberField;
  private JTextField durationField;
  private JTextField fileField;

  public EditSongPanel() {
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
    
    JButton fileButton = new JButton("Wybierz");
    
    JLabel fileLabel = new JLabel("Plik");
    
    JButton saveButton = new JButton("Zapisz");
    
    JButton cancelButton = new JButton("Anuluj");
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          setClosed(true);
        } catch (PropertyVetoException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    });
    GroupLayout groupLayout = new GroupLayout(getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addContainerGap()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                  .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(trackNumberLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(titleLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(albumLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(artistLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                  .addPreferredGap(ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                  .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(artistField, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
                    .addComponent(albumField, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
                    .addComponent(titleField, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
                    .addComponent(trackNumberField, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)))
                .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                  .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(durationlabel, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                    .addComponent(fileLabel, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE))
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                    .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                      .addComponent(fileField)
                      .addPreferredGap(ComponentPlacement.RELATED)
                      .addComponent(fileButton))
                    .addComponent(durationField, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)))))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(131)
              .addComponent(saveButton)
              .addGap(18)
              .addComponent(cancelButton)))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
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
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(fileField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(fileButton)
            .addComponent(fileLabel))
          .addPreferredGap(ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(saveButton)
            .addComponent(cancelButton))
          .addGap(18))
    );
    getContentPane().setLayout(groupLayout);

    setVisible(true);
  }

}
