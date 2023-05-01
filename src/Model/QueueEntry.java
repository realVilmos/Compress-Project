package Model;

public class QueueEntry {
  private final int sequence;
  private final byte[] data;

  public QueueEntry(int sequence, byte[] data){
    this.sequence = sequence;
    this.data = data;
  }

  public int getSequence() {
    return sequence;
  }

  public byte[] getData() {
    return data;
  }
}
