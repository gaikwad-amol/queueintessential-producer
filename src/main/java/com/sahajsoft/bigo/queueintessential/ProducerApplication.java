package com.sahajsoft.bigo.queueintessential;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@EnableAutoConfiguration
@ComponentScan("com.sahajsoft")
public class ProducerApplication {

  @Autowired
  private Producer producer;

  @RequestMapping("/")
  String home() throws IOException, InterruptedException {
    producer.start();
    return "Started file transfer!";
  }

  public static void main(String[] args) {
    SpringApplication.run(ProducerApplication.class, args);
  }
}
