/**
 * 收藏相关API
 */
import request from '@/utils/request';

/**
 * 获取用户收藏列表
 * @returns {Promise} - 返回Promise对象，包含收藏列表数据
 * @param pageNum 页码
 * @param pageSize 每页数量
 * @param year 年份
 * @param order 排序方式
 * @param letter 搜索关键字
 * @param type 类型
 */
export function getFilterData(pageNum, pageSize,year,order,letter,type) {
    return request.get('/video/filter', { pageNum, pageSize , year,order,letter,type });
}
