<template>
  <div class="app-container">
    <el-row>
      <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
        <div class="left-panel">
          <el-form class="el-form el-form--inline" size="mini">
            <el-form-item label="创建时间:">
              <el-date-picker
                v-model="createTimes"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="margin-right: 10px;width: 100%;"
                size="mini"
              />
            </el-form-item>
            <el-form-item label="请输入搜索内容:">
              <el-input v-model="searchObj.username" type="text" autocomplete="off" placeholder="请输入查询的用户名称" />
            </el-form-item>
            <el-form-item>
              <el-button icon="el-icon-search" type="primary" size="mini" @click="fetchData()">查询</el-button>
              <el-button icon="el-icon-refresh" type="warning" size="mini" @click="resetData()">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>

    <el-table
      :data="tableData"
      tooltip-effect="dark"
      style="width: 100%;margin-top: 10px;"
      border
    >
      <el-table-column
        label="序号"
        width="70"
        center
      >
        <template slot-scope="scope">{{ (currentPage - 1) * pageSize + scope.$index +1 }}</template>
      </el-table-column>
      <el-table-column
        prop="username"
        label="登录账号"
        width="120"
      />
      <el-table-column
        prop="ipaddr"
        label="登录IP地址"
        show-overflow-tooltip
      />
      <el-table-column prop="status" label="状态" width="80" />
      <el-table-column
        prop="msg"
        label="提示信息"
        show-overflow-tooltip
      />
      <el-table-column
        prop="createTime"
        label="创建时间"
        show-overflow-tooltip
        header-align="center"
        align="center"
      />
      <el-table-column
        fixed="right"
        label="操作"
        width="100"
        align="center"
      >
        <template slot-scope="scope">
          <el-button
            icon="el-icon-view"
            size="mini"
            type="text"
            @click="openDetailDialog(scope.row.id)"
          >详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="block" style="text-align:center;padding: 2px 5px; margin: 15px 0 0 0;font-weight: 400;">
      <el-pagination
        :current-page="currentPage"
        :page-sizes="[ 5, 10, 20,40]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog title="登录日志详情" :visible.sync="dialogTableVisible">
      <el-form :model="LoginLogForm" size="mini" style="padding-right: 10px;">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户名：">{{ LoginLogForm.username }}</el-form-item>
            <el-form-item label="操作状态：">
              <div v-if="LoginLogForm.status === 0">正常</div>
              <div v-else-if="LoginLogForm.status === 1">失败</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="提示信息：">{{ LoginLogForm.msg }}</el-form-item>
            <el-form-item label="IP地址: ">{{ LoginLogForm.ipaddr }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="访问时间：">{{ LoginLogForm.createTime }}</el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeDialog()">关 闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import sysLoginLog from '@/api/system/sysLoginLog'
export default {
  data() {
    return {
      createTimes: [], // 选择创建日期表单数组
      tableData: [], // 表格数据，用户数据
      total: 0, // 总记录数
      pageSize: 5, // 每页记录数
      currentPage: 1, // 当前页码
      searchObj: {}, // 条件搜索框
      dialogTableVisible: false, // 是否弹出日志详情弹出框
      LoginLogForm: {} // 详情对象
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchData()
    },
    resetData() {
      this.searchObj = {}
      this.createTimes = []
      this.fetchData()
    },
    fetchData() {
      if (this.createTimes != null && this.createTimes.length === 2) {
        this.searchObj.createTimeBegin = this.createTimes[0]
        this.searchObj.createTimeEnd = this.createTimes[1]
      }
      sysLoginLog.getPageList(this.currentPage, this.pageSize, this.searchObj)
        .then(res => {
          this.tableData = res.data.records
          for (let i = 0; i < this.tableData.length; i++) {
            this.tableData[i].status = this.tableData[i].status === 1 ? '成功' : '失败'
          }
          this.total = res.data.total
        })
    },
    openDetailDialog(id) {
      sysLoginLog.queryById(id)
        .then(res => {
          this.LoginLogForm = res.data
          this.dialogTableVisible = true
        })
    },
    closeDialog() {
      this.dialogTableVisible = false
    }

  }
}
</script>

<style>
.left-panel {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    justify-content: flex-start;
}
.right-panel {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    justify-content: flex-start;
    justify-content: flex-end;
}
</style>
