package com.sahajsoft.bigo.queueintessential.message;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
public class Message {
  private static final String LAST_MESSAGE = "LAST";
  private String content;

  private Message(String content) {
    this.content = content;
  }

  public Message() {
    content = LAST_MESSAGE;
  }

  public static Optional<Message> createMessage(Path path) {
    if (/*path == null || !path.toFile().exists() || path.toFile().isHidden() || */path.toFile().isDirectory()) {
      log.error("file not found " + path.toFile().getAbsolutePath());
      return Optional.empty();
    }
    try {
      return Optional.of(new Message(new String(Files.readAllBytes(path))));

    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return Optional.empty();
  }

  public boolean hasContent() {
    return content != null && !content.isEmpty();
  }

  public String getContent() {
    return content;
  }

}
