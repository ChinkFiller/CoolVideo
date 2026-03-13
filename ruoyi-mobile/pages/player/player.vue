<template>
  <base-page :show-nav="false">
    <wd-overlay :show="showOverLay" @click="showOverLay = false">
      <view class="real-comment-box-container" @click.stop="">
        <wd-textarea
            show-word-limit
            fixed
            auto-height
            class="input"
            placeholder="发表一下你的想法"
            :maxlength="100"
            :focus="focusState"
            :cursor="0"
            v-model="commentContent"
            @blur="this.focusState=false"
        />
        <view class="function-bar">
          <wd-button class="sub-button" type="primary" @click="submitComment">发表</wd-button>
        </view>
      </view>
    </wd-overlay>

    <view class="player-container" :style="themeStore().theme">
      <view class="fake-video-box"/>

      <DomVideoPlayer
          class="fake-video-box"
          src="https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/2minute-demo.mp4"
          controls
      />



      <!-- 视频信息 -->
      <view class="video-info">
        <!-- 评论和详情 -->
        <wd-tabs v-model="tabValue" swipeable animated>
          <!--详情-->
          <wd-tab title="详情">
            <scroll-view scroll-y class="detail-container">
              <wd-collapse v-model="collapseValue">
                <wd-collapse-item :title="videoData.name" name="info">
                  <view>
                    <view class="detail-item">{{ videoData.ins }}</view>
                    <view class="detail-item">导演：{{ videoData.leader }}</view>
                    <view class="detail-item">演员：{{ videoData.actor }}</view>
                    <view class="detail-item">更新时间：{{ formatTime(videoData.publicDate) }}</view>
                    <view class="detail-item">分集：{{ getVideoState(videoData.state,videoData.part) }}</view>
                  </view>
                </wd-collapse-item>
              </wd-collapse>

              <!-- 操作按钮 -->
              <view class="action-buttons">
                <view class="action-btn" @click="handleLike">
                  <wd-icon :name="videoData.isLike ? 'heart-filled' : 'heart'" size="24px" color="#ff5252"></wd-icon>
                  <text>{{ videoData.likes || 0 }}</text>
                </view>
                <view class="action-btn" @click="handleSubscribe">
                  <wd-icon :name="videoData.isSubscribe ? 'star-filled' : 'star'" size="24px" color="#ffb300"></wd-icon>
                  <text>{{ videoData.subscribe || 0 }}</text>
                </view>
                <view class="action-btn" @click="handleShare">
                  <wd-icon name="share" :color="themeStore()" size="24px"></wd-icon>
                  <text>分享</text>
                </view>
                <view class="action-btn" @click="handleDownload">
                  <wd-icon name="download" size="24px"></wd-icon>
                  <text>下载</text>
                </view>
              </view>

              <!-- 选集 -->
              <view class="parts-container">
                <view class="parts-title">选集</view>
                <view class="episode-row">
                  <scroll-view scroll-x class="episode-scroll" show-scrollbar="false">
                    <view class="parts-list">
                      <view
                        v-for="(i,c) in videoData.part"
                        :key="i"
                        class="part-item"
                        :class="{ active: currentPart === i }"
                        @click="changePart(i)"
                      >
                        第{{ i }}集
                      </view>
                      <view class="part-item" style="width: 20px;background-color: transparent"></view>
                    </view>
                  </scroll-view>
                  <view class="more-arrow" @click="showPartPopup = true">
                    <wd-icon name="arrow-right" size="25px"></wd-icon>
                  </view>
                </view>
              </view>
            </scroll-view>
          </wd-tab>

          <wd-tab title="评论">
            <view class="comment-container">
              <!-- 评论输入框 -->
              <view class="comment-input" @click="showInput" v-show="!showOverLay">
                <view class="input">
                  发表一下你的想法
                </view>
              </view>

              <!-- 评论列表 -->
              <infinite-scroll-list
                  ref="comments"
                  class="comment-list"
                  :load-function="loadComments"
                  :empty-text="'暂无评论，快来发表第一条评论吧'"
                  :scroll-height="calcScrollHeight(390,true)"
                  :background-color="themeStore().theme['--primary-color']"
                  v-slot="{ data }"
              >
                <view class="comment-item">
                  <view class="comment-user">
                    <image class="user-avatar" :src="getServer()+data.userData.avatar" mode="aspectFill"></image>
                    <view class="user-info">
                      <view class="user-name">{{ data.userData.nickName }}</view>
                      <view class="comment-time">{{ formatTime(data.time) }}</view>
                    </view>
                  </view>
                  <view class="comment-content">{{ data.content }}</view>
                  <view class="comment-actions">
                    <view class="like-btn" @click="likeCommentClick(data.id,(res)=>{if (res) {data.isUserAgree = !data.isUserAgree;data.agrees = data.isUserAgree ? data.agrees + 1 : data.agrees - 1}})">
                      <wd-icon :name="data.isUserAgree ? 'heart-filled' : 'heart'" size="16px"></wd-icon>
                      <text>{{ data.agrees || 0 }}</text>
                    </view>
                  </view>
                </view>
              </infinite-scroll-list>
            </view>
          </wd-tab>
        </wd-tabs>


        <!-- 选集底部弹窗 -->
        <wd-popup v-model="showPartPopup" position="bottom" round>
          <view class="part-popup">
            <view class="popup-header">
              <text class="popup-title">选择分集</text>
              <wd-icon name="close" size="20px" class="popup-close" @click="showPartPopup = false" />
            </view>
            <scroll-view scroll-y class="popup-scroll">
              <wd-grid :column="4" :gutter="8" :clickable="true">
                <wd-grid-item
                  v-for="i in videoData.part"
                  :key="'grid-' + i"
                  useSlot
                  :custom-class="'episode-grid-item'"
                  @itemclick="changePart(i)"
                >
                  <view class="episode-cell" :class="{ active: currentPart === i }">
                    第{{ i }}集
                  </view>
                </wd-grid-item>
              </wd-grid>
              <view style="height: 12px;"></view>
            </scroll-view>
          </view>
        </wd-popup>
      </view>
    </view>
  </base-page>
