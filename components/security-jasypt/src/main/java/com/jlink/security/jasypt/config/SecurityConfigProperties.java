package com.jlink.security.jasypt.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "jlink.security.jasypt")
public class SecurityConfigProperties {
    private Boolean enabled;
    private String prefix="ENC(";
    private String suffix=")";
    private String decryptClassName="com.jlink.security.jasypt.decrypt.impl.AESDecrypt";

}
