/**
 * 图片工具函数
 */

import {getServer} from "./server";

/**
 * 处理图片URL，将直链转换为代理接口格式
 * @param {string} url - 原始图片URL
 * @param {boolean} useProxy - 是否使用代理，默认为true
 * @returns {string} - 处理后的URL
 */


export function processImageUrl(url, useProxy = true) {
  const imgServer=getServer()
  // 如果URL为空或不使用代理，则直接返回原URL或默认图片
  if (!url) {
    return '../../static/loadImgError.png';
  }

  // 如果不使用代理或已经是代理URL，则直接返回
  if (!useProxy || url.startsWith(imgServer+'/img/cover')) {
    return url;
  }

  //使用本地图片的情况
  if (!url.startsWith('http')&&!url.startsWith('https')) {
    return imgServer+url;
  }


  // 将URL转换为代理格式
  return `${imgServer}/img/cover?url=${encodeURIComponent(url)}`;
}

export default {
  processImageUrl
};
