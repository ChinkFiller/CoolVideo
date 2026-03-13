import {checkVersion} from "../api/app/version";
import md5 from "md5";
import {settingFuncs} from "../constant/settingFuncs";

/**
 * 更新应用程序函数
 * @param {string} url - 用于更新应用的URL地址
 */
function updateApp(url){
    // 尝试通过plus.runtime.openURL打开指定URL
    // 如果打开失败，则执行回调函数
    plus.runtime.openURL(url, ()=>{
        // 将URL地址复制到剪贴板
        uni.setClipboardData({
            data:url
        })
        // 显示提示消息，告知用户手动访问URL进行下载
        uni.showToast({
            title: '打开浏览器失败，请手动访问'+url+"进行下载",
            icon: 'none'
        })
    })
}

export function getNoticeHash(noticeList){
    // 对数组进行深度排序，确保对象键顺序一致
    const sortedJson = JSON.stringify(noticeList, function (key, value) {
        if (value && typeof value === 'object' && !Array.isArray(value)) {
            return Object.keys(value)
                .sort()
                .reduce((result, key) => {
                    result[key] = value[key];
                    return result;
                }, {});
        }
        return value;
    });
    return md5(sortedJson)
}

export function checkClientVersion(success=()=>{}){
    //获取当前的运行平台
    const plt=uni.getSystemInfoSync().platform
    const appID=plus.runtime.appid
    //获取当前应用的版本号
    plus.runtime.getProperty(appID, (wgtInfo) => {
        checkVersion(wgtInfo.versionCode,plt,appID).then(res=>{
            const config=res.data.data

            if (config.hasUpdate){
                if (config.ignorable){
                    uni.showModal({
                        title: '发现新版本 V'+config.lastVersion/100,
                        content: config.newVersionDesc,
                        success: function (res) {
                            if (res.confirm) {
                                updateApp(config.updateUrl)
                            } else if (res.cancel) {
                                plus.navigator.closeSplashscreen();
                                success()
                            }
                        }
                    });
                }else{
                    uni.showToast({
                        showCancel: false,
                        title:"提示",
                        content: '当前版本过低,请立即更新',
                        success: function (res) {
                            updateApp(config.updateUrl)
                            // 打开浏览器后，退出应用
                            setTimeout(()=>{
                                plus.runtime.quit()
                            },2000)
                        }
                    })
                }
            }else{
                plus.navigator.closeSplashscreen();
                success()
            }
        }).catch(e=>{
            // 退出App的代码
            if (e.code===-600001){
                uni.showToast({
                    title: e.message,
                    icon: 'none'
                })
                setTimeout(()=>{
                    //如果应用被停用，退出应用
                    plus.runtime.quit()
                },1500)
            }else{
                // 非退出代码
                uni.showToast({
                    title: e.message,
                    icon: 'none'
                })
            }
        })
    });
}


export function calcScrollHeight(subtractHeight=0,includeHeader=true){
    const sysInfo=uni.getSystemInfoSync()
    const headerHeight=includeHeader?sysInfo.statusBarHeight:0
    return sysInfo.windowHeight-headerHeight-subtractHeight
}

export function getSettingDetail(key){
    return settingFuncs[key]
}
