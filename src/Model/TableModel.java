package Model;

import CompressionProject.CompressorMain;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
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
                try{
                  URL url = (elem instanceof Folder) ? CompressorMain.class.getResource("/icons/folder.png") : CompressorMain.class.getResource("/icons/file.png");
                  BufferedImage icon = ImageIO.read(url);
                  String filename = elem.getName();
                  return new IconTextItem(filename, new ImageIcon(icon));
                }catch(IOException e){
                  e.printStackTrace();
                }
            }
            case 1:{
                String pattern = "yyyy. MM. dd  HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                Date date = elem.getCreationDate();
                return simpleDateFormat.format(date.getTime());
            }
            case 2:{
                String pattern = "yyyy. MM. dd  HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                Date date = elem.getLastModifiedDate();
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
