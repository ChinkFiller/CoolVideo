<template>
	<view class="full" v-if="is_asking" @click="__fullClose()">
		<view class="asking" @click="touchedWindows=true">
			<p style="font-size: 18px;margin-top: 5px;">{{title}}</p>
			<br>
			<scroll-view class="content-box" scroll-y="true">{{content}}</scroll-view>
			<br>
			<view class="option">
				<button @click="is_asking=false">我知道了</button>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name:"popup-notice",
		data() {
			return {
				is_asking:false,
				title:'',
				content:''
			};
		},
		methods:{
			show(title,content){
				this.is_asking=true
				this.title=title
				this.content=content
			},
			__fullClose(){
				if (!this.touchedWindows){
					this.is_asking=false
				}
				this.touchedWindows=false
			}
		}
	}
</script>

<style lang="scss">
.full{
		position: absolute;
		width: 100%;
		height: 100%;
		background-color: rgba(0, 0, 0, 0.6);
		z-index: 100;
	}
	.asking{
		position: absolute;
		width: 60%;
		aspect-ratio: 1;
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
		color: red;
	}
	button::after {
	border: none !important;
	}
	.content-box{
		word-wrap: break-word;
		word-break: break-all;
		white-space: pre-line;
		width: 90%;
		margin-left: 5%;
		margin-right: 5%;
		text-align: start;
		height: 50%;
	}
</style>