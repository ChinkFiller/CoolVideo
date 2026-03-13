<template>
    <scroll-view
        scroll-y
        :style="{height:`${scrollHeight}px`}"
        class="infinite-scroll"
        :scroll-top="scrollTop"
        refresher-enabled
        :refresher-triggered="loading"
        :refresher-background="backgroundColor"
        @scrolltolower="handleLoadMore"
        @refresherrefresh="handleRefresh"
    >
      <view v-for="(item, index) in list" :key="index" class="animation-fade-500">
        <!-- 内容插槽 -->
        <slot :data="item"></slot>
      </view>


      <!-- 加载提示 -->
      <view v-if="list.length === 0 && !loading" class="empty-tip">
        <text>{{ emptyText }}</text>
      </view>

      <view v-else class="load-more">
        <text v-if="hasMore">上拉加载更多</text>
        <text v-else>已经到底了</text>
      </view>
    </scroll-view>
</template>

<script>
import {themeStore} from "../../store/theme";

export default {
  name: 'InfiniteScrollList',
  data() {
    return{
      refresherTriggered:false,
      loading: false,
      hasMore: false,
      scrollTop:0,
      list: [],
      pageNum: 1,
      pageSize: 10,
      total:0,
    }
  },
  props: {
    loadFunction:{type:Function ,required: true},
    emptyText: { type: String, default: '暂无数据' },
    scrollHeight:{type:Number,default: 0},
    backgroundColor:{type:String,default: "#fff"},
    initLoad:{type:Boolean,default: true},// 是否初始化加载数据
  },
  mounted() {
    if(this.initLoad){
      this.loadData()
    }
  },
  methods: {
    themeStore,
    async loadData(){
      try {
        const res = await this.loadFunction(this.pageNum, this.pageSize, this.searchKeyword)
        this.list=this.list.concat(res.data.rows || [])
        this.total = res.data.total || 0
        this.hasMore = this.list.length < this.total
      } catch (e) {
        // 加载新的数据失败，回退上一页
        if (this.pageNum > 0){
          this.pageNum--
        }
        uni.showToast({ title: '加载数据失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    handleLoadMore() {
      if (!this.hasMore) return
      // 加载下一页数据
      this.pageNum++
      this.loadData()
    },
    // 处理搜索
    handleSearch(keyWord){
      this.searchKeyword=keyWord
      this.pageNum=1
      // 清空数据
      this.list=[]
      this.loadData()
    },
    handleRefreshWithoutLoading(){
      // 设置为第一页
      this.pageNum=1
      // 清空数据
      this.list=[]
      // 加载数据
      this.loadData()
    },
    // 处理刷新
    handleRefresh(){
      if (this.loading) return
      this.loading=true
      // 设置为第一页
      this.pageNum=1
      // 清空数据
      this.list=[]
      // 加载数据
      this.loadData()
    },
    // 免刷新清除一个数据
    removeItem(id){
      this.list = this.list.filter(item => item.id !== id)
    }
  }
}
</script>

<style lang="scss">

.infinite-scroll {

}
.empty-tip {
  text-align: center;
  padding: 30px 0;
  color: #999;
}
.load-more {
  text-align: center;
  padding: 15px 0;
  color: #999;
  font-size: 14px;
}
</style>
