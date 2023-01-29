package Services;
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
import CompressionProject.GUI;
import Model.File;
import Model.FileFolderNameComperator;
import java.awt.Desktop;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        this.gui.addNavigationTextFieldListener(new navigationTextFieldListener());
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
                    Desktop desktop = Desktop.getDesktop();
                    java.io.File file = ((File)choosenElem).getPath().toFile();
                    if(file.exists()) try {
                        desktop.open(file);
                    } catch (IOException ex) {
                        gui.displayErrorMessage("Hiba történt a fájl megnyitása közben.");
                        Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
    
    class navigationTextFieldListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            while(stack.size() > 1){
                stack.pop();
            }
            
            String navigationPath = gui.getNavigationTextFieldValue();
            if(navigationPath.charAt(0) != '\\') navigationPath = "\\" + navigationPath;
            
            String folders[] = navigationPath.split("\\\\");
            
            for(int i = 1; i < folders.length; i++){
                System.out.println(i + ": " + folders[i]);
                Folder parentFolder = stack.peek();
                
                Folder childFolder = (Folder)(parentFolder.getChildByName(folders[i]));
                if(childFolder == null){
                    gui.displayErrorMessage("Nem található az útvonal");
                    return;
                }
                
                stack.push(childFolder);
            }
            
            updateGUI();
        }
    }
    
    private void updateGUI(){
        ArrayList<HierarchyInterface> children = stack.peek().getChildren();
        Collections.sort(children, new FileFolderNameComperator());
        model.setElements(children);
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
