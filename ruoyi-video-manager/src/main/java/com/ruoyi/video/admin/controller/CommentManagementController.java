package com.ruoyi.video.admin.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.video.admin.domain.form.CommentProhibitForm;
import com.ruoyi.video.admin.domain.vo.CommentManagementVo;
import com.ruoyi.video.admin.service.CommentManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 评论管理Controller
 *
 * @author ruoyi
 * @date 2025-08-06
 */
@RestController
@RequestMapping("/system/video/comments")
public class CommentManagementController extends BaseController
{
    @Autowired
    private CommentManagementService commentsService;

    /**
     * 查询评论管理列表
     */
    @PreAuthorize("@ss.hasPermi('video:comment:list')")
    @GetMapping("/list")
    public TableDataInfo list(CommentManagementVo comments)
    {
        startPage();
        List<CommentManagementVo> list = commentsService.selectCommentList(comments);
        return getDataTable(list);
    }

    /**
     * 导出评论管理列表
     */
    @PreAuthorize("@ss.hasPermi('video:comment:export')")
    @Log(title = "评论管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CommentManagementVo comments)
    {
        List<CommentManagementVo> list = commentsService.selectCommentList(comments);
        ExcelUtil<CommentManagementVo> util = new ExcelUtil<CommentManagementVo>(CommentManagementVo.class);
        util.exportExcel(response, list, "评论管理数据");
    }

    /**
     * 获取评论管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('video:comment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(commentsService.selectCommentById(id));
    }

    /**
     * 查看拦截配置
     */
    @PreAuthorize("@ss.hasAnyPermi('video:comment:config')")
    @GetMapping("/config")
    public AjaxResult getProhibitConfig(){
        return success(commentsService.getProhibitConfig());
    }


    /**
     * 修改评论拦截规则
     */
    @PreAuthorize("@ss.hasAnyPermi('video:comment:config:edit')")
    @PutMapping
    public AjaxResult setProhibitConfig(@RequestBody CommentProhibitForm form){
        return toAjax(commentsService.addInterceptRole(form));
    }

    /**
     * 删除评论管理
     */
    @PreAuthorize("@ss.hasPermi('video:comment:remove')")
    @Log(title = "评论管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(commentsService.deleteCommentByIds(ids));
    }
}
