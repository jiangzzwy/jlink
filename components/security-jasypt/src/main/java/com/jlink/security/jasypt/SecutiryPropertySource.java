package com.jlink.security.jasypt;


public interface SecutiryPropertySource<T> {
    String getName();

    T getSource();

    boolean containsProperty(String name);

    Object getProperty(String name);
}
