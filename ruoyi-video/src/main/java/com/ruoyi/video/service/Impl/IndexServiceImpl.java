package com.ruoyi.video.service.Impl;


import com.ruoyi.video.domain.vo.IndexInfoVo;
import com.ruoyi.video.mapper.IndexMapper;
import com.ruoyi.video.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    IndexMapper indexMapper;


    @Override
    @Cacheable(value = "indexConfigCache",key = "'indexInfo'" ,sync = true)
    public IndexInfoVo getIndexInfo() {
        return IndexInfoVo.builder()
                .banner(indexMapper.getBanner())
                .hots(indexMapper.getHots())
                .latest(indexMapper.getLast())
                .fastFunction(indexMapper.getFastFunction())
                .indexNotice(indexMapper.getIndexNotice())
                .popNotice(indexMapper.getPopNotice())
                .build();
    }
}
