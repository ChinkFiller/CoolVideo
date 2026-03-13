import { defineStore } from 'pinia';
import {lightTheme, darkTheme, blueTheme} from "../constant/themeConstant";

export const themeStore = defineStore('theme', {
    state: () => {
        return {
            theme: uni.getStorageSync('theme')
        };
    },
    actions: {
        setDarkMode(){
            this.theme=darkTheme
            uni.setStorageSync('theme', this.theme)
        },
        setLightMode(){
            this.theme=lightTheme
            uni.setStorageSync('theme', this.theme)
        },
        setBlurMode(){
            this.theme=blueTheme
            uni.setStorageSync('theme', this.theme)
        }
    },
    getters: {
        getTheme(){
            return this.theme;
        }
    }
});
