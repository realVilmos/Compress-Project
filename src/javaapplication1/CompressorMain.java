package javaapplication1;
import com.formdev.flatlaf.FlatLightLaf;
public class CompressorMain {

    public static void main(String[] args) {
        FlatLightLaf.setup();
        GUI t = new GUI();
        t.setTitle("Tömörítő program");
        t.pack();
        t.setSize(700, 405);
        t.setVisible(true);
    }
    
}
