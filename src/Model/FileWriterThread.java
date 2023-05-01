package Model;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class FileWriterThread implements Runnable{
  private final DataOutputStream outputStream;
  private final LinkedBlockingQueue<QueueEntry> queue;
  private int expectedSequence = 1;

  public FileWriterThread(DataOutputStream outputStream, LinkedBlockingQueue<QueueEntry> queue){
    this.outputStream = outputStream;
    this.queue = queue;
  }

  @Override
  public void run(){
    try {
      while (true) {
        if(queue.peek() == null){
          continue;
        }
        if(queue.peek().getSequence() == -1 && queue.size() == 1){
          break;
        }
        QueueEntry entry = queue.take();
        while (entry.getSequence() != expectedSequence) {
          queue.put(entry);
          entry = queue.take();
        }
        outputStream.write(entry.getData());
        expectedSequence++;
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
