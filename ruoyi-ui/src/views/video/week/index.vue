<template>
  <div class="app-container" v-if="!autoUpdateOpen">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="视频名称" prop="vid">
        <el-select v-model="queryParams.vid" placeholder="请选择一个视频" filterable style="width: 100%">
          <el-option
            v-for="item in videoIndex"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="更新时间" prop="week">
        <el-select v-model="queryParams.week" placeholder="请选择一个更新时间" style="width: 100%">
          <el-option
            v-for="item in dict.type.week"
            :key="item.value"
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
      <el-table-column label="视频名称" align="center" prop="id" >
        <template slot-scope="scope">
          {{ videoIndex.find(v => v.value === scope.row.vid).label}}
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" >
        <template slot-scope="scope">
          <dict-tag :options="dict.type.week" :value="scope.row.week"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['video:week:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['video:week:remove']"
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
        <el-form-item label="视频名称" >
          <el-select v-model="form.vid" placeholder="请选择一个视频" filterable style="width: 100%">
            <el-option
              v-for="item in videoIndex"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="更新时间">
          <el-select v-model="form.week" placeholder="请选择一个时间" style="width: 100%">
            <el-option
              v-for="item in dict.type.week"
              :key="parseInt(item.value)"
              :label="item.label"
              :value="parseInt(item.value)"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
  <div class="app-container" v-else>
    <el-alert
      title="温馨提示"
      type="warning"
      description="开启了资源自动更新后将无法编辑周期表，若要手动编辑周期表，请关闭资源自动更新"
      :closable="false"
      show-icon>
    </el-alert>
  </div>
</template>

<script>
import { listWeek, getWeek, delWeek, addWeek, updateWeek } from "@/api/video/week"
import dict from "@/utils/dict";
import {getVideoDict} from "@/api/dict/dict";

export default {
  dicts: ['week'],
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
      autoUpdateOpen:false,
      videoIndex:[],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        week: null,
        vid:null
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
    this.getConfigKey("video.autoupdate.enable").then(res=>{
      this.autoUpdateOpen=res.msg==="true"
    })
    getVideoDict().then(res=>{
      this.videoIndex=res.data
    })
  },
  methods: {
    dict,
    /** 查询视频周期列表 */
    getList() {
      this.loading = true
      listWeek(this.queryParams).then(response => {
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
        id: null,
        week: null
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
      this.title = "添加视频周期"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getWeek(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改视频周期"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.title==="修改视频周期") {
            updateWeek(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addWeek(this.form).then(response => {
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
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除视频周期编号为"' + ids + '"的数据项？').then(function() {
        return delWeek(ids)
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
