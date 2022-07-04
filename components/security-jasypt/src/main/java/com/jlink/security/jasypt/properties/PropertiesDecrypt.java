package com.jlink.security.jasypt.properties;

import com.jlink.security.jasypt.config.SecurityConfigProperties;
import com.jlink.security.jasypt.decrypt.Decrypt;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertiesDecrypt {
    @SneakyThrows
    public static String decrypt(String name, String value, SecurityConfigProperties configProperties){
        String result=null;
        final boolean needEncryptedValue = isEncryptedValue(name, value, configProperties);
        if(needEncryptedValue){
            final Decrypt decrypt = (Decrypt) Class.forName(configProperties.getDecryptClassName()).newInstance();
             result = decrypt.decrypt(getInnerEncryptedValue(value, configProperties));
            log.info("解密：{} - {} 成功!",name,getInnerEncryptedValue(value,configProperties));
        }else{
           result= value;
        }
       return result;
    }

    public static boolean isEncryptedValue(final String name,final String value,SecurityConfigProperties configProperties) {
        if (value == null) {
            return false;
        }
        final String trimmedValue = value.trim();
        return (trimmedValue.startsWith(configProperties.getPrefix()) && trimmedValue.endsWith(configProperties.getSuffix()));
    }

    private static String getInnerEncryptedValue(final String value,SecurityConfigProperties configProperties) {
        return value.substring(
                configProperties.getPrefix().length(),
                (value.length() - configProperties.getSuffix().length()));
    }
}
