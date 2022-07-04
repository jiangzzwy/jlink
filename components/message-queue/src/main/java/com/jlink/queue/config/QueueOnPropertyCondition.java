package com.jlink.queue.config;


import com.jlink.queue.entities.QueueType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;
import java.util.Map;

@Slf4j
public class QueueOnPropertyCondition implements Condition {
    private static final String QUEUE_TYPE="queueType";
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Map<String, Object> annotationAttributes = annotatedTypeMetadata.getAnnotationAttributes(QueueConditionalOnProperty.class.getName());
        QueueType queueType = (QueueType) annotationAttributes.get(QUEUE_TYPE);
        String keys = conditionContext.getEnvironment().getProperty((String) annotationAttributes.get("key"));
        return Arrays.asList(keys.split(",")).contains(queueType.name());
    }
}
