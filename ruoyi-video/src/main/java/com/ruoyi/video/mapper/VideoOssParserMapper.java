package com.ruoyi.video.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface VideoOssParserMapper {

    @Select("select file_path from cv_video_cache where vid=#{vid} and part=#{part} and oss_id=#{ossId}")
    String selectVideoOssKey(@Param("vid") Long vid, @Param("part") Integer part,@Param("ossId") Long ossId);
}
