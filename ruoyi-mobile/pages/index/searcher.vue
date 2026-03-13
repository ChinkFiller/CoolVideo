<template>
  <base-page >
    <wd-message-box selector="searcher" style="z-index: 20"></wd-message-box>
    <template v-slot:header>
      <!-- 顶部搜索栏 -->
      <view class="search-bar-container" :style="{ '--primary-color': themeStore().theme.theme==='light'?'#e7e7e7':themeStore().theme['--primary-color'] }">
        <wd-icon name="thin-arrow-left" class="icon-search" style="background-color: transparent;margin-right: 10px" @click="back"></wd-icon>
        <view class="search-box">
          <wd-icon name="search" class="icon-search"></wd-icon>
          <input
              class="search-input"
              type="text"
              v-model="keyword"
              placeholder="请输入搜索内容"
              confirm-type="search"
              @confirm="doSearch(keyword)"
              @input="handleInput"
          />
          <text v-if="keyword" class="icon-clear" @click="keyword=''">✕</text>
        </view>
        <view class="search-btn" @click="doSearch(keyword)">搜索</view>
      </view>
    </template>

    <view class="search-page" v-show="!searched">
      <view class="placeholder-box"></view>
      <view class="content-box">

        <view class="section" v-if="historyList.length > 0">
          <view class="header">
            <text class="title">搜索历史</text>
            <view class="clear-btn" @click="clearHistory">
              <wd-icon name="delete"/> 清空
            </view>
          </view>
          <view class="tag-list">
            <view
                class="tag"
                v-for="(item, index) in historyList"
                :key="index"
                @click="doSearch(item)"
            >
              {{ item }}
            </view>
          </view>
        </view>

        <view class="section">
          <view class="header">
            <text class="title">猜你想搜</text>
            <view class="refresh-btn" @click="switchRecommendList">
              <wd-icon name="refresh"/>
            </view>
          </view>
          <view class="tag-list">
            <view
                class="tag hot"
                v-for="(item, index) in recommendList"
                :key="index"
                @click="doSearch(item.name)"
            >

              {{index<3?'🔥'+item.name:item.name }}
            </view>
          </view>
        </view>

      </view>
    </view>
    <view class="search-page" v-show="searched">
      <infinite-scroll-list
          style="padding-top: 100rpx"
          ref="searchList"
          :load-function="searchVideos"
          :emptyText="'没有找到相关视频'"
          :scroll-height="calcScrollHeight(50,true)"
          :background-color="themeStore().theme['--primary-color']"
          :init-load="false"
          v-slot="{ data }"
      >
        <view class="history-item" @click="goToVideoDetail(data.id)">
          <page-image class="video-img" :src="processImageUrl(data.imgUrl)" />
          <view class="info-container">
            <view class="watch-info">
              <text class="video-text">{{ data.name }}</text>
              <text class="part-text">{{ getVideoState(data.state,data.part) }}</text>
            </view>
            <view class="update-time">上映时间: {{ formatDate(data.publicDate) }}</view>
          </view>
        </view>
      </infinite-scroll-list>


    </view>
  </base-page>
</template>

<script>
import { useMessage } from '@/uni_modules/wot-design-uni'
import { searchVideos } from "../../api/video/video";
import { getHomeConfig } from "../../api/index";
import {themeStore} from "../../store/theme";
import {back, goToVideoDetail, goToVideoPlayer} from "../../utils/routerUtils";
import {processImageUrl} from "../../utils/imageUtils";
import {calcScrollHeight} from "../../utils/clientUtils";
import {formatWatchTime, getVideoState} from "../../utils/stringUtils";
import {formatDate} from "../../utils/dateUtils";

