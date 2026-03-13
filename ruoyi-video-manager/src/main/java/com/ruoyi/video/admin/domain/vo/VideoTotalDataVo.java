package com.ruoyi.video.admin.domain.vo;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class VideoTotalDataVo {
    List<Map<String, Object>> topPlayCount;
    List<Map<String, Object>> topLike;
    List<Map<String, Object>> topComment;
}
