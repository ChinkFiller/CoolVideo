<template>
	
	<!-- 背景颜色设置，根据是否为暗色模式来设置 -->
	<page-meta page-style="background:#343434" v-if="is_dark"></page-meta>
	<page-meta page-style="background:#eef0f1" v-else></page-meta>
	<popup-asking ref="version"></popup-asking>
	<popup-notice ref="notice"></popup-notice>
	<!-- 主页的html结构 -->
	<view class="nav" :style="{background:now_nav_color}">
		<uni-icons type="bars" color="#5e6d82" size="30" style="margin-top: 35px;margin-right: 5px;margin-left: 5px;" @click="show_child_page()"></uni-icons>
		<input type="text" v-model="keyword" @focus="placehold_switch='hidden'" @blur="show_or_not()" @confirm="search_data(keyword)" :focus="search_bar_focus"/>
		<uni-icons custom-prefix="iconfont" type="icon-baitianheitian" color="#5e6d82" size="30" style="margin-top: 35px;margin-left: 5px;margin-right: 5px;" @click="switch_to_day()"></uni-icons>
		<span :style="{visibility:placehold_switch}" @click="search_bar_focus=true"><i class="fa fa-search"></i>输入你想搜索的番剧</span>
	</view>
	<scroll-view scroll-y="true" :style="{height:`${main_page_height}px`}"  @scroll="scroll_position" refresher-enabled="true" @refresherrefresh="onRefresh" :refresher-triggered="triggered" @scrolltolower="end_load()" refresher-background="rgba(0,0,0,0)" v-show='page_index==0' :scroll-top="controlPage">
		<!-- banner，后期根据接口修改数据 -->
		<view class="shop_stream_img">
			<swiper autoplay="true" interval="3000" duration="1000" circular="true" @change="change_now_img" style="width: 100%;" id="show">
				<swiper-item v-for="item,index in swiper_data">
					<view @click="go_to_web(item.url)" v-if="item.type=='web'">
						<view class="swiper_title">{{item.title}}</view>
						<image :src="item.img" style="border-radius: 10px;width: 90%;margin-left: 5%;margin-right: 5%;" mode="aspectFill" @error="error_img_handle('swiper_data',index)"></image>
					</view>
					<view @click="go_to_player(item.id,item.img_url,item.name,item.state)" v-if="item.type=='video'">
						<view class="swiper_title">{{item.name}}</view>
						<image :src="item.img_url" style="border-radius: 10px;width: 90%;margin-left: 5%;margin-right: 5%;" mode="aspectFill" @error="error_img_handle('swiper_data',index)"></image>
					</view>
				</swiper-item>
			</swiper>
		</view>
		
		<view class="nav_bg" :style="{background:now_bg_color}"></view>
		<!-- 功能框，后期根据接口数据修改 -->
		<view class="fast_function">
			<view class="fast_function-item" v-for="item,index in fast_function_data" @click="go_to_web(item.href)">
				<image :src="item.img"></image>
				<br />
				<span style="color: white;" v-if="is_dark">{{item.title}}</span>
				<span v-else>{{item.title}}</span>
			</view>
		</view>
		<view style="margin-left: 20px;color: white;" v-if="is_dark">番剧推荐</view>
		<view style="margin-left: 20px;" v-else>番剧推荐</view>
		<view class="shop-list">
			<view class="shop-list-item" v-for="item,index in item_data" @click="go_to_player(item.id,item.img,item.title,item.tag) " style="background-color: #4c4c4c;color: white;" :animation="item.animation" v-if="is_dark">
				<view class="img_show">
					<my-img :src="item.img" style="width: 100%;aspect-ratio: 0.75;"></my-img>
					<view class="shop-list-item-tag">&nbsp;&nbsp;&nbsp;{{item.tag}}</view>
				</view>
				<view class="shop-list-item-title">{{item.title}}</view>
			</view>
			<view class="shop-list-item" v-for="item,index in item_data" @click="go_to_player(item.id,item.img,item.title,item.tag) " style="background-color: #dadada;" :animation="item.animation" v-else>
				<view class="img_show">
					<my-img :src="item.img" style="width: 100%;aspect-ratio: 0.75;"></my-img>
					<view class="shop-list-item-tag">&nbsp;&nbsp;&nbsp;{{item.tag}}</view>
				</view>
				<view class="shop-list-item-title">{{item.title}}</view>
			</view>
		</view>
	</scroll-view>
	<view class="footer" v-if="is_dark" style="color: white;">
		<view class="footer-item" hover-class="to_small" hover-start-time="0" hover-stay-time="150" @click="back_to_top();page_index=0">
			<uni-icons type="home-filled" size="24" v-if="page_index==0" color="white"></uni-icons>
			<uni-icons type="home" size="24" v-else color="white"></uni-icons>
			<br />
			首页
		</view>
		<view class="footer-item" hover-class="to_small" hover-start-time="0" hover-stay-time="150" @click="page_index=1;now_nav_color=bg_color[1];get_time_update()">
			<uni-icons type="calendar-filled" size="24" color="white" v-if="page_index==1"></uni-icons>
			<uni-icons type="calendar" size="24" color="white" v-else></uni-icons>
			<br />
			周期表
		</view>
	</view>
	<view class="footer" v-else>
		<view class="footer-item" hover-class="to_small" hover-start-time="0" hover-stay-time="150" @click="back_to_top();page_index=0;">
			<uni-icons type="home-filled" size="24" v-if="page_index==0"></uni-icons>
			<uni-icons type="home" size="24" v-else></uni-icons>
			<br />
			首页
		</view>
		<view class="footer-item" hover-class="to_small" hover-start-time="0" hover-stay-time="150" @click="page_index=1;now_nav_color=bg_color[1];get_time_update()">
			<uni-icons type="calendar-filled" size="24" v-if="page_index==1"></uni-icons>
			<uni-icons type="calendar" size="24" v-else></uni-icons>
			<br />
			周期表
		</view>
	</view>
	<view class="left_bar" :style="{left:`${child_page_position}px`,transition:`all ${!isMoving*0.4}s ease`}" style="background-color: #444444;" @touchmove="move" @touchstart="start_move" @touchend="end" v-if='is_dark'>
		<view style="height: 20%;width: 100%;background-color: #636363;background-image: url('../../static/bgico_white.png');" class="person_bg">
			<view class="user_msg">
				<view class="person_icon">
					<image :src="login_msg.icon" v-if="login_msg" @click="go_to('/pages/person/person')"></image>
					<image src="../../static/user.png" style="margin-top: 5px;" @click="go_to('/pages/login/login')" v-else></image>
				</view>
				<view class="username" style="color: white" v-if="login_msg">{{login_msg.name}}</view>
				<view class="username" style="color: white;" v-else>未登录</view>
			</view>
		</view>
		<view class="function_bar">
			<view class="one_bar_tip" @click="go_to('/pages/history/history')" style="color: white;"><i class="fa fa-clock-o" ></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;历史记录</view>
			<view class="one_bar_tip" style="color: white;"><i class="fa fa-download"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;下载管理</view>
			<view class="one_bar_tip" style="color: white;" @click="go_to('/pages/setting/setting')"><i class="fa fa-cog"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;设置</view>
		</view>
	</view>
	<view class="left_bar" :style="{left:`${child_page_position}px`,transition:`all ${!isMoving*0.4}s ease`}" style="background-color: #f5f5f5;" @touchmove="move" @touchstart="start_move" @touchend="end" v-else>
		<view style="height: 20%;width: 100%;background-color: #e6e6e6;" class="person_bg">
			<view class="user_msg">
				<view class="person_icon" >
					<image :src="login_msg.icon" v-if="login_msg" @click="go_to('/pages/person/person')"></image>
					<image style="margin-top: 5px;" src="../../static/user.png" @click="go_to('/pages/login/login')" v-else></image>
				</view>
				<view class="username" v-if="login_msg">{{login_msg.name}}</view>
				<view class="username" v-else>未登录</view>
			</view>
		</view>
		<view class="function_bar">
			<view class="one_bar_tip" @click="go_to('/pages/history/history')"><i class="fa fa-clock-o"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;历史记录</view>
			<view class="one_bar_tip"><i class="fa fa-download"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;下载管理</view>
			<view class="one_bar_tip"  @click="go_to('/pages/setting/setting')"><i class="fa fa-cog"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;设置</view>
		</view>
	</view>
	<view style="position:absolute;width: 100%;height: 100%;background:rgba(0,0,0,0.5);top: 0px;z-index: 1;transition: all 0.5s;" v-if="is_show" @click="hide_child_page()"></view>
	
	<!-- 周期表的界面 -->
	<view class="choose_day" v-show="page_index==1">
		<view class="buttom_line" :style="{transform:`translateX(${15+137*(date_update_index)}%)`}"></view>
		<view class="date_choose" v-if="is_dark" style="color: white;" @click="date_update_index=0">周一</view>
		<view class="date_choose" v-if="is_dark" style="color: white;" @click="date_update_index=1">周二</view>
		<view class="date_choose" v-if="is_dark" style="color: white;" @click="date_update_index=2">周三</view>
		<view class="date_choose" v-if="is_dark" style="color: white;" @click="date_update_index=3">周四</view>
		<view class="date_choose" v-if="is_dark" style="color: white;" @click="date_update_index=4">周五</view>
		<view class="date_choose" v-if="is_dark" style="color: white;" @click="date_update_index=5">周六</view>
		<view class="date_choose" v-if="is_dark" style="color: white;" @click="date_update_index=6">周日</view>
		<view class="date_choose" v-if="!is_dark" @click="date_update_index=0">周一</view>
		<view class="date_choose" v-if="!is_dark" @click="date_update_index=1">周二</view>
		<view class="date_choose" v-if="!is_dark" @click="date_update_index=2">周三</view>
		<view class="date_choose" v-if="!is_dark" @click="date_update_index=3">周四</view>
		<view class="date_choose" v-if="!is_dark" @click="date_update_index=4">周五</view>
		<view class="date_choose" v-if="!is_dark" @click="date_update_index=5">周六</view>
		<view class="date_choose" v-if="!is_dark" @click="date_update_index=6">周日</view>
	</view>
	<swiper :autoplay="false" v-show="page_index==1" :style="{height:`${main_page_height-35}px`}" :current="date_update_index" @change="change_now_tag_position">
		<swiper-item v-for="one_day_data_index,index in date">
			<scroll-view style="width: 100%;height: 100%;" scroll-y="true">
				<view class="day_update_list">
					<view class="day_update_list_item" v-for="item,index in all_update_data[one_day_data_index]" style="background-color: #4c4c4c;color: white;" v-if="is_dark" @click="go_to_player(item.id,item.img_url,item.name,item.state) ">
						<view class="day_update_list_image_show">
							<my-img :src="item.img_url" mode="aspectFill" class="img"></my-img>
						</view>
						<view class="day_update_list_textbar">
							<!-- <view class="day_update_list_title" v-if="item.name.length>14" style="animation: move_text 5s 0.5s infinite linear;">{{item.name}}&nbsp;&nbsp;&nbsp;</view> -->
							<view class="day_update_list_title" style="overflow: hidden;text-overflow: ellipsis;">{{item.name}}&nbsp;&nbsp;&nbsp;</view>
							<view class="day_update_list_tag">{{item.state}}</view>
						</view>
					</view>
					<view class="day_update_list_item" v-for="item,index in all_update_data[one_day_data_index]" style="background-color: #c8c8c8;" v-else @click="go_to_player(item.id,item.img_url,item.name,item.state) ">
						<view class="day_update_list_image_show">
							<my-img :src="item.img_url" mode="aspectFill" class="img"></my-img>
						</view>
						<view class="day_update_list_textbar">
							<!-- <view class="day_update_list_title" v-if="item.name.length>14" style="animation: move_text 5s 0.5s infinite linear;">{{item.name}}&nbsp;&nbsp;&nbsp;</view> -->
							<view class="day_update_list_title" style="overflow: hidden;text-overflow: ellipsis;">{{item.name}}&nbsp;&nbsp;&nbsp;</view>
							<view class="day_update_list_tag">{{item.state}}</view>
						</view>
					</view>
				</view>
			</scroll-view>
		</swiper-item>
	</swiper>
	
	
	
