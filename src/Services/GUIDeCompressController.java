package Services;

import CompressionProject.GUI;
import CompressionProject.ProgressBar;
import Model.DeCompressTableModel;
import Model.Folder;
import Model.HierarchyInterface;
import Model.IconTextRenderer;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.swing.*;
import javax.swing.TransferHandler;

public class GUIDeCompressController {
  private GUI gui;
  private DeCompressTableModel deCompressModel;
  private Stack<Folder> stack;
  private java.io.File fileToDeCompress;
  private ProgressBar progressBar;
  private DeCompressService deCompressService;

  public GUIDeCompressController(GUI gui, ProgressBar progressBar) {
    this.gui = gui;
    this.progressBar = progressBar;
    initialize();
  }

  public GUIDeCompressController(GUI gui, ProgressBar progressBar, File f) {
    this.gui = gui;
    this.progressBar = progressBar;
    initialize();
    Folder root = deCompressService.buildHierarchyModel(f);
    stack.peek().setChildren(root.getChildren());
    updateGUI();
  }

  private void initialize() {
    this.deCompressModel = new DeCompressTableModel();
    this.stack = new Stack<>();
    this.deCompressService = new HuffmanDeCompressService(progressBar);

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
      if (stack.peek().getChildren().size() == 0) {
        gui.displayErrorMessage("Nincs mit kitömöríteni!");
        return;
      }

      Object[] data = gui.chooseDirectory();
      boolean createNewFolder = (boolean) data[0];
      Path deCompressto;

      if (createNewFolder) {
        File folder = new File(((Path) data[1]).toString() + "/" + fileToDeCompress.getName());
        folder.mkdir();
        deCompressto = folder.toPath();
      } else {
        deCompressto = (Path) data[1];
      }

      Folder toDeCompress = new Folder();
      int[] selected = gui.getDeCompressTableSelectedRows();
      ArrayList<HierarchyInterface> chlidren = stack.peek().getChildren();

      Thread t = new Thread(() -> {
        if (selected.length > 0) {
          for (int row : selected) {
            toDeCompress.addChild(chlidren.get(row));
          }
          deCompressService.deCompress(toDeCompress, deCompressto);
        } else {
          deCompressService.deCompress(stack.peek(), deCompressto);
        }
      });
      t.start();

    }
  }

  class DeCompressTableMouseListener extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent mouseEvent) {

      JTable table = (JTable) mouseEvent.getSource();
      Point point = mouseEvent.getPoint();
      int row = table.rowAtPoint(point);
      if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
        System.out.println("Double click!");
        HierarchyInterface choosenElem = stack.peek().getChildren().get(row);
        if (choosenElem instanceof Folder) {
          stack.push((Folder) choosenElem);
          updateGUI();
        } else {
          //Ki kell csomagolni és futtatni a fájlt
        }
      }
    }
  }

  class ChooseCompressedFileListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      java.io.File f = gui.getSelectedFileFromDialog();
      fileToDeCompress = f;

      Folder root = deCompressService.buildHierarchyModel(f);
      stack.peek().setChildren(root.getChildren());
      updateGUI();
    }
  }

  class DeCompressBackButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (stack.size() > 1) {
        stack.pop();
        updateGUI();
      }
    }
  }

  class DeCompressNavigationTextFieldListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      while (stack.size() > 1) {
        stack.pop();
      }

      String navigationPath = gui.getCompressNavigationTextFieldValue();
      if (navigationPath.charAt(0) != '\\') navigationPath = "\\" + navigationPath;

      String folders[] = navigationPath.split("\\\\");

      for (int i = 1; i < folders.length; i++) {
        System.out.println(i + ": " + folders[i]);
        Folder parentFolder = stack.peek();

        Folder childFolder = (Folder) (parentFolder.getFolderByName(folders[i]));
        if (childFolder == null) {
          gui.displayErrorMessage("Nem található az útvonal");
          return;
        }

        stack.push(childFolder);
      }

      updateGUI();
    }
  }

  private void updateGUI() {
    ArrayList<HierarchyInterface> children = stack.peek().getChildren();

    deCompressModel.setElements(children);
    String path = "";

    for (Folder elem : stack) {
      if (elem.getHeader() == null) {
        path += "\\";
      } else {
        path += elem.getHeader().getNameAndExtension() + "\\";
      }
    }

    gui.setdeCompressTableModel(deCompressModel);
    gui.setdeCompressNavigationTextField(path);
  }
}
