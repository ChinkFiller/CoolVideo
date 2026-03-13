package com.ruoyi.video.admin.domain.form;


import lombok.Data;

@Data
public class UpdateConfig {
    //自动更新的开关
    Boolean autoUpdate;
    //自动更新的间隔。单位小时
    Integer updateInterval;
}
