package com.ruoyi.video.domain.vo;


import lombok.Data;

import java.io.Serializable;

@Data
public class UserPublicInfoVo implements Serializable {
    String nickName;
    Integer userId;
    Integer avatar;
    Integer vip;
}
