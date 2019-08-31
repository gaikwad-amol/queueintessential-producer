package com.sahajsoft.bigo.queueintessential.producer;

import com.sahajsoft.bigo.queueintessential.broker.BrokerClient;
import com.sahajsoft.bigo.queueintessential.config.ProducerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class Producer {

  private BrokerClient brokerClient;
  private ProducerProperties producerProperties;

  @Autowired
  public Producer(BrokerClient brokerClient, ProducerProperties producerProperties) {
    this.brokerClient = brokerClient;
    this.producerProperties = producerProperties;
  }

  public int sendMessages() {
    startBrokerClientConnection();
    File folder = getFolderWithFilesToSend();
    return sendFiles(folder);
  }

  private int sendFiles(File folder) {
    Optional<Message> message;
    int numberOfFilesSend = 0;
    File[] listOfFiles = folder.listFiles(file -> !file.isHidden());
    if (listOfFiles != null && listOfFiles.length != 0) {
      log.info("Total number of files to be sent - " + listOfFiles.length);
      for (File file : listOfFiles) {
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
      log.info("Total number of files sent successfully - " + numberOfFilesSend);
    } else {
      log.info("No files present in the folder - " + folder.getAbsolutePath());
    }
    return numberOfFilesSend;
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

}
