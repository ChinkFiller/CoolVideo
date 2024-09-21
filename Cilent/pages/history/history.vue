<template>
	<page-meta page-style="background:#343434" v-if="is_dark"></page-meta>
	<page-meta page-style="background:#eef0f1" v-else></page-meta>
	<popup-asking ref="ask"></popup-asking>
	<view class="top_span"></view>
	<view style="margin-left: 10px;font-size: 18px;color: white;margin-bottom: 5px;" v-if="is_dark"><i class="fa fa-angle-left" style="font-size: 35px;margin-right: 15px;transform: translateY(5px);" @click="back()"></i>观看历史</view>
	<view style="margin-left: 10px;font-size: 18px;margin-bottom: 5px;" v-else><i class="fa fa-angle-left" style="font-size: 35px;margin-right: 15px;transform: translateY(5px);" @click="back()"></i>观看历史</view>
	<view style="height: 2px;background-color: #828282;width: 100%" v-if="is_dark"></view>
	<view style="height: 2px;background-color: #9a9b9c;width: 100%" v-else></view>
	<scroll-view scroll-y="true" style="width: 100%;" :style="{height:`${main_hight}px`}">
		<view class="other_video" v-for="item,index in his_data" @click="go_to_player(item.id,item.img_url,item.title,item.full)">
			<my-img class="img" :src="item.img_url" mode="aspectFill"></my-img>
			<view class="intruduce" v-if="is_dark">
				<view style="color: white;" class="text_bar">
					<!-- <view class="other_video_title" v-if="item.title.length>14" style="animation: move_text 5s 0.5s infinite linear;">{{item.title}}</view> -->
					<view class="other_video_title" style="text-overflow: ellipsis;overflow: hidden;">{{item.title}}</view>
					<span class="part">已看到 {{item.state}}</span>
				</view>
				<view class="delete" @click="test(item.id)" style="color: white;"><i class="fa fa-trash-o"></i></view>
			</view>
			<view class="intruduce" v-else>
				<view class="text_bar">
					<!-- <view class="other_video_title" v-if="item.title.length>14" style="animation: move_text 5s 0.5s infinite linear;">{{item.title}}</view> -->
					<view class="other_video_title" style="text-overflow: ellipsis;overflow: hidden;">{{item.title}}</view>
					<span class="part">已看到 {{item.state}} </span>
				</view>
				<view class="delete" @click="test(item.id)"><i class="fa fa-trash-o"></i></view>
			</view>
		</view>
		<p v-if="his_data.length==0 && is_dark" style="text-align: center;color: white;">暂无记录</p>
		<p v-if="his_data.length==0 && !is_dark" style="text-align: center;">暂无记录</p>
	</scroll-view>
</template>

