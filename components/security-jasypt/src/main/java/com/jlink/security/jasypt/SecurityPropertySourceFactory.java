package com.jlink.security.jasypt;


import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Properties;


public class SecurityPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        if (name == null) {
            name = getNameForResource(resource.getResource());
        }
        String filename = resource.getResource().getFilename();
        Properties props;
        if (filename != null && (filename.endsWith(".yml") || filename.endsWith(".yaml"))) {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();
            props = factory.getObject();
        } else {
            props = new Properties();
            PropertiesLoaderUtils.fillProperties(props, resource);
        }

        //PropertiesLoader.decrypt(props);
        return new PropertiesPropertySource(name, props);
    }

    private String getNameForResource(Resource resource) {
        String name = resource.getDescription();
        if (!StringUtils.hasText(name)) {
            name = resource.getClass().getSimpleName() + "@" + System.identityHashCode(resource);
        }
        return name;
    }
}
