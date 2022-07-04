package com.jlink.queue.consumer;

public interface Consumer {
    public void consumer(String businessId,String msg, Ack ack);
}
