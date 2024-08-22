<style lang="scss">
	.user-status-text {
		font-size: 36rpx;
		text-shadow: 
		        1px 1px 0 #000, /* 描边效果 */
		        -1px -1px 0 #000,
		        1px -1px 0 #000,
		        -1px 1px 0 #000;
	}
	.pao {  
		transform: 'translateX(100%)';  
		position: absolute;
		top: 0;
		left: 0;
	}  
	.all-content{
		position: relative;
		overflow: hidden;
	}
</style>

<template>
	<cover-view>
		<view class="all-content" :style="{width:`${screenWidth}px`,height:`${maxTop}px`,visibility:`${DanmuWatch?'visible':'hidden'}`}">
			<view class="pao" v-for="(item,index) in listData" :key="item.id" :ref="'pao_' + item.id" :style="{top:`${item.top}px`,width:`${screenWidth}px`}">
				<text class="user-status-text" :style="{color:item.color}">{{item.text}}</text>
			</view>
		</view>
	</cover-view>
	
</template>
<script>
import playerNvue from '../../pages/v_player/player.nvue';
	// #ifdef APP-PLUS
	const animation = weex.requireModule('animation')
	// #endif
	export default {
		props: {
			maxTop: {
				type: Number,
				default: 200
			},
			screenWidth:{  //弹幕容器宽度
				type:Number,
				default: 375
			}
		},
		data() {
			return {
				listData: [],
				itemId:1,
				needRemove:[],
				DanmuRun:true,
				DanmuWatch:true,
				isClear:false,
			    barrages:[],
				temBarrages:[]
			}
		},
		created() {
		},
		methods: {
			add(obj){
				this.itemId++
				let data = {
					text: obj.text,
					color:obj.color==null?'white':obj.color,
					id:this.itemId,
					top:Math.ceil(Math.random() * (this.maxTop - 40)),   //这里的40是弹幕距离弹幕容器底部的距离，可以根据自己需求修改
					nowPosition:100
				}
				this.barrages.push({
					text:obj.text,
					color:obj.color==null?'white':obj.color,
					time:obj.time
				})
				this.listData.push(data);
				setTimeout(()=>{
					this.__playDanmu(data)
				},200)
			},
			__add(obj) {
				this.itemId++
				let data = {
					text: obj.text,
					color:obj.color==null?'white':obj.color,
					id:this.itemId,
					top:Math.ceil(Math.random() * (this.maxTop - 40)),   //这里的40是弹幕距离弹幕容器底部的距离，可以根据自己需求修改
					nowPosition:100
				}
				this.listData.push(data);
				setTimeout(()=>{
					this.__playDanmu(data)
				},200)
			},
			__playDanmu(data){
				if (data.nowPosition<-100||this.isClear){
					if (this.listData.length!=0){
						this.listData.shift()
					}
					return;
				}
				if (this.DanmuRun){
					data.nowPosition-=5
					animation.transition(this.$refs['pao_' + data.id][0], {
						styles: {
							transform: `translateX(${data.nowPosition}%)`
						},
						duration: 180, //ms     
						timingFunction: 'linear',
						delay: 0,//ms  
					}, () => {
						this.__playDanmu(data)
					})
				}else{
					setTimeout(()=>{
						this.__playDanmu(data)
					},180)
				}
			},
			stop() {
				this.DanmuRun=false
			},
			run(){
				this.DanmuRun=true
			},
			show(){
				this.DanmuWatch=true
			},
			hide(){
				this.DanmuWatch=false
			},
			//动态设置弹幕
			setBarrages(datas){
				this.barrages=datas
				this.temBarrages=Array.from(this.barrages)
			},
			async clear(){
				this.isClear=true
				this.temBarrages=Array.from(this.barrages);
				await new Promise(resolve => setTimeout(resolve, 800));
				this.isClear=false
			},
			//视频更新弹幕处理函数，跳转播放是需要调用clear函数
			sendBarrageOnTime(time){
				var i=0;
				while (i<this.temBarrages.length){
					if (this.temBarrages[i].time<time+1&&this.temBarrages[i].time>time-1){
						this.__add(this.temBarrages[i])
						this.temBarrages.splice(i,1)
						continue;
					}
					i++;
				}
			}
		}

	}
</script>
<style>

</style>
