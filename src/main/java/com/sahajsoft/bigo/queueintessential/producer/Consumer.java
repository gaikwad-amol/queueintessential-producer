package com.sahajsoft.bigo.queueintessential.producer;

import com.sahajsoft.bigo.queueintessential.config.ProducerProperties;
import com.sahajsoft.bigo.queueintessential.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;

@Component
@Scope("prototype")
@Slf4j
public class Consumer {

  private Broker broker;
  private ProducerProperties properties;
  private ConsumerNIOClient consumerNIOClient;

  @Autowired
  public Consumer(Broker broker, ProducerProperties properties, ConsumerNIOClient consumerNIOClient) {
    this.broker = broker;
    this.properties = properties;
    this.consumerNIOClient = consumerNIOClient;
  }

  public void start() {
    while (true) {
      if (consumerNIOClient.isConsumerAvailable()) {
        Optional<Message> message = broker.fetch();
        if (message.isPresent()) {
          consumerNIOClient.sendMessage(message.get().getContent());
        }
      }
      else {
        //TODO introduce locking
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void openConnection(InetSocketAddress inetSocketAddress) {
    try {
      System.out.println("connecting to client + " + inetSocketAddress.getHostName() + " " + inetSocketAddress.getPort() + " " + consumerNIOClient);
      consumerNIOClient.startConnection(inetSocketAddress);
    } catch (IOException e) {
      log.error("error connecting to client + " + inetSocketAddress.getHostName() + " " + inetSocketAddress.getPort() + " " + consumerNIOClient, e);
    }
  }


}
