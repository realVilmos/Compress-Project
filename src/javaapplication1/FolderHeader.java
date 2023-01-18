package javaapplication1;

import java.util.Date;

public class FolderHeader {
    protected int id;
    protected String nameAndExtension;
    protected Date creationDate;

    public FolderHeader(int id, String nameAndExtension, Date creationDate) {
        this.id = id;
        this.nameAndExtension = nameAndExtension;
        this.creationDate = creationDate;
    }
    
    public FolderHeader(){
        
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
    
    
}
