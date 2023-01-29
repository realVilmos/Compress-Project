package Model;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class File implements HierarchyInterface{
    private Path path;
    private FileHeader header;
    private BasicFileAttributes attr;

    public File(Path path, BasicFileAttributes attr) {
        this.path = path;
        this.attr = attr;
    }
    
    public void setHeader(FileHeader header){
        this.header = header;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public BasicFileAttributes getAttr() {
        return attr;
    }

    public void setAttr(BasicFileAttributes attr) {
        this.attr = attr;
    }
    
    public String getName(){
        return this.path.getFileName().toString();
    }
    
}
