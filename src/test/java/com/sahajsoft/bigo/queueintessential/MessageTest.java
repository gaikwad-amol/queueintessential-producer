package com.sahajsoft.bigo.queueintessential;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

  @Test
  public void shouldCreateMessageFromFile() {
    File file = new File(getClass().getClassLoader().getResource("1-9e635f22-1ad9-4cd9-adbc-9588699c251d-1566236758.json").getFile());
    Message message = Message.createMessage(file);
    assertNotNull(message.getContent());
  }

}