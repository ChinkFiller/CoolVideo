import {getUserProfile, updateUserPassword, updateUserProfile} from "../api/user/profile";



export const settingFuncs={
    accountInfo:{
        username:{
            type:"button",
            value:"",
            label:"邮箱",
            init(){
                getUserProfile().then(res=>{
                    this.value=res.data.data.email
                })
            },
            click(options){
                uni.navigateTo({
                    url:"/pages/setting/accountInfo"
                })
            }
        },
        nickname:{
            type:"text-edit",
            label:"昵称",
            value:"",
            title:"请输入新的昵称",
            init(){
                getUserProfile().then(res=>{
                    this.value=res.data.data.nickName
                })
            },
            async setter(options){
                const res=await getUserProfile()
                updateUserProfile({
                    nickName: options.value,
                    email: res.data.data.email
                }).then(() => {
                    options.messageBox.alert({
                        title: '提示',
                        msg:'昵称修改成功'
                    })
                    this.value=options.value
                }).catch(err => {
                    console.log(err)
                    options.messageBox.alert({
                        title: '提示',
                        msg:'昵称修改失败'
                    })
                })
            }
        },
        password:{
            type:"text-edit",
            value:"",
            label:"密码",
            title:"请输入新的密码",
            init(){

            },
            setter(options){
                let oldPassword=""
                options.messageBox.prompt({
                    title: "输入旧的密码",
                    inputValue:oldPassword ,
                }).then(r=>{
                    updateUserPassword({
                        newPassword: options.value,
                        repeatPassword:options.value,
                        oldPassword: r.value
                    }).then(res=>{
                        options.messageBox.alert({
                            title: '提示',
                            msg: '密码修改成功'
                        })
                    }).catch(e=>{
                        options.messageBox.alert({
                            title: '提示',
                            msg: e.message
                        })
                    })
                })

            }
        }
    },
    player:{
        mode:{
            type:"select",
            label:"播放器解析模式",
            value:"",
            selectOptions:[
                "render.js",
                "weex"
            ],
            init(){
                this.value=uni.getStorageSync("playerMode")||"render.js"
            },
            setter(options){
                this.value=options.value
                uni.setStorageSync("playerMode",options.value)
            }
        }
    },
    download:{
        used:{
            type:"text",
            label:"已下载数量",
            value:"50",
            init(){
                this.value=50
            }
        }
    },
    storageClean:{
        clean:{
            type:"button",
            label:"清理缓存",
            click:(options)=>{
                options.messageBox.alert({
                    title:"提示",
                    msg:"清理缓存成功"
                })
            }
        }
    }
}
