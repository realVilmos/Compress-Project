package Model.Huffman;
public class Leaf extends Node{

    private byte binaryValue;

    public Leaf(byte value, int frequency) {
        super(frequency);
        this.binaryValue = value;
    }

    public byte getValue() {
        return binaryValue;
    }
}
