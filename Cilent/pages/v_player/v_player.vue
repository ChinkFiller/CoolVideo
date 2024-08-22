<template>
	<page-meta page-style="background:#343434" v-if="is_dark"></page-meta>
	<page-meta page-style="background:#eef0f1" v-else></page-meta>
	<view class="top_span"></view>
	<view class="main_player">
		<view class="loding_img" :style="{backgroundImage:`url(${all_img_url})`}">
		</view>
		<view class="loding_text">
			正在解析视频地址... 
		</view>
	</view>
	<view style="margin-left: 20px;color: white;font-size: 18px;margin-bottom: 10px;" id="test" v-if="is_dark" @click="jubaoListisShow=false;colorSelectorShow=false">
		简介
		<span class="color-select-btn" :style="{backgroundColor:danmucolor}" @click.native.stop="colorSelectorShow=!colorSelectorShow;jubaoListisShow=false"></span>
		<span class="jubao" @click.native.stop="jubaoListisShow=!jubaoListisShow;colorSelectorShow=false">!</span>
		<input v-model="danmu" @confirm="sendDanmu()" type="text" name="" id="" class="danmu-input" style="background-color: #717171;">
		<span @click="sendDanmu()" class="danmu-send-btn" style="background-color: #717171;">发送</span>
		<span style="position: absolute;right:245px;margin-top: 5px;"><uni-icons :type="danmuIcon" size="30" color="white" @click="danmuswitch()"></uni-icons></span>
		<view style="background-color: red;width: 30px;border-radius: 3px;height: 6px;margin-left: 3px;"></view>
	</view>
	<view style="margin-left: 20px;font-size: 18px;margin-bottom: 10px;" id="test" v-else>
		简介
		<span class="color-select-btn" :style="{backgroundColor:danmucolor}" @click.native.stop="colorSelectorShow=!colorSelectorShow;jubaoListisShow=false"></span>
		<span class="jubao" @click.native.stop="jubaoListisShow=!jubaoListisShow">!</span>
		<input v-model="danmu" @confirm="sendDanmu()" type="text" name="" id="" class="danmu-input" style="background-color: #cfcfcf;">
		<span @click="sendDanmu()" class="danmu-send-btn" style="background-color: #cfcfcf;">发送</span>
		<span style="position: absolute;right:245px;margin-top: 5px;"><uni-icons :type="danmuIcon" size="30" color="#434343" @click="danmuswitch()"></uni-icons></span>
		<view style="background-color: red;width: 30px;border-radius: 3px;height: 6px;margin-left: 3px;"></view>
	</view>
	<scroll-view scroll-y="true" :style="{height:`${bar_heigh}px`}" @click="jubaoListisShow=false;colorSelectorShow=false">
		<view v-if="is_dark" style="color: white;margin-bottom: 20px;">
			<view class="main_title" style="margin-right: 2.5px;" v-if="!getIsTextWarp()">{{or_title}}</view>
			<view :class="[now_main_title_style]" v-else @click="switch_title()">
				<uni-icons type="down" size="18" class="more_button" color="white" :style="{transform:`rotate(${more_btn_rote}deg)`}"></uni-icons>
				{{or_title}}
			</view>
		</view>
		<view v-else style="margin-bottom: 20px;">
			<view class="main_title" style="margin-right: 2.5px;" v-if="!getIsTextWarp()">{{or_title}}</view>
			<view :class="[now_main_title_style]" v-else @click="switch_title()">
				<uni-icons type="down" size="18" class="more_button" :style="{transform:`rotate(${more_btn_rote}deg)`}"></uni-icons>
				{{or_title}}
			</view>
		</view>
		<view class="fast_function">
			<view class="fast_function-item" v-for="item,index in fast_function_data" @click="item.func">
				<view class="download_pross" v-show="index==3">
				  <view class="progress" :style="{width:`${download_p}%`}"></view>
				</view>
				<uni-icons :type="item.img" size="30" v-if="is_dark" :color="item.color"></uni-icons>
				<uni-icons :type="item.img" size="30" v-else :color="item.color"></uni-icons>
				<view style="color: white;" v-if="is_dark" class="fast_function_title">{{item.title}}</view>
				<view  class="fast_function_title" v-else>{{item.title}}</view>
			</view>
		</view>
		
		<scroll-view class="part-select" scroll-x="true">
			<view class="one-part" v-for="item,index in other_video" @click="switch_other(item.part_id)">
				<view class="part" v-if="item.playing&&is_dark" style="color: red;background-color: #545454">{{item.part}}</view>
				<view class="part" v-if="item.playing&&!is_dark" style="color: red;background-color: #d0d0d0">{{item.part}}</view>
			    <view class="part" v-if="is_dark&&!item.playing" style="background-color: #545454;color: white;">{{item.part}}</view>
				<view class="part" v-if="!is_dark&&!item.playing" style="background-color: #d0d0d0">{{item.part}}</view>
			</view>
		</scroll-view>
		
		<view class="other_video" v-for="item,index in others" @click="go_to_player(item.id,item.img_url,item.name,item.state)">
			<image :src="item.img_url" mode="aspectFill" @error="errorImageHandle(index)"></image>
			<view class="intruduce" v-if="is_dark">
				<view class="text_bar">
					<view class="other_video_title" style="color: white;">{{item.name}}</view>
					<view class="part" style="color: white;">{{item.state}}</view>
				</view>
			</view>
			<view class="intruduce" v-else>
				<view class="text_bar">
					<view class="other_video_title">{{item.name}}</view>
					<view class="part">{{item.state}}</view>
				</view>
			</view>
		</view>
	</scroll-view>
	<view class="jubao-list" :style="{top:`${jubaoListisShow?60:100}%`}">
		<view style="width: 100%;height: 10%;text-align: center;" :style="{backgroundColor:`${is_dark?'#5a5a5a':'#dcdcdd'}`,color:`${is_dark?'white':'black'}`}">举报弹幕</view>
		<scroll-view style="position: relative;width: 100%;height: 90%;" scroll-y="true" :style="{backgroundColor:`${is_dark?'#5a5a5a':'#dcdcdd'}`}">
			<view class="item" v-if="danmuList.length==0" :style="{backgroundColor:`${is_dark?'#a7a7a7':'#ededee'}`,color:`${is_dark?'white':'black'}`}"><span style="margin: auto;">暂无弹幕</span></view>
			<view class="item" v-for="item,index in danmuList" :style="{backgroundColor:`${is_dark?'#a7a7a7':'#ededee'}`,color:`${is_dark?'white':'black'}`}">
				<span class="content">{{item.text}}</span>
				<span class="time">{{formateTimeFromNumber(item.time)}}</span>
				<uni-icons type="hand-down" size="30" class="upload" @click="jubaoDanmu(item.id)"></uni-icons>
			</view>
		</scroll-view>
	</view>
	<view class="color-select" v-if="colorSelectorShow">
		<view class="color-select-item" v-for="item,index in colors" :style="{backgroundColor:item}" @click="danmucolor=item;colorSelectorShow=false"></view>
	</view>
