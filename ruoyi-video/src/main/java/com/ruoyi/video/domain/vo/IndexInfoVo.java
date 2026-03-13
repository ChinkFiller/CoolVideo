package com.ruoyi.video.domain.vo;


import com.ruoyi.video.domain.Banner;
import com.ruoyi.video.domain.FastFunction;
import com.ruoyi.video.domain.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IndexInfoVo implements Serializable {
    List<Banner> banner;
    List<Video> latest;
    List<Video> hots;
    List<FastFunction> fastFunction;
    List<Map<String, Object>> indexNotice;
    List<Map<String,Object>> popNotice;
}
