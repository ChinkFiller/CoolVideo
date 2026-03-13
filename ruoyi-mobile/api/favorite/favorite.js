/**
 * 收藏相关API
 */
import request from '@/utils/request';

/**
 * 获取用户收藏列表
 * @returns {Promise} - 返回Promise对象，包含收藏列表数据
 * @param pageNum - 页码
 * @param pageSize - 每页大小
 * @param keyWord - 搜索关键词
 */
export function getUserFavorites(pageNum, pageSize,keyWord) {
    return request.get('/video/user/subscribe', { pageNum, pageSize ,keyWord});
}

/**
 * 删除用户收藏
 * @param {Number} id - 收藏ID
 * @returns {Promise} - 返回Promise对象
 */
export function deleteFavorite(id) {
    return request.get('/video/subscribe/' + id);
}
