package com.jlink.queue.config;


import com.jlink.queue.entities.QueueType;
import com.jlink.queue.processor.KafkaQueueMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;
import java.util.Map;

@EnableConfigurationProperties(MessageQueueProperties.class)
public class MessageQueueAutoConfigure {
    @Bean
    public KafkaQueueMessageProcessor queueMessageProcessor(MessageQueueProperties messageQueueProperties, Environment environment){
        return new KafkaQueueMessageProcessor(messageQueueProperties,environment);
    }
}
