package Services;
import CompressionProject.ProgressBar;
import Model.*;
import Model.File;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;

public abstract class CompressService {
  private int id = 0;
  private ArrayList<FolderHeader> definedHeaders;
  protected Folder rootFolder;
  private String hierarchy;
  protected ProgressBar progressBar;
  private Utils utils;

  CompressService(ProgressBar progressBar) {
    this.definedHeaders = new ArrayList<>();
    this.hierarchy = "";
    this.progressBar = progressBar;
    utils = new Utils();
  }

  CompressService(Folder folder, ProgressBar progressBar) {
    this.rootFolder = folder;
    this.definedHeaders = new ArrayList<>();
    this.hierarchy = "";
    this.progressBar = progressBar;
    utils = new Utils();
  }

  public void initialize(Folder folder) {
    this.rootFolder = folder;
  }

  public void compress(java.io.File to) {
    this.progressBar.reset(0);;
    progressBar.setProgressLength(utils.getNumberOfFiles(this.rootFolder));
    progressBar.setVisible();
    initializeHeaders(this.rootFolder);

    try {
      DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(to.toPath() + ".arc", true)));

      //Legelejére ha valami kell a tömörítéshez pl ide Huffman Fa
      writeToFileBegin(outputStream);

      int threadPool = Runtime.getRuntime().availableProcessors();
      ExecutorService executor = Executors.newFixedThreadPool(threadPool);

      LinkedBlockingQueue<QueueEntry> queue = new LinkedBlockingQueue<>();

      Thread writerThread = new Thread(new FileWriterThread(outputStream, queue));
      writerThread.start();

      compressElementsInFolder(this.rootFolder, queue, executor);

      executor.shutdown();
      queue.put(new QueueEntry(-1, new byte[0]));

      writerThread.join();

      //ide jöhet a 6 bájtos elválasztó "pecsét"
      //BVZ6BP: Bognár Vilmos Zsolt 6 Bájtos Pecsétje :)
      String signature = "BVZ6BP";
      outputStream.writeBytes(signature);
      addDistanceToHeaders((long) signature.length());

      makeStringHierarchy(this.rootFolder);
      outputStream.writeBytes(hierarchy);
      outputStream.write(']');
      addDistanceToHeaders((long) (hierarchy.length() + 1));

      writeHeaders(outputStream);

