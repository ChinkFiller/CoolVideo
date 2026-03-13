package com.ruoyi.video.admin.mapper;

import com.ruoyi.video.domain.Banner;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface BannerManagementMapper {
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public Banner selectBannerById(Long id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param banner 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<Banner> selectBannerList(Banner banner);

    /**
     * 新增【请填写功能名称】
     *
     * @param banner 【请填写功能名称】
     * @return 结果
     */
    public int insertBanner(Banner banner);

    /**
     * 修改【请填写功能名称】
     *
     * @param banner 【请填写功能名称】
     * @return 结果
     */
    public int updateBanner(Banner banner);

    /**
     * 删除【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteBannerById(Long id);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBannerByIds(Long[] ids);
}
