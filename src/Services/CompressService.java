package Services;
import Model.File;
import Model.FileHeader;
import Model.Folder;
import Model.FolderHeader;
import Model.HierarchyInterface;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

public abstract class CompressService {
    
    //Mivel a header megoldások bármely jővőbeli tömörítési eljárásban ugyanazon az elven működnének, itt definiálom őket.
    
    //Még kell: Hierarchia ábrázolás a végére
    
    private int id = 0;
    private ArrayList<FolderHeader> definedHeaders = new ArrayList<>();
    private Folder rootFolder;
    
    CompressService(Folder folder){
        this.rootFolder = folder;
    }
    
    public void compress(){   
        initializeHeaders(this.rootFolder);
 
        try {
            PrintWriter pw = new PrintWriter("Compressed.txt");
            //Verziószám?
            
            //Legelejére ha valami kell a tömörítéshez pl ide Huffman Fa
            writeToFileBegin(pw);
            
            compressElementsInFolder(this.rootFolder, pw);
            
            //ide jöhet a 6 bájtos elválasztó "pecsét"
            //Bognár Vilmos Zsolt 6 Bájtos Pecsétje :)
            String sixByteSignature = "BVZ6BP";
            pw.write(sixByteSignature);
            addDistanceToHeaders((long)sixByteSignature.length());
            //valami makeHeader
            writeHeaders(pw);
            
            //Hierarchia ábrázolása stringben, ezt esetleg lehet kombinálni a writeHeaderrel hogy közbe történjen

            pw.flush();
            pw.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    protected void initializeHeaders(Folder folder){
        for(HierarchyInterface Elem : folder.getChildren()){
            FolderHeader header = (Elem instanceof Folder) ? new FolderHeader() : new FileHeader();
            header.setId(generateId());
            header.setNameAndExtension(Elem.getName());
            header.setCreationDate(Elem.getCreationDate());
            
            if(header instanceof FileHeader fileHeader){
                fileHeader.setModificationDate(Elem.getLastModifiedDate());
            }
            
            Elem.setHeader(header);
            
            if(Elem instanceof Folder) initializeHeaders((Folder)Elem);       
        }
    }
    
    protected void compressElementsInFolder(Folder folder, PrintWriter pw){
        for(HierarchyInterface Elem : folder.getChildren()){
           if(Elem instanceof Folder){
                compressElementsInFolder((Folder)Elem, pw);
                FolderHeader header = Elem.getHeader();
                addDefinedHeaderToList(header);
                
            }else{
                File f = (File)Elem;
                long returnValues[] = encodeAndWriteFile(f, pw);
                byte junkbits = (byte) returnValues[0];
                long length = returnValues[1];

                FileHeader header = (FileHeader) f.getHeader();
                header.setJunkBits(junkbits);
                addDefinedHeaderToList(header);
                addDistanceToHeaders(length);
            }  
        }
        
    }

    protected long[] encodeAndWriteFile(File f, PrintWriter pw){
        //Ez a külön szedett tömörítési eljárásban definiálandó
        //Az első elem a szemét bitek száma, a második a fájl mérete
        return new long[]{0, 0};
    }
    
    protected void addDefinedHeaderToList(FolderHeader header){
        definedHeaders.add(header);
    }
    
    protected void addDistanceToHeaders(Long distance){
        for(FolderHeader h : definedHeaders){
            if(h instanceof FileHeader fileHeader){
                fileHeader.addDistanceFromHeader(distance);
            }
        }
    }
    
    private int generateId(){
        this.id = this.id + 1;
        return this.id;
    }
    
    //Defined header-ön kéne végig loopolni még mindig
    private void writeHeaders(PrintWriter pw){
        
        for(int i = 0; i < definedHeaders.size(); i++){
            int bytesUsedForHeader = 0;
            if(definedHeaders.get(i) instanceof FileHeader fileHeader){
                //id
                String indicatedBinaryId = fileHeader.getIndicatedStringBinaryId();
                bytesUsedForHeader += writeBinaryToFile(indicatedBinaryId, pw);

                //relatív elhelyezkedés
                bytesUsedForHeader += writeSizeAndBinaryToFile(fileHeader.getDistanceFromHeader(), pw);

                //Tömörített állomány mérete
                bytesUsedForHeader += writeSizeAndBinaryToFile(fileHeader.getFileSize(), pw);

                //szemét bitek
                bytesUsedForHeader += writeBinaryToFile(numberToBinary(fileHeader.getJunkBits()), pw);

                //Név és kiterjesztés
                bytesUsedForHeader += writeBinaryToFile(numberToBinary(fileHeader.getNameAndExtension().length()), pw);
                pw.append(fileHeader.getNameAndExtension());
                bytesUsedForHeader += fileHeader.getNameAndExtension().length();

                //Dátumok
                Calendar calendar = Calendar.getInstance();

                //Módosítás dátuma
                calendar.setTime(fileHeader.getModificationDate());
                bytesUsedForHeader += writeSizeAndBinaryToFile(calendar.get(Calendar.SECOND), pw);

                //Létrehozás dátuma
                calendar.setTime(fileHeader.getCreationDate());
                bytesUsedForHeader += writeSizeAndBinaryToFile(calendar.get(Calendar.SECOND), pw);
            }else{
                //azonosító
                String indicatedBinaryId = definedHeaders.get(i).getIndicatedStringBinaryId();
                bytesUsedForHeader += writeBinaryToFile(indicatedBinaryId, pw);
                
                //Név
                bytesUsedForHeader += writeBinaryToFile(numberToBinary(definedHeaders.get(i).getNameAndExtension().length()), pw);
                pw.append(definedHeaders.get(i).getNameAndExtension());
                bytesUsedForHeader += definedHeaders.get(i).getNameAndExtension().length();
                
                //Létrehozás dátuma
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(definedHeaders.get(i).getCreationDate());
                bytesUsedForHeader += writeSizeAndBinaryToFile(calendar.get(Calendar.SECOND), pw);
            }
            
            //Header távolság hozzáadása a következő headerökhöz
            
            for(int j = i; j < definedHeaders.size(); j++){
                if(definedHeaders.get(j) instanceof FileHeader fileHeader){
                    fileHeader.addDistanceFromHeader(bytesUsedForHeader);
                }
            }
           
        }
    }
    
    private int writeSizeAndBinaryToFile(long num, PrintWriter pw){
        int bytesUsed = 0;
        String s = numberToBinary(num);
        bytesUsed += writeBinaryToFile(numberToBinary(s.length()/8), pw);
        bytesUsed += writeBinaryToFile(s, pw);
        return bytesUsed;
    }
    
       
    private String numberToBinary(long num){
        String binary = Long.toBinaryString(num);
        while(binary.length() % 8 != 0){
            binary = "0" + binary;
        }
        
        return binary;
    }
    
    private int writeBinaryToFile(String binary, PrintWriter pw){
        String s[] = binary.split("(?<=\\G.{8})");
               
        for(int i = 0; i < s.length; i++){
            pw.append((char)Integer.parseInt(s[i], 2));
        }
        
        return s.length;
    }

    protected void writeToFileBegin(PrintWriter pw) {
        
    }
    
}
