package com.jlink.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


@ConfigurationProperties(prefix = "jlink.fulltext-search")
@Data
public class JlinkFulltextProperties {
    private  ElasticsearchType type;
    private List<String> uris;
    private String username;
    private String password;


    public enum ElasticsearchType{
        elasticsearch()
    }
}
