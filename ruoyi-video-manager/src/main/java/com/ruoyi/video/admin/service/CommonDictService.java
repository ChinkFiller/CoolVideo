package com.ruoyi.video.admin.service;

import java.util.List;
import java.util.Map;

public interface CommonDictService {
    List<Map<String,Object>> selectVideoInfoDict();

    List<Map<String,Object>> selectUserInfoDict();
}
