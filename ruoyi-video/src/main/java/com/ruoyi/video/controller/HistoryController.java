package com.ruoyi.video.controller;


import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.ResultCodes;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.video.domain.History;
import com.ruoyi.video.domain.vo.UserHistoryInfoVo;
import com.ruoyi.video.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/history")
@Tag(name = "历史记录",description = "管理用户的播放历史等操作")
public class HistoryController extends BaseController {

    @Autowired
    HistoryService historyService;


    @Operation(summary = "获取用户播放历史")
    @GetMapping("/list")
    public TableDataInfo getList(
            @RequestParam(value = "keyWord", defaultValue = "") String keyWord,
            @RequestParam(value = "pageNum",required = false,defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize
    ){
        startPage();
        List<UserHistoryInfoVo> list=historyService.getUserHistory(SecurityUtils.getUserId(),keyWord,pageNum,pageSize);
        return getDataTable(list);
    }


    @Operation(summary = "添加或更新用户播放历史")
    @PostMapping("/update")
    public AjaxResult update(@RequestBody History history){
        history.setUid(SecurityUtils.getUserId());
        try {
            // 记录历史记录
            historyService.setHistory(history);
            return success();
        }catch (Exception e){
            throw new ServiceException(ResultCodes.UNKNOWN_ERROR);
        }
    }


    @Operation(summary = "删除用户播放历史")
    @DeleteMapping("/delete/{id}")
    public AjaxResult delete(
            @PathVariable("id") Long id
    ){
        if (historyService.removeHistory(SecurityUtils.getUserId(),id)){
            return AjaxResult.success();
        }else{
            return AjaxResult.error(ResultCodes.OPTION_ERROR);
        }
    }

    @Operation(summary = "用户退出播放页执行刷新")
    @GetMapping("/flush/{vid}")
    public AjaxResult flush(
            @PathVariable("vid") Long vid
    ){
        historyService.flushHistory(vid);
        return AjaxResult.success();
    }
}
