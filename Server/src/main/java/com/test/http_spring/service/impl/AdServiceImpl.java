package com.test.http_spring.service.impl;

import com.test.http_spring.mapper.adMapper;
import com.test.http_spring.pojo.Banner;
import com.test.http_spring.pojo.FastFunction;
import com.test.http_spring.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdServiceImpl implements AdService {
    @Autowired
    private adMapper adMapper;
    @Override
    public List<Banner> getAllBannerData() {
        return adMapper.getAllBannerAd();
    }

    @Override
    public List<FastFunction> getAllFastFunctionSetting() {
        return adMapper.getAllFastFunctionSetting();
    }

    @Override
    public void removeBanner(int bid) {
        adMapper.removeBanner(bid);
    }

    @Override
    public void addBanner(Banner data) {
        adMapper.addBanner(data);
    }

    @Override
    public void updateBanner(Banner data) {
        adMapper.updateBanner(data);
    }

    @Override
    public void removeFastFunction(int fid) {
        adMapper.removeFastFunction(fid);
    }

    @Override
    public void addFastFunction(FastFunction data) {
        adMapper.addFastFunction(data);
    }

    @Override
    public void updateFastFunction(FastFunction data) {
        adMapper.updateFastFunction(data);
    }
}
