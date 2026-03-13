package com.ruoyi.video.admin.service;

import com.ruoyi.video.admin.domain.form.CommentProhibitForm;
import com.ruoyi.video.admin.domain.vo.CommentManagementVo;

import java.util.List;

/**
 * 评论管理Service接口
 *
 * @author ruoyi
 * @date 2025-08-06
 */
public interface CommentManagementService
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
     * 批量删除评论管理
     *
     * @param ids 需要删除的评论管理主键集合
     * @return 结果
     */
    public int deleteCommentByIds(String[] ids);

    /**
     * 删除评论管理信息
     *
     * @param id 评论管理主键
     * @return 结果
     */
    public int deleteCommentById(String id);

    int addInterceptRole(CommentProhibitForm form);

    CommentProhibitForm getProhibitConfig();
}
