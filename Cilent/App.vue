<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
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
				uni.setStorageSync("serverUrl","http://video.conyafertools.work")
			}
			if(uni.getStorageSync("testMode")==""){
				uni.setStorageSync("testMode",false)
			}
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
		}
	}
</script>

<style>
	/*每个页面公共css */
</style>
