import request from '@/utils/request'

// 查询更新器日志列表
export function getParserList() {
  return request({
    url: '/system/parser/mode/list',
    method: 'get'
  })
}

// 查询更新器日志详细
export function updateParserStatus(data) {
  return request({
    url: '/system/parser/mode/',
    method: 'put',
    data: data
  })
}
