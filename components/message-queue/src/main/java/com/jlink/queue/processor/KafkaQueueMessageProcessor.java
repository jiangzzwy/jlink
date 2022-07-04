package com.jlink.queue.processor;

import com.jlink.queue.config.MessageQueueProperties;
import com.jlink.queue.config.QueueConditionalOnProperty;
import com.jlink.queue.consumer.Consumer;
import com.jlink.queue.consumer.QueueMessageListener;
import com.jlink.queue.entities.QueueType;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@ConditionalOnClass(value = {KafkaConsumer.class})
@Slf4j
public class KafkaQueueMessageProcessor implements ApplicationContextAware, SmartInitializingSingleton, DisposableBean {

    private ApplicationContext applicationContext;

    final MessageQueueProperties messageQueueProperties;
    final  Environment environment;

    public KafkaQueueMessageProcessor(MessageQueueProperties messageQueueProperties, Environment environment) {
        this.messageQueueProperties = messageQueueProperties;
        this.environment = environment;
    }

    private AtomicBoolean stop=new AtomicBoolean(false);

    @Override
    public void destroy() throws Exception {
        stop.set(true);
    }

    @Override
    public void afterSingletonsInstantiated() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(QueueMessageListener.class);

        for (int i = 0; i < beanNames.length; i++) {
            String beanName=beanNames[i];
            Object consumerBean = applicationContext.getBean(beanName);
            Class<?> consumerClass = consumerBean.getClass();
            QueueMessageListener message = consumerClass.getAnnotation(QueueMessageListener.class);
            if(!(consumerBean instanceof Consumer)){
                throw new IllegalArgumentException("必须是Consumer接口的子类");
            }else {
                QueueType queueType = QueueType.valueOf(getValueFromEnv(message.queueType()));
                if(queueType.equals(QueueType.kafka)){
                    String topicName = getValueFromEnv(message.topic());
                    if(message.enabled()) {
                        for (int j = 0; j < message.numThread(); j++) {
                            Thread thread = new Thread(() -> {
                                Properties properties = messageQueueProperties.getConsumer().get(queueType);
                                properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, message.groupId());
                                properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
                                properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
                                properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
                                KafkaConsumer kafkaConsumer = new KafkaConsumer(properties);
                                kafkaConsumer.subscribe(Arrays.asList(topicName));
                                while (!stop.get()) {
                                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.parse(message.duration()));
                                    for (ConsumerRecord record : records) {
                                        try {
                                           ((Consumer) consumerBean).consumer((String) record.key(), (String) record.value(), () -> kafkaConsumer.commitSync());
                                        }catch (Exception e){
                                            log.error("消费{}失败 {} - {}",topicName,e.getMessage(), record.value());
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                            thread.setDaemon(true);
                            thread.setName(queueType.name() + "_THREAD_" + beanNames[i] + "_" + j);
                            thread.start();
                            log.info("启动 {} 消费 {} topic成功！BeanName {}", queueType, topicName, beanName);
                        }
                    }else {
                        log.warn("{}消息，topic {} ,暂时没有开启消费！",queueType.name(),topicName);
                    }
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
       this.applicationContext=applicationContext;
    }

    private String getValueFromEnv(String value){
        return environment.resolvePlaceholders(value);
    }
}
