package com.sahajsoft.bigo.queueintessential.config;

import com.sahajsoft.bigo.queueintessential.producer.Broker;
import com.sahajsoft.bigo.queueintessential.producer.Consumer;
import com.sahajsoft.bigo.queueintessential.producer.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@RestController
@EnableAutoConfiguration
@ComponentScan("com.sahajsoft.bigo.queueintessential")
@Slf4j
public class ProducerApplication {

  private static ConfigurableApplicationContext applicationContext;

  @RequestMapping("/")
  String home() {
    return "Hello I am producer!";
  }

  @RequestMapping("/broker/messages")
  String broker() {
    return applicationContext.getBean(Broker.class).getNumberOfMessages().toString();
  }

  public static void main(String[] args) {
    applicationContext = SpringApplication.run(ProducerApplication.class, args);
//    new Thread(() -> {
//
//      Consumer consumer1 = applicationContext.getBean(Consumer.class);
//      System.out.println("consumer1 " + consumer1);
//      byte[] byteArr = new byte[]{(byte)192, (byte)168, (byte)1, (byte)39};
//      try {
//        InetSocketAddress localhost = new InetSocketAddress(InetAddress.getByAddress("Amols-MacBook-Pro-3.local", byteArr), 8888);
//        consumer1.openConnection(localhost);
//      } catch (UnknownHostException e) {
//        e.printStackTrace();
//      }
//      consumer1.start();
//
//    }).start();

        new Thread(() -> {

      Consumer consumer1 = applicationContext.getBean(Consumer.class);
      System.out.println("consumer1 " + consumer1);
      byte[] consumer1IP = new byte[]{(byte)172, (byte)31, (byte)70, (byte)127};
      try {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByAddress("ip-172-31-70-127.ec2.internal", consumer1IP), 8888);
        consumer1.openConnection(inetSocketAddress);
      } catch (UnknownHostException e) {
        log.error("error starting " + consumer1IP);
      }
      consumer1.start();

    }).start();

    new Thread(() -> {

      Consumer consumer2 = applicationContext.getBean(Consumer.class);
      System.out.println("consumer2 " + consumer2);
      byte[] consumer2IP = new byte[]{(byte)172, (byte)31, (byte)68, (byte)151};
      try {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByAddress(" ip-172-31-68-151.ec2.internal", consumer2IP), 8888);
        consumer2.openConnection(inetSocketAddress);
      } catch (UnknownHostException e) {
        log.error("error starting " + consumer2IP);
      }
      consumer2.start();

    }).start();

    try {
      applicationContext.getBean(Producer.class).sendFilesAsMessagesToBroker();
    } catch (Exception e) {
      log.error("Error occurred while starting the producer,", e);
    }

  }
}
