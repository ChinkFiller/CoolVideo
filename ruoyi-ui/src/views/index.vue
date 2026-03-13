<template>
  <div class="app-container">
    <el-card class="card">
      <i class="el-icon-s-platform" style="margin-right: 10px"/>
      <span>{{sysName}}</span>
    </el-card>
    <el-card class="card">
        <div slot="header" class="clearfix">
            <span>网站数据统计</span>
            <el-button style="float: right; padding: 3px 0" type="text" icon="el-icon-refresh" @click="getTotalInfo">刷新</el-button>
        </div>
        <div v-show="loadingTotalInfo">
            <el-skeleton :rows="6" animated />
        </div>
        <div class="box-card" v-show="!loadingTotalInfo">
          <div class="chart" v-if="!loadingTotalInfo" ref="play"/>
          <div class="chart" v-if="!loadingTotalInfo" ref="like"/>
        </div>
    </el-card>
    <el-card class="card" >
      <div slot="header" class="clearfix">
        <span>服务器信息</span>
        <el-button style="float: right; padding: 3px 0" type="text" icon="el-icon-refresh" @click="getServerInfo">刷新</el-button>
      </div>
      <div v-show="loadingServerInfo">
        <el-skeleton :rows="6" animated />
      </div>
      <div class="server-info" v-show="!loadingServerInfo">
        <el-card class="info">
          <div slot="header"><span><i class="el-icon-cpu"></i> CPU</span></div>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <table cellspacing="0" style="width: 100%;">
              <thead>
              <tr>
                <th class="el-table__cell is-leaf"><div class="cell">属性</div></th>
                <th class="el-table__cell is-leaf"><div class="cell">值</div></th>
              </tr>
              </thead>
              <tbody>
              <tr>
                <td class="el-table__cell is-leaf"><div class="cell">核心数</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" v-if="server.cpu">{{ server.cpu.cpuNum }}</div></td>
              </tr>
              <tr>
                <td class="el-table__cell is-leaf"><div class="cell">用户使用率</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" v-if="server.cpu">{{ server.cpu.used }}%</div></td>
              </tr>
              <tr>
                <td class="el-table__cell is-leaf"><div class="cell">系统使用率</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" v-if="server.cpu">{{ server.cpu.sys }}%</div></td>
              </tr>
              <tr>
                <td class="el-table__cell is-leaf"><div class="cell">当前空闲率</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" v-if="server.cpu">{{ server.cpu.free }}%</div></td>
              </tr>
              </tbody>
            </table>
          </div>
        </el-card>
        <el-card class="info">
          <div slot="header"><span><i class="el-icon-tickets"></i> 内存</span></div>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <table cellspacing="0" style="width: 100%;">
              <thead>
              <tr>
                <th class="el-table__cell is-leaf"><div class="cell">属性</div></th>
                <th class="el-table__cell is-leaf"><div class="cell">内存</div></th>
                <th class="el-table__cell is-leaf"><div class="cell">JVM</div></th>
              </tr>
              </thead>
              <tbody>
              <tr>
                <td class="el-table__cell is-leaf"><div class="cell">总内存</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" v-if="server.mem">{{ server.mem.total }}G</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" v-if="server.jvm">{{ server.jvm.total }}M</div></td>
              </tr>
              <tr>
                <td class="el-table__cell is-leaf"><div class="cell">已用内存</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" v-if="server.mem">{{ server.mem.used}}G</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" v-if="server.jvm">{{ server.jvm.used}}M</div></td>
              </tr>
              <tr>
                <td class="el-table__cell is-leaf"><div class="cell">剩余内存</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" v-if="server.mem">{{ server.mem.free }}G</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" v-if="server.jvm">{{ server.jvm.free }}M</div></td>
              </tr>
              <tr>
                <td class="el-table__cell is-leaf"><div class="cell">使用率</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" v-if="server.mem" :class="{'text-danger': server.mem.usage > 80}">{{ server.mem.usage }}%</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" v-if="server.jvm" :class="{'text-danger': server.jvm.usage > 80}">{{ server.jvm.usage }}%</div></td>
              </tr>
              </tbody>
            </table>
          </div>
        </el-card>
        <el-card style="width: 98%;margin: 10px auto auto auto">
          <div slot="header">
            <span><i class="el-icon-monitor"></i> 服务器信息</span>
          </div>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <table cellspacing="0" style="width: 100%;">
              <tbody>
              <tr>
                <td class="el-table__cell is-leaf"><div class="cell">服务器名称</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" v-if="server.sys">{{ server.sys.computerName }}</div></td>
                <td class="el-table__cell is-leaf"><div class="cell">操作系统</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" v-if="server.sys">{{ server.sys.osName }}</div></td>
              </tr>
              <tr>
                <td class="el-table__cell is-leaf"><div class="cell">服务器IP</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" v-if="server.sys">{{ server.sys.computerIp }}</div></td>
                <td class="el-table__cell is-leaf"><div class="cell">系统架构</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" v-if="server.sys">{{ server.sys.osArch }}</div></td>
              </tr>
              </tbody>
            </table>
          </div>
        </el-card>
        <el-card style="width: 98%;margin: 10px auto auto auto">
          <div slot="header">
            <span><i class="el-icon-receiving"></i> 磁盘状态</span>
          </div>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <table cellspacing="0" style="width: 100%;">
              <thead>
              <tr>
                <th class="el-table__cell el-table__cell is-leaf"><div class="cell">盘符路径</div></th>
                <th class="el-table__cell is-leaf"><div class="cell">文件系统</div></th>
                <th class="el-table__cell is-leaf"><div class="cell">盘符类型</div></th>
                <th class="el-table__cell is-leaf"><div class="cell">总大小</div></th>
                <th class="el-table__cell is-leaf"><div class="cell">可用大小</div></th>
                <th class="el-table__cell is-leaf"><div class="cell">已用大小</div></th>
                <th class="el-table__cell is-leaf"><div class="cell">已用百分比</div></th>
              </tr>
              </thead>
              <tbody v-if="server.sysFiles">
              <tr v-for="(sysFile, index) in server.sysFiles" :key="index">
                <td class="el-table__cell is-leaf"><div class="cell">{{ sysFile.dirName }}</div></td>
                <td class="el-table__cell is-leaf"><div class="cell">{{ sysFile.sysTypeName }}</div></td>
                <td class="el-table__cell is-leaf"><div class="cell">{{ sysFile.typeName }}</div></td>
                <td class="el-table__cell is-leaf"><div class="cell">{{ sysFile.total }}</div></td>
                <td class="el-table__cell is-leaf"><div class="cell">{{ sysFile.free }}</div></td>
                <td class="el-table__cell is-leaf"><div class="cell">{{ sysFile.used }}</div></td>
                <td class="el-table__cell is-leaf"><div class="cell" :class="{'text-danger': sysFile.usage > 80}">{{ sysFile.usage }}%</div></td>
              </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </div>
    </el-card>
    <el-card class="card">
      <div slot="header" class="clearfix">
        <span>自动更新器日志</span>
        <el-button style="float: right; padding: 3px 0;color: red" type="text"  icon="el-icon-delete-solid" @click="clearUpdaterLog">清除所有日志</el-button>
      </div>
      <div class="updater-log" style="overflow:auto" @scroll="handleScroll">
        <div v-for="item in logData" class="log">
          [{{item.time}}]&nbsp;{{item.message}}
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import * as echarts from "echarts"
import {clearUpdaterLog, listLog} from "@/api/video/updaterLog";
import { getServer } from "@/api/monitor/server"
import {getTotalInfo} from "@/api/video/film";
import {getVideoDict} from "@/api/dict/dict";

