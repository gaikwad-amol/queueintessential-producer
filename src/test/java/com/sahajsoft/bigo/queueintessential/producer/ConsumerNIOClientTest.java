package com.sahajsoft.bigo.queueintessential.producer;

import com.sahajsoft.bigo.queueintessential.config.ProducerProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConsumerNIOClientTest {

  private ConsumerNIOClient consumerNIOClient;
  private ProducerProperties producerProperties;
  Server server;
  String result;

  @BeforeEach
  public void setUp() {
    producerProperties = mock(ProducerProperties.class);
    when(producerProperties.writerThreads()).thenReturn(2);
    consumerNIOClient = new ConsumerNIOClient(producerProperties);
  }

  @AfterEach
  public void tearDown() throws IOException {
    server.stop();
  }

  @Test
  public void shouldConnectToServerSuccessfully() throws IOException, InterruptedException {
    givenServerIsRunning();
    consumerNIOClient.startConnection(serverAddress());

    Assertions.assertTrue(consumerNIOClient.isConsumerAvailable());
  }

  private InetSocketAddress serverAddress() throws UnknownHostException {
    byte[] byteArr = new byte[]{(byte) 127, (byte) 0, (byte) 0, (byte) 1};
    return new InetSocketAddress(InetAddress.getByAddress("localhost", byteArr), 6666);
  }

  private void givenServerIsRunning() throws InterruptedException {
    Thread t = new Thread(() -> {
      server = new Server();
      try {
        server.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    t.start();
    Thread.sleep(5000);
  }


  class Server {

    private ServerSocketChannel serverSocketChannel;

    public void start() throws IOException {
      Selector selector = Selector.open();
      serverSocketChannel = ServerSocketChannel.open();
      InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 6666);

      serverSocketChannel.bind(inetSocketAddress);
      serverSocketChannel.configureBlocking(false);

      while (true) {
        selector.select();

        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
        while (selectionKeyIterator.hasNext()) {
          SelectionKey myKey = selectionKeyIterator.next();

          if (myKey.isAcceptable()) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            System.out.println("Connection Accepted: " + socketChannel.getLocalAddress() + "\n");

          }
          else if (myKey.isReadable()) {
            System.out.println("ere");
            SocketChannel socketChannel = (SocketChannel) myKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
            socketChannel.read(byteBuffer);
            byteBuffer.flip();
            result = new String(byteBuffer.array()).trim();
          }
          selectionKeyIterator.remove();
        }
      }
    }

    public void stop() throws IOException {
      if(serverSocketChannel != null) {
        serverSocketChannel.close();
      }
    }
  }
}