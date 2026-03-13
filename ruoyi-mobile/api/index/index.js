/**
 * 主页相关API
 */
import request from '@/utils/request';


/**
 * 获取主页配置信息
 * @returns {Promise} - 返回Promise对象，包含banner、latest、hots和perweek数据
 */
export function getHomeConfig() {
  return request.get('/index/config');
}

/**
 * 获取周期表数据
 * 返回形如 {"0": []} 的对象，key 为周索引（周一=0，周日=6）
 */
export function getWeekTable() {
  return request.get('/index/weekTable')
}

