package Services;
import CompressionProject.ProgressBar;
import Model.Folder;
import Model.HierarchyInterface;
import Model.IconTextRenderer;
import Model.TableModel;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Stack;
import CompressionProject.GUI;
import Model.File;
import Model.FileFolderNameComperator;
import java.awt.Desktop;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class GUICompressController {

    private GUI gui;
    private TableModel compressModel;
    private Stack<Folder> stack;
    private Utils utils;

    private ProgressBar progressBar;

    public GUICompressController(GUI gui, ProgressBar progressBar){
        this.gui = gui;
        this.compressModel = new TableModel();
        this.stack = new Stack<>();
        this.progressBar = progressBar;

        stack.push(new Folder(null, null));

        utils = new Utils();

        gui.setCompressTableModel(compressModel);
        gui.setTableColumnModel(0, new IconTextRenderer());
        gui.addFileImportHandler(new FileImportHandler());
        compressModel.setElements(stack.peek().getChildren());

        this.gui.addCompressTableMouseListener(new SelectedRowListener());
        this.gui.addBackButtonListener(new BackButtonListener());
        this.gui.addPlusButtonListener(new PlusButtonListener());
        this.gui.addMinusButtonListener(new MinusButtonListener());
        this.gui.addNavigationTextFieldListener(new NavigationTextFieldListener());
        this.gui.addCompressButtonListener(new CompressButtonListener());
    }

    class SelectedRowListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent mouseEvent){
            JTable table = (JTable) mouseEvent.getSource();
            Point point = mouseEvent.getPoint();
            int row = table.rowAtPoint(point);
            if(mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1){
                HierarchyInterface choosenElem = stack.peek().getChildren().get(row);
                if(choosenElem instanceof Folder){
                    stack.push((Folder)choosenElem);
                    updateGUI();
                }else{
                    Desktop desktop = Desktop.getDesktop();
                    java.io.File file = ((File)choosenElem).getPath().toFile();
                    if(file.exists()) try {
                        desktop.open(file);
                    } catch (IOException ex) {
                        gui.displayErrorMessage("Hiba történt a fájl megnyitása közben.");
                        Logger.getLogger(GUICompressController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    class BackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(stack.size() > 1){
                stack.pop();
                updateGUI();
            }
        }
    }

    class PlusButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            java.io.File files[] = gui.getSelectedFilesFromDialog();
            utils.addElementsToFolder(stack.peek(), files);
            updateGUI();
        }
    }

    class MinusButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] selectedRows = gui.getSelectedCompressTableRows();
            for(int i = selectedRows.length-1; i >= 0; i--){
                stack.peek().removeChild(selectedRows[i]);
            }
            updateGUI();
        }
    }

    class NavigationTextFieldListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            while(stack.size() > 1){
                stack.pop();
            }

            String navigationPath = gui.getCompressNavigationTextFieldValue();
            if(navigationPath.charAt(0) != '\\') navigationPath = "\\" + navigationPath;

            String folders[] = navigationPath.split("\\\\");

            for(int i = 1; i < folders.length; i++){
                System.out.println(i + ": " + folders[i]);
                Folder parentFolder = stack.peek();

                Folder childFolder = (Folder)(parentFolder.getFolderByName(folders[i]));
                if(childFolder == null){
                    gui.displayErrorMessage("Nem található az útvonal");
                    return;
                }

                stack.push(childFolder);
            }

            updateGUI();
        }
    }

    class CompressButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(stack.peek().getChildren().size() > 0){
                java.io.File compressToPath = gui.getSelectedCompressLocation();
                if(compressToPath == null){
                  return;
                }
                progressBar.setVisible();
                Thread t = new Thread(() -> {
                  CompressService compressService = new HuffmanCompressService(progressBar);
                  compressService.initialize(stack.elementAt(0));
                  compressService.compress(compressToPath);
                });
                t.start();
            }else{
                gui.displayErrorMessage("Kérem adjon hozzá fájlokat");
            }
        }
    }

    private void updateGUI(){
        ArrayList<HierarchyInterface> children = stack.peek().getChildren();
        Collections.sort(children, new FileFolderNameComperator());
        compressModel.setElements(children);
        String path = "";

        for(Folder elem : stack){
            if(elem.getPath() == null){
                path += "\\";
            }else{
                path += elem.getPath().getFileName().toString() + "\\";
            }
        }

        gui.setCompressTableModel(compressModel);
        gui.setCompressNavigationTextField(path);
    }

    public class FileImportHandler extends TransferHandler {
      @Override
      public boolean canImport(TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
      }

      @Override
      public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
          return false;
        }

        Transferable t = support.getTransferable();
        try {
          List<java.io.File[]> filesList = ((List<java.io.File[]>) t.getTransferData(DataFlavor.javaFileListFlavor));
          java.io.File[] files = filesList.toArray(new java.io.File[filesList.size()]);

          utils.addElementsToFolder(stack.peek(), files);
          updateGUI();
          for (java.io.File file : files) {
            System.out.println(file.getAbsolutePath());
          }
          return true;
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }
      }
    }
}
