package com.jlink.queue.consumer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface QueueMessageListener {
    String queueType() ;
    String topic() ; //JMQ、kafka配置
    int numThread() default 1;
    boolean enabled() default true;

    //kafka特殊属性配置
    String groupId() default "kafka";
    String duration() default "PT5S";
}
