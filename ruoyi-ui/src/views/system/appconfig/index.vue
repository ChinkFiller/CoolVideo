<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="App名称" prop="appName">
        <el-input
          v-model="queryParams.appName"
          placeholder="请输入App的名称"
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
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:appconfig:add']"
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
          v-hasPermi="['system:appconfig:edit']"
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
          v-hasPermi="['system:appconfig:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5" v-if="false">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:appconfig:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="appconfigList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80px"/>
      <el-table-column label="App名称" align="center" prop="appName" />
      <el-table-column label="App最新版本代码" align="center" prop="appVersion" />
      <el-table-column label="App最低支持版本" align="center" prop="finalVersion" />
      <el-table-column label="AppID" align="center" prop="appId" width="150px" />
      <el-table-column label="App运行平台" align="center" width="100px">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.plt" :value="scope.row.plt"/>
        </template>
      </el-table-column>
      <el-table-column label="App最新版下载地址" align="center">
        <template slot-scope="scope">
          <el-link type="primary" :href="scope.row.updateUrl" target="_blank">{{scope.row.updateUrl}}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="App的状态" align="center" >
        <template slot-scope="{}" slot="header">
          <!-- 表头名称 -->
          <span>App状态</span>
          <el-tooltip class="item" effect="dark" placement="top" style="cursor: pointer">
            <i class="el-icon-question" style="font-size:16px;"></i>
            <!-- 悬停提示内容 -->
            <div slot="content">
              <div>可以选择启用或停用一个App</div>
              <div>停用后的App将不可使用</div>
            </div>
          </el-tooltip>
        </template>
        <template slot-scope="scope">
          <el-switch v-model="scope.row.state" active-value="1" inactive-value="0" @change="handleSwitchChange(scope.row)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="是否强制更新" align="center" prop="forceUpdate" >
        <template slot-scope="{}" slot="header">
          <!-- 表头名称 -->
          <span>强制更新</span>
          <el-tooltip class="item" effect="dark" placement="top" style="cursor: pointer">
            <i class="el-icon-question" style="font-size:16px;"></i>
            <!-- 悬停提示内容 -->
            <div slot="content">
              <div>开启后将强制App端升级为最新的版本，不再兼容老版本</div>
            </div>
          </el-tooltip>
        </template>
        <template slot-scope="scope">
          <el-switch v-model="scope.row.forceUpdate" active-value="1" inactive-value="0" @change="handleSwitchChange(scope.row)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:appconfig:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:appconfig:remove']"
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

    <!-- 添加或修改App端配置信息管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="App名称" prop="appName">
          <el-input v-model="form.appName" placeholder="请输入App的名称" />
        </el-form-item>
        <el-form-item label="App版本代码" prop="appVersion">
          <el-input v-model="form.appVersion" placeholder="请输入App的版本代码" type="number"/>
        </el-form-item>
        <el-form-item label="最低支持的版本" prop="finalVersion">
          <el-input v-model="form.finalVersion" placeholder="请输入最低支持的版本" type="number"/>
        </el-form-item>
        <el-form-item label="App运行平台" prop="plt">
          <el-select v-model="form.plt" placeholder="请选择App运行的平台" style="display: flex">
            <el-option
              v-for="item in dict.type.plt"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="AppID" prop="appId">
          <el-input v-model="form.appId" placeholder="请输入AppID" />
        </el-form-item>
        <el-form-item label="App更新地址" prop="updateUrl">
          <el-input v-model="form.updateUrl" placeholder="请输入App的最新版下载地址" />
        </el-form-item>
        <el-form-item label="新版本描述" prop="finalVersion">
          <el-input v-model="form.description" placeholder="请输入最新版本的版本描述" type="textarea"/>
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
import { listAppconfig, getAppconfig, delAppconfig, addAppconfig, updateAppconfig } from "@/api/system/appconfig"
import dict from "@/utils/dict";

export default {
  dicts: ['plt'],
  name: "Appconfig",
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
      // App端配置信息管理表格数据
      appconfigList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        appName: null,
        plt: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        appName: [
          { required: true, message: "App的名称不能为空", trigger: "blur" }
        ],
        appVersion: [
          { required: true, message: "App的版本代码不能为空", trigger: "blur" }
        ],
        appId: [
          { required: true, message: "App的ID不能为空", trigger: "blur" }
        ],
        plt: [
          { required: true, message: "App运行的平台不能为空", trigger: "change" }
        ],
        updateUrl: [
          { required: true, message: "App更新地址不能为空", trigger: "change" }
        ],
        finalVersion: [
          { required: true, message: "App最低支持版本号不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    dict,
    /** 查询App端配置信息管理列表 */
    getList() {
      this.loading = true
      listAppconfig(this.queryParams).then(response => {
        this.appconfigList = response.rows
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
        appName: null,
        appVersion: null,
        appId: null,
        plt: null,
        updateUrl: null,
        finalVersion: null,
        forceUpdate: null
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
      this.title = "添加App端配置信息管理"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getAppconfig(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改App端配置信息管理"
      })
    },
    handleSwitchChange(row){
      updateAppconfig(row).then(response => {
        this.$modal.msgSuccess("更改状态成功")
        this.getList()
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAppconfig(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            this.form.forceUpdate=0
            this.form.state=1
            addAppconfig(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除App端配置信息管理编号为"' + ids + '"的数据项？').then(function() {
        return delAppconfig(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/appconfig/export', {
        ...this.queryParams
      }, `appconfig_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
