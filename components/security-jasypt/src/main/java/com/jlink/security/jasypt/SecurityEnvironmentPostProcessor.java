package com.jlink.security.jasypt;

import com.jlink.security.jasypt.config.SecurityConfigProperties;
import com.jlink.security.jasypt.proxy.SecurityPropertySourceMethodInterceptor;
import com.jlink.security.jasypt.wrap.SecurityEnumerablePropertySourceWrapper;
import com.jlink.security.jasypt.wrap.SecurityMapPropertySourceWrapper;
import com.jlink.security.jasypt.wrap.SecurityPropertySourceWrapper;
import com.jlink.security.jasypt.wrap.SecuritySystemEnvironmentPropertySourceWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.*;

import java.util.Arrays;
import java.util.HashSet;


@Slf4j
public class SecurityEnvironmentPostProcessor implements BeanFactoryPostProcessor, Ordered {

    //1.必须走代理的实现
    private HashSet<String> MUST_PROXY = new HashSet<String>(Arrays.asList(
            "org.springframework.boot.context.config.ConfigFileApplicationListener$ConfigurationPropertySources",
            "org.springframework.boot.context.properties.source.ConfigurationPropertySourcesPropertySource"
    ));
    private  ConfigurableEnvironment environment;
    public SecurityEnvironmentPostProcessor(ConfigurableEnvironment environment) {
        this.environment = environment;
    }


    private <S> PropertySource<?> wrap(PropertySource<S> source, SecurityConfigProperties configProperties) {
        if (source instanceof SystemEnvironmentPropertySource) {
            return new SecuritySystemEnvironmentPropertySourceWrapper((SystemEnvironmentPropertySource) source,configProperties);
        } else if (source instanceof MapPropertySource) {
            return new SecurityMapPropertySourceWrapper((MapPropertySource) source,configProperties);
        } else if (source instanceof EnumerablePropertySource) {
            return new SecurityEnumerablePropertySourceWrapper<S>((EnumerablePropertySource<S>) source,configProperties);
        }
        return new SecurityPropertySourceWrapper<S>(source,configProperties);
    }

    private <S> PropertySource<S> proxy(PropertySource<S> source,SecurityConfigProperties configProperties){
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetClass(source.getClass());
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addInterface(SecutiryPropertySource.class);
        proxyFactory.setTarget(source);
        proxyFactory.addAdvice(new SecurityPropertySourceMethodInterceptor<S>(source,configProperties));
        return (PropertySource<S>) proxyFactory.getProxy();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        final Boolean enabled = environment.getProperty("jlink.security.jasypt.enabled", Boolean.class);
        if(enabled){
            final SecurityConfigProperties configProperties = new SecurityConfigProperties();
            configProperties.setEnabled(true);
            final String prefix = environment.getProperty("jlink.security.jasypt.prefix", String.class);
            if(!StringUtils.isBlank(prefix)){
                configProperties.setPrefix(prefix);
            }
            final String suffix = environment.getProperty("jlink.security.jasypt.suffix", String.class);
            if(!StringUtils.isBlank(suffix)){
                configProperties.setSuffix(suffix);
            }
            final String className = environment.getProperty("jlink.security.jasypt.decrypt-class-name", String.class);
            if(!StringUtils.isBlank(className)){
                configProperties.setDecryptClassName(className);
            }
            MutablePropertySources propSources = environment.getPropertySources();
            for (PropertySource<?> source : propSources) {
                if (MUST_PROXY.contains(source.getClass().getName())) {
                    propSources.replace(source.getName(), proxy(source,configProperties));
                } else {
                    propSources.replace(source.getName(), wrap(source,configProperties));
                }
            }
        }

    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
