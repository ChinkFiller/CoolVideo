<template>
  <div>
    <img
      style="width: 100%;height: 100%;object-fit: cover "
      :src="coverData"
      alt="视频封面加载失败"
    />
  </div>
</template>

<script>
import {getVideoFileCover, getWebVideoCover} from "@/utils/image";

export default {
  name:"video-cover-preview",
  data(){
    return {
      coverData: null
    }
  },
  props: {
    url:{
      type: String,
      default: ""
    },
    file:{
      type: File,
      default: null
    }
  },
  created() {
    if (this.url!=="") {
      getWebVideoCover(this.url,(img)=>{
        this.coverData = img
      })
    }else if (this.file!==null) {
      getVideoFileCover(this.file,(img)=>{
        this.coverData=img
      })
    }else {
      this.coverData = ""
    }
  }
}
</script>

<style scoped lang="scss">

</style>
