<template>
	<view>
		<image style="width: 100%;height: 100%;border-radius: 10px;" :src="title" :mode="mode" v-show="!loaded"></image>
		<image style="width: 100%;height: 100%;border-radius: 10px;" :src="src" :mode="mode" @load="loadImg()" @error="error()" v-show="loaded"></image>
	</view>
</template>

<script>
	export default {
		name:"my-img",
		props: {
			src: {
				type: String,
				default: ""
			},
			mode:{  //弹幕容器宽度
				type:String,
				default: "aspectFill"
			},
		},
		data() {
			return {
				loaded:false,
				title:"../../static/load.gif"
			};
		},
		methods:{
			loadImg(){
				this.loaded=true
			},
			error(){
				this.title="../../static/nodata.png"
			}
		}
	}
</script>

<style>

</style>