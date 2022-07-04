package com.jlink.security.jasypt.wrap;

import com.jlink.security.jasypt.config.SecurityConfigProperties;
import com.jlink.security.jasypt.properties.PropertiesDecrypt;
import org.springframework.core.env.SystemEnvironmentPropertySource;

import java.util.concurrent.ConcurrentHashMap;

public class SecuritySystemEnvironmentPropertySourceWrapper extends SystemEnvironmentPropertySource {
    private SystemEnvironmentPropertySource source;
    private ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<String, String>();
    private SecurityConfigProperties configProperties;

    public SecuritySystemEnvironmentPropertySourceWrapper(SystemEnvironmentPropertySource source, SecurityConfigProperties configProperties) {
        super(source.getName(), source.getSource());
        this.source = source;
        this.configProperties=configProperties;
    }

    public Object getProperty(String name) {
        if (cache.containsKey(name)) {
            return cache.get(name);
        }
        Object value = this.source.getProperty(name);
        if ((value instanceof String) && (value!=null)) {
            value=PropertiesDecrypt.decrypt(name,(String) value,configProperties);
        }
        return value;
    }
}
