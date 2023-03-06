package Model.Huffman;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Huffman {
    private Map<Character, Integer> characterFrequencies;
    private Map<Character, String> huffmanCodes;
    private Node huffmanTreeRoot;
    private StringBuilder huffmanStringTree;

    public Huffman(Map<Character, Integer> characterFrequencies){
        this.characterFrequencies = characterFrequencies;
        this.huffmanCodes = new HashMap<>();
        this.huffmanStringTree = new StringBuilder();
        generateHuffmanTree();
    }

    public Huffman(){

    }

    public void setTree(Node node){
        this.huffmanTreeRoot = node;
    }

    public String getStringHuffmanTree(){
        return this.huffmanStringTree.toString();
    }

    private void generateHuffmanTree(){
        Queue<Node> queue = new PriorityQueue<Node>(new NodeComparator());
        this.characterFrequencies.forEach((character, frequency) -> {
            queue.add(new Leaf(character, frequency));
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
          System.out.print(leafnode.getCharacter());
          return;
        }
        recursivePrintTree(node.getLeftChild());
        recursivePrintTree(node.getRightChild());
        System.out.print("0");
    }

    private void generateHuffmanCodes(Node node, String code){
        if(node instanceof Leaf leafnode){
            this.huffmanCodes.put(leafnode.getCharacter(), code);
            huffmanStringTree.append("1");
            huffmanStringTree.append(leafnode.getCharacter());
            return;
        }
        generateHuffmanCodes(node.getLeftChild(), code.concat("0"));
        generateHuffmanCodes(node.getRightChild(), code.concat("1"));
        this.huffmanStringTree.append("0");
    }
    public String encode(String s){
        StringBuilder sb = new StringBuilder();
        for(char character : s.toCharArray()){
            sb.append(this.huffmanCodes.get(character));
        }
        return sb.toString();
    }

    public String encodeChar(char c){
        return this.huffmanCodes.get(c);
    }

    public String decode(String binary){
        Node currentNode = huffmanTreeRoot;
        StringBuilder decoded = new StringBuilder();
        for(char c : binary.toCharArray()){
            currentNode = (c == '0') ? currentNode.getLeftChild() : currentNode.getRightChild();
            if(currentNode instanceof Leaf){
              decoded.append(((Leaf)currentNode).getCharacter());
              currentNode = huffmanTreeRoot;
            }
        }
        return decoded.toString();
    }


}

class NodeComparator implements Comparator<Node>{

    @Override
    public int compare(Node n1, Node n2) {
        return n1.getFrequency() - n2.getFrequency();
    }

}
