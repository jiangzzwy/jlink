package com.jlink.security.jasypt.proxy;


import com.jlink.security.jasypt.config.SecurityConfigProperties;
import com.jlink.security.jasypt.wrap.SecurityPropertySourceWrapper;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.env.PropertySource;

@Slf4j
public class SecurityPropertySourceMethodInterceptor<S> extends SecurityPropertySourceWrapper<S> implements MethodInterceptor {
    public SecurityPropertySourceMethodInterceptor(PropertySource<S> source, SecurityConfigProperties configProperties) {
        super(source,configProperties);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if (isGetPropertyCall(methodInvocation)) {
            return this.getProperty((String)methodInvocation.getArguments()[0]);
        }
        return methodInvocation.proceed();
    }

    private boolean isGetPropertyCall(MethodInvocation methodInvocation) {
        return "getProperty".equals(methodInvocation.getMethod().getName())
                && methodInvocation.getArguments().length == 1
                && methodInvocation.getArguments()[0].getClass() == String.class;
    }
}
