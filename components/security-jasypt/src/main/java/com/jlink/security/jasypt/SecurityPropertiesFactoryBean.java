package com.jlink.security.jasypt;

import org.springframework.beans.factory.config.PropertiesFactoryBean;

import java.io.IOException;
import java.util.Properties;

public class SecurityPropertiesFactoryBean extends PropertiesFactoryBean {

    @Override
    public void loadProperties(Properties props) throws IOException {
        super.loadProperties(props);
       //PropertiesLoader.decrypt(props);
    }
}
