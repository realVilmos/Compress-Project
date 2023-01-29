package Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

public class TableModel extends AbstractTableModel{
    
    private List<HierarchyInterface> elements;
    
    public TableModel(){
        this.elements = new ArrayList();
    }
    
    public TableModel(ArrayList<HierarchyInterface> elements){
        this.elements = elements;
    }

    @Override
    public int getRowCount() {
        return this.elements.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        HierarchyInterface elem = this.elements.get(rowIndex);
        switch(columnIndex) {
            case 0: {
                ImageIcon icon = (elem instanceof Folder) ? new ImageIcon("src/icons/folder.png") : new ImageIcon("src/icons/file.png");
                String filename = (elem instanceof Folder) ? ((Folder)elem).getPath().getFileName().toString() : ((File)elem).getPath().getFileName().toString();
                return new IconTextItem(filename, icon);
            }
            case 1:{
                String pattern = "yyyy. MM. dd  HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                Date date = new Date((elem instanceof Folder) ? ((Folder)elem).getAttr().creationTime().toMillis() : ((File)elem).getAttr().creationTime().toMillis());
                return simpleDateFormat.format(date.getTime());
            }
            case 2:{
                String pattern = "yyyy. MM. dd  HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                Date date = new Date((elem instanceof Folder) ? ((Folder)elem).getAttr().lastModifiedTime().toMillis() : ((File)elem).getAttr().lastModifiedTime().toMillis());
                return simpleDateFormat.format(date.getTime());
            }
        }
        return null;
    }
    
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "Név és kiterjesztés";
            case 1: return "Létrehozás dátuma";
            case 2: return "Módosítás dátuma";
        }
        return null;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return IconTextItem.class;
            case 1: return String.class;
            case 2: return String.class;
        }
        return Object.class;
    }
    
    public void setElements(ArrayList<HierarchyInterface> elements){
        this.elements = elements;
        fireTableDataChanged();
    }
    
    
    
    
    
}
