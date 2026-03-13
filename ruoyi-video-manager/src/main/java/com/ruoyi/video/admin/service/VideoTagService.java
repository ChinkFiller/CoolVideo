package com.ruoyi.video.admin.service;

import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.video.admin.domain.vo.VideoTagVo;

public interface VideoTagService {

    VideoTagVo getVideoTagInfo(Long id);

    int addTag(SysDictData data);

    int updateTag(VideoTagVo vo);

    int deleteTag(Long[] id);
}
