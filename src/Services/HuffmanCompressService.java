package Services;

import CompressionProject.ProgressBar;
import Model.File;
import Model.Folder;
import Model.HierarchyInterface;
import Model.Huffman.Huffman;
import Model.QueueEntry;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class HuffmanCompressService extends CompressService {

    private Huffman huffman;
    private Map<Byte, Integer> byteFrequencies;
    private final int CHUNK_SIZE = 4 * 1024; //4KB
    private int sequence;

    public HuffmanCompressService(Folder folder, ProgressBar progressBar){
        super(folder, progressBar);
        this.byteFrequencies = new HashMap<>();
        getAllByteFrequencies(folder, byteFrequencies);
        this.huffman = new Huffman(byteFrequencies);
        sequence = 0;
    }

    public HuffmanCompressService(ProgressBar progressBar){
        super(progressBar);
        sequence = 0;
    }
    @Override
    public void initialize(Folder folder){
        super.rootFolder = folder;
        this.progressBar.reset(0);
        this.byteFrequencies = new HashMap<>();
        progressBar.setProgressText("Huffman fa elkészítése");
        getAllByteFrequencies(folder, byteFrequencies);
        this.huffman = new Huffman(byteFrequencies);
    }

    @Override
    protected long[] encodeAndWriteFile(File file, LinkedBlockingQueue<QueueEntry> queue, ExecutorService executor){
      long length;
      byte junkbits;

      length = 0;
      junkbits = 0;

      try {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file.getPath().toFile()));
        final Map<Byte, String> huffmanCodes = huffman.getHuffmanCodesMap();

        byte[] buffer = new byte[CHUNK_SIZE];
        int bytesRead;

        StringBuilder sb = new StringBuilder();
        while ((bytesRead = bis.read(buffer)) != -1) {
          for(int i = 0; i < bytesRead; i++){
            sb.append(huffmanCodes.get(buffer[i]));
          }

          int remainder = sb.length() % 8;
          String temp = "";

          if(remainder != 0){
            temp = sb.substring(sb.length()-remainder, sb.length());
            sb.setLength(sb.length()-remainder);
          }
          sequence++;
          while(((ThreadPoolExecutor)executor).getActiveCount() >= ((ThreadPoolExecutor)executor).getCorePoolSize()) {
            Thread.sleep(100);
          }
          executor.submit(new ProcessHuffmanChunk(sequence, sb.toString(), queue));
          length += sb.length()/8;
          sb.setLength(0);
          sb.append(temp);
        }

        //Maradt szemét, de ez mehet ahogy van
        if(sb.length() > 0){
          sequence++;
          executor.submit(new ProcessHuffmanChunk(sequence, sb.toString(), queue));

          length += (int)Math.ceil((double)sb.length()/(double)8);
          junkbits = (byte)(8-sb.length());
        }

      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      return new long[]{length, junkbits};
    }

    @Override
    protected void writeToFileBegin(DataOutputStream outputStream) throws IOException {
        byte[] huffmanTree = huffman.getHuffmanTree();
        for(byte b : huffmanTree){
            outputStream.write(b);
        }
        outputStream.write('0');
    }

    private void getAllByteFrequencies(Folder folder, Map<Byte, Integer> characterFrequencies){
        for(HierarchyInterface Elem : folder.getChildren()){
            if(Elem instanceof Folder){
                getAllByteFrequencies((Folder)Elem, characterFrequencies);
            }else{
                try{
                    File f = (File)Elem;
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f.getPath().toFile()));
                    byte[] buffer = new byte[CHUNK_SIZE];
                    int bytesRead;

                    while((bytesRead = bis.read(buffer)) != -1){
                        for(int i = 0; i < bytesRead; i++){
                          Integer currentFrequency = byteFrequencies.get(buffer[i]);
                          characterFrequencies.put(buffer[i], (currentFrequency  == null) ? 1 : currentFrequency + 1);
                        }
                    }

                    bis.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}

class ProcessHuffmanChunk implements Runnable {
  int sequence;
  String chunk;
  LinkedBlockingQueue<QueueEntry> queue;

  ProcessHuffmanChunk(int sequence, String chunk, LinkedBlockingQueue<QueueEntry> queue){
    this.sequence = sequence;
    this.chunk = chunk;
    this.queue = queue;
  }
  public void run() {
    String[] binaryByteStrings = chunk.split(("(?<=\\G.{8})"));
    byte[] data = new byte[binaryByteStrings.length];

    for(int i = 0; i < data.length; i++){
      data[i] = (byte)Integer.parseInt(binaryByteStrings[i], 2);
    }

    try {
      queue.put(new QueueEntry(sequence, data));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    data = null;
    chunk = null;
    queue = null;
    sequence = 0;
  }
}
