package com.jlink.ossstorage.client;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

public interface OssClient {
    /**
     * 创建bucket
     * @param bucketName
     */
    void createBucket(String bucketName) throws Exception;

    /**
     * 删除bucket
     * @param bucketName
     */
    void deleteBucket(String bucketName)throws Exception;

    /**
     *
     * @param bucketName
     * @param fileName
     */
    void deleteFileByName(String bucketName,String fileName);
    /**
     *
     * @param fileName
     */
    void deleteFileByName(String fileName);
    /**
     * 通过fileKey删除file
     * @param bucketName
     * @param fileKey
     */
    void deleteFileByKey(String bucketName,String fileKey);
    /**
     * 通过fileKey删除file
     * @param fileKey
     */
    void deleteFileByKey(String fileKey);
    /**
     * bucketName优先通过参数获取，为null时从配置获取 上传文件
     * @param bucketName
     * @param localFile
     * @throws Exception
     */
    String uploadFile(String bucketName,String originFileName, File localFile) ;
    /**
     * bucketName优先通过参数获取，为null时从配置获取 上传文件
     * @param localFile
     * @throws Exception
     */
    String uploadFile(String originFileName, File localFile) ;

    /**
     *
     * @param bucketName
     * @param inputStream
     * @throws Exception
     */
    String uploadFile(String bucketName, String originFileName, InputStream inputStream) ;
    /**
     *
     * @param inputStream
     * @throws Exception
     */
    String uploadFile(String originFileName, InputStream inputStream) ;

    /**
     * 下载文件
     * @param bucketName
     * @return
     * @throws Exception
     */
    InputStream downLoadFile(String bucketName, String fileId) throws Exception;

    /**
     * 下载文件
     * @return
     * @throws Exception
     */
    InputStream downLoadFile( String fileId) throws Exception;

    /**
     * 获取文件访问地址
     * @param fileName
     * @param bucketName
     * @param expireAt
     * @return
     * @throws Exception
     */
    String getFileURL(String fileName, String bucketName, Date expireAt) throws Exception;

    /**
     * 获取文件访问地址
     * @param fileName
     * @param expireAt
     * @return
     * @throws Exception
     */
    String getFileURL(String fileName, Date expireAt) throws Exception;
    /**
     * 判断bucketName存在
     * @param bucketName
     * @return
     * @throws Exception
     */
    Boolean checkBucketExsit(String bucketName) throws Exception;

    /**
     *
     * @param bucketName
     * @param fileName
     * @return
     * @throws Exception
     */
    Boolean checkBucketFileExist(String bucketName,String fileName) throws Exception;
    /**
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    Boolean checkBucketFileExist(String fileName) throws Exception;
    /**
     * 获取文件元数据
     * @param bucketName
     * @param fileId
     * @return
     */
     Map<String,String> getUserMetaData(String bucketName, String fileId);
    /**
     * 获取文件元数据
     * @param fileId
     * @return
     */
    Map<String,String> getUserMetaData(String fileId);
}
