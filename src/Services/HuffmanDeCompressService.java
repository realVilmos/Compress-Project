package Services;

import CompressionProject.ProgressBar;
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
import java.util.List;
import java.util.Stack;
import javax.swing.JOptionPane;

public class HuffmanDeCompressService extends DeCompressService{
    private Huffman huffman = new Huffman();
    private ProgressBar progressBar;
    private final int CHUNK_SIZE = 4 * 1024; //4KB
    private Utils utils;

    public HuffmanDeCompressService(ProgressBar progressBar){
      super();
      this.progressBar = progressBar;
      this.utils = new Utils();
    }
    @Override
    public void deCompress(Folder folder, Path to) {
        try{
          this.progressBar.reset(0);;
          huffman.setTree(reconstructHuffmanTree());
          progressBar.setProgressLength(utils.getNumberOfFiles(folder));

          progressBar.setVisible();

          recursiveDeCompress(folder, to);

          progressBar.hide();
        }catch(IOException e){
          e.printStackTrace();
        }
    }

    public void deCompress(List<HierarchyInterface> list, Path to){
      try{
        Folder root = new Folder();
        for(HierarchyInterface elem : list){
          root.addChild(elem);
        }

        this.progressBar.reset(0);;
        huffman.setTree(reconstructHuffmanTree());
        progressBar.setProgressLength(utils.getNumberOfFiles(root));

        progressBar.setVisible();

        recursiveDeCompress(root, to);

        progressBar.hide();
      }catch(IOException e){
        e.printStackTrace();
      }
    }
    @Override
    public void recursiveDeCompress(Folder folder, Path to){
        for(HierarchyInterface elem : folder.getChildren()){
          if (elem.getHeader() instanceof FileHeader fh){
            //Fájl elkészítése
            java.io.File innerFile = new File(to + "/" + fh.getNameAndExtension());
            if(innerFile.exists()) {
              int res = JOptionPane.showConfirmDialog(null, "A(z) " + innerFile.getName() + " fájl már létezik! Fellül kívánja írni?");
              if(res == JOptionPane.YES_OPTION){
                try{
                  Files.delete(innerFile.toPath());
                }catch (IOException e){
                  e.printStackTrace();
                }
              }else{
                continue;
              }
            }
            try{
              DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(innerFile)));

              long fileStart = raf.getFileSize() - fh.getPosFromFileEnd() - fh.getDistanceFromHeader();
              long fileSize = fh.getFileSize();
              System.out.println("id : "+  fh.getId() + ", fileStart: " + fileStart + ", fileSize:" + fileSize);

              long bytesRead = 0;
              StringBuilder binaryString = new StringBuilder();
              while(bytesRead < fileSize){
                byte[] buffer;
                if(bytesRead + CHUNK_SIZE >= fileSize){
                  buffer = raf.readBytes(fileStart+bytesRead, fileSize-bytesRead);
                  bytesRead = fileSize;
                }else{
                  buffer = raf.readBytes(fileStart+bytesRead, CHUNK_SIZE);
                  bytesRead += CHUNK_SIZE;
                }

                for(byte b : buffer){
                  String temp = Integer.toBinaryString(b & 0xFF);
                  while(temp.length() != 8){
                    temp = "0" + temp;
                  }
                  binaryString.append(temp);
                }

                if(bytesRead == fileSize){
                  binaryString.setLength(binaryString.length()-fh.getJunkBits());
                }

                Node currentNode = huffman.getHuffmanTreeRoot();

                char[] binaryArr = binaryString.toString().toCharArray();
                int writtenUntil = 0;

                for(int i = 0; i < binaryArr.length; i++){
                  currentNode = (binaryArr[i] == '0') ? currentNode.getLeftChild() : currentNode.getRightChild();
                  if(currentNode instanceof Leaf){
                    writtenUntil = i;
                    dos.write(((Leaf)currentNode).getValue());
                    currentNode = huffman.getHuffmanTreeRoot();
                  }
                }

                String leftOver = binaryString.substring(writtenUntil+1, binaryString.length());
                binaryString.setLength(0);
                binaryString.append(leftOver);
              }

              progressBar.increment();

              dos.flush();
              dos.close();
              progressBar.setProgressText("Kicsomagolva: " + innerFile.toPath());
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
                if(stack.size() == 0) return null;
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
