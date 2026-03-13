import request from '@/utils/request'

// 获取用户个人信息
export function getUserProfile() {
  return request.get('/system/user/profile')
}

// 修改用户个人信息
export function updateUserProfile(data) {
  return request.put('/system/user/profile',data)
}

export function updateUserPassword(data) {
  return request.put('/system/user/profile/updatePwd',data)
}
