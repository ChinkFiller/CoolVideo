<template>
  <base-page :show-nav="false">
    <view class="video-detail" :style="Object.assign({ '--theme-color': themeColor },themeStore().theme)">
      <wd-icon name="thin-arrow-left" size="22px" class="back-btn" @click="back()"></wd-icon>
      <!-- 背景图（高斯模糊） -->
      <view class="blur-background" :style="{ backgroundImage: `url(${processImageUrl(videoDetail.imgUrl)})` }"></view>
      <!-- 视频封面和信息 -->
      <view class="video-header">
        <view class="video-cover">
          <page-image :src="processImageUrl(videoDetail.imgUrl)" mode="aspectFill" style="height: 160px"></page-image>
        </view>

        <view class="video-info">
          <view class="video-title">{{ videoDetail.name }}</view>
          <view class="video-state">{{ getVideoState(videoDetail.state,videoDetail.part) }}</view>

          <view class="video-meta">
            <view class="play-count"><wd-icon name="play-circle-stroke" size="18px"></wd-icon> {{ formatPlayCount(videoDetail.playCount) }}</view>
            <view class="action-buttons">
              <view class="action-button" @click="handlePlay">
                <view class="play-now">立即播放</view>
              </view>
            </view>
          </view>

          <view class="video-stats">
            <view class="stat-item" @click="handleLike">
              <wd-icon :name="videoDetail.isLike ? 'heart-filled' : 'heart'" size="24px" color="#ff5252"></wd-icon>
              <text>{{ videoDetail.likes }}</text>
            </view>
            <view class="stat-item" @click="handleSubscribe">
              <wd-icon :name="videoDetail.isSubscribe ? 'star-filled' : 'star'" size="24px" color="#ffb300"></wd-icon>
              <text>{{ videoDetail.subscribe }}</text>
            </view>
          </view>
        </view>
      </view>

      <scroll-view class="scroll-bar" scroll-y>


        <!-- 视频详情 -->
        <view class="video-description">
          <view class="section-title">分类</view>
          <view class="description-content">
            <view style="display: flex;flex-wrap: wrap" v-if="videoDetail.tags.length>0">
              <view style="margin: 5px" v-for="(item,index) in videoDetail.tags" :key="index">
                <view class="video-tag">{{ item }}</view>
              </view>
            </view>
            <view v-else>暂无分类</view>
          </view>
          <view class="section-title">导演/演员</view>
          <view class="description-content">
            <text>导演：{{ videoDetail.leader || '未知' }}</text>
            <text>演员：{{ videoDetail.actor || '未知' }}</text>
            <text>更新日期：{{ formatDate(videoDetail.publicDate) }}</text>
          </view>

          <view class="section-title">视频介绍</view>
          <view class="description-content">
            <text>{{ videoDetail.ins || '暂无介绍' }}</text>
          </view>
        </view>

        <!-- 选集 -->
        <view class="episode-section">
          <view class="section-title">选集</view>
          <view class="episode-grid">
            <view
                v-for="i in videoDetail.part"
                :key="i"
                class="episode-item"
                @click="playEpisode(i)"
            >
              {{ i }}
            </view>
          </view>
        </view>
      </scroll-view>

    </view>
  </base-page>
</template>

<script>
import { getVideoDetail, likeVideo, subscribeVideo } from '../../api/video/video.js';
import { processImageUrl } from '../../utils/imageUtils.js';
import {themeStore} from "../../store/theme";
import {formatDate} from "../../utils/dateUtils";
import {getVideoState} from "../../utils/stringUtils";

