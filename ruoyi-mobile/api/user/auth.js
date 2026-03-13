import request from '@/utils/request'

// 获取验证码
export function getCaptchaImage() {
  return request.get('/captchaImage')
}

export function getRegisterCaptchaImage() {
  return request.get('/register/captcha')
}

// 登录
export function login(data) {
  return request.post('/login', data)
}
