import request from '@/utils/request'

// 查询更新器日志列表
export function listLog(query) {
  return request({
    url: '/system/video/updater/log/list',
    method: 'get',
    params: query
  })
}

// 查询更新器日志详细
export function getLog(id) {
  return request({
    url: '/system/video/updater/log/' + id,
    method: 'get'
  })
}

// 新增更新器日志
export function addLog(data) {
  return request({
    url: '/system/video/updater/log',
    method: 'post',
    data: data
  })
}

// 修改更新器日志
export function updateLog(data) {
  return request({
    url: '/system/video/updater/log',
    method: 'put',
    data: data
  })
}

// 删除更新器日志
export function delLog(id) {
  return request({
    url: '/system/video/updater/log/' + id,
    method: 'delete'
  })
}
export function clearUpdaterLog(id) {
  return request({
    url: '/system/video/updater/log/all',
    method: 'delete'
  })
}

