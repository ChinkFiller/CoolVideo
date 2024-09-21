<template>
	<view class="full" v-if="is_asking" @click="__fullClose()">
		<view class="asking" @click="touchedWindows=true">
			<p style="font-size: 18px;margin-top: 5px;">{{title}}</p>
			<br>
			<p>{{content}}</p>
			<br>
			<view class="option">
				<button @click="__nostate()">取消</button>
				<button style="color: red;" @click="__okstate()">确定</button>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name:"popup-asking",
		data() {
			return {
				is_asking:false,
				title:"",
				content:"",
				options:null,
				touchedWindows:false,
				canFullClose:true
			};
		},
		methods:{
			show(title,content,canFullclose=true,option){
				this.is_asking=true
				this.title=title,
				this.content=content
				this.options=option
				this.canFullClose=canFullclose
			},
			__okstate(){
				this.is_asking=false
				this.options({state:true})
			},
			__nostate(){
				this.is_asking=false
				this.options({state:false})
			},
			__fullClose(){
				if (!this.touchedWindows&&this.canFullClose){
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