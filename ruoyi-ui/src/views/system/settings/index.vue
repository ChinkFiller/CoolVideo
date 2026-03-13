<template>
  <div class="app-container">
   <el-card>
     <div slot="header">
       系统常用设置
     </div>
     <el-form :model="form" ref="queryForm" label-width="200px" label-position="left" >
       <el-form-item label="登陆时进行人机验证">
         <el-switch
           @change="submit"
           v-model="form.captchaEnabled"
           :active-value="true"
           :inactive-value="false"
         />
       </el-form-item>
       <el-form-item label="用户注册功能">
         <el-switch
           @change="submit"
           v-model="form.registerEnabled"
           :active-value="true"
           :inactive-value="false"
         />
       </el-form-item>
       <el-form-item label="图片缓存">
         <div>已缓存图片:{{form.cacheImgNum}}个 占用空间:{{(form.cacheImgSize/1024).toFixed(2)}}MB</div>
         <el-button type="primary" @click="clearCache" :loading="clearingImg">清空缓存</el-button>
       </el-form-item>
     </el-form>
   </el-card>
  </div>
</template>

<script>
import {clearImgCache, getSystemSettings, submitSystemSetting} from "@/api/system/settings";

export default {
  name: "Settings",
  data() {
    return {
      clearingImg:false,
      form: {
      }
    }
  },
  created() {
    this.getSettings()
  },
  methods: {
    /** 查询轮播图信息列表 */
    getSettings() {
      getSystemSettings().then(res=>{
        this.form = res.data
      })
    },
    clearCache(){
      this.$confirm("确定要清空图片缓存吗？", "提示", {type: "warning"}).then(() => {
        this.clearingImg=true
        clearImgCache().then(res=>{
        setTimeout(()=>{
          this.clearingImg=false
          this.$message.success("缓存清空成功!")
          this.getSettings()
        }, 500)
        }).catch(e=> {
            this.clearingImg=false
            this.$message.error("清空缓存失败:"+e.message)
        })
      })
    },
    submit(){
      this.$refs["queryForm"].validate((valid) => {
        if (valid) {
          submitSystemSetting(this.form).then(res=>{
            this.$message.success("设置成功")
            this.getSettings()
          })
        }
      })
    }
  }
}
</script>
