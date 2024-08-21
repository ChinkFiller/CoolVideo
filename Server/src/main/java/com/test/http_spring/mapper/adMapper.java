package com.test.http_spring.mapper;


import com.test.http_spring.pojo.Banner;
import com.test.http_spring.pojo.FastFunction;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface adMapper {
    List<Banner> getAllBannerAd();
    List<FastFunction> getAllFastFunctionSetting();
    void removeBanner(int bid);
    void addBanner(Banner data);
    void updateBanner(Banner data);
    void  removeFastFunction(int fid);
    void  addFastFunction(FastFunction data);
    void  updateFastFunction(FastFunction data);
}
