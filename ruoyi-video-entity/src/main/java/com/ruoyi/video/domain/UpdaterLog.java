package com.ruoyi.video.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新器日志对象 updater_log
 *
 * @author ruoyi
 * @date 2025-08-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdaterLog implements Serializable {
    private static final long serialVersionUID = 1L;

    Long id;

    /** 信息 */
    @Excel(name = "信息")
    private String message;

    /** 日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date time;
}
