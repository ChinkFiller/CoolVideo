package com.ruoyi.video.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.*;


/**
 * 轮播图实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Banner extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**轮播图**/
    String cover;

    /**轮播图id**/
    Integer id;

    /**轮播图标题**/
    String title;

    /**轮播图跳转链接**/
    String url;

    /**轮播图视频id**/
    Integer vid;
}