      outputStream.flush();
      outputStream.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    progressBar.hide();
  }

  protected void initializeHeaders(Folder folder) {
    for (HierarchyInterface Elem : folder.getChildren()) {
      FolderHeader header = (Elem instanceof Folder) ? new FolderHeader() : new FileHeader();
      header.setId(generateId());
      header.setNameAndExtension(Elem.getName());
      header.setCreationDate(Elem.getCreationDate());

      if (header instanceof FileHeader fileHeader) {
        fileHeader.setModificationDate(Elem.getLastModifiedDate());
      }

      Elem.setHeader(header);

      if (Elem instanceof Folder) initializeHeaders((Folder) Elem);
    }
  }

  protected void compressElementsInFolder(Folder folder, LinkedBlockingQueue<QueueEntry> queue, ExecutorService executor) {
    for (HierarchyInterface Elem : folder.getChildren()) {
      if (Elem instanceof Folder) {
        compressElementsInFolder((Folder) Elem, queue, executor);
        FolderHeader header = Elem.getHeader();
        addDefinedHeaderToList(header);
      } else {
        File f = (File) Elem;
        progressBar.setProgressText(f.getPath().toString() + " tömörítése");
        long returnValues[] = encodeAndWriteFile(f, queue, executor);
        long length = returnValues[0];
        byte junkbits = (byte) returnValues[1];


        FileHeader header = (FileHeader) f.getHeader();
        header.setJunkBits(junkbits);
        header.setFileSize(length);
        addDefinedHeaderToList(header);
        addDistanceToHeaders(length);
        progressBar.increment();
      }
    }

  }

  protected long[] encodeAndWriteFile(File file, LinkedBlockingQueue<QueueEntry> queue, ExecutorService executor) {
    //Ez a külön szedett tömörítési eljárásban definiálandó
    //Az első elem a szemét bitek száma, a második a fájl mérete
    return new long[]{0, 0};
  }

  protected void addDefinedHeaderToList(FolderHeader header) {
    definedHeaders.add(header);
  }

  protected void addDistanceToHeaders(Long distance) {
    for (FolderHeader h : definedHeaders) {
      if (h instanceof FileHeader fileHeader) {
        fileHeader.addDistanceFromHeader(distance);
      }
    }
  }

  private int generateId() {
    this.id = this.id + 1;
    return this.id;
  }

  //Defined header-ön kéne végig loopolni még mindig
  private void writeHeaders(DataOutputStream outputStream) throws IOException {

    for (int i = 0; i < definedHeaders.size(); i++) {
      int bytesUsedForHeader = 0;
      if (definedHeaders.get(i) instanceof FileHeader fileHeader) {
        //id
        String indicatedBinaryId = fileHeader.getIndicatedStringBinaryId();
        bytesUsedForHeader += writeBinaryToFile(indicatedBinaryId, outputStream);

        //relatív elhelyezkedés
        bytesUsedForHeader += writeSizeAndBinaryToFile(fileHeader.getDistanceFromHeader(), outputStream);

        //Tömörített állomány mérete
        bytesUsedForHeader += writeSizeAndBinaryToFile(fileHeader.getFileSize(), outputStream);

        //szemét bitek
        bytesUsedForHeader += writeBinaryToFile(numberToBinary(fileHeader.getJunkBits()), outputStream);

        //Név és kiterjesztés
        int length = fileHeader.getNameAndExtension().getBytes(StandardCharsets.UTF_8).length;
        bytesUsedForHeader += writeBinaryToFile(numberToBinary(length), outputStream);
        outputStream.write(fileHeader.getNameAndExtension().getBytes(StandardCharsets.UTF_8));
        bytesUsedForHeader += length;

        //Módosítás dátuma
        bytesUsedForHeader += writeSizeAndBinaryToFile(fileHeader.getModificationDate().getTime() / 1000, outputStream);

        //Létrehozás dátuma
        bytesUsedForHeader += writeSizeAndBinaryToFile(fileHeader.getCreationDate().getTime() / 1000, outputStream);
      } else {
        //azonosító
        String indicatedBinaryId = definedHeaders.get(i).getIndicatedStringBinaryId();
        bytesUsedForHeader += writeBinaryToFile(indicatedBinaryId, outputStream);

        //Név
        bytesUsedForHeader += writeBinaryToFile(numberToBinary(definedHeaders.get(i).getNameAndExtension().getBytes(StandardCharsets.UTF_8).length), outputStream);
        outputStream.write(definedHeaders.get(i).getNameAndExtension().getBytes(StandardCharsets.UTF_8));
        bytesUsedForHeader += definedHeaders.get(i).getNameAndExtension().getBytes(StandardCharsets.UTF_8).length;

        //Létrehozás dátuma
        bytesUsedForHeader += writeSizeAndBinaryToFile(definedHeaders.get(i).getCreationDate().getTime() / 1000, outputStream);
      }

      //Header távolság hozzáadása a következő headerökhöz

      for (int j = i; j < definedHeaders.size(); j++) {
        if (definedHeaders.get(j) instanceof FileHeader fh) {
          fh.addDistanceFromHeader(bytesUsedForHeader);
        }
      }

    }
  }

  private int writeSizeAndBinaryToFile(long num, DataOutputStream outputStream) throws IOException {
    int bytesUsed = 0;
    String s = numberToBinary(num);
    int length = (int) Math.ceil((double) s.length() / (double) 8);
    bytesUsed += writeBinaryToFile(numberToBinary((length == 0) ? 1 : length), outputStream);
    bytesUsed += writeBinaryToFile(s, outputStream);
    return bytesUsed;
  }


  private String numberToBinary(long num) {
    String binary = Long.toBinaryString(num);
    while (binary.length() % 8 != 0) {
      binary = "0" + binary;
    }

    return binary;
  }

  private int writeBinaryToFile(String binary, DataOutputStream outputStream) throws IOException {
    String s[] = binary.split("(?<=\\G.{8})");

    for (int i = 0; i < s.length; i++) {
      outputStream.write(Integer.parseInt(s[i], 2));
    }

    return s.length;
  }

  protected void writeToFileBegin(DataOutputStream pw) throws IOException {

  }

  private void makeStringHierarchy(Folder folder) {
    ArrayList<HierarchyInterface> elements = folder.getChildren();
    Collections.sort(elements, new FileFolderNameComperator());
    for (int i = 0; i < elements.size(); i++) {
      if (elements.get(i) instanceof Folder) {
        this.hierarchy += elements.get(i).getHeader().getId() + "[";
        makeStringHierarchy((Folder) elements.get(i));
        this.hierarchy += "]";
      } else {
        this.hierarchy += elements.get(i).getHeader().getId();
        if (i < elements.size() - 1) {
          this.hierarchy += ";";
        }

      }
    }
  }
}


