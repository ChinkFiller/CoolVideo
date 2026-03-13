package com.ruoyi.system.service.impl;


import com.ruoyi.system.service.ISysOssClientService;
import com.ruoyi.system.service.ISysOssConfigService;
import com.ruoyi.system.service.ISysOssFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@Service
public class SysOssFileServiceImpl implements ISysOssFileService {

    @Autowired
    ISysOssConfigService sysOssConfig;

    @Autowired
    ISysOssClientService ossClientService;

    @Override
    public void uploadFile(MultipartFile file,@Nullable String prefix ,String key) throws IOException {
        // 处理前缀（如果存在）
        String finalKey = (prefix != null && !prefix.isEmpty())
                ? prefix.endsWith("/") ? prefix + key : prefix + "/" + key
                : key;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(ossClientService.getActiveConfig().getBucketName())
                .key(finalKey)
                .contentType(file.getContentType())
                .build();

        ossClientService.getS3Client().putObject(request,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    @Override
    public void uploadFile(File file, String prefix, String key) throws IOException {
        // 处理前缀（如果存在）
        String finalKey = (prefix != null && !prefix.isEmpty())
                ? prefix.endsWith("/") ? prefix + key : prefix + "/" + key
                : key;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(ossClientService.getActiveConfig().getBucketName())
                .key(finalKey)
                .contentType(Files.probeContentType(file.toPath()))
                .build();

        ossClientService.getS3Client().putObject(
                request,
                RequestBody.fromFile(file));
    }

    @Override
    public InputStream downloadFile(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(ossClientService.getActiveConfig().getBucketName())
                .key(key)
                .build();
        ResponseInputStream<GetObjectResponse> response = ossClientService.getS3Client().getObject(getObjectRequest);
        return response;
    }

    @Override
    public String generateDownloadUrl(String key, long expireMinutes) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(ossClientService.getActiveConfig().getBucketName())
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expireMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = ossClientService.getS3Presigner().presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    @Override
    public String generateUploadUrl(String key, long expireMinutes) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(ossClientService.getActiveConfig().getBucketName())
                .key(key)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expireMinutes))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = ossClientService.getS3Presigner().presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }

    @Override
    public List<String> listFiles(String prefix) {
        ListObjectsV2Request.Builder builder = ListObjectsV2Request.builder()
                .bucket(ossClientService.getActiveConfig().getBucketName())
                .maxKeys(1000); // 每次最多拉取1000个

        if (prefix != null && !prefix.isEmpty()) {
            builder.prefix(prefix);
        }

        ListObjectsV2Response response =ossClientService.getS3Client().listObjectsV2(builder.build());

        List<String> result = new ArrayList<>();
        for (S3Object obj : response.contents()) {
            result.add(obj.key());
        }

        return result;
    }

    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(ossClientService.getActiveConfig().getBucketName())
                .key(key)
                .build();

        ossClientService.getS3Client().deleteObject(deleteObjectRequest);
    }

    @Override
    public void deleteFiles(List<String> keys) {
        if (keys == null || keys.isEmpty()) return;
        List<ObjectIdentifier> objects = new ArrayList<>();
        for (String key : keys) {
            objects.add(ObjectIdentifier.builder().key(key).build());
        }

        Delete delete = Delete.builder().objects(objects).build();

        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                .bucket(ossClientService.getActiveConfig().getBucketName())
                .delete(delete)
                .build();

        ossClientService.getS3Client().deleteObjects(deleteObjectsRequest);
    }
}
