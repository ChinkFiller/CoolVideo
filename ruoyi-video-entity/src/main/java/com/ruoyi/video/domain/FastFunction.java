package com.ruoyi.video.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.*;

import java.io.Serializable;

/**
 * 主页快速功能对象 fast_functions
 *
 * @author ruoyi
 * @date 2025-08-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FastFunction extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** id */
    private Long fid;

    /** 标题 */
    private String title;

    /** 跳转链接 */
    private String href;

    /** 图标地址 */
    private String img;
}
