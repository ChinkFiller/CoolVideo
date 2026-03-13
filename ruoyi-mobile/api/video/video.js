import request from "../../utils/request";


/**
 * 搜索视频
 * @param {string} key - 搜索关键词
 * @param pageNum - 页码
 * @param pageSize - 每页数量
 * @returns {Promise} - 返回Promise对象
 */
export function searchVideos(pageNum,pageSize,key) {
    return request.get('/video/search', { pageNum,pageSize,key });
}

/**
 * 获取推荐视频
 * @returns {Promise} - 返回Promise对象
 */
export function getRecommendVideos() {
    return request.get('/video/recommend');
}

/**
 * 获取热门视频
 * @returns {Promise} - 返回Promise对象
 */
export function getHotVideos() {
    return request.get('/video/hot');
}

/**
 * 获取视频详情
 * @param {number} id - 视频ID
 * @returns {Promise} - 返回Promise对象
 */
export function getVideoDetail(id) {
    return request.get(`/video/detail/${id}`);
}

/**
 * 点赞视频
 * @param {number} id - 视频ID
 * @returns {Promise} - 返回Promise对象
 */
export function likeVideo(id) {
    return request.get(`/video/like/${id}`);
}

/**
 * 收藏视频
 * @param {number} id - 视频ID
 * @returns {Promise} - 返回Promise对象
 */
export function subscribeVideo(id) {
    return request.get(`/video/subscribe/${id}`);
}
