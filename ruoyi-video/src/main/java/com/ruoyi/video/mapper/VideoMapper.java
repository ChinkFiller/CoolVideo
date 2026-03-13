package com.ruoyi.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.video.domain.Video;
import com.ruoyi.video.domain.bo.PlayCountBo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoMapper extends BaseMapper<Video> {
    Video getVideo(Long id);
    int agreeVideo(@Param("uid") Long uid,@Param("vid") Long vid);
    int disAgreeVideo(@Param("uid") Long uid,@Param("vid") Long vid);

    List<Video> searchVideo(@Param("keyWord") String keyWord);

    List<Video> getRecommendVideo(Long id);

    List<Video> getVideoByFilter(
            @Param("order") String order,
            @Param("letter") String letter,
            @Param("year") Integer year
    );

    int subscribeVideo(@Param("uid") Long uid,@Param("vid") Long vid);

    int cancelSubscribeVideo(@Param("uid") Long uid, @Param("vid") Long vid);

    List<Video> getUserSubscribeVideo(@Param("uid") Long uid, @Param("keyWord") String keyWord);

    int isUserSubscribeThisVideo(@Param("uid") Long uid, @Param("vid") Long vid);

    int isUserLikeThisVideo(@Param("uid") Long uid, @Param("vid") Long vid);

    /**
     * 获取所有视频的播放次数信息
     *
     * @return 返回一个List集合，其中每个元素是一个Map对象
     *         Map的键为String类型，表示视频的属性名称
     *         Map的值为Object类型，表示对应属性的值，可能是播放次数等数据
     */
    List<PlayCountBo> getAllVideoPlayCount();

    void updatePlayCount(List<PlayCountBo> data);
}
