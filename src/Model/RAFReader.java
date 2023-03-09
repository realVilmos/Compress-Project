package Model;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RAFReader {
  RandomAccessFile raf;
  long position;
  long fileLength;

  public RAFReader(File file){
    try{
      raf = new RandomAccessFile(file, "r");
      position = 0;
      fileLength = raf.length();
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public byte[] readBytes(long pos, long size){
    byte[] bytes = new byte[(int)size];
    try{
      raf.seek(pos);
      raf.readFully(bytes);
    }catch(IOException e){
      e.printStackTrace();
    }
    return bytes;
  }

  public long getFileSize(){
    try{
      return raf.length();
    }catch(IOException e){
      e.printStackTrace();
    }
    return 0;
  }

  public byte readByte(){
    byte b = 0;
    try{
      raf.seek(position);
      b = raf.readByte();
      position++;
    }catch(IOException e){
      e.printStackTrace();
    }

    return b;
  }

}
