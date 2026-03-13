package com.ruoyi.video.admin.mapper;

import com.ruoyi.video.domain.FastFunction;

import java.util.List;

/**
 * 主页快速功能Mapper接口
 *
 * @author ruoyi
 * @date 2025-08-07
 */
public interface FastFunctionManagementMapper
{
    /**
     * 查询主页快速功能
     *
     * @param fid 主页快速功能主键
     * @return 主页快速功能
     */
    public FastFunction selectFastFunctionByFid(Long fid);

    /**
     * 查询主页快速功能列表
     *
     * @param fastFunction 主页快速功能
     * @return 主页快速功能集合
     */
    public List<FastFunction> selectFastFunctionList(FastFunction fastFunction);

    /**
     * 新增主页快速功能
     *
     * @param fastFunction 主页快速功能
     * @return 结果
     */
    public int insertFastFunction(FastFunction fastFunction);

    /**
     * 修改主页快速功能
     *
     * @param fastFunction 主页快速功能
     * @return 结果
     */
    public int updateFastFunction(FastFunction fastFunction);

    /**
     * 删除主页快速功能
     *
     * @param fid 主页快速功能主键
     * @return 结果
     */
    public int deleteFastFunctionByFid(Long fid);

    /**
     * 批量删除主页快速功能
     *
     * @param fids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFastFunctionByFids(Long[] fids);
}
