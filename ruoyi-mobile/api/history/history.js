/**
 * 历史记录相关API
 */
import request from '@/utils/request';

/**
 * 同步历史记录
 * @param {Object} data - 历史记录数据
 * @returns {Promise} - 返回Promise对象
 */
export function syncHistory(data) {
    return request.post('/history/update', data);
}

export function flushHistory(vid) {
    return request.get('/history/flush/'+vid);
}

/**
 * 获取用户历史记录列表
 * @returns {Promise} - 返回Promise对象，包含历史记录列表数据
 * @param pageNum - 页码
 * @param pageSize - 每页数量
 * @param keyWord - 关键词
 */
export function getUserHistory(pageNum, pageSize,keyWord) {
    return request.get('/history/list', {pageNum, pageSize, keyWord});
}

/**
 * 删除历史记录
 * @param {Number} id - 历史记录ID
 * @returns {Promise} - 返回Promise对象
 */
export function deleteHistory(id) {
    return request.delete('/history/delete/' + id);
}
