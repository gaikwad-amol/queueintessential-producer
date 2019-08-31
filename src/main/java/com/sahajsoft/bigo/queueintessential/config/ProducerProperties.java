package com.sahajsoft.bigo.queueintessential.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.Objects;

@Configuration
@PropertySource("classpath:application.properties")
public class ProducerProperties {

  private Environment environment;

  @Autowired
  public ProducerProperties(Environment environment) {
    this.environment = environment;
  }

  public File getFileFolderLocation() {
    return new File(Objects.requireNonNull(environment.getProperty("producer.folder")));
  }

  public String getBrokerIPAddress() {
    return environment.getProperty("broker.ipaddress");
  }

  public Integer getBrokerSocketPort() {
    return Integer.valueOf(Objects.requireNonNull(environment.getProperty("broker.socket.port")));
  }
}
