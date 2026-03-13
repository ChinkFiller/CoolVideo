<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="昵称" prop="nickName">
        <el-autocomplete
          clearable
          class="inline-input"
          v-model="queryParams.nickName"
          :fetch-suggestions="searchUser"
          placeholder="请输入内容"
          :trigger-on-focus="false"
        ></el-autocomplete>
      </el-form-item>
      <el-form-item label="手机号" prop="phoneNumber">
        <el-input
          v-model="queryParams.phoneNumber"
          placeholder="请输入手机号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input
          v-model="queryParams.email"
          placeholder="请输入邮箱"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="视频名称" prop="videoName">
        <el-autocomplete
          clearable
          class="inline-input"
          v-model="queryParams.videoName"
          :fetch-suggestions="searchVideo"
          placeholder="请输入内容"
          :trigger-on-focus="false"
        ></el-autocomplete>
      </el-form-item>
      <el-form-item label="发布时间" prop="timeStart,timeEnd">
        <el-date-picker clearable
                        v-model="queryParams.timeStart"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择开始时间">
        </el-date-picker>
        <span style="margin: 10px;">至</span>
        <el-date-picker clearable
                        v-model="queryParams.timeEnd"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择结束时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['video:comment:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['video:comment:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-error"
          size="mini"
          @click="handleIntercept"
          v-hasPermi="['video:comment:export']"
        >拦截规则</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="commentsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" />
      <el-table-column label="发布用户昵称" align="center" >
        <template slot-scope="scope">
          {{ scope.row.nickName }}
        </template>
      </el-table-column>
      <el-table-column label="视频名称" align="center">
        <template slot-scope="scope">
          {{ scope.row.videoName }}
        </template>
      </el-table-column>
      <el-table-column label="发布时间" align="center" prop="time" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.time, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发布者ip" align="center" prop="remoteIp"/>
      <el-table-column label="发布者ip属地" align="center" prop="remoteLocation"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['video:comment:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['video:comment:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>


    <el-dialog title="修改拦截规则" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="拦截关键词" prop="name">
          <el-input v-model="form.keyword" type="textarea" placeholder="请输入关键词，以逗号分割" :rows="5"/>
        </el-form-item>
        <el-form-item label="用户禁评" prop="name">
          <el-select v-model="form.userIds" multiple filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in userDict"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="视频禁评" prop="name">
          <el-select v-model="form.videoIds" multiple filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in videoDict"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="评论详情" :visible.sync="commentDetail" width="500px" append-to-body>
      <el-form ref="form" :model="comment" label-width="100px">
        <el-form-item label="发布者邮箱" prop="name">
          <el-input v-model="comment.email" readonly />
        </el-form-item>
        <el-form-item label="发布者时间" prop="name">
          <el-input :value="parseTime(comment.time, '{y}-{m}-{d}')" readonly />
        </el-form-item>
        <el-form-item label="发布者IP属地" prop="name">
          <el-input v-model="comment.remoteLocation" readonly />
        </el-form-item>
        <el-form-item label="评论内容" prop="name">
          <el-input v-model="comment.content" type="textarea" :rows="5" readonly />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="commentDetail=false">关 闭</el-button>
      </div>
    </el-dialog>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import {listComments, delComments, setProhibitConfig, getProhibitConfig,getComments} from "@/api/video/comment"
import {getUserDict, getVideoDict} from "@/api/dict/dict";
import {parseTime} from "../../../utils/ruoyi";

export default {
  name: "Comments",
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
      // 评论管理表格数据
      commentsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      userDict:[],
      videoDict:[],
      comment:[],
      commentDetail:false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        uid: null,
        content: null,
        vid: null,
        time: null,
        videoName:null,
        phoneNumber:null,
        nickName:null,
        email:null,
        timeStart:null,
        timeEnd:null
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
    getUserDict().then(res=>{
      this.userDict = res.data
    })
    getVideoDict().then(res=>{
      this.videoDict = res.data
    })
  },
  methods: {
    parseTime,
    /** 查询评论管理列表 */
    getList() {
      this.loading = true
      listComments(this.queryParams).then(response => {
        this.commentsList = response.rows
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
        uid: null,
        content: null,
        vid: null,
        time: null
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
    handleView(row){
      getComments(row.id).then(res=>{
        this.comment=res.data
        this.commentDetail=true;
      }).catch(err=>{
        this.$modal.msgError(err.message)
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
            this.form.userIds=this.form.userIds.join(',')
            this.form.videoIds=this.form.videoIds.join(',')
            setProhibitConfig(this.form).then(response => {
              this.$modal.msgSuccess("修改规则成功")
              this.open=false
            })
        }
      })
    },
    searchUser(q,cd){
      const results = this.userDict.filter(item => item.label.indexOf(q) >= 0);
      // 调用 callback 返回建议列表的数据
      cd(results.map(item => {return {value:item.label}}));
    },
    searchVideo(q,cd){
      const results = this.videoDict.filter(item => item.label.indexOf(q) >= 0);
      // 调用 callback 返回建议列表的数据
      cd(results.map(item => {return {value:item.label}}));
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除评论ID为"' + ids + '"的数据项？').then(function() {
        return delComments(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/video/comments/export', {
        ...this.queryParams
      }, `comments_${new Date().getTime()}.xlsx`)
    },
    handleIntercept(){
      getProhibitConfig().then(res=>{
        this.form=res.data
        this.open=true

        if (this.form.userIds===""){
          this.form.userIds=[]
        }else{
          this.form.userIds=this.form.userIds.split(",").map(Number)
        }

        if (this.form.videoIds===""){
          this.form.videoIds=[]
        }else{
          this.form.videoIds=this.form.videoIds.split(",").map(Number)
        }
      })
    }
  }
}
</script>
