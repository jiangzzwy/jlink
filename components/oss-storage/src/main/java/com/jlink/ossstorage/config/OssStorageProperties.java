package com.jlink.ossstorage.config;

import com.amazonaws.Protocol;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jlink.oss-storage.minio",ignoreUnknownFields = true)
public class OssStorageProperties {
    private String accesskey;
    private String secretkey;
    private String region="cn-north-1";
    private String endpointUrl;
    private Protocol protocol=Protocol.HTTP;
    private String bucketName="jlink";
    private CheckSumAlgoType checkSumAlgoType=CheckSumAlgoType.CRC32;
}