</template>

<script>
	import icons from '../../uni_modules/uni-icons/components/uni-icons/uni-icons.vue'
	import ajax from '../../common/ajax'
	import popupasking from '@/components/popup-asking/popup-asking.vue'
	import popupnotice from '@/components/popup-notice/popup-notice.vue'
	import myimg from '@/components/my-img/my-img.vue'
	export default {
		components:{
			icons,
			popupasking,
			popupnotice,
			myimg
		},
		onBackPress() {
			if(this.child_page_position==0){
				this.hide_child_page()
			}
		},
		onShow() {
			if(this.child_page_position==0){
				this.hide_child_page()
			}
			ajax.do_request("/get_banner","GET",{},(res)=>{
				if (!res.error){
					this.swiper_data=res.data
				}
			})
			ajax.do_request("/get_fastfunction_set","GET",{},(res)=>{ 
				if (!res.error){
					this.fast_function_data=res.data
				}
			})
			//联网检查本地用户信息存储，如果不存在用户信息就设置为false
			if (!uni.getStorageSync('login_token')==""){
				ajax.do_request("/check_state","GET",{},(res)=>{
					if (res.error==2){
						uni.showToast({
							title:"登录信息失效",
							icon:'error'
						})
						uni.setStorageSync("login_token","")
						this.login_msg=false
					}else{
						this.login_msg=uni.getStorageSync('login_token')
					}
				})
			}else{
				this.login_msg=false
			}
		},
		onReady() {
			ajax.do_request('/app/version','GET',{},(res)=>{
				if (!res.error){
					if (parseFloat(res.data.clientVersion)>parseFloat(getApp().globalData.version)){
						this.$refs.version.show('更新提示',`检测到新版本V${parseFloat(res.data.clientVersion)},当前版本V${parseFloat(getApp().globalData.version)}`,false,(res1)=>{
							if (res1.state) {
								plus.runtime.openURL(res.data.url);
								plus.runtime.quit()
							} 
							else {
								plus.runtime.quit()
							}
						})
					}else{
						ajax.do_request("/app/notice","GET",{},(res)=>{
							if (!res.error){
								if(res.data.show){
									if (getApp().globalData.isBoot){
										this.$refs.notice.show(res.data.title,res.data.content)
										getApp().globalData.isBoot=false
									}
								}
							}
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
			ajax.do_request('/get_command_video',"GET",{},(res)=>{
				if (!res.error){
					this.commandVideosnum+=10
					for (var i=0;i<res.data.length;i++){
						try{
							if (res.data[i].img_url=="" || res.data[i].img_url==undefined){
								res.data[i].img_url="../static/nodata.png" 
							} 
							var animate= uni.createAnimation({
							    duration: 0,
								timingFunction: 'ease',
							})
							animate.scale(1.5,1.5).opacity(0).step()
							this.item_data.push({
								img:res.data[i].img_url,
								tag:res.data[i].state,
								title:res.data[i].name,
								id:res.data[i].id,
								animation:animate.export()
							})
							setTimeout((position)=>{
								var animate1= uni.createAnimation({
								    duration: 650,
									timingFunction: 'ease-out',
								})
								animate1.scale(1,1).opacity(1).step()
								this.item_data[(this.commandVideosnum-10)+position].animation=animate1.export()
							},150*(i+1),i)
						}catch(err){
							console.log(err)
							continue;
						}
					}
				}
			})
		},
		data() {
			return {
				keyword:"",//搜索栏的搜索词
				bg_color:['rgb(51, 124, 203)', 'rgb(218, 193, 151)'],//背景颜色组
				now_bg_color:"rgb(218, 193, 151)",//当前的背景颜色
				now_nav_color:"rgb(218, 193, 151)",//当前的顶部nav颜色
				placehold_switch:"visible",//搜索提示词是否消失
				now_page_position:0,//当前滑窗的位置
				controlPage:0,
				main_page_height:0,//主窗口高度
				triggered: false,//滑窗是否刷新
				is_dark:false,//是否为暗色模式
				page_index:0,//当前的显示模块
				child_page_position:0,//子页的位置，-50为隐藏，0为显示
				torch_start:0,//触控开始的方位
				main_width:0,//屏幕的宽度
				is_show:false,//遮罩是否显示
				all_update_data:{},//所有的更新信息
				date:["1","2","3","4","5","6","7"],//星期表
				date_update_index:0,//当天的更新index
				search_bar_focus:false,
				login_msg:{},//登录信息
				isMoving:0,//是否正在滑动窗口
				swiper_data:[//顶部banner的数据,默认数据，用于请求失败的时候
					{
						type:"video",
						name:'异世界魔王与召唤少女的奴隶魔术',
						img_url:"https://dd-static.jd.com/ddimg/jfs/t1/206821/28/23866/31689/62b4ac33E9ef77800/71076712334d971f.jpg",
						id:347,
						state:"12集全"
					},
					{
						type:"video",
						name:'约会大作战 第四季',
						img_url:"https://dd-static.jd.com/ddimg/jfs/t1/90198/37/28892/38172/62b4ac0bEad649679/5ef0efef9d35d770.jpg",
						id:465,
						state:"12集全"
					}
				],
				item_data:[],//推荐视频的json储存
				fast_function_data:[],//快速功能的数组，仅支持web跳转，暂不支持视频跳转
				commandVideosnum:0
			}
		},
		onLoad() {
			//获取白天还是黑天模式
			this.is_dark=getApp().globalData.dark
			//获取屏幕宽高，用于 构建滑窗盒子
			uni.getSystemInfo({
				success: (res) => {
					this.main_page_height=res.windowHeight-120
					this.main_width=res.windowWidth;
					this.child_page_position=-this.main_width*0.7;
				}
			})
			ajax.do_request("/get_banner","GET",{},(res)=>{
				if (!res.error){
					this.swiper_data=res.data
				}
			})
			ajax.do_request("/get_fastfunction_set","GET",{},(res)=>{ 
				if (!res.error){
					this.fast_function_data=res.data
				}
			})
		},
		methods: {
			change_now_img(e){
				if (e.detail.current==0 && this.page_index==0){
					this.now_bg_color=this.bg_color[1]
					if (this.now_page_position<=139){
						this.now_nav_color=this.bg_color[1]
					}
				}else{
					if (this.page_index==0){
						this.now_bg_color=this.bg_color[0]
						if (this.now_page_position<=139){
							this.now_nav_color=this.bg_color[0]
						}
					}
				}
			},
			show_or_not(){
				if(this.keyword==''){
					this.placehold_switch='visible'
					this.search_bar_focus=false
				}
			},
			scroll_position(e){
				this.now_page_position=e.detail.scrollTop
				if (this.now_page_position>139){
					this.now_nav_color=this.bg_color[1]
				}else{
					this.now_nav_color=this.now_bg_color
				}
			},
			go_to_web(url){
				this.hide_child_page()
				uni.navigateTo({
					url:"/pages/notice/notice?url="+url,
					animationType:'zoom-fade-out',
					animationDuration:300
				})
			},
			search_data(key){
				uni.navigateTo({
					url:"/pages/search_res/search_res?key="+key,
					animationDuration:300,
					animationType:'zoom-fade-out'
				})
			},
			go_to(url){
				this.hide_child_page()
				uni.navigateTo({
					url:url,
					animationDuration:300,
					animationType:'zoom-fade-out'
				})
			},
			switch_page(url){
				console.log(url)
				uni.navigateTo({
					url:url,
					animationDuration:0,
					animationType:'none'
				})
			},
			switch_to_day(){
				if (this.is_dark){
					this.is_dark=false
					getApp().globalData.dark=false
					uni.setStorageSync('dark',false)
				}else{
					this.is_dark=true
					getApp().globalData.dark=true
					uni.setStorageSync('dark',true)
				}
			},
			go_to_player(id,img,title,state){
				this.hide_child_page()
				uni.navigateTo({
					url:"/pages/v_player/v_player?id="+id+'&img='+img+'&title='+title+'&part='+state,
					animationDuration:300,
					animationType:'slide-in-right'
				})
			},
			show_child_page(){
				this.child_page_position=0;
				this.is_show=true
			},
			hide_child_page(){
				this.child_page_position=-this.main_width*0.7;
				this.is_show=false
			},
			move(e){
				var pos=e.changedTouches[0].clientX-this.torch_start;
				this.isMoving=true
				if (pos>=0){
					this.child_page_position=0
				}else{
					this.child_page_position=pos
				}
			},
			end(e){
				this.isMoving=false
				if (-(e.changedTouches[0].clientX-this.torch_start)>this.main_width*0.2){
					this.child_page_position=-this.main_width*0.7
					this.is_show=false
				}else{
					this.child_page_position=0
				}
			},
			start_move(e){
				this.torch_start=e.changedTouches[0].clientX;
			},
			get_time_update(){
				if (new Date().getDay()==0){
					this.date_update_index=6;
				}else{
					this.date_update_index=new Date().getDay()-1;
				}
				if (!this.all_update_data.length>0){
					ajax.do_request("/get_weekdata",'GET',{},(res)=>{
						if (res.error==1){
							console.log('请求失败')
						}else{
							this.all_update_data=res.data
							this.$forceUpdate();
						}
					})
				}
			},
			back_to_top(){
				this.now_nav_color=this.now_bg_color
				if (this.page_index==0){
					this.controlPage=this.now_page_position
					this.$nextTick(()=>{
						this.controlPage=0
						// this.onRefresh()
					})
					
				}
			},
			change_now_tag_position(e){
				this.date_update_index=e.detail.current;
			},
			error_img_handle(list,index){
				this[list][index].img='../static/nodata.png'
			},
			error_img_handle_update(pos,index){
				this.all_update_data[pos][index].img_url="../static/nodata.png"
			},
			onRefresh() { 
				this.triggered=true; 
				this.item_data.splice(0,this.item_data.length)
				this.commandVideosnum=0
				ajax.do_request('/get_command_video',"GET",{},(res)=>{
					if (!res.error){
						this.commandVideosnum+=10
						for (var i=0;i<res.data.length;i++){
							try{
								if (res.data[i].img_url=="" || res.data[i].img_url==undefined){
									res.data[i].img_url="../static/nodata.png"
								} 
								var animate= uni.createAnimation({
								    duration: 0,
									timingFunction: 'ease',
								})
								animate.scale(1.5,1.5).opacity(0).step()
								this.item_data.push({
									img:res.data[i].img_url,
									tag:res.data[i].state,
									title:res.data[i].name,
									id:res.data[i].id,
									animation:animate.export()
								})
								setTimeout((position)=>{
									var animate1= uni.createAnimation({
									    duration: 650,
										timingFunction: 'ease-out',
									})
									animate1.scale(1,1).opacity(1).step()
									this.item_data[(this.commandVideosnum-10)+position].animation=animate1.export()
								},150*(i+1),i)
								setTimeout(() => {
									this.triggered = false;
						        }, 1000)
							}catch(err){
								console.log(err)
								continue;
							}
						}
						this.triggered = false;
					}else{
						this.triggered = false;
					}
				})
            },
			end_load(){
				ajax.do_request('/get_command_video',"GET",{},(res)=>{
					if (!res.error){
						this.commandVideosnum+=10
						for (var i=0;i<res.data.length;i++){
							try{
								if (res.data[i].img_url=="" || res.data[i].img_url==undefined){
									res.data[i].img_url="../static/nodata.png"
								} 
								var animate= uni.createAnimation({
								    duration: 0,
									timingFunction: 'ease',
								})
								animate.scale(1.5,1.5).opacity(0).step()
								this.item_data.push({
									img:res.data[i].img_url,
									tag:res.data[i].state,
									title:res.data[i].name,
									id:res.data[i].id,
									animation:animate.export()
								})
								setTimeout((position)=>{
									var animate1= uni.createAnimation({
									    duration: 650,
										timingFunction: 'ease-out',
									})
									animate1.scale(1,1).opacity(1).step()
									this.item_data[(this.commandVideosnum-10)+position].animation=animate1.export()
								},150*(i+1),i)
								setTimeout(() => {
									this.triggered = false;
						        }, 800)
							}catch(err){
								console.log(err)
								continue;
							}
						}
						this.triggered = false;
					}else{
						this.triggered = false;
					}
				})
			}
		},
		
	}
</script>

<style>
	@import url('../../common/font.css');
	@import url('../../common/iconfont.css');
	page{
		overflow: hidden;
		transition: all 0.3s;
		/* background-color: #eef0f1; */
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
	
	.swiper_title{
		position: absolute;
		z-index: 1;
		top:85%;
		background: linear-gradient(rgba(255,255,255,0),rgba(0, 0, 0, 1));
		width: 90%;
		margin-left: 5%;
		color: white;
	}
	.top_span{
		background-color: transparent;
		height: 35px;
	}
	.nav{
		width: 100%;
		height: 75px;
		display: flex;
		text-align: center;
		position: relative;
		transition: all 0.8s;
	}
	.nav input{
		background-color: #dfe2e3;
		height: 30px;
		width: 100%;
		border-radius: 17.5px;
		text-align: left;
		text-indent: 2em;
		margin-top: 35px;
	}
	.nav span{
		position: absolute;
		left: 50%;
		margin-top: 35px;
		transform: translateX(-50%);
		color: #788ba5;
		line-height: 30px;
	}
	.nav_bg{
		position: absolute;
		width: 100%;
		height: 140px;
	    top: 0px;
		z-index: -1;
		transition: all 0.8s;
		pointer-events: none;
	}
	
	.shop_stream_img{
		width: 90%;
		margin-left: 5%;
		margin-right: 5%;
		margin-top: 10px;
	}
	
	.fast_function{
		width: 100%;
		display: flex;
		flex-wrap: wrap;
		margin-top: 10px;
		margin-bottom: 10px;
	}
	.fast_function-item{
		width: 20%;
		aspect-ratio: 1;
		text-align: center;
		font-size: 10px;
	}
	
	.fast_function-item image{
		position: relative;
	    width: 45%;
		height: 45%;
		margin-left: 20%;
        margin-right: 20%;
	    border-radius: 10%;
		margin-top: 5%;
	}
	.fast_function-item span{
		position: relative;
	}
	
	.shop-list{
		width: 100%;
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
		
	}
	.img_show{
		border-top-right-radius: 10px;
		border-top-left-radius: 10px;
	}
	.img_show .img{
		width: 100%;
		/* border-top-right-radius: 10px;
		border-top-left-radius: 10px; */
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
	.left_bar{
		position: absolute;
		top: 0px;
		height: 100%;
		width: 60%;
		z-index: 2;
		/* transition: all 0.1s ease; */
	}
	.person_icon{
		width: 80%;
		aspect-ratio: 1;
		margin-left: 8%;
		margin-right: 8%;
		margin-top: 40px;
		border-radius: 50%;
		border: solid 2px #788ba5;
		overflow: hidden;
		background-color: white;
	}
	.person_icon image{
		width: 100%;
		height: 100%;
	}
	.person_bg{
		background-image: url("../../static/bgicon.png");
		background-repeat: no-repeat;
		background-size: cover;
	}
	.username{
		width: 100%;
		margin-top: 5px;
		text-align: center;
		white-space: nowrap;
	}
	.user_msg{
		position: absolute;
		margin-left:8%;
		text-align: center;
		width: 30%;
	}
	.function_bar{
		width: 100%;
	}
	.one_bar_tip{
		width: 80%;
		margin-top: 10px;
		margin-bottom:10px;
		margin-left: 10%;
		margin-right: 10%;
		height: 50px;
		line-height: 50px;
		font-size: 18px;
	}
	/* 功能类 */
	.to_small{
		transform: scale(0.7,0.7);
	}
	
	.day_update_list{
		width: 100%;
		margin-top: 10px;
	}
	.day_update_list_item{
		height: 80px;
		display: flex;
		border-radius: 10px;
		width: 95%;
		margin: 3px 3px 2.5% 2.5%;
	}
	.day_update_list_textbar{
		margin-left: 5px;
		width: 65%;
		overflow: hidden;
	}
	.day_update_list_title{
		margin-top: 5px;
		white-space: nowrap;
	}
	.day_update_list_image_show{
		width: 30%;
		margin-left: 5px;
		border-radius: 10px;
		height: 70px;
		margin-top: 5px;
		margin-bottom: 5px;
		background-color: #202122;
	}
	.day_update_list_image_show .img{
		width: 100%;
		height: 100%;
		border-radius: 10px;
	}
	.day_update_list_tag{
		margin-top: 5px;
		width: 100%;
	}
	.choose_day{
		width: 100%;
		display: flex;
		height: 35px;
	}
	.date_choose{
		width: 15%;
		line-height: 35px;
		text-align: center;
		font-size: 15px;
		white-space: nowrap;
	}
	.buttom_line{
		position: absolute;
		/* margin: 28px 0px 2.25% 2.25%; */
		margin-top: 28px;
		width: 10.5%;
		height: 5px;
		background-color: red;
		border-radius: 2.5px;
		transition: all 0.3s;
	}
	
	@keyframes move_text {
		0%{transform: translateX(0%);}
		100%{transform: translateX(-100%);}
	}
	
	.footer{
		position: absolute;
		bottom: 0;
		width: 100%;
		height: 55px;
		display: flex;
		background: transparent;
	}
	.footer-item{
		width: 20%;
		text-align: center;
		height: 50px;
		margin-left: auto;
		margin-right: auto;
		margin-top: 13px;
		transition: all 75ms;
		font-size: 12px;
		
	}
</style>
