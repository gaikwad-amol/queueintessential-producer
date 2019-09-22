package com.sahajsoft.bigo.queueintessential.producer;

import com.sahajsoft.bigo.queueintessential.config.ProducerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

@Component
@Slf4j
@Scope("prototype")
public class ConsumerNIOClient {

  private static final String LAST_MESSAGE = "LAST";
  private static final String END_TAG = "<END>";

  private ProducerProperties producerProperties;
  private SocketChannel socketChannel;
  private boolean isConsumerAvailable = false;
  private BlockingQueue<String> queue;
  private ExecutorService executorService;

  @Autowired
  public ConsumerNIOClient(ProducerProperties producerProperties) {
    this.producerProperties = producerProperties;
  }

  public void startConnection(InetSocketAddress inetSocketAddress) throws IOException {
    socketChannel = SocketChannel.open(inetSocketAddress);
    log.info("is connected - " + socketChannel.isConnected() + " " + this);
    isConsumerAvailable = true;
    queue = new LinkedBlockingDeque<>();
    processMessage();
  }

  public String sendMessage(String message) {
    try {
      queue.put(message);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void processMessage() {
    Integer threads = producerProperties.writerThreads();
    executorService = Executors.newFixedThreadPool(threads, new CustomThreadFactory("WriteToSocket"));
    for (int i = 0; i < threads; i++) {
      executorService.submit(new MessageWriter());
    }
  }

  @PreDestroy
  public void cleanUp() throws Exception {
    socketChannel.close();
    if (!executorService.isShutdown()) {
      executorService.shutdown();
    }
  }

  public boolean isConsumerAvailable() {
    return isConsumerAvailable;
  }

  private class MessageWriter implements Runnable {

    @Override
    public void run() {
      while (true) {
        try {
          String message = queue.take();

          if (!StringUtils.isEmpty(message)) {
            StringBuilder messageContent = new StringBuilder(message);
            messageContent.append(END_TAG);
            ByteBuffer buffer = ByteBuffer.wrap(messageContent.toString().getBytes());
            try {
              socketChannel.write(buffer);
            } catch (IOException e) {
              log.error("exception occurred while writing to socket for message " + messageContent, e);
            }
            if (LAST_MESSAGE.equals(message)) {
              log.info("Sent message as LAST");
            }
          }

        } catch (InterruptedException e) {
          log.error("exception occurred while polling internal queue ", e);
        }
      }

    }
  }
}

