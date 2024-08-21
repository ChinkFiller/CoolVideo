<template>
	<page-meta page-style="background:#343434" v-if="is_dark"></page-meta>
	<page-meta page-style="background:#eef0f1" v-else></page-meta>
	<view class="full" v-show="is_asking" @click="is_asking=false">
		<view class="asking">
			<p style="font-size: 18px;margin-top: 5px;">提示</p>
			<br>
			<p>确认登出吗？</p>
			<br>
			<view class="option">
				<button @click="is_asking=false;">取消</button>
				<button style="color: red;" @click="quit_login()">确定</button>
			</view>
		</view>
	</view>
	<view class="top_nav">
		<image src="../../static/bg.gif" mode="top left"></image>
		<view class="mirror" style="background: linear-gradient(to top,rgba(52,52,52,1),rgba(255,255,255,0));" v-if="is_dark"></view>
		<view class="mirror" style="background: linear-gradient(to top,rgba(238,240,241,1),rgba(255,255,255,0));" v-else></view>
		<view class="user_msg">
			<view class="img_icon" @click="iconOption()">
				<image :src="user_msg.icon"></image>
			</view>
			<view class="user_title">
				<view class="user_name" @click="iconOption()">{{user_msg.name}}</view>
				<view class="tap">正式会员</view>
			</view>
		</view>
		<view class="quit" @click="is_asking=true">
			<i class="fa fa-sign-out"></i>
		</view>
		<view class="back" @click="back()">
			<i class="fa fa-angle-left"></i>
		</view>
	</view>
	<view class="main_ui">
		<view class="vip">
		    <view class="vip_text_bar">
		    	<view class="vip_main_title">会员中心</view>
			    <view class="vip_tap">无限次视频加速，番剧更新提醒</view>
		    </view>
			<button @click="vipShop()">立即购买</button>
		</view>
		<view class="download_tap">
			<i class="fa fa-flash"></i>
			<view class="download_tap_text">本月剩余视频加速次数{{0}}/{{5}}</view>
		</view>
		<view style="position: relative;top: 100px;margin-left: 15px;color:#eef0f1;" v-if="is_dark">我的追番</view>
		<view style="position: relative;top: 100px;margin-left: 15px;" v-else>我的追番</view>
		<scroll-view scroll-x="true" class="my_allow">
			<view class="my_allow_item" v-for="item,index in my_fllow" @click="go_to_player(item.id,item.img,item.title,item.state)">
				<image :src="item.img_url" mode="aspectFill"></image>
				<view class="my_allow_text" style="color: #eef0f1;" v-if="is_dark">{{item.title}}</view>
				<view class="my_allow_text" v-else>{{item.title}}</view>
			</view>
			<view v-show="my_fllow.length==0" style="width: 100%;text-align: center;">暂无追番</view>
		</scroll-view>
	</view>
</template>

