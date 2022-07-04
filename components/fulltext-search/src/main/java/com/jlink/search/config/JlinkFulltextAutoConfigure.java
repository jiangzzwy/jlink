package com.jlink.search.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Collectors;

@EnableConfigurationProperties(JlinkFulltextProperties.class)
@Slf4j
public class JlinkFulltextAutoConfigure {
    @Bean
    @ConditionalOnProperty(prefix = "jlink.fulltext-search",name = "type",havingValue = "elasticsearch")
    public RestHighLevelClient restHighLevelClient(JlinkFulltextProperties elasticsearchProperties){

        final List<HttpHost> httpHosts = elasticsearchProperties.getUris().stream().map(address -> {
            final String[] split = address.split(":");
            return new HttpHost(split[0], Integer.parseInt(split[1]));
        }).collect(Collectors.toList());
        final RestClientBuilder restClientBuilder = RestClient.builder(httpHosts.toArray(new HttpHost[]{}));
        String username = elasticsearchProperties.getUsername();
        String password = elasticsearchProperties.getPassword();

        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
            restClientBuilder.setHttpClientConfigCallback((httpAsyncClientBuilder) -> {
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
                return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            });
        }
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(restClientBuilder);
        log.info("init elasticsearch client successfully ....");

        return restHighLevelClient;
    }
}
