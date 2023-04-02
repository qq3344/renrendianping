<template>
  <div class="app-container">
    <el-row>
      <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
        <div class="left-panel">
          <el-button type="primary" icon="el-icon-plus" size="mini" @click="openDialog()">添加</el-button>
          <el-button type="danger" icon="el-icon-delete" size="mini" @click="batchRemove()">批量删除</el-button>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
        <div class="right-panel">
          <form class="el-form el-form--inline">
            <div class="el-form-item el-form-item--small">
              <div class="el-form-item__content">
                <div class="el-input el-input--small el-input--suffix">
                  <input v-model="searchObj.roleName" class="el-input__inner" type="text" autocomplete="off" placeholder="请输入角色名称">
                </div>
              </div>
            </div>
            <div class="el-form-item el-form-item--small">
              <div class="el-form-item__content">
                <el-button icon="el-icon-search" type="primary" size="mini" @click="fetchData()">查询</el-button>
                <el-button icon="el-icon-refresh" type="warning" size="mini" @click="resetData()">重置</el-button>
              </div>
            </div>
          </form>
        </div>
      </el-col>
    </el-row>
    <el-table
      ref="multipleTable"
      v-loading="loading"
      :data="tableData"
      tooltip-effect="dark"
      style="width: 100%;margin-top: 10px;"
      border
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        type="selection"
        width="55"
      />
      <el-table-column
        label="序号"
        width="70"
        center
      >
        <template slot-scope="scope">{{ (currentPage - 1) * pageSize + scope.$index +1 }}</template>
      </el-table-column>
      <el-table-column
        prop="roleName"
        label="角色名称"
        width="120"
      />
      <el-table-column
        prop="roleCode"
        label="角色编码"
        show-overflow-tooltip
      />
      <el-table-column
        prop="description"
        label="描述"
        show-overflow-tooltip
      />
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
            icon="el-icon-delete"
            type="danger"
            size="mini"
            @click="deleteSysRole(scope.row.id)"
          >
            删除
          </el-button>
          <el-button
            icon="el-icon-baseball"
            type="success"
            size="mini"
            @click="showAssignAuth(scope.row)"
          >
            分配菜单
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
        <el-form-item label="角色名称" label-width="120px" prop="roleName">
          <el-input v-model="dialogForm.roleName" autocomplete="off" />
        </el-form-item>
        <el-form-item label="角色编码" label-width="120px" prop="roleCode">
          <el-input v-model="dialogForm.roleCode" autocomplete="off" />
        </el-form-item>
        <el-form-item label="描述" label-width="120px">
          <el-input v-model="dialogForm.description" autocomplete="off" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelDialog()">取 消</el-button>
        <el-button type="primary" @click="submitDialogForm(dialogForm.id)">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="分配菜单" :visible.sync="dialogVisible">
      <el-form size="mini" style="padding-right: 10px;">
        <el-form-item>
          <el-tree
            ref="tree"
            :data="sysMenuList"
            show-checkbox
            node-key="id"
            :props="defaultProps"
          />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submit()">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import sysRole from '@/api/system/sysRole'
import sysMenu from '@/api/system/sysMenu'

export default {
  data() {
    return {
      tableData: [], // 列表表格数据
      total: 0, // 总记录数
      pageSize: 5, // 每页记录数
      currentPage: 1, // 当前页码
      searchObj: {}, // 条件搜索框
      multipleSelection: [], // 选中的行
      loading: true, // 懒加载
      dialogTitle: '', // 弹框的标题
      dialogFormVisible: false, // 默认弹框不打开
      dialogForm: {}, // 弹框的表单
      dialogVisible: false, // 分配菜单弹框
      sysRoleMenu: {}, // 分配菜单表单
      sysMenuList: [], // 分配菜单数据
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      rules: {
        roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
        roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
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
      sysRole.getPageList(this.currentPage, this.pageSize, this.searchObj)
        .then(res => {
          this.loading = false
          this.tableData = res.data.records
          this.total = res.data.total
        })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
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
      this.searchObj = {}
      this.fetchData()
    },
    deleteSysRole(id) {
      this.$confirm('此操作将永久删除该角色, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        sysRole.removeById(id)
          .then(res => {
            this.$message.success('删除成功！')
            this.fetchData()
          })
          .catch(this.$message.error)
      })
    },
    openDialog(id) {
      if (id == null) {
        this.dialogTitle = '添加'
        this.dialogFormVisible = true
      } else {
        this.dialogTitle = '更新'
        this.dialogFormVisible = true
        sysRole.queryById(id)
          .then(res => {
            this.dialogForm = res.data
          })
      }
    },
    cancelDialog() {
      this.dialogFormVisible = false
      this.dialogForm = {}
    },
    submitDialogForm(id) {
      if (id != null) {
        this.updateSysRole(id)
      } else {
        this.saveSysRole()
      }
    },
    saveSysRole() {
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          sysRole.saveSysRole(this.dialogForm)
            .then(res => {
              this.$message.success('添加成功！')
              this.dialogForm = {}
              this.dialogFormVisible = false
              this.fetchData()
            })
            .catch(this.$message.error)
        }
      })
    },
    updateSysRole() {
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          sysRole.updateSysRole(this.dialogForm)
            .then(res => {
              this.$message.success('修改成功！')
              this.dialogForm = {}
              this.dialogFormVisible = false
              this.fetchData()
            })
            .catch(error => {
              this.$message.error(error)
              this.dialogForm = {}
              this.dialogFormVisible = false
              this.fetchData()
            })
        }
      })
    },
    batchRemove() {
      if (this.multipleSelection.length === 0) {
        this.$message.warning('请选择要删除的记录！')
        return
      }
      this.$confirm('此操作将永久删除该角色, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const ids = []
        this.multipleSelection.forEach(role => ids.push(role.id))
        console.log(ids)
        sysRole.batchRemove(ids)
          .then(res => {
            this.$message.success('删除成功！')
            this.fetchData()
          })
          .catch(this.$message.error)
      })
    },
    showAssignAuth(row) {
      this.dialogVisible = true
      this.sysRoleMenu.roleId = row.id
      // sysMenu.getSelectMenuId(row.id)
      //   .then(res => {
      //     console.log(res.data)
      //     this.$refs.tree.setCheckedKeys(res.data)
      //   })
      sysMenu.treeMenu()
        .then(res => {
          this.sysMenuList = res.data
        })
      sysMenu.toAssign(row.id)
        .then(res => {
          this.sysMenuList = res.data
          const checkedIds = this.getCheckedIds(this.sysMenuList)
          this.$refs.tree.setCheckedKeys(checkedIds)
        })
    },
    getCheckedIds(auths, initArr = []) {
      return auths.reduce((pre, item) => {
        if (item.select && item.children.length === 0) {
          pre.push(item.id)
        } else if (item.children) {
          this.getCheckedIds(item.children, initArr)
        }
        return pre
      }, initArr)
    },
    submit() {
      const allCheckedNodes = this.$refs.tree.getCheckedNodes(false, true)
      const idList = allCheckedNodes.map(node => node.id)
      console.log(idList)
      this.sysRoleMenu.menuIdList = idList
      sysMenu.doAssign(this.sysRoleMenu)
        .then(res => {
          this.$message.success('添加成功！')
          this.dialogVisible = false
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
