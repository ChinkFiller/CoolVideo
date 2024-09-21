<template>
	<!-- 背景颜色设置，根据是否为暗色模式来设置 -->
	<page-meta page-style="background:#343434" v-if="is_dark"></page-meta>
	<page-meta page-style="background:#eef0f1" v-else></page-meta>
	<web-view :src="url"></web-view>
</template>

<script>
	export default {
		data() {
			return {
				is_dark:false,
				url:""
			}
		},
		onLoad(e) {
			this.is_dark=getApp().globalData.dark
			this.url=e.url
		}
	}
</script>

<style>
	.main_box{
		width: 100%;
		height: 100%;
	}
	.main_box iframe{
		width: 100%;
		height: 100%;
		border: 0px ;
	}
</style>
