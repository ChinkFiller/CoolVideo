package com.ruoyi.video.admin.domain.form;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemCommonSettingForm {
    Boolean captchaEnabled;
    Boolean registerEnabled;
}
