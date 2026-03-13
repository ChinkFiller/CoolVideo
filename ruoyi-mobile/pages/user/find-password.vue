<template>
  <base-page>
    <wd-message-box selector="find-password"></wd-message-box>
    <view class="find-container" :style="themeStore().theme">
      <wd-input
          v-model="email"
          placeholder="请输入注册邮箱"
          clearable
          class="input-field"
      />
      <wd-button
          block
          type="primary"
          class="btn-primary"
          @click="sendFindRequest"
      >
        发送找回请求
      </wd-button>
      <wd-button
          block
          plain
          class="btn-secondary"
          @click="goLogin"
      >
        返回登录
      </wd-button>
    </view>
  </base-page>

</template>

<script>
import { findPassword } from '../../api/user/user.js'
import {themeStore} from "@/store/theme";
import {useMessage} from "../../uni_modules/wot-design-uni";

export default {
  setup(){
    const message = useMessage('find-password')
    return { message }
  },
  data() {
    return {
      email: '',
      message: null
    }
  },
  methods: {
    themeStore() {
      return themeStore
    },
    // 发送找回密码请求
    sendFindRequest() {
      if (!this.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email)) {
        return uni.showToast({ title: '请输入有效邮箱', icon: 'none' })
      }
      findPassword(this.email).then(() => {
        this.message.alert({
          title: '提示',
          msg: '您的密码已初始化为123456，请您在登录后尽快修改密码'
        }).then(res=>{
          uni.redirectTo({ url: '/pages/user/login' })
        })
      }).catch(e=>{
        uni.showToast({ title: e.message, icon  : 'none' })
      })
    },

    // 跳转回登录
    goLogin() {
      uni.redirectTo({ url: '/pages/user/login' })
    }
  }
}
</script>

<style scoped lang="scss">
.find-container {
  background-color: var(--primary-color);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  min-height: 100vh;

  /* 新增：居中布局 */
  display: flex;
  align-items: center;
  justify-content: center;

  /* 内容宽度限制 */
  flex-direction: column;
  padding: 0 20px;

  .input-field {
    margin-bottom: 20px;
    overflow: hidden;
    width: 100%;
  }

  .btn-primary {
    margin-bottom: 15px;
    border-radius: 30px;
    font-size: 16px;
    padding: 10px 0;
    width: 100%;
  }

  .btn-secondary {
    border-radius: 30px;
    font-size: 16px;
    padding: 10px 0;
    width: 100%;
  }
}
</style>
