package com.ruoyi.video.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.*;


/**
 * 视频周期对象 week
 *
 * @author ruoyi
 * @date 2025-08-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WeekTable extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 周表id */
    @Excel(name = "周表ID")
    private Long id;

    /** 视频的id */
    @Excel(name = "视频的id")
    private Long vid;

    /** 周期 */
    @Excel(name = "周期")
    private Integer week;
}
