package com.sahajsoft.bigo.queueintessential.config;

import java.util.concurrent.atomic.AtomicInteger;

public class Stats {

  public static AtomicInteger numberOfFilesToBeSend = new AtomicInteger(0);
  public static AtomicInteger numberOfFilesSent = new AtomicInteger(0);
  public static AtomicInteger numberOfFilesFailed = new AtomicInteger(0);

}
