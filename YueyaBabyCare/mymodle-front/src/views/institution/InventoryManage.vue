<template>
  <div class="inventory-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">库存管理</span>
          <el-button type="primary" icon="Plus" @click="handleAddRecord">库存操作</el-button>
        </div>
      </template>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon blue">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalProducts || 0 }}</div>
            <div class="stat-label">商品总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon emerald">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalStock || 0 }}</div>
            <div class="stat-label">库存总量</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon red">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.lowStockCount || 0 }}</div>
            <div class="stat-label">库存预警</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon cyan">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.todayInCount || 0 }}</div>
            <div class="stat-label">今日入库</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon amber">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16V4m0 0l4 4m-4-4l-4 4m-6 0v12m0 0l-4-4m4 4l4-4"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.todayOutCount || 0 }}</div>
            <div class="stat-label">今日出库</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon purple">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.monthInCount || 0 }}</div>
            <div class="stat-label">本月入库</div>
          </div>
        </div>
      </div>

      <!-- 标签页 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- 当前库存 -->
        <el-tab-pane label="当前库存" name="current">
          <el-form :inline="true" :model="stockQueryParams" class="search-form">
            <el-form-item label="商品名称">
              <el-input v-model="stockQueryParams.keyword" placeholder="请输入商品名称" clearable style="width: 200px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleStockQuery">查询</el-button>
              <el-button icon="Refresh" @click="handleStockReset">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table v-loading="stockLoading" :data="stockList" border style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="coverImg" label="封面" width="80">
              <template #default="{ row }">
                <el-image v-if="row.coverImg" :src="getImageUrl(row.coverImg)" style="width: 50px; height: 50px" fit="cover" />
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="商品名称" min-width="180" show-overflow-tooltip />
            <el-table-column prop="stock" label="当前库存" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getStockTagType(row)">{{ row.stock || 0 }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="stockAlert" label="预警值" width="100" align="center">
              <template #default="{ row }">
                <span>{{ row.stockAlert || 10 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="unit" label="单位" width="80" align="center" />
            <el-table-column prop="sales" label="销量" width="80" align="center" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleInStock(row)">入库</el-button>
                <el-button type="warning" size="small" @click="handleOutStock(row)">出库</el-button>
                <el-button type="info" size="small" @click="handleSetAlert(row)">预警</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="stockQueryParams.pageNum"
            v-model:page-size="stockQueryParams.pageSize"
            :total="stockTotal"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="getStockList"
            @current-change="getStockList"
            style="margin-top: 20px; justify-content: flex-end"
          />
        </el-tab-pane>

        <!-- 操作记录 -->
        <el-tab-pane label="操作记录" name="records">
          <el-form :inline="true" :model="recordQueryParams" class="search-form">
            <el-form-item label="操作类型">
              <el-select v-model="recordQueryParams.operateType" placeholder="全部" clearable style="width: 120px">
                <el-option label="全部" :value="null" />
                <el-option label="入库" :value="1" />
                <el-option label="出库" :value="2" />
                <el-option label="盘点" :value="3" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleRecordQuery">查询</el-button>
              <el-button icon="Refresh" @click="handleRecordReset">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table v-loading="recordLoading" :data="recordList" border style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="productCoverImg" label="商品" width="80">
              <template #default="{ row }">
                <el-image v-if="row.productCoverImg" :src="getImageUrl(row.productCoverImg)" style="width: 50px; height: 50px" fit="cover" />
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="productName" label="商品名称" min-width="150" show-overflow-tooltip />
            <el-table-column prop="operateTypeName" label="操作类型" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getOperateTypeTag(row.operateType)">{{ row.operateTypeName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="操作数量" width="100" align="center" />
            <el-table-column prop="beforeStock" label="操作前库存" width="100" align="center" />
            <el-table-column prop="afterStock" label="操作后库存" width="100" align="center" />
            <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
            <el-table-column prop="createTime" label="操作时间" width="180" />
          </el-table>

          <el-pagination
            v-model:current-page="recordQueryParams.pageNum"
            v-model:page-size="recordQueryParams.pageSize"
            :total="recordTotal"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="getRecordList"
            @current-change="getRecordList"
            style="margin-top: 20px; justify-content: flex-end"
          />
        </el-tab-pane>

        <!-- 库存预警 -->
        <el-tab-pane label="库存预警" name="lowStock">
          <el-alert v-if="lowStockList.length > 0" title="以下商品库存不足，请及时补货！" type="warning" show-icon :closable="false" style="margin-bottom: 20px" />
          <el-alert v-else title="暂无库存预警商品" type="success" show-icon :closable="false" style="margin-bottom: 20px" />

          <el-table v-loading="lowStockLoading" :data="lowStockList" border style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="coverImg" label="封面" width="80">
              <template #default="{ row }">
                <el-image v-if="row.coverImg" :src="getImageUrl(row.coverImg)" style="width: 50px; height: 50px" fit="cover" />
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="商品名称" min-width="180" show-overflow-tooltip />
            <el-table-column prop="stock" label="当前库存" width="100" align="center">
              <template #default="{ row }">
                <el-tag type="danger">{{ row.stock || 0 }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="stockAlert" label="预警值" width="100" align="center">
              <template #default="{ row }">
                <span>{{ row.stockAlert || 10 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="unit" label="单位" width="80" align="center" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleInStock(row)">立即入库</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 库存操作对话框 -->
    <el-dialog v-model="operateDialogVisible" :title="operateDialogTitle" width="500px" @close="resetOperateForm">
      <el-form ref="operateFormRef" :model="operateForm" :rules="operateFormRules" label-width="100px">
        <el-form-item label="商品" v-if="operateForm.productId">
          <span>{{ operateForm.productName }}</span>
        </el-form-item>
        <el-form-item label="商品" prop="productId" v-else>
          <el-select v-model="operateForm.productId" placeholder="请选择商品" filterable style="width: 100%" @change="handleProductChange">
            <el-option v-for="item in productOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作类型" prop="operateType">
          <el-radio-group v-model="operateForm.operateType" :disabled="operateTypeDisabled">
            <el-radio :value="1">入库</el-radio>
            <el-radio :value="2">出库</el-radio>
            <el-radio :value="3">盘点</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="操作数量" prop="quantity" v-if="operateForm.operateType !== 3">
          <el-input-number v-model="operateForm.quantity" :min="1" :max="99999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="盘点后库存" prop="afterStock" v-if="operateForm.operateType === 3">
          <el-input-number v-model="operateForm.afterStock" :min="0" :max="99999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="operateForm.remark" type="textarea" :rows="3" placeholder="请输入备注" maxlength="200" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="operateDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="operateSubmitting" @click="handleOperateSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 设置预警值对话框 -->
    <el-dialog v-model="alertDialogVisible" title="设置库存预警值" width="400px">
      <el-form ref="alertFormRef" :model="alertForm" label-width="100px">
        <el-form-item label="商品名称">
          <span>{{ alertForm.productName }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ alertForm.currentStock }}</span>
        </el-form-item>
        <el-form-item label="预警值" prop="alertValue">
          <el-input-number v-model="alertForm.alertValue" :min="0" :max="9999" style="width: 100%" />
          <div class="form-tip">当库存低于此值时将显示预警</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="alertDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="alertSubmitting" @click="handleAlertSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getInventoryList,
  getCurrentStock,
  getLowStockProducts,
  addInventoryRecord,
  setStockAlert,
  getInventoryStats
} from '@/api/inventory'

// 统计数据
const stats = ref({})

// 标签页
const activeTab = ref('current')

// 当前库存
const stockLoading = ref(false)
const stockList = ref([])
const stockTotal = ref(0)
const stockQueryParams = reactive({
  keyword: '',
  pageNum: 1,
  pageSize: 10
})

// 操作记录
const recordLoading = ref(false)
const recordList = ref([])
const recordTotal = ref(0)
const recordQueryParams = reactive({
  operateType: null,
  pageNum: 1,
  pageSize: 10
})

// 库存预警
const lowStockLoading = ref(false)
const lowStockList = ref([])

// 库存操作对话框
const operateDialogVisible = ref(false)
const operateSubmitting = ref(false)
const operateFormRef = ref(null)
const operateTypeDisabled = ref(false)
const productOptions = ref([])

const operateForm = reactive({
  productId: null,
  productName: '',
  operateType: 1,
  quantity: 1,
  afterStock: 0,
  remark: ''
})

const operateFormRules = {
  productId: [{ required: true, message: '请选择商品', trigger: 'change' }],
  operateType: [{ required: true, message: '请选择操作类型', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入操作数量', trigger: 'blur' }],
  afterStock: [{ required: true, message: '请输入盘点后库存', trigger: 'blur' }]
}

// 预警值对话框
const alertDialogVisible = ref(false)
const alertSubmitting = ref(false)
const alertFormRef = ref(null)
const alertForm = reactive({
  productId: null,
  productName: '',
  currentStock: 0,
  alertValue: 10
})

// 对话框标题
const operateDialogTitle = computed(() => {
  if (operateForm.operateType === 1) return '商品入库'
  if (operateForm.operateType === 2) return '商品出库'
  if (operateForm.operateType === 3) return '库存盘点'
  return '库存操作'
})

// 获取图片URL
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return process.env.VUE_APP_API_BASE_URL + url
}

// 获取库存标签类型
const getStockTagType = (row) => {
  const stock = row.stock || 0
  const alert = row.stockAlert || 10
  if (stock <= 0) return 'danger'
  if (stock <= alert) return 'warning'
  return 'success'
}

// 获取操作类型标签
const getOperateTypeTag = (type) => {
  if (type === 1) return 'success'
  if (type === 2) return 'warning'
  if (type === 3) return 'info'
  return ''
}

// 获取统计数据
const loadStats = async () => {
  try {
    const res = await getInventoryStats()
    stats.value = res.data || {}
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取当前库存列表
const getStockList = async () => {
  stockLoading.value = true
  try {
    const res = await getCurrentStock(stockQueryParams)
    stockList.value = res.data?.records || []
    stockTotal.value = res.data?.total || 0
    // 同时更新商品选项
    productOptions.value = stockList.value
  } catch (error) {
    console.error('获取库存列表失败:', error)
  } finally {
    stockLoading.value = false
  }
}

// 获取操作记录列表
const getRecordList = async () => {
  recordLoading.value = true
  try {
    const res = await getInventoryList(recordQueryParams)
    recordList.value = res.data?.records || []
    recordTotal.value = res.data?.total || 0
  } catch (error) {
    console.error('获取操作记录失败:', error)
  } finally {
    recordLoading.value = false
  }
}

// 获取库存预警列表
const getLowStockList = async () => {
  lowStockLoading.value = true
  try {
    const res = await getLowStockProducts()
    lowStockList.value = res.data || []
  } catch (error) {
    console.error('获取库存预警列表失败:', error)
  } finally {
    lowStockLoading.value = false
  }
}

// 标签页切换
const handleTabChange = (tab) => {
  if (tab === 'current') {
    getStockList()
  } else if (tab === 'records') {
    getRecordList()
  } else if (tab === 'lowStock') {
    getLowStockList()
  }
}

// 当前库存查询
const handleStockQuery = () => {
  stockQueryParams.pageNum = 1
  getStockList()
}

// 当前库存重置
const handleStockReset = () => {
  stockQueryParams.keyword = ''
  stockQueryParams.pageNum = 1
  getStockList()
}

// 操作记录查询
const handleRecordQuery = () => {
  recordQueryParams.pageNum = 1
  getRecordList()
}

// 操作记录重置
const handleRecordReset = () => {
  recordQueryParams.operateType = null
  recordQueryParams.pageNum = 1
  getRecordList()
}

// 新增库存操作
const handleAddRecord = () => {
  resetOperateForm()
  operateTypeDisabled.value = false
  operateDialogVisible.value = true
}

// 入库
const handleInStock = (row) => {
  resetOperateForm()
  operateForm.productId = row.id
  operateForm.productName = row.name
  operateForm.operateType = 1
  operateTypeDisabled.value = true
  operateDialogVisible.value = true
}

// 出库
const handleOutStock = (row) => {
  resetOperateForm()
  operateForm.productId = row.id
  operateForm.productName = row.name
  operateForm.operateType = 2
  operateTypeDisabled.value = true
  operateDialogVisible.value = true
}

// 商品选择变化
const handleProductChange = (productId) => {
  const product = productOptions.value.find(p => p.id === productId)
  if (product) {
    operateForm.productName = product.name
  }
}

// 重置操作表单
const resetOperateForm = () => {
  operateForm.productId = null
  operateForm.productName = ''
  operateForm.operateType = 1
  operateForm.quantity = 1
  operateForm.afterStock = 0
  operateForm.remark = ''
  operateFormRef.value?.resetFields()
}

// 提交库存操作
const handleOperateSubmit = async () => {
  if (!operateFormRef.value) return
  await operateFormRef.value.validate(async (valid) => {
    if (!valid) return
    operateSubmitting.value = true
    try {
      await addInventoryRecord(operateForm)
      ElMessage.success('操作成功')
      operateDialogVisible.value = false
      loadStats()
      getStockList()
      if (activeTab.value === 'records') {
        getRecordList()
      }
      if (activeTab.value === 'lowStock') {
        getLowStockList()
      }
    } catch (error) {
      console.error('操作失败:', error)
      ElMessage.error(error.message || '操作失败')
    } finally {
      operateSubmitting.value = false
    }
  })
}

// 设置预警值
const handleSetAlert = (row) => {
  alertForm.productId = row.id
  alertForm.productName = row.name
  alertForm.currentStock = row.stock || 0
  alertForm.alertValue = row.stockAlert || 10
  alertDialogVisible.value = true
}

// 提交预警值设置
const handleAlertSubmit = async () => {
  alertSubmitting.value = true
  try {
    await setStockAlert(alertForm.productId, alertForm.alertValue)
    ElMessage.success('设置成功')
    alertDialogVisible.value = false
    loadStats()
    getStockList()
    if (activeTab.value === 'lowStock') {
      getLowStockList()
    }
  } catch (error) {
    console.error('设置失败:', error)
    ElMessage.error(error.message || '设置失败')
  } finally {
    alertSubmitting.value = false
  }
}

onMounted(() => {
  loadStats()
  getStockList()
})
</script>

<style scoped lang="scss">
.inventory-manage {
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

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(6, 1fr);
    gap: 16px;
    margin-bottom: 24px;
    
    .stat-card {
      background: #FFFFFF;
      border: 1px solid #E5E7EB;
      border-radius: 12px;
      padding: 16px;
      display: flex;
      align-items: center;
      gap: 12px;
      transition: all 0.2s;
      
      &:hover {
        border-color: #D1D5DB;
        box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
      }
      
      .stat-icon {
        width: 44px;
        height: 44px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
        
        svg {
          width: 22px;
          height: 22px;
        }
        
        &.blue {
          background: #DBEAFE;
          color: #3B82F6;
        }
        
        &.emerald {
          background: #D1FAE5;
          color: #10B981;
        }
        
        &.red {
          background: #FEE2E2;
          color: #EF4444;
        }
        
        &.cyan {
          background: #CFFAFE;
          color: #06B6D4;
        }
        
        &.amber {
          background: #FEF3C7;
          color: #F59E0B;
        }
        
        &.purple {
          background: #EDE9FE;
          color: #8B5CF6;
        }
      }
      
      .stat-info {
        min-width: 0;
        
        .stat-value {
          font-size: 22px;
          font-weight: 700;
          color: #1F2937;
          line-height: 1.2;
        }
        
        .stat-label {
          font-size: 13px;
          color: #6B7280;
          margin-top: 2px;
          white-space: nowrap;
        }
      }
    }
  }

  .search-form {
    margin-bottom: 15px;
  }

  .form-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 5px;
  }
}
</style>
