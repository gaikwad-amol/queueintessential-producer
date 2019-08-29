package com.sahajsoft.bigo.queueintessential;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

@Service
@Slf4j
public class Producer {

  @Autowired
  private Client client;

  public void start() throws IOException, InterruptedException {
    client.startConnection("localhost", 8082);
    File folder = new File("/Users/amolg/Documents/Big O/send");
    if (!folder.exists()) {
      throw new RuntimeException("Folder does not exist!");
    }
    Optional<Message> message;

    File[] listOfFiles = folder.listFiles(file -> !file.isHidden());
    if (listOfFiles != null && listOfFiles.length != 0) {
      for (File file : listOfFiles) {
        message = Message.createMessage(file);
        if (message.isPresent()) {
          message.get().send(client);
        }
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
        log.info("New incoming file::Event kind::" + event.kind() + " Filename:: " + filename);
        File file = new File(filename);
        message = Message.createMessage(file);
        if (message.isPresent()) {
          message.get().send(client);
        }
        file.delete();
      }
      key.reset();
    }
  }

  @PreDestroy
  public void cleanUp() throws Exception {
    client.stopConnection();
  }

}
