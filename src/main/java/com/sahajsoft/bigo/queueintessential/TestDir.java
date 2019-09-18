package com.sahajsoft.bigo.queueintessential;

import com.sahajsoft.bigo.queueintessential.message.Message;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.StreamSupport;

public class TestDir {
    public static void main2( String[] args ) throws IOException, ExecutionException, InterruptedException {
      int maxFiles = 10;
      System.out.println( "TEST BIG DIR" );
      nioRun( "/Users/amolg/Documents/Big O/finalDataSet4Sept2019", maxFiles );
      //nioRun( "/Users/amolg/Documents/Big O/sampleData3Sept2019", maxFiles );
      //ioRun( "/Users/amolg/Documents/Big O/finalDataSet4Sept2019", maxFiles );
    }

   // the classical way
    private static void ioRun( String filePath, int maxFiles )
      throws IOException {
      int i = 1;
      System.out.println( "IO run" );
      long start = System.currentTimeMillis();
      File folder = new File( filePath );
      File[] listOfFiles = folder.listFiles();
      // System.out.println("Total : " + listOfFiles.length);
      for (File file : listOfFiles) {
        Optional<Message> message = Message.createMessage(file);

      }
      long stop = System.currentTimeMillis();
      System.out.println( "Elapsed: " + (stop - start) + " ms" );
    }

   // the new way
    private static void nioRun( String filePath, int maxFiles )
        throws IOException, ExecutionException, InterruptedException {
      int i = 1;
      System.out.println( "NIO run" );
      long start = System.currentTimeMillis();
      Path dir = FileSystems.getDefault().getPath( filePath );
      DirectoryStream<Path> stream = Files.newDirectoryStream( dir );
      //StreamSupport.stream(stream.spliterator(), true).forEach(path->Message.createMessageNew(path)); //193817 ms
      ForkJoinPool forkJoinPool = new ForkJoinPool(4);
      forkJoinPool.submit(()->StreamSupport.stream(stream.spliterator(), true).forEach(path->Message.createMessageNew(path))).get(); ////269683 //319366
//      for (Path path : stream) {
//        //new String(Files.readAllBytes(path));
//        Optional<Message> message = Message.createMessageNew(path);
//      }
      //stream.close();
      long stop = System.currentTimeMillis();
      System.out.println( "Elapsed: " + (stop - start) + " ms" );
    }
}