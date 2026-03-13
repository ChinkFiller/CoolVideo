import request from '@/utils/request'

// 查询视频周期列表
export function listWeek(query) {
  return request({
    url: '/system/video/week/list',
    method: 'get',
    params: query
  })
}

// 查询视频周期详细
export function getWeek(id) {
  return request({
    url: '/system/video/week/' + id,
    method: 'get'
  })
}

// 新增视频周期
export function addWeek(data) {
  return request({
    url: '/system/video/week',
    method: 'post',
    data: data
  })
}

// 修改视频周期
export function updateWeek(data) {
  return request({
    url: '/system/video/week',
    method: 'put',
    data: data
  })
}

// 删除视频周期
export function delWeek(id) {
  return request({
    url: '/system/video/week/' + id,
    method: 'delete'
  })
}
