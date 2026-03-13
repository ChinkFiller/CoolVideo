<template>
  <div class="app-container">
    <h2>多源解析设置</h2>


    <div class="main-box">
      <div class="text" style="padding-top: 20px">开始解析</div>
      <div class="text">
        <i class="el-icon-bottom"/>
      </div>
      <draggable
        v-model="parserList"
        animation="250"
        class="drag-area"
        @change="handleParserChange"
      >
        <div
          v-for="card in parserList"
          :key="card.value"
        >
          <div class="card">
            <div>
              <div style="font-size: 23px">{{card.parser}}</div>
              <div style="font-size: 14px;color: #6c6c6c;margin-top: 5px">{{card.description}}</div>
            </div>
          </div>
        </div>
      </draggable>
      <div class="text">
        <i class="el-icon-bottom"/>
      </div>
      <div class="text" style="padding-bottom: 20px">结束解析</div>
    </div>

  </div>
</template>

<script>
import draggable from 'vuedraggable';
import {getParserList, updateParserStatus} from "@/api/video/parserMode";

export default {
  components: {
    draggable,
  },
  data() {
    return {
      parserList:[],
    };
  },
  created() {
    this.loadConfig()
  },

  methods: {
    loadConfig(){
      getParserList().then(res=>{
        this.parserList=res.data
      })
    },
    handleParserChange() {

      updateParserStatus({
        parserModeList:this.parserList.map(item => item.parser)
      }).then(res=>{
        this.$message.success('修改解析配置成功')
      }).catch(e=>{
        this.loadConfig()
      })
    }
  }
};
</script>

<style scoped>
.main-box{
  background-color: #f0f2f5;
  border-radius: 10px;
}

.main-box .text{
  width: 100%;
  font-size: 30px;
  text-align: center;
  font-weight: bold;
  user-select: none;
}

.drag-area {
  min-height: 100px; /* 保证空列表时也能拖入（如果有跨列表拖拽需求） */
  overflow-y: auto;
  padding: 10px;
}

.card {
  background: white;
  padding: 15px;
  margin-bottom: 10px;
  border-radius: 10px;
  border: 1px solid #ddd;
  cursor: move; /* 鼠标变成移动图标 */
  display: flex;
  justify-content: space-between;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}
</style>
