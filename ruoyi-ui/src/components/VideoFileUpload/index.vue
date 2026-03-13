<template>
  <el-upload
    v-if="isShow"
    action="#"
    :limit="1"
    accept="video/*"
    ref="upload"
    :multiple="false"
    :file-list="fileList"
    list-type="picture-card"
    :auto-upload="false"
    :on-change="handleFileChange"
  >
    <div class="el-upload__tip" slot="tip">只能上传视频类型的文件，且不超过1GB</div>
    <i slot="default" class="el-icon-plus"/>
    <div slot="file" slot-scope="{file}">
      <video-cover-preview
        v-if="fileList[0].raw!==undefined"
        class="el-upload-list__item-thumbnail"
        :file="fileList[0].raw"
      />
      <video-cover-preview
        v-else
        class="el-upload-list__item-thumbnail"
        :url="url"
      />
      <span class="el-upload-list__item-actions">
          <span
            v-if="fileList[0].raw===undefined"
            class="el-upload-list__item-delete"
            @click="handleDownload()"
          >
            <i class="el-icon-download"></i>
          </span>
          <span
            class="el-upload-list__item-delete"
            @click="handleFileRemove()"
          >
            <i class="el-icon-delete"></i>
          </span>
        </span>
    </div>
  </el-upload>
</template>

<script>
import VideoCoverPreview from "@/components/VideoCoverPreview/index.vue";

export default {
  name: 'video-file-upload',
  components: {VideoCoverPreview},
  props: {
    url:{
      type: String,
      default: '',
      fileExist:false,
    }
  },
  data() {
      return {
        fileList:[],
        isShow:false
      }
  },
  methods: {
    hideAddBtn(){
      setTimeout(()=>{
        document.querySelector('.el-upload--picture-card').style.display = 'none';
      },100)
    },
    showAddBtn(){
      setTimeout(()=>{
        document.querySelector('.el-upload--picture-card').style.display = 'block';
      },1000)
    },
    handleFileRemove() {
      //显示新增文件
      this.showAddBtn()
      this.$refs.upload.clearFiles()
      this.fileExist=false
    },
    handleFileChange(file){
      if (file.size<1024*1024*1024){
        this.fileList[0]=file
        this.fileExist=true
        // 隐藏新增文件
        this.hideAddBtn()
      }else{
        this.$modal.msgError("文件大小不能超过1G")
      }
    },
    getFile(){
      console.log(this.fileExist)
      if (this.fileList[0].raw===undefined){
        return null
      }else{
        return this.fileList[0].raw
      }
    },
    handleDownload(){
      const a=document.createElement('a')
      a.href=this.url
      a.target='_blank'
      a.click()
    },
    handlePictureCardPreview(){

    },
    reset(){
      this.fileList=[]
      this.showAddBtn()
      // 隐藏组件
      this.isShow=false
    },
    hasFile(){
      return this.fileExist
    },
    init(){
      if (this.url!=='' && this.url!==null){
        this.fileExist=true
        // 添加一个文件列表
        this.fileList[0]={}
        // 隐藏新增文件
        this.hideAddBtn()
      }
      // 显示组件
      this.isShow=true
    }
  }
}

</script>
<style scoped lang="scss">

</style>
