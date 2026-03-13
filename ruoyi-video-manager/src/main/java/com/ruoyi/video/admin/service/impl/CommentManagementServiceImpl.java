package com.ruoyi.video.admin.service.impl;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.video.admin.domain.form.CommentProhibitForm;
import com.ruoyi.video.admin.domain.vo.CommentManagementVo;
import com.ruoyi.video.admin.mapper.CommentManagementMapper;
import com.ruoyi.video.admin.service.CommentManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论管理Service业务层处理
 *
 * @author ruoyi
 * @date 2025-08-06
 */
@Service
public class CommentManagementServiceImpl implements CommentManagementService
{
    @Autowired
    private CommentManagementMapper commentsMapper;

    @Autowired
    ISysConfigService configService;

    /**
     * 查询评论管理
     *
     * @param id 评论管理主键
     * @return 评论管理
     */
    @Override
    public CommentManagementVo selectCommentById(String id)
    {
        return commentsMapper.selectCommentById(id);
    }

    /**
     * 查询评论管理列表
     *
     * @param comments 评论管理
     * @return 评论管理
     */
    @Override
    public List<CommentManagementVo> selectCommentList(CommentManagementVo comments)
    {
        return commentsMapper.selectCommentList(comments);
    }

    /**
     * 新增评论管理
     *
     * @param comments 评论管理
     * @return 结果
     */
    @Transactional
    @Override
    public int insertComment(CommentManagementVo comments)
    {
        return commentsMapper.insertComment(comments);
    }

    /**
     * 修改评论管理
     *
     * @param comments 评论管理
     * @return 结果
     */
    @Transactional
    @Override
    public int updateComment(CommentManagementVo comments)
    {
        return commentsMapper.updateComment(comments);
    }

    /**
     * 批量删除评论管理
     *
     * @param ids 需要删除的评论管理主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCommentByIds(String[] ids)
    {
        return commentsMapper.deleteCommentByIds(ids);
    }

    /**
     * 删除评论管理信息
     *
     * @param id 评论管理主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCommentById(String id)
    {
        return commentsMapper.deleteCommentById(id);
    }

    @Override
    @Transactional
    public int addInterceptRole(CommentProhibitForm form) {
        SysConfig config;
        //设置新的关键词合集
        config=configService.selectFullConfigByKey("comment.prohibit.keyword");
        if(config!=null){
            config.setConfigValue(CacheConstants.FILTER_KEY+form.getKeyword());
            configService.updateConfig(config);
        }

        //设置禁止评论的用户
        config=configService.selectFullConfigByKey("comment.prohibit.user");
        if(config!=null){
            config.setConfigValue(CacheConstants.FILTER_KEY+form.getUserIds());
            configService.updateConfig(config);
        }

        //设置禁止评论的视频
        config=configService.selectFullConfigByKey("comment.prohibit.video");
        if(config!=null){
            config.setConfigValue(CacheConstants.FILTER_KEY+form.getVideoIds());
            configService.updateConfig(config);
        }
        return 1;
    }

    @Override
    public CommentProhibitForm getProhibitConfig() {
        return CommentProhibitForm.builder()
                .keyword(configService.selectConfigByKey("comment.prohibit.keyword").replace(CacheConstants.FILTER_KEY,""))
                .userIds(configService.selectConfigByKey("comment.prohibit.user").replace(CacheConstants.FILTER_KEY,""))
                .videoIds(configService.selectConfigByKey("comment.prohibit.video").replace(CacheConstants.FILTER_KEY,""))
                .build();
    }

}
