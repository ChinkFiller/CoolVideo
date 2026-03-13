package com.ruoyi.video.service;


import com.ruoyi.video.domain.History;
import com.ruoyi.video.domain.vo.UserHistoryInfoVo;

import java.util.List;

public interface HistoryService {
    List<UserHistoryInfoVo> getUserHistory(Long uid, String keyWord, int pageNum, int pageSize);
    boolean removeHistory(Long uid,Long vid);
    void setHistory(History history);
    History getUserOneHistory(Long uid, Long vid);

    void flushHistory(Long vid);
}
