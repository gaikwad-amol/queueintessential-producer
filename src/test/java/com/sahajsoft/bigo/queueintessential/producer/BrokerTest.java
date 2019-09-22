package com.sahajsoft.bigo.queueintessential.producer;

import com.sahajsoft.bigo.queueintessential.config.ProducerProperties;
import com.sahajsoft.bigo.queueintessential.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BrokerTest {

  private static final String TEST_FILE = "TestFolderWithFiles/1-9e635f22-1ad9-4cd9-adbc-9588699c251d-1566236757.json";
  private Broker broker;
  private ProducerProperties producerProperties;
  private Optional<Message> actualMessage;

  @BeforeEach
  public void setUp() {
    actualMessage = Optional.empty();
    producerProperties = mock(ProducerProperties.class);
    when(producerProperties.getQueueCapacity()).thenReturn(10);
    broker = new Broker(producerProperties);
  }

  @Test
  public void shouldAddMessagesToBroker() {
    givenMessage();
    verifyNumberOfMessageBrokerHas(0);

    whenAddMessageToBroker();

    verifyNumberOfMessageBrokerHas(1);
  }

  @Test
  public void shouldPollMessagesFromBroker() {
    shouldAddMessagesToBroker();

    whenPollMessageFromBroker();

    Assertions.assertEquals(expectedMessage().getContent(), actualMessage.get().getContent());
    verifyNumberOfMessageBrokerHas(0);
  }

  private void whenPollMessageFromBroker() {
    actualMessage = broker.fetch();
  }

  private void verifyNumberOfMessageBrokerHas(int i) {
    assertEquals(i, broker.getNumberOfMessages());
  }

  private void whenAddMessageToBroker() {
    actualMessage.ifPresent(message -> broker.accept(message));
  }

  private void givenMessage() {
    File file = createPathForATestFile();
    actualMessage = Message.createMessage(file.toPath());
  }

  private File createPathForATestFile() {
    String testFile = Objects.requireNonNull(getClass().getClassLoader().getResource(TEST_FILE)).getPath();
    return new File(testFile);
  }

  private Message expectedMessage() {
    String testFile = Objects.requireNonNull(getClass().getClassLoader().getResource(TEST_FILE)).getPath();
    return Message.createMessage(new File(testFile).toPath()).get();
  }
}