package com.ruoyi.video.admin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemCommonSettingVo {
    // 是否启用登录验证码
    Boolean captchaEnabled;
    // 是否启用注册功能
    Boolean registerEnabled;
    // 缓存的图片数量
    Integer cacheImgNum;
    // 缓存的图片所占空间
    Long cacheImgSize;
}