<script>
	import ajax from '../../common/ajax';
	import popupasking from '@/components/popup-asking/popup-asking.vue'
	import myimg from '@/components/my-img/my-img.vue'
	export default {
		components:{
			popupasking,
			myimg
		},
		onShow(){
			var data=uni.getStorageSync('history')
			var timeset=data.splice(0,1)[0]
			if (uni.getStorageSync('login_token')==''){
				let redata=[]
				//console.log(data)
				for (var x=data.length-1;x>=0;x--){
					redata.push(data[x])
				}
				this.his_data=redata
			}
			ajax.do_request("/get_history","GET",{},(res)=>{
				if (!res.error){
					//判断一下本地的历史记录记录时间和云端的时间，哪个新用哪个
					if (timeset>parseInt(res.data[0])){//使用本地记录 
						let redata=[]
						for (var x=data.length-1;x>=0;x--){
							redata.push(data[x])
						}
						//本地数据同步云端
						console.log('云端同步了本地数据')
						this.rememberHistoryToCloud(data)
						this.his_data=redata
					}else{//使用云端记录
					    if (timeset!=parseInt(res.data[0])){
							console.log('本地数据同步了云端') 
							data=res.data
							uni.setStorageSync("history",res.data)
							data.splice(0,1)
						}
						let redata=[]
						for (var x=data.length-1;x>=0;x--){ 
							redata.push(data[x])
						}
						this.his_data=redata
					}
				}else{
					if (!uni.getStorageSync('login_token')==''){
						let redata=[]
						for (var x=data.length-1;x>=0;x--){
							redata.push(data[x])
						}
						this.his_data=redata
					}
					
				}
			})
		},
		onLoad(){
			this.is_dark=getApp().globalData.dark
			uni.getSystemInfo({
				success:(res) =>{
					this.main_hight=res.windowHeight-70
				},
			})
		},
		data() {
			return {
				his_data:[],
				is_dark:false,
				can_jump:true,
				main_hight:0,
				now_index:0,
				is_asking:false,
				now_id:0,
			} 
		},
		methods: {
			go_to_player(id,img,title,state){
				ajax.do_request('/search_data_id',"GET",{id:id},(res)=>{
					if (!res.error){
						if (res.data.img_url==""){
							res.data.img_url="../static/nodata.png"
						}
						if (this.can_jump){
							uni.navigateTo({
								url:`/pages/v_player/v_player?id=${id}&img=${res.data.img_url}&title=${res.data.name}&part=${res.data.state}`,
								animationDuration:300,
								animationType:'slide-in-right'
							})
						}
					}else{
						if (this.can_jump){
							uni.navigateTo({
								url:`/pages/v_player/v_player?id=${id}&img=${img}&title=${title}&part=${state}`,
								animationDuration:300,
								animationType:'slide-in-right'
							})
						}
					}
				})
			},
			rememberHistory(data){
				//头部插入时间戳
				data.unshift(parseInt(new Date().getTime()/1000))
				//保存本地
				uni.setStorageSync("history",data)
				//删除头部时间戳
				data.splice(0,1)
				ajax.do_request("/upload_history","POST",{"data":data},(res)=>{
					
				})
			},
			delete_data(){
			   var tem_data=uni.getStorageSync('history')
			   for (var i=0;i<tem_data.length;i++){
				   if (tem_data[i].id==this.now_id){
					   tem_data.splice(i,1)
					   break;
				   }
			   }
			   tem_data.splice(0,1)
			   let redata=[]
			   for (var x=tem_data.length-1;x>=0;x--){
			   	redata.push(tem_data[x])
			   }
			   this.his_data=redata
			   //同步云端数据
			   this.rememberHistory(tem_data)
			   this.can_jump=true
			   uni.showToast({
			   	icon:'none',
				title:"删除成功!"
			   })
			},
			test(id){
				this.now_id=id
				this.can_jump=false
				this.$refs.ask.show('删除历史','是否删除这个历史记录?',true,(res)=>{
					if (res.state){
						this.delete_data()
					}
				})
			},
			back(){
				uni.navigateBack({
					animationType:"slide-out-right",
					delta:1,
					animationDuration:200
				})
			},
			getTime(){
				return new Date().getTime()/1000
			},
			rememberHistoryToCloud(data){
				ajax.do_request("/upload_history","POST",{"data":data},(res)=>{
					
				})
			},
		}
	}
</script>

<style>
	@import url('../../common/font.css');
	page{
		overflow: hidden;
	}
	
	@keyframes move_text {
		0%{transform: translateX(0%);}
		100%{transform: translateX(-100%);}
	}
	
	.top_span{
		background-color: transparent; 
		height: 35px;
	}
	.other_video{
		width: 93%;
		margin-left: 3%;
		margin-right: 3%;
		height: 100px;
		margin-top: 15px;
		margin-bottom: 5px;
		border-bottom: solid 0px gray;
		display: flex;
		flex-wrap: nowrap;
	}
	.img{
		width: 30%;
		height: 90px;
     	border-radius: 10px;
		margin-top: 2.5px;
		margin-bottom: 5px;
		margin-left: 1.5%;
		margin-right: 1.5%;
	}
	.intruduce{
		width: 67%;
		height: 100px;
		float: right;
	}
	.other_video_title{
		white-space: nowrap;
	}
	.text_bar{
		margin-left: 10px;
		margin-top: 10px;
		width: 80%;
		overflow: hidden;
	}
	.delete{
		position: relative;
        left: 85%;
		font-size: 20px;
		bottom: 10px;
	}
</style>