</template>

<script>
    import ajax from '../../common/ajax'
	import uniIcons from '../../uni_modules/uni-icons/components/uni-icons/uni-icons.vue'
	export default {
		components:{
			uniIcons,
		},
		data() {
			return {
				others:[],
				//其他选集的信息集合
				other_video:[],
				colors:['#ffffff','#ff0000','#ffff00','#005500','#000000','#e800e8','#ff5500','#0055ff','#ff75ff'],
				video:"",//视频地址
				all_img_url:"",//图片地址
				title:'',//加了第几集的标题
				or_title:'',//原始标题
				can_play:false,//是否解析完成可以播放
				now_part:1,//现在是第几集
				id:1,//视频的id
				bar_heigh:0,//滑窗高度
				is_dark:false,//是否黑色模式
				download_p:0,//下载进度变量
				is_agree:false,//是否点赞 
				download_task:{},//下载任务
				more_btn_rote:0,
				now_main_title_style:'main_title',
				cdn:0,
				nowCdn:0,
				width:0,
				video_paused:false,
				full:0,
				isFollow:false,
				danmu:"",
				tempageID:0,
				jubaoListisShow:false,
				danmuList:[],
				danmucolor:'white',
				colorSelectorShow:false,
				danmuIcon:"chatbubble-filled",
				select:["普通线路[已选择]","加速线路"],
				fast_function_data:[//快速功能的json
					{
						title:0,
						func:this.agree,
						img:'hand-up',
						color:""
					},
					{
						title:"追番",
						func:this.allow,
						img:'heart',
						color:""
					},
					{
						title:"切换源",
						func:this.change_url,
						img:'refreshempty',
						color:""
					},
					{
						title:"下载",
						func:this.download,
						img:'cloud-download',
						color:""
					},
					{
						title:"分享",
						func:this.share,
						img:'undo',
						color:""
					}
				]
			}
		},
		onLoad(e) {
			this.all_img_url=e.img;
			this.title=e.title
			this.or_title=e.title
			this.id=e.id;
			this.is_dark=getApp().globalData.dark
			const parts=this.extractNumbers(e.part)
			this.full=parts!=''?parts:1
			uni.getSubNVueById("player").hide()
			if (this.is_dark){
				for (var y in this.fast_function_data){
					this.fast_function_data[y].color="white"
				}
			}
			//获取点赞数量
			ajax.do_request("/get_agree","GET",{id:e.id},(res)=>{
				if(!res.error){
					this.fast_function_data[0].title=res.data.num
					if (res.data.is_agree){
						this.is_agree=true
						this.changeAgree(true)
					}
				}
			})
			//获取追番数据
			if (uni.getStorageSync('login_token')!=''){
				this.getAllowData()
			}
			//获取推荐视频
			ajax.do_request("/get_command_video","GET",{},(res)=>{
				if (!res.error){
					this.others=res.data
				}
			})
			uni.getSystemInfo({
				success: (res) => {
					this.width=res.screenWidth
				}
			})
			if (parts==''){
				this.title=e.part
				this.other_video.push({
					part_id:i,
					part:e.part,
					playing:true
				})
			}else{
				this.title="第1话"
				for (var i=1;i<=parseInt(parts);i++){
					this.other_video.push({
						part_id:i,
						part:`第${i}话`,
						playing:false
					})
				}
			}
			//循环本地存储的历史记录数组，查找符合值
			var tem_list=uni.getStorageSync('history')
			var a=true
			for (var i=0;i<tem_list.length;i++){
				if (tem_list[i].id==this.id){a=false;break;}
			}
			if (a){//第一次播放的事件处理
				if (parts==''){
					tem_list.push({
						title:this.or_title,
						state:e.part,
						id:this.id,
						img_url:this.all_img_url,
						full:e.part
					})
				}else{
					tem_list.push({
						title:this.or_title,
						state:"第1话",
						id:this.id,
						img_url:this.all_img_url,
						full:e.part
					})
				}
				//加入到历史记录中
				this.rememberHistory(tem_list)
				this.other_video[0].playing=true
				this.getVideoUrl(1)
			}else{
				//刷新位置视频的位置
				let datas=tem_list[i]
				tem_list.splice(i,1)
				tem_list.push(datas)
				this.rememberHistory(tem_list)
				if (this.extractNumbers(datas.state)!=''){
					this.other_video[this.extractNumbers(datas.state)-1].playing=true
					this.title=this.or_title+` 第${this.extractNumbers(datas.state)}话`
					this.now_part=this.extractNumbers(datas.state)
					this.getVideoUrl(this.extractNumbers(datas.state))
				}else{
					this.other_video[0].playing=true
					this.getVideoUrl(1)
				}
			}
		},
		onBackPress() {
			if (getApp().globalData.isPlayerFullScreen){//触发返回键先判断是否为全屏状态，是的话解除全屏，不返回
				plus.screen.lockOrientation('portrait-primary');
				plus.navigator.showSystemNavigation()
				plus.navigator.setFullscreen(false)
				uni.$emit("backOrsize",{})
				uni.getSubNVueById("player").setStyle({
					height:"300px"
				})
				return true;
			}else{
				if (this.jubaoListisShow){
					this.jubaoListisShow=false
					return true;
				}
				uni.getSubNVueById("player").hide()
				return false;
			}
		},
		onReady() {
			uni.$on("fullscreen",res=>{//播放器全屏大小调整触发事件
				plus.screen.lockOrientation('landscape-primary');
				plus.navigator.hideSystemNavigation();
				plus.navigator.setFullscreen(true)
				this.colorSelectorShow=false
				this.jubaoListisShow=false
				uni.getSubNVueById("player").setStyle({
					height:"100%"
				})
			})
			uni.$on("backToOrPage",res=>{//播放器还原屏幕大小触发函数
				plus.screen.lockOrientation('portrait-primary');
				plus.navigator.showSystemNavigation()
				plus.navigator.setFullscreen(false)
				uni.getSubNVueById("player").setStyle({
					height:"300px"
				})
			})
			let view = uni.createSelectorQuery().select("#test");
			let start=0;
			view.boundingClientRect(data=>{
				uni.getSystemInfo({
					success: (res) => {
						this.bar_heigh=res.screenHeight-310-data.height;
					}
				})
			}).exec();
		},
		onShow() {
		},
		methods: {
			test(){
				console.log(1)
			},
			sendDanmu(){
				if (this.danmu==""){
					uni.showToast({
						title:"不能发送空弹幕",
						icon:"error"
					})
				}else if(uni.getStorageSync('login_token')==''){
					uni.showToast({
						title:"未登录不能发送弹幕",
						icon:"error"
					})	
				}else{
				try{
					uni.$emit("sendDanmu",{value:this.danmu,id:this.id,part:this.now_part,color:this.danmucolor})
					this.danmu=""
					setTimeout(()=>{
						ajax.do_request("/getdanmu","GET",{id:this.id,part:this.now_part},(res)=>{
							if (!res.error){
								this.danmuList=res.data
							}
						})
					},1000)
				}catch(err){
					uni.showToast({
						title:"发送失败",
						icon:"error"
					})	
				}
				}
			},
			errorImageHandle(index){
				this.others[index].img_url="../static/nodata.png"
			},
			go_to_player(id,img,title,state){
				uni.getSubNVueById("player").hide()
				uni.$emit('pauseVideo',{})
				uni.redirectTo({
					url:"/pages/v_player/v_player?id="+id+'&img='+img+'&title='+title+'&part='+state,
					animationDuration:300,
					animationType:'slide-in-right'
				})
			},
			danmuswitch(){
				if (this.danmuIcon=="chatbubble-filled"){
					this.danmuIcon="chatbubble"
					uni.$emit("danmuSwitch",{state:false})
				}else{
					this.danmuIcon="chatbubble-filled"
					uni.$emit("danmuSwitch",{state:true})
				}
			},
			rememberHistory(data){
				data[0]=parseInt(new Date().getTime()/1000)
				uni.setStorageSync("history",data)
				data.splice(0,1)
				ajax.do_request("/upload_history","POST",{"data":data},(res)=>{
					
				})
			},
			rememerFollow(data){
				data[0]=parseInt(new Date().getTime()/1000)
				uni.setStorageSync("allow",data)
				data.splice(0,1)
				ajax.do_request("/upload_allow","POST",{"data":data},(res)=>{
					
				})
			},
			jubaoDanmu(id){
				ajax.do_request("/badDanmuFind",'GET',{id:this.id,part:this.now_part,danmuID:id},(res)=>{
					if (!res.error){
						uni.showToast({
							title:"举报成功",
							icon:"success"
						})
					}else{
						uni.showToast({
							title:"举报失败",
							icon:"error"
						})
					}
				})
			},
			formateTimeFromNumber(time){
				var min=parseInt(time/60);
				var sec=parseInt(time)%60;
				if (min<10){
					min=`0${min}`
				}
				if (sec<10){
					sec=`0${sec}`
				}
				return `${min}:${sec}`;
			},
			getAllowData(){
				let data=uni.getStorageSync("allow")
				let localtime=data.splice(0,1)
				ajax.do_request("/get_allow","GET",{},(res)=>{
					if (!res.error){
						 let remoteData=res.data;
						 if (parseInt(remoteData[0])>localtime){
							for (var i=1;i<remoteData.length;i++){
								 if (parseInt(remoteData[i].id)==this.id){
									 this.fast_function_data[1].title="已追番"
									 this.changeAllow(true)
									 this.isFollow=true
									 break;
								 }
							}
							console.log("云端数据同步到了本地")
							uni.setStorageSync("allow",remoteData)
						 }else{
							 if (parseInt(remoteData[0])<localtime){
								 console.log("本地数据同步到了云端")
								ajax.do_request("/upload_allow","POST",{"data":data},(res)=>{})
							 }else{
								 for (var i in data){
								 	if (parseInt(data[i].id)==parseInt(this.id)){
								 		this.fast_function_data[1].title="已追番"
								 		this.changeAllow(true)
								 		break;
								 	}
								 }
							 }
						 }
					}else{
						for (var i in data){
							if (parseInt(data[i].id)==parseInt(this.id)){
								this.fast_function_data[1].title="已追番"
								this.changeAllow(true)
								this.isFollow=true
								break;
							}
						}
					}
				},1000)
			},
			getVideoUrl(num){
				this.now_part=num
				ajax.do_request("/getdanmu","GET",{id:this.id,part:num},(res)=>{
					if (!res.error){
						this.danmuList=res.data
					}
				})
				ajax.do_request("/get_video_url","GET",{id:this.id,num:num,cdn:this.cdn},(res)=>{ 
					if (!res.error){
						if(this.cdn==1){
							this.video=uni.getStorageSync("serverUrl")+res.data.url
						}else{
							if (res.data.isOther){
								this.video=res.data.url
							}else{
								this.video=uni.getStorageSync("serverUrl")+res.data.url
							}
						}
						this.can_play=true
						uni.getSubNVueById("player").show()
						setTimeout(()=>{
							uni.$emit("play",{url:this.video,title:`第${num}话`,id:this.id,part:num})
						},500)
					}else{
						uni.showToast({
							title:"加载失败，请稍后重试"
						})
					}
				},8000)
			},
			send_msg(title){
				plus.push.createMessage(
				                title,
								{
									"cover" : false,
									"when" : new Date(),
									'title' : "通知消息"
								}, 
								{
									'id' : 'id',
									'key': "key"
								});
			},
			image_error(){
				this.all_img_url='../static/nodata.png'
			},
			switch_title(){
				if (this.now_main_title_style=='main_title'){
					this.now_main_title_style='main_title_wrap'
					this.more_btn_rote=180
				}else{
					this.now_main_title_style='main_title'
					this.more_btn_rote=0
				}
			},
			getIsTextWarp(){
				if (this.width*0.85<this.or_title.length*18){
					return true
				}else{
					return false
				}
			},
			extractNumbers(str) {
			      // 使用正则表达式匹配数字
			      const numbers = str.match(/\d+/g);
			      // 如果匹配到数字，返回一个包含所有数字的字符串，否则返回空字符串
			      return numbers ? numbers.join(', ') : '';
			},
			changeAgree(state){
				if (state){
					this.fast_function_data[0].img='hand-up-filled'
					this.fast_function_data[0].color='red'
					this.is_agree=true
				}else{
					this.is_agree=false
					this.fast_function_data[0].img='hand-up'
					if (this.is_dark){
						this.fast_function_data[0].color='white'
					}else{
						this.fast_function_data[0].color='black'
					}
				}
			},
			changeAllow(state){
				if (state){
					this.fast_function_data[1].img='heart-filled'
					this.fast_function_data[1].color='red'
					this.isFollow=true
				}else{
					this.isFollow=false
					this.fast_function_data[1].img='heart'
					if (this.is_dark){
						this.fast_function_data[1].color='white'
					}else{
						this.fast_function_data[1].color='black'
					}
				}
			},
			agree(){
				if (uni.getStorageSync('login_token')==''){
                    uni.showToast({
                    	icon:'error',
						title:'请先登录'
                    })
				}else{
					if (this.is_agree){
						ajax.do_request("/agree","GET",{mode:0,id:this.id},(res)=>{
							if (!res.error){
								this.fast_function_data[0].title-=1;
								this.changeAgree(false)
							}else{
								console.log(msg)
								uni.showToast({
									icon:'error',
									title:"取消失败"
								})
							}
						})
					}else{
						ajax.do_request("/agree","GET",{mode:1,id:this.id},(res)=>{
							if (!res.error){
								this.fast_function_data[0].title+=1;
								this.changeAgree(true)
							}else{
								uni.showToast({
									icon:'error',
									title:"点赞失败"
								})
							}
						})
					}
				}
			},
			allow(){
				if (uni.getStorageSync('login_token')==''){
					uni.showToast({
						icon:'error',
						title:'请先登录'
					})
				}else{
					if (this.fast_function_data[1].title!="已追番"){
						this.fast_function_data[1].title="已追番"
						var a=uni.getStorageSync("allow")
						a.push({
							id:this.id,
							img_url:this.all_img_url,
							title:this.or_title,
							state:this.full
						})
						this.rememerFollow(a);
						this.changeAllow(true)
					}else{
						this.fast_function_data[1].title="追番"
						this.rememerFollow(uni.getStorageSync("allow").filter((item)=>{
							return parseInt(item.id) != this.id;
						}))
						this.changeAllow(false)
					}
				}
			},
			change_url(){
				// uni.showToast({
				// 	icon:'error',
				// 	title:"暂无可更换源"
				// })
				uni.showActionSheet({
					itemList:this.select,
					itemColor:'red',
					success:(res)=> {
						//console.log(res.tapIndex)
						if (res.tapIndex==1){
							if (!this.cdn==1){
								this.cdn=1
							    this.switch_other(this.now_part)
								this.select=["普通路线","加速路线[已选择]"]
							}
						}
						if (res.tapIndex==0){
							if (!this.cdn==0){
								this.cdn=0
							    this.switch_other(this.now_part)
								this.select=["普通路线[已选择]","加速路线"]
							}
						}
					}
				})
			},
			download(){
				if (!this.can_play || this.cdn==1){
					if (!this.can_play){
						uni.showToast({
						icon:'error',
						title:'请稍后重试'
						})
					}
					if (this.cdn==1){
						uni.showToast({
						icon:'error',
						title:'该路线无法下载'
						})
					}
				}else{
					if (this.fast_function_data[3].title=="下载"){
						uni.showToast({
							icon:'success',
							title:"下载任务已创建"
						})
						//uni.authorize({scope: 'scope.writePhotosAlbum'})//提前申请权限，保证文件能成功保存
						this.download_task=uni.downloadFile({
								url:this.video,
								success: (res)=>{
									// 2 成功下载后而且状态码为200时将视频保存到本地系统
									if(res.statusCode === 200) {
										uni.saveVideoToPhotosAlbum({
											filePath: res.tempFilePath,
										})
										this.fast_function_data[3].title=`已下载`
										this.download_p=0
										this.send_msg(`${this.title}下载成功`)
									}
									// 如果该资源不可下载或文件格式出错则提示用户
									else {
										uni.showToast({icon:'error',title:"下载失败"});
									}
								},
								fail: (err)=>{
									uni.showToast({icon:'error',title:"下载失败"})
								}
						})
						this.download_task.onProgressUpdate((res)=>{
							this.download_p=res.progress;
							this.fast_function_data[3].title=`${res.progress}%`
						})
					}else{
						if (this.fast_function_data[3].title=='已下载'){
							plus.runtime.launchApplication({
								pname:"com.android.gallery3d",
								fail:(res)=>{uni.showToast({
									icon:'none',
									title:'视频已保存在系统相册，请前往相册查看！',
									duration:3000
								})}
							})
						}else{
								uni.showModal({
								title: '提示',
								// 提示文字
								content: '确认停止下载任务吗？',
								// 取消按钮的文字自定义
								cancelText: "取消",
								// 确认按钮的文字自定义
								confirmText: "停止",
								//删除字体的颜色
								confirmColor:'red',
								//取消字体的颜色
								cancelColor:'#000000',
								success:(res)=> {
									if (res.confirm){
										this.download_task.abort()
										uni.showToast({
											title:"下载任务已取消"
										})
										this.download_p=0
										this.fast_function_data[3].title="下载"
									}
								}
							})
						}
					}
				}
				
				
			},
			share(){
				uni.$emit("danmuSwitch",{state:false})
				uni.showToast({
					icon:'error',
					title:"暂未开启"
				})
			},
			switch_other(num){
				if (num==this.now_part && this.nowCdn==this.cdn){
					return 0;
				}
				uni.getSubNVueById("player").hide()
				uni.$emit('pauseVideo',{})
				for (var i=0;i<this.other_video.length;i++){
					this.other_video[i].playing=false
				}
				this.other_video[num-1].playing=true
				this.now_part=num
				ajax.do_request("/getdanmu","GET",{id:this.id,part:num},(res)=>{
					if (!res.error){
						this.danmuList=res.data
					}
				})
				ajax.do_request("/get_video_url","GET",{id:this.id,num:num,cdn:this.cdn},(res)=>{
					if (!res.error){
						this.nowCdn=this.cdn
						if (res.data.isOther){
							this.video=res.data.url
						}else{
							this.video=uni.getStorageSync("serverUrl")+res.data.url
						}
						this.title=this.or_title+` 第${num}话`
						uni.$emit("play",{url:this.video,title:`第${this.now_part}话`,id:this.id,part:num})//事件触发，用于播放器播放
						uni.getSubNVueById("player").show()
						var tem_list=uni.getStorageSync('history')
						for (var i=1;i<tem_list.length;i++){
							if (tem_list[i].title==this.or_title){
								tem_list[i].state=` 第${num}话`;
								break;
							}
						}
						this.rememberHistory(tem_list)
					}
				},8000)
			}
		}
	}
