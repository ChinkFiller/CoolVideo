package com.ruoyi.video.admin.domain.vo;


import com.ruoyi.common.annotation.Excel;
import com.ruoyi.video.domain.Comment;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CommentManagementVo extends Comment {
    //用户昵称
    @Excel(name = "用户昵称")
    String nickName;
    @Excel(name = "视频名称")
    String videoName;
    @Excel(name = "用户手机号")
    String phoneNumber;
    @Excel(name = "用户邮箱")
    String email;
    //查询参数使用，开始日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate timeStart;
    //查询参数使用，结束日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate timeEnd;
}
