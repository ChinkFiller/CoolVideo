<template>
  <base-page :show-nav="false">
    <view class="user-center-container" :class="{'dark-mode': isDarkMode}">

      <view class="header-section" :style="headerStyle">
        <view class="gradient-overlay"></view>
        <view class="settings-bar" style="color: #f5f5f5;" @click.stop>
          <text class="mode-toggle-btn iconfont icon-liangsemoshi" @click="toggleMode" v-if="isDarkMode"></text>
          <text class="mode-toggle-btn iconfont icon-qiehuan-ansemoshi" @click="toggleMode" v-else></text>
        </view>

        <view class="user-info-wrapper" @click.stop>
          <view class="avatar-box" @click="uploadAvatar">
            <image class="avatar" :src="userInfo.avatar" mode="aspectFill"></image>
          </view>

          <view class="text-info">
            <text class="username">{{ userInfo.nickName }}</text>
            <view class="vip-tag">
              <text class="tag-text">{{ userInfo.roleGroup }}</text>
            </view>
          </view>
        </view>
      </view>

      <view class="vip-bar-card" :style="{ backgroundImage: 'url(../../static/vip-bar.png)' }">
        <view class="vip-content">
          <view class="vip-text-group">
            <text class="vip-title">加入会员享更多功能</text>
            <text class="vip-sub">续费享更多权益</text>
          </view>
          <view class="vip-btn" @click="goToVipPage">
            <text>会员中心 </text>
            <wd-icon name="arrow-right"></wd-icon>
          </view>
        </view>
      </view>

      <view class="function-grid">
        <view class="grid-item" v-for="item in functionList" :key="item.name" @click="item.func">
          <wd-icon  class="grid-icon" :name="item.icon"></wd-icon>
          <text class="grid-text">{{ item.name }}</text>
        </view>
      </view>

      <view class="menu-list-section">
        <text class="section-header">更多功能</text>
        <view class="menu-box">
          <view class="menu-item" v-for="item in moreServices" :key="item.name" @click="item.func">
            <view class="menu-left">
              <wd-icon class="menu-icon" :name="item.icon"></wd-icon>
              <text class="menu-name">{{ item.name }}</text>
            </view>
            <wd-icon name="arrow-right"></wd-icon>
          </view>
        </view>
      </view>

      <view style="height: 50px;"></view>
    </view>


    <!--附加组件-->
    <tab-bar></tab-bar>
    <!-- 头像裁剪组件 -->
    <wd-img-cropper
        style="z-index: 9999"
        v-model="cropper.show"
        :img-src="cropper.src"
        @confirm="handleConfirmUpload"
        @cancel="cropper.show=false"
    ></wd-img-cropper>
  </base-page>
</template>

<script>
import {themeStore} from "../../store/theme";
import { getUserProfile } from '../../api/user/profile.js'
import {getServer} from "../../utils/server";
import {userStore} from "../../store/user";
import {checkClientVersion} from "../../utils/clientUtils";

