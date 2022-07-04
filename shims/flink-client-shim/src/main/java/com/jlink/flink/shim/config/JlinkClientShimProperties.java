package com.jlink.flink.shim.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jlink.flink.client")
@Data
public class JlinkClientShimProperties {
    private String flinkLocation;
    private String metastoreUri;
}
