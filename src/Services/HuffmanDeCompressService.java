package Services;

import Model.FileHeader;
import Model.Folder;
import Model.HierarchyInterface;
import Model.Huffman.Huffman;
import Model.Huffman.Leaf;
import Model.Huffman.Node;
import Model.RAFReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Stack;

public class HuffmanDeCompressService extends DeCompressService{
    Huffman huffman = new Huffman();
    RAFReader raf;

    public HuffmanDeCompressService() throws IOException {
      super();
    }
    @Override
    public void deCompress(Folder folder, Path to) throws IOException {
        raf = new RAFReader(new File("Compressed.txt"));
        huffman.setTree(reconstructHuffmanTree());
        huffman.printTree();

        //Rekurzív kicsomagolás a Path-re jej
        recursiveDeCompress(folder, to);
    }

    public void recursiveDeCompress(Folder folder, Path to){
        for(HierarchyInterface elem : folder.getChildren()){
          if (elem.getHeader() instanceof FileHeader fh){
            //Fájl elkészítése
            java.io.File innerFile = new File(to + "/" + fh.getNameAndExtension());
            try{
              DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(innerFile)));

              long fileStart = raf.getFileSize() - fh.getPosFromFileEnd() - fh.getDistanceFromHeader();
              long fileSize = fh.getFileSize();
              System.out.println("id : "+  fh.getId() + ", fileStart: " + fileStart + ", fileSize:" + fileSize);

              byte bytes[] = raf.readBytes(fileStart, fileSize);
              System.out.println(Arrays.toString(bytes));
              byte[] decoded = huffman.decode(bytes, fh.getJunkBits());
              dos.write(decoded);

              dos.flush();
              dos.close();
              System.out.println("Kész: " + innerFile.toPath());
              Files.setAttribute(innerFile.toPath(), "creationTime", FileTime.fromMillis(fh.getCreationDate().getTime()));
              Files.setAttribute(innerFile.toPath(), "lastModifiedTime", FileTime.fromMillis(fh.getCreationDate().getTime()));
            }catch(IOException e){
              e.printStackTrace();
            }
          }else if(elem instanceof Folder f){
            //MKDIR jöhet a path + mappa nevére és akkor az a path passed down

            java.io.File innerFolder = new File(to.toString() + "/" + f.getHeader().getNameAndExtension());
            try{
              innerFolder.mkdir();
              Files.setAttribute(innerFolder.toPath(), "creationTime", FileTime.fromMillis(f.getHeader().getCreationDate().getTime()));
            }catch(IOException e){
              e.printStackTrace();
            }

            recursiveDeCompress(f, innerFolder.toPath());
          }
        }
    }

    public Node reconstructHuffmanTree() throws IOException {
        Stack<Node> stack = new Stack();

        do{
            byte b = raf.readByte();
            if(b == '1'){
                b = raf.readByte();
                stack.push(new Leaf(b, 0));
            }else if(b == '0'){
                if(stack.size() == 1){
                    //megvan a gyökér
                    return stack.pop();
                }else{
                    Node right = stack.pop();
                    Node left = stack.pop();
                    stack.push(new Node(left, right));
                }
            }
        }while(stack.size() > 0);

        return null;
    }
}
