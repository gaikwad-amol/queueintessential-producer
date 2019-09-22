package com.sahajsoft.bigo.queueintessential.producer;

import com.sahajsoft.bigo.queueintessential.config.ProducerProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;

import static org.mockito.Mockito.*;

public class ProducerTest {

  private Producer producer;
  private ProducerProperties producerProperties;
  private Broker broker;
  private int actualNumberOfFilesSent;
  private int expectedNumberOfFilesSent;
  private String testFolderWithFiles;

  @BeforeEach
  public void setUp() {
    producerProperties = mock(ProducerProperties.class);
    when(producerProperties.producerThreads()).thenReturn(2);
    when(producerProperties.getQueueCapacity()).thenReturn(10000000);

    broker = new Broker(producerProperties);
    producer = new Producer(producerProperties, broker);
    actualNumberOfFilesSent = 0;
    testFolderWithFiles = Objects.requireNonNull(getClass().getClassLoader().getResource("TestFolderWithFiles")).getPath();
    //testFolderWithFiles = "/Users/amolg/Documents/Big O/finalDataSet4Sept2019";
    expectedNumberOfFilesSent = new File(testFolderWithFiles).listFiles().length;
  }

  @Test
  public void shouldSendMessagesToBrokerSuccessfully() throws InterruptedException {
    givenFolderWithFilesToBeSent();
    whenSendMessages();

    waitForThreadsToComplete();
    verifyMessagesAreReceivedByBroker();
  }

  @Test
  public void shouldNotSendMessagesToBrokerOnErrorToReadFiles() throws InterruptedException {
    givenFolderWithIncorrectPath();
    whenSendMessages();
    Assertions.assertEquals(0, actualNumberOfFilesSent);
  }

  private void givenFolderWithIncorrectPath() {
    testFolderWithFiles = testFolderWithFiles + "invalid_path";
    when(producerProperties.getFileFolderLocationString()).thenReturn(testFolderWithFiles);
  }

  private void verifyMessagesAreReceivedByBroker() {
    Assertions.assertEquals(expectedNumberOfFilesSent, actualNumberOfFilesSent);
    Assertions.assertEquals(expectedNumberOfMessagesIncludingLastMessage(), broker.getNumberOfMessages());
  }

  private int expectedNumberOfMessagesIncludingLastMessage() {
    return expectedNumberOfFilesSent + 1;
  }

  private void waitForThreadsToComplete() throws InterruptedException {
    Thread.sleep(2000);
  }

  private void whenSendMessages() {
    actualNumberOfFilesSent = producer.sendFilesAsMessagesToBroker();
  }

  private void givenFolderWithFilesToBeSent() {
    when(producerProperties.getFileFolderLocationString()).thenReturn(testFolderWithFiles);
  }
}