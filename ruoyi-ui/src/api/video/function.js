import request from '@/utils/request'

// 查询主页快速功能列表
export function listFunction(query) {
  return request({
    url: '/system/video/function/list',
    method: 'get',
    params: query
  })
}

// 查询主页快速功能详细
export function getFunction(fid) {
  return request({
    url: '/system/video/function/' + fid,
    method: 'get'
  })
}

// 新增主页快速功能
export function addFunction(data) {
  return request({
    url: '/system/video/function',
    method: 'post',
    data: data
  })
}

// 修改主页快速功能
export function updateFunction(data) {
  return request({
    url: '/system/video/function',
    method: 'put',
    data: data
  })
}

// 删除主页快速功能
export function delFunction(fid) {
  return request({
    url: '/system/video/function/' + fid,
    method: 'delete'
  })
}
