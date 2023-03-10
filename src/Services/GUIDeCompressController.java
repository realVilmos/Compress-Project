package Services;

import CompressionProject.CheckBoxAccessory;
import CompressionProject.GUI;
import Model.DeCompressTableModel;
import Model.Folder;
import Model.HierarchyInterface;
import Model.IconTextRenderer;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class GUIDeCompressController {
    private GUI gui;
    private DeCompressService deCompressService;
    private DeCompressTableModel deCompressModel;
    private Stack<Folder> stack;
    private String deCompressFileName;

    public GUIDeCompressController(GUI gui, DeCompressService deCompressService){
        this.gui = gui;
        this.deCompressService = deCompressService;
        this.deCompressModel = new DeCompressTableModel();
        this.stack = new Stack<>();

        stack.push(new Folder(null, null));

        gui.setdeCompressTableModel(deCompressModel);
        gui.setDeCompressTableColumnModel(0, new IconTextRenderer());
        deCompressModel.setElements(stack.peek().getChildren());

        this.gui.addDeCompressButtonListener(new DeCompressButtonListener());
        this.gui.addChooseCompressedFileBtnListener(new ChooseCompressedFileListener());
        this.gui.addDeCompressTableMouseListener(new DeCompressTableMouseListener());
        this.gui.addDeCompressBackButtonListener(new DeCompressBackButtonListener());
        this.gui.addDeCompressNavigationTextFieldListener(new DeCompressNavigationTextFieldListener());
    }

    class DeCompressButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
          try {
              Object[] data = gui.chooseDirectory();
              boolean createNewFolder = (boolean) data[0];
              Path deCompressto = (Path)data[1];

              if(createNewFolder){
                File folder = new File(deCompressto.toString() + "/" + deCompressFileName);
                folder.mkdir();
                deCompressto = folder.toPath();
              }

              Folder toDeCompress = new Folder();
              int[] selected = gui.getDeCompressTableSelectedRows();
              ArrayList<HierarchyInterface> chlidren = stack.peek().getChildren();

              if(selected.length > 0){
                for(int row : selected){
                  toDeCompress.addChild(chlidren.get(row));
                }

                deCompressService.deCompress(toDeCompress, deCompressto);
              }else{
                deCompressService.deCompress(stack.peek(), deCompressto);
              }

          } catch (IOException ex) {
            throw new RuntimeException(ex);
          }
        }
    }

    class DeCompressTableMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent mouseEvent){

            JTable table = (JTable) mouseEvent.getSource();
            Point point = mouseEvent.getPoint();
            int row = table.rowAtPoint(point);
            if(mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1){
                System.out.println("Double click!");
                HierarchyInterface choosenElem = stack.peek().getChildren().get(row);
                if(choosenElem instanceof Folder){
                    stack.push((Folder)choosenElem);
                    updateGUI();
                }else{
                    //Ki kell csomagolni ??s futtatni a f??jlt
                }
            }
        }
    }

    class ChooseCompressedFileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            java.io.File f = gui.getSelectedFileFromDialog();
            deCompressFileName = f.getName();
            try {
                Folder root = deCompressService.buildHierarchyModel(f);

                stack.peek().setChildren(root.getChildren());

                updateGUI();

            } catch (IOException ex) {
                Logger.getLogger(GUIDeCompressController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class DeCompressBackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(stack.size() > 1){
              stack.pop();
              updateGUI();
            }
        }
    }

    class DeCompressNavigationTextFieldListener implements ActionListener{
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
                    gui.displayErrorMessage("Nem tal??lhat?? az ??tvonal");
                    return;
                }

                stack.push(childFolder);
            }

            updateGUI();
        }
    }

    private void updateGUI(){
        ArrayList<HierarchyInterface> children = stack.peek().getChildren();

        deCompressModel.setElements(children);
        String path = "";

        for(Folder elem : stack){
            if(elem.getHeader() == null){
                path += "\\";
            }else{
                path += elem.getHeader().getNameAndExtension() + "\\";
            }
        }

        gui.setdeCompressTableModel(deCompressModel);
        gui.setdeCompressNavigationTextField(path);
    }
}