export default {
  name: "Index",
  data() {
    return {
        pageNum:0,
        logData:[],
        server: [],
        videoDict:[],
        sysName:process.env.VUE_APP_TITLE,
        loadingServerInfo:false,
        loadingTotalInfo:false
    }
  },
  created() {
    this.getServerInfo()
    this.getTotalInfo()
    this.loadUpdaterLog()
    getVideoDict().then(res=>{
      this.videoDict=res.data
    })
  },
  methods: {
      handleScroll(e){
          const el = e.target;
          // 判断是否到达底部
          if (el.scrollTop + el.clientHeight+1 >= el.scrollHeight) {
              this.loadUpdaterLog()
          }
      },
      getTotalInfo(){
          this.loadingTotalInfo=true
          getTotalInfo().then(response => {
            this.loadingTotalInfo=false
            setTimeout(()=>{
              this.drawChart('play',"播放量排行",response.data.topPlayCount)
              this.drawChart('like',"点赞量排行",response.data.topLike)
            },500)
          })
      },
      /** 查询服务器信息 */
      getServerInfo() {
        this.loadingServerInfo=true
        getServer().then(response => {
          this.server = response.data
          this.loadingServerInfo=false
        })
      },
      loadUpdaterLog() {
        let a={
          pageNum:this.pageNum,
          pageSize:15,
          id:null
        }
        listLog(a).then(res=>{
          if (this.pageNum*15<=res.total){
            //只有小于总页数时进行累加
            this.logData.push(...res.rows)
            this.pageNum++;
          }
        })
      },
      clearUpdaterLog(){
        this.$modal.confirm('是否清空所有更新日志？').then(function() {
          return clearUpdaterLog()
        }).then(() => {
          this.$message.success("清除成功")
          this.pageNum=1
          this.logData=[]
          this.loadUpdaterLog()
        }).catch(() => {})
      },
      drawChart(ref,title,data) {
        let myChart = echarts.init(this.$refs[ref],"macarons");
        let tag=[]
        let value=[]
        data.forEach(item=>{
          tag.push(this.videoDict.find(v=>{return v.value===item.id}).label)
          value.push(item.number)
        })
        let option = {
          title: {
            text: title
          },
          tooltip: {},
          xAxis: {
            type: 'value' // 横轴为数值轴
          },
          yAxis: {
            type: 'category', // 纵轴为类目轴
            data: tag.reverse(),// 类目
            axisLabel: {
              formatter: function(value) {
                if (value.length > 6) {
                  return value.substring(0, 6) + '...';
                } else {
                  return value;
                }
              },
              tooltip: {
                show: true  // 在 ECharts 5.x 后，可以给 axisLabel 开启 tooltip
              }
            }
          },
          series: [{
            type: 'bar', // 依然是 bar 类型
            data: value.reverse(),
            label: {
              show: true,       // 显示标签
              position: 'right',// 横向柱状图的“柱顶”是右侧
              formatter: '{c}'  // {c} 表示当前数据值
            }
          }]
        };
        myChart.setOption(option);
      }
  }
}
</script>

<style scoped lang="scss">
.app-container{
  .box-card{
    display: flex;
    flex-wrap: wrap;
    .chart{
      width: 48%;
      height: 400px;
      margin: 10px auto auto auto;
    }
  }
}
.card{
  border-radius: 10px;
  margin-bottom: 10px;
}
.updater-log{
  height: 200px;
  background-color: #1f1f1f;
  .log{
    height: 20px;
    color: white;
    margin: 10px 0 10px 15px;
  }
}
.server-info{
  display: flex;
  flex-wrap: wrap;
  .info{
    width: 48%;
    margin: auto;
  }
}
</style>

