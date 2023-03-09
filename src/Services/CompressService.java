package Services;
import Model.File;
import Model.FileFolderNameComperator;
import Model.FileHeader;
import Model.Folder;
import Model.FolderHeader;
import Model.HierarchyInterface;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;

public abstract class CompressService {

    //Mivel a header megoldások bármely jővőbeli tömörítési eljárásban ugyanazon az elven működnének, itt definiálom őket.

    //Még kell: Hierarchia ábrázolás a végére

    private int id = 0;
    private ArrayList<FolderHeader> definedHeaders;
    protected Folder rootFolder;
    private String hierarchy;

    CompressService(){
        this.definedHeaders = new ArrayList<>();
        this.hierarchy = "";
    }

    CompressService(Folder folder){
        this.rootFolder = folder;
        this.definedHeaders = new ArrayList<>();
        this.hierarchy = "";
    }

    public void initialize(Folder folder){
      this.rootFolder = folder;
    }

    public void compress(){
        initializeHeaders(this.rootFolder);

        try {
            DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("Compressed.txt", true)));

            //Legelejére ha valami kell a tömörítéshez pl ide Huffman Fa
            writeToFileBegin(outputStream);

            compressElementsInFolder(this.rootFolder, outputStream);

            //ide jöhet a 6 bájtos elválasztó "pecsét"
            //BVZ6BP: Bognár Vilmos Zsolt 6 Bájtos Pecsétje :)
            String signature = "BVZ6BP";
            outputStream.writeBytes(signature);
            addDistanceToHeaders((long)signature.length());

            //Hierarchia ábrázolása stringben, ezt esetleg lehet kombinálni a writeHeaderrel hogy közbe történjen
            makeStringHierarchy(this.rootFolder);
            outputStream.writeBytes(hierarchy);
            outputStream.write(']');
            addDistanceToHeaders((long)(hierarchy.length()+1));

            writeHeaders(outputStream);

            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
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

    protected void compressElementsInFolder(Folder folder, DataOutputStream outputStream){
        for(HierarchyInterface Elem : folder.getChildren()){
           if(Elem instanceof Folder){
                compressElementsInFolder((Folder)Elem, outputStream);
                FolderHeader header = Elem.getHeader();
                addDefinedHeaderToList(header);

            }else{
                File f = (File)Elem;
                long returnValues[] = encodeAndWriteFile(f, outputStream);
                byte junkbits = (byte) returnValues[0];
                long length = returnValues[1];

                FileHeader header = (FileHeader) f.getHeader();
                header.setJunkBits(junkbits);
                header.setFileSize(length);
                addDefinedHeaderToList(header);
                addDistanceToHeaders(length);
            }
        }

    }

