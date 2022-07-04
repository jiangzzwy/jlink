package com.jlink.ossstorage.client.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.jlink.ossstorage.client.OssClient;
import com.jlink.ossstorage.config.OssStorageProperties;
import com.jlink.ossstorage.utils.DigestUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MinIOClient implements OssClient {
    @Autowired
    private OssStorageProperties ossStorageProperties;
    public static Integer DAY_SECOND =  7*24*3600;

    private static final String FILE_NAME="fileName";
    private static final String FILE_CHECKSUM="checkSum";

    private AmazonS3 client;

    @PostConstruct
    public void init(){
        log.info("init oss-client {}", ossStorageProperties);
        client = AmazonS3Client.builder()
                .withCredentials((new AWSStaticCredentialsProvider(new BasicAWSCredentials(ossStorageProperties.getAccesskey(), ossStorageProperties.getSecretkey()))))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ossStorageProperties.getEndpointUrl(),
                        ossStorageProperties.getRegion()))
                .withPathStyleAccessEnabled(true) //设置endpoint/bucket方式访问，非bucket.endpoint方式访问
                //如果是公网endpoint，这里请填写HTTPS，如果是内网endpoint请填写HTTP
                .withClientConfiguration(new ClientConfiguration().withProtocol(ossStorageProperties.getProtocol()))
                .build();
    }

    public AmazonS3 getClient(){
        return client;
    }
    @Override
    public void createBucket(String bucketName) throws Exception {
        bucketName = bucketName.replace("/","-").replace("\\","-");
        if(!client.doesBucketExistV2(bucketName)){
            client.createBucket(bucketName);
            log.info("创建 {} bucket成功！",bucketName);
        }else{
            log.info("创建 {} 已经存在，无需创建",bucketName);
        }
    }

    @Override
    public void deleteBucket(String bucketName) throws Exception {
        bucketName = bucketName.replace("/","-").replace("\\","-");
        client.deleteBucket(bucketName);
        log.info("删除 {} bucket成功！",bucketName);
    }

    @Override
    public void deleteFileByName(String bucketName, String fileName) {
        bucketName = bucketName.replace("/","-").replace("\\","-");
        fileName =  fileName.replace("/","-").replace("\\","-");
        if (client.doesBucketExistV2(bucketName)) {
            client.deleteObject(bucketName,  fileName);
        }
    }

    @Override
    public void deleteFileByName(String fileName) {
        deleteFileByName(ossStorageProperties.getBucketName(),fileName);
    }

    @Override
    public void deleteFileByKey(String bucketName, String fileKey) {
        client.deleteObject(bucketName,fileKey);
    }

    @Override
    public void deleteFileByKey(String fileKey) {
        deleteFileByKey(ossStorageProperties.getBucketName(),fileKey);
    }

    @SneakyThrows
    @Override
    public String uploadFile(String bucketName, String originFileName, File localFile)   {
        bucketName = bucketName.replace("/","-").replace("\\","-");

        if (!client.doesBucketExistV2(bucketName)) {
            client.createBucket(bucketName);
        }
        String checkSum = DigestUtils.genfileId(localFile, ossStorageProperties.getCheckSumAlgoType());
        String fileName = DigestUtils.genUniqueFileId();
        try {

            if(originFileName==null){
                originFileName=localFile.getName();
            }

            log.info("real fileName is :" +originFileName );
            ObjectMetadata metadata = new ObjectMetadata();

            HashMap<String, String> userMeta = new HashMap<>();
            userMeta.put(FILE_NAME,originFileName);
            userMeta.put(FILE_CHECKSUM,checkSum);


            metadata.setUserMetadata(userMeta);

            client.putObject(bucketName,  fileName, new FileInputStream(localFile),metadata);
            log.info("localFile name :" + originFileName + " up success!");
        } catch (AmazonServiceException e) {
            if (e.getErrorCode().equals("AccessDenied")) {
                log.error("Access denied!\n" +
                        "1. if you're the bucket owner, make sure your bucket name is uniq.\n" +
                        "2. if you are a grantee, make sure the owner grant the right acl setting.");
            } else {
                log.error("Upload file failed!");
            }
        }
        return fileName;
    }

    @Override
    public String uploadFile(String originFileName, File localFile) {
        return uploadFile(ossStorageProperties.getBucketName(),originFileName,localFile);
    }

    @SneakyThrows
    @Override
    public String uploadFile(String bucketName, String originFileName, InputStream inputStream)  {
        File tempFile = File.createTempFile("tmp_", "_tmp");
        IOUtils.copy(inputStream, new FileOutputStream(tempFile));
        String fileId = uploadFile(bucketName, originFileName, tempFile);
        tempFile.delete();
        return fileId;
    }

    @Override
    public String uploadFile(String originFileName, InputStream inputStream) {
        return uploadFile(ossStorageProperties.getBucketName(),originFileName,inputStream);
    }

    public Map<String,String> getUserMetaData(String bucketName, String fileId){
        bucketName = bucketName.replace("/","-").replace("\\","-");

        log.info("begin download file");
        if (client.doesObjectExist(bucketName, fileId)) {
            S3Object s3Object =  client.getObject(bucketName,fileId);
            log.info("download file existed");
            return s3Object.getObjectMetadata().getUserMetadata();
        } else {
            log.warn("bucketName,{}" + bucketName + ",fileName,{}" + fileId + ";does not existed");
            return null;
        }
    }

    @Override
    public Map<String, String> getUserMetaData(String fileId) {
        return getUserMetaData(ossStorageProperties.getBucketName(),fileId);
    }

    @Override
    public InputStream downLoadFile(String bucketName, String fileId) throws Exception {
        bucketName = bucketName.replace("/","-").replace("\\","-");

        log.info("begin download file");
        if (client.doesObjectExist(bucketName, fileId)) {
            S3Object s3Object =  client.getObject(bucketName,fileId);

            log.info("download file existed");
            return s3Object.getObjectContent();
        } else {

            log.warn("bucketName,{}" + bucketName + ",fileName,{}" + fileId + ";does not existed");
            return null;
        }

    }

    @Override
    public InputStream downLoadFile(String fileId) throws Exception {
        return downLoadFile(ossStorageProperties.getBucketName(),fileId);
    }

    @Override
    public String getFileURL(String fileId, String bucketName, Date expireAt) throws Exception {
        bucketName = bucketName.replace("/","-").replace("\\","-");
        log.info("get file URL params:" + bucketName + "," + fileId);
        URL presignedUrl = client.generatePresignedUrl(bucketName, fileId, expireAt, HttpMethod.GET);
        log.info("urls presignedUrl is:" + presignedUrl);
        log.info("string is:" + presignedUrl.toString());
        return URLDecoder.decode(presignedUrl.toString(),"utf-8");

    }

    @Override
    public String getFileURL(String fileName, Date expireAt) throws Exception {
        return getFileURL(ossStorageProperties.getBucketName(),fileName,expireAt);
    }


    @Override
    public Boolean checkBucketExsit(String bucketName) throws Exception {
        bucketName = bucketName.replace("/","-").replace("\\","-");
        return client.doesBucketExistV2(bucketName);
    }

    @Override
    public Boolean checkBucketFileExist(String bucketName, String fileId) throws Exception {
        bucketName = bucketName.replace("/","-").replace("\\","-");
        return client.doesObjectExist(bucketName,fileId);
    }

    @Override
    public Boolean checkBucketFileExist(String fileName) throws Exception {
        return checkBucketFileExist(ossStorageProperties.getBucketName(),fileName);
    }
}
