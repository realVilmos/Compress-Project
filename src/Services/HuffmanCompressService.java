package Services;

import Model.File;
import Model.Folder;
import Model.HierarchyInterface;
import Model.Huffman.Huffman;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HuffmanCompressService extends CompressService {

    private Huffman huffman;
    private Map<Byte, Integer> byteFrequencies;

    public HuffmanCompressService(Folder folder){
        super(folder);
        this.byteFrequencies = new HashMap<>();
        getAllByteFrequencies(folder, byteFrequencies);
        this.huffman = new Huffman(byteFrequencies);
        printByteFrequecies();
    }

    public HuffmanCompressService(){
        super();
    }
    @Override
    public void initialize(Folder folder){
        super.rootFolder = folder;
        this.byteFrequencies = new HashMap<>();
        getAllByteFrequencies(folder, byteFrequencies);
        this.huffman = new Huffman(byteFrequencies);
        printByteFrequecies();
    }

    @Override
    protected long[] encodeAndWriteFile(File f, DataOutputStream outputStream){
        //Visszaadja az utolsó bájtban található szemét bitek számát
        byte junkbits = 0;
        long length = 0;
        try{
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f.getPath().toFile()));
            byte[] buffer = new byte[4096];
            int bytesRead;

            String leftover = "";
            StringBuilder encoded = new StringBuilder();
            while((bytesRead = bis.read(buffer)) != -1){
              encoded.append(leftover);
              if(bytesRead < buffer.length){
                byte[] temp = new byte[bytesRead];
                java.lang.System.arraycopy(buffer, 0, temp, 0, bytesRead);
                encoded.append(huffman.encode(temp));
              }else{
                encoded.append(huffman.encode(buffer));
              }

              String binaryByteStrings[] = encoded.toString().split(("(?<=\\G.{8})"));

              int bytesToEncode = binaryByteStrings.length;

              if(binaryByteStrings[binaryByteStrings.length-1].length() != 8){
                leftover = binaryByteStrings[binaryByteStrings.length-1];
                bytesToEncode--;
              }
              length+=bytesToEncode;

              for(int i = 0; i < bytesToEncode; i++){
                outputStream.write(Integer.parseInt(binaryByteStrings[i], 2));
              }

              encoded.setLength(0);
            }

            //Maradék keletkezett azaz szemét is
            if(leftover.length() != 0){
              junkbits = (byte)(8 - leftover.length());
              while(leftover.length() < 8){
                leftover = leftover + "0";
              }
              outputStream.write(Integer.parseInt(leftover, 2));
              length+=1;
            }

            bis.close();
        }catch(IOException e){
            e.printStackTrace();
        }

        return new long[]{junkbits, length};
    }

    @Override
    protected void writeToFileBegin(DataOutputStream outputStream) throws IOException {
        byte[] huffmanTree = huffman.getHuffmanTree();
        for(byte b : huffmanTree){
            outputStream.write(b);
        }
        outputStream.write('0');
    }

    //Ez valószínűleg túl drága, de szükséges
    private void getAllByteFrequencies(Folder folder, Map<Byte, Integer> characterFrequencies){
        for(HierarchyInterface Elem : folder.getChildren()){
            if(Elem instanceof Folder){
                getAllByteFrequencies((Folder)Elem, characterFrequencies);
            }else{
                try{
                    File f = (File)Elem;
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f.getPath().toFile()));
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while((bytesRead = bis.read(buffer)) != -1){
                        for(int i = 0; i < bytesRead; i++){
                          Integer currentFrequency = byteFrequencies.get(buffer[i]);
                          characterFrequencies.put(buffer[i], (currentFrequency  == null) ? 1 : currentFrequency + 1);
                        }
                    }

                    bis.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void printByteFrequecies(){
      for(byte b : byteFrequencies.keySet()){
        System.out.println(b + ": " + byteFrequencies.get(b));
      }
    }

}
