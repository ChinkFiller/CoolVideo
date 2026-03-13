package com.ruoyi.video.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.enums.ResultCodes;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ip.AddressUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.plugin.PluginLoader;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.video.api.ContextFilter;
import com.ruoyi.video.domain.Comment;
import com.ruoyi.video.domain.vo.CommentDataVo;
import com.ruoyi.video.mapper.CommentMapper;
import com.ruoyi.video.service.CommentService;
import com.ruoyi.video.service.VideoService;
import org.pf4j.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论服务
 * 用于发表评论
 * **/
@Service
public class CommentServiceImpl implements CommentService{

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    ISysUserService userService;

    @Autowired
    VideoService videoService;

    @Autowired
    ISysConfigService configService;

    @Autowired
    PluginManager pluginManager;

    @Override
    @Cacheable(value = "oneCommentCache",key = "#id" ,sync = true)
    public Comment getOneComment(Long id) {
        return commentMapper.selectById(id);
    }

    // 根据视频id获取评论
    @Override
    @Cacheable(value = "commentListCache",key = "#id",sync = true)
    public List<CommentDataVo> getComment(Long id) {
        List<Comment> data=commentMapper.getCommentByVideoId(id);
        List<CommentDataVo> result=new ArrayList<>();
        for (Comment one : data) {
            CommentDataVo dataVo=new CommentDataVo().getCommentDataVo(one);
            dataVo.setIsUserAgree(commentMapper.isUserAgreeThisComment(one.getId(),SecurityUtils.getUserId()));
            result.add(dataVo);
        }
        return result;
    }


    @Override
    @CacheEvict(value = "commentListCache",key = "#comment.vid")
    public boolean addComment(Comment comment){
        //过滤禁止评论的用户
        if (configService.selectConfigByKey("comment.prohibit.user").replace(CacheConstants.FILTER_KEY,"").contains(SecurityUtils.getUserId().toString())){
            throw new ServiceException(ResultCodes.USER_COMMENT_PROHIBIT);
        }

        //过滤禁止评论的视频
        if (configService.selectConfigByKey("comment.prohibit.video").replace(CacheConstants.FILTER_KEY,"").contains(comment.getVid().toString())){
            throw new ServiceException(ResultCodes.VIDEO_COMMENT_PROHIBIT);
        }
        //尝试加载插件
        try {
            List<ContextFilter> filterList = pluginManager.getExtensions(ContextFilter.class);
            for (ContextFilter filter : filterList) {
                if (filter.doCommentFilter(comment.getContent())){
                    throw new ServiceException(ResultCodes.COMMENT_CONTAIN_PROHIBIT_WORD);
                }
            }

        }catch (Exception ignored){}

        //使用系统设置的拦截规则
        //过滤违规关键字
        if (configService.selectConfigByKey("comment.prohibit.keyword").replace(CacheConstants.FILTER_KEY,"").contains(comment.getContent())){
            throw new ServiceException(ResultCodes.COMMENT_CONTAIN_PROHIBIT_WORD);
        }

        comment.setUid(SecurityUtils.getUserId());
        comment.setTime(LocalDateTime.now());
        // 获取客户端的ip并写入
        comment.setRemoteIp(IpUtils.getIpAddr());
        // 获取客户端地址并写入
        comment.setRemoteLocation(AddressUtils.getRealAddressByIP(IpUtils.getIpAddr()));
        if (!videoService.isVideoExist(comment.getVid(),1)){
            throw new ServiceException(ResultCodes.NOT_FOUND);
        }
        if (StringUtils.isBlank(comment.getContent())){
            throw new ServiceException(ResultCodes.UPLOAD_EMPTY_ERROR);
        }
        return commentMapper.addComment(comment)>0;
    }


    // 点赞一个评论的id
    @Override
    @CacheEvict(value = "commentListCache",key = "#data.vid")
    public boolean agreeOrDisAgreeComment(Comment data) {
        // 用户是否已经点赞
        if (commentMapper.isUserAgreeThisComment(data.getId(), SecurityUtils.getUserId())>0){
            return commentMapper.disagreeComment(data.getId(),SecurityUtils.getUserId())>0;
        }else{
            return commentMapper.agreeComment(data.getId(),SecurityUtils.getUserId())>0;
        }
    }


}