</template>

<script>
import { getVideoInfo, getCommentList, addComment, likeComment } from '../../api/player/player';
import { likeVideo, subscribeVideo } from '../../api/video/video';
import {getServer} from "../../utils/server";
import {formatTime, getVideoState} from "../../utils/stringUtils";
import {flushHistory, syncHistory} from "../../api/history/history";
import {themeStore} from "../../store/theme";
import {calcScrollHeight} from "../../utils/clientUtils";
import DomVideoPlayer from 'uniapp-video-player'

export default {
  components: {
    DomVideoPlayer
  },
  data() {
    return {
      videoPlayer: null,
      videoId: null,
      videoData: {
        id: 0,
        name: '',
        ins: '',
        state: '',
        publicDate: '',
        leader: '',
        actor: '',
        imgUrl: '',
        part: 1,
        likes: 0,
        subscribe: 0,
        playCount: 0,
        isSubscribe: 0,
        isLike: 0
      },
      toast:null,
      isFullScreen:false,
      currentPart: 1,
      videoUrl: '',
      collapseValue: ['info'],
      tabValue: 0,
      commentContent: '',
      showPartPopup: false,
      showOverLay:false,
      focusState:false
    };
  },
  onBackPress() {
    if (this.showPartPopup){
      this.showPartPopup = false
      return true
    }else if (this.isFullScreen){
      uni.$emit("full",{})//播放器还原屏幕大小触发函数
      return true
    }else{
      return false
    }
  },
  onLoad(options) {
    uni.$on("syncFullState",res=>{//播放器还原屏幕大小触发函数
      this.isFullScreen=res.state
    })
    // 页面加载时获取参数
    this.videoId = options.id;
    if (!this.videoId) {
      uni.showToast({
        title: '视频ID不存在',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
      return;
    }
    this.loadVideoInfo(options.part);
    this.loadComments();
  },
  onUnload() {
    flushHistory(this.videoId)
  },
  methods: {
    calcScrollHeight,
    getVideoState,
    themeStore,
    formatTime,
    getServer,
    async loadVideoInfo(part) {
      try {
        const res = await getVideoInfo(this.videoId);
        if (res.data.code === 200) {
          Object.assign(this.videoData, res.data.data.videoData);
          if (part!==undefined){
            // 用户强调当前的选集
            this.currentPart = parseInt(part);
          }else{
            //用户没有强调选集
            if (res.data.data.history!==null){
              // 设置为历史记录中的选集
              this.currentPart=parseInt(res.data.data.history.part)
              setTimeout(()=>{
                uni.$emit("seek",{
                  time:res.data.data.history.watchTime
                })
              },800)
              uni.showToast({
                title: '已为您跳转到上次播放的位置',
                icon: 'none',
                duration:3000
              })
            }else{
              // 历史记录没有
              // 设置为第一集
              this.currentPart = 1
              //同步历史记录
              await syncHistory({
                vid:this.videoId,
                part:this.currentPart,
                watchTime:0
              })
            }
          }
          await this.updateVideoUrl();
        } else {
          uni.showToast({title: '获取视频信息失败', icon: 'none'});
        }
      } catch (e) {
        console.log(e.message)
        console.error('加载视频信息失败', e);
      }
    },
    async updateVideoUrl() {
      const res = await getVideoInfo(this.videoId);
      // 设置安全密钥
      const securityKey = res.data.data.securityKey
      setTimeout(()=>{
        uni.$emit("play",{
          url:`${getServer()}/player/video/play/url/${this.videoId}/${this.currentPart}/${securityKey}.mp4`,
          title:`${this.videoData.name} 第${this.currentPart}话`,
          id:this.videoId,
          part:this.currentPart,
        })//事件触发，用于播放器播放
      },500)
    },

    async loadComments() {
      return getCommentList(this.videoId);
    },
    async submitComment() {
      if (!this.commentContent.trim()) {
        return uni.showToast({title: '评论内容不能为空', icon: 'none'});
      }
      try {
        await addComment({
          content: this.commentContent,
          vid: this.videoId
        });
        uni.showToast({title: '评论成功', icon: 'success'});
        this.commentContent = '';
        this.showOverLay=false
        this.$refs.comments.handleRefreshWithoutLoading()
      } catch (e) {
        uni.showToast({title: e.message || '评论失败', icon: 'error'});
      }
    },
    async likeCommentClick(commentId,callback) {
      try {
        await likeComment(commentId);
        callback(true)
      } catch (e) {
        callback(false)
        uni.showToast({title: '点赞评论失败', icon: 'none'});
      }
    },
    async handleLike() {
      try {
        const res = await likeVideo(this.videoId);
        if (res.data.code === 200) {
          this.videoData.isLike = this.videoData.isLike ? 0 : 1;
          this.videoData.isLike ? this.videoData.likes++ : this.videoData.likes = Math.max(0, this.videoData.likes - 1);
        }
      } catch (e) {
        console.error('点赞失败', e);
      }
    },
    async handleSubscribe() {
      try {
        const res = await subscribeVideo(this.videoId);
        if (res.data.code === 200) {
          this.videoData.isSubscribe = this.videoData.isSubscribe ? 0 : 1;
          this.videoData.isSubscribe ? this.videoData.subscribe++ : this.videoData.subscribe = Math.max(0, this.videoData.subscribe - 1);
        }
      } catch (e) {
        console.error('收藏失败', e);
      }
    },
    handleShare() {
      uni.showToast({title: '功能正在开发中', icon: 'none'});
    },
    handleDownload() {
      uni.showToast({title: '功能正在开发中', icon: 'none'});
    },
    changePart(part) {
      if (this.currentPart === part) return;
      this.currentPart = part;
      if (this.showPartPopup) this.showPartPopup = false;
      // 远程同步历史记录
      syncHistory( {
        vid: this.videoId,
        part: part,
        watchTime: 0,
      })
      // 更新播放链接
      this.updateVideoUrl();
    },
    showInput(){
      this.showOverLay=true
      setTimeout(()=>{
        this.focusState=true
      },200)
    }
  }
};
</script>




<style lang="scss" scoped>
.player-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  color: var(--text-color);
  background-color: var(--bg-color);

  .fake-video-box{
    width: 100%;
    height: calc(300px + var(--status-bar-height));
  }

  .detail-container{
    height: calc(100vh - 300px - 40px);

    .video-info {
      flex: 1;
      padding: 10px;
      overflow-y: auto;
    }

    .action-buttons {
      display: flex;
      justify-content: space-around;
      padding: 15px 0;

      .action-btn {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 5px;
      }
    }

    .parts-container {
      margin: 15px;
      padding-bottom: 50px;

      .parts-title {
        font-size: 16px;
        font-weight: bold;
        margin-bottom: 10px;
      }
      .episode-row {
        display: flex;
        align-items: center;
        gap: 0;
      }
      .episode-scroll {
        width: 100%;
        white-space: nowrap;
      }
      .parts-list {
        display: flex;
        flex-direction: row;
        align-items: center;
        gap: 8px;
      }
      .part-item {
        width: 80px;
        height: 40px;
        background-color: var(--bg-color);
        border-radius: 5px;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;

        &.active {
          background-color: #007aff;
          color: #fff;
        }
      }
      .more-arrow {
        position: absolute;
        width: 50px;
        height: 40px;
        color: var(--text-color);
        background: linear-gradient(to right, transparent, var(--primary-color));
        display: flex;
        align-items: center;
        justify-content: center;
        right: 0;
      }
    }
  }


  .comment-container {
    height: calc(100vh - 300px - 40px);
    border-top: solid 1px var(--bg-color);

    .comment-input {
      z-index: 10;
      position: fixed;
      width: calc(100% - 20px);
      max-height: 80px;
      min-height: 40px;
      bottom: 0;
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 5px 10px 5px 10px;
      border-top: 2px solid var(--bg-color);
      background: var(--primary-color);

      .input {
        flex-grow: 1;
        height: 25px;
        font-size: 16px;
        border: 1px solid #ddd;
        border-radius: 18px;
        padding: 0 15px;
        color: var(--text-color);
        background: var(--bg-color);
      }
    }

    .comment-list {
      display: flex;
      flex-direction: column;
      height: calc(100vh - 380px);
      padding-bottom: 30px;


      .no-comment {
        text-align: center;
        color: #999;
        padding: 20px 0;
      }

      .comment-item {
        padding: 10px;
        margin: 20px 10px 5px 10px;
        background-color: var(--bg-color);
        border-radius: 8px;

        .comment-user {
          display: flex;
          align-items: center;
          gap: 10px;
          margin-bottom: 8px;

          .user-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
          }

          .user-info {
            .user-name {
              font-weight: bold;
            }

            .comment-time {
              font-size: 12px;
              color: #999;
            }
          }
        }

        .comment-content {
          margin-bottom: 8px;
          margin-left: 10px;
          line-height: 1.5;
        }

        .comment-actions {
          display: flex;
          justify-content: flex-end;

          .like-btn {
            display: flex;
            align-items: center;
            color: red;
            gap: 5px;
          }
        }
      }
    }
  }
}

/* 底部选集弹窗样式 */
.part-popup {
  padding: 12px 12px calc(env(safe-area-inset-bottom) + 12px) 12px;

  .popup-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 4px 4px 8px 4px;

    .popup-title {
      font-size: 16px;
      font-weight: bold;
    }
  }

  .popup-scroll {
    max-height: calc(100vh - 400px - env(safe-area-inset-bottom));
    padding: 4px 0 12px 0;
  }

  .episode-cell {
    width: 100%;
    height: 40px;
    background-color: var(--bg-color);
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;

    &.active {
      background-color: #007aff;
      color: #fff;
    }
  }
}

.real-comment-box-container{
  position: fixed;
  bottom: 0;
  width: 100%;
  background-color: var(--primary-color);
  padding-top: 15px;
  border-radius: 10px 10px 0 0;

  .input{
    width: calc(100% - 20px);
    margin: 0 10px 0 10px;

    background-color: var(--primary-color);
  }

  .function-bar {
    width:100%;
    display: flex;
    margin: 10px 0 10px 0;

    .sub-button{
      width: 15px;
      margin: 0 10px 0 auto;
    }
  }
}
</style>
