<template>
	<page-meta page-style="background:#343434" v-if="is_dark"></page-meta>
	<page-meta page-style="background:#eef0f1" v-else></page-meta>
	<popup-input ref="inputbar"></popup-input>
	<view class="top_nav"></view>
	<view style="margin-left: 15px;font-size: 20px;height: 50px;line-height: 55px;color: white;" v-if="is_dark"><i class="fa fa-angle-left" style="font-size: 35px;margin-right: 15px;transform: translateY(5px);" @click="back()"></i>系统设置</view>
	<view style="margin-left: 15px;font-size: 20px;height: 50px;line-height: 55px;" v-else><i class="fa fa-angle-left" style="font-size: 35px;margin-right: 15px;transform: translateY(5px);" @click="back()"></i>系统设置</view>
	<view style="height: 2px;background-color: #828282;width: 100%" v-if="is_dark"></view>
	<view style="height: 2px;background-color: #9a9b9c;width: 100%" v-else></view>
	<scroll-view scroll-y="true" :style="{height:`${height}px`}">
		<!-- <view v-for="item,index in settings" class="one_setting">
			<view class="title">{{item.title}}</view>
			<switch :checked="item.state" @change="item.func" />
			<hr/>
		</view> -->
		<view class="one_setting">
			<view class="title" style="color: white;" v-if="is_dark">开发模式</view>
			<view class="title" v-else>开发模式</view>
			<switch :checked="testMode" @change="changeTestMode()" />
		</view>
		<view class="one_setting" @click="changeServerUrl()" v-show="testMode">
			<view class="one_input_title" style="color: white;" v-if="is_dark">
				<view style="margin-top: 5px;font-size: 18px;margin-bottom: 5px;">服务器地址</view>
				<view>{{serverUrl}}</view>
			</view>
			<view class="one_input_title" v-else>
				<view style="margin-top: 5px;font-size: 18px;margin-bottom: 5px;">服务器地址</view>
				<view>{{serverUrl}}</view>
			</view>
			<uni-icons type="right" size="30" style="margin-left: auto;margin-right: 15px;margin-top: 15px;" v-if="is_dark" color="white"></uni-icons>
			<uni-icons type="right" size="30" style="margin-left: auto;margin-right: 15px;margin-top: 15px;" v-else></uni-icons>
		</view>
		<view class="one_setting" @click="checkUpdate()">
			<view class="title" style="color: white;" v-if="is_dark">客户端版本 {{cilentVersion}}</view>
			<view class="title" v-else>客户端版本 {{cilentVersion}}</view>
		</view>
	</scroll-view>
</template>

<script>
	import uniIcons from '../../uni_modules/uni-icons/components/uni-icons/uni-icons.vue'
	import ajax from '../../common/ajax.js'
	import popupinput from '@/components/popup-input/popup-input.vue'
	export default {
		components:{
			uniIcons,
			popupinput,
		},
		onLoad() {
			uni.getSystemInfo({
				success:(res)=>{
					this.height=res.windowHeight-90
				}
			})
			this.is_dark=getApp().globalData.dark
			this.cilentVersion='V'+getApp().globalData.version
		},
		data() {
			return {
				height:0,
				is_dark:false,
				testMode:uni.getStorageSync('testMode'),
				serverUrl:uni.getStorageSync("serverUrl"),
				cilentVersion:''
			}
		},
		methods: {
			back(){
				uni.navigateBack({
					animationType:"slide-out-right",
					delta:1,
					animationDuration:200
				})
			},
			checkUpdate(){
				ajax.do_request('/app/version','GET',{},(res)=>{
					if (!res.error){
						if (parseFloat(res.data.clientVersion)>parseFloat(getApp().globalData.version)){
							uni.showModal({
								title: '检测到新版本',
								content: `检测到新版本V${parseFloat(res.data.clientVersion)},当前版本V${parseFloat(getApp().globalData.version)}`,
								success: (res1)=> {
									if (res1.confirm) {
										plus.runtime.openURL(res.data.url);
									} 
									else {
										plus.runtime.quit()
									}
								}
							})
						}else{
							uni.showToast({
								title:'当前已是最新版',
								icon:'none'
							})
						}
					}else{
						uni.showToast({
							title: '版本校验失败',
							icon: 'error',
							duration: 2000
						})
					}
				},3000)
			},
			checkServer(url){
				uni.showLoading({
				     title: '加载中'
				});
				uni.request({
					url:url+"/app/version",
					method:"GET",
					timeout:2500,
					success:(res)=>{
						if (res.statusCode==200){
							if (parseFloat(getApp().globalData.supportServerVersion)<parseFloat(res.data.data.serverVersion)){
								uni.showToast({
									title:"客户端版本过低",
									icon:'error'
								})
								uni.hideLoading();
							}else{
								uni.setStorageSync("serverUrl",url)
								this.serverUrl=url
								uni.showToast({
									title:"修改成功！",
								})
								uni.hideLoading();
							}
						}else{
							uni.showToast({
								title:"服务器验证失败",
								icon:'error'
							})
							uni.hideLoading();
						}
					},
					fail: (res) => {
						uni.showToast({
							title:"服务器验证失败",
							icon:'error'
						})
					}
				})
			},
			changeServerUrl(){
				this.$refs.inputbar.show("服务器地址",this.serverUrl,true,(res)=>{
					if (res.state){
						if (res.content!==''&&this.serverUrl!==res.content){
							if (res.content.startsWith("http://")||res.content.startsWith("https://")){
								let data=res.content
								if (data.charAt(data.length-1)=='/'){
									data=data.slice(0,data.length-1)
								}
								this.checkServer(data)
							}else{
								uni.showToast({
									title:"服务器地址格式有误",
									icon:'error'
								})
							}
						}
					}
				})
				// uni.showModal({
				// 	title: '服务器地址',
				// 	editable:true,
				// 	content:"已认证",
				// 	placeholderText:this.serverUrl,
				// 	confirmText: '确认',
				// 	cancelText: '取消',
				// 	success:(res)=>{
				// 		if (res.confirm){

				// 		}
				// 	}
				// })
			},
			changeTestMode(){
				this.testMode=!this.testMode
				uni.setStorageSync("testMode",this.testMode)
			}
		}
	}
</script>

<style>
	@import url('../../common/font.css');
	page{
		overflow: hidden;
	}
	.top_nav{
		height: 35px;
		width: 100%;
	}
	.one_setting{
		margin-top: 5px;
		margin-bottom: 5px;
		width: 100%;
		height: 65px;
		display: flex;
		border-bottom: solid 0.5px gray;
	}
	.one_setting switch{
		margin-left: auto;
		margin-right: 15px;
		margin-top: 20px;
	}
	.one_setting .title{
		line-height: 65px;
		margin-left: 15px;
		font-size: 18px;
	}
	.one_input_title{
		height: 65px;
		margin-left: 15px;
	}

</style>
