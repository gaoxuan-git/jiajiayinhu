<template>
  <div class="admin-product-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">商品管理</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="所属机构">
          <el-select v-model="queryParams.institutionId" placeholder="全部机构" clearable filterable style="width: 200px">
            <el-option v-for="inst in institutionList" :key="inst.id" :label="inst.name" :value="inst.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品分类">
          <el-tree-select
            v-model="queryParams.categoryId"
            :data="categoryTree"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="全部分类"
            clearable
            check-strictly
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="商品名称/描述" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="全部" :value="null" />
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 批量操作 -->
      <div class="batch-actions" v-if="selectedIds.length > 0">
        <span class="selected-count">已选择 {{ selectedIds.length }} 项</span>
        <el-button type="success" size="small" @click="handleBatchStatus(1)">批量上架</el-button>
        <el-button type="warning" size="small" @click="handleBatchStatus(0)">批量下架</el-button>
        <el-button type="danger" size="small" @click="handleBatchDelete">批量删除</el-button>
      </div>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="productList"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="coverImg" label="封面" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.coverImg"
              :src="getImageUrl(row.coverImg)"
              style="width: 60px; height: 60px"
              fit="cover"
              :preview-src-list="[getImageUrl(row.coverImg)]"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="institutionName" label="所属机构" width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tag type="info">{{ row.institutionName || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.stock > 10 ? 'success' : row.stock > 0 ? 'warning' : 'danger'">
              {{ row.stock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sales" label="销量" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" title="编辑商品" width="700px" @close="resetForm">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入商品名称" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品分类" prop="categoryId">
              <el-tree-select
                v-model="formData.categoryId"
                :data="categoryTree"
                :props="{ label: 'name', value: 'id', children: 'children' }"
                placeholder="请选择分类"
                check-strictly
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="销售价格" prop="price">
              <el-input-number v-model="formData.price" :min="0.01" :precision="2" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价">
              <el-input-number v-model="formData.originalPrice" :min="0" :precision="2" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存数量" prop="stock">
              <el-input-number v-model="formData.stock" :min="0" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位">
              <el-input v-model="formData.unit" placeholder="如：件、盒、瓶" maxlength="10" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="formData.sort" :min="0" :max="9999" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="formData.status">
                <el-radio :value="1">上架</el-radio>
                <el-radio :value="0">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="商品描述">
          <el-input v-model="formData.description" type="textarea" :rows="4" placeholder="请输入商品描述" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  adminGetProductList,
  adminUpdateProduct,
  adminDeleteProduct,
  adminUpdateProductStatus,
  adminBatchDeleteProducts,
  adminBatchUpdateStatus
} from '@/api/product'
import { getCategoryTree } from '@/api/productCategory'
import { getInstitutionList } from '@/api/institution'

const loading = ref(false)
const productList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const editingId = ref(null)
const categoryTree = ref([])
const institutionList = ref([])
const selectedIds = ref([])

const queryParams = reactive({
  institutionId: null,
  categoryId: null,
  keyword: '',
  status: null,
  pageNum: 1,
  pageSize: 10
})

const formData = reactive({
  categoryId: null,
  name: '',
  description: '',
  price: null,
  originalPrice: null,
  stock: 0,
  unit: '',
  sort: 0,
  status: 1
})

const formRules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入销售价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }]
}

// 获取图片完整URL
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return process.env.VUE_APP_API_BASE_URL + url
}

// 获取商品列表
const getList = async () => {
  loading.value = true
  try {
    const res = await adminGetProductList(queryParams)
    productList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取分类树
const loadCategoryTree = async () => {
  try {
    const res = await getCategoryTree()
    categoryTree.value = res.data || []
  } catch (error) {
    console.error('获取分类树失败:', error)
  }
}

// 获取机构列表
const loadInstitutionList = async () => {
  try {
    const res = await getInstitutionList()
    institutionList.value = res.data || []
  } catch (error) {
    console.error('获取机构列表失败:', error)
  }
}

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const handleReset = () => {
  queryParams.institutionId = null
  queryParams.categoryId = null
  queryParams.keyword = ''
  queryParams.status = null
  queryParams.pageNum = 1
  getList()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 编辑
const handleEdit = (row) => {
  editingId.value = row.id
  formData.categoryId = row.categoryId
  formData.name = row.name
  formData.description = row.description || ''
  formData.price = row.price
  formData.originalPrice = row.originalPrice
  formData.stock = row.stock
  formData.unit = row.unit || ''
  formData.sort = row.sort || 0
  formData.status = row.status
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除商品"${row.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await adminDeleteProduct(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

// 状态变更
const handleStatusChange = async (row) => {
  try {
    await adminUpdateProductStatus(row.id, row.status)
    ElMessage.success(row.status === 1 ? '已上架' : '已下架')
  } catch (error) {
    console.error('状态更新失败:', error)
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error(error.message || '状态更新失败')
  }
}

// 批量上架/下架
const handleBatchStatus = (status) => {
  const statusText = status === 1 ? '上架' : '下架'
  ElMessageBox.confirm(`确定要批量${statusText}选中的 ${selectedIds.value.length} 个商品吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await adminBatchUpdateStatus(selectedIds.value, status)
      ElMessage.success(`批量${statusText}成功`)
      getList()
    } catch (error) {
      console.error('批量操作失败:', error)
      ElMessage.error(error.message || '批量操作失败')
    }
  }).catch(() => {})
}

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个商品吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await adminBatchDeleteProducts(selectedIds.value)
      ElMessage.success('批量删除成功')
      getList()
    } catch (error) {
      console.error('批量删除失败:', error)
      ElMessage.error(error.message || '批量删除失败')
    }
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await adminUpdateProduct(editingId.value, formData)
      ElMessage.success('修改成功')
      dialogVisible.value = false
      getList()
    } catch (error) {
      console.error('提交失败:', error)
      ElMessage.error(error.message || '提交失败')
    } finally {
      submitting.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  editingId.value = null
}

onMounted(() => {
  getList()
  loadCategoryTree()
  loadInstitutionList()
})
</script>

<style scoped lang="scss">
.admin-product-manage {
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

  .batch-actions {
    margin-bottom: 15px;
    padding: 10px 15px;
    background: #f0f9eb;
    border-radius: 4px;
    display: flex;
    align-items: center;
    gap: 10px;
    .selected-count {
      color: #67c23a;
      font-weight: 500;
    }
  }

  .price {
    color: #f56c6c;
    font-weight: 600;
  }
}
</style>
