
<template>
	<page-meta page-style="background:#343434" v-if="is_dark"></page-meta>
	<page-meta page-style="background:#eef0f1" v-else></page-meta>
	<view class="full" v-if="is_asking" @click="checkCanBack">
		<view class="asking" @click="test()">
			<p style="font-size: 18px;margin-top: 5px;">人机验证</p>
			<br />
			<view class="vcode_people">
			  <canvas canvas-id="canvas" @click="updateImageCode"></canvas>
			</view>
			<view class="vcode-input" :style="{borderColor: `${bordercolor}`}">
				<input type="text" v-model="checkVcodeText">
			</view>
			<view class="option">
				<button @click="is_asking=false;">取消</button>
				<button style="color: red;" @click="checkVcode()">确定</button>
			</view>
		</view>
	</view>
	<view class="main_fun" :style="{transform:`translateY(${-isRegist*45}px)`}">
		<image src="../../static/bgicon.png" mode=""></image>
		<input type="text" placeholder="邮箱地址" v-model="username" v-if="is_dark" style="border-color: #eef0f1;color: #eef0f1;"/>
		<input type="text" placeholder="邮箱地址" v-model="username" v-else/>
		<input type="safe-password" placeholder="密码" v-model="pawd" :password="showps" v-if="is_dark" style="border-color: #eef0f1;color: #eef0f1;"/>
		<input type="safe-password" placeholder="密码" v-model="pawd" :password="showps" v-else/>
		<view class="show_ps" v-if="is_dark"><i :class="[showps_icon]" @click="change_icon()" style="color: #eef0f1;"></i></view>
		<view class="show_ps" v-else><i :class="[showps_icon]" @click="change_icon()" ></i></view>
		<input type="text" placeholder="验证码" v-model="vcode" v-if="is_dark && isRegist" style="border-color: #eef0f1;color: #eef0f1;width: 70%;"/>
		<input type="text" placeholder="验证码" v-model="vcode" v-if="isRegist&&!is_dark" style="border-color: #343434;color: #343434;width: 70%;"/>
		<view class="vcode" v-show="isRegist">
			<button :disabled="!canSend" v-if="is_dark" @click="checkRealPeople" style="background-color: #5f6161;color:#eef0f1;">{{showText}}</button>
			<button :disabled="!canSend" @click="checkRealPeople" v-else>{{showText}}</button>
		</view>
		<view class="else-function" v-if="!isRegist">
			<text class="lost-password">忘记密码</text>
			<text class="regist" @click="Regist()">注册</text>
		</view>
		<view style="color: #07aef5;margin-top: 5px;" v-if="isRegist" @click="isRegist=false">返回登录</view>
		<button @click="login()" class="login"><i :class="[login_icon]"></i></button>
	</view>
</template>

