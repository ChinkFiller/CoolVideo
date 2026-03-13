package com.ruoyi.common.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterVerifyCodeBody {
    @NonNull
    String username;
    @NonNull
    String sign;
    @NonNull
    String uuid;
    @NonNull
    String code;
}
