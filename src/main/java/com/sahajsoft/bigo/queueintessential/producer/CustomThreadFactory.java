package com.sahajsoft.bigo.queueintessential.producer;

import java.util.concurrent.ThreadFactory;

public class CustomThreadFactory implements ThreadFactory {
    
    private String name;
    private int threadCount;

    public CustomThreadFactory(String name) {
      this.name = name;
      threadCount = 0;
    }

    public Thread newThread(Runnable r) {
      threadCount++;
      return new Thread(r, name + threadCount);
    }
  }