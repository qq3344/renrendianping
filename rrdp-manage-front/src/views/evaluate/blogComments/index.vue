<template>
  <div class="app-container">
    <el-row>
      <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
        <div class="left-panel">
          <el-form class="el-form el-form--inline" size="mini">
            <el-form-item label="内容:">
              <el-input v-model="searchObj.content" type="text" autocomplete="off" placeholder="请输入博客内容" />
            </el-form-item>
            <el-form-item label="发布者:">
              <el-input v-model="searchObj.username" type="text" autocomplete="off" placeholder="请输入发布者名称" />
            </el-form-item>
            <el-form-item>
              <el-button icon="el-icon-search" type="primary" size="mini" @click="fetchData()">查询</el-button>
              <el-button icon="el-icon-refresh" type="warning" size="mini" @click="resetData()">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>

    <div style="height:420px;overflow: scroll;">
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
            <img :src="scope.row.icon ? 'http://localhost:8080'+scope.row.icon:'http://localhost:8080/imgs/icons/default-icon.png'" style="width: 100px;height: 100px;">
          </template>
        </el-table-column>
        <el-table-column
          prop="nickName"
          label="评论人"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            <el-tag type="danger">{{ scope.row.nickName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="被评论人"
        >
          <template slot-scope="scope">
            <el-tag type="info">{{ scope.row.pnickName ? scope.row.pnickName : '无' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="来源"
        >
          <el-tag type="warning">博客</el-tag>
        </el-table-column>
        <el-table-column
          label="内容"
          prop="content"
          show-overflow-tooltip
        />
        <el-table-column
          label="发布时间"
          align="center"
          prop="createTime"
          show-overflow-tooltip
        />
        <el-table-column
          label="操作"
          align="center"
          fixed="right"
        >
          <template slot-scope="scope">
            <el-button
              icon="el-icon-delete"
              type="danger"
              size="mini"
              @click="removeBlogComment(scope.row.id)"
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
  </div>
</template>

<script>
import blogComments from '@/api/evaluate/blogComments'
export default {
  data() {
    return {
      createTimes: [], // 选择创建日期表单数组
      tableData: [], // 表格数据，用户数据
      total: 0, // 总记录数
      pageSize: 10, // 每页记录数
      currentPage: 1, // 当前页码
      loading: true, // 懒加载
      searchObj: {} // 表单搜索对象
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
      blogComments.getBlogCommentsList(this.currentPage, this.pageSize, this.searchObj)
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

    removeBlogComment(id) {
      this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
        //   blog.removeBlog(id)
        //     .then(res => {
        //       this.$message.success('删除成功！')
        //       this.fetchData()
        //     })
        //     .catch(this.$message.error)
          this.$message.success('模拟删除成功！')
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

