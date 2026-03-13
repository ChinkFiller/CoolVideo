import request from "@/utils/request";

export function getVideoDict(){
  return request({
    url: '/system/dict/video',
    method: 'get'
  })
}

export function getUserDict(){
  return request({
    url: '/system/dict/user',
    method: 'get'
  })
}
