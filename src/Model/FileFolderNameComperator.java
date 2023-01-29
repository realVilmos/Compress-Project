package Model;

import java.util.Comparator;

/**
 *
 * @author Vilmos Bognar
 */
public class FileFolderNameComperator implements Comparator<HierarchyInterface>{

    @Override
    public int compare(HierarchyInterface h1, HierarchyInterface h2) {
        if(h1 instanceof Folder && h2 instanceof File) return Integer.MIN_VALUE;
        if(h2 instanceof Folder && h1 instanceof File) return Integer.MAX_VALUE;
        
        return h1.getName().compareTo(h2.getName());
    }
    
}
