package com.ruoyi.video.service;


import com.ruoyi.video.domain.Video;

import java.util.List;


public interface VideoService {
    Video getOneVideo(Long id);

    List<Video> searchVideo(String keyWord, Integer pageNum, Integer pageSize);

    boolean agreeOrDisagreeVideo(Long id);

    boolean isVideoExist(Long vid, Integer part);

    int isUserSubscribeThisVideo(Long uid,Long vid);

    boolean subscribeVideoOrCancelSubscribe(Long vid);

    List<Video> searchVideoWithFilter(String order, String letter, String category, Integer year, Integer type, Integer pageNum, Integer pageSize);

    List<Video> getUserSubscribeVideo(String keyWord, Integer pageNum, Integer pageSize);

    int isUserLikeThisVideo(Long uid,Long vid);
}
