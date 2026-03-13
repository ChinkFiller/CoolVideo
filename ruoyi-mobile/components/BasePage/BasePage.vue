<template>
  <wd-config-provider :theme="themeStore().theme.theme">
    <view :class="pageClass" :style="themeStore().theme" >
      <view class="top-nav-safe" :style="{backgroundColor: navColor}" v-if="showNav">
        <slot name="header"></slot>
      </view>
      <slot></slot>
    </view>
  </wd-config-provider>
</template>

<script>
import { themeStore } from '@/store/theme.js'

export default {
  name: 'BasePage',
  setup() {
    return {
      themeStore,
      pageClass:'base-page-show-nav'
    }
  },
  data(){
    return {
    }
  },
  props:{
    showNav:{
      type:Boolean,
      default:true
    },
    navColor:{
      type:String,
      default:""
    }
  },
  created() {
    if (!this.showNav){
      this.pageClass='base-page-no-nav'
    }
  },
}
</script>

<style lang="scss">
.base-page-show-nav {
  height: calc(100vh - var(--status-bar-height));
  background-color: var(--bg-color);
  color: var(--text-color);
  padding-top: var(--status-bar-height);
}

.base-page-no-nav {
  height: 100vh;
  background-color: var(--bg-color);
  color: var(--text-color);
}

.top-nav-safe{
  background-color: var(--primary-color);
  z-index: 10;
  padding-top: var(--status-bar-height);
  position: fixed;
  top: 0;
  width: 100%;
}
</style>
