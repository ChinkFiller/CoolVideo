<template>
  <base-page :nav-color="themeStore().theme['--primary-color']">
    <wd-message-box selector="login"/>
    <view id="login-page" class="login-container" :style="themeStore().theme">
      <view class="login-header">
        <image class="logo" src="@/static/logo.png" />
        <text class="title">CoolVideo登录</text>
      </view>

      <view class="form-field">
        <wd-input v-model="form.username" placeholder="用户名" clearable prefix-icon="user" />
      </view>

      <view class="form-field">
        <wd-input v-model="form.password" placeholder="密码" show-password clearable prefix-icon="lock-on" />
      </view>

      <view class="captcha-row" v-if="needCaptcha">
        <wd-input v-model="form.code" placeholder="请输入验证码" clearable />
        <image :src="captchaImg" mode="aspectFit" class="captcha-img" @click="getCaptcha" />
      </view>

      <wd-button block type="primary" class="login-btn" @click="handleLogin">登录</wd-button>

      <view class="links">
        <text @click="openProtocol('用户协议', userAgreement)">《用户协议》</text>
        <text @click="openProtocol('隐私协议', privacyPolicy)">《隐私协议》</text>
      </view>

      <view class="links2">
        <text @click="goRegister">注册</text>
        <text @click="goFindPassword">忘记密码？</text>
      </view>
    </view>
  </base-page>
</template>

<script>
import { getCaptchaImage, login } from '@/api/user/auth.js'
import { useMessage } from '@/uni_modules/wot-design-uni'
import {themeStore} from "@/store/theme";
import {userStore} from "@/store/user";
import {canRegister} from "../../api/user/user";

export default {
  setup() {
    const messageBox = useMessage('login')
    return { messageBox }
  },

  data() {
    return {
      form: {
        username: '',
        password: '',
        code: '',
        uuid: ''
      },
      needCaptcha: true,
      captchaImg: '',
      userAgreement: '这是用户协议内容...',
      privacyPolicy: '这是隐私协议内容...'
    }
  },

  mounted() {
    this.getCaptcha()
  },

  methods: {
    themeStore,
    // 获取验证码
    getCaptcha() {
      getCaptchaImage().then(res => {
        if (!res.data.captchaEnabled) {
          this.needCaptcha = false
        } else {
          this.captchaImg = 'data:image/png;base64,' + res.data.img
          this.form.uuid = res.data.uuid
        }
      })
    },

    // 登录
    handleLogin() {
      uni.showLoading({ title: '登录中...' })
      if (!this.form.username || !this.form.password || (!this.form.code && this.needCaptcha)) {
        uni.showToast({ title: '请填写完整信息', icon: 'none' })
        return
      }

      login(this.form)
          .then(res => {
            userStore().setUserLoginState({
              token: res.data.token
            })
            uni.hideLoading()
            setTimeout(() => {
              uni.reLaunch({ url: '/pages/index/index' })
            }, 100)
          })
          .catch(err => {
            uni.showToast({
              title: err.message || '登录失败',
              icon: 'none'
            })
            this.getCaptcha()
          })
    },

    // 打开协议弹窗
    openProtocol(title, msg) {
      this.messageBox.alert({ title, msg })
    },

    // 跳转注册
    goRegister() {
      canRegister().then(res => {
        if (res.data.data) {
          uni.navigateTo({ url: '/pages/user/register' })
        }else{
          uni.showToast({ title: '该App暂未开放注册', icon: 'none' })
        }
      })

    },

    // 跳转找回密码
    goFindPassword() {
      uni.navigateTo({ url: '/pages/user/find-password' })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 0;
  justify-content: center;
  min-height: 100vh;
  background-color: var(--primary-color);
  color: var(--text-color);

.login-header {
  display: flex;
  flex-direction: column; // 让 Logo 和标题上下排列
  align-items: center;    // 水平居中
  margin-bottom: 30rpx;   // 底部留空

  .logo {
    width: 120rpx;
    height: 120rpx;
    margin-bottom: 20rpx; // Logo 和标题的间距
  }

  .title {
    font-size: 36rpx;     // 标题字体大一点
    font-weight: bold;
  }
}

  // 统一宽度
  .form-field {
    width: 300px;
    margin-bottom: 20px;

    .wd-input {
      width: 100%;
      border-radius: 8px;
      overflow: hidden;
    }
  }

  .captcha-row {
    width: 300px;
    display: flex;
    align-items: center;
    margin-bottom: 20px;

    .wd-input {
      flex: 1;
      border-radius: 8px;
    }

    .captcha-img {
      width: 100px;
      height: 40px;
      margin-left: 10px;
      border-radius: 6px;
      border: 1px solid #ddd;
    }
  }

  .login-btn {
    width: 300px;
    height: 44px;
    font-size: 16px;
    border-radius: 999px;
    margin-top: 10px;
  }

  .links {
    margin-top: 15px;
    display: flex;
    justify-content: center;
    gap: 10px;

    text {
      font-size: 14px;
      color: #409eff;
      cursor: pointer;
      display: inline-block;
    }
  }

  .links2 {
    margin-top: 10px;
    width: 300px;
    display: flex;
    justify-content: space-between;

    text {
      font-size: 14px;
      cursor: pointer;


      &:hover {
        color: #409eff;
      }
    }
  }
}
</style>
