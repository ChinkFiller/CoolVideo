<template>
  <view class="dev-tool">
    <scroll-view scroll-y class="scroll-box">

      <!-- 服务器地址配置 -->
      <view class="card">
        <text class="title">服务器地址</text>
        <input v-model="serverUrl" placeholder="输入服务器地址" class="input" />
        <button type="primary" size="mini" @click="saveServerUrl">保存</button>
      </view>

      <!-- LocalStorage 数据展示 -->
      <view class="card">
        <text class="title">LocalStorage 数据</text>
        <view v-for="(item, key) in storageData" :key="key" class="storage-item">
          <text>{{ key }}: {{ stringify(item) }}</text>
          <button size="mini" @click="removeStorage(key)">删除</button>
        </view>
        <button size="mini" type="warn" @click="clearStorage">清空所有</button>
      </view>

      <!-- 模拟请求 -->
      <view class="card">
        <text class="title">模拟请求</text>
        <input v-model="reqUrl" placeholder="接口地址" class="input" />
        <textarea v-model="reqData" placeholder="请求参数(JSON)" class="textarea" />
        <button type="primary" size="mini" @click="sendRequest">发送请求</button>
        <text class="subtitle">响应结果：</text>
        <scroll-view scroll-y class="response-box">
          <text selectable>{{ response }}</text>
        </scroll-view>
      </view>

      <!-- 请求日志 -->
      <view class="card">
        <text class="title">近期请求</text>
        <button size="mini" type="warn" @click="clearRequestLogs">清空请求日志</button>
        <view v-if="requestLogs.length === 0">暂无请求记录</view>
        <view v-for="(log, index) in requestLogs" :key="index" class="log-item">
          <text class="subtitle">[{{ index+1 }}] {{ log.method }} {{ log.url }}</text>
          <text class="log-text">params: {{ stringify(log.params) }}</text>
          <text class="log-text">header: {{ stringify(log.header) }}</text>
          <text class="log-text">data: {{ stringify(log.data) }}</text>
          <text class="log-text">response: {{ stringify(log.response) }}</text>
        </view>
      </view>

      <!-- 其他调试功能 -->
      <view class="card">
        <text class="title">调试功能</text>
        <button size="mini" @click="getDeviceInfo">查看设备信息</button>
        <text v-if="deviceInfo">设备信息：{{ deviceInfo }}</text>
      </view>

    </scroll-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      serverUrl: uni.getStorageSync("SERVER_URL") || "",
      storageData: {},
      reqUrl: "",
      reqData: "",
      response: "",
      deviceInfo: "",
      requestLogs: []
    };
  },
  onShow() {
    this.loadStorage();
    this.loadRequestLogs();
  },
  methods: {
    // 保存服务器地址
    saveServerUrl() {
      uni.setStorageSync("SERVER_URL", this.serverUrl);
      uni.showToast({ title: "保存成功" });
      this.loadStorage();
    },
    // 加载所有 localStorage 数据
    loadStorage() {
      try {
        const res = uni.getStorageInfoSync();
        let data = {};
        res.keys.forEach(k => {
          if (k!=='REQUEST_LOG'){
            data[k] = uni.getStorageSync(k);
          }
        });
        this.storageData = data;
      } catch (e) {
        console.error(e);
      }
    },
    // 删除单个 storage
    removeStorage(key) {
      uni.removeStorageSync(key);
      this.loadStorage();
    },
    // 清空 storage
    clearStorage() {
      uni.clearStorageSync();
      this.loadStorage();
      uni.showToast({ title: "已清空" });
    },
    // 模拟请求
    sendRequest() {
      let data = {};
      try {
        data = this.reqData ? JSON.parse(this.reqData) : {};
      } catch (e) {
        return uni.showToast({ title: "JSON 格式错误", icon: "error" });
      }
      uni.request({
        url: this.reqUrl,
        method: "POST",
        data,
        success: res => {
          this.response = JSON.stringify(res.data, null, 2);
        },
        fail: err => {
          this.response = JSON.stringify(err, null, 2);
        }
      });
    },
    // 保存请求日志
    saveRequestLog(log) {
      let logs = uni.getStorageSync("REQUEST_LOG") || [];
      if (!Array.isArray(logs)) logs = [];
      logs.unshift(log); // 最新的放前面
      if (logs.length > 50) logs = logs.slice(0, 50); // 只保留50条
      uni.setStorageSync("REQUEST_LOG", logs);
      this.requestLogs = logs;
    },
    // 加载请求日志
    loadRequestLogs() {
      let logs = uni.getStorageSync("REQUEST_LOG").reverse() || [];
      this.requestLogs = Array.isArray(logs) ? logs : [];
    },
    // 清空请求日志
    clearRequestLogs() {
      uni.removeStorageSync("REQUEST_LOG");
      uni.setStorageSync("REQUEST_LOG",[])
      this.requestLogs = [];
      uni.showToast({ title: "请求日志已清空" });
    },
    // 获取设备信息
    getDeviceInfo() {
      uni.getSystemInfo({
        success: res => {
          this.deviceInfo = JSON.stringify(res, null, 2);
        }
      });
    },
    stringify(obj) {
      if (typeof obj === "object") {
        try {
          return JSON.stringify(obj);
        } catch (e) {
          return String(obj);
        }
      }
      return String(obj);
    }
  }
};
</script>

<style scoped>
.dev-tool {
  padding: 20rpx;
}
.scroll-box {
  height: 100vh;
}
.card {
  background: #fff;
  padding: 20rpx;
  margin-bottom: 20rpx;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05);
}
.title {
  font-weight: bold;
  margin-bottom: 10rpx;
  display: block;
}
.subtitle {
  margin-top: 10rpx;
  font-size: 26rpx;
  color: #666;
}
.input {
  border: 1rpx solid #ccc;
  padding: 10rpx;
  margin-bottom: 10rpx;
}
.textarea {
  border: 1rpx solid #ccc;
  padding: 10rpx;
  height: 150rpx;
  margin-bottom: 10rpx;
}
.response-box {
  max-height: 200rpx;
  border: 1rpx solid #ddd;
  padding: 10rpx;
  margin-top: 10rpx;
}
.storage-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6rpx;
}
.log-item {
  margin-bottom: 20rpx;
  padding: 10rpx;
  border: 1rpx solid #eee;
  border-radius: 8rpx;
  background: #fafafa;
}
.log-text {
  font-size: 24rpx;
  color: #333;
  margin-left: 10rpx;
}
</style>
