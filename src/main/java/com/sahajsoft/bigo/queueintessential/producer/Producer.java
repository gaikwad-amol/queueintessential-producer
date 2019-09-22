package com.sahajsoft.bigo.queueintessential.producer;

import com.sahajsoft.bigo.queueintessential.config.ProducerProperties;
import com.sahajsoft.bigo.queueintessential.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class Producer {

  private ProducerProperties producerProperties;
  private ExecutorService executorService;
  private Broker broker;

  @Autowired
  public Producer(ProducerProperties producerProperties, Broker broker) {
    this.broker = broker;
    this.producerProperties = producerProperties;
    executorService = Executors.newFixedThreadPool(producerProperties.producerThreads(), new CustomThreadFactory("CreateAndSendToBroker"));
  }

  public int sendFilesAsMessagesToBroker() {
    long start = System.currentTimeMillis();
    log.info("Producer will start to read directory");
    int numberOfFilesSend = sendFilesAsMessagesToBroker(producerProperties.getFileFolderLocationString());
    log.info("Total number of files sent to broker successfully - " + numberOfFilesSend + " " + (System.currentTimeMillis() - start));
    return numberOfFilesSend;
  }

  private int sendFilesAsMessagesToBroker(String folder) {
    Path dir = FileSystems.getDefault().getPath(folder);
    int numberOfFilesSend = 0;
    DirectoryStream<Path> stream;
    try {
      stream = Files.newDirectoryStream(dir);
      for (Path path : stream) {
        executorService.execute(new MessageProcessor(path));
        numberOfFilesSend++;
      }
      executorService.execute(() -> broker.accept(new Message()));

    } catch (IOException e) {
      log.error("Error occurred while processing files to be sent - ", e);
    }
    return numberOfFilesSend;
  }

  @PreDestroy
  public void cleanUp() throws Exception {
    if (!executorService.isShutdown()) {
      executorService.shutdown();
    }
  }

  private void createAndSendMessage(Path path) {
    Optional<Message> message;
    message = Message.createMessage(path);
    message.ifPresent(value -> broker.accept(value));
  }

  private class MessageProcessor implements Runnable {

    private Path path;

    MessageProcessor(Path path) {
      this.path = path;
    }

    @Override
    public void run() {
      createAndSendMessage(path);
    }
  }
}
