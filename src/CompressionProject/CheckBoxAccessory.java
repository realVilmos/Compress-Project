package CompressionProject;

import javax.swing.*;
import java.awt.*;

public class CheckBoxAccessory extends JComponent {
  JCheckBox virtualCheckBox;
  boolean checkBoxInit = false;

  int preferredWidth = 250;
  int preferredHeight = 10;//Mostly ignored as it is
  int checkBoxPosX = 5;
  int checkBoxPosY = 20;
  int checkBoxWidth = preferredWidth;
  int checkBoxHeight = 20;

  public CheckBoxAccessory()
  {
    setPreferredSize(new Dimension(preferredWidth, preferredHeight));
    virtualCheckBox = new JCheckBox("Mappa készítése a kibontandó fájloknak", checkBoxInit);
    virtualCheckBox.setBounds(checkBoxPosX, checkBoxPosY, checkBoxWidth, checkBoxHeight);
    this.add(virtualCheckBox);
  }

  public boolean isBoxSelected()
  {
    return virtualCheckBox.isSelected();
  }
}