    protected long[] encodeAndWriteFile(File f, DataOutputStream outputStream){
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
    private void writeHeaders(DataOutputStream outputStream) throws IOException {

        for(int i = 0; i < definedHeaders.size(); i++){
            int bytesUsedForHeader = 0;
            if(definedHeaders.get(i) instanceof FileHeader fileHeader){
                //id
                String indicatedBinaryId = fileHeader.getIndicatedStringBinaryId();
                bytesUsedForHeader += writeBinaryToFile(indicatedBinaryId, outputStream);

                //relatív elhelyezkedés
                bytesUsedForHeader += writeSizeAndBinaryToFile(fileHeader.getDistanceFromHeader(), outputStream);

                //Tömörített állomány mérete
                bytesUsedForHeader += writeSizeAndBinaryToFile(fileHeader.getFileSize(), outputStream);

                //szemét bitek
                bytesUsedForHeader += writeBinaryToFile(numberToBinary(fileHeader.getJunkBits()), outputStream);

                //Név és kiterjesztés
                int length = fileHeader.getNameAndExtension().getBytes(StandardCharsets.UTF_8).length;
                bytesUsedForHeader += writeBinaryToFile(numberToBinary(length), outputStream);
                outputStream.write(fileHeader.getNameAndExtension().getBytes(StandardCharsets.UTF_8));
                bytesUsedForHeader += length;

                //Módosítás dátuma
                bytesUsedForHeader += writeSizeAndBinaryToFile(fileHeader.getModificationDate().getTime()/1000, outputStream);

                //Létrehozás dátuma
                bytesUsedForHeader += writeSizeAndBinaryToFile(fileHeader.getCreationDate().getTime()/1000, outputStream);
            }else{
                //azonosító
                String indicatedBinaryId = definedHeaders.get(i).getIndicatedStringBinaryId();
                bytesUsedForHeader += writeBinaryToFile(indicatedBinaryId, outputStream);

                //Név
                bytesUsedForHeader += writeBinaryToFile(numberToBinary(definedHeaders.get(i).getNameAndExtension().length()), outputStream);
                outputStream.write(definedHeaders.get(i).getNameAndExtension().getBytes(StandardCharsets.UTF_8));
                bytesUsedForHeader += definedHeaders.get(i).getNameAndExtension().length();

                //Létrehozás dátuma
                bytesUsedForHeader += writeSizeAndBinaryToFile(definedHeaders.get(i).getCreationDate().getTime()/1000, outputStream);
            }

            //Header távolság hozzáadása a következő headerökhöz

            for(int j = i; j < definedHeaders.size(); j++){
                if(definedHeaders.get(j) instanceof FileHeader fh){
                  fh.addDistanceFromHeader(bytesUsedForHeader);
                }
            }

        }
    }

    private int writeSizeAndBinaryToFile(long num, DataOutputStream outputStream) throws IOException {
        int bytesUsed = 0;
        String s = numberToBinary(num);
        int length = (int)Math.ceil((double)s.length()/(double)8);
        bytesUsed += writeBinaryToFile(numberToBinary((length == 0) ? 1 : length), outputStream);
        bytesUsed += writeBinaryToFile(s, outputStream);
        System.out.println(bytesUsed);
        return bytesUsed;
    }


    private String numberToBinary(long num){
        String binary = Long.toBinaryString(num);
        while(binary.length() % 8 != 0){
          binary = "0" + binary;
        }

        return binary;
    }

    private int writeBinaryToFile(String binary, DataOutputStream outputStream) throws IOException {
        String s[] = binary.split("(?<=\\G.{8})");

        for(int i = 0; i < s.length; i++){
            outputStream.write(Integer.parseInt(s[i], 2));
        }

        return s.length;
    }

    protected void writeToFileBegin(DataOutputStream pw) throws IOException {

    }

    private void makeStringHierarchy(Folder folder){
        ArrayList<HierarchyInterface> elements = folder.getChildren();
        Collections.sort(elements, new FileFolderNameComperator());
        for(int i = 0; i < elements.size(); i++){
            if(elements.get(i) instanceof Folder){
                this.hierarchy += elements.get(i).getHeader().getId() + "[";
                makeStringHierarchy((Folder)elements.get(i));
                this.hierarchy += "]";
            }else{
                this.hierarchy += elements.get(i).getHeader().getId();
                if(i < elements.size()-1){
                    this.hierarchy += ";";
                }

            }
        }
    }

  public ArrayList<HierarchyInterface> traverseFolder(Folder folder){
    java.io.File subFiles[] = folder.getPath().toFile().listFiles();
    ArrayList<HierarchyInterface> returnList = new ArrayList<HierarchyInterface>();
    try{
      for(java.io.File subFile : subFiles){
        Path subPath = subFile.toPath();
        BasicFileAttributes attr = Files.readAttributes(subPath, BasicFileAttributes.class);
        if(subFile.isDirectory()){
          Folder subFolder = new Folder(subFile.toPath(), attr);
          subFolder.setChildren(traverseFolder(subFolder));
          returnList.add(subFolder);
        }else{
          returnList.add(new File(subFile.toPath(), attr));
        }
      }
    }catch(IOException e){
      e.printStackTrace();
    };
    return returnList;
  }

  public void addElementsToFolder(Folder currentFolder, java.io.File files[]){
    for(java.io.File file : files){
      Path path = file.toPath();
      try {
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);

        if(file.isDirectory()){
          Folder folder = new Folder(path, attr);
          folder.setChildren(traverseFolder(folder));
          currentFolder.addChild(folder);
        }else{
          currentFolder.addChild(new File(path, attr));
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

}
