package javaapplication1.Table;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class IconTextRenderer extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        IconTextItem item = (IconTextItem)value;
        setText( item.getText() );
        setIcon( item.getIcon() );
        return this;
    }
}
