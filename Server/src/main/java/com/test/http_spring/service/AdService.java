package com.test.http_spring.service;

import com.test.http_spring.pojo.Banner;
import com.test.http_spring.pojo.FastFunction;

import java.util.List;

public interface AdService {
    List<Banner> getAllBannerData();
    List<FastFunction> getAllFastFunctionSetting();
    void removeBanner(int bid);
    void addBanner(Banner data);
    void updateBanner(Banner data);
    void  removeFastFunction(int fid);
    void  addFastFunction(FastFunction data);
    void  updateFastFunction(FastFunction data);
}
