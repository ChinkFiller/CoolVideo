/**
 * 请求封装模块
 */

import {userStore} from '@/store/user'
import {getServer} from "./server";
import {saveRequest} from "./devUtils";


// 请求超时时间
const TIMEOUT = 10000;

/**
 * 封装的请求方法
 * @param {Object} options - 请求配置
 * @param {string} options.url - 请求路径
 * @param {string} options.method - 请求方法，默认GET
 * @param {Object} options.data - 请求数据
 * @param {Object} options.params - URL参数
 * @param {Object} options.header - 请求头
 * @param {number} options.timeout - 超时时间
 * @returns {Promise} - 返回Promise对象
 */
const request = (options = {}) => {
  return new Promise((resolve, reject) => {
    // 构建完整URL
    let url = options.url;
    if (!url.startsWith('http')) {
      url = getServer() + url;
    }

    // 处理URL参数
    if (options.params) {
      const queryString = Object.keys(options.params)
        .filter(key => options.params[key] !== undefined && options.params[key] !== null)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(options.params[key])}`)
        .join('&');

      if (queryString) {
        url += (url.includes('?') ? '&' : '?') + queryString;
      }
    }

    // 发起请求
    uni.request({
      url,
      data: options.data,
      method: options.method || 'GET',
      header: {
        'Content-Type': 'application/json',
        'Authorization': userStore().token,

        ...options.header
      },
      timeout: options.timeout || TIMEOUT,
      success: (res) => {
        saveRequest(Object.assign(options,{response:{
          code:res.statusCode,
          header:res.header,
          data:JSON.stringify(res.data).substring(0,50)
        }}))
        // 请求成功，但需要判断业务状态码
        if (res.statusCode >= 200 && res.statusCode < 300) {
          // 判断自定义错误码
          if (res.data.error === 0 || res.data.code===200 ) {
            resolve(res);
          } else {
            // 自定义错误码异常
            if (res.data.code === 401) {
              // 处理未授权错误
              uni.showToast({
                title: '登录过期，请重新登录',
                icon: 'none'
              });
              userStore().clearUserLoginState()
              // 跳转到登录页
              uni.redirectTo({
                url: '/pages/user/login'
              });
            }
            // 处理业务错误
            const error = new Error(res.data.msg || '请求失败');
            error.code = res.data.code;
            error.response = res;
            reject(error);
          }
        } else {
          // 处理HTTP错误
          const error = new Error(`请求失败，状态码: ${res.statusCode}`);
          error.response = res;
          reject(error);
        }
      },
      fail: (err) => {
        saveRequest(Object.assign(options,{response:err}))
        // 请求失败
        const error = new Error('网络请求失败');
        error.error = err;
        reject(error);
      }
    });
  });
};

/**
 * GET请求
 * @param {string} url - 请求路径
 * @param {Object} params - URL参数
 * @param {Object} options - 其他配置
 * @returns {Promise}
 */
const get = (url, params = {}, options = {}) => {
  return request({
    url,
    method: 'GET',
    params,
    ...options
  });
};

/**
 * POST请求
 * @param {string} url - 请求路径
 * @param {Object} data - 请求数据
 * @param {Object} options - 其他配置
 * @returns {Promise}
 */
const post = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'POST',
    data,
    ...options
  });
};

/**
 * PUT请求
 * @param {string} url - 请求路径
 * @param {Object} data - 请求数据
 * @param {Object} options - 其他配置
 * @returns {Promise}
 */
const put = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'PUT',
    data,
    ...options
  });
};

/**
 * DELETE请求
 * @param {string} url - 请求路径
 * @param {Object} params - URL参数
 * @param {Object} options - 其他配置
 * @returns {Promise}
 */
const del = (url, params = {}, options = {}) => {
  return request({
    url,
    method: 'DELETE',
    params,
    ...options
  });
};

export default {
  request,
  get,
  post,
  put,
  delete: del
};
