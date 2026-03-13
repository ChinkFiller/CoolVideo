package com.ruoyi.video.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppConfigVo {
    /**最新版本**/
    Long lastVersion;

    /**运行平台**/
    String plt;

    /**更新地址**/
    String UpdateUrl;

    /**是否需要更新**/
    Boolean hasUpdate;

    /**是否可忽略当前版本**/
    Boolean ignorable;

    /**新版本描述**/
    String newVersionDesc;
}
