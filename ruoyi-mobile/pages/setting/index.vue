<template>
  <base-page :nav-color="themeStore().theme['--header-bg']">
    <wd-message-box></wd-message-box>
    <template v-slot:header>
      <view class="custom-navbar">
        <wd-icon name="thin-arrow-left" @click="back"></wd-icon>

        <view class="nav-title">
          <text>设置</text>
        </view>

        <view class="nav-right"></view>
      </view>
    </template>

    <view class="content-body">
      <view class="group" v-for="(group, gIndex) in menuList" :key="gIndex">
        <view
            class="item"
            v-for="(item, iIndex) in group"
            :key="iIndex"
            @click="handleNavigate(item)"
            :class="{ 'no-border': iIndex === group.length - 1 }"
        >
          <view class="content-left">
            <text class="title">{{ item.title }}</text>
            <text v-if="getDynamicDesc(item.key)" class="subtitle">
              {{ getDynamicDesc(item.key) }}
            </text>
          </view>

          <view class="content-right">
            <text v-if="item.showStatus" class="status-text">
              {{ getDynamicDesc(item.key) }}
            </text>
            <wd-icon name="arrow-right" size="24" color="#999999"></wd-icon>
          </view>
        </view>
      </view>
      <view class="group">
        <view
            class="item no-border"
            style="justify-content:center;font-size: 30rpx;color: #f55858"
            @click="logout"
        >
          退出登录
        </view>
      </view>
    </view>

  </base-page>
</template>

<script>
import {back} from "../../utils/routerUtils";
import {userStore} from "../../store/user";
import {useMessage} from "../../uni_modules/wot-design-uni";
import {themeStore} from "../../store/theme";

export default {
  data() {
    return {
      settingsData: {},
      message: useMessage(),
      menuList: [
        [
          { title: '账号资料', key: 'accountInfo' }
        ],
        [
          { title: '播放器设置', key: 'player' }
        ],
        [
          { title: '下载管理', key: 'download' },
          { title: '清理存储空间', key: 'storageClean' },
        ]
      ]
    };
  },
  onShow() {
    this.loadSettings();
  },
  methods: {
    themeStore,
    back,
    loadSettings() {
      try {
        const value = uni.getStorageSync('appSettings');
        if (value) {
          this.settingsData = JSON.parse(value);
        } else {
          // 默认模拟数据
          this.settingsData = {
            home_recommend: { desc: "双列/关闭自动播放" },
            timer_shutdown: { desc: "不开启" }
          };
          uni.setStorageSync('appSettings', JSON.stringify(this.settingsData));
        }
      } catch (e) {
        console.error('读取设置失败', e);
      }
    },
    getDynamicDesc(key) {
      if (this.settingsData[key] && this.settingsData[key].desc) {
        return this.settingsData[key].desc;
      }
      return '';
    },
    logout(){
      this.message.confirm({
        title: '提示',
        msg: '确定要退出登录吗？',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(() => {
        userStore().clearUserLoginState()
        uni.navigateTo({ url: '/pages/index/index' })
      }).catch(() => {})
    },
    handleNavigate(item) {
      uni.navigateTo({
        url: `/pages/setting/detail?configKey=${item.key}&title=${item.title}`
      });
    }
  }
};
</script>

<style lang="scss" scoped>

/* --- 自定义导航栏样式 --- */
.custom-navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 44px; /* 标准导航栏高度 */
  color: var(--text-color);
  background-color: var(--header-bg); /* 与背景一致 */
  padding: 0 16px;

  .nav-left {
    width: 60rpx; /* 增加点击区域 */
    height: 100%;
    display: flex;
    align-items: center;
  }

  .nav-title {
    font-size: 34rpx;
    font-weight: 500;
    color: var(--text-color);
  }

  .nav-right {
    width: 60rpx; /* 占位，保持标题居中 */
  }
}

/* --- 列表内容样式 --- */
.content-body {
  padding-top: 59px;
  padding-bottom: 20rpx;
}

.group {
  background-color: var(--header-bg);
  margin-bottom: 24rpx;
}

.item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 30rpx;
  position: relative;

  /* 分割线 */
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 30rpx;
    right: 0;
    height: 1px;
    background-color: #2c2c2e;
    transform: scaleY(0.5);
  }

  &.no-border::after {
    display: none;
  }
}

.content-left {
  display: flex;
  flex-direction: column;

  .title {
    font-size: 30rpx;
    color: var(--text-color);
  }

  .subtitle {
    font-size: 24rpx;
    color: #8e8e93;
    margin-top: 8rpx;
  }
}

.content-right {
  display: flex;
  align-items: center;

  .status-text {
    font-size: 28rpx;
    margin-right: 10rpx;
  }

  .arrow {
    font-size: 32rpx;
    color: #58585e;
    margin-left: 10rpx;
    font-family: monospace;
  }
}
</style>
