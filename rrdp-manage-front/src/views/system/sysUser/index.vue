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
            <el-form-item label="关键字:">
              <el-input v-model="searchObj.keyword" type="text" autocomplete="off" placeholder="请输入搜索关键字" />
            </el-form-item>
            <el-form-item>
              <el-button icon="el-icon-search" type="primary" size="mini" @click="fetchData()">查询</el-button>
              <el-button icon="el-icon-refresh" type="warning" size="mini" @click="resetData()">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>
    <el-row>
      <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
        <div class="left-panel">
          <el-button
            type="primary"
            icon="el-icon-plus"
            size="mini"
            :disabled="$hasBP('bnt.sysUser.add') === false"
            @click="openDialog()"
          >添加</el-button>
        </div>
      </el-col>
    </el-row>
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
        prop="username"
        label="用户名"
        width="120"
      />
      <el-table-column
        prop="name"
        label="姓名"
        show-overflow-tooltip
      />
      <el-table-column
        prop="phone"
        label="手机号码"
        show-overflow-tooltip
      />
      <el-table-column label="状态" width="80">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            @change="switchStatus(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
        prop="createTime"
        label="创建时间"
        show-overflow-tooltip
      />
      <el-table-column
        fixed="right"
        label="操作"
        width="300"
        align="center"
      >
        <template slot-scope="scope">
          <el-button
            icon="el-icon-edit"
            type="primary"
            size="mini"
            @click="openDialog(scope.row.id)"
          >
            修改
          </el-button>
          <el-button
            icon="el-icon-baseball"
            type="success"
            size="mini"
            @click="showAssignRole(scope.row)"
          >
            分配角色
          </el-button>
          <el-button
            icon="el-icon-delete"
            type="danger"
            size="mini"
            @click="removeSysUser(scope.row.id)"
          >
            删除
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

    <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible">
      <el-form ref="ruleForm" :model="dialogForm" :rules="rules" label-width="150px" size="mini" style="padding-right: 10px;">
        <el-form-item label="用户名" label-width="120px" prop="username">
          <el-input v-model="dialogForm.username" autocomplete="off" />
        </el-form-item>
        <el-form-item v-if="!dialogForm.id" label="密码" label-width="120px" prop="password">
          <el-input v-model="dialogForm.password" type="password" autocomplete="off" />
        </el-form-item>
        <el-form-item label="姓名" label-width="120px" prop="name">
          <el-input v-model="dialogForm.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="手机" label-width="120px" prop="phone">
          <el-input v-model="dialogForm.phone" autocomplete="off" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelDialog()">取 消</el-button>
        <el-button type="primary" @click="submitDialogForm(dialogForm.id)">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="分配角色" :visible.sync="dialogAssignVisible">
      <el-form ref="ruleForm" :model="assignRoleVo" :rules="assignRule" label-width="150px" size="mini" style="padding-right: 10px;">
        <el-form-item label="角色" label-width="120px">
          <el-select
            v-model="userRoleIds"
            multiple
            placeholder="请选择角色"
          >
            <el-option
              v-for="item in allRoles"
              :key="item.id"
              :label="item.roleName"
              :value="item.id.toString()"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelDialog()">取 消</el-button>
        <el-button type="primary" @click="assignRole()">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import sysUser from '@/api/system/sysUser'
import sysRole from '@/api/system/sysRole'
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
      },
      dialogAssignVisible: false,
      assignRoleVo: {},
      assignRule: {},
      allRoles: [],
      userRoleIds: []
    }
  },
  watch: {
    dialogFormVisible(newValue, oldValue) {
      if (this.dialogFormVisible === false) {
        this.dialogForm = {}
      }
    },
    dialogAssignVisible(newValue, oldValue) {
      if (this.dialogAssignVisible === false) {
        this.assignRoleVo = {}
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
      sysUser.getPageList(this.currentPage, this.pageSize, this.searchObj)
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
    switchStatus(row) {
      const confirmTitle = row.status ? `你确定要启用该${row.name}嘛？` : `你确定要停用该${row.name}嘛？`
      this.$confirm(`${confirmTitle}`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        sysUser.updateStatus(row.id, Number(row.status))
          .then(res => {
            this.$message({
              type: 'success',
              message: '修改成功!'
            })
            this.fetchData()
          })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消'
        })
        this.fetchData()
      })
    },
    resetData() {
      this.createTimes = []
      this.searchObj = {}
      this.fetchData()
    },
    openDialog(id) {
      if (id == null) {
        this.dialogTitle = '添加'
        this.dialogFormVisible = true
      } else {
        this.dialogTitle = '更新'
        this.dialogFormVisible = true
        sysUser.queryById(id)
          .then(res => {
            this.dialogForm = res.data
          })
      }
    },
    cancelDialog() {
      this.dialogFormVisible = false
      this.dialogForm = {}
      this.dialogAssignVisible = false
      this.assignRoleVo = {}
    },
    submitDialogForm(id) {
      if (id != null) {
        this.updateSysUser(id)
      } else {
        this.saveSysUser()
      }
    },
    saveSysUser() {
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          sysUser.saveSysUser(this.dialogForm)
            .then(res => {
              this.$message.success('添加成功！')
              this.dialogFormVisible = false
              this.dialogForm = {}
              this.fetchData()
            })
            .catch(this.$message.error)
        }
      })
    },
    updateSysUser() {
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          sysUser.updateSysUser(this.dialogForm)
            .then(res => {
              this.$message.success('修改成功！')
              this.dialogForm = {}
              this.dialogFormVisible = false
              this.fetchData()
            })
            .catch(this.$message.error)
        }
      })
    },
    removeSysUser(id) {
      this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          sysUser.removeSysUser(id)
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
    },
    // 分配角色
    showAssignRole(row) {
      this.dialogAssignVisible = true
      sysRole.getRolesByUserId(row.id)
        .then(res => {
          this.allRoles = res.data.allRoles
          if (res.data.userRoleIds.toString().split(',')[0] === '') {
            this.userRoleIds = []
          } else {
            this.userRoleIds = res.data.userRoleIds.toString().split(',')
          }
          this.assignRoleVo.userId = row.id
        })
    },
    assignRole() {
      this.assignRoleVo.roleIdList = this.userRoleIds
      sysRole.assignRoles(this.assignRoleVo)
        .then(res => {
          this.$message.success('添加成功！')
          this.dialogAssignVisible = false
          this.assignRoleVo = {}
          this.fetchData()
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
