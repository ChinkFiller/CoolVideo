package com.ruoyi.video.admin.service.impl;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.ResultCodes;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.service.ISysChunkUploadService;
import com.ruoyi.system.service.ISysOssClientService;
import com.ruoyi.system.service.ISysOssFileService;
import com.ruoyi.video.admin.domain.VideoStorage;
import com.ruoyi.video.admin.domain.form.VideoStorageUploadForm;
import com.ruoyi.video.admin.mapper.VideoStorageMapper;
import com.ruoyi.video.admin.service.VideoManagementService;
import com.ruoyi.video.admin.service.VideoStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class VideoStorageServiceImpl implements VideoStorageService {

    @Autowired
    private VideoStorageMapper VideoStorageMapper;

    @Autowired
    ISysOssClientService ossClientService;

    @Autowired
    ISysOssFileService ossFileService;

    @Autowired
    RedisCache redisService;

    @Autowired
    VideoManagementService videoManagementService;

    @Autowired
    ISysChunkUploadService chunkUploadService;

    /**
     * 查询视频资源信息
     *
     * @param id 视频资源信息主键
     * @return 视频资源信息
     */
    @Override
    public VideoStorage selectVideoStorageById(Long id)
    {
        // 查询数据库中的原数据
        VideoStorage storage=VideoStorageMapper.selectVideoStorageById(id);
        // 生成下载浏览链接
        storage.setFilePath(ossFileService.generateDownloadUrl(storage.getFilePath(),90));
        // 返回数据
        return storage;
    }

    /**
     * 查询视频资源信息列表
     *
     * @param VideoStorage 视频资源信息
     * @return 视频资源信息
     */
    @Override
    public List<VideoStorage> selectVideoStorageList(VideoStorage VideoStorage) {
        // 设置仅查看当前默认存储的信息
        VideoStorage.setOssId(ossClientService.getActiveConfig().getOssConfigId());
        return VideoStorageMapper.selectVideoStorageList(VideoStorage);
    }

    /**
     * 新增视频资源信息
     *
     * @param form 视频资源表单
     * @return 结果
     */
    @Override
    public int insertVideoStorage(VideoStorageUploadForm form)
    {
        if (!videoManagementService.isVideoExist(form.getVid(),form.getPart())){
            throw new RuntimeException("该视频分集不存在");
        }

        String ossFileId;
        try {
            ossFileId=chunkUploadService.mergeChunkToOss(form.getFileMd5(), form.getFileName(), form.getChunkCount());
        }catch (Exception e){
            throw new ServiceException(ResultCodes.MERGE_CHUNK_ERROR);
        }
        // 加载文件信息
        return VideoStorageMapper.insertVideoStorage(VideoStorage
                .builder()
                .vid(form.getVid())
                .part(form.getPart())
                .ossId(ossClientService.getActiveConfig().getOssConfigId())
                .filePath(ossFileId)
                .build()
        );
    }

    /**
     * 修改视频资源信息
     *
     * @param form 视频资源表单
     * @return 结果
     */
    @Override
    public int updateVideoStorage(VideoStorageUploadForm form)
    {
        if (!videoManagementService.isVideoExist(form.getVid(),form.getPart())){
            throw new RuntimeException("该视频分集不存在");
        }

        if (form.getFileMd5()==null){
            return VideoStorageMapper.updateVideoStorage(VideoStorage.builder()
                    .id(form.getId())
                    .vid(form.getVid())
                    .part(form.getPart())
                    .build()
            );
        }else{
            VideoStorage storage=VideoStorageMapper.selectVideoStorageById(form.getId());

            // 删除原文件
            ossFileService.deleteFile(storage.getFilePath());

            String ossFileId;
            try {
                ossFileId=chunkUploadService.mergeChunkToOss(form.getFileMd5(), form.getFileName(), form.getChunkCount());
            }catch (Exception e){
                throw new ServiceException(ResultCodes.MERGE_CHUNK_ERROR);
            }
            //写入数据库信息
            return VideoStorageMapper.updateVideoStorage(VideoStorage.builder()
                    .id(form.getId())
                    .vid(form.getVid())
                    .part(form.getPart())
                    .ossId(ossClientService.getActiveConfig().getOssConfigId())
                    .filePath(ossFileId)
                    .build()
            );
        }
    }

    /**
     * 批量删除视频资源信息
     *
     * @param ids 需要删除的视频资源信息主键
     * @return 结果
     */
    @Override
    public int deleteVideoStorageByIds(Long[] ids)
    {
        List<String> deleteFileKeys=new ArrayList<>();
        Arrays.asList(ids).forEach(item->{
            // 添加需要删除的key
            deleteFileKeys.add(VideoStorageMapper.selectVideoStorageById(item).getFilePath());
        });
        // 调度oss文件服务删除文件
        ossFileService.deleteFiles(deleteFileKeys);
        // 删除数据库中的文件信息
        return VideoStorageMapper.deleteVideoStorageByIds(ids);
    }

    /**
     * 删除视频资源信息信息
     *
     * @param id 视频资源信息主键
     * @return 结果
     */
    @Override
    public int deleteVideoStorageById(Long id)
    {
        // 删除Oss服务器上的文件数据
        ossFileService.deleteFile(VideoStorageMapper.selectVideoStorageById(id).getFilePath());

        // 删除数据库中的文件信息
        return VideoStorageMapper.deleteVideoStorageById(id);
    }
}
