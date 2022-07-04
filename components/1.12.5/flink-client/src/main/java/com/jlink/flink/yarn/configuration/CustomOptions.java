package com.jlink.flink.yarn.configuration;

import org.apache.flink.configuration.ConfigOption;

import java.util.Arrays;
import java.util.List;

import static org.apache.flink.configuration.ConfigOptions.key;

/** 用户自定义参数 */
public class CustomOptions {
    public static final ConfigOption<String> CUSTOM_KERBEROS_KDC =
            key("custom.kerberos.kdc")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("kerberos远程访问kdc服务器");

    public static final ConfigOption<String> CUSTOM_KERBEROS_REALM =
            key("custom.kerberos.realm")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("kerberos远程访问kdc服务器");

    public static final ConfigOption<List<String>> CUSTOM_HADOOP_FILES =
            key("custom.hadoop-files")
                    .stringType()
                    .asList()
                    .noDefaultValue()
                    .withDescription("远程hadoop配置文件地址,多个地址之间通过','分割");

    public static final ConfigOption<String> CUSTOM_BUSINESS_ID =
            key("custom.business.id")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("业务ID信息，必须保证唯一性");

    public static final ConfigOption<String> CUSTOM_CALLBACK_URL =
            key("custom.callback.url")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("回调URL信息");

    public static List<ConfigOption> getCustomOptions(){
        return Arrays.asList(
                CUSTOM_HADOOP_FILES,
                CUSTOM_KERBEROS_KDC,
                CUSTOM_KERBEROS_REALM);


    }
}
