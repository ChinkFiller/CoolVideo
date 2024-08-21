<template>
	<page-meta page-style="background:#343434" v-if="is_dark"></page-meta>
	<page-meta page-style="background:#eef0f1" v-else></page-meta>
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
		<view class="one_setting">
			<view class="title" style="color: white;" v-if="is_dark">客户端版本 {{cilentVersion}}</view>
			<view class="title" v-else>客户端版本 {{cilentVersion}}</view>
		</view>
	</scroll-view>
</template>

<script>
	import uniIcons from '../../uni_modules/uni-icons/components/uni-icons/uni-icons.vue'
	import ajax from '../../common/ajax.js'
	export default {
		components:{
			uniIcons 
		},
		onLoad() {
			uni.getSystemInfo({
				success:(res)=>{
					this.height=res.windowHeight-90
				}
			})
			this.is_dark=getApp().globalData.dark
		},
		data() {
			return {
				height:0,
				is_dark:false,
				testMode:uni.getStorageSync('testMode'),
				serverUrl:uni.getStorageSync("serverUrl"),
				cilentVersion:"V1.26 Build by Conyafer"
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
			changeServerUrl(){
				uni.showModal({
					title: '服务器地址',
					editable:true,
					content:"已认证",
					placeholderText:this.serverUrl,
					confirmText: '确认',
					cancelText: '取消',
					success:(res)=>{
						if (res.confirm){
							if (!(res.content=='')){
								//console.log(res.content)
								if (res.content.startsWith("http://")||res.content.startsWith("https://")){
									let data=res.content
									if (data.charAt(data.length-1)=='/'){
										data=data.slice(0,data.length-1)
									}
									uni.request({
										url:data+"/get_weekdata",
										method:"GET",
										timeout:2000,
										success:(res)=>{
											if (res.statusCode==200){
												uni.setStorageSync("serverUrl",data)
												this.serverUrl=data
												uni.showToast({
													title:"修改成功！",
												})
											}else{
												uni.showToast({
													title:"服务器验证失败",
													icon:'error'
												})
											}
										},
										fail: (res) => {
											uni.showToast({
												title:"服务器验证失败",
												icon:'error'
											})
										}
									})
								}else{
									uni.showToast({
										title:"服务器地址格式有误",
										icon:'error'
									})
								}
							}
						}
					}
				})
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
