package Model;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class ReverseReader {
  RandomAccessFile raf;

  long position;

  public ReverseReader(File f) throws IOException {
    raf = new RandomAccessFile(f, "r");
    position = raf.length() -1;
  }

  public int[] readUntilSignature() throws IOException {
    List<Integer> unsignedBytes = new ArrayList<>();
    int c;

    //BVZ6BP
    int[] signatureBytes = new int[]{66, 86, 90, 54, 66, 80};

    do{
      c = readNextUnsignedByte();
      unsignedBytes.add(c);

      int arrSize = unsignedBytes.size();
      if(arrSize > signatureBytes.length){
        int[] subArr = unsignedBytes.subList(arrSize-6, arrSize).stream().mapToInt(i->i).toArray();
        int conditions = 0;
        if(subArr[0] == signatureBytes[5]) conditions++;
        if(subArr[1] == signatureBytes[4]) conditions++;
        if(subArr[2] == signatureBytes[3]) conditions++;
        if(subArr[3] == signatureBytes[2]) conditions++;
        if(subArr[4] == signatureBytes[1]) conditions++;
        if(subArr[5] == signatureBytes[0]) conditions++;
        if(conditions == 6) break;
      }

    }while(position > -1);

    for(int i = 0; i < signatureBytes.length; i++){
      unsignedBytes.remove(unsignedBytes.size()-1);
    }

    Collections.reverse(unsignedBytes);

    int[] arr = unsignedBytes.stream().mapToInt(i -> i).toArray();

    return arr;
  }

  private int readNextUnsignedByte() throws IOException {
    raf.seek(position);
    int c = raf.readUnsignedByte();
    position--;
    return c;
  }

  public void close() throws IOException {
    if(raf != null){
      raf.close();
      raf = null;
    }
  }

  public boolean ready(){
    return (position>0) ? true : false;
  }

}
