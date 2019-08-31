package com.sahajsoft.bigo.queueintessential.config;

import com.sahajsoft.bigo.queueintessential.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@ComponentScan("com.sahajsoft.bigo.queueintessential")
@Slf4j
public class ProducerApplication {

  @RequestMapping("/")
  String home() {
    return "Hello I am producer!";
  }

  public static void main(String[] args) {
    ConfigurableApplicationContext applicationContext = SpringApplication.run(ProducerApplication.class, args);
    try {
      applicationContext.getBean(Producer.class).sendMessages();
    } catch (Exception e) {
      log.error("Error occurred while starting the producer,", e);
    }
  }
}
