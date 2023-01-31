/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Services;

import CompressionProject.GUI;
import Model.Folder;
import Model.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GUIDeCompressController {
    private GUI gui;
    private DeCompressService deCompressService;
    private TableModel deCompressModel;
    private Stack<Folder> stack;
    
    public GUIDeCompressController(GUI gui, DeCompressService deCompressService){
        this.gui = gui;
        this.deCompressService = deCompressService;
        this.deCompressModel = new TableModel();
        this.stack = new Stack<>();
        
        stack.push(new Folder(null, null));
        
        gui.setdeCompressTableModel(deCompressModel);
        deCompressModel.setElements(stack.peek().getChildren());
        
        this.gui.addDeCompressButtonListener(new DeCompressButtonListener());
    }

    class DeCompressButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
        }
    }
}
