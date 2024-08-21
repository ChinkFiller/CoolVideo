function do_request(url,method="GET",pama={},success_func,timeout=1200){
	function handelData(res){
		if (res.errMsg.includes("request:fail")){//判断是否请求失败
			uni.showToast({
				icon:"error",
				title:"未连接到网络"
			}) 
			success_func({'error':1})//网络错误
		}else{
			if (res.data.error==0 && res.statusCode==200){//判断服务器查找信息是否错误
				success_func({'error':0,'data':res.data.data})
			}else{
				if (res.data.msg=="login_disable"){
					success_func({'error':2})//登录信息失效，返回2
				}else{
					success_func({'error':1,"msg":res.data.msg})//服务错误，返回1
				}
			}
		}
	}
	let head={token:""}
	if (uni.getStorageSync("login_token")!=""){ 
		head["token"]=uni.getStorageSync("login_token").token
	}
	uni.request({
		url:uni.getStorageSync('serverUrl')+url,
		method:method,
		data:pama,
		header:head,
		success:handelData,
		fail:handelData,
		timeout:timeout
	})
}

export default{
	do_request
}