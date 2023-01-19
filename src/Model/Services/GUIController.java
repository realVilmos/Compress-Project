package Model.Services;
import Model.Folder;
import Model.HierarchyInterface;
import Model.IconTextRenderer;
import Model.TableModel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Stack;
import javaapplication1.GUI;
import javax.swing.JTable;

public class GUIController {
    
    private GUI gui;
    private HierarchyService service;
    private TableModel model;
    private Stack<Folder> stack;
    
    public GUIController(GUI gui, HierarchyService service){
        this.gui = gui;
        this.service = service;
        this.model = new TableModel();
        this.stack = new Stack<>();
        
        stack.push(new Folder(null, null));
        
        gui.setTableModel(model);
        gui.setTableColumnModel(0, new IconTextRenderer());
        model.setElements(stack.peek().getChildren());
        
        this.gui.addTableMouseListener(new selectedRowListener());
        this.gui.addBackButtonListener(new backButtonListener());
        this.gui.addPlusButtonListener(new plusButtonListener());
        this.gui.addMinusButtonListener(new minusButtonListener());
    }
    
    class selectedRowListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent mouseEvent){
            JTable table = (JTable) mouseEvent.getSource();
            Point point = mouseEvent.getPoint();
            int row = table.rowAtPoint(point);
            if(mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1){      
                HierarchyInterface choosenElem = stack.peek().getChildren().get(row);
                if(choosenElem instanceof Folder){
                    stack.push((Folder)choosenElem);
                    updateGUI();
                }else{
                    //Meg kell nyitni a fájlt
                }
            }
        }
    }
    
    class backButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(stack.size() > 1){
                stack.pop();
                updateGUI();
            }
        }
    }
    
    class plusButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            java.io.File files[] = gui.getSelectedFilesFromDialog();
            service.addElementsToFolder(stack.peek(), files);
            updateGUI();
        }
    }
    
    class minusButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] selectedRows = gui.getSelectedTableRows();
            for(int i = selectedRows.length-1; i >= 0; i--){
                stack.peek().removeChild(selectedRows[i]);
            }
            updateGUI();
        } 
    }
    
    private void updateGUI(){
        model.setElements(stack.peek().getChildren());
        String path = "";
        
        for(Folder elem : stack){
            if(elem.getPath() == null){
                path += "\\";
            }else{
                path += elem.getPath().getFileName().toString() + "\\";
            }
        }
        
        gui.setTableModel(model);
        gui.setNavigationTextField(path); 
    } 
}
