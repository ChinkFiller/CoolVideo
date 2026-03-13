package com.ruoyi.video.admin.service.impl;

import com.ruoyi.video.admin.mapper.FastFunctionManagementMapper;
import com.ruoyi.video.admin.service.FastFunctionManagementService;
import com.ruoyi.video.domain.FastFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 主页快速功能Service业务层处理
 *
 * @author ruoyi
 * @date 2025-08-07
 */
@Service
public class FastFunctionManagementServiceImpl implements FastFunctionManagementService
{
    @Autowired
    private FastFunctionManagementMapper fastFunctionMapper;

    /**
     * 查询主页快速功能
     *
     * @param fid 主页快速功能主键
     * @return 主页快速功能
     */
    @Override
    public FastFunction selectFastFunctionByFid(Long fid)
    {
        return fastFunctionMapper.selectFastFunctionByFid(fid);
    }

    /**
     * 查询主页快速功能列表
     *
     * @param fastFunction 主页快速功能
     * @return 主页快速功能
     */
    @Override
    public List<FastFunction> selectFastFunctionList(FastFunction fastFunction)
    {
        return fastFunctionMapper.selectFastFunctionList(fastFunction);
    }

    /**
     * 新增主页快速功能
     *
     * @param fastFunction 主页快速功能
     * @return 结果
     */
    @Override
    public int insertFastFunction(FastFunction fastFunction)
    {
        return fastFunctionMapper.insertFastFunction(fastFunction);
    }

    /**
     * 修改主页快速功能
     *
     * @param fastFunction 主页快速功能
     * @return 结果
     */
    @Override
    public int updateFastFunction(FastFunction fastFunction)
    {
        return fastFunctionMapper.updateFastFunction(fastFunction);
    }

    /**
     * 批量删除主页快速功能
     *
     * @param fids 需要删除的主页快速功能主键
     * @return 结果
     */
    @Override
    public int deleteFastFunctionByFids(Long[] fids)
    {
        return fastFunctionMapper.deleteFastFunctionByFids(fids);
    }

    /**
     * 删除主页快速功能信息
     *
     * @param fid 主页快速功能主键
     * @return 结果
     */
    @Override
    public int deleteFastFunctionByFid(Long fid)
    {
        return fastFunctionMapper.deleteFastFunctionByFid(fid);
    }
}
