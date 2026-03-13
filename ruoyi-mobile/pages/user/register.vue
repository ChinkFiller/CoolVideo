<template>
  <base-page>
    <wd-message-box selector="verify-code">
      <verify-code-box v-model:value="captcha" ref="captcha"/>
    </wd-message-box>
    <view class="register-container" :style="themeStore().theme">
      <view class="form-card">
        <view class="email-row">
          <wd-input v-model="form.username" placeholder="邮箱" clearable />
          <wd-button v-if="!isSend" type="primary" size="small" @click="sendCodeWithVerify">发送验证码</wd-button>
          <wd-count-down v-else :time="10*60*1000" :format="'mm:ss'" @finish="isSend=false"/>
        </view>

        <wd-input v-model="form.code" placeholder="邮箱验证码" clearable />
        <wd-input v-model="form.password" placeholder="密码" show-password clearable />
        <wd-input v-model="repeatPassword" placeholder="重复密码" show-password clearable />

        <wd-button block type="primary" class="mt-20" @click="handleRegister">注册</wd-button>
        <wd-button block plain @click="goLogin">返回登录</wd-button>
      </view>
    </view>
  </base-page>
</template>

<script>
import { sendEmailCode, registerUser } from '@/api/user/user.js'
import { themeStore } from '@/store/theme'
import md5 from 'md5'
import {useMessage} from "../../uni_modules/wot-design-uni";

export default {
  data() {
    return {
      verifyBox:useMessage("verify-code"),
      isSend:false,
      repeatPassword: '', //重复密码
      form: {
        username:null,
        password:null,
        code:null,
        uuid:null
      },
      captcha:{
        code: '',
        uuid: ''
      },
    }
  },

  onLoad() {
    this.getCaptcha()
  },

  methods: {
    themeStore() {
      return themeStore
    },
    sendCodeWithVerify(){
      if (!this.form.username || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.username)) {
        return uni.showToast({ title: '请输入有效邮箱', icon: 'none' })
      }
      this.verifyBox.alert({
        title: '人机验证',
      }).then(()=>{
        if (this.captcha.code===null || this.captcha.code===''){
          this.getCaptcha()
          return uni.showToast({ title: '请输入验证码', icon: 'none' })
        }
        const sign = md5(this.form.username+this.captcha.code)
        sendEmailCode(this.form.username, sign,this.captcha.code,this.captcha.uuid).then(() => {
          this.isSend=true
          uni.showToast({ title: '验证码发送成功', icon: 'none' })
        }).catch(e=>{
          uni.showToast({ title: '人机验证未通过', icon: 'none' })
        }).finally(()=>{
          this.captcha.code=""
          this.$refs.captcha.getCaptchaImage()
        })
      }).catch(()=>{
        this.captcha.code=""
        this.$refs.captcha.getCaptchaImage()
      })
    },

    // 注册提交
    handleRegister() {
      if (!this.form.username || !this.form.password || !this.form.code) {
        return uni.showToast({ title: '请填写完整信息', icon: 'none' })
      }
      if (this.form.password !== this.repeatPassword) {
        return uni.showToast({ title: '两次密码不一致', icon: 'none' })
      }
      registerUser(this.form).then(() => {
        uni.showToast({ title: '注册成功', icon: 'success' })
        setTimeout(() => {
          uni.redirectTo({ url: '/pages/user/login' })
        }, 800)
      }).catch(e=>{
        uni.showToast({ title: e.message, icon: 'none' })
      })
    },
    handleCodeVerifyClose(){
      this.showCodeVerify=false
    },

    // 跳转登录
    goLogin() {
      uni.redirectTo({ url: '/pages/user/login' })
    }
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0;
  min-height: 100vh;
  background-color: var(--primary-color);

  .form-card {
    background: var(--primary-color);
    padding: 40rpx;
    border-radius: 16rpx;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
    width: 100%;
    max-width: 600rpx;
    display: flex;
    flex-direction: column;
    gap: 20rpx;
  }

  .email-row {
    display: flex;
    gap: 10rpx;
    align-items: center;

    .wd-input {
      flex: 1;
    }
  }

  .mt-20 {
    margin-top: 20rpx;
  }
}
</style>
