package Model.Services;

import Model.File;
import Model.Folder;
import Model.HierarchyInterface;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaapplication1.GUI;

public class HierarchyService {
    
    public ArrayList<HierarchyInterface> traverseFolder(Folder folder){
        java.io.File subFiles[] = folder.getPath().toFile().listFiles();
        ArrayList<HierarchyInterface> returnList = new ArrayList<HierarchyInterface>();
        try{
            for(java.io.File subFile : subFiles){
                Path subPath = subFile.toPath();
                BasicFileAttributes attr = Files.readAttributes(subPath, BasicFileAttributes.class);
                if(subFile.isDirectory()){
                    Folder subFolder = new Folder(subFile.toPath(), attr);
                    subFolder.setChildren(traverseFolder(subFolder));
                    returnList.add(subFolder);
                }else{
                    returnList.add(new File(subFile.toPath(), attr));
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        };
        return returnList;
    }
    
    public void addElementsToFolder(Folder currentFolder, java.io.File files[]){
        for(java.io.File file : files){
            Path path = file.toPath();
            try {
                BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);

                if(file.isDirectory()){
                    Folder folder = new Folder(path, attr);
                    folder.setChildren(traverseFolder(folder));
                    currentFolder.addChild(folder);
                }else{
                    currentFolder.addChild(new File(path, attr));
                }
            } catch (IOException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void removeElementsFromFolder(Folder currentFolder, int selectedRows[]){
        System.out.println("If kívűl");
        for(int i = selectedRows.length-1; i >= 0; i--){
            currentFolder.removeChild(selectedRows[i]);
        }
    }
}
