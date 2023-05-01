package CompressionProject;
import Model.File;
import Model.Folder;
import Services.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Objects;

public class CompressorMain {

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      GUI view = new GUI();
      ProgressBar progressBar = new ProgressBar();
      GUICompressController compressController = new GUICompressController(view, progressBar);
      GUIDeCompressController deCompressController = new GUIDeCompressController(view, progressBar);

      URL url = CompressorMain.class.getResource("/icons/main-icon.png");
      BufferedImage img = ImageIO.read(url);
      view.setIconImage(img);
      view.setTitle("Archiva");
      view.pack();
      view.setSize(700, 405);
      view.setVisible(true);
    } else {
      ProgressBar progressBar = new ProgressBar();
      if(Objects.equals(args[0], "--de-compress")) {
        progressBar.setVisible();
        java.io.File toDecompress = new java.io.File(args[1]);
        HuffmanDeCompressService deCompressService = new HuffmanDeCompressService(progressBar);
        Folder root = deCompressService.buildHierarchyModel(toDecompress);
        deCompressService.deCompress(root, toDecompress.getParentFile().toPath());
        System.exit(0);
      }
      if (Objects.equals(args[0], "--compress")) {
        progressBar.setVisible();
        Utils utils = new Utils();
        Folder root = new Folder();

        java.io.File location = new java.io.File(args[1]).getParentFile();

        //Modellezés
        for (int i = 1; i < args.length; i++) {
          java.io.File file = new java.io.File(args[i]);
          try {
            Path subPath = file.toPath();
            BasicFileAttributes attr = Files.readAttributes(subPath, BasicFileAttributes.class);
            if (file.isDirectory()) {
              Folder subFolder = new Folder(file.toPath(), attr);
              subFolder.setChildren(utils.traverseFolder(subFolder));
              root.addChild(subFolder);
            } else {
              root.addChild(new File(file.toPath(), attr));
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

        HuffmanCompressService compressService = new HuffmanCompressService(root, progressBar);
        compressService.compress(new java.io.File(location + "/" + location.getName()));
        System.exit(0);
      }
      if(Objects.equals(args[0], "--open")){
        GUI view = new GUI();
        GUICompressController compressController = new GUICompressController(view, progressBar);
        GUIDeCompressController deCompressController = new GUIDeCompressController(view, progressBar, new java.io.File(args[1]));

        view.setSelectedTab(1);

        URL url = CompressorMain.class.getResource("/icons/main-icon.png");
        BufferedImage img = ImageIO.read(url);
        view.setIconImage(img);
        view.setTitle("Archiva");
        view.pack();
        view.setSize(700, 405);
        view.setVisible(true);
      }
    }
  }
}
