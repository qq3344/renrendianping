<template>
  <div class="app-container">
    <el-row>
      <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
        <div class="left-panel">
          <el-form class="el-form el-form--inline" size="mini">
            <el-form-item label="店铺ID:">
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
          prop="shopName"
          label="店铺名称"
        />
        <el-table-column
          label="标题"
          prop="title"
        />
        <el-table-column
          label="优惠券类型"
        >
          <template slot-scope="scope">
            <el-tag
              v-if="scope.row.type === 1"
              type="danger"
            >
              秒杀券
            </el-tag>
            <el-tag
              v-if="scope.row.type === 0"
              type="success"
            >
              优惠券
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          label="使用规则"
          prop="rules"
          min-width="200px"
        />
        <el-table-column
          label="支付金额"
          show-overflow-tooltip
          align="center"
          prop="payValue"
        />
        <el-table-column
          label="抵扣金额"
          show-overflow-tooltip
          align="center"
          prop="actualValue"
        />
        <el-table-column
          label="操作"
          width="200"
          align="center"
          fixed="right"
        >
          <template slot-scope="scope">
            <el-button
              icon="el-icon-edit"
              type="primary"
              size="mini"
              @click="openVoucherDialog(scope.row.id)"
            >
              修改
            </el-button>
            <el-button
              icon="el-icon-delete"
              type="danger"
              size="mini"
              @click="removeVoucherById(scope.row.id)"
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
      title="修改优惠券"
      :visible.sync="voucherDialogFormVisible"
      width="30%"
      center
      top="5vh"
    >
      <el-form :model="voucherDialogForm" size="mini" style="padding-right: 10px;">
        <el-form-item label="标题" label-width="120px" prop="title">
          <el-input v-model="voucherDialogForm.title" autocomplete="off" />
        </el-form-item>
        <el-form-item label="副标题" label-width="120px" prop="subTitle">
          <el-input v-model="voucherDialogForm.subTitle" autocomplete="off" />
        </el-form-item>
        <el-form-item label="使用规则" label-width="120px" prop="rules">
          <el-input v-model="voucherDialogForm.rules" autocomplete="off" />
        </el-form-item>
        <el-form-item label="支付金额" label-width="120px" prop="payValue">
          <el-input v-model="voucherDialogForm.payValue" autocomplete="off" />
        </el-form-item>
        <el-form-item label="抵扣金额" label-width="120px" prop="actualValue">
          <el-input v-model="voucherDialogForm.actualValue" autocomplete="off" />
        </el-form-item>
        <el-form-item label="优惠券类型" label-width="120px" prop="type">
          <el-select v-model="voucherDialogForm.type" :disabled="voucherDialogForm.type == 1" placeholder="请选择">
            <el-option
              v-for="voucherType in voucherTypeOptions"
              :key="voucherType.value"
              :label="voucherType.label"
              :value="voucherType.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="voucherDialogForm.type == 1" label="库存" label-width="120px" prop="stock">
          <el-input v-model="voucherDialogForm.stock" type="text" autocomplete="off" />
        </el-form-item>
        <el-form-item v-if="voucherDialogForm.type == 1" label="生效时间" label-width="120px" prop="beginTime">
          <el-date-picker
            v-model="voucherDialogForm.beginTime"
            type="datetime"
            placeholder="选择生效时间"
            value-format="yyyy-MM-dd HH:mm:ss"
          />
        </el-form-item>
        <el-form-item v-if="voucherDialogForm.type == 1" label="失效时间" label-width="120px" prop="endTime">
          <el-date-picker
            v-model="voucherDialogForm.endTime"
            type="datetime"
            placeholder="选择失效时间"
            value-format="yyyy-MM-dd HH:mm:ss"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelVoucherDialog()">取 消</el-button>
        <el-button type="primary" @click="submitVoucherDialogForm()">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import voucher from '@/api/evaluate/voucher'
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
      voucherDialogForm: {}, // 优惠券表单
      voucherDialogFormVisible: false, // 优惠券弹框
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
      voucher.getVoucherList(this.currentPage, this.pageSize, this.searchObj)
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
    openVoucherDialog(id) {
      voucher.queryVoucherVoById(id)
        .then(res => {
          this.voucherDialogForm = res.data
          this.voucherDialogFormVisible = true
        })
    },
    submitVoucherDialogForm() {
      voucher.updateVoucher(this.voucherDialogForm)
        .then(res => {
          this.$message.success(res.message)
          this.fetchData()
          this.voucherDialogForm = {}
          this.voucherDialogFormVisible = false
        })
    },
    cancelVoucherDialog() {
      this.voucherDialogForm = {}
      this.voucherDialogFormVisible = false
    },
    removeVoucherById(id) {
      this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          voucher.removeVoucherById(id)
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

