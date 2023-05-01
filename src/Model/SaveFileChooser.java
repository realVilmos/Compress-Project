package Model;
import java.io.File;
import javax.swing.*;

public class SaveFileChooser extends JFileChooser {
  @Override
  public void approveSelection(){
    File f = getSelectedFile();
    if(getDialogType() == SAVE_DIALOG) {
      if(f.exists()) {
        int result = JOptionPane.showConfirmDialog(this, "A fájl már létezik. Felül szeretné írni?", "Létező fájl", JOptionPane.YES_NO_CANCEL_OPTION);
        switch (result) {
          case JOptionPane.YES_OPTION -> super.approveSelection();
          case JOptionPane.NO_OPTION, JOptionPane.CLOSED_OPTION -> {
          }
          case JOptionPane.CANCEL_OPTION -> cancelSelection();
        }
      } else {
        super.approveSelection();
      }
    }
  }
}
