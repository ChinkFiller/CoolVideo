package com.ruoyi.video.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommonDictMapper {

    @Select("select name as label,id as value from cv_video_info")
    List<Map<String, Object>> selectVideoIndex();


    @Select("select nick_name as label,user_id as value from sys_user where del_flag=0")
    List<Map<String, Object>> selectUserIndex();
}