export default {
  data() {
    return {
      message: useMessage("searcher"),
      keyword: '', // 搜索关键词
      historyList: [], // 历史记录数组
      // 模拟热门搜索数据，实际开发中请从接口获取
      recommendList: [],
      searched:false,
      recommendTagList:[
          "hots",
          "latest"
      ],
      nowRecommendListIndex:0
    };
  },
  onLoad() {
    // 页面加载时，从本地缓存读取历史记录
    this.getHistory();
    this.loadHotList();
  },
  onBackPress() {
    if (this.searched){
      this.searched=false;
      return true;
    }else {
      return false;
    }
  },
  methods: {
    goToVideoDetail,
    getVideoState,
    formatDate,
    formatWatchTime,
    searchVideos,
    calcScrollHeight,
    processImageUrl,
    goToVideoPlayer,
    back,
    themeStore,
    switchRecommendList(){
      if (this.nowRecommendListIndex<this.recommendTagList.length-1){
        this.nowRecommendListIndex++;
      }else{
        this.nowRecommendListIndex=0;
      }
      this.loadHotList()
    },
    async loadHotList(){
      const res = await getHomeConfig();
      if (this.recommendTagList[this.nowRecommendListIndex]==="hots"){
        this.recommendList=res.data.data.hots
      }
      if (this.recommendTagList[this.nowRecommendListIndex]==="latest"){
        this.recommendList=res.data.data.latest
      }
    },
    // 执行搜索的核心逻辑
    doSearch(key) {
      // 1. 校验非空
      if (!key) {
        uni.showToast({ title: '请输入搜索内容', icon: 'none' });
        return;
      }

      // 2. 更新当前输入框显示（如果是点击标签触发的）
      this.keyword = key;

      // 3. 保存到历史记录
      this.saveHistory(key);

      this.searched=true

      setTimeout(() => {
        this.$refs.searchList.handleSearch(this.keyword)
      }, 200);
    },

    // 保存历史记录（包含去重逻辑）
    saveHistory(key) {
      let list = this.historyList;

      // 如果已存在，先删除旧的（为了把最新的排到前面）
      const index = list.indexOf(key);
      if (index > -1) {
        list.splice(index, 1);
      }

      // 添加到头部
      list.unshift(key);

      // 限制长度，例如只保留最近10条
      if (list.length > 10) {
        list.pop();
      }

      this.historyList = list;
      // 持久化存储到本地
      uni.setStorageSync('search_history', JSON.stringify(list));
    },

    // 读取历史记录
    getHistory() {
      const value = uni.getStorageSync('search_history');
      if (value) {
        this.historyList = JSON.parse(value);
      }
    },

    // 清空历史记录
    clearHistory() {
      this.message.confirm({
        title: '提示',
        msg: '确定要清空搜索历史吗？'
      }).then(() => {
        this.historyList = [];
        uni.removeStorageSync('search_history');
      }).catch(() => {

      })
    },

    handleInput(e){
      if (e.detail.value === 'devTool') {
        uni.navigateTo({
          url: '/pages/dev/dev'
        })
      }
    }
  }
};
</script>

<style lang="scss" scoped>
page{
  overflow-y: hidden;
}
/* 页面背景 */
.search-page {
  min-height: 100%;
  background-color: var(--primary-color);
}

/* 顶部搜索栏容器 */
.search-bar-container {
  width: 100%;
  height: 100rpx; /* 高度 */
  background-color: var(--primary-color);
  display: flex;
  align-items: center;
  padding: 0 30rpx;
  box-sizing: border-box;
  box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);
}

/* 占位符，防止内容被fixed遮挡 */
.placeholder-box {
  height: 50px;
}

/* 搜索输入框区域 */
.search-box {
  flex: 1;
  height: 60rpx;
  background-color: var(--header-bg);
  border-radius: 30rpx;
  display: flex;
  align-items: center;
  padding: 0 20rpx;
  margin-right: 20rpx;
}

.icon-search {
  color: #999;
  margin-right: 10rpx;
  font-size: 28rpx;
  background-color: var(--header-bg);
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: var(--text-color);
  background-color: var(--header-bg);
}

.icon-clear {
  color: #999;
  font-size: 28rpx;
  padding: 10rpx;
}

/* 搜索按钮 */
.search-btn {
  font-size: 30rpx;
  color: #258dff; /* 主题色 */
  font-weight: bold;
}

/* 内容区域 */
.content-box {
  padding: 30rpx;
}

/* 板块通用样式 */
.section {
  margin-bottom: 50rpx;

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;

    .title {
      font-size: 30rpx;
      font-weight: bold;
      color: var(--text-color);
    }

    .clear-btn, .refresh-btn {
      font-size: 26rpx;
      color: #999;
    }
  }
}

/* 标签列表 */
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx; /* 控制间距 */

  .tag {
    padding: 12rpx 28rpx;
    background-color: #ffffff;
    border-radius: 30rpx;
    font-size: 26rpx;
    color: #555;
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;

    /* 简单的阴影效果 */
    box-shadow: 0 2rpx 6rpx rgba(0,0,0,0.03);

    &.hot {
      background-color: #fff0f0; /* 热门推荐给个淡红色背景 */
      color: #ff4d4f;
    }

    .hot-icon {
      margin-right: 6rpx;
      font-size: 24rpx;
    }
  }
}

.history-item {
  display: flex;
  background-color: var(--primary-color);
  border-radius: 8px;
  padding: 10px;
  position: relative;
  margin: 10px 10px 0 10px;

  .video-container {
    flex: 0 0 100px;
  }

  .video-img{
    width: 100px;
    border-radius: 10px;
    height: 130px;
  }
  .info-container {
    flex: 1;
    padding-left: 10px;
    position: relative;

    .watch-info {
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
      margin-top: 5px;

      .video-text{
        font-size: 16px;
        margin-bottom: 10px;
        width: 100%;
      }

      .part-text {
        font-size: 14px;
      }

      .time-text {
        font-size: 14px;
      }
    }

    .update-time {
      font-size: 12px;
      color: #999;
      margin-top: 10px;
    }
  }

  .delete-btn {
    position: absolute;
    right: 10px;
    bottom: 10px;
    color: #ff5252;
  }
}
</style>
