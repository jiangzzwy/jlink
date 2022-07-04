package com.jlink.flink.utils;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class FlinkStatusUtils {

    public static void callBack(String url,DeployResult deployResult){
        try {
           final HttpClient httpClient = new DefaultHttpClient();
           final HttpPost method = new HttpPost(url);
           method.addHeader("Content-type","application/json;charset=utf-8");
           method.setHeader("Accept", "application/json");
           method.setEntity(new StringEntity(genDeployJSON(deployResult), "UTF-8"));
           httpClient.execute(method);
       }catch (Exception e){
           e.printStackTrace();
       }
    }
    public static String genDeployJSON(Object callBack) throws IOException, IllegalAccessException {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper.writeValueAsString(callBack) ;
    }

}
