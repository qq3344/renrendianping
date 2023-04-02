<template>
  <div class="app-container">
    <el-row>
      <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
        <div class="left-panel">
          <el-form class="el-form el-form--inline" size="mini">
            <el-form-item label="店铺名称:">
              <el-input v-model="searchObj.keyword" type="text" autocomplete="off" placeholder="请输入店铺关键字" />
            </el-form-item>
            <el-form-item>
              <el-button icon="el-icon-search" type="primary" size="mini" @click="fetchData()">查询</el-button>
              <el-button icon="el-icon-refresh" type="warning" size="mini" @click="resetData()">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>

    <div style="height:460px;overflow: scroll;">
      <el-table
        v-loading="loading"
        :data="tableData"
        tooltip-effect="dark"
        style="width: 100%;margin-top: 10px;"
      >
        <el-table-column
          label="序号"
          width="70"
          align="center"
        >
          <template slot-scope="scope">{{ (currentPage - 1) * pageSize + scope.$index +1 }}</template>
        </el-table-column>
        <el-table-column
          label="日记图片"
          min-width="100"
          align="center"
        >
          <template slot-scope="scope">
            <img :src="scope.row.images" style="width: 100px;height: 100px;">
          </template>
        </el-table-column>
        <el-table-column
          prop="title"
          label="标题"
          show-overflow-tooltip
        />
        <el-table-column
          label="发布者"
          prop="username"
        />
        <el-table-column
          label="关联店铺"
          prop="shopName"
        />
        <el-table-column
          label="发布时间"
          align="center"
          prop="createTime"
        />
        <el-table-column
          label="操作"
          width="200"
          align="center"
          fixed="right"
        >
          <template slot-scope="scope">
            <el-button
              icon="el-icon-view"
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
              @click="removeBlog(scope.row.id)"
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
        :page-sizes="[ 10, 20,40]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog
      title="博客详情"
      :visible.sync="dialogFormVisible"
      fullscreen
      top="1vh"
    >
      <el-form :model="dialogForm" size="mini" style="padding-right: 10px;">
        <el-form-item label="标题" label-width="120px" prop="title">
          <el-input v-model="dialogForm.title" disabled autocomplete="off" />
        </el-form-item>
        <el-form-item label="关联店铺" label-width="120px" prop="shopName">
          <el-input v-model="dialogForm.shopName" disabled autocomplete="off" />
        </el-form-item>
        <el-form-item label="发布者" label-width="120px" prop="username">
          <el-input v-model="dialogForm.username" disabled autocomplete="off" />
        </el-form-item>
        <el-form-item label="发表图片" label-width="120px">
          <div class="demo-image">
            <div
              v-for="(images,index) in dialogForm.images"
              :key="index"
              class="block"
              style="float:left;margin-left: 10px;"
            >
              <el-image
                style="width: 100px; height: 100px"
                :src="'http://localhost:8080'+images"
              />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="博客内容" label-width="120px">
          <div style="height:300px;border: 1px solid black;overflow: scroll;" v-html="dialogForm.content" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="canceldialog()">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import blog from '@/api/evaluate/blog.js'
export default {
  data() {
    return {
      createTimes: [], // 选择创建日期表单数组
      tableData: [], // 表格数据，用户数据
      total: 0, // 总记录数
      pageSize: 10, // 每页记录数
      currentPage: 1, // 当前页码
      loading: true, // 懒加载
      searchObj: {}, // 表单搜索对象
      dialogForm: {}, // 博客详情表单
      dialogFormVisible: false // 博客详情弹框
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
      blog.getBlogList(this.currentPage, this.pageSize, this.searchObj)
        .then(res => {
          this.loading = false
          this.total = res.data.total
          this.tableData = res.data.records
          for (let i = 0; i < this.tableData.length; i++) {
            this.tableData[i].images = this.tableData[i].images.split(',').length > 1 ? 'http://localhost:8080' + this.tableData[i].images.split(',')[0] : 'http://localhost:8080' + this.tableData[i].images
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
      blog.queryById(id)
        .then(res => {
          this.dialogForm = res.data
          this.dialogForm.images = this.dialogForm.images.split(',')
          this.dialogFormVisible = true
        })
    },
    canceldialog() {
      this.dialogForm = {}
      this.dialogFormVisible = false
    },
    removeBlog(id) {
      this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          blog.removeBlog(id)
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

