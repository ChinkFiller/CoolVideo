<template>
  <view class="code-verify-container">
    <image class="captcha-img" :src="captchaImg" mode="aspectFit" @click="getCaptchaImage" />
    <wd-input class="item" v-model="value.code" placeholder="请输入验证码" @input="inputChange"/>
  </view>
</template>

<script>
import { getRegisterCaptchaImage } from "../../api/user/auth";
export default {
  name: 'VerifyCodeBox',
  props: {
    value: {
      type: Object,
      required: true,
    },
  },
  data(){
    return {
      captchaImg:""
    }
  },
  created() {
    this.getCaptchaImage()
  },
  methods:{
    async getCaptchaImage(){
      const res=await getRegisterCaptchaImage()
      this.captchaImg = 'data:image/png;base64,' + res.data.img
      this.value.uuid=res.data.uuid
    },
    inputChange(e){
      this.$emit('input', {code:e.value,uuid:this.value.uuid})
    }
  }
}
</script>

<style lang="scss">
.code-verify-container{
  height: 80px;
  width: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin: 10px auto;

  .item{
    margin: auto;
    width: 200px;
  }

  .captcha-img {
    width: 100px;
    height: 40px;
    margin: auto;
    border-radius: 6px;
    border: 1px solid #ddd;
  }
}
</style>
