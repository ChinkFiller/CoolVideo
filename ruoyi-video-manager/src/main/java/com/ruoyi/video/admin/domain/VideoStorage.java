package com.ruoyi.video.admin.domain;

import com.ruoyi.common.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoStorage {
    /** id */
    private Long id;

    /** 视频的id */
    @Excel(name = "视频的id")
    private Long vid;

    /** 视频的分集 */
    @Excel(name = "视频的分集")
    private Integer part;

    /** 文件的存储路径 */
    @Excel(name = "文件的存储路径")
    private String filePath;

    /** 对应的oss存储的配置id */
    @Excel(name = "存储位置")
    private Long ossId;
}
