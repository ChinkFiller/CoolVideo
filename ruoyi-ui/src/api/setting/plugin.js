import request from '@/utils/request'

// 查询插件列表
export function listPlugins(query) {
  return request({
    url: '/system/plugin/list',
    method: 'get',
    params: query
  })
}

// 通过文件安装插件
export function installPluginByFile(formData) {
  return request({
    url: '/system/plugin/install',
    method: 'post',
    data: formData, // formData 里要包含 file
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 通过远程链接安装插件
export function installPluginByUrl(url) {
  return request({
    url: '/system/plugin/install',
    method: 'get',
    params: { url }
  })
}

// 卸载插件
export function uninstallPlugin(pluginId) {
  return request({
    url: `/system/plugin/uninstall/${pluginId}`,
    method: 'get'
  })
}

// 停用插件
export function stopPlugin(pluginId) {
  return request({
    url: `/system/plugin/stop/${pluginId}`,
    method: 'get'
  })
}

// 启用插件
export function startPlugin(pluginId) {
  return request({
    url: `/system/plugin/start/${pluginId}`,
    method: 'get'
  })
}

// 查询插件详情
export function getPluginDetail(pluginId) {
  return request({
    url: `/system/plugin/detail/${pluginId}`,
    method: 'get'
  })
}

// 查询插件设置
export function getPluginSetting(pluginId) {
  return request({
    url: `/system/plugin/detail/setting/${pluginId}`,
    method: 'get'
  })
}

// 更新插件设置
export function updatePluginSetting(pluginId, data) {
  return request({
    url: `/system/plugin/update/setting/${pluginId}`,
    method: 'put',
    data: data
  })
}
