package com.jlink.flink.shim.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(JlinkClientShimProperties.class)
public class JlinkClientShimAutoConfigure {

}
