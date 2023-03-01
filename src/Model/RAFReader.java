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

  public char readUTF8char(){
    char c = (char)0;
    try{
      raf.seek(position);
      byte b = raf.readByte();

      if ((b & 0xFF) < 128) {
        //1-byte character
        c = (char)b;
        position += 1;
        return c;
      }

      if((b & 0xFF) < 224){
        //2-byte character
        byte b2 = raf.readByte();
        //mivel a második feléből jön 6 bit, 6-ot shifteljük balra a keletkezett integeren bellül a bájtot
        c = (char) (((b & 0b00011111) << 6) | (b2 & 0b00111111));
        position += 2;
        return c;
      }

      //3-byte character
      byte b2 = raf.readByte();
      byte b3 = raf.readByte();
      //ugyanaz a logika csak itt harmadokban
      c = (char) (((b & 0b00001111) << 12) | ((b2 & 0b00111111) << 6) | (b3 & 0b00111111));
      position += 3;

      return c;
    }catch(IOException e){
      e.printStackTrace();
    }
    return c;
  }

}
