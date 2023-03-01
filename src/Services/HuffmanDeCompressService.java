package Services;

import Model.Huffman.Huffman;
import Model.Huffman.Leaf;
import Model.Huffman.Node;
import Model.RAFReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

public class HuffmanDeCompressService extends DeCompressService{
    Huffman huffman = new Huffman();
    RAFReader raf;

    public HuffmanDeCompressService() throws IOException {
      super();
    }
    @Override
    public void deCompress() throws IOException {
        raf = new RAFReader(new File("Compressed.txt"));
        huffman.setTree(reconstructHuffmanTree());
        huffman.printTree();
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
}
