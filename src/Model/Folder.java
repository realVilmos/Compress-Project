package javaapplication1;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class Folder implements HierarchyInterface{
    private Path path;
    private ArrayList<HierarchyInterface> children;
    private FolderHeader header;
    private BasicFileAttributes attr;

    public Folder(Path path, BasicFileAttributes attr) {
        this.path = path;
        this.children = new ArrayList<HierarchyInterface>();
        this.attr = attr;
    }
    
    public void addChild(HierarchyInterface elem){
        children.add(elem);
    }
    
    public void removeChild(int index){
        children.remove(index);
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public ArrayList<HierarchyInterface> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<HierarchyInterface> children) {
        this.children = children;
    }

    public FolderHeader getHeader() {
        return header;
    }

    public void setHeader(FolderHeader header) {
        this.header = header;
    }

    public BasicFileAttributes getAttr() {
        return attr;
    }

    public void setAttr(BasicFileAttributes attr) {
        this.attr = attr;
    }
    
    
    
    
    
}
