package com.sahajsoft.bigo.queueintessential;

import com.sahajsoft.bigo.queueintessential.broker.BrokerClient;
import com.sahajsoft.bigo.queueintessential.broker.BrokerNIOClient;
import com.sahajsoft.bigo.queueintessential.config.ProducerProperties;
import com.sahajsoft.bigo.queueintessential.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProducerTest {

  private BrokerNIOClient brokerClient;
  private ProducerProperties producerProperties;

  @BeforeEach
  public void setUp() {
    brokerClient = mock(BrokerNIOClient.class);
    producerProperties = mock(ProducerProperties.class);
  }

  @Test
  public void shouldSendMessagesToTheBroker() throws IOException {
    File testFolderWithFiles = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("TestFolderWithFiles")).getFile());
    when(producerProperties.getFileFolderLocation()).thenReturn(
        testFolderWithFiles);
    doNothing().when(brokerClient).startConnection(null, 0);

    Producer producer = new Producer(brokerClient, producerProperties);
    int actualNumberOfMessagesSent = producer.sendMessages();

    assertEquals(testFolderWithFiles.listFiles().length, actualNumberOfMessagesSent);
    verify(producerProperties).getFileFolderLocation();
    verify(brokerClient).startConnection(null, 0);
    verify(brokerClient, times(2)).sendMessage(anyString());
  }

  @Test
  public void shouldContinueToSendMessagesOnFailureToSendOneMessageToTheBroker() throws IOException {
    File testFolderWithFiles = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("TestFolderWithFiles")).getFile());
    when(producerProperties.getFileFolderLocation()).thenReturn(
        testFolderWithFiles);
    doNothing().when(brokerClient).startConnection(null, 0);
    when(brokerClient.sendMessage(anyString())).thenThrow(new IOException("Forced exception for message 1")).thenReturn("success");

    Producer producer = new Producer(brokerClient, producerProperties);
    int actualNumberOfMessagesSent = producer.sendMessages();

    assertEquals(testFolderWithFiles.listFiles().length - 1, actualNumberOfMessagesSent);
    verify(producerProperties).getFileFolderLocation();
    verify(brokerClient).startConnection(null, 0);
    verify(brokerClient, times(2)).sendMessage(anyString());
  }

  @Test
  public void test() {
    String pathname = "/Users/amolg/Documents/Big O/sampleData3Sept2019";
    long start = System.currentTimeMillis();
    File file = new File(pathname);
    String[] list = file.list();
    for (String filename: list) {
      File file1 = new File(file.getAbsolutePath()+"/"+filename);
      file1.exists();
      //break;
    }
    System.out.println(System.currentTimeMillis() - start);
  }

  @Test
  public void test2() throws IOException {
    String pathname = "/Users/amolg/Documents/Big O/sampleData3Sept2019";
    long start = System.currentTimeMillis();
    Path dir = FileSystems.getDefault().getPath( pathname );
    DirectoryStream<Path> stream = Files.newDirectoryStream( dir );
    for (Path path : stream) {
      path.getFileName();
      File file = path.toFile();
      System.out.println(file.exists());
      break;
    }
    stream.close();
    System.out.println(System.currentTimeMillis() - start);
  }

}