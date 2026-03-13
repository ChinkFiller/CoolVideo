import request from '@/utils/request'

// 查询评论管理列表
export function listComments(query) {
  return request({
    url: '/system/video/comments/list',
    method: 'get',
    params: query
  })
}

// 查询评论管理详细
export function getComments(id) {
  return request({
    url: '/system/video/comments/' + id,
    method: 'get'
  })
}

// 修改评论管理
export function setProhibitConfig(data) {
  return request({
    url: '/system/video/comments',
    method: 'put',
    data: data
  })
}

// 修改评论管理
export function getProhibitConfig() {
  return request({
    url: '/system/video/comments/config',
    method: 'get'
  })
}

// 删除评论管理
export function delComments(id) {
  return request({
    url: '/system/video/comments/' + id,
    method: 'delete'
  })
}
