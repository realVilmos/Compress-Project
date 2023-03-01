package Model;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class File implements HierarchyInterface{
    private Path path;
    private FolderHeader header;
    private BasicFileAttributes attr;

    public File(Path path, BasicFileAttributes attr) {
        this.path = path;
        this.attr = attr;
    }
    
    public File(){
        
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
    
    @Override
    public Date getCreationDate() {
        return new Date(this.attr.creationTime().toMillis());
    }

    @Override
    public FolderHeader getHeader() {
        return header;
    }

    @Override
    public void setHeader(FolderHeader header) {
        this.header = header;
    }
    
    @Override
    public Date getLastModifiedDate() {
        return new Date(this.attr.lastModifiedTime().toMillis());
    }
}