export default {
  data() {
    return {
      isDarkMode: false, // 是否为暗黑模式
      cropper:{ //头像裁剪
        src:"",
        show:false
      },
      userInfo: {
        bgUrl: '../../static/bg.gif',
        userId: '',
        userName: '',
        nickName: '',
        email: '',
        avatar: '',
        loginIp: ''
      },
      // 功能区
      functionList: [
        { name: '历史记录', icon: 'history',func:()=>{
            uni.navigateTo({
              url: '/pages/videolib/history'
            })
        }},
        { name: '我的收藏', icon: 'star', func:()=>{
            uni.navigateTo({
              url: '/pages/videolib/favorite'
            })
        }},
        { name: '下载记录', icon: 'download', func:()=>{
            uni.showToast({
              title: '敬请期待',
              icon: 'none'
            })
        }},
        { name: '意见反馈', icon: 'chat', func:()=>{
            uni.showToast({
              title: '敬请期待',
              icon: 'none'
            })
        }},
      ],
      // 更多服务
      moreServices: [
        { name: '常见问题', icon: 'tips',func: ()=>{
            plus.runtime.openURL('https://github.com/ChinkFiller/CoolVideo/issues', () => {
              uni.setClipboardData({
                data: 'https://github.com/ChinkFiller/CoolVideo/issues'
              })
              this.messageBox.alert({
                title: '提示',
                msg: '打开浏览器失败，请手动访问剪切板内的链接'
              })
            })
        }},
        { name: '关于我们', icon: 'github-filled',func:()=>{
            plus.runtime.openURL('https://github.com/ChinkFiller/CoolVideo', () => {
              uni.setClipboardData({
                data: 'https://github.com/ChinkFiller/CoolVideo'
              })
              this.messageBox.alert({
                title: '提示',
                msg: '打开浏览器失败，请手动访问剪切板内的链接'
              })
            })
        }},
        { name: '当前版本:null', icon: 'mobile-vibrate',func:()=>{
            checkClientVersion(() => {
              uni.showToast({
                title: '当前已经是最新版本~',
                icon: 'none'
              })
            })
          }},
        { name: '设置', icon: 'setting',func:()=>{
            // uni.showToast({
            //   title: '敬请期待',
            //   icon: 'none'
            // })
            uni.navigateTo({
              url: '/pages/setting/index'
            })
        }},
      ]
    };
  },
  onLoad() {
    // 设置是否为深色模式
    this.isDarkMode=themeStore().theme.theme === 'dark'

    // 获取版本号
    plus.runtime.getProperty(plus.runtime.appid, (wgtInfo) => {
      this.moreServices[2].name="当前版本:V"+wgtInfo.version
    })
  },

  onShow() {
    getApp().globalData.nowIndex = 'my'
    // 获取用户数据
    this.getUserProfile()
  },

  computed: {
    headerStyle() {
      // 这里的背景图是用户自定义的
      return {
        backgroundImage: `url(${this.userInfo.bgUrl})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center'
      };
    }
  },

  methods: {
    getUserProfile(){
      getUserProfile().then(res => {
        Object.assign(this.userInfo, res.data.data)
        this.userInfo.avatar = this.userInfo.avatar?getServer()+this.userInfo.avatar:'../../static/user.png'
        this.userInfo.roleGroup=res.data.roleGroup
      })
    },
    toggleMode() {
      if (this.isDarkMode) {
        themeStore().setLightMode()
        this.isDarkMode=false
      } else {
        themeStore().setDarkMode()
        this.isDarkMode=true
      }
    },
    uploadBackground() {
      uni.chooseImage({
        count: 1,
        success: (res) => {
          this.userInfo.bgUrl = res.tempFilePaths[0];
          uni.showToast({ title: '背景已更新', icon: 'none' });
        }
      });
    },

    goToVipPage(){
      uni.showToast({
        title: '敬请期待',
        icon: 'none'
      })
    },
    uploadAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          this.cropper.src = res.tempFilePaths[0]
          this.cropper.show=true
        }
      })
    },
    handleConfirmUpload(event) {
      const { tempFilePath } = event
      uni.uploadFile({
        url: getServer() + '/system/user/profile/avatar',
        filePath: tempFilePath,
        name: 'avatarfile',
        header: {
          'Authorization': userStore().token
        },
        success: (uploadRes) => {
          const result = JSON.parse(uploadRes.data)
          if (result.code === 200) {
            this.userInfo.avatar = getServer() + result.imgUrl
            uni.showToast({
              title: '修改头像成功',
              icon: 'success'
            })
          } else {
            uni.showToast({
              title: result.msg || '修改头像失败',
              icon: 'error'
            })
          }
        },
        fail: () => {
          uni.showToast({
            title: '上传头像失败',
            icon: 'error'
          })
        }
      })
    }
  }
};
</script>

<style lang="scss">

/* ================= 变量定义 ================= */
:root {
  --page-bg: #ffffff;
  --card-bg: #ffffff;
  --text-main: #333333;
  --text-sub: #666666;
  --vip-border: #2968d2; /* B站粉 */
  --vip-text: #2968d2;
  --border-line: #eeeeee;
}

.dark-mode {
  --page-bg: #121212; /* 极黑背景 */
  --card-bg: #121212;
  --text-main: #e0e0e0;
  --text-sub: #aaaaaa;
  --vip-border: #2968d2;
  --vip-text: #2968d2;
  --border-line: #333333;
}

.user-center-container {
  min-height: 100vh;
  background-color: var(--page-bg);
  /* 解决顶部可能出现的白色缝隙 */
  overflow-x: hidden;
}

/* ================= 1. 头部区域 ================= */
.header-section {
  position: relative;
  width: 100%;
  height: 520rpx; /* 足够的高度容纳背景和重叠部分 */
  /* 背景图在 style 中动态设置 */
}

/* 渐变遮罩：实现背景图底部"消失"融合的效果 */
.gradient-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 120rpx; /* 渐变高度 */
  background: linear-gradient(to bottom, transparent 0%, var(--page-bg) 100%);
  pointer-events: none;
  z-index: 1;
}

/* 模式切换 */
.settings-bar {
  position: absolute;
  top: calc(var(--status-bar-height) + 20rpx); /* 适配刘海屏 */
  right: 30rpx;
  z-index: 10;
}

.mode-toggle-btn {
  font-size: 48rpx;
}

/* ================= 2. 用户信息 (左对齐) ================= */
.user-info-wrapper {
  position: absolute;
  left: 40rpx;
  bottom: 140rpx; /* 留出空间给 VIP Bar 插入 */
  display: flex;
  flex-direction: row; /* 横向排列 */
  align-items: center; /* 垂直居中 */
  z-index: 5; /* 在遮罩层之上 */
}

.avatar-box {
  position: relative;
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(255, 255, 255, 0.9);
  overflow: hidden;
  margin-right: 30rpx;
  background-color: #ccc;
}

.avatar {
  width: 100%;
  height: 100%;
}

.camera-icon {
  display: none; /* 暂时隐藏，或根据需求显示 */
}

.text-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.username {
  font-size: 40rpx;
  font-weight: bold;
  color: #ffffff;
  margin-bottom: 12rpx;
  text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.5);
}

/* ================= 3. 会员标签 (透明+边框) ================= */
.vip-tag {
  align-self: flex-start; /* 靠左 */
  border: 2rpx solid white; /* 仅有边框 */
  border-radius: 50rpx; /* 圆角 */
  padding: 4rpx 16rpx;
  background-color: rgba(0, 0, 0, 0); /* 极其微弱的背景增加可读性，或设为 transparent */
  backdrop-filter: blur(4px);
  font-weight: bold;
}

.tag-text {
  font-size: 28rpx;
  color: white; /* 字体颜色与边框一致 */
  font-weight: bold;
}

/* ================= 4. 会员权益条 (VIP Bar) ================= */
.vip-bar-card {
  position: relative;
  height: 110rpx;
  /* 核心布局：负 Margin 提上去，覆盖在 Header 底部 */
  margin: -65rpx 30rpx 0;
  border-radius: 16rpx;
  background-size: cover;
  background-position: center;
  /* background-image 在行内样式中设置 */

  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.2);
  z-index: 10; /* 确保在最上层 */

  display: flex;
  align-items: center;
  padding: 0 30rpx;
}

.vip-content {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.vip-text-group {
  display: flex;
  flex-direction: column;
}

.vip-title {
  color: #f8ecea; /* 适合深红背景的浅色字 */
  font-size: 30rpx;
  font-weight: bold;
}
.vip-sub {
  color: rgba(252, 236, 232, 0.7);
  font-size: 22rpx;
}

.vip-btn {
  background-color: #fff;
  color: #c94a56; /* 取背景图的主色调 */
  padding: 10rpx 24rpx;
  border-radius: 30rpx;
  font-size: 24rpx;
  font-weight: bold;
}

/* ================= 5. 功能图标区 ================= */
.function-grid {
  display: flex;
  justify-content: space-around;
  margin: 40rpx 30rpx; /* 上下间距 */
}

.grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.grid-icon {
  font-size: 56rpx;
  color: #5c97cf; /* 使用主题粉色 */
  margin-bottom: 12rpx;
}

.grid-text {
  margin-top: 5px;
  font-size: 26rpx;
  color: var(--text-sub);
}

/* ================= 6. 更多服务列表 ================= */
.menu-list-section {
  margin: 0 30rpx;
}

.section-header {
  font-size: 32rpx;
  font-weight: bold;
  color: var(--text-color);
  margin-bottom: 20rpx;
  display: block;
}

.menu-box {
  background-color: var(--card-bg);
  border-radius: 16rpx;
  padding: 0 20rpx;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 10rpx;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-left {
  display: flex;
  align-items: center;
}

.menu-icon {
  font-size: 40rpx;
  margin-right: 24rpx;
  color: var(--vip-text);
  width: 50rpx; /* 对齐图标宽度 */
  text-align: center;
}

.menu-name {
  font-size: 30rpx;
  color: var(--text-main);
}
</style>
