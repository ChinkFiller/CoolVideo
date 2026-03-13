<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="插件名称" prop="pluginId">
        <el-input
          v-model="queryParams.pluginId"
          placeholder="请输入插件名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          icon="el-icon-s-shop"
          size="mini"
          @click="handleMarket"
        >插件市场</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-upload"
          size="mini"
          @click="handleFileInstall"
        >通过文件安装插件</el-button>
      </el-col>

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="pluginList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="插件名称" align="center" prop="pluginId" width="150"/>
      <el-table-column label="描述" align="center" prop="description"/>
      <el-table-column label="提供者" align="center" prop="provider" width="120"/>
      <el-table-column label="版本" align="center" prop="version" width="120"/>
      <el-table-column label="状态" align="center" key="status">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.state" @change="handleStatusChange(scope.row)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="300">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-setting"
            @click="handleSetting(scope.row)"
            v-hasPermi="['video:function:edit']"
          >设置</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleUninstall(scope.row)"
            v-hasPermi="['video:function:remove']"
          >卸载</el-button>
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

    <!-- 添加或修改主页快速功能对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px">
      <div v-html="settingPage"></div>
    </el-dialog>

    <el-dialog title="插件文件上传" :visible.sync="fileUploadOpen" width="400px">
      <el-upload
        drag
        accept=".zip,.jar"
        action="/setting/plugin/upload"
        ref="upload"
        :http-request="handleUpload"
        :auto-upload="false"
        :on-error="handleUploadError"
        :on-success="handleUploadSuccess"
        :on-change="handleFileChange"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传zip/jar文件，且不超过50MB</div>
      </el-upload>
      <div style="display: flex;width: 100%;margin-top: 20px">
        <el-button style="margin: auto" type="primary" @click="uploadFileToServer">安装以上插件</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getPluginSetting,
  installPluginByFile,
  listPlugins,
  startPlugin,
  stopPlugin,
  uninstallPlugin
} from "@/api/setting/plugin";


export default {
  name: "Plugin",
  data() {
    return {
      // 遮罩层
      loading: true,
      fileList:[],
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 文件上传弹窗是否显示
      fileUploadOpen:false,
      // 总条数
      total: 0,
      // 最多上传文件数
      maxUpload:10,
      // 主页快速功能表格数据
      pluginList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      settingPage: "",
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        pluginId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询主页快速功能列表 */
    getList() {
      this.loading = true
      listPlugins(this.queryParams).then(response => {
        this.pluginList = response.data
        this.total = this.pluginList.length
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
        fid: null,
        title: null,
        href: null,
        img: null
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
      this.ids = selection.map(item => item.fid)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 通过文件安装插件按钮操作 */
    handleFileInstall() {
      this.fileUploadOpen=true
    },
    /** 插件设置按钮操作 */
    handleSetting(row) {
      const pluginId = row.pluginId
      getPluginSetting(pluginId).then(response => {
        this.settingPage = response.msg
        this.open = true
        this.title = "插件设置"
      })
    },
    //修改插件运行状态
    handleStatusChange(row){
      if (row.state){
        startPlugin(row.pluginId).then(response => {
          this.$message.success("启动插件成功")
          this.getList()
        })
      } else {
        stopPlugin(row.pluginId).then(response => {
          this.$message.success("停止插件成功")
          this.getList()
        })
      }
    },
    /** 删除按钮操作 */
    handleUninstall(row) {
      const fids = row.pluginId || this.ids
      this.$modal.confirm('是否确认卸载"' + fids + '"插件？').then(function() {
        //卸载插件
        return uninstallPlugin(fids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("卸载成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/function/export', {
        ...this.queryParams
      }, `function_${new Date().getTime()}.xlsx`)
    },
    handleUploadError(res, file) {
      this.$modal.msgError("上传失败")
    },
    handleUploadSuccess(res, file, fileList) {
      this.$modal.msgSuccess("上传成功")
    },
    handleFileChange(file) {
      if (file.size > 500 * 1024) {
        this.$modal.msgError("文件大小不能超过500kb")
        return false
      }
    },
    //处理上传安装插件
    handleUpload(e){
      const fd = new FormData()
      fd.set("file", e.file)
      installPluginByFile(fd).then(res=>{
        this.$modal.msgSuccess("插件安装成功")
        this.fileUploadOpen=false
        this.getList()
      })
    },
    //确认安装的检查函数
    uploadFileToServer(){
      this.$refs.upload.submit()
      this.$refs.upload.clearFiles()
    }
  }
}
</script>
