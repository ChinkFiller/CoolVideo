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
            @search="handleSearch"
            @clear="handleSearchClear"
        ></wd-search>
      </view>

      <infinite-scroll-list
          ref="history"
          :load-function="getUserFavorites"
          :emptyText="'暂无历史记录'"
          :scroll-height="calcScrollHeight(50,true)"
          :background-color="themeStore().theme['--primary-color']"
          v-slot="{ data }"
      >
        <view class="favorite-item" @click="goToVideoPlayer(data.id)" v-if="data.name!==null">
          <page-image class="video-img" :src="processImageUrl(data.imgUrl)" />
          <view class="info-container">
            <view class="watch-info">
              <text class="video-text">{{ data.name }}</text>
              <text class="time-text" style="width: 100%">更新时间: {{ formatDate(data.publicDate) }}</text>
              <text class="part-text">{{ getVideoState(data.state, data.part) }}</text>
            </view>
            <view class="delete-btn" @click.stop="confirmDelete(data.id)">
              <wd-icon name="delete" size="20" />
            </view>
          </view>
        </view>
        <view v-else>
          <page-image class="video-img" :show-error="true" />
          <view class="info-container">
            <view class="watch-info">
              <text class="video-text">已失效的视频</text>
            </view>
            <view class="delete-btn" @click.stop="confirmDelete(data.id)">
              <wd-icon name="delete" size="20" />
            </view>
          </view>
        </view>
      </infinite-scroll-list>

    </view>

  </base-page>
</template>

<script>
import { themeStore } from '@/store/theme'
import { processImageUrl } from '@/utils/imageUtils'
import { useMessage } from '@/uni_modules/wot-design-uni'
import {getVideoState} from "../../utils/stringUtils";
import {formatDate} from "../../utils/dateUtils";
import {goToVideoPlayer} from "../../utils/routerUtils";
import {calcScrollHeight} from "../../utils/clientUtils";
import {deleteFavorite, getUserFavorites} from "../../api/favorite/favorite";

export default {
  data() {
    return {
      // store & 组件实例
      theme: themeStore(),
      message: useMessage(),

      // 搜索状态
      searchKeyword: '',
    }
  },

  onLoad() {

  },

  methods: {
    goToVideoPlayer,
    getVideoState,
    getUserFavorites,
    calcScrollHeight,
    formatDate,
    processImageUrl,
    themeStore,


    handleSearchClear(){
      this.$refs.history.handleSearch("")
    },
    handleSearch(){
      this.$refs.history.handleSearch(this.searchKeyword)
    },
    confirmDelete(id) {
      this.message
          .confirm({
            title: '提示',
            msg: '确认删除该历史记录？'
          })
          .then(() => this.handleDelete(id))
    },

    async handleDelete(id) {
      try {
          await deleteFavorite(id)
          this.$refs.history.removeItem(id)
          uni.showToast({ title: '删除收藏成功', icon: 'none' })
      } catch (e) {
        uni.showToast({ title: '删除收藏失败', icon: 'none' })
      }
    },

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

.favorite-item {
  display: flex;
  background-color: var(--primary-color);
  border-radius: 8px;
  padding: 10px;
  position: relative;
  margin: 10px 10px 0 10px;

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
      margin-top: 5px;
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
