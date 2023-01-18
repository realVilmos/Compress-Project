package javaapplication1;

import java.util.Date;

public class FileHeader extends FolderHeader{
    
    private long distanceFromHeader;
    private long fileSize;
    private Byte junkBits;
    private Date modificationDate;

    public long getDistanceFromHeader() {
        return distanceFromHeader;
    }

    public void setDistanceFromHeader(long distanceFromHeader) {
        this.distanceFromHeader = distanceFromHeader;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public Byte getJunkBits() {
        return junkBits;
    }

    public void setJunkBits(Byte junkBits) {
        this.junkBits = junkBits;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameAndExtension() {
        return nameAndExtension;
    }

    public void setNameAndExtension(String nameAndExtension) {
        this.nameAndExtension = nameAndExtension;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public FileHeader(long distanceFromHeader, long fileSize, Byte junkBits, Date modificationDate, int id, String nameAndExtension, Date creationDate) {
        super(id, nameAndExtension, creationDate);
        this.distanceFromHeader = distanceFromHeader;
        this.fileSize = fileSize;
        this.junkBits = junkBits;
        this.modificationDate = modificationDate;
    }
    
    public FileHeader(){
        
    }
    
    
    
}
