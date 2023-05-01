package Services;

import Model.File;
import Model.Folder;
import Model.HierarchyInterface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class Utils {
  public void addElementsToFolder(Folder currentFolder, java.io.File[] files) {
    for (java.io.File file : files) {
      Path path = file.toPath();
      try {
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);

        if (file.isDirectory()) {
          Folder folder = new Folder(path, attr);
          folder.setChildren(traverseFolder(folder));
          currentFolder.addChild(folder);
        } else {
          currentFolder.addChild(new File(path, attr));
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  public ArrayList<HierarchyInterface> traverseFolder(Folder folder) {
    java.io.File[] subFiles = folder.getPath().toFile().listFiles();
    ArrayList<HierarchyInterface> returnList = new ArrayList<>();
    try {
      assert subFiles != null;
      for (java.io.File subFile : subFiles) {
        Path subPath = subFile.toPath();
        BasicFileAttributes attr = Files.readAttributes(subPath, BasicFileAttributes.class);
        if (subFile.isDirectory()) {
          Folder subFolder = new Folder(subFile.toPath(), attr);
          subFolder.setChildren(traverseFolder(subFolder));
          returnList.add(subFolder);
        } else {
          returnList.add(new File(subFile.toPath(), attr));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return returnList;
  }

  public int getNumberOfFiles(Folder folder){
    int num = 0;
    for(HierarchyInterface elem : folder.getChildren()){
      if(elem instanceof Folder){
        num += getNumberOfFiles((Folder)elem);
      }else if(elem instanceof File){
        num++;
      }
    }
    return num;
  }
}
