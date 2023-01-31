package Services;
import Model.File;
import Model.FileHeader;
import Model.Folder;
import Model.FolderHeader;
import Model.HierarchyInterface;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public abstract class CompressService {
    
    //Mivel a header megoldások bármely jővőbeli tömörítési eljárásban ugyanazon az elven működnének, itt definiálom őket.
    
    private int id = 0;
    private ArrayList<FileHeader> definedHeaders = new ArrayList<>();
    private Folder rootFolder;
    
    CompressService(Folder folder){
        this.rootFolder = folder;
    }
    
    public void compress(){   
        initializeHeaders(this.rootFolder);
 
        try {
            PrintWriter pw = new PrintWriter("Merged.txt");
            compressElementsInFolder(this.rootFolder, pw);
            
            //ide jöhet a 6 bájtos elválasztó "pecsét"
            //Bognár Vilmos 6 Bájtos Pecsétje :)
            pw.write("BV6BP");
            //valami makeHeader/writeHeader ami bele is írja már a fájlba ÉS hozzáadja a távolságot a többi headerhöz

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
            
            Elem.setHeader(header);
            
            if(Elem instanceof Folder) initializeHeaders((Folder)Elem);       
        }
    }
    
    protected void compressElementsInFolder(Folder folder, PrintWriter pw){
        for(HierarchyInterface Elem : folder.getChildren()){
           if(Elem instanceof Folder){
                //Itt is csinálni Headert
                compressElementsInFolder((Folder)Elem, pw);
                
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
    
    protected void addDefinedHeaderToList(FileHeader header){
        definedHeaders.add(header);
    }
    
    protected void addDistanceToHeaders(Long distance){
        for(FileHeader h : definedHeaders){
            h.addDistanceFromHeader(distance);
        }
    }
    
    private int generateId(){
        this.id = this.id + 1;
        return this.id;
    }
    
    
}