<script>
	import ajax from '../../common/ajax';
	import { Mcaptcha } from '../../common/mcaptcha.js'
	import md5 from "../../common/md5.js"
	export default {
		data() {
			return {
				username:"",
				pawd:"",
				vcode:"",
				showps:true,
				showps_icon:'fa fa-eye-slash',
				login_icon:'fa fa-arrow-right',
				mcaptcha:null,
				is_dark:false,
				isRegist:false,
				canSend:true,
				showText:"发送验证码",
				timer:null,
				is_asking:false,
				checkVcodeText:'',
				isCanBack:true,
				bordercolor:"black"
			}
		},
		onLoad(){
			this.is_dark=getApp().globalData.dark
			this.mcaptcha = new Mcaptcha({
			     el: 'canvas',
			     width: 80,
			     height: 45,
			     createCodeImg: ""
			   });
		},
		methods: { 
			login(){
				if (this.username=='' || this.pawd==''){
					uni.showToast({
						title:"账号或密码不能为空",
						icon:'none'
					})
					return
				}
				this.login_icon='fa fa-spinner fa-pulse'
				if (this.isRegist){
					ajax.do_request("/register","POST",{username:this.username,"password":this.pawd,"vcode":this.vcode},(res)=>{
						if (!res.error){
							uni.showToast({
								title:"注册成功！"
							})
							this.login_icon='fa fa-arrow-right'
							setTimeout(()=>{
								uni.setStorageSync("login_token",{name:res.data.name,token:res.data.token,icon:res.data.icon})
								uni.reLaunch({
									url:"/pages/index/index",
									animationType:'slide-in-left',
									animationDuration:200
								})
							},400)
						}else{
							if (res.msg=="vcode error"){ 
								uni.showToast({
									title:"验证码错误!",
									icon:'error'
								})
								this.login_icon='fa fa-arrow-right'
							}else{
								uni.showToast({
									title:"注册失败!",
									icon:'error'
								})
								this.login_icon='fa fa-arrow-right'
							}
						}
					})
				}else{
					ajax.do_request('/login','POST',{'username':this.username,'password':this.pawd},(res)=>{
					if (!res.error){
						if (res.data.login_state){
							//加载登录信息
							uni.setStorageSync("login_token",{name:res.data.name,token:res.data.token,icon:res.data.icon})
							//刷新历史记录
							uni.setStorageSync("history",[17000]);
							//刷新追番
							uni.setStorageSync("allow",[17000]);
							uni.reLaunch({
								url:"/pages/index/index",
								animationType:'slide-in-left',
								animationDuration:200
							})
						}else{
							this.login_icon='fa fa-arrow-right'
							uni.showToast({
								title:res.data.msg,
								icon:'none'
							})
							this.login_icon='fa fa-arrow-right'
						}
					}else{
						this.login_icon='fa fa-arrow-right'
						uni.showToast({
							title:'网络加载失败，请稍后重试',
							icon:'none'
						})
					}
					})
				}
			},
			change_icon(){
				if (this.showps){
					this.showps=!this.showps
					this.showps_icon='fa fa-eye'
				}else{
					this.showps=!this.showps
					this.showps_icon='fa fa-eye-slash'
				}
			},
			test(){
				this.isCanBack=false
			},
			// 刷新验证码
			updateImageCode() {
			  this.mcaptcha.refresh()
			},
			Regist(){
				this.isRegist=true
				this.bordercolor="balck"
				this.checkVcodeText=""
			},
			judgeEmail(email){
				const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
				return emailRegex.test(email);
			},
			checkCanBack(){
				if (this.isCanBack){
					this.is_asking=false
				}else{
					this.isCanBack=true
				}
			},
			checkVcode(){
				if (this.mcaptcha.validate(this.checkVcodeText)){
					this.sendEamil()
					this.is_asking=false
				}else{
					uni.showToast({
						icon:"error",
						title:"人机验证失败"
					})
					this.bordercolor="red"
					//this.updateImageCode()
				}
			},
			checkRealPeople(){
				this.bordercolor="balck"
				this.checkVcodeText=""
				if(this.username==""){
					uni.showToast({
						title:"请填入邮箱地址",
						icon:'error'
					})
				}else{
					if (this.judgeEmail(this.username)){
						this.is_asking=true
						setTimeout(()=>{
							this.updateImageCode()
						},150)
					}else{
						uni.showToast({
							title:"邮箱地址无效",
							icon:'error'
						})
					}
				}
			},
			sendEamil(){
				ajax.do_request("/get_vcode","GET",{mailAddress:this.username},(res)=>{
					 if (!res.error){
						uni.showToast({
							title:"验证码发送成功!"
						})
						this.showText=`${120}秒`
						this.canSend=false
						this.timer=setInterval(()=>{
								let time=parseInt(this.showText.replace(/[^\d.]/g, ""));
								this.showText=`${time-1}秒`
								if (time==0){
									clearInterval(this.timer)
									this.showText="重新发送"
									this.canSend=true
								}
						},1000)
					 }else{
						uni.showToast({
							title:res.msg,
							icon:'error'
						})
					 }
						})
			}
		}
	}
</script>

<style>
	@import url("/common/font.css");
	page{
		overflow: hidden;
	}
	.main_fun{
		position: absolute;
		width: 80%;
		top: 12.5%;
		height: 75%;
		margin-left:10%;
		text-align: center;
	}
	.main_fun input{
		margin-top: 20px;
		height: 40px;
		border-radius: 20px;
		border: solid 2px black;
		text-indent: 10px;
	}
	.main_fun image{
		width: 100%;
	}
	.main_fun .login{
		width: 80px;
		height: 80px;
		border-radius: 40px;
		line-height: 85px;
		margin-top: 20px;
		margin-left: auto;
		margin-right: auto;
		background: linear-gradient(to right,#2a50fe, #b029ff);
	}
	.main_fun button i{
		font-size: 30px;
	}
	.show_ps{
		font-size: 20px;
		position: relative;
		left: 85%;
		bottom: 35px;
		text-align: start;
		height: 5px;
	}
	.else-function text{
		color: #07aef5;
	}
	.else-function .lost-password{
		margin-right: 60%;
	}
	.vcode{
		position: relative;
		left: 72.5%;
		bottom: 40px;
		height: 5px;
	}
	.vcode button{
		width: 30%;
		right: 35%;
		font-size: 14px;
		padding: 0px;
		border-radius: 10px;
	}
	.vcode_people canvas{
		width:40%;
		height:86rpx;
		background-color: aliceblue;
		position: relative;
		left: 30%;
	}
	.vcode-input{
		border: solid 2px black;
		margin: 10px 10% 10px 10%;
		border-radius: 10px;
	}
	.full{
		position: absolute;
		width: 100%;
		height: 100%;
		background-color: rgba(0, 0, 0, 0.6);
		z-index: 1;
	}
	.asking{
		position: absolute;
		width: 60%;
		aspect-ratio: 1.2;
		background-color: white;
		margin: 0 20% 0 20%;
		top: 50%;
		transform: translateY(-50%);
		text-align: center;
		border-radius: 10px;
		pointer-events: all;
		z-index: 5;
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
