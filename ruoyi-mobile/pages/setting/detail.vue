<template>
  <base-page :nav-color="themeStore().theme['--header-bg']">
    <wd-message-box></wd-message-box>
    <template v-slot:header>
      <view class="custom-navbar">
        <wd-icon name="thin-arrow-left" @click="back"></wd-icon>

        <view class="nav-title">
          <text> {{ pageTitle }}</text>
        </view>

        <view class="nav-right"></view>
      </view>
    </template>

    <view class="content-body">
      <view class="group">
        <view
            @click="handleSetting(item)"
            class="item"
            v-for="(item, key, index) in currentConfigObj"
            :key="index"
            :class="{ 'no-border': index === Object.keys(currentConfigObj).length - 1 }"
        >
          <view class="content-left">
            <text class="title">{{ item.label }}</text>
          </view>

          <view class="content-right">
            <text v-if="isShowValue(item.type)" class="status-text">
              {{ item.value }}
            </text>
            <wd-icon v-if="isShowSetIcon(item.type)" name="arrow-right" size="24" color="#999999"/>
          </view>
        </view>
      </view>
    </view>
    <wd-popup v-model="picker.show" position="bottom" round>
      <view class="picker-toolbar">
        <view class="picker-cancel" @click="picker.cancel();picker.show=false;">取消</view>
        <view class="picker-confirm" @click="picker.confirm();picker.show=false;">确定</view>
      </view>
      <wd-picker-view :columns="picker.columns" v-model="picker.value" />
    </wd-popup>


  </base-page>
</template>

<script>
import {back} from "../../utils/routerUtils";
import {useMessage} from "../../uni_modules/wot-design-uni";
import {themeStore} from "../../store/theme";
import {getSettingDetail} from "../../utils/clientUtils";

export default {
  data() {
    return {
      message: useMessage(),
      picker:{
        show:false,
        columns:[],
        label:"",
        value:"",
        confirm:()=>{},
        cancel:()=>{}
      },
      currentConfigObj: {},
      pageTitle: ''
    };
  },
  onLoad(options) {
    // 1. 接收 URL 参数
    this.currentKey = options.configKey;
    this.pageTitle = options.title;

    // 2. 读取数据
    this.initData();
  },
  methods: {
    themeStore,
    back,
    reset(){
      this.picker = {
        show:false,
        columns:[],
        label:"",
        value:"",
        confirm:()=>{},
        cancel:()=>{}
      }
    },
    initData() {
      try {
        // 获取当前 Key 对应的数据
        this.currentConfigObj = getSettingDetail(this.currentKey) || {};
        Object.keys(this.currentConfigObj).forEach(key=>{
          try {
            this.currentConfigObj[key].init()
          }catch (e){}
        })
      } catch (e) {
        console.error(e);
      }
    },
    async handleSetting(item){
      let baseOptions = {
        messageBox:this.message,
        value:""
      }
      switch (item.type){
        case "text-edit":{
          this.message.prompt({
                title: item.title,
                inputValue: item.value,
                inputPattern: item.pattern
          }).then(res=>{
            baseOptions.value=res.value
            item.setter(baseOptions)
          })
          break;
        }
        case "select":{
          this.picker.show=true
          // 设置标题
          this.picker.label=item.title
          // 设置选项
          this.picker.columns=item.selectOptions
          // 设置默认值
          this.picker.value=item.value

          // 确认触发
          this.picker.confirm= ()=>{
            baseOptions.value=this.picker.value
            item.setter(baseOptions)
            this.reset()
          }

          this.picker.cancel=()=>{
            this.reset()
          }

          break;
        }
        case "button":{
          item.click(baseOptions)

          break;
        }
        default:{
          break;
        }
      }

    },
    isShowSetIcon(type){
      const list={
        "text-edit":true,
        "select":true,
        "text":false,
        "button":true
      }
      return list[type]
    },
    isShowValue(type){
      const list={
        "text-edit":true,
        "select":true,
        "text":true,
        "button":true
      }
      return list[type]
    }
  }
};
</script>

<style lang="scss" scoped>

/* --- 自定义导航栏样式 --- */
.custom-navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 44px; /* 标准导航栏高度 */
  color: var(--text-color);
  background-color: var(--header-bg); /* 与背景一致 */
  padding: 0 16px;

  .nav-left {
    width: 60rpx; /* 增加点击区域 */
    height: 100%;
    display: flex;
    align-items: center;
  }

  .nav-title {
    font-size: 34rpx;
    font-weight: 500;
    color: var(--text-color);
  }

  .nav-right {
    width: 60rpx; /* 占位，保持标题居中 */
  }
}

/* --- 列表内容样式 --- */
.content-body {
  padding-top: 59px;
  padding-bottom: 20rpx;
}

.group {
  background-color: var(--header-bg);
  margin-bottom: 24rpx;
}

.item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 30rpx;
  position: relative;

  /* 分割线 */
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 30rpx;
    right: 0;
    height: 1px;
    background-color: #2c2c2e;
    transform: scaleY(0.5);
  }

  &.no-border::after {
    display: none;
  }
}

.content-left {
  display: flex;
  flex-direction: column;

  .title {
    font-size: 30rpx;
    color: var(--text-color);
  }

  .subtitle {
    font-size: 24rpx;
    color: #8e8e93;
    margin-top: 8rpx;
  }
}

.content-right {
  display: flex;
  align-items: center;

  .status-text {
    font-size: 28rpx;
    color: var(--text-color);
    margin-right: 10rpx;
  }

  .arrow {
    font-size: 32rpx;
    color: #58585e;
    margin-left: 10rpx;
    font-family: monospace;
  }
}
.picker-toolbar{
  display: flex;
  width: 100%;
  font-size: 40rpx;
  margin-top: 10px;
  border-radius: 10px;


  .picker-cancel{
    margin: 5px auto 5px 20px;
    color: var(--text-color);
  }
  .picker-confirm{
    margin: 5px 20px 5px auto;
    color: #0e8ef3;
  }
}
</style>
