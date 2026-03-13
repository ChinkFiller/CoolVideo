import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listBanner(query) {
  return request({
    url: '/system/video/banner/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getBanner(id) {
  return request({
    url: '/system/video/banner/' + id,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addBanner(data) {
  return request({
    url: '/system/video/banner',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateBanner(data) {
  return request({
    url: '/system/video/banner',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delBanner(id) {
  return request({
    url: '/system/video/banner/' + id,
    method: 'delete'
  })
}
