package com.ruoyi.video.admin.service.impl;

import com.ruoyi.video.admin.mapper.BannerManagementMapper;
import com.ruoyi.video.admin.service.BannerManagementService;
import com.ruoyi.video.domain.Banner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BannerManagementServiceImpl implements BannerManagementService {

    @Autowired
    private BannerManagementMapper bannerManagementMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public Banner selectBannerById(Long id)
    {
        return bannerManagementMapper.selectBannerById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param banner 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<Banner> selectBannerList(Banner banner)
    {
        return bannerManagementMapper.selectBannerList(banner);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param banner 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBanner(Banner banner)
    {
        return bannerManagementMapper.insertBanner(banner);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param banner 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBanner(Banner banner)
    {
        return bannerManagementMapper.updateBanner(banner);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteBannerByIds(Long[] ids)
    {
        return bannerManagementMapper.deleteBannerByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteBannerById(Long id)
    {
        return bannerManagementMapper.deleteBannerById(id);
    }
}
