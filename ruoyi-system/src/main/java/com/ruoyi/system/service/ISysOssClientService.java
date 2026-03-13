package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysOssConfig;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

public interface ISysOssClientService {

    S3Client getS3Client();

    S3Presigner getS3Presigner();

    SysOssConfig getActiveConfig();

    void reloadClientConfig();

    S3Client createTemClient(SysOssConfig config);

}
