<template>
  <view class="img-box">
    <!--主要图片显示窗口-->
    <image v-show="loaded && !showError" class="img animation-fade-500" :src="src" :mode="mode" @load="loadImg()" @error="error()"></image>
    <!--加载图片-->
    <image v-show="!loaded" class="img" src="../../static/load.gif" :mode="mode"></image>
    <!--    加载失败图片-->
    <image v-show="showError" class="img" src="../../static/loadImgError.png" :mode="mode"></image>
  </view>
</template>

<script>
	export default {
		name:"page-image",
		props: {
			src: {
				type: String,
				default: ""
			},
			mode:{  //弹幕容器宽度
				type:String,
				default: "aspectFill"
			}
		},
    created() {
      if (this.src===""){
        this.loaded=true
        this.showError=true
      }
    },
		data() {
			return {
				loaded:false,
        showError:false
			};
		},
		methods:{
			loadImg(){
        this.loaded=true
			},
			error(){
        this.loaded=true
        this.showError=true
      }
		}
	}
</script>

<style>
.img-box{
  overflow: hidden;
}

.img{
  width: 100%;
  height: 100%;
}

</style>
