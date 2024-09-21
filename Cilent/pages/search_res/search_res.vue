<template>
	<page-meta page-style="background:#343434" v-if="is_dark"></page-meta>
	<page-meta page-style="background:#eef0f1" v-else></page-meta>
	<view class="top_span"></view>
	<view class="search-bar" v-if="is_dark">
		<input type="text" v-model="keyword" @confirm="research()" placeholder="搜索你想看的番剧" style="background-color: #6b6b6b;"/>
	</view>
	<view class="search-bar" v-else>
		<input type="text" v-model="keyword" @confirm="research()" placeholder="搜索你想看的番剧" style="background-color: #dddddd;"/>
	</view>
	<scroll-view scroll-y="true" style="width: 100%;" :style="{height:`${main_hight}px`}" refresher-enabled="true" @refresherrefresh="onRefuresh()" :refresher-triggered="triggered" refresher-background="rgba(0,0,0,0)">
		<view v-if="search_data.length==0&&is_dark" style="text-align: center;width: 100%;color: #dadada;">
			啥都木有 (T_T)
		</view>
		<view v-if="search_data.length==0&&!is_dark" style="text-align: center;width: 100%;">
			啥都木有 (T_T)
		</view>
		<view class="shop-list">
			<view class="shop-list-item" v-for="item,index in search_data" @click="go_to_player(item.id,item.img_url,item.name,item.state) " style="background-color: #4c4c4c;" v-if="is_dark">
				<view class="img_show">
					<my-img :src="item.img_url" style="width: 100%;aspect-ratio:0.75;"></my-img>
					<view class="shop-list-item-tag">&nbsp;&nbsp;&nbsp;{{item.state}}</view>
				</view>
				<view class="shop-list-item-title" style="color: white;">{{item.name}}</view>
			</view>
			<view class="shop-list-item" v-for="item,index in search_data" @click="go_to_player(item.id,item.img_url,item.name,item.state) " style="background-color: #dadada;" v-else>
				<view class="img_show">
					<my-img :src="item.img_url" style="width: 100%;aspect-ratio:0.75;"></my-img>
					<view class="shop-list-item-tag">&nbsp;&nbsp;&nbsp;{{item.state}}</view>
				</view>
				<view class="shop-list-item-title">{{item.name}}</view>
			</view>
		</view>
	</scroll-view>
</template>

<script>
import ajax from '../../common/ajax'
import myimg from '@/components/my-img/my-img.vue'
	export default {
		components:{
			myimg
		},
		onLoad(e){
			this.is_dark=getApp().globalData.dark
			uni.getSystemInfo({
				success:(res) =>{
					this.main_hight=res.windowHeight-80
				},
			})
			this.keyword=e.key
			this.lastKey=e.key
			ajax.do_request("/search_data","GET",{key:e.key},(res)=>{
				if (!res.error){
					for (var i=0;i<res.data.length;i++){
						setTimeout((data)=>{
							this.search_data.push(data)
						},150*(i+1),res.data[i])
					}
				}else{
					uni.showToast({
						title:"搜索失败",
						icon:'error'
					})
				}
			})
		},
		data() {
			return {
				search_data:[],
				is_dark:false,
				main_hight:0,
				keyword:"",
				triggered:false,
				lastKey:""
			} 
		},
		methods: {
			go_to_player(url,img,title,state){
				uni.navigateTo({
					url:"/pages/v_player/v_player?id="+url+'&img='+img+'&title='+title+'&part='+state,
					animationDuration:300,
					animationType:'slide-in-right'
				})
			},
			research(){
				this.search_data=[]
				this.lastKey=this.keyword
				ajax.do_request("/search_data","GET",{key:this.keyword},(res)=>{
					if (!res.error){
						for (var i=0;i<res.data.length;i++){
							setTimeout((data)=>{
								this.search_data.push(data)
							},150*(i+1),res.data[i])
						}
					}else{
						uni.showToast({
							title:"搜索失败",
							icon:'error'
						})
					}
				})
			},
			onRefuresh(){
				this.triggered=true
				this.search_data=[]
				ajax.do_request("/search_data","GET",{key:this.lastKey},(res)=>{
					if (!res.error){
						this.triggered=false
						for (var i=0;i<res.data.length;i++){
							setTimeout((data)=>{
								this.search_data.push(data)
							},150*(i+1),res.data[i])
						}
					}else{
						uni.showToast({
							title:"搜索失败",
							icon:'error'
						})
						this.triggered=false
					}
				})
			}
		}
	}
</script>

<style>
	page{
		overflow: hidden;
	}
	@keyframes loadin {
		100%{
			transform: scale(1,1);
			filter: opacity(100%);
		}
		0%{
			transform: scale(1.4,1.4);
			filter: opacity(0%);
		}
	}
	
	.search-bar{
		width: 100%;
		height: 45px;
		display: flex;
	}
	.search-bar input{
		width: 90%;
		margin:auto;
		height: 30px;
		border-radius: 15px;
		text-indent: 2em;
	}
	
	.top_span{
		background-color: transparent; 
		height: 35px;
	}
	.shop-list{
		width: calc(100%-10px);
		margin: 5px;
		display: flex;
		flex-wrap: wrap;
		justify-content: flex-start;
		
	}
	.shop-list-item{
		width: 47.5%;
		margin-left: 1.25%;
		margin-right: 1.25%;
		margin-top: 5px;
		margin-bottom: 5px;
		border-radius: 10px;
		animation-name: loadin;
		animation-duration: 0.4s; 
		animation-timing-function: ease-in;
		animation-iteration-count: 1;
	}
	.img_show{
		border-top-right-radius: 10px;
		border-top-left-radius: 10px;
	}
	.shop-list-item-title{
		position: relative;
		top: -20px;
		font-size: 14px;
		margin-left: 10px;
	}
	.shop-list-item-tag{
		position: relative;
		width: 100%;
		top: -20px;
		color: white;
		z-index: 1;
		font-size: 12px;
		background: linear-gradient(rgba(255,255,255,0),rgba(0, 0, 0, 1));
	}
</style>
