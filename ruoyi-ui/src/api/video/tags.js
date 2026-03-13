import request from '@/utils/request'

// 查询视频周期列表
export function listTag(query) {
  return request({
    url: '/system/video/tag/list',
    method: 'get',
    params: query
  })
}

// 查询视频周期详细
export function getTag(id) {
  return request({
    url: '/system/video/tag/' + id,
    method: 'get'
  })
}

// 新增视频周期
export function addTag(data) {
  return request({
    url: '/system/video/tag',
    method: 'post',
    data: data
  })
}

// 修改视频周期
export function updateTag(data) {
  return request({
    url: '/system/video/tag',
    method: 'put',
    data: data
  })
}

// 删除视频周期
export function delTag(id) {
  return request({
    url: '/system/video/tag/' + id,
    method: 'delete'
  })
}
