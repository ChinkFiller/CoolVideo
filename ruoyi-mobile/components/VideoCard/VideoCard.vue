<template>
    <view class="video-item">
        <view @click="goToVideoDetail(videoData.id)">
            <page-image :src="processImageUrl(videoData.imgUrl)" class="video-img"></page-image>
            <view class="video-info">
                <text class="video-title">{{ videoData.name }}</text>
                <text class="video-desc">{{ getVideoState(videoData.state,videoData.part) }}</text>
            </view>
        </view>
    </view>
</template>

<script>
import { processImageUrl } from '../../utils/imageUtils.js'
import {getVideoState} from "../../utils/stringUtils";
import { goToVideoDetail} from "../../utils/routerUtils";

export default {
  name: 'VideoCard',
  props: {
    videoData: {
      type: Object,
      default: {}
    }
  },
  methods:{
    goToVideoDetail,
    getVideoState,
    processImageUrl,
  }
}
</script>

<style lang="scss">
.video-item {
  margin: 10px;
  width: 100px;

  .video-img{
    width: 100%;
    height: 135px;
    border-radius: 10px;
  }

  .video-info {
    padding: 5px 0;


    .video-title {
      font-size: 14px;
      display: block;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .video-desc {
      font-size: 12px;
      color: #999;
      display: block;
    }
  }
}
</style>
