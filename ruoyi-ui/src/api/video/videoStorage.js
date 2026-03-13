import request from '@/utils/request'

// 查询视频资源信息列表
export function listVideoStorage(query) {
  return request({
    url: '/system/video/storage/list',
    method: 'get',
    params: query
  })
}

// 查询视频资源信息详细
export function getVideoStorage(id) {
  return request({
    url: '/system/video/storage/' + id,
    method: 'get'
  })
}

// 新增视频资源信息
export function addVideoStorage(data) {
  return request({
    url: '/system/video/storage',
    method: 'post',
    data: data,
    headers:{
      'Content-Type':'multipart/form-data'
    }
  })
}

// 修改视频资源信息
export function updateVideoStorage(data) {
  return request({
    url: '/system/video/storage',
    method: 'put',
    data: data,
    headers:{
      'Content-Type':'multipart/form-data'
    }
  })
}

// 删除视频资源信息
export function delVideoStorage(id) {
  return request({
    url: '/system/video/storage/' + id,
    method: 'delete'
  })
}
