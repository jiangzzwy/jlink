package com.jlink.queue.producer;


import com.jlink.queue.config.MessageQueueProperties;
import com.jlink.queue.entities.QueueType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

@Slf4j
public class ProducerProxy implements FactoryBean<Producer>, InvocationHandler {
    @Autowired
    private MessageQueueProperties messageQueueProperties;
    private String queueType;
    @Autowired
    Environment environment;

    private static final String URL="url";
    private static final String TOKEN="token";
    private static final String APP="app";

    KafkaProducer<String, String> producer;

    @Override
    public Producer getObject() throws Exception {
        return (Producer) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Producer.class}, this);
    }

    @Override
    public Class<?> getObjectType() {
        return Producer.class;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object[] pargs = (Object[]) args;
        String topic= ((String) pargs[0]);
        String key= ((String) pargs[1]);
        String msg= ((String) pargs[2]);

        if(log.isDebugEnabled()){
            log.debug(" 类型: {} ,Topic {}, businessId {} ,value {} ",topic,queueType,key,msg);
        }

        Properties properties = messageQueueProperties.getProducer().get(queueType);
        switch (QueueType.valueOf(queueType)){
            case kafka:
                if(producer==null){
                    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                    producer = new KafkaProducer<>(properties);
                }
                producer.send(new ProducerRecord<String,String>(topic,key,msg));
                break;
            case log:
                log.info("{} {}\t{}",topic,key,msg);
        }
        log.info("初始化{}生产者成功！",queueType);
        return null;
    }

    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }
}
