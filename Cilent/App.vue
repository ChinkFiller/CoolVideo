
<script>
	export default {
		onLaunch: function() {
			//初始化变量
			if (uni.getStorageSync('dark')==''){
				uni.setStorageSync('dark',false);
			}
			if (uni.getStorageSync('history')==''){
				uni.setStorageSync('history',new Array(1).fill(1700));
			} 
			if (uni.getStorageSync("allow")==''){
				uni.setStorageSync("allow",[17000])
			}
			if(uni.getStorageSync("serverUrl")==""){
				uni.setStorageSync("serverUrl","https://video.conyafertools.work")
			}
			if(uni.getStorageSync("testMode")==""){
				uni.setStorageSync("testMode",false)
			}
			//plus.navigator.closeSplashscreen()
			setTimeout(() => {
				plus.navigator.closeSplashscreen();
			}, 2000);
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		},
		globalData:{
			dark:uni.getStorageSync('dark'),
			isPlayerFullScreen:false,
			version:'1.27',
			supportServerVersion:'1.26',
			isBoot:true
		}
	}
</script>

<style>
	/*每个页面公共css */
</style>
