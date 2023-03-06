package Services;

import Model.FileHeader;
import Model.Folder;
import Model.HierarchyInterface;
import Model.Huffman.Huffman;
import Model.Huffman.Leaf;
import Model.Huffman.Node;
import Model.RAFReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

        //Rekurzív kicsomagolás a Path-re jej
        recursiveDeCompress(folder, to);
    }

    public void recursiveDeCompress(Folder folder, Path to){
        for(HierarchyInterface elem : folder.getChildren()){
          if (elem.getHeader() instanceof FileHeader fh){
            //Fájl elkészítése
            java.io.File innerFile = new File(to + "/" + fh.getNameAndExtension());
            try{
              FileWriter fw = new FileWriter(innerFile, StandardCharsets.ISO_8859_1);

              long fileStart = raf.getFileSize() - fh.getPosFromFileEnd() - fh.getDistanceFromHeader();
              long fileSize = fh.getFileSize();

              byte bytes[] = raf.readBytes(fileStart, fileSize);
              StringBuilder binary = bytesToBinaryString(bytes);
              binary.setLength(binary.length() - fh.getJunkBits());

              //MEGKELL SZEREZNI AZ UTF-8 KARAKTER KÓDOT ÉS AZT BELEIRNI UTF-8 KÓDOLÁSSAL

              String decoded = huffman.decode(binary.toString());

              for(char c : decoded.toCharArray()){
                fw.write(c);
              }

              fw.flush();
              fw.close();
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
            char c = raf.readUTF8char();
            if(c == '1'){
                c = raf.readUTF8char();
                stack.push(new Leaf(c, 0));
            }else if(c == '0'){
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

    public StringBuilder bytesToBinaryString(byte[] bytes){
        StringBuilder binary = new StringBuilder();

        for(byte b : bytes){
          String temp = Integer.toBinaryString(b & 0xFF);
          while(temp.length() < 8){
            temp = "0" + temp;
          }
          binary.append(temp);
        }

        return binary;
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
