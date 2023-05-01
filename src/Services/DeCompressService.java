package Services;

import CompressionProject.ProgressBar;
import Model.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class DeCompressService {
    private ArrayList<FolderHeader> headers = new ArrayList<>();
    protected int[] contents;
    private int contentCurrPos;
    private final int OPEN_BRACKET = 0x5B;
    private final int CLOSE_BRACKET = 0x5D;
    protected java.io.File compressedFile;
    protected RAFReader raf;

    public Folder buildHierarchyModel(java.io.File f){
        this.compressedFile = f;
        this.raf = new RAFReader(this.compressedFile);
        Folder root = new Folder();
        contents = getFileContentAfterSignature(f);

        //bracketcounter ide hogy megállapítsuk meddig van a hierarchia, tudjuk a határát a header olvasáshoz

        int bracketCounter = 0;
        contentCurrPos = 0;

        while(bracketCounter != -1){
            if(contentCurrPos < contents.length){
                if(contents[contentCurrPos] == OPEN_BRACKET){
                bracketCounter++;
                }else if(contents[contentCurrPos] == CLOSE_BRACKET){
                    bracketCounter--;
                }
            }else{
                System.out.println("Hibás fájl");
                break;
            }

            contentCurrPos++;
        }
        String hierarchy = new String(createByteSubArray(0, contentCurrPos-1));
        root.setChildren(traverseHierarchyTree(hierarchy));
        System.out.println(hierarchy);

        while(contentCurrPos < contents.length){
            buildHeader();
        }

        return root;
    }

    protected void deCompress(Folder folder, Path to){

    }

    protected void deCompress(List<HierarchyInterface> list, Path to){}

    protected void recursiveDeCompress(Folder folder, Path to){}

    private void buildHeader(){
        //Addig kell olvasni az ID-t amíg 1-el kezdődik a binary string
        //Szóval ha nagyobb egyenlő mint 128 akkor a binary stringjét hozzá lehet adni a stringhez
        //ha kisebb mint 128 akkor ki kell egészíteni az elejét nullákkal

        //ID OLVASÁS
        String idBinary = "";
        long tempPos = contentCurrPos;
        //Ez nem fut le alapból ha nem 2 vagy több bájton tárolódik az ID
        while(contents[contentCurrPos] >= 128){
            idBinary += Integer.toBinaryString(contents[contentCurrPos]);
            contentCurrPos++;
        }

        //Ez mindenképp lefut
        String temp = "";

        temp = Integer.toBinaryString(contents[contentCurrPos]);
        while(temp.length() < 8){
            temp = "0" + temp;
        }
        contentCurrPos++;

        idBinary += temp;
        int id = IntegerFromBinaryIndicatedString(idBinary);
        System.out.println(id);

        //meg kell szerezni itt a headert a listából és továbbá neki beállítani a dolgait
        //a listából tudjuk hogy fájl vagy mappa-e és az alapján folytatódik a beolvasás!
        FolderHeader header = null;

        for(FolderHeader fh : headers){
            if(fh.getId() == id){
                header = fh;
                headers.remove(fh);
                break;
            }
        }

        if(header instanceof FileHeader fh){
            fh.setPosFromFileEnd(contents.length - tempPos);
            //readNumber első értéke i, második az olvasott szám
            //Relatív távolság a fejléctől
            fh.setDistanceFromHeader(readNumber());
            System.out.println("relatív távolság: " + fh.getDistanceFromHeader());

            //Tömörített fájl mérete
            fh.setFileSize(readNumber());
            System.out.println("tömörített méret:" + fh.getFileSize());

            //Szemét bitek
            fh.setJunkBits((byte)contents[contentCurrPos]);
            System.out.println("Szemét:" + fh.getJunkBits());
            contentCurrPos++;

            //Név és kiterjesztés mérete
            int size = contents[contentCurrPos];
            contentCurrPos++;

            fh.setNameAndExtension(new String(createByteSubArray(contentCurrPos, contentCurrPos+size)));
            contentCurrPos += size;

            //Módosítás dátuma
            fh.setModificationDate(new Date(readNumber()*1000));

            //Létrehozás dátuma
            fh.setCreationDate(new Date(readNumber()*1000));

        }else{
            //Név és kiterjesztés mérete
            int size = contents[contentCurrPos];
            contentCurrPos++;

            header.setNameAndExtension(new String(createByteSubArray(contentCurrPos, contentCurrPos+size)));
            contentCurrPos += size;

            //Létrehozás dátuma
            header.setCreationDate(new Date(readNumber()*1000));
        }
    }

    private long readNumber(){
        //Bájtok száma amely megállapítja a relatív elhelyezkedést
        int bytesUsed = 0;
        bytesUsed = contents[contentCurrPos];
        System.out.println(bytesUsed);
        contentCurrPos++;

        int bytes[] = new int[bytesUsed];
        for(int j = 0; j < bytesUsed; j++){
            bytes[j] = contents[contentCurrPos];
            contentCurrPos++;
        }

        return bytesToNumber(bytes);
    }

    private long bytesToNumber(int bytes[]){
        String binaryString = "";
        long num = 0;

        for(int i = 0; i < bytes.length; i++){
          num += ((long) (bytes[i] & 0xFF) << (bytes.length-i-1)*8);
        }

        System.out.println(num);
        return num;
    }


    private int IntegerFromBinaryIndicatedString(String binaryIndicatedString){
        if(binaryIndicatedString.length() < 8){
            return Integer.parseInt(binaryIndicatedString, 2);
        }else{
            String binaryString = "";
            String s[] = binaryIndicatedString.split("(?<=\\G.{8})");

            for(int i = 0; i < s.length; i++){
                binaryString += s[i].substring(1, 8);
            }
            return Integer.parseInt(binaryString, 2);
        }

    }

    private ArrayList<HierarchyInterface> traverseHierarchyTree(String contents){
        String currId = "";
        ArrayList<HierarchyInterface> children = new ArrayList<>();

        int charPos = 0;
            while(charPos < contents.length()){

                while(charPos < contents.length() && Character.isDigit(contents.charAt(charPos))){
                    currId += contents.charAt(charPos);
                    charPos++;
                }

                if(charPos != contents.length()){
                    switch(contents.charAt(charPos)){
                        case '[':{
                            charPos++;
                            int folderStart = charPos;
                            int localBracketCounter = 1;

                            //Sub mappa keresés
                            while(localBracketCounter != 0){
                                if(contents.charAt(charPos) == '['){
                                    localBracketCounter++;
                                }else if(contents.charAt(charPos) == ']'){
                                    localBracketCounter--;
                                }
                                charPos++;
                            }

                            Folder folder = new Folder();
                            //Üres, ne nézzük a gyerekeit
                            if(folderStart != (charPos-1)){
                                folder.setChildren(traverseHierarchyTree(contents.substring(folderStart, charPos-1)));
                            }
                            FolderHeader fh = new FolderHeader();
                            fh.setId(Integer.parseInt(currId));

                            folder.setHeader(fh);
                            headers.add(fh);

                            children.add(folder);
                            currId = "";
                        }break;
                        case ';': {
                            charPos++;
                            //itt nem is kell traverse a while miatt
                            //csak add to root new File
                            File file = new File();
                            FileHeader fh = new FileHeader();

                            fh.setId(Integer.parseInt(currId));
                            currId = "";
                            file.setHeader(fh);
                            headers.add(fh);

                            children.add(file);
                        }break;
                    }
                }else{
                    //Legvégén itt van egy fájl amit az if miatt nem olvasott be
                    File file = new File();
                    FileHeader fh = new FileHeader();

                    fh.setId(Integer.parseInt(currId));
                    currId = "";
                    file.setHeader(fh);
                    headers.add(fh);

                    children.add(file);
                }
            }

        return children;
    }

    private int[] getFileContentAfterSignature(java.io.File f){
        try{
          ReverseReader rr = new ReverseReader(f);
          int[] content = rr.readUntilSignature();
          rr.close();
          return content;
        }catch(IOException e){
          e.printStackTrace();
        }
        return new int[]{};
    }

    private byte[] createByteSubArray(int from, int dest){
        int[] subArr = new int[dest-from];
        System.arraycopy(contents, from, subArr, 0, dest-from);

        byte[] byteArr = new byte[dest-from];

        for(int i = 0; i < subArr.length; i++){
          byteArr[i] = (byte)subArr[i];
        }

        return byteArr;
    }
}
