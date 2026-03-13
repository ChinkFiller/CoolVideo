<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="视频状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择视频更新状态">
          <el-option
            v-for="dict in dict.type.update_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="导演" prop="leader">
        <el-input
          v-model="queryParams.leader"
          placeholder="请输入导演"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="演员" prop="actor">
        <el-input
          v-model="queryParams.actor"
          placeholder="请输入演员"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="更新时间" prop="publicDate">
        <el-date-picker clearable
                        v-model="queryParams.publicDate"
                        type="month"
                        value-format="yyyy-MM-ddTHH:mm:ss"
                        placeholder="请选择更新时间">
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
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['video:data:add']"
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
          v-hasPermi="['video:data:edit']"
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
          v-hasPermi="['video:data:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['video:data:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-s-platform"
          size="mini"
          @click="handleUpdateConfig"
          v-hasPermi="['video:updater:view']"
        >自动更新设置</el-button>
      </el-col>
      <el-col :span="1.5" v-show="autoUpdateOpen">
        <el-button
          type="primary"
          plain
          icon="el-icon-caret-right"
          size="mini"
          @click="handleUpdaterRun"
          v-hasPermi="['video:updater:run']"
        >立即更新</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" />
      <el-table-column label="名称" align="center" prop="name" />
      <el-table-column label="分集" align="center" prop="part" />
      <el-table-column label="状态" align="center" prop="state">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.update_status" :value="scope.row.state" />
        </template>
      </el-table-column>
<!--      <el-table-column label="导演" align="center" prop="leader" />-->
<!--      <el-table-column label="演员" align="center" prop="actor" />-->
      <el-table-column label="封面" align="center" prop="imgUrl" width="120">
        <template slot-scope="scope">
          <image-preview :src="'/img/cover?url='+scope.row.imgUrl"></image-preview>
        </template>
      </el-table-column>
      <el-table-column label="简介" align="center" prop="ins" width="300">
        <template slot-scope="scope">
          <div style="overflow-y: auto;width: 280px;height: 130px">
            {{scope.row.ins}}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="publicDate" width="150">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.publicDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" width="100px">
        <template slot-scope="{}" slot="header">
          <!-- 表头名称 -->
          <span>更新锁</span>
          <el-tooltip class="item" effect="dark" placement="top" style="cursor: pointer">
            <i class="el-icon-question" style="font-size:16px;"></i>
            <!-- 悬停提示内容 -->
            <div slot="content">
              <div>锁定后，自动更新脚本将无法自动更新该视频</div>
              <div>只能通过后台手动修改信息</div>
            </div>
          </el-tooltip>
        </template>
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.autoUpdateLock==='0'"
            size="mini"
            type="text"
            icon="el-icon-unlock"
            @click="handleLockInfo(scope.row)"
            v-hasPermi="['video:data:edit']"
          >未锁定</el-button>
          <el-button
            v-else
            size="mini"
            type="text"
            icon="el-icon-lock"
            @click="handleLockInfo(scope.row)"
            v-hasPermi="['video:data:edit']"
          >已锁定</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100px" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['video:data:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['video:data:remove']"
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

    <el-dialog title="修改自动更新配置" :visible.sync="configOpen" width="500px" append-to-body>
      <el-form ref="form" :model="updateConfig" label-width="80px">
        <el-form-item label="自动更新" prop="name">
          <el-switch
            v-model="updateConfig.autoUpdate"
            active-color="#13ce66"
            inactive-color="#ff4949">
          </el-switch>
        </el-form-item>
        <el-form-item label="更新间隔 " prop="name" v-show="updateConfig.autoUpdate">
          <el-input-number v-model="updateConfig.updateInterval" :min="1" :max="23" label="小时"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitUpdateConfig">保 存</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 添加或修改视频数据保存的数据对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="分集" prop="part">
          <el-input v-model="form.part" placeholder="请输入分集" type="number"/>
        </el-form-item>
        <el-form-item label="更新状态" prop="state">
          <el-select v-model="form.state" placeholder="请选择更新状态" style="display: flex">
            <el-option
              v-for="dict in dict.type.update_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="周更时间" prop="week" v-if="form.state==='1'">
          <el-select v-model="form.week" placeholder="请选择一个时间" style="width: 100%">
            <el-option
              v-for="item in dict.type.week"
              :key="parseInt(item.value)"
              :label="item.label"
              :value="parseInt(item.value)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="导演" prop="leader">
          <el-input v-model="form.leader" placeholder="请输入导演" />
        </el-form-item>
        <el-form-item label="演员" prop="actor">
          <el-input v-model="form.actor" placeholder="请输入演员" />
        </el-form-item>
        <el-form-item label="分类" prop="tags">
          <el-select v-model="form.tags" style="display: flex" multiple placeholder="视频标签">
            <el-option
              v-for="dict in dict.type.video_tag"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="封面地址" prop="imgUrl">
          <el-radio-group v-model="imgURlSource" style="margin-bottom: 10px">
            <el-radio-button label="0">文件</el-radio-button>
            <el-radio-button label="1">外链</el-radio-button>
          </el-radio-group>
          <image-upload v-model="form.imgUrl" :limit="1" :is-show-tip="true" v-if="imgURlSource==='0'"/>
          <el-input v-model="form.imgUrl" v-else/>
        </el-form-item>
        <el-form-item label="简介" prop="ins">
          <el-input v-model="form.ins" type="textarea" placeholder="请输入内容" :rows="8"/>
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
  listData,
  getData,
  delData,
  addData,
  updateData,
  getUpdateConfig,
  updateConfig,
  runUpdater, lockInfo, unlockInfo
} from "@/api/video/film"
import { imgLoadErrorUrl } from "@/utils/constants";
import dict from "@/utils/dict";

export default {
  dicts:['update_status','video_tag','week'],
  name: "Data",
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
      // 视频数据保存的数据表格数据
      dataList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      //自动更新配置页面是否显示
      configOpen:false,
      updateConfig:{},
      imgURlSource:"0",
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        state: null,
        leader: null,
        actor: null,
        imgUrl: null,
        part: null,
        ins: null,
        publicDate: null,
        type: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [{ required: true, message: "视频名称不能为空", trigger: "blur" }],
        state: [{ required: true, message: "视频更新状态不能为空", trigger: "blur" }],
        leader: [{ required: true, message: "导演不能为空", trigger: "blur" }],
        actor: [{ required: true, message: "演员不能为空", trigger: "blur" }],
        imgUrl: [{ required: true, message: "视频封面不能为空", trigger: "blur" }],
        part: [{ required: true, message: "视频分集数量不能为空", trigger: "blur" }],
        ins: [{ required: true, message: "视频简介不能为空", trigger: "blur" }],
      },
      autoUpdateOpen:false
    }
  },
  created() {
    this.getList()
    this.getConfigKey("video.autoupdate.enable").then(res=>{
      this.autoUpdateOpen=res.msg==="true"
    })
  },
  methods: {
    dict,
    setPart(state){
      const match = state.match(/\d+/); // 匹配第一个连续的数字
      this.form.part=match ? parseInt(match[0], 10) : 1;

    },
    imgLoadErrorUrl() {
      return imgLoadErrorUrl
    },
    /** 查询视频数据保存的数据列表 */
    getList() {
      this.loading = true
      listData(this.queryParams).then(response => {
        this.dataList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.configOpen=false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        name: null,
        state: null,
        leader: null,
        actor: null,
        imgUrl: null,
        part: null,
        ins: null,
        publicDate: null,
        type: null
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
      this.title = "添加视频数据"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getData(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改视频数据"
        if (this.form.imgUrl.startsWith("http") || this.form.imgUrl.startsWith("https")){
          this.imgURlSource="1"
        }
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateData(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addData(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    submitUpdateConfig(){
      updateConfig(this.updateConfig).then(response => {
        this.$modal.msgSuccess("修改更新配置成功")
        this.autoUpdateOpen = !!this.updateConfig.autoUpdate;
        this.configOpen=false
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除视频数据保存的数据编号为"' + ids + '"的数据项？').then(function() {
        return delData(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    handleUpdateConfig(){
      getUpdateConfig().then(res=>{
        this.updateConfig=res.data
        this.configOpen=true
      })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/video/film/export', {
        ...this.queryParams
      }, `data_${new Date().getTime()}.xlsx`)
    },
    handleUpdaterRun(){
      runUpdater().then(res=>{
        this.$modal.msgSuccess("启动成功！可前往主页查看更新日志")
      })
    },
    handleLockInfo(row){
      if (row.autoUpdateLock==='0'){
        lockInfo(row.id)
        this.$modal.msgSuccess("已设置自动更新锁")
        row.autoUpdateLock='1'
      }else{
        unlockInfo(row.id)
        this.$modal.msgSuccess("已取消自动更新锁")
        row.autoUpdateLock='0'
      }
    }
  }
}
</script>
