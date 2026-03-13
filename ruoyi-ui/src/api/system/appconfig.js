import request from '@/utils/request'

// 查询App端配置信息管理列表
export function listAppconfig(query) {
  return request({
    url: '/system/appconfig/list',
    method: 'get',
    params: query
  })
}

// 查询App端配置信息管理详细
export function getAppconfig(id) {
  return request({
    url: '/system/appconfig/' + id,
    method: 'get'
  })
}

// 新增App端配置信息管理
export function addAppconfig(data) {
  return request({
    url: '/system/appconfig',
    method: 'post',
    data: data
  })
}

// 修改App端配置信息管理
export function updateAppconfig(data) {
  return request({
    url: '/system/appconfig',
    method: 'put',
    data: data
  })
}

// 删除App端配置信息管理
export function delAppconfig(id) {
  return request({
    url: '/system/appconfig/' + id,
    method: 'delete'
  })
}
