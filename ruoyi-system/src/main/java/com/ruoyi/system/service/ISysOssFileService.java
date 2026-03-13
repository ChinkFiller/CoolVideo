package com.ruoyi.system.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ISysOssFileService {

    /**
     * 上传文件
     */
    public void uploadFile(MultipartFile file, String prefix, String key) throws IOException;

    public void uploadFile(File file, String prefix, String key) throws IOException;
    /**
     * 下载文件
     */
    public InputStream downloadFile(String key);


    /**
     * 生成临时下载链接
     */
    public String generateDownloadUrl(String key, long expireMinutes);

    /**
     * 生成临时上传链接（前端可直传）
     */
    public String generateUploadUrl(String key, long expireMinutes);

    /**
     * 列出指定 bucket 下的文件（可带前缀路径）
     * @param prefix 路径前缀，可为空
     */
    public List<String> listFiles(String prefix);

    /**
     * 删除单个文件
     * @param key 文件key
     */
    public void deleteFile(String key);

    /**
     * 批量删除多个文件
     * @param keys  文件key列表
     */
    public void deleteFiles(List<String> keys);
}
