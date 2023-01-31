package Model.Huffman;
import java.util.Comparator;

public class Node{
    private Node leftChild;
    private Node rightChild;
    private int frequency;
    
    public Node(Node leftChild, Node rightChild){
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.frequency = leftChild.getFrequency() + rightChild.getFrequency();
    }
    
    public Node(int frequency){
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    public Node getLeftChild() {
        return leftChild;
    }

    public Node getRightChild() {
        return rightChild;
    }
}