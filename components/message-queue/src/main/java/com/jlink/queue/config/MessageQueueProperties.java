package com.jlink.queue.config;

import com.jlink.queue.entities.QueueType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@ConfigurationProperties(prefix = "jlink.queue")
@Data
public class MessageQueueProperties {
    private List<QueueType> queueTypes;
    private HashMap<QueueType, Properties> consumer;
    private HashMap<QueueType, Properties> producer;
}
