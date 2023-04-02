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
    <el-row>
      <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
        <div class="left-panel">
          <el-button
            type="primary"
            icon="el-icon-plus"
            size="mini"
            @click="openDialog()"
          >添加</el-button>
        </div>
      </el-col>
    </el-row>

    <div style="height:430px;overflow: scroll;">
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
          align="center"
        >
          <template slot-scope="scope">{{ (currentPage - 1) * pageSize + scope.$index +1 }}</template>
        </el-table-column>
        <el-table-column
          prop="name"
          label="店铺名称"
          width="180"
        />
        <el-table-column
          label="商铺类型"
          align="center"
        >
          <template slot-scope="scope">
            <el-tag
              type="warning"
            >
              {{ scope.row.typeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="图片"
          min-width="100"
          align="center"
        >
          <template slot-scope="scope">
            <img :src="scope.row.images" style="width: 100px;height: 100px;">
          </template>
        </el-table-column>
        <el-table-column
          prop="area"
          label="商圈"
          show-overflow-tooltip
        />
        <el-table-column
          prop="address"
          label="地址"
          show-overflow-tooltip
        />
        <el-table-column
          prop="openHours"
          label="开业时间"
          show-overflow-tooltip
          align="center"
        />
        <el-table-column
          prop="createTime"
          label="创建"
          show-overflow-tooltip
          align="center"
        />
        <el-table-column
          fixed="right"
          label="操作"
          width="310"
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
              @click="openVoucherDialog(scope.row.id)"
            >
              添加优惠劵
            </el-button>
            <el-button
              icon="el-icon-delete"
              type="danger"
              size="mini"
              @click="removeShop(scope.row.id)"
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
        <el-form-item label="店铺名称" label-width="120px" prop="name">
          <el-input v-model="dialogForm.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="商铺类型" label-width="120px" prop="typeId">
          <el-select v-model="dialogForm.typeId" :disabled="dialogForm.id != null" placeholder="请选择">
            <el-option
              v-for="shopType in shopTypeList"
              :key="shopType.id"
              :label="shopType.name"
              :value="shopType.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商圈" label-width="120px" prop="area">
          <el-input v-model="dialogForm.area" type="text" autocomplete="off" />
        </el-form-item>
        <el-form-item label="地址" label-width="120px" prop="address">
          <el-input v-model="dialogForm.address" autocomplete="off" />
        </el-form-item>
        <el-form-item label="开业时间" label-width="120px" prop="openHours">
          <el-input v-model="dialogForm.openHours" autocomplete="off" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelDialog()">取 消</el-button>
        <el-button type="primary" @click="submitDialogForm(dialogForm.id)">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="添加优惠券"
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
          <el-select v-model="voucherDialogForm.type" placeholder="请选择">
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
import shopType from '@/api/evaluate/shopType'
import shop from '@/api/evaluate/shop'
import voucher from '@/api/evaluate/voucher'
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
      shopTypeList: [], // 商铺类型
      voucherDialogForm: {}, // 优惠券表单
      voucherDialogFormVisible: false, // 优惠券弹框
      shopId: null, // 添加优惠券的商铺id
      voucherTypeOptions: [{
        value: '0',
        label: '优惠券'
      }, {
        value: '1',
        label: '秒杀券'
      }]
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
      shop.getShopList(this.currentPage, this.pageSize, this.searchObj)
        .then(res => {
          this.loading = false
          this.total = res.data.total
          this.tableData = res.data.records
          for (let i = 0; i < this.tableData.length; i++) {
            this.tableData[i].images = this.tableData[i].images.split(',').length > 1 ? this.tableData[i].images.split(',')[0] : this.tableData[i].images
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
      shopType.queryAll().then(
        res => {
          this.shopTypeList = res.data
        }
      )
      if (id == null) {
        this.dialogTitle = '添加商铺'
        this.dialogFormVisible = true
      } else {
        this.dialogTitle = '更新店铺'
        this.dialogFormVisible = true
        this.shopTypeController = true
        shop.queryById(id)
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
        this.updateSysUser(id)
      } else {
        this.saveShop()
      }
    },
    saveShop() {
      shop.saveShop(this.dialogForm)
        .then(res => {
          this.$message.success('添加成功！')
          this.dialogFormVisible = false
          this.dialogForm = {}
          this.fetchData()
        })
        .catch(this.$message.error)
    },
    updateSysUser() {
      shop.updateShop(this.dialogForm)
        .then(res => {
          this.$message.success('修改成功！')
          this.dialogForm = {}
          this.dialogFormVisible = false
          this.fetchData()
        })
        .catch(this.$message.error)
    },
    removeShop(id) {
      this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          shop.removeShop(id)
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
    openVoucherDialog(id) {
      this.shopId = id
      this.voucherDialogFormVisible = true
      this.voucherDialogForm = {}
    },
    submitVoucherDialogForm() {
      this.voucherDialogForm.shopId = this.shopId
      voucher.saveVoucher(this.voucherDialogForm)
        .then(res => {
          this.$message.success(res.message)
          this.fetchData()
          this.voucherDialogForm = {}
          this.voucherDialogFormVisible = false
          this.shopId = null
        })
    },
    cancelVoucherDialog() {
      this.voucherDialogForm = {}
      this.voucherDialogFormVisible = false
      this.shopId = null
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
