package Services;

import Model.File;
import Model.FileHeader;
import Model.Folder;
import Model.HierarchyInterface;
import Model.Huffman.Huffman;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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

    @Override
    protected long[] encodeAndWriteFile(File f, PrintWriter pw){
        //Visszaadja az utolsó bátban található szemét bitek számát
        byte junkbits = 0;
        long length = 0;
        try{
            BufferedReader br = new BufferedReader(new FileReader(f.getPath().toFile()));
            StringBuilder sb = new StringBuilder();

            //Soronként huffmankódolás
            //Mivel soronként kódolunk, minden sor végén keletkezhet "szemét" így elmenjük az ott keletkezett bájtokat a kövi sorra
            String encodedBinaryString = "";
            while(br.ready()){

                String line = br.readLine();
                encodedBinaryString = encodedBinaryString + huffman.encode(line);

                String binaryByteStrings[] = encodedBinaryString.split("(?<=\\G.{8})");
                encodedBinaryString = "";

                if(binaryByteStrings[binaryByteStrings.length-1].length() < 8){
                    //az utolsó elemet nem akarjuk még leírni mert nagy eséllyel szemét a vége
                    encodedBinaryString += binaryByteStrings[binaryByteStrings.length-1];

                    for(int i = 0; i < binaryByteStrings.length-2; i++)
                        sb.append((char)Integer.parseInt(binaryByteStrings[i], 2));
                    

                }else{

                    for(int i = 0; i < binaryByteStrings.length-1; i++)
                        sb.append((char)Integer.parseInt(binaryByteStrings[i], 2));

                }  
                pw.write(sb.toString());
                length += sb.toString().getBytes("UTF-8").length;
                sb.setLength(0);
            }

            //utolsó bit a szeméttel.
            //Itt kell headerbe beállítani hogy mennyi szemét van a végén
            if(encodedBinaryString != ""){
                pw.write((char)Integer.parseInt(encodedBinaryString, 2));
                junkbits = (byte) (8-encodedBinaryString.length());
                length = length + 1;
            }
            
            br.close();
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return new long[]{junkbits, length};
    }
    
    //Ez valószínűleg túl drága 
    private void getAllCharacterFrequencies(Folder folder, Map<Character, Integer> characterFrequencies){
        for(HierarchyInterface Elem : folder.getChildren()){
            if(Elem instanceof Folder){
                getAllCharacterFrequencies((Folder)Elem, characterFrequencies);
            }else{
                try{
                    File f = (File)Elem;
                    BufferedReader br = new BufferedReader(new FileReader(f.getPath().toFile()));

                    while(br.ready()){
                        String line = br.readLine();
                        
                        for(char character : line.toCharArray()){
                            Integer currentFrequency = characterFrequencies.get(character); 
                            characterFrequencies.put(character, (currentFrequency  == null) ? 1 : currentFrequency + 1 );
                        }
                    }
                    
                    br.close();     
                }catch(IOException e){
                    e.printStackTrace();
                }
            } 
        }
    }
    
    
}