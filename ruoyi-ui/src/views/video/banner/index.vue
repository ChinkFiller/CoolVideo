<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px" @submit.native.prevent>
      <el-form-item label="标题" prop="title" >
        <el-input
          v-model="queryParams.title"
          placeholder="请输入标题"
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
          v-hasPermi="['video:banner:add']"
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
          v-hasPermi="['video:banner:edit']"
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
          v-hasPermi="['video:banner:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5" v-if="false">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['video:banner:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bannerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" />
      <el-table-column label="封面" align="center" prop="cover">
        <template slot-scope="scope">
          <image-preview :src="'/img/cover?url='+scope.row.cover" style="width: 150px"></image-preview>
        </template>
      </el-table-column>
      <el-table-column label="标题" align="center" prop="title" />
      <el-table-column label="跳转链接" align="center" prop="url" >
        <template slot-scope="scope">
          <el-link type="primary" :href="scope.row.url" v-if="scope.row.url!=null" target="_blank">{{scope.row.url}}</el-link>
          <el-link type="primary" :href="'https://moegirl.icu/'+scope.row.title" target="_blank" v-else>视频介绍</el-link>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['video:banner:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['video:banner:remove']"
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

    <!-- 添加或修改轮播图信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="封面" prop="cover" v-if="type===0">
          <image-upload v-model="form.cover" limit="1"/>
        </el-form-item>
        <el-form-item label="标题" prop="title" v-if="type===0">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-radio-group v-model="urlType" style="margin: 0 0 10px 80px">
          <el-radio-button label="0">外链</el-radio-button>
          <el-radio-button label="1">站内视频</el-radio-button>
        </el-radio-group>
        <el-form-item label="跳转" prop="vid" v-if="urlType==='1'">
          <el-select
            style="display: flex"
            v-model="form.vid"
            filterable
            v-if="urlType==='1'"
            placeholder="请选择视频"
          >
            <el-option
              v-for="item in videoOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="跳转" prop="url" v-if="urlType==='0'">
          <el-input v-model="form.url" placeholder="请输入外链" />
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
import { listBanner, getBanner, delBanner, addBanner, updateBanner } from "@/api/video/banner";
import { getVideoDict} from "@/api/dict/dict";
import {imgLoadErrorUrl} from "@/utils/constants";

export default {
  name: "Banner",
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
      // 轮播图信息表格数据
      type:0,
      // 轮播图信息表格数据
      bannerList: [],
      urlType:'0',
      videoOptions:[],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        cover: null,
        title: null,
        vid: null,
        url: null
      },
      // 表单参数
      form: {
      },
      // 表单校验
      rules: {
        "cover": [{ required: true, message: "封面不能为空", trigger: "blur" }],
        "title": [{ required: true, message: "标题不能为空", trigger: "blur" }],
        "url": [{ validator:this.validateUrlOrVid, trigger: "blur" }],
        "vid": [{ validator:this.validateUrlOrVid, trigger: "blur" }]
      }
    }
  },
  created() {
    this.getList()
    getVideoDict().then(response => {
      this.videoOptions = response.data
    })
  },
  methods: {
    imgLoadErrorUrl() {
      return imgLoadErrorUrl
    },
    /** 查询轮播图信息列表 */
    getList() {
      this.loading = true
      listBanner(this.queryParams).then(response => {
        this.bannerList = response.rows
        this.bannerList.forEach(item=>{
          if (item.vid!==null){
            item.type=1
          }else{
            item.type=0
          }
        })
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
        cover: null,
        title: null,
        vid: null,
        url: null
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
      this.type=0
      this.title = "添加轮播图信息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getBanner(id).then(response => {
        this.form=response.data
        if (this.form.vid!==null){
          this.urlType='1'
        }else{
          this.urlType='0'
        }
        this.open = true
        this.title = "修改轮播图信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.urlType==='0'){
            this.form.vid=null
          }else{
            this.form.url=null
          }
          if (this.form.id != null) {
            updateBanner(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addBanner(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除轮播图信息编号为"' + ids + '"的数据项？').then(function() {
        return delBanner(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/banner/export', {
        ...this.queryParams
      }, `banner_${new Date().getTime()}.xlsx`)
    },
    validateUrlOrVid(rule, value, callback) {
      const { url, vid } = this.form

      // 两个都为空
      if (!url && !vid) {
        callback(new Error('跳转不能设置为空'))
      } else {
        callback()
      }
    }

  }
}
</script>
