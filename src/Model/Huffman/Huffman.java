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
    private String huffmanStringTree;
    
    public Huffman(Map<Character, Integer> characterFrequencies){
        this.characterFrequencies = characterFrequencies;
        this.huffmanCodes = new HashMap<>();
        this.huffmanStringTree = "";
        generateHuffmanTree();
        
    }
    
    public String getStringHuffmanTree(){
        return this.huffmanStringTree;
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
    
    private void generateHuffmanCodes(Node node, String code){
        if(node instanceof Leaf leafnode){
            this.huffmanCodes.put(leafnode.getCharacter(), code);
            this.huffmanStringTree += ("1" + leafnode.getCharacter());
            return;
        }
        generateHuffmanCodes(node.getLeftChild(), code.concat("0"));
        generateHuffmanCodes(node.getRightChild(), code.concat("1"));
        this.huffmanStringTree += "0";
    }
    
    private void reconstructTree(){
        
    }
    
    public String encode(String s){
        StringBuilder sb = new StringBuilder();
        for(char character : s.toCharArray()){
            sb.append(this.huffmanCodes.get(character));
        }
        return sb.toString();
    }
    
    public String decode(String s){
        return s;
    }
}

class NodeComparator implements Comparator<Node>{

    @Override
    public int compare(Node n1, Node n2) {
        return n1.getFrequency() - n2.getFrequency();
    }
    
}
