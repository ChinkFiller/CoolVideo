<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="服务商" prop="configKey">
        <el-select v-model="queryParams.configKey" placeholder="请选择OSS服务供应商" style="display: flex">
          <el-option
            v-for="item in dict.type.cloud_provider"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="桶名称" prop="bucketName">
        <el-input
          v-model="queryParams.bucketName"
          placeholder="请输入桶名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="地区" prop="region">
        <el-input
          v-model="queryParams.region"
          placeholder="请输入地区"
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
          v-hasPermi="['system:ossconfig:add']"
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
          v-hasPermi="['system:ossconfig:edit']"
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
          v-hasPermi="['system:ossconfig:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5" v-if="false">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:ossconfig:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ossconfigList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="OSS配置ID" align="center" prop="ossConfigId" />
      <el-table-column label="云服务商" align="center" prop="configKey" >
        <template slot-scope="scope">
          <dict-tag :options="dict.type.cloud_provider" :value="scope.row.configKey"/>
        </template>
      </el-table-column>
      <el-table-column label="存储桶" align="center" prop="bucketName" />
      <el-table-column label="地区" align="center" prop="region" />
      <el-table-column align="center" prop="status" >
        <template slot-scope="{}" slot="header">
          <!-- 表头名称 -->
          <span>默认存储</span>
          <el-tooltip class="item" effect="dark" placement="top" style="cursor: pointer">
            <i class="el-icon-question" style="font-size:16px;"></i>
            <!-- 悬停提示内容 -->
            <div slot="content">
              <div>开启后将默认使用该配置为主要的存储</div>
              <div>只能有一个默认存储方式</div>
            </div>
          </el-tooltip>
        </template>
        <template slot-scope="scope">
          <el-switch v-model="scope.row.status" active-value="1" inactive-value="0" @change="handleSetDefaultConfig(scope.row)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="text"
              icon="el-icon-s-promotion"
              @click="handleTestConfig(scope.row)"
              v-hasPermi="['system:ossconfig:list']"
          >测试配置</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:ossconfig:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:ossconfig:remove']"
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

    <!-- 添加或修改对象存储配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="云服务商" prop="configKey">
          <el-select v-model="form.configKey" placeholder="请选择OSS服务供应商" style="display: flex">
            <el-option
              v-for="item in dict.type.cloud_provider"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="accessKey" prop="accessKey">
          <el-input v-model="form.accessKey" placeholder="请输入accessKey" />
        </el-form-item>
        <el-form-item label="秘钥" prop="secretKey">
          <el-input v-model="form.secretKey" placeholder="请输入秘钥" type="password" show-password/>
        </el-form-item>
        <el-form-item label="桶名称" prop="bucketName">
          <el-input v-model="form.bucketName" placeholder="请输入桶名称" />
        </el-form-item>
        <el-form-item label="前缀" prop="prefix">
          <el-input v-model="form.prefix" placeholder="请输入前缀" />
        </el-form-item>
        <el-form-item label="访问站点" prop="endpoint">
          <el-input v-model="form.endpoint" placeholder="请输入访问站点" >
            <template slot="prepend" v-if="form.isHttps==='Y'">https://</template>
            <template slot="prepend" v-else>http://</template>
          </el-input>
        </el-form-item>
        <el-form-item label="自定义域名" prop="domain">
          <el-input v-model="form.domain" placeholder="请输入自定义域名" >
            <template slot="prepend" v-if="form.isHttps==='Y'">https://</template>
            <template slot="prepend" v-else>http://</template>
          </el-input>
        </el-form-item>
        <el-form-item label="使用HTTPS" prop="isHttps">
          <el-radio v-model="form.isHttps" label="Y">是</el-radio>
          <el-radio v-model="form.isHttps" label="N">否</el-radio>
        </el-form-item>
        <el-form-item label="地区" prop="region">
          <el-input v-model="form.region" placeholder="请输入地区" />
        </el-form-item>
        <el-form-item label="桶权限类型" prop="accessPolicy">
          <el-select v-model="form.accessPolicy" placeholder="请选择桶权限类型" style="display: flex">
            <el-option
              v-for="item in dict.type.sys_oss_access_policy"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="扩展字段" prop="ext1">
          <el-input v-model="form.ext1" placeholder="请输入扩展字段" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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
import {
  listOssconfig,
  getOssconfig,
  delOssconfig,
  addOssconfig,
  updateOssconfig,
  testOssconfig, updateStatus
} from "@/api/system/ossconfig"
import dict from "@/utils/dict";

export default {
  dicts:["cloud_provider","sys_oss_access_policy"],
  name: "Ossconfig",
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
      // 对象存储配置表格数据
      ossconfigList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        configKey: null,
        accessKey: null,
        secretKey: null,
        bucketName: null,
        prefix: null,
        endpoint: null,
        domain: null,
        isHttps: null,
        region: null,
        accessPolicy: null,
        status: null,
        ext1: null,
        createDept: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        configKey: [
          { required: true, message: "配置key不能为空", trigger: "blur" }
        ],
        accessPolicy: [
          { required: true, message: "桶权限类型(0=private 1=public 2=custom)不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    dict,
    /** 查询对象存储配置列表 */
    getList() {
      this.loading = true
      listOssconfig(this.queryParams).then(response => {
        this.ossconfigList = response.rows
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
        ossConfigId: null,
        configKey: null,
        accessKey: null,
        secretKey: null,
        bucketName: null,
        prefix: null,
        endpoint: null,
        domain: null,
        isHttps: null,
        region: null,
        accessPolicy: null,
        status: null,
        ext1: null,
        createDept: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
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
      this.ids = selection.map(item => item.ossConfigId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加对象存储配置"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const ossConfigId = row.ossConfigId || this.ids
      getOssconfig(ossConfigId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改对象存储配置"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.ossConfigId != null) {
            updateOssconfig(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addOssconfig(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleSetDefaultConfig(row){
      if (row.status==="0"){
        this.$message.warning("至少设置一个存储方式")
        setTimeout(()=>{
          row.status="1"
        },500)
      }else{
        updateStatus(row).then(response => {
          this.$modal.msgSuccess("设置成功")
          this.getList()
        }).catch(()=>{
          this.getList()
        })
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ossConfigIds = row.ossConfigId || this.ids
      this.$modal.confirm('是否确认删除对象存储配置编号为"' + ossConfigIds + '"的数据项？').then(function() {
        return delOssconfig(ossConfigIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/ossconfig/export', {
        ...this.queryParams
      }, `ossconfig_${new Date().getTime()}.xlsx`)
    },
    handleTestConfig(row){
      testOssconfig(row.ossConfigId).then(res => {
        this.$modal.msgSuccess("配置测试成功")
      }).catch(()=>{})
    }
  }
}
</script>
