package CompressionProject;
import Services.*;

import java.io.IOException;

public class CompressorMain {

    public static void main(String[] args) throws IOException {
        DeCompressService deCompressService = new HuffmanDeCompressService();
        CompressService compressService = new HuffmanCompressService();

        GUI view = new GUI();
        GUICompressController compressController = new GUICompressController(view, compressService);
        GUIDeCompressController deCompressController = new GUIDeCompressController(view, deCompressService);

        view.setTitle("Tömörítő program");
        view.pack();
        view.setSize(700, 405);
        view.setVisible(true);

    }

}
