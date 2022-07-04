package com.jlink.security.jasypt.config;

import com.jlink.security.jasypt.SecurityEnvironmentPostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

@EnableConfigurationProperties({SecurityConfigProperties.class})
@Slf4j
public class SecurityAutoConfigure {
    @Bean
    @ConditionalOnProperty(prefix = "jlink.security.jasypt",name = "enabled",havingValue = "true")
    public static SecurityEnvironmentPostProcessor securityEnvironmentPostProcessor(ConfigurableEnvironment environment){
        log.info("init jasypt tools successfully ...");
        return new SecurityEnvironmentPostProcessor(environment);
    }
}


