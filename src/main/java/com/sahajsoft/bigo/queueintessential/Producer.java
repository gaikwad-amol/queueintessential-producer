package com.sahajsoft.bigo.queueintessential;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
public class Producer {

  public void start() throws IOException, InterruptedException {
    File folder = new File("/Users/amolg/Documents/Big O/send");
    if (!folder.exists()) {
      throw new RuntimeException("Folder does not exist!");
    }
    Message message;

    File[] listOfFiles = folder.listFiles(file -> !file.isHidden());
    if (listOfFiles != null && listOfFiles.length != 0) {
      for (File file : listOfFiles) {
        message = Message.createMessage(file);
        message.send();
        file.delete();
      }
    }

    WatchService watchService
        = FileSystems.getDefault().newWatchService();

    Path path = Paths.get(folder.getAbsolutePath());
    path.register(
        watchService,
        StandardWatchEventKinds.ENTRY_CREATE);

    WatchKey key;
    while ((key = watchService.take()) != null) {
      for (WatchEvent<?> event : key.pollEvents()) {
        String filename = "/Users/amolg/Documents/Big O/send/" + event.context();
        System.out.println("New incoming file::Event kind::" + event.kind() + " Filename:: " + filename);
        File file = new File(filename);
        message = Message.createMessage(file);
        message.send();
        file.delete();
      }
      key.reset();
    }
  }

}
