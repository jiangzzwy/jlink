package com.jlink.queue.producer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
public class ProducerProxyRegister  implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private ResourceLoader resourceLoader;
    private final String QUEUE_TYPE="queueType";
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader=resourceLoader;
    }
    private Environment environment;

    public ProducerProxyRegister(Environment environment) {
        this.environment = environment;
    }

    @SneakyThrows
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        log.info("开始加载Producer...");
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(QueueProducerScan.class.getName()));
        String[] packages = attributes.getStringArray("basePackages");
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        for (String pkg: packages) {
            PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();

            Resource[] resources = patternResolver.getResources("classpath*:/" + pkg.replace(".", File.separator) + File.separator+"*");
            MetadataReaderFactory metadata = new SimpleMetadataReaderFactory();
            log.info("资源：{}",resources.length);
            for(Resource resource : resources) {
                MetadataReader metadataReader = metadata.getMetadataReader(resource);
                ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                sbd.setResource(resource);
                sbd.setSource(resource);
                sbd.setScope("singleton");
                candidates.add(sbd);
            }
            for(BeanDefinition beanDefinition : candidates) {
                String className = beanDefinition.getBeanClassName();
                Class<?> beanClass = Class.forName(className);
                if (beanClass.isAnnotationPresent(QueueProducer.class) && Producer.class.isAssignableFrom(beanClass)) {
                    QueueProducer queueProducer = beanClass.getAnnotation(QueueProducer.class);
                    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ProducerProxy.class);
                    GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
                    definition.getPropertyValues().add(QUEUE_TYPE,environment.resolvePlaceholders(queueProducer.queueType()));
                    definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
                    registry.registerBeanDefinition(queueProducer.beanName(), definition);
                    log.info("MessageQueue生产代理Bean {} 类型 {}",queueProducer.beanName(),environment.resolvePlaceholders(queueProducer.queueType()));
                }
            }
        }

    }
}
