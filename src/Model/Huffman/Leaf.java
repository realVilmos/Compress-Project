package Model.Huffman;
public class Leaf extends Node{
    
    private char character;
    
    public Leaf(char character, int frequency) {
        super(frequency);
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }
}
