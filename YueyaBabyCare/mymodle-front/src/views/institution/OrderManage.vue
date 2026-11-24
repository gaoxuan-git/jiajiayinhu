<template>
  <div class="order-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">订单管理</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="订单状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="全部" :value="null" />
            <el-option label="待发货" :value="1" />
            <el-option label="待收货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
            <el-option label="已退款" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单编号">
          <el-input v-model="queryParams.orderNo" placeholder="请输入订单编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card" @click="filterByStatus(1)">
          <div class="stat-icon amber">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingDelivery || 0 }}</div>
            <div class="stat-label">待发货</div>
          </div>
        </div>
        <div class="stat-card" @click="filterByStatus(2)">
          <div class="stat-icon blue">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingReceive || 0 }}</div>
            <div class="stat-label">待收货</div>
          </div>
        </div>
        <div class="stat-card" @click="filterByStatus(3)">
          <div class="stat-icon emerald">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.completed || 0 }}</div>
            <div class="stat-label">已完成</div>
          </div>
        </div>
        <div class="stat-card" @click="filterByStatus(null)">
          <div class="stat-icon purple">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.total || 0 }}</div>
            <div class="stat-label">全部订单</div>
          </div>
        </div>
      </div>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="orderList" border style="width: 100%">
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column prop="parentName" label="买家" width="100" />
        <el-table-column label="商品信息" min-width="250">
          <template #default="{ row }">
            <div v-for="item in row.items" :key="item.id" class="order-item">
              <el-image
                v-if="item.productImg"
                :src="getImageUrl(item.productImg)"
                style="width: 40px; height: 40px; margin-right: 8px"
                fit="cover"
              />
              <div class="item-info">
                <div class="item-name">{{ item.productName }}</div>
                <div class="item-price">¥{{ item.price }} × {{ item.quantity }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="payAmount" label="实付金额" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.payAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 1" type="success" size="small" link @click="handleShip(row)">发货</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <div v-if="currentOrder" class="order-detail">
        <!-- 订单基本信息 -->
        <el-descriptions title="订单信息" :column="2" border>
          <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentOrder.status)">{{ currentOrder.statusName }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ currentOrder.payTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ getPayTypeName(currentOrder.payType) }}</el-descriptions-item>
          <el-descriptions-item label="实付金额">
            <span class="price">¥{{ currentOrder.payAmount }}</span>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 收货信息 -->
        <el-descriptions title="收货信息" :column="1" border style="margin-top: 20px">
          <el-descriptions-item label="收货人">{{ currentOrder.receiverName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址">{{ currentOrder.receiverAddress }}</el-descriptions-item>
        </el-descriptions>

        <!-- 物流信息 -->
        <el-descriptions v-if="currentOrder.deliveryNo" title="物流信息" :column="2" border style="margin-top: 20px">
          <el-descriptions-item label="物流公司">{{ currentOrder.deliveryCompany }}</el-descriptions-item>
          <el-descriptions-item label="物流单号">{{ currentOrder.deliveryNo }}</el-descriptions-item>
          <el-descriptions-item label="发货时间">{{ currentOrder.deliveryTime }}</el-descriptions-item>
          <el-descriptions-item v-if="currentOrder.receiveTime" label="收货时间">{{ currentOrder.receiveTime }}</el-descriptions-item>
        </el-descriptions>

        <!-- 商品列表 -->
        <div class="goods-title">商品信息</div>
        <el-table :data="currentOrder.items" border style="width: 100%">
          <el-table-column label="商品" min-width="200">
            <template #default="{ row }">
              <div class="goods-info">
                <el-image
                  v-if="row.productImg"
                  :src="getImageUrl(row.productImg)"
                  style="width: 50px; height: 50px; margin-right: 10px"
                  fit="cover"
                />
                <span>{{ row.productName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="100">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" align="center" />
          <el-table-column prop="totalAmount" label="小计" width="100">
            <template #default="{ row }">¥{{ row.totalAmount }}</template>
          </el-table-column>
        </el-table>

        <!-- 订单备注 -->
        <div v-if="currentOrder.remark" class="remark-section">
          <div class="remark-title">订单备注</div>
          <div class="remark-content">{{ currentOrder.remark }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="currentOrder && currentOrder.status === 1" type="primary" @click="handleShip(currentOrder)">发货</el-button>
      </template>
    </el-dialog>

    <!-- 发货对话框 -->
    <el-dialog v-model="shipVisible" title="订单发货" width="500px">
      <el-form ref="shipFormRef" :model="shipForm" :rules="shipRules" label-width="100px">
        <el-form-item label="物流公司" prop="deliveryCompany">
          <el-select v-model="shipForm.deliveryCompany" placeholder="请选择物流公司" style="width: 100%">
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="圆通速递" value="圆通速递" />
            <el-option label="韵达快递" value="韵达快递" />
            <el-option label="申通快递" value="申通快递" />
            <el-option label="邮政快递" value="邮政快递" />
            <el-option label="京东物流" value="京东物流" />
            <el-option label="极兔速递" value="极兔速递" />
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号" prop="deliveryNo">
          <el-input v-model="shipForm.deliveryNo" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button type="primary" :loading="shipping" @click="confirmShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getInstitutionOrders, getInstitutionOrderDetail, addLogistics, getInstitutionOrderStats } from '@/api/order'

const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const detailVisible = ref(false)
const shipVisible = ref(false)
const shipping = ref(false)
const currentOrder = ref(null)
const shipFormRef = ref(null)
const stats = ref({})

const queryParams = reactive({
  status: null,
  orderNo: '',
  pageNum: 1,
  pageSize: 10
})

const shipForm = reactive({
  orderId: null,
  deliveryCompany: '',
  deliveryNo: ''
})

const shipRules = {
  deliveryCompany: [{ required: true, message: '请选择物流公司', trigger: 'change' }],
  deliveryNo: [{ required: true, message: '请输入物流单号', trigger: 'blur' }]
}

// 获取图片完整URL
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return process.env.VUE_APP_API_BASE_URL + url
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'primary',
    2: 'info',
    3: 'success',
    4: 'info',
    5: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取支付方式名称
const getPayTypeName = (payType) => {
  const nameMap = {
    1: '微信支付',
    2: '支付宝',
    3: '余额支付'
  }
  return nameMap[payType] || '-'
}

// 获取订单列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getInstitutionOrders(queryParams)
    orderList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取统计数据
const getStats = async () => {
  try {
    const res = await getInstitutionOrderStats()
    stats.value = res.data || {}
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const handleReset = () => {
  queryParams.status = null
  queryParams.orderNo = ''
  queryParams.pageNum = 1
  getList()
}

// 按状态筛选
const filterByStatus = (status) => {
  queryParams.status = status
  queryParams.pageNum = 1
  getList()
}

// 查看详情
const handleDetail = async (row) => {
  try {
    const res = await getInstitutionOrderDetail(row.id)
    currentOrder.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  }
}

// 发货
const handleShip = (row) => {
  shipForm.orderId = row.id
  shipForm.deliveryCompany = ''
  shipForm.deliveryNo = ''
  detailVisible.value = false
  shipVisible.value = true
}

// 确认发货
const confirmShip = async () => {
  if (!shipFormRef.value) return
  await shipFormRef.value.validate(async (valid) => {
    if (!valid) return
    shipping.value = true
    try {
      await addLogistics(shipForm.orderId, shipForm.deliveryCompany, shipForm.deliveryNo)
      ElMessage.success('发货成功')
      shipVisible.value = false
      getList()
      getStats()
    } catch (error) {
      console.error('发货失败:', error)
      ElMessage.error(error.message || '发货失败')
    } finally {
      shipping.value = false
    }
  })
}

onMounted(() => {
  getList()
  getStats()
})
</script>

<style scoped lang="scss">
.order-manage {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .title {
      font-size: 18px;
      font-weight: 600;
    }
  }

  .search-form {
    margin-bottom: 20px;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-bottom: 24px;
    
    .stat-card {
      background: #FFFFFF;
      border: 1px solid #E5E7EB;
      border-radius: 12px;
      padding: 20px;
      display: flex;
      align-items: center;
      gap: 16px;
      cursor: pointer;
      transition: all 0.2s;
      
      &:hover {
        border-color: #D1D5DB;
        box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
        transform: translateY(-2px);
      }
      
      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
        
        svg {
          width: 24px;
          height: 24px;
        }
        
        &.amber {
          background: #FEF3C7;
          color: #F59E0B;
        }
        
        &.blue {
          background: #DBEAFE;
          color: #3B82F6;
        }
        
        &.emerald {
          background: #D1FAE5;
          color: #10B981;
        }
        
        &.purple {
          background: #EDE9FE;
          color: #8B5CF6;
        }
      }
      
      .stat-info {
        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: #1F2937;
          line-height: 1.2;
        }
        
        .stat-label {
          font-size: 14px;
          color: #6B7280;
          margin-top: 4px;
        }
      }
    }
  }

  .order-item {
    display: flex;
    align-items: center;
    padding: 5px 0;
    .item-info {
      .item-name {
        font-size: 13px;
        color: #333;
      }
      .item-price {
        font-size: 12px;
        color: #999;
      }
    }
  }

  .price {
    color: #f56c6c;
    font-weight: 600;
  }
}

.order-detail {
  .goods-title {
    font-size: 16px;
    font-weight: 600;
    margin: 20px 0 10px;
    padding-left: 10px;
    border-left: 3px solid #409eff;
  }

  .goods-info {
    display: flex;
    align-items: center;
  }

  .remark-section {
    margin-top: 20px;
    padding: 15px;
    background: #f5f7fa;
    border-radius: 4px;
    .remark-title {
      font-weight: 600;
      margin-bottom: 8px;
    }
    .remark-content {
      color: #666;
    }
  }
}
</style>
