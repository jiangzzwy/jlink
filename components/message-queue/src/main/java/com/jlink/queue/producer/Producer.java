package com.jlink.queue.producer;

public interface Producer  {
     void send(String topic,String key,String msg);
}