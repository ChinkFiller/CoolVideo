<template>
  <base-page>
    <template v-slot:header>
      <!-- 顶部搜索栏 -->
      <view class="search-bar" @click="uni.navigateTo({ url: '/pages/index/searcher'})">
        <wd-search
            :disabled="true"
            hide-cancel
            v-model="searchValue"
            placeholder="搜索你想看的视频"
        ></wd-search>
      </view>
    </template>

    <wd-message-box selector="index" v-slot="{ msg }">
      <view v-html="msg"></view>
    </wd-message-box>

    <!-- 搜索结果展示 -->
    <view class="index-page" :style="themeStore().theme">

		  <!-- 原有内容（当不显示搜索结果时显示） -->
		  <view v-show="loaded" class="animation-fade-500">
        <wd-swiper :list="bannerImages" autoplay :indicator="{ type: 'dots-bar' }" style="margin: 0 10px 0 10px;" @click="handleBannerClick">
          <template #indicator="{ current, total }">
          <view style="position: absolute; bottom: 10px; left: 10px;color: white;font-weight: bold;font-size: 16px">{{ bannerList[current]?.title }}</view>
          <view style="position: absolute; bottom: 16px; right: 20px; display: flex; align-items: center;">
            <view
              v-for="(item, index) in total"
              :key="index"
              :style="{
            width: current === index ? '16px' : '8px',
            height: '8px',
            borderRadius: '4px',
            backgroundColor: current === index ? '#fff' : 'rgba(255,255,255,0.5)',
            marginLeft: index === 0 ? '0' : '6px',
            transition: 'all 0.3s'
            }"
            />
          </view>
          </template>
        </wd-swiper>

        <!-- 公告栏 -->
        <view class="notice-bar">
          <wd-notice-bar direction="vertical" :text="indexNoticeTitles" :delay="3" custom-class="space" />
        </view>

        <!-- 功能按钮区 -->
        <view class="function-buttons">
          <view class="function-item" v-for="(item, index) in functionItems" :key="index" @click="handleFuncClick(item.href)">
            <image :src="processImageUrl()(item.img)" style="width: 24px;height: 24px;"/>
          <text>{{ item.title }}</text>
          </view>
        </view>

        <!-- 最新更新 -->
        <view class="video-section">
          <view class="section-header">
          <text class="section-title">最新更新</text>
          <wd-icon name="arrow-right" size="16px"></wd-icon>
          </view>
          <scroll-view scroll-x class="video-scroll">
          <view class="video-list">
            <video-card v-for="(item, index) in latestList" :key="index" :video-data="item"/>
          </view>
          </scroll-view>
        </view>

        <!-- 热门推荐 -->
        <view class="video-section">
          <view class="section-header">
          <text class="section-title">热门推荐</text>
          <wd-icon name="arrow-right" size="16px"></wd-icon>
          </view>
          <scroll-view scroll-x class="video-scroll">
          <view class="video-list">
            <video-card v-for="(item, index) in hotsList" :key="index" :video-data="item"/>
          </view>
          </scroll-view>
        </view>
		  </view>


      <!-- 底部标签栏 -->
      <tab-bar></tab-bar>
    </view>
  </base-page>
</template>

<script>
import { getHomeConfig } from '../../api/index/index'
import { themeStore } from '../../store/theme'
import { processImageUrl } from "../../utils/imageUtils";
import {createIterator} from "../../utils/stringUtils";
import {getNoticeHash} from "../../utils/clientUtils";

import { useMessage } from '@/uni_modules/wot-design-uni'

export default {
  data() {
    return {
      searchValue: '',
      activeWeekTab: new Date().getDay() === 0 ? 6 : new Date().getDay() - 1,
      loaded: false,

      indexNotice: [],
      indexNoticeTitles: [],
      popNotice: [],
      bannerList: [],
      latestList: [],
      hotsList: [],
      bannerImages: [],
      functionItems: [],

      message: useMessage()
    }
  },

  onReady() {
    this.getHomeData()
  },

  onShow() {
    // 设置当前的页面
    getApp().globalData.nowIndex = 'home'
  },

  onPullDownRefresh(){
    this.getHomeData()
  },

  methods: {
    processImageUrl() {
      return processImageUrl
    },
    themeStore,

    handleFuncClick(url) {
      plus.runtime.openWeb(url)
    },
    async getHomeData() {
      this.loaded=false
      try {
        const result = await getHomeConfig()
        const { banner, latest, hots, perweek, fastFunction, indexNotice: idxNotice, popNotice: pNotice } =
        result.data.data || {}

        this.bannerList = banner || []
        this.latestList = latest || []
        this.hotsList = hots || []
        this.weekList = perweek || {}
        this.functionItems = fastFunction || []
        this.indexNotice = idxNotice || []
        this.indexNoticeTitles = this.indexNotice.map(item => item.title)
        this.bannerImages = this.bannerList.map(item => processImageUrl(item.cover))
        this.popNotice = pNotice || []

        const noticeHash=getNoticeHash(this.popNotice)
        if (uni.getStorageSync('noticeHash') !== noticeHash && this.popNotice.length!=0) {
          const a=(it)=>{
            const item=it.next()
            if (!item.done){
              this.message.alert({
                title: item.value.title,
                msg: item.value.content
              }).then(() => {
                a(it)
              })
            }else{
              uni.setStorageSync('noticeHash', noticeHash)
            }
          }
          a(createIterator(this.popNotice))
        }
      } catch (error) {
        uni.showToast({
          title: error.message || '获取数据失败，请稍后重试',
          icon: 'none'
        })
      }finally {
        this.loaded = true
        uni.stopPullDownRefresh();
      }
    },

    handleBannerClick(item) {
      item = this.bannerList[item.index]
      if (item.vid) {
        uni.navigateTo({
          url: `/pages/detail/detail?id=${item.vid}`
        })
      } else if (item.url) {
        plus.runtime.openWeb(item.url)
      }
    }
  }
}
</script>

<style lang="scss">
.index-page{
  padding-top: calc(50px + 10px);
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 50px - 50px - var(--status-bar-height));
  padding-bottom: 50px;
  background-color: var(--primary-color);
  color: var(--text-color);

  .search-bar {
    z-index: 85;
    height: 50px;
    width: 100%;
    background-color: var(--primary-color);
  }

  .notice-bar {
    margin: 10px 10px;

    .notice-content {
      font-size: 14px;
    }
  }

  .function-buttons {
    display: flex;
    justify-content: space-around;
    padding: 15px;
    background-color: var(--primary-color);
    margin-bottom: 10px;

    .function-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      font-size: 12px;
    }
  }

  /* 视频板块 */
  .video-section {
    margin-bottom: 15px;
    background-color: var(--primary-color);
    padding: 10px 0;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 15px 10px;

      .section-title {
        font-size: 16px;
        font-weight: bold;
        color: var(--text-color);
      }
    }

    .video-scroll {
      white-space: nowrap;

      .video-list {
        display: inline-flex;
        padding: 0 10px;
      }
    }
  }

  /* 搜索结果 */
  .search-results {
    background-color: var(--primary-color);
    padding: 0;

    .search-result-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin: 0 15px 10px 15px;

      .search-result-title {
        font-size: 20px;
        font-weight: bold;
      }

      .search-result-count {
        font-size: 12px;
        color: #999;
      }
    }

    .search-result-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, 120px);
      width: 100%;
      justify-content: center;
    }
  }

}
</style>
