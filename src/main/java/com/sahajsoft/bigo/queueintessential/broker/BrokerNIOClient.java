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

  public void startConnection(String ip, int port) throws IOException {
    InetSocketAddress crunchifyAddr = new InetSocketAddress(ip, port);
    crunchifyClient = SocketChannel.open(crunchifyAddr);
    log.info("is connected - " + crunchifyClient.isConnected());
  }

  public String sendMessage(String message) throws IOException {
    ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
    crunchifyClient.write(buffer);
    return null;
  }

  @PreDestroy
  public void cleanUp() throws Exception {
    crunchifyClient.close();
  }
}
