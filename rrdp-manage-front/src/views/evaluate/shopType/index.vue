<template>
  <div class="app-container">
    <el-row>
      <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
        <div class="left-panel">
          <el-button type="primary" icon="el-icon-plus" size="mini" @click="openDialog()">添加</el-button>
        </div>
      </el-col>
    </el-row>

    <div style="height:500px;overflow: scroll;">
      <el-table
        v-loading="loading"
        :data="tableData"
        tooltip-effect="dark"
        style="width: 100%;margin-top: 10px;"
      >
        <el-table-column
          label="序号"
          width="70"
          center
        >
          <template slot-scope="scope">{{ (currentPage - 1) * pageSize + scope.$index +1 }}</template>
        </el-table-column>
        <el-table-column
          prop="name"
          label="名称"
        />
        <el-table-column style="line-height: 10px;" align="center" label="头像">
          <template slot-scope="scope">
            <img
              :src="scope.row.icon ? 'http://localhost:8080/imgs'+scope.row.icon:'http://img.shiyit.com/FjzfvfWYZVED7eXMS4EL8KNR949K'"
              style="width: 100px;height: 100px;"
            >
          </template>
        </el-table-column>
        <el-table-column
          label="排序"
          show-overflow-tooltip
          align="center"
        >
          <template slot-scope="scope">
            <el-tag
              type="warning"
            >
              {{ scope.row.sort }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="createTime"
          label="创建时间"
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
              修改
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

    <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible" width="40%">
      <el-form :model="dialogForm" size="mini" label-width="80px" style="padding-right: 40px;">
        <el-form-item label="类型名称" prop="name">
          <el-input v-model="dialogForm.name" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-upload
            class="avatar-uploader"
            action="http://localhost:8800/upload/icon"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <img v-if="dialogForm.icon" :src="dialogForm.icon" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dialogForm.sort" controls-position="right" :min="1" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="cancelDialog()">取 消</el-button>
        <el-button type="primary" @click="submitDialog()">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import shopType from '@/api/evaluate/shopType'
export default {
  data() {
    return {
      createTimes: [], // 选择创建日期表单数组
      tableData: [], // 表格数据，用户数据
      total: 0, // 总记录数
      pageSize: 5, // 每页记录数
      currentPage: 1, // 当前页码
      loading: true, // 懒加载
      dialogTitle: '', // 弹框的标题
      dialogFormVisible: false, // 默认弹框不打开
      dialogForm: {
        name: '',
        sort: 1,
        icon: ''
      }, // 弹框的表单
      iconUrl: '' // 保存图标路径到数据库
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
      shopType.getTypeList(this.currentPage, this.pageSize)
        .then(res => {
          this.loading = false
          this.total = res.data.total
          this.tableData = res.data.records
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
    handleAvatarSuccess(res, file) {
      const formData = new FormData()
      // 将文件传给后端下载
      formData.append('file', file)
      this.dialogForm.icon = 'http://localhost:8080/imgs' + res.data
      this.iconUrl = res.data
    },
    openDialog(id) {
      if (id) {
        this.dialogTitle = '修改'
        shopType.queryById(id).then(
          res => {
            this.dialogForm = res.data
            this.dialogForm.icon = 'http://localhost:8080/imgs' + res.data.icon
          }
        )
        this.dialogFormVisible = true
      } else {
        this.dialogTitle = '添加'
        this.dialogFormVisible = true
      }
    },
    submitDialog() {
      if (this.dialogForm.id) {
        this.dialogForm.icon = this.iconUrl
        shopType.updateShopType(this.dialogForm).then(
          res => {
            if (res.code === 200) {
              this.dialogFormVisible = false
              this.dialogForm = {}
              this.iconUrl = {}
              this.fetchData()
              this.$message.success(res.message)
            }
          }
        ).catch(this.$message.error)
      } else {
        this.dialogForm.icon = this.iconUrl
        shopType.saveShopType(this.dialogForm).then(
          res => {
            if (res.code === 200) {
              this.dialogFormVisible = false
              this.dialogForm = {}
              this.iconUrl = {}
              this.fetchData()
              this.$message.success(res.message)
            }
          }
        ).catch(this.$message.error)
      }
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
          shopType.removeShopType(id)
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
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
  </style>

