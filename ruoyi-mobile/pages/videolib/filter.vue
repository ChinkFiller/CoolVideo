<template>
  <base-page>
    <wd-message-box></wd-message-box>
    <view class="container" :style="themeStore().theme">
      <!-- 搜索框 -->
      <view class="search-container">
        <wd-search
            hide-cancel
            v-model="searchKeyword"
            placeholder="搜索"
            @search="loadFilterData"
            @clear="loadFilterData"
        ></wd-search>
      </view>

        <view class="filter-box">
          <view class="filter-item">
            <text class="filter-title">年份</text>
            <wd-segmented class="filter-option" @change="loadFilterData" :options="videoYearList" v-model:value="currentVideoYear"/>
          </view>
          <view class="filter-item">
            <text class="filter-title">排序</text>
            <wd-segmented class="filter-option" @change="loadFilterData" :options="videoOrderList" v-model:value="currentVideoOrder"/>
          </view>
        </view>

        <!-- 视频盒子 -->
        <scroll-view
            scroll-y
            class="filter-container"
            refresher-enabled
            :refresher-triggered="refreshTrigger"
            :scroll-top="filterScrollTop"
            :refresher-background="themeStore().theme['--primary-color']"
            @refresherrefresh="refreshFilter"
            @scrolltolower="loadMoreFilterData"
        >
          <view v-if="videoFilterList.length === 0" class="empty-tip">
            <text>{{ '没有找到相关记录' }}</text>
          </view>
          <view class='video-type' v-else>
            <video-card v-for="(item, index) in videoFilterList" :key="index" class="animation-fade-500"  :video-data="item"></video-card>
          </view>
          <!-- 加载更多 -->
          <view v-if="videoFilterList.length > 0" class="load-more">
            <text v-if="videoFilterHasMore">上拉加载更多</text>
            <text v-else>已经到底了</text>
          </view>
        </scroll-view>
      </view>

    <tab-bar></tab-bar>
  </base-page>
</template>

<script>
import { themeStore } from '@/store/theme'
import { useMessage } from '@/uni_modules/wot-design-uni'
import { getFilterData } from '@/api/filter/filter'

export default {
  data() {
    return {
      // store & 组件实例
      theme: themeStore(),
      message: useMessage(),

      refreshTrigger:false,

      // 搜索状态
      searchKeyword: '',

      // 视频分类
      videoYearList: Array.from({ length: 4 }, (_, i) => new Date().getFullYear() - i),
      currentVideoYear: new Date().getFullYear(),
      videoOrderList: ['按时间排序', '按点赞排序'],
      currentVideoOrder: '按时间排序',
      videoFilterList: [],
      filterPageNum: 1,
      videoFilterTotal: 0,
      videoFilterHasMore: false,
      filterScrollTop:0


    }
  },

  onLoad() {
    this.loadFilterData()
  },

  onShow() {
    getApp().globalData.nowIndex = 'video'
  },

  methods: {
    themeStore,

    // ------------------视频筛选------------------
    async loadFilterData() {
      // 重置列表数据
      this.videoFilterList=[]
      this.filterPageNum=1
      try {
        const res = await getFilterData(
            this.filterPageNum,
            10,
            this.currentVideoYear,
            this.currentVideoOrder === '按时间排序' ? 'time' : 'score',
            this.searchKeyword,
            '11'
        )
        this.videoFilterList = res.data.rows || []
        this.videoFilterTotal = res.data.total || 0
        this.videoFilterHasMore = this.videoFilterList.length < this.videoFilterTotal
      } catch (e) {
        uni.showToast({ title: '加载视频分类失败', icon: 'none' })
      } finally {
        this.refreshTrigger=false
        this.filterScrollTop=0
      }
    },

    async loadMoreFilterData() {
      if (!this.videoFilterHasMore) return
      try {
        this.filterPageNum++
        const res = await getFilterData(
            this.filterPageNum,
            10,
            this.currentVideoYear,
            this.currentVideoOrder === '按时间排序' ? 'time' : 'score',
            this.searchKeyword,
            '11'
        )
        if (res.data.code === 200) {
          const newData = res.data.rows || []
          this.videoFilterList = [...this.videoFilterList, ...newData]
          this.videoFilterHasMore = this.videoFilterList.length < this.videoFilterTotal
        }
      } catch (e) {
        this.filterPageNum--
        uni.showToast({ title: '加载视频分类失败', icon: 'none' })
      } finally {
        this.refreshTrigger=false
      }
    },

    refreshFilter(){
      if (this.refreshTrigger) return
      this.refreshTrigger=true
      this.loadFilterData()
    }
  }
}
</script>


<style lang="scss">
page{
  overflow: hidden;
}
.container {
  min-height: 100vh;
  background-color: var(--primary-color);
  color: var(--text-color);
}

.search-container {
  width: 100%;
  height: 50px;
}

.filter-container{
  padding-bottom: 20px;
  margin: 0 auto;
  height: calc(100vh - 50px - 50px - 102px - var(--status-bar-height));
}

.empty-tip {
  text-align: center;
  padding: 30px 0;
  color: #999;
}

.filter-box{
  width: 90%;
  margin: 10px auto;

  .filter-item{
    display: flex;
    margin: 10px 0;

    .filter-title{
      white-space: nowrap;
      line-height: 35px;
      margin-right: 10px;
    }
    .filter-option{

    }
  }
}

.video-type{
  width: 100%;
  display: grid;
  justify-content: center;
  grid-template-columns: repeat(auto-fill, 120px);
}

.load-more {
  text-align: center;
  padding: 15px 0;
  color: #999;
  font-size: 14px;
}
</style>
