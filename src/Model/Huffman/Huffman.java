package Model.Huffman;
import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Huffman {
    private Map<Byte, Integer> characterFrequencies;
    private Map<Byte, String> huffmanCodes;
    private Node huffmanTreeRoot;
    private ByteArrayOutputStream huffmanTree;

    public Huffman(Map<Byte, Integer> byteFrequencies){
        this.characterFrequencies = byteFrequencies;
        this.huffmanCodes = new HashMap<>();
        this.huffmanTree = new ByteArrayOutputStream();
        generateHuffmanTree();
    }

    public Huffman(){

    }

    public void setTree(Node node){
        this.huffmanTreeRoot = node;
    }

    public byte[] getHuffmanTree(){
        return this.huffmanTree.toByteArray();
    }

    private void generateHuffmanTree(){
        Queue<Node> queue = new PriorityQueue<Node>(new NodeComparator());
        this.characterFrequencies.forEach((byt, frequency) -> {
            queue.add(new Leaf(byt, frequency));
        });

        while(queue.size() > 1){
            queue.add(new Node(queue.poll(), queue.poll()));
        }
        this.huffmanTreeRoot = queue.poll();
        generateHuffmanCodes(this.huffmanTreeRoot, "");
    }

    public void printTree(){
      recursivePrintTree(this.huffmanTreeRoot);
    }
    public void recursivePrintTree(Node node){
        if(node instanceof Leaf leafnode){
          System.out.print("1");
          System.out.print("("+leafnode.getValue()+")");
          System.out.print("");
          return;
        }
        recursivePrintTree(node.getLeftChild());
        recursivePrintTree(node.getRightChild());
        System.out.print("0");
    }

    private void generateHuffmanCodes(Node node, String code){
        if(node == null){
          return;
        }
        if(node instanceof Leaf leafnode){
            this.huffmanCodes.put(leafnode.getValue(), code);
            huffmanTree.write('1');
            huffmanTree.write(leafnode.getValue());
            return;
        }
        generateHuffmanCodes(node.getLeftChild(), code.concat("0"));
        generateHuffmanCodes(node.getRightChild(), code.concat("1"));
        huffmanTree.write('0');
    }

    public Map<Byte, String> getHuffmanCodesMap(){
      return this.huffmanCodes;
    }

    public Node getHuffmanTreeRoot(){
      return this.huffmanTreeRoot;
    }


}

class NodeComparator implements Comparator<Node>{

    @Override
    public int compare(Node n1, Node n2) {
        return n1.getFrequency() - n2.getFrequency();
    }

}