<script>
	import do_request from "../../common/ajax.js"
	export default {
		onLoad() {
			this.user_msg=uni.getStorageSync("login_token");
			this.is_dark=getApp().globalData.dark
			let data=uni.getStorageSync("allow")
			let localtime=data.splice(0,1)
			do_request.do_request("/get_allow","GET",{},(res)=>{
				if (!res.error){
					 let remoteData=res.data;
					 if (parseInt(remoteData[0])>localtime){
						console.log("云端数据同步到了本地")
						uni.setStorageSync("allow",remoteData)
						remoteData.splice(0,1)
						this.my_fllow=remoteData
					 }else{
						if (parseInt(remoteData[0])<localtime){
							console.log("本地数据同步到了云端")
							do_request.do_request("/upload_allow","POST",{"data":data},(res)=>{})
						}
						this.my_fllow=data
					 }
				}else{
					this.my_fllow=data
				}
			},1000)
		},	
		data() {
			return {
				user_msg:'',
				is_asking:false,
				is_dark:false,
				my_fllow:[]
			}
		},
		methods: {
			quit_login(){
				//退出后删除登录信息
				uni.setStorageSync("login_token","");
				//退出后删除历史记录信息
				uni.setStorageSync("history",[17000]);
				//刷新追番
				uni.setStorageSync("allow",[17000]);
				uni.navigateTo({
					url:"/pages/index/index",
					animationType:'slide-in-left',
					animationDuration:200
				})
			},
			back(){
				uni.navigateBack({
					animationType:"slide-out-right",
					delta:1,
					animationDuration:200
				})
			},
			go_to_player(id,img,title,state){
				do_request.do_request('/search_data_id',"GET",{id:id},(res)=>{
					if (!res.error){
						if (res.data.img_url==""){
							res.data.img_url="../static/nodata.png"
						}
						
						uni.navigateTo({
							url:`/pages/v_player/v_player?id=${id}&img=${res.data.img_url}&title=${res.data.name}&part=${res.data.state}`,
							animationDuration:300,
							animationType:'zoom-fade-out'
						})
					}else{
						uni.navigateTo({
							url:`/pages/v_player/v_player?id=${id}&img=${img}&title=${title}&part=${state}`,
							animationDuration:300,
							animationType:'zoom-fade-out'
						})
					}
				})
			},
			vipShop(){
				uni.showToast({
					title:"功能暂未开启",
					icon:"error"
				})
			},
			iconOption(){
				uni.showActionSheet({
					itemList:["更改头像","更改昵称"],
					alertText:"取消",
					success:(res)=>{
						if (res.tapIndex==0){
							uni.chooseImage({
							    count: 1,
							    sizeType: ['compressed'], //可以指定是原图还是压缩图，默认二者都有
							    sourceType: ['album'], //从相册选择
							    success: (res) => {
									uni.uploadFile({
										url:uni.getStorageSync('serverUrl')+"/upload_icon",
										filePath:res.tempFilePaths[0],
										header:{token:this.user_msg.token},
										name:"file",
										success: (back) => {
											//console.log(back.data)
											if (JSON.parse(back.data).error=="0"){
												uni.showToast({
													title:"上传成功！",
													icon:"none"
												})
												this.user_msg.icon=JSON.parse(back.data).data.icon
												uni.setStorageSync("login_token",this.user_msg)
											}else{
												uni.showToast({
													title:"上传失败！",
													icon:"none"
												})
											}
										},
										fail() {
											uni.showToast({
												title:"上传失败！",
												icon:"none"
											})
										}
									})
							    }
							});
						}
						if (res.tapIndex==1){
							uni.showModal({
							        title: '请输入新的昵称',
							        content: '',
							        editable:true,//是否显示输入框
									placeholderText:'输入新的昵称',//输入框提示内容
							        confirmText: '确认',
							        cancelText: '取消',
							        success: (res) => {
							          if (res.confirm) {
							            do_request.do_request("/rename","GET",{name:res.content},(back)=>{
											if (!back.error){
												uni.showToast({
													title:"更改成功！",
													icon:"none"
												})
												this.user_msg.name=res.content
												uni.setStorageSync("login_token",this.user_msg)
											}else{
												uni.showToast({
													title:"更改失败!"+back.msg,
													icon:"none"
												})
											}
										})
							        }
							    } 
							});
						}
					}
				})
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
		width: 100%;
		height: 250px;
	}
	.top_nav .mirror{
		position: absolute;
		width: 100%;
		height: 80px;
		top: 170px;
	}
	.top_nav image{
		position: absolute;
		width: 100%;
		height:250px;
	}
	.user_msg{
		position: absolute;
		top: 75px;
		width: 70%;
		height: 100px;
		margin: 0 22.5% 0 2.5%;
		overflow: hidden;
		text-overflow: ellipsis;
		z-index: 1;
		color: white;
	}
	.img_icon{
		position: absolute;
		width: 25%;
		aspect-ratio: 1;
		border: solid 2px aliceblue;
		border-radius: 50%;
		overflow: hidden;
		margin: 0 0 0 0%;
		top: 50%;
		transform: translateY(-50%);
	}
	.img_icon image{
		width: 100%;
		height: 100%;
	}
	.user_title{
		position: absolute;
		left: 35%;
		top: 50%;
		transform: translateY(-50%);
		white-space: nowrap;
	}
	.tap{
		font-size: 10px;
		border-radius: 3px;
		border: solid 1px white;
		text-align: center;
		margin-top: 10px;
		width: 50px;
	}
	.user_name{
		font-size: 18px;
	}
	.quit{
		position: absolute;
		top: 35px;
		right: 5%;
		font-size: 30px;
		color: white;
	}
	.back{
		position: absolute;
		top: 35px;
		left: 5%;
		font-size: 30px;
		color: white;
	}
	.main_ui{
		width: 100%;
		height: calc(100%-250px);
	}
	.vip{
		position: absolute;
		width: 90%;
		transform: translateY(-100%);
		margin: 10px 5% 10px 5%;
		height: 60px;
		background-image: url("/static/vip-bg.jpg");
		background-repeat: no-repeat;
		background-size: cover;
		background-position: center;
		border-radius: 10px;
	}
	.vip_text_bar{
		position: absolute;
		width:100%;
		height: 80%;
		margin-left: 0%;
		top: 50%;
		transform: translateY(-50%);
	}
	.vip_main_title{
		position: absolute;
		margin-left: 10%;
		top: 10%;
		color: #000000;
	}
	.vip_tap{
		position: absolute;
		margin-left: 10%;
		bottom: 10%;
		font-size: 12px;
	}
	.vip  button{
		position: absolute;
		width: 25%;
		height: 50%;
		right: 5%;
		top: 50%;
		transform: translateY(-50%);
		font-size: 12px;
		border-radius: 10px;
	}
	.download_tap{
		position: absolute;
		width: 80%;
		left: 50%;
		transform: translateY(30%) translateX(-50%);
		display: flex;
		text-align: center;
		background-color: antiquewhite;
		padding: 3%;
		border-radius: 10px;
	}
	.download_tap i{
		font-size: 50px;
		margin-left: 5%;
	}
	.download_tap_text{
		line-height: 50px;
		margin-left: 10%;
	}
	.my_allow{
		position: relative;
		top: 100px;
		height: 130px;
		width: 100%;
		white-space: nowrap;
	}
	.my_allow_item{
		width: 30%;
		margin-right: 5%;
		margin-left: 5%;
	    height: 80px;
		margin-top: 10px;
		margin-bottom: 10px;
		background-color: antiquewhite;
		display: inline-block;
		border-radius: 10px;
	}
	.my_allow_item image{
		width: 100%;
		height: 80px;
		border-radius: 10px;
	}
	.my_allow_text{
		width: 100%;
		text-align: center;
		line-height: 20px;
		font-size: 12px;
		overflow: hidden;
		text-overflow: ellipsis;
	}
	
	
	
	
	
	
	
	
	
	
	
	.full{
		position: absolute;
		width: 100%;
		height: 100%;
		background-color: rgba(0, 0, 0, 0.6);
		z-index: 10;
	}
	.asking{
		position: absolute;
		width: 60%;
		aspect-ratio: 1.5;
		background-color: white;
		margin: 0 20% 0 20%;
		top: 50%;
		transform: translateY(-50%);
		text-align: center;
		border-radius: 10px;
	}
	.asking .option{
		position: absolute;
		bottom: 0px;
		width: 100%;
		display: flex;
		border-top-style: solid;
		border-top-width: 1px;
		border-top-color: gray;
	}
	.asking .option button{
		width: 50%;
		background-color: transparent;
	}
	button::after {
	border: none !important;
	}
</style>
