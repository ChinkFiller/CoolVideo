import request from '@/utils/request'

// 发送邮箱验证码
export function sendEmailCode(username, sign,code,uuid) {
  return request.post('/email/verify-code', { username, sign , code, uuid})
}

// 注册
export function registerUser(data) {
  return request.post('/register', data)
}

// 找回密码
export function findPassword(username) {
  return request.get('/find-password', { username })
}

export function canRegister() {
  return request.get('/can-register')
}
