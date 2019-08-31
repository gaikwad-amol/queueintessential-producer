package com.sahajsoft.bigo.queueintessential;

import com.sahajsoft.bigo.queueintessential.config.ProducerProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProducerTest {

  private BrokerClient brokerClient;
  private ProducerProperties producerProperties;

  @BeforeEach
  public void setUp() {
    brokerClient = mock(BrokerClient.class);
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

}