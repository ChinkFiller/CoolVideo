package com.ruoyi.video.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.video.domain.History;
import com.ruoyi.video.domain.vo.UserHistoryInfoVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HistoryMapper extends BaseMapper<History> {

    List<History> getAllUserHistory();
    List<UserHistoryInfoVo> getUserHistory(@Param("uid") Long uid, @Param("keyWord") String keyWord);

    History getUserOneHistory(@Param("uid") Long uid,@Param("vid") Long vid);

    int deleteHistory(@Param("uid") Long uid, @Param("vid") Long vid);

    int upsert(List<History> historyList);

    void updateOneHistory(History history);
}
