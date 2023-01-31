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
    
    public Huffman(Map<Character, Integer> characterFrequencies){
        this.characterFrequencies = characterFrequencies;
        this.huffmanCodes = new HashMap<>();
        generateHuffmanTree();
    }
    
    private void generateHuffmanTree(){
        Queue<Node> queue = new PriorityQueue<Node>(new NodeComparator());
        characterFrequencies.forEach((character, frequency) -> {
            queue.add(new Leaf(character, frequency));
        });
        
        while(queue.size() > 1){
            queue.add(new Node(queue.poll(), queue.poll()));
        }
        
        generateHuffmanCodes(queue.poll(), "");
    }
    
    private void generateHuffmanCodes(Node node, String code){
        if(node instanceof Leaf){
            Leaf leaf = (Leaf)node;
            huffmanCodes.put(leaf.getCharacter(), code);
            return;
        }
        generateHuffmanCodes(node.getLeftChild(), code.concat("0"));
        generateHuffmanCodes(node.getRightChild(), code.concat("1"));
    }
    
    private void reconstructTree(){
        
    }
    
    public String encode(String s){
        StringBuilder sb = new StringBuilder();
        for(char character : s.toCharArray()){
            sb.append(huffmanCodes.get(character));
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
