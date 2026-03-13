import request from '@/utils/request'

// 查询视频数据保存的数据列表
export function listData(query) {
  return request({
    url: '/system/video/film/list',
    method: 'get',
    params: query
  })
}

// 查询视频数据保存的数据详细
export function getData(id) {
  return request({
    url: '/system/video/film/' + id,
    method: 'get'
  })
}

// 新增视频数据保存的数据
export function addData(data) {
  return request({
    url: '/system/video/film',
    method: 'post',
    data: data
  })
}

// 修改视频数据保存的数据
export function updateData(data) {
  return request({
    url: '/system/video/film',
    method: 'put',
    data: data
  })
}

// 删除视频数据保存的数据
export function delData(id) {
  return request({
    url: '/system/video/film/' + id,
    method: 'delete'
  })
}

export function updateConfig(config){
  return request({
    url: '/system/video/film/update',
    method: 'post',
    data: config
    })
}

export function getUpdateConfig(){
  return request({
    url: '/system/video/film/update',
    method: 'get'
  })
}

export function runUpdater(){
  return request({
    url: '/system/video/film/update/run',
    method: 'get'
  })
}

export function getTotalInfo(){
  return request({
    url: '/system/video/film/total/info',
    method: 'get'
  })
}

export function lockInfo(id){
  return request({
    url: '/system/video/film/lock/' + id,
    method: 'get'
  })
}

export function unlockInfo(id){
  return request({
    url: '/system/video/film/unlock/' + id,
    method: 'get'
  })
}





