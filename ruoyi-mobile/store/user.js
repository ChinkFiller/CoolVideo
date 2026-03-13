import { defineStore } from 'pinia';

export const userStore = defineStore('user', {
	state: () => {
		return {
			token: uni.getStorageSync('token')
		};
	},
	actions: {
		clearUserLoginState(){
			this.token = ''
			uni.removeStorageSync('token')
		},
		setUserLoginState(info){
			this.token=info.token
			uni.setStorageSync('token', info.token)
		}
	},
	getters: {
		getUserToken(){
			return this.token;
		}
	}
});
