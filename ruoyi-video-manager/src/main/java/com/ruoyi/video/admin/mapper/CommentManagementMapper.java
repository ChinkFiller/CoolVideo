package com.ruoyi.video.admin.mapper;


import com.ruoyi.video.admin.domain.vo.CommentManagementVo;

import java.util.List;

/**
 * 评论管理Mapper接口
 *
 * @author ruoyi
 * @date 2025-08-06
 */
public interface CommentManagementMapper
{
    /**
     * 查询评论管理
     *
     * @param id 评论管理主键
     * @return 评论管理
     */
    public CommentManagementVo selectCommentById(String id);

    /**
     * 查询评论管理列表
     *
     * @param comments 评论管理
     * @return 评论管理集合
     */
    public List<CommentManagementVo> selectCommentList(CommentManagementVo comments);

    /**
     * 新增评论管理
     *
     * @param comments 评论管理
     * @return 结果
     */
    public int insertComment(CommentManagementVo comments);

    /**
     * 修改评论管理
     *
     * @param comments 评论管理
     * @return 结果
     */
    public int updateComment(CommentManagementVo comments);

    /**
     * 删除评论管理
     *
     * @param id 评论管理主键
     * @return 结果
     */
    public int deleteCommentById(String id);

    /**
     * 批量删除评论管理
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCommentByIds(String[] ids);

    /**
     * 批量删除视频数据保存的数据
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFilmDataByIds(String[] ids);


    /**
     * 通过评论管理主键删除视频数据保存的数据信息
     *
     * @param id 评论管理ID
     * @return 结果
     */
    public int deleteFilmDataById(String id);
}
