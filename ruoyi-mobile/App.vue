<script>
import {lightTheme} from "./constant/themeConstant";
import {checkClientVersion} from "./utils/clientUtils";

export default {
	onLaunch: function() {
		console.log('App Launch')


    //初始化默认服务器地址
    if (uni.getStorageSync("SERVER_URL")=== "") {
      uni.setStorageSync("SERVER_URL",
          "http://112.124.25.184/video-api"
      );
    }

    //初始化弹窗提示标识
    if (uni.getStorageSync("noticeHash")=== "") {
      uni.setStorageSync("noticeHash", "");
    }

    //初始化请求日志
    if (uni.getStorageSync('REQUEST_LOG')===''){
      uni.setStorageSync('REQUEST_LOG',[])
    }

    //初始化默认主题
    if (uni.getStorageSync('theme')==='') {
      //设置默认亮色主题
      uni.setStorageSync('theme',lightTheme)
    }

    //对版本进行检查更新
    checkClientVersion()

    //5秒后无响应就进入App
    setTimeout(()=>{
      plus.navigator.closeSplashscreen();
    },5000)

	},
	onShow: function() {
    console.log('App Show')
	},
	onHide: function() {
		console.log('App Hide')
	},
  globalData:{
    nowIndex:'home'
  }
}
</script>

<style>
	@import "@/style/global.scss";
	@import "@/style/animation.scss";

  @import "/iconfont/iconfont.css";
</style>
