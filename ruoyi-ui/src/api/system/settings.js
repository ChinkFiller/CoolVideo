import request from '@/utils/request'

// 查询设置列表
export function getSystemSettings() {
  return request({
    url: '/system/common/settings/config',
    method: 'get'
  })
}

// 登录验证码开关
export function submitSystemSetting(data) {
  return request({
    url: '/system/common/settings/submit',
    method: 'post',
    data:data
  })
}

// 清除图片缓存
export function clearImgCache() {
  return request({
    url: '/system/common/settings/cache/img',
    method: 'delete'
  })
}
