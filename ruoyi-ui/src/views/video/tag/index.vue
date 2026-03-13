<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px" @submit.native.prevent>
      <el-form-item label="标签名称" prop="vid">
        <el-input v-model="queryParams.dictLabel" placeholder="请输入标签名称" clearable @keyup.enter.native="handleQuery"/>
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
          v-hasPermi="['video:week:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['video:week:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['video:week:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5" v-if="false">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['video:week:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="weekList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="标签名称" align="center" prop="dictLabel" ></el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
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

    <!-- 添加或修改视频周期对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标签名称" >
          <el-input v-model="form.dictLabel"/>
        </el-form-item>
        <el-form-item label="标记视频" prop="dictValue" v-if="title==='修改视频标签'">
          <el-checkbox-group v-model="tagedVideoIds" class="video-tag-box" v-if="form.tagVideoIds.length!==0">
            <el-checkbox v-for="video in form.tagVideoIds" :label="video" :key="video">
              {{ videoIndex.find(v => v.value === video)? videoIndex.find(v => v.value === video).label: '视频不存在' }}
            </el-checkbox>
          </el-checkbox-group>
          <span v-else>该标签未标记过任何视频</span>
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
import {addTag, delTag, getTag, listTag, updateTag} from "@/api/video/tags";
import {getVideoDict} from "@/api/dict/dict";


export default {
  name: "Week",
  data() {
    return {
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
      // 视频周期表格数据
      weekList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      tagedVideoIds:[],
      videoIndex:[],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    }
  },
  created() {
    getVideoDict().then(res=>{
      this.videoIndex=res.data
    })
    this.getList()
  },
  methods: {
    /** 查询视频周期列表 */
    getList() {
      this.loading = true
      listTag(this.queryParams).then(response => {
        this.weekList = response.rows
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
        tagVideoIds:[],
        dictLabel:""
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
      this.ids = selection.map(item => item.dictCode)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加视频标签"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.dictCode || this.ids
      getTag(id).then(response => {
        this.form = response.data
        this.open = true
        this.tagedVideoIds=JSON.parse(JSON.stringify(this.form.tagVideoIds))
        this.title = "修改视频标签"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.title==="修改视频标签") {
            this.form.tagVideoIds = this.tagedVideoIds
            updateTag(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            this.form.dictValue="1"
            this.form.dictType='video_tag'
            addTag(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.dictCode || this.ids
      this.$modal.confirm('删除该视频标签后已存在的视频标记将一并删除，是否继续？').then(function() {
        return delTag(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/week/export', {
        ...this.queryParams
      }, `week_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>

<style>
.video-tag-box{
  display: flex;
  flex-direction: column;
  max-height: 180px;
  border:solid 1px #dadde4;
  padding: 5px;
  border-radius: 5px;
  overflow-y: auto;
}
</style>
