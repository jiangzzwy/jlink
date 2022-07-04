package com.jlink.security.jasypt;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.IOException;
import java.util.Properties;

public class SecurityPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    public SecurityPropertyPlaceholderConfigurer() {
        this.ignoreUnresolvablePlaceholders = true;
    }

    @Override
    public void loadProperties(Properties props) throws IOException {
        super.loadProperties(props);
        //PropertiesLoader.decrypt(props);
    }
}
