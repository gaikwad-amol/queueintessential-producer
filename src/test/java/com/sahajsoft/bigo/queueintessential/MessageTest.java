package com.sahajsoft.bigo.queueintessential;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageTest {

  @Test
  public void shouldCreateMessageFromFile() {
    File file = new File(getClass().getClassLoader().getResource("1-9e635f22-1ad9-4cd9-adbc-9588699c251d-1566236758.json").getFile());
    Optional<Message> message = Message.createMessage(file);
    assertTrue(message.isPresent() && message.get().hasContent());
  }

  @Test
  public void shouldNotCreateMessageFromInvalidFile() {
    Optional<Message> message = Message.createMessage(new File(""));
    assertFalse(message.isPresent());
  }

}