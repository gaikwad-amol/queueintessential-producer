package com.sahajsoft.bigo.queueintessential.broker;

import com.sahajsoft.bigo.queueintessential.config.ProducerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BrokerNIOClient {

  @Autowired
  private ProducerProperties producerProperties;
  private SocketChannel crunchifyClient;
  private ByteBuffer buffer;
  private BlockingQueue<String> queue;
  //private int count;

  public void startConnection(String ip, int port) throws IOException {
    InetSocketAddress crunchifyAddr = new InetSocketAddress(ip, port);
    crunchifyClient = SocketChannel.open(crunchifyAddr);
    log.info("is connected - " + crunchifyClient.isConnected());
    queue = new LinkedBlockingDeque<>(producerProperties.getQueueCapacity());
    processMessage();
  }

  public String sendMessage(String message) throws IOException {
    //count++;
    //log.info("Message sent -" + message);
    //log.info("message sent count  - " + count);
    String newMessage = message + "<END>";
    //buffer = ByteBuffer.wrap(newMessage.getBytes());
    //crunchifyClient.write(buffer);
    try {
      queue.put(newMessage);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void processMessage() throws IOException {
    Integer threads = producerProperties.threads();
    ExecutorService executorService = Executors.newFixedThreadPool(threads);
    for (int i = 0; i < threads; i++) {
      executorService.submit(new MessageWriter());
    }
//    while (true) {
//      String message = queue.poll();
//      if (!StringUtils.isEmpty(message)) {
//
//        buffer = ByteBuffer.wrap(message.getBytes());
//        crunchifyClient.write(buffer);
//      }
//      //crunchifyClient.write(buffer);
//    }
  }

  @PreDestroy
  public void cleanUp() throws Exception {
    crunchifyClient.close();
  }

  class MessageWriter implements Runnable {

    @Override
    public void run() {
      Thread.currentThread().setName("MessageWriter" + Math.random());
      while (true) {
        try {
          String message = queue.take();
          if (!StringUtils.isEmpty(message)) {
            buffer = ByteBuffer.wrap(message.getBytes());
            crunchifyClient.write(buffer);
            //log.info(" wrote by " + Thread.currentThread().getName());
          }
        } catch (InterruptedException | IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
