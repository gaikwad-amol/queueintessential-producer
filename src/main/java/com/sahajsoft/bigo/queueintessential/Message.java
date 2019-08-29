package com.sahajsoft.bigo.queueintessential;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class Message {
  private String content;

  private Message(String content) {
    this.content = content;
  }

  public static Optional<Message> createMessage(File file) {
    if (file == null || !file.exists() || file.isHidden()) {
      return Optional.empty();
    }
    StringBuilder contentBuilder = new StringBuilder();
    try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8)) {
      stream.forEach(contentBuilder::append);
      return Optional.of(new Message(contentBuilder.toString()));
    } catch (IOException e) {
      log.error("Error occurred while creating message for file - " + file.getAbsolutePath(), e);
    }
    return Optional.empty();
  }

  public boolean hasContent() {
    return content != null && !content.isEmpty();
  }

  String getContent() {
    return content;
  }

  public void send(Client client) throws IOException {
    log.info("sending message - " + content);
    String response = client.sendMessage(content);
    log.info("response - " + response);

  }
}
