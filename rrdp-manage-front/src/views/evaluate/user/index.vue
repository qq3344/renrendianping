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
            <el-form-item label="用户名:">
              <el-input v-model="searchObj.nickName" type="text" autocomplete="off" placeholder="请输入搜索的昵称" />
            </el-form-item>
            <el-form-item label="号码:">
              <el-input v-model="searchObj.phone" type="text" autocomplete="off" placeholder="请输入搜索的号码" />
            </el-form-item>
            <el-form-item>
              <el-button icon="el-icon-search" type="primary" size="mini" @click="fetchData()">查询</el-button>
              <el-button icon="el-icon-refresh" type="warning" size="mini" @click="resetData()">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>

    <div style="height:350px;overflow: scroll;">
      <el-table
        v-loading="loading"
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
          prop="nickName"
          label="昵称"
        />
        <el-table-column style="line-height: 10px;" align="center" label="头像">
          <template slot-scope="scope">
            <img
              :src="scope.row.icon ? 'http://localhost:8080/'+scope.row.icon:'http://img.shiyit.com/FjzfvfWYZVED7eXMS4EL8KNR949K'"
              style="width: 100px;height: 100px;"
            >
          </template>
        </el-table-column>
        <el-table-column
          prop="phone"
          label="手机号码"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          prop="createTime"
          label="创建时间"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          prop="updateTime"
          label="更新时间"
          show-overflow-tooltip
        />
        <el-table-column
          label="操作"
          width="200"
          align="center"
        >
          <template slot-scope="scope">
            <el-button
              icon="el-icon-edit"
              type="primary"
              size="mini"
              @click="openDialog(scope.row.id)"
            >
              详情
            </el-button>
            <el-button
              icon="el-icon-delete"
              type="danger"
              size="mini"
              @click="removeUser(scope.row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

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

    <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible">
      <el-form :model="dialogForm" size="mini" style="padding-right: 10px;">
        <el-row>
          <el-col :span="12">
            <el-form-item label="所在城市">{{ dialogForm.city }}</el-form-item>
            <el-form-item label="性别：">
              <div v-if="dialogForm.gender === true">女</div>
              <div v-else-if="dialogForm.gender === false">男</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="粉丝数：">{{ dialogForm.fans }}</el-form-item>
            <el-form-item label="关注数: ">{{ dialogForm.followee }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="介绍：">{{ dialogForm.introduce }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="生日：">{{ dialogForm.birthday }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="积分：">{{ dialogForm.credits }}</el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="cancelDialog()">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import user from '@/api/evaluate/user'
export default {
  data() {
    return {
      createTimes: [], // 选择创建日期表单数组
      tableData: [], // 表格数据，用户数据
      total: 0, // 总记录数
      pageSize: 5, // 每页记录数
      currentPage: 1, // 当前页码
      searchObj: {}, // 条件搜索框
      loading: true, // 懒加载
      dialogTitle: '', // 弹框的标题
      dialogFormVisible: false, // 默认弹框不打开
      dialogForm: {}, // 弹框的表单
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号码', trigger: 'blur' }]
      }
    }
  },
  watch: {
    dialogFormVisible(newValue, oldValue) {
      if (this.dialogFormVisible === false) {
        this.dialogForm = {}
      }
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      if (this.createTimes != null && this.createTimes.length === 2) {
        this.searchObj.createTimeBegin = this.createTimes[0]
        this.searchObj.createTimeEnd = this.createTimes[1]
      }
      user.getPageList(this.currentPage, this.pageSize, this.searchObj)
        .then(res => {
          this.loading = false
          this.total = res.data.total
          this.tableData = res.data.records
          for (let i = 0; i < this.tableData.length; i++) {
            this.tableData[i].status = this.tableData[i].status === 1
          }
        })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchData()
    },
    resetData() {
      this.createTimes = []
      this.searchObj = {}
      this.fetchData()
    },
    openDialog(id) {
      user.queryInfoById(id)
        .then(res => {
          this.dialogForm = res.data
        })
      this.dialogTitle = '详情'
      this.dialogFormVisible = true
    },
    cancelDialog() {
      this.dialogFormVisible = false
      this.dialogForm = {}
    },
    removeUser(id) {
      this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          user.removeUser(id)
            .then(res => {
              this.$message.success('删除成功！')
              this.fetchData()
            })
            .catch(this.$message.error)
        })
        .catch(() => {
          this.$message({
            type: 'success',
            message: '已取消删除'
          })
        })
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
