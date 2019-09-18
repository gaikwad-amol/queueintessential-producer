package com.sahajsoft.bigo.queueintessential.message;

import com.sahajsoft.bigo.queueintessential.broker.BrokerClient;
import com.sahajsoft.bigo.queueintessential.broker.BrokerNIOClient;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class Message {
  private String content;

  private Message(String content) {
    this.content = content;
  }

  public Message() {
    content = "LAST";
  }

  public static Optional<Message> createMessage(File file) {
    if (file == null || !file.exists() || file.isHidden() || file.isDirectory()) {
      log.error("file not found " + file.getAbsolutePath());
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

  public static Optional<Message> createMessageNew(Path path) {
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

  String getContent() {
    return content;
  }

  public void send(BrokerNIOClient brokerClient) throws IOException {
    String response = brokerClient.sendMessage(content);
  }
}
