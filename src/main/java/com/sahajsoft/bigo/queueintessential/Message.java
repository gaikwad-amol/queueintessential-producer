package com.sahajsoft.bigo.queueintessential;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Message {
  private String content;

  private Message(String content) {
    this.content = content;
  }

  public static Message createMessage(File file) {
    StringBuilder contentBuilder = new StringBuilder();
    try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8)) {
      stream.forEach(s -> contentBuilder.append(s));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new Message(contentBuilder.toString());
  }

  String getContent() {
    return content;
  }

  public void send() {
    System.out.println("message - " + content);
  }
}
