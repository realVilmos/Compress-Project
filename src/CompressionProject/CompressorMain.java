package CompressionProject;
import Model.Services.GUIController;
import Model.Services.HierarchyService;
import com.formdev.flatlaf.FlatLightLaf;
public class CompressorMain {

    public static void main(String[] args) {
        
        GUI view = new GUI();
        HierarchyService service = new HierarchyService();
        GUIController controller = new GUIController(view, service);
        
        view.setTitle("Tömörítő program");
        view.pack();
        view.setSize(700, 405);
        view.setVisible(true);
        
    }
    
}
