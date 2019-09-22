package com.sahajsoft.bigo.queueintessential.producer;

import com.sahajsoft.bigo.queueintessential.config.ProducerProperties;
import com.sahajsoft.bigo.queueintessential.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
@Component
public class Broker {

  private BlockingQueue<Message> queue;
  private ProducerProperties brokerProperties;

  @Autowired
  public Broker(ProducerProperties producerProperties) {
    this.brokerProperties = producerProperties;
    queue = new LinkedBlockingDeque<>(producerProperties.getQueueCapacity());
  }

  public void accept(Message message) {
    try {
      queue.put(message);
    } catch (InterruptedException e) {
      log.error("Error occurred while broker accepting message - " + message);
    }
  }

  public Optional<Message> fetch() {
    Optional<Message> message = Optional.empty();
    try {
      return Optional.of(queue.take());
    } catch (InterruptedException e) {
      log.error("Error occurred while fetching message from broker - ");
    }
    return message;
  }

  public Integer getNumberOfMessages() {
    return queue.size();
  }

}
