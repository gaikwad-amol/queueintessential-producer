package com.sahajsoft.bigo.queueintessential.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.Objects;

@Configuration
@PropertySource("classpath:application.properties")
public class ProducerProperties {

  private Environment environment;

  @Autowired
  public ProducerProperties(Environment environment) {
    this.environment = environment;
  }

  public String getFileFolderLocationString() {
    return Objects.requireNonNull(environment.getProperty("producer.folder"));
  }

  public Integer writerThreads() {
    return Integer.valueOf(Objects.requireNonNull(environment.getProperty("writer.threads")));
  }

  public Integer producerThreads() {
    return Integer.valueOf(Objects.requireNonNull(environment.getProperty("producer.threads")));
  }

  public Integer getQueueCapacity() {
    return Integer.valueOf(Objects.requireNonNull(environment.getProperty("queueCapacity")));
  }
}
