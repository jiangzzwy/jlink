package com.jlink.flink.utils;

import org.apache.flink.configuration.Configuration;
import org.apache.hadoop.io.IOUtils;
import org.jets3t.service.io.TempFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class FlinkConfigUtils {
    private static final Logger LOG = LoggerFactory.getLogger(FlinkConfigUtils.class);

    public static String hadoopKeyTabPath(URL url){
        if(url.getProtocol().equals("http")){
            File configFile =null;
            try {
                final URLConnection urlConnection = url.openConnection();
                final InputStream inputStream = urlConnection
                        .getInputStream();
                configFile = TempFile.createTempFile("flink", ".keytab");
                configFile.deleteOnExit();
                final FileOutputStream fileOutputStream = new FileOutputStream(configFile);
                IOUtils.copyBytes(inputStream,fileOutputStream,1024,true);
            }catch (Exception e){
                e.printStackTrace();
            }
            return "file://"+configFile.getAbsolutePath();
        }else if(url.getProtocol().equals("file")){
            return url.getPath();
        }else {
            throw new RuntimeException("目前仅仅支持http://和file://文件");
        }
    }
    public static String log4jProperties(URL url){

        if(url.getProtocol().equals("http") || url.getProtocol().equals("file")) {
            try {
                final URLConnection urlConnection = url.openConnection();
                final InputStream inputStream = urlConnection
                        .getInputStream();

                final String dir = new File(FlinkConfigUtils.class.getProtectionDomain().getCodeSource().
                        getLocation().toURI().getPath()).getParent()+"/../../tmp/log4j/" + System.currentTimeMillis();
                System.out.println(dir);
                final File logDir = new File(dir);
                if (!logDir.exists()) {
                    logDir.mkdirs();
                }
                final File log4jFile = new File(logDir + "/log4j.properties");
                Runtime.getRuntime().addShutdownHook(new Thread(()->{
                    final File dirFile = new File(log4jFile.getParent());
                    for (File file : dirFile.listFiles()) {
                        file.delete();
                    }
                    dirFile.delete();
                }));
                final FileOutputStream fileOutputStream = new FileOutputStream(log4jFile);
                IOUtils.copyBytes(inputStream, fileOutputStream, 1024, true);
                return log4jFile.getAbsolutePath();
            }catch(Exception e){
                throw new RuntimeException("加载文件异常");
            }
        }else {
            throw new RuntimeException("目前仅仅支持http://和file://文件");
        }
    }
    public static Configuration loadFlinkConfiguration(URL url,Configuration dynamicProperties) {
        File configFile =null;
        if(url.getProtocol().equals("http")) {
            try {
                final URLConnection urlConnection = url.openConnection();
                final InputStream inputStream = urlConnection
                        .getInputStream();
                configFile = TempFile.createTempFile("flink", "yaml");
                configFile.deleteOnExit();

                final FileOutputStream fileOutputStream = new FileOutputStream(configFile);
                IOUtils.copyBytes(inputStream,fileOutputStream,1024,true);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(url.getProtocol().equals("file")){
            configFile=new File(url.getFile());
        }else {
            throw new RuntimeException("目前仅仅支持http://和file://文件");
        }
        Configuration configuration = loadYAMLResource(configFile);
        if (dynamicProperties != null) {
            configuration.addAll(dynamicProperties);
        }
        return configuration;
    }
    private static Configuration loadYAMLResource(File file) {
        final Configuration config = new Configuration();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                // 1. check for comments
                String[] comments = line.split("#", 2);
                String conf = comments[0].trim();
                // 2. get key and value
                if (conf.length() > 0) {
                    String[] kv = conf.split(": ", 2);
                    // skip line with no valid key-value pair
                    if (kv.length == 1) {
                        LOG.warn(
                                "Error while trying to split key and value in configuration file "
                                        + file
                                        + ":"
                                        + lineNo
                                        + ": \""
                                        + line
                                        + "\"");
                        continue;
                    }

                    String key = kv[0].trim();
                    String value = kv[1].trim();

                    // sanity check
                    if (key.length() == 0 || value.length() == 0) {
                        LOG.warn(
                                "Error after splitting key and value in configuration file "
                                        + file
                                        + ":"
                                        + lineNo
                                        + ": \""
                                        + line
                                        + "\"");
                        continue;
                    }
                    config.setString(key, value);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error parsing YAML configuration.", e);
        }
        return config;
    }
}
