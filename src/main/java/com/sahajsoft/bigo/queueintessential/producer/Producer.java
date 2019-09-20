package com.sahajsoft.bigo.queueintessential.producer;

import com.sahajsoft.bigo.queueintessential.broker.BrokerNIOClient;
import com.sahajsoft.bigo.queueintessential.config.ProducerProperties;
import com.sahajsoft.bigo.queueintessential.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class Producer {

  private BrokerNIOClient brokerClient;
  private ProducerProperties producerProperties;
  private ExecutorService executorService;
  private static int threadNumber = 0;

  @Autowired
  public Producer(BrokerNIOClient brokerClient, ProducerProperties producerProperties) {
    this.brokerClient = brokerClient;
    this.producerProperties = producerProperties;
    executorService = Executors.newFixedThreadPool(producerProperties.threads(), new MyThreadFactory());
  }

  public int sendMessages() {
    startBrokerClientConnection();
    long start = System.currentTimeMillis();
    log.info("kick producer");
    int numberOfFilesSend = sendFilesNew(producerProperties.getFileFolderLocationString());
//    int numberOfFilesSend = sendFiles(getFolderWithFilesToSend());
    log.info("Total number of files sent successfully - " + numberOfFilesSend + " " + (System.currentTimeMillis() - start));
    return numberOfFilesSend;
  }

  private int sendFiles(File folder) {
    Optional<Message> message;
    int numberOfFilesSend = 0;
    //File[] listOfFiles = folder.listFiles();
    String[] listOfFiles = folder.list();
    File file;
    if (listOfFiles != null && listOfFiles.length != 0) {
      log.info("Total number of files to be sent - " + listOfFiles.length);
      for (String filename : listOfFiles) {
        file = new File(folder.getAbsolutePath() + "/" + filename);
        message = Message.createMessage(file);
        if (message.isPresent()) {
          try {
            message.get().send(brokerClient);
            numberOfFilesSend++;
          } catch (IOException e) {
            log.error("Failed to send file with name - " + file.getName(), e);
          }
        }
      }
      Message endMessage = new Message();
      try {
        endMessage.send(brokerClient);
      } catch (IOException e) {
        e.printStackTrace();
      }

    } else {
      log.info("No files present in the folder - " + folder.getAbsolutePath());
    }
    return numberOfFilesSend;
  }

  private int sendFilesNew(String folder) {
    Optional<Message> message;
    AtomicInteger numberOfFilesSend = new AtomicInteger();
    Path dir = FileSystems.getDefault().getPath( folder);
    DirectoryStream<Path> stream = null;
    try {
      stream = Files.newDirectoryStream( dir );
//      StreamSupport.stream(stream.spliterator(), true).forEach(path->{
//        Optional<Message> message = Message.createMessageNew(path);
//        if (message.isPresent()) {
//          try {
//            message.get().send(brokerClient);
//            numberOfFilesSend.getAndIncrement();
//          } catch (IOException e) {
//            log.error("Failed to send file with name - " + path, e);
//          }
//        }
//        });
      for (Path path : stream) {
        executorService.execute(new createAndSendMessage(numberOfFilesSend, path));
      }
      Message endMessage = new Message();
      try {
        endMessage.send(brokerClient);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return numberOfFilesSend.get();
  }

  private void createAndSendMessage(AtomicInteger numberOfFilesSend, Path path) {
    Optional<Message> message;
    message = Message.createMessageNew(path);
    if (message.isPresent()) {
      try {
        message.get().send(brokerClient);
        numberOfFilesSend.getAndIncrement();
      } catch (IOException e) {
        log.error("Failed to send file with name - " + path, e);
      }
    }
  }

  private File getFolderWithFilesToSend() {
    File folder = producerProperties.getFileFolderLocation();
    if (!folder.exists()) {
      throw new RuntimeException("Folder does not exist!");
    }
    return folder;
  }

  private void startBrokerClientConnection() {
    try {
      brokerClient.startConnection(producerProperties.getBrokerIPAddress(), producerProperties.getBrokerSocketPort());
    } catch (IOException e) {
      log.error("Failed to connect the broker - ", e);
      throw new RuntimeException("Could not connect to broker");
    }
  }

  class createAndSendMessage implements Runnable {

    private AtomicInteger numberOfFilesSend;
    private Path path;

    public createAndSendMessage(AtomicInteger numberOfFilesSend, Path path) {
      this.numberOfFilesSend = numberOfFilesSend;
      this.path = path;
    }

    @Override
    public void run() {
      createAndSendMessage(numberOfFilesSend, path);
    }
  }

  class MyThreadFactory implements ThreadFactory {

    public Thread newThread(Runnable r) {
      return new Thread(r, "CreateAndSend" + threadNumber ++);
    }
  }




}
