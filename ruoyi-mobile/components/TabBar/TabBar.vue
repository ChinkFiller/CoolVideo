<template>
  <!-- 底部标签栏 -->
  <wd-tabbar
      v-model="activeTabbar"
      @change="handleTabChange"
      fixed
      safe-area-inset-bottom
      :inactive-color="themeStore().theme['--text-color']"
  >
    <wd-tabbar-item
        v-for="(item,index) in tabs"
        :name="item.name"
        :icon="item.icon"
        :title="item.title"
    />
  </wd-tabbar>
</template>

<script>

import {themeStore} from "../../store/theme";

export default {
  data() {
    return {
      activeTabbar: getApp().globalData.nowIndex,
      tabs:[
        {
          name:'home',
          icon:'home',
          title:'首页',
          path:'/pages/index/index'
        },
        {
          name:'week',
          icon:'view-module',
          title:'周期表',
          path:'/pages/weektable/weektable'
        },
        {
          name:'video',
          icon:'video',
          title:'影视库',
          path:'/pages/videolib/filter'
        },
        {
          name:'my',
          icon:'user',
          title:'我的',
          path:'/pages/user/profile'
        }
      ]
    }
  },
  methods: {
    themeStore,
    handleTabChange(name) {
      if (name.value===getApp().globalData.nowIndex){
        return;
      }
      getApp().globalData.nowIndex = name.value
      this.tabs.forEach(item=>{
        if (item.name===name.value){
          uni.redirectTo({
            url: item.path,
            animationType:"none"
          })
        }
      })
    }
  },
}
</script>
