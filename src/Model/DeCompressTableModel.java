package Model;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

public class DeCompressTableModel extends AbstractTableModel{

    private List<HierarchyInterface> elements;

    public DeCompressTableModel(){
        this.elements = new ArrayList();
    }

    public DeCompressTableModel(ArrayList<HierarchyInterface> elements){
        this.elements = elements;
    }

    @Override
    public int getRowCount() {
        return (this.elements == null) ? 1 : elements.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(this.elements == null){
          switch(columnIndex){
            case 0: {
              ImageIcon icon = new ImageIcon();
              String filename = "Üres a mappa";
              return new IconTextItem(filename, icon);
            }
            case 1: case 2: {
              return "";
            }
          }
        }

        HierarchyInterface elem = this.elements.get(rowIndex);
        FolderHeader header = elem.getHeader();

        switch(columnIndex) {
            case 0: {
                ImageIcon icon = (elem instanceof Folder) ? new ImageIcon("src/icons/folder.png") : new ImageIcon("src/icons/file.png");
                String filename = header.getNameAndExtension();
                return new IconTextItem(filename, icon);
            }
            case 1:{
                String pattern = "yyyy. MM. dd  HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                Date date = header.getCreationDate();
                return simpleDateFormat.format(date.getTime());
            }
            case 2:{
                String pattern = "yyyy. MM. dd  HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                if(elem instanceof File){
                  Date date = ((FileHeader)header).getModificationDate();
                  return simpleDateFormat.format(date.getTime());
                }else{
                  return "-";
                }

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
        case 0:
          return IconTextItem.class;
        case 1:
          return String.class;
        case 2:
          return String.class;
      }
      return Object.class;
    }

    public void setElements(ArrayList<HierarchyInterface> elements){
        this.elements = elements;
        fireTableDataChanged();
    }





}
