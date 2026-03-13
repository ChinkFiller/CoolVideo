<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="视频" prop="vid">
        <el-select
          style="display: flex"
          v-model="queryParams.vid"
          filterable
          placeholder="请选择需要搜素的视频"
        >
          <el-option
            v-for="item in videoDict"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:videoStorage:add']"
        >添加视频</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-setting"
          size="mini"
          @click="handleMultiPluginParser"
          v-hasPermi="['system:parser:list']"
        >多源解析设置</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:videoStorage:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="videoStorageList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" />
      <el-table-column label="视频名称" align="center" >
        <template slot-scope="scope">
          {{ videoDict.find(v=>{return v.value===scope.row.vid}).label }}
        </template>
      </el-table-column>
      <el-table-column label="视频分集" align="center" prop="part" />
      <el-table-column label="文件路径" align="center" prop="filePath" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:videoStorage:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:videoStorage:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />



    <!-- 添加或修改视频资源信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" :rules="rules" width="500px" @close="handleClose" @opened="handleOpen">
      <el-dialog :visible.sync="showUploadProcess"
                 width="600px"
                 :show-close="false"
                 :close-on-press-escape="false"
                 :close-on-click-modal="false"
                 :append-to-body="true"
                 top="200px"
      >
        <el-progress :percentage="uploadProcess"></el-progress>
      </el-dialog>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="视频信息" prop="vid">
          <el-select
            style="display: flex"
            v-model="form.vid"
            filterable
            placeholder="请选择视频信息"
          >
            <el-option
              v-for="item in videoDict"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分集" prop="part">
          <el-input v-model="form.part" type="number" placeholder="请输入对应的分集" />
        </el-form-item>
        <el-form-item label="视频上传">
          <video-file-upload
            ref="preview"
            :url="form.filePath"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listVideoStorage, getVideoStorage, delVideoStorage, addVideoStorage, updateVideoStorage } from "@/api/video/videoStorage";
import { getVideoDict} from "@/api/dict/dict";
import VideoFileUpload from "@/components/VideoFileUpload/index.vue";
import dict from "@/utils/dict";
import {uploadFileToOssByChunk} from "@/utils/fileUploader";

export default {
  name: "VideoStorage",
  components: {VideoFileUpload},
  data() {
    return {
      uploadProcess:0,
      showUploadProcess:false,
      videoDict:[],
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 视频资源信息表格数据
      videoStorageList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        vid:null,
        pageNum: 1,
        pageSize: 10,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        vid:{ required: true, message: '请选择一个视频', trigger: 'blur' },
        part:{ required: true, message: '输入分集', trigger: 'blur' }
      }
    }
  },
  created() {
    // 获取主要存储数据
    this.getList()
    // 获取字典
    getVideoDict().then(res=>{
      this.videoDict = res.data
    })

  },
  methods: {
    dict,
    /** 查询视频资源信息列表 */
    getList() {
      this.loading = true
      listVideoStorage(this.queryParams).then(response => {
        this.videoStorageList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        vid: null,
        part: null,
        filePath: null,
        ossId: null,
        mode: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加视频资源信息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getVideoStorage(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改视频资源信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 判断视频文件是否存在
          if (!this.$refs.preview.hasFile()){
            this.$message.error("必须上传一个视频!")
            return
          }
          const file=this.$refs.preview.getFile()
          if (file!=null){
            this.showUploadProcess=true
            this.uploadProcess=0
            uploadFileToOssByChunk(file, {
              disableAutoMerge:true,
              merge:(data)=>{
                this.showUploadProcess=false
                this.form.fileMd5=data.fileMd5
                this.form.fileName=data.fileName
                this.form.chunkCount=data.chunkCount
                const loading=this.$loading({
                  lock: true,
                  text: '合并文件中...',
                  spinner: 'el-icon-loading',
                  background: 'rgba(0, 0, 0, 0.7)'
                })
                if (this.form.id != null) {
                  updateVideoStorage(this.form).then(response => {
                    loading.close();
                    this.$modal.msgSuccess("修改成功")
                    this.open = false
                    this.getList()
                  }).catch(e=>{
                    loading.close();
                    this.open = false
                    this.getList()
                  })
                } else {
                  addVideoStorage(this.form).then(response => {
                    loading.close();
                    this.$modal.msgSuccess("新增成功")
                    this.open = false
                    this.getList()
                  }).catch(e=>{
                    loading.close();
                    this.open = false
                    this.getList()
                  })
                }
              },
              process: (a)=>{
                // 写入进度
                this.uploadProcess=parseInt(a)
              },
              success: (fileId)=>{

              },
              error: (e)=>{
                this.showUploadProcess=false
                this.$message.error("上传失败:"+e.message)
              }
            })
          }else{
            if (this.form.id != null) {
              updateVideoStorage(this.form).then(response => {
                this.$modal.msgSuccess("修改成功")
                this.open = false
                this.getList()
              })
            } else {
              addVideoStorage(this.form).then(response => {
                this.$modal.msgSuccess("新增成功")
                this.open = false
                this.getList()
              })
            }
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除视频资源信息编号为"' + ids + '"的数据项？').then(function() {
        return delVideoStorage(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/video/storage/export', {
        ...this.queryParams
      }, `videoStorage_${new Date().getTime()}.xlsx`)
    },
    handleClose(){
      this.$refs.preview.reset()
    },
    handleOpen(){
      this.$refs.preview.init()
    },
    handleMultiPluginParser(){
      this.$router.push("/video/storage/parser/index")
    }
  }
}
</script>

<style>
.mini-label{
  font-size: 12px;
  margin-right: 10px;
  font-weight: bold;
  color: #666;
}
</style>
