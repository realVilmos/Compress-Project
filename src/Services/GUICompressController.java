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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;

public class GUICompressController {
    
    private GUI gui;
    private HierarchyService hierarchyService;
    private TableModel compressModel;
    private Stack<Folder> stack;
    
    public GUICompressController(GUI gui, HierarchyService service){
        this.gui = gui;
        this.hierarchyService = service;
        this.compressModel = new TableModel();
        this.stack = new Stack<>();
        
        stack.push(new Folder(null, null));
        
        gui.setCompressTableModel(compressModel);
        gui.setTableColumnModel(0, new IconTextRenderer());
        compressModel.setElements(stack.peek().getChildren());
        
        this.gui.addTableMouseListener(new SelectedRowListener());
        this.gui.addBackButtonListener(new BackButtonListener());
        this.gui.addPlusButtonListener(new PlusButtonListener());
        this.gui.addMinusButtonListener(new MinusButtonListener());
        this.gui.addNavigationTextFieldListener(new NavigationTextFieldListener());
        this.gui.addCompressButtonListener(new CompressButtonListener());
    }

    class SelectedRowListener extends MouseAdapter {
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
                        Logger.getLogger(GUICompressController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
    
    class BackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(stack.size() > 1){
                stack.pop();
                updateGUI();
            }
        }
    }
    
    class PlusButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            java.io.File files[] = gui.getSelectedFilesFromDialog();
            hierarchyService.addElementsToFolder(stack.peek(), files);
            updateGUI();
        }
    }
    
    class MinusButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] selectedRows = gui.getSelectedCompressTableRows();
            for(int i = selectedRows.length-1; i >= 0; i--){
                stack.peek().removeChild(selectedRows[i]);
            }
            updateGUI();
        } 
    }
    
    class NavigationTextFieldListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            while(stack.size() > 1){
                stack.pop();
            }
            
            String navigationPath = gui.getCompressNavigationTextFieldValue();
            if(navigationPath.charAt(0) != '\\') navigationPath = "\\" + navigationPath;
            
            String folders[] = navigationPath.split("\\\\");
            
            for(int i = 1; i < folders.length; i++){
                System.out.println(i + ": " + folders[i]);
                Folder parentFolder = stack.peek();
                
                Folder childFolder = (Folder)(parentFolder.getFolderByName(folders[i]));
                if(childFolder == null){
                    gui.displayErrorMessage("Nem található az útvonal");
                    return;
                }
                
                stack.push(childFolder);
            }
            
            updateGUI();
        }
    }
    
    class CompressButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(stack.peek().getChildren().size() > 0){
                
                
                CompressService compressService = new HuffmanCompressService(stack.elementAt(0));
                compressService.compress();
            }else{
                gui.displayErrorMessage("Kérem adjon hozzá fájlokat");
            }
        }
        
    }
    
    private void updateGUI(){
        ArrayList<HierarchyInterface> children = stack.peek().getChildren();
        Collections.sort(children, new FileFolderNameComperator());
        compressModel.setElements(children);
        String path = "";
        
        for(Folder elem : stack){
            if(elem.getPath() == null){
                path += "\\";
            }else{
                path += elem.getPath().getFileName().toString() + "\\";
            }
        }
        
        gui.setCompressTableModel(compressModel);
        gui.setCompressNavigationTextField(path); 
    } 
}
