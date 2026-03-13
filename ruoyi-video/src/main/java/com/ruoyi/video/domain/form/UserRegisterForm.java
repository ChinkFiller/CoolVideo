package com.ruoyi.video.domain.form;

import com.ruoyi.common.core.domain.entity.SysUser;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserRegisterForm {
    @NotBlank
    SysUser userData;
    @NotBlank
    String verifyCode;
    @NotBlank
    String uuid;
    @NotBlank
    String sign;
}
