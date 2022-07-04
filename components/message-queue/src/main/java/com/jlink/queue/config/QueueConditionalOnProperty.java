package com.jlink.queue.config;

import com.jlink.queue.entities.QueueType;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(QueueOnPropertyCondition.class)
public @interface QueueConditionalOnProperty {
    QueueType queueType();
    String key();
}
