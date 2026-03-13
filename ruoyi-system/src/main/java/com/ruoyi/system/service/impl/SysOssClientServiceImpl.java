package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.oss.OssClientBuilder;
import com.ruoyi.system.domain.SysOssConfig;
import com.ruoyi.system.service.ISysOssClientService;
import com.ruoyi.system.service.ISysOssConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import javax.annotation.PostConstruct;

@Service
public class SysOssClientServiceImpl implements ISysOssClientService {

    @Autowired
    ISysOssConfigService ossConfigService;

    private static final Logger logger = LoggerFactory.getLogger("oss-service");

    public S3Client client;
    public S3Presigner presigner;
    public SysOssConfig activeConfig;



    @PostConstruct
    void init(){
        SysOssConfig config = ossConfigService.getOssConfig();
        try {
            // 初始化客户端
            this.client=OssClientBuilder.createS3Client(
                    Region.of(config.getRegion().isEmpty()?"auto":config.getRegion()),
                    config.getEndpoint(),
                    config.getAccessKey(),
                    config.getSecretKey(),
                    config.getIsHttps().equals("Y")
            );
            // 初始化预签名客户端
            this.presigner=OssClientBuilder.createPresigner(
                    Region.of(config .getRegion().isEmpty()?"auto":config.getRegion()),
                    config.getEndpoint(),
                    config.getAccessKey(),
                    config.getSecretKey(),
                    config.getIsHttps().equals("Y")
            );
            //设置当前活动的配置
            this.activeConfig=config;
            logger.info("Oss客户端初始化成功");
        }catch (Exception e){
            logger.error("初始化客户端失败",e);
        }
    }

    @Override
    public S3Client getS3Client() {
        return this.client;
    }

    @Override
    public S3Presigner getS3Presigner() {
        return this.presigner;
    }

    @Override
    public SysOssConfig getActiveConfig() {
        return this.activeConfig;
    }

    @Override
    public void reloadClientConfig() {
        this.init();
    }

    @Override
    public S3Client createTemClient(SysOssConfig config) {
        return OssClientBuilder.createS3Client(
                Region.of(config.getRegion().isEmpty()?"auto":config.getRegion()),
                config.getEndpoint(),
                config.getAccessKey(),
                config.getSecretKey(),
                config.getIsHttps().equals("Y")
        );
    }
}
