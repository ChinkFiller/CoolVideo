package com.ruoyi.video.admin.service.impl;

import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.system.service.ISysDictDataService;
import com.ruoyi.video.admin.domain.vo.VideoTagVo;
import com.ruoyi.video.admin.mapper.VideoManagementMapper;
import com.ruoyi.video.admin.service.VideoTagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class VideoTagServiceImpl implements VideoTagService {

    @Autowired
    private VideoManagementMapper videoManagementMapper;

    @Autowired
    private ISysDictDataService dictDataService;


    @Override
    public VideoTagVo getVideoTagInfo(Long id) {
        SysDictData data = dictDataService.selectDictDataById(id);
        VideoTagVo vo = new VideoTagVo();
        BeanUtils.copyProperties(data,vo);
        // 获取已打上该标签的视频
        vo.setTagVideoIds(videoManagementMapper.selectVideoIdByTagCode(vo.getDictValue()));
        return vo;
    }

    @Override
    public int addTag(SysDictData data) {
        data.setCreateBy(SecurityUtils.getUsername());
        data.setDictType("video_tag");
        data.setDictValue(UUID.getUUIDWithOutLine().substring(0,8));
        System.out.println(data);
        return dictDataService.insertDictData(data);
    }

    @Override
    @Transactional
    public int updateTag(VideoTagVo data) {
        data.setUpdateBy(SecurityUtils.getUsername());
        data.setDictType("video_tag");
        // 更新插入tag
        videoManagementMapper.updateVideoTagByTagCode(data.getTagVideoIds(),data.getDictValue());
        return dictDataService.updateDictData(data);
    }

    @Override
    @Transactional
    public int deleteTag(Long[] id) {
        videoManagementMapper.deleteVideoTag(id);
        dictDataService.deleteDictDataByIds(id);
        return 1;
    }
}
