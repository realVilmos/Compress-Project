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
    private Map<Character, Integer> characterFrequencies;

    public HuffmanCompressService(Folder folder){
        super(folder);
        this.characterFrequencies = new HashMap<>();
        getAllCharacterFrequencies(folder, characterFrequencies);
        this.huffman = new Huffman(characterFrequencies);
    }

    public HuffmanCompressService(){
        super();
    }
    @Override
    public void initialize(Folder folder){
        super.rootFolder = folder;
        this.characterFrequencies = new HashMap<>();
        printCharacterFrequencies();
        getAllCharacterFrequencies(folder, characterFrequencies);
        this.huffman = new Huffman(characterFrequencies);

    }

    @Override
    protected long[] encodeAndWriteFile(File f, OutputStreamWriter outputStream){
        //Visszaadja az utolsó bájtban található szemét bitek számát
        byte junkbits = 0;
        long length = 0;
        try{
            BufferedReader br = new BufferedReader(new FileReader(f.getPath().toFile(), StandardCharsets.ISO_8859_1));

            //Karakterenként huffmankódolás

            StringBuilder tempEncodedString = new StringBuilder();
            while(br.ready()){
                char c = (char)br.read();
                String encoded = huffman.encodeChar(c);
                tempEncodedString.append(encoded);

                if(tempEncodedString.length() % 8 == 0){
                    String binaryByteStrings[] = tempEncodedString.toString().split(("(?<=\\G.{8})"));
                    tempEncodedString.setLength(0);
                    for(String s : binaryByteStrings){
                        outputStream.write(Integer.parseInt(s, 2));
                        length++;
                    }
                }
            }

            //Nem volt osztható 8-al, volt szemét.
            if(tempEncodedString.length() != 0){
                String binaryByteStrings[] = tempEncodedString.toString().split(("(?<=\\G.{8})"));
                tempEncodedString.setLength(0);
                for(String s : binaryByteStrings){
                    while(s.length() < 8){
                      s = s + "0";
                    }
                    System.out.println(s);
                    System.out.println(Integer.parseInt(s, 2));
                    outputStream.write(Integer.parseInt(s, 2));
                    length++;
                }
                junkbits = (byte) (8-binaryByteStrings[binaryByteStrings.length-1].length());
            }

            br.close();

        }catch(IOException e){
            e.printStackTrace();
        }

        return new long[]{junkbits, length};
    }

    @Override
    protected void writeToFileBegin(OutputStreamWriter outputStream) throws IOException {
        String huffmanTree = this.huffman.getStringHuffmanTree() + "0";
        System.out.println(huffmanTree);
        char[] chars = huffmanTree.toCharArray();
        for(char c : chars){
          byte[] charbytes = charToUTF8Bytes(c);
          for(byte b : charbytes){
            outputStream.write(b & 0xFF);
          }
        }
    }

    //Ez valószínűleg túl drága
    private void getAllCharacterFrequencies(Folder folder, Map<Character, Integer> characterFrequencies){
        for(HierarchyInterface Elem : folder.getChildren()){
            if(Elem instanceof Folder){
                getAllCharacterFrequencies((Folder)Elem, characterFrequencies);
            }else{
                try{
                    File f = (File)Elem;
                    BufferedReader br = new BufferedReader(new FileReader(f.getPath().toFile(), StandardCharsets.ISO_8859_1));

                    while(br.ready()){
                        char c = (char)br.read();
                        Integer currentFrequency = characterFrequencies.get(c);
                        characterFrequencies.put(c, (currentFrequency  == null) ? 1 : currentFrequency + 1 );
                    }

                    br.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void printCharacterFrequencies(){
      for(char c : characterFrequencies.keySet()){
        System.out.println(c + " - " + (int)c + ": " + characterFrequencies.get(c));
      }
    }

  byte[] charToUTF8Bytes(char c) {
    byte[] result = null;
    if (c <= 0x7F) {
      // Character is within the ASCII range (1 byte)
      result = new byte[1];
      result[0] = (byte) c;
    } else if (c <= 0x7FF) {
      // Character is within the first 11 bits (2 bytes)
      result = new byte[2];
      result[0] = (byte) (0xC0 | (c >> 6));
      result[1] = (byte) (0x80 | (c & 0x3F));
    } else {
      // Character is outside the first 11 bits (3 bytes)
      result = new byte[3];
      result[0] = (byte) (0xE0 | (c >> 12));
      result[1] = (byte) (0x80 | ((c >> 6) & 0x3F));
      result[2] = (byte) (0x80 | (c & 0x3F));
    }
    return result;
  }


}
