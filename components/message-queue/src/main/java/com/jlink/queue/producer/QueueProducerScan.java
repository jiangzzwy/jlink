package com.jlink.queue.producer;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(ProducerProxyRegister.class)
public @interface QueueProducerScan {
    String[] basePackages() default {};
}
