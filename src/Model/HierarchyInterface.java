package Model;

import java.util.Date;

public interface HierarchyInterface {
    String getName();
    Date getCreationDate();
    Date getLastModifiedDate();
    FolderHeader getHeader();
    void setHeader(FolderHeader header);
}
