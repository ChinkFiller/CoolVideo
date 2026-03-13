/**
 * 播放器相关API
 */
import request from '@/utils/request';

/**
 * 获取视频播放信息
 * @param {number} id - 视频ID
 * @returns {Promise} - 返回Promise对象
 */
export function getVideoInfo(id) {
  return request.get(`/video/watch/${id}`);
}

/**
 * 获取视频评论列表
 * @param {number} id - 视频ID
 * @returns {Promise} - 返回Promise对象
 */
export function getCommentList(id) {
  return request.get(`/comment/list/${id}`);
}

/**
 * 添加评论
 * @param {Object} data - 评论数据
 * @param {string} data.content - 评论内容
 * @param {number} data.vid - 视频ID
 * @returns {Promise} - 返回Promise对象
 */
export function addComment(data) {
  return request.post('/comment/add', data);
}

/**
 * 点赞评论
 * @param {number} id - 评论ID
 * @returns {Promise} - 返回Promise对象
 */
export function likeComment(id) {
  return request.get(`/comment/agree/${id}`);
}


export function testPlayUrl(url){
  return request.get(url);
}
