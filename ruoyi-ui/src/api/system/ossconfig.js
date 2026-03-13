import request from '@/utils/request'

// 查询对象存储配置列表
export function listOssconfig(query) {
  return request({
    url: '/system/ossconfig/list',
    method: 'get',
    params: query
  })
}

// 查询对象存储配置详细
export function getOssconfig(ossConfigId) {
  return request({
    url: '/system/ossconfig/' + ossConfigId,
    method: 'get'
  })
}

// 新增对象存储配置
export function addOssconfig(data) {
  return request({
    url: '/system/ossconfig',
    method: 'post',
    data: data
  })
}

// 修改对象存储配置
export function updateOssconfig(data) {
  return request({
    url: '/system/ossconfig',
    method: 'put',
    data: data
  })
}

// 删除对象存储配置
export function delOssconfig(ossConfigId) {
  return request({
    url: '/system/ossconfig/' + ossConfigId,
    method: 'delete'
  })
}


// 测试对象存储配置
export function testOssconfig(ossConfigId) {
  return request({
    url: '/system/ossconfig/test/' + ossConfigId,
    method: 'get'
  })
}
export function updateStatus(data) {
  return request({
    url: '/system/ossconfig/updateStatus',
    method: 'put',
    data:data
  })
}