export default {
  data() {
    return {
      videoId: null,
      videoDetail: {
        id: 0,
        name: '',
        ins: '',
        state: '',
        publicDate: '',
        leader: '',
        actor: '',
        imgUrl: '',
        part: 0,
        likes: 0,
        subscribe: 0,
        week: -1,
        type: '',
        playCount: 0,
        isSubscribe: 0,
        isLike: 0
      },
      pageName:"detail",
      themeColor: '#2196f3' // 默认主题色
    }
  },
  onLoad(options) {
    if (options.id) {
      this.videoId = options.id;
      this.fetchVideoDetail();
    }
  },
  methods: {
    getVideoState,
    formatDate,
    themeStore,
    processImageUrl,

    back(){
      uni.navigateBack()
    },

    // 获取视频详情
    async fetchVideoDetail() {
      try {
        const result = await getVideoDetail(this.videoId);
        this.videoDetail = result.data.data || this.videoDetail;

        this.themeColor = '#2196f3';
      } catch (error) {
        uni.showToast({
          title: '获取视频详情失败',
          icon: 'none'
        });
      }
    },

    // 格式化播放量
    formatPlayCount(count) {
      if (!count) return '0';

      count = Number(count);
      if (count < 10000) {
        return count.toString();
      } else {
        return (count / 10000).toFixed(2) + '万';
      }
    },

    // 处理点赞
    async handleLike() {
      try {
        await likeVideo(this.videoId);
        // 更新本地状态
        this.videoDetail.isLike = this.videoDetail.isLike ? 0 : 1;
        this.videoDetail.likes += this.videoDetail.isLike ? 1 : -1;

        uni.showToast({
          title: this.videoDetail.isLike ? '点赞成功' : '取消点赞',
          icon: 'none'
        });
      } catch (error) {
        console.error('点赞操作失败:', error);
      }
    },

    // 处理收藏
    async handleSubscribe() {
      try {
        await subscribeVideo(this.videoId);
        // 更新本地状态
        this.videoDetail.isSubscribe = this.videoDetail.isSubscribe ? 0 : 1;
        this.videoDetail.subscribe += this.videoDetail.isSubscribe ? 1 : -1;

        uni.showToast({
          title: this.videoDetail.isSubscribe ? '收藏成功' : '取消收藏',
          icon: 'none'
        });
      } catch (error) {
        console.error('收藏操作失败:', error);
      }
    },

    handlePlay() {
      uni.navigateTo({
        url: "/pages/player/player?id=" + this.videoId
      });
    },
    // 播放指定集数
    playEpisode(episode) {
      // 这里可以实现跳转到播放页面的逻辑
      uni.navigateTo({
        url: "/pages/player/player?id=" + this.videoId + "&part=" + episode
      });
    }
  }
}
</script>

<style lang="scss">
.video-detail {
  --theme-color: #2196f3; // 默认主题色，会被动态覆盖
  position: relative;
  min-height: calc(100vh - 70px);
  padding-top: 70px;
  background-color: var(--primary-color);
  color: var(--text-color);

  .back-btn{
    color: white;
    left: 10px;
    top: 40px;
    position: absolute;
    font-weight: bold;
    z-index: 99
  }

  // 高斯模糊背景
  .blur-background {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 250px;
    background-size: cover;
    background-position: center;
    filter: blur(20px);
    background-color: #727272;
    opacity: 0.7;
    z-index: 0;
  }

  // 视频头部信息
  .video-header {
    position: relative;
    z-index: 1;
    display: flex;
    padding: 20px;

    .video-cover {
      width: 120px;
      height: 160px;
      border-radius: 8px;
      overflow: hidden;
      background-color: #666;

      image {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .video-info {
      flex: 1;
      margin-left: 15px;
      color: #fff;
      text-shadow: 0 1px 2px rgba(0,0,0,0.5);

      .video-title {
        font-size: 18px;
        font-weight: bold;
        margin-bottom: 5px;
      }

      .video-state {
        font-size: 14px;
        margin-bottom: 10px;
      }

      .video-meta {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 15px;

        .play-count {
          font-size: 16px;
        }

        .action-buttons {
          display: flex;

          .play-now {
            background-color: var(--theme-color);
            color: #fff;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 14px;
          }
        }
      }

      .video-stats {
        display: flex;

        .stat-item {
          display: flex;
          align-items: center;
          margin-right: 20px;

          text {
            margin-left: 5px;
            font-size: 16px;
          }
        }
      }
    }
  }

  .scroll-bar{
    height: calc(100vh - 300px);
    border-radius: 10px 10px 0 0;
    padding-top: 20px;
    background-color: var(--primary-color);

    // 视频详情
    .video-description {
      position: relative;
      z-index: 1;
      background-color: var(--primary-color);
      padding: 15px;
      margin-top: 10px;


      .section-title {
        font-size: 16px;
        font-weight: bold;
        color: var(--theme-color);
        margin-bottom: 10px;
        position: relative;
        padding-left: 10px;

        &:before {
          content: '';
          position: absolute;
          left: 0;
          top: 2px;
          height: 16px;
          width: 4px;
          background-color: var(--theme-color);
          border-radius: 2px;
        }
      }

      .description-content {
        font-size: 14px;
        line-height: 1.5;
        margin-bottom: 15px;

        text {
          display: block;
          margin-bottom: 5px;
        }
      }
    }

    // 选集
    .episode-section {
      background-color: var(--primary-color);
      padding: 15px;

      .episode-grid {
        display: flex;
        flex-wrap: wrap;
        margin: 0 -5px;

        .episode-item {
          width: calc(16.666% - 10px);
          margin: 5px;
          height: 40px;
          display: flex;
          align-items: center;
          justify-content: center;
          background-color: var(--card-bg);
          border-radius: 20px;
          font-size: 14px;

          &:active {
            background-color: var(--theme-color);
            color: #fff;
          }
        }
      }
    }
  }

  .video-tag{
    color: var(--text-color);
    padding: 2.5px 10px;
    border:solid 1px var(--text-color);
    border-radius: 20px;
  }
}
</style>
