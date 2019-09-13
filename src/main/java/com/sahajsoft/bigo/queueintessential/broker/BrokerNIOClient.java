package com.sahajsoft.bigo.queueintessential.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

@Component
@Slf4j
public class BrokerNIOClient {

  private SocketChannel crunchifyClient;
  //private int count;

  public void startConnection(String ip, int port) throws IOException {
    InetSocketAddress crunchifyAddr = new InetSocketAddress(ip, port);
    crunchifyClient = SocketChannel.open(crunchifyAddr);
    log.info("is connected - " + crunchifyClient.isConnected());
  }

  public String sendMessage(String message) throws IOException {
    //count++;
    //log.info("Message sent -" + message);
    //log.info("message sent count  - " + count);
    String newMessage = message + "<END>";
    ByteBuffer buffer = ByteBuffer.wrap(newMessage.getBytes());
    crunchifyClient.write(buffer);
    return null;
  }

  @PreDestroy
  public void cleanUp() throws Exception {
    crunchifyClient.close();
  }
}
