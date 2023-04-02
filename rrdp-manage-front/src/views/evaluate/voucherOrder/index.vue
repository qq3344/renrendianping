<template>
  <div class="app-container">
    <el-row>
      <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
        <div class="left-panel">
          <el-form class="el-form el-form--inline" size="mini">
            <el-form-item label="内容:">
              <el-input v-model="searchObj.shopName" type="text" autocomplete="off" placeholder="请输入商铺名称" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="searchObj.nickName" type="text" autocomplete="off" placeholder="请输入购买者昵称" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="searchObj.voucherType" placeholder="请选择优惠券类型">
                <el-option
                  v-for="item in voucherTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />

              </el-select>
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
          label="商铺名称"
          prop="shopName"
          show-overflow-tooltip
        />
        <el-table-column
          label="商铺类型"
        >
          <template slot-scope="scope">
            <el-tag>{{ scope.row.shopType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="优惠券标题"
          prop="title"
          show-overflow-tooltip
        />
        <el-table-column
          label="优惠券类型"
        >
          <template slot-scope="scope">
            <el-tag v-if="scope.row.voucherTypeId === 0" type="success">优惠券</el-tag>
            <el-tag v-if="scope.row.voucherTypeId === 1" type="danger">秒杀券</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="购买者昵称"
          prop="nickName"
        />
        <el-table-column
          label="支付类型"
          prop="payType"
          align="center"
        >
          <template slot-scope="scope">
            <el-tag v-if="scope.row.payType === 1" type="warning">余额支付</el-tag>
            <el-tag v-if="scope.row.payType === 2">支付宝</el-tag>
            <el-tag v-if="scope.row.payType === 3" type="success">微信</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="订单状态"
          prop="status"
          align="center"
        >
          <template slot-scope="scope">
            <el-tag v-if="scope.row.payType === 1" type="danger">未支付</el-tag>
            <el-tag v-if="scope.row.payType === 2" type="success">已支付</el-tag>
            <el-tag v-if="scope.row.payType === 3" type="info">已核销</el-tag>
            <el-tag v-if="scope.row.payType === 4" type="warning">已取消</el-tag>
            <el-tag v-if="scope.row.payType === 5" type="danger">退款中</el-tag>
            <el-tag v-if="scope.row.payType === 6" type="danger">已退款</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="购买时间"
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
              @click="removeVoucherOrder(scope.row.id)"
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
import voucherOrder from '@/api/evaluate/voucherOrder'
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
      voucherTypeOptions: [{
        value: 0,
        label: '优惠券'
      }, {
        value: 1,
        label: '秒杀券'
      }] // 优惠劵类型
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
      voucherOrder.getVoucherOrderList(this.currentPage, this.pageSize, this.searchObj)
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

    removeVoucherOrder(id) {
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

