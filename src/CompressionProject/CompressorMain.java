package CompressionProject;
import Services.CompressService;
import Services.DeCompressService;
import Services.GUICompressController;
import Services.GUIDeCompressController;
import Services.HierarchyService;
import Services.HuffmanCompressService;
import Services.HuffmanDeCompressService;
import com.formdev.flatlaf.FlatLightLaf;
public class CompressorMain {

    public static void main(String[] args) {
        HierarchyService hierarchyService = new HierarchyService();
        DeCompressService deCompressService = new HuffmanDeCompressService();
        
        GUI view = new GUI();
        GUICompressController compressController = new GUICompressController(view, hierarchyService);
        GUIDeCompressController deCompressController = new GUIDeCompressController(view, deCompressService);
        
        view.setTitle("Tömörítő program");
        view.pack();
        view.setSize(700, 405);
        view.setVisible(true);
        
    }
    
}
