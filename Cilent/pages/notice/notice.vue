<template>
	<view class="main_box">
	   <web-view :src="url"></web-view>
	</view>
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
			console.log(this.url)
		}
	}
</script>

<style>

</style>
