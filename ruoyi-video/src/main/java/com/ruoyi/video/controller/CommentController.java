package com.ruoyi.video.controller;


import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.ResultCodes;
import com.ruoyi.video.domain.Comment;
import com.ruoyi.video.domain.vo.CommentDataVo;
import com.ruoyi.video.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@Tag(name = "评论服务", description = "用于获取一个视频的评论")
public class CommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/list/{id}")
    @Operation(summary = "获取视频评论")
    @Parameter(name = "id", description = "视频id")
    public TableDataInfo comments(
            @PathVariable(value = "id") Long id
    ){
        startPage();
        List<CommentDataVo> list=commentService.getComment(id);
        return getDataTable(list);
    }

    @PostMapping("/add")
    @Operation(summary = "添加评论")
    public AjaxResult addComment(
            @RequestBody Comment comment
    ){
        if (commentService.addComment(comment)){
            return AjaxResult.success();
        }else{
            return AjaxResult.error(ResultCodes.OPTION_ERROR);
        }
    }

    @GetMapping("/agree/{id}")
    @Operation(summary = "点赞评论或取消点赞，系统自动判断")
    public AjaxResult agreeComment(
            @PathVariable(value = "id") Long id
    ){
        Comment data=commentService.getOneComment(id);
        if (data==null){return AjaxResult.error(ResultCodes.NOT_FOUND);}
        if (commentService.agreeOrDisAgreeComment(data)){
            return AjaxResult.success();
        }else{
            return AjaxResult.error(ResultCodes.OPTION_ERROR);
        }
    }

}