</script>

<style>
	page{
		overflow: hidden;
	}
	
	.part-select{
		width: 100%;
		height: 70px;
		white-space: nowrap;
	}
	
	.one-part{
		width: 80px;
		height: 40px;
		text-align: center;
		line-height: 40px;
		display: inline-block;
		margin:15px 15px 0px 15px;
	}
	.part{
		width: 100%;
		height: 100%;
		border-radius: 10px;
	}
	.danmu-input{
		position: absolute;
		display: inline-block;
		right: 80px;
		margin-top: 8px;
		border-radius: 10px;
		text-indent: 0.5em;
		font-size: 15px;
        width: 160px;
		height: 20px;
	}
	.danmu-send-btn{
		position: absolute;
		right: 80px;
		margin-top: 8px; 
		height: 20px;
	    font-size:15px;
		padding: 0px 8px 0px 5px;
		border-top-right-radius: 10px;
		border-bottom-right-radius: 10px;
	}
	.jubao{
		position: absolute;
		right: 15px;
		background-color: #e82a04;
		font-size: 16px;
		padding: 1px 8px 1px 8px;
		margin-top: 8px;
		border-radius: 10px;
	}
	.jubao-list{
		position: absolute;
		height: 40%;
		top: 100%;
		width: 100%;
		background-color: aqua;
		transition: all 0.5s;
	}
	.jubao-list .item{
		width: 100%;
		display: flex;
		width: 90%;
		margin: 10px 5% 10px 5%;
		border-radius: 5px;
		background-color: antiquewhite;
	}
	.jubao-list .item .content{
		margin-left: 15px;
		font-size: 20px;
	}
	.jubao-list .item .time{
		margin-left: auto;
		margin-right:10px;
		font-size: 20px;
	}
	.jubao-list .item .upload{
		margin-right: 15px;
	}
	.color-select-btn{
		background-color: white;
		width: 25px;
		height: 25px;
		margin-top: 5px;
		position: absolute;
		right: 45px;
		border-radius: 3px;
		border: solid 2.5px #595959;
	}
	.color-select{
		position: absolute;
		top:340px;
		right: 10px;
		background-color: #ffffff;
		display: flex;
		width: 100px;
		height: 100px;
		border-radius: 5px;
		flex-wrap: wrap;
	}
	.color-select-item{
		width: 25px;
		height: 25px;
		margin: 3px;
		border-radius: 5px;
		border: solid 0.5px #747474;
	}
	.top_span{
		background-color: transparent;
		height: 35px;
	}
    .main_player{
		width: 100%;
		height: 265px;
	}
	.other_video{
		width: 100%;
		height: 100px;
		margin-top: 5px;
		margin-bottom: 5px;
		background-color: ;
		border-top-style:solid;
		border-top-width: 0px;
		border-top-color: #8a8a8a;
	}
	.other_video image{
		width: 30%;
		height: 90%;
		border-radius: 10px;
		margin-top: 8px;
		margin-left: 20px;
		margin-right: 0;
	}
	.intruduce{
		width: 60%;
		height: 100px;
		float: right;
	}
	.other_video_title{
		width: 100%;
		white-space: nowrap;
		overflow: hidden;
		display: block;
		text-overflow: ellipsis;
	}
	.main_title{
		margin: 20px 10% 5px 5%;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		font-size: 18px;
	}
	.main_title_wrap{
		margin: 20px 10% 5px 5%;
		font-size: 18px;
	}
	.more_button{
		position:absolute;
		width: 5%;
		height: 15px;
		top: 10px;
		right: 5%;
		transition: all 0.4s;
		margin-top: 15px;
	}
	.text_bar{
		margin-top: 20px;
		margin-left: 5px;
		width: 90%;
	}
	.loding_img{
		width: 100%;
		height: 265px;
		filter: brightness(0.5);
		background-position: center;
		background-repeat: no-repeat;
		background-size: cover;
	}
	.loding_text{
		position: absolute;
		top: 275px;
		margin-left: 10px;
		color: white;
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
	    width: 45%;
		height: 45%;
		margin-left: 20%;
	    margin-right: 20%;
	    border-radius: 10%;
		margin-top: 5%;
	}
	.fast_function_title{
	    font-size: 15px;
		line-height: 20px;
		margin-top: 5px;
	}
	.download_pross{
		position: absolute;
		width: 8%;
		aspect-ratio: 1;
		left: 65%;
		transform: translate(15%);
		margin-top: 5px;
	}
	.download_pross .progress{
		position: absolute;
		bottom: 0px;
		background-color: red;
		border-radius: 2.5px;
		height: 5px;
	}
</style>
