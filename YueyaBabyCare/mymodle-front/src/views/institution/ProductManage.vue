<template>
  <div class="product-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">商品管理</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">新增商品</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="商品状态">
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

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon blue">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.total }}</div>
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
            <div class="stat-value">{{ statistics.onSale }}</div>
            <div class="stat-label">上架中</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon amber">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.offSale }}</div>
            <div class="stat-label">已下架</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon purple">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.totalSales }}</div>
            <div class="stat-label">总销量</div>
          </div>
        </div>
      </div>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="productList" border style="width: 100%">
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
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="originalPrice" label="原价" width="100">
          <template #default="{ row }">
            <span v-if="row.originalPrice" class="original-price">¥{{ row.originalPrice }}</span>
            <span v-else>-</span>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" @close="resetForm">
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
        <el-form-item label="封面图片">
          <el-upload
            class="cover-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeUpload"
            accept="image/*"
          >
            <el-image v-if="formData.coverImg" :src="getImageUrl(formData.coverImg)" class="cover-image" fit="cover" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="商品图片">
          <el-upload
            v-model:file-list="imageFileList"
            :action="uploadUrl"
            :headers="uploadHeaders"
            list-type="picture-card"
            :on-success="handleImageSuccess"
            :on-remove="handleImageRemove"
            :before-upload="beforeUpload"
            accept="image/*"
            :limit="5"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getInstitutionProducts, createProduct, updateProduct, deleteProduct, updateProductStatus } from '@/api/product'
import { getCategoryTree } from '@/api/productCategory'

const loading = ref(false)
const productList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const editingId = ref(null)
const categoryTree = ref([])
const imageFileList = ref([])

const queryParams = reactive({
  status: null,
  pageNum: 1,
  pageSize: 10
})

const formData = reactive({
  categoryId: null,
  name: '',
  coverImg: '',
  images: [],
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

// 统计数据
const statistics = computed(() => {
  const total = productList.value.length
  const onSale = productList.value.filter(p => p.status === 1).length
  const offSale = productList.value.filter(p => p.status === 0).length
  const totalSales = productList.value.reduce((sum, p) => sum + (p.sales || 0), 0)
  return { total, onSale, offSale, totalSales }
})

// 对话框标题
const dialogTitle = computed(() => editingId.value ? '编辑商品' : '新增商品')

// 上传配置
const uploadUrl = process.env.VUE_APP_API_BASE_URL + '/file/upload'
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

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
    const res = await getInstitutionProducts(queryParams)
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

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const handleReset = () => {
  queryParams.status = null
  queryParams.pageNum = 1
  getList()
}

// 新增
const handleAdd = () => {
  editingId.value = null
  resetFormData()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  editingId.value = row.id
  formData.categoryId = row.categoryId
  formData.name = row.name
  formData.coverImg = row.coverImg || ''
  formData.images = row.images || []
  formData.description = row.description || ''
  formData.price = row.price
  formData.originalPrice = row.originalPrice
  formData.stock = row.stock
  formData.unit = row.unit || ''
  formData.sort = row.sort || 0
  formData.status = row.status
  // 设置图片列表
  imageFileList.value = (row.images || []).map((url, index) => ({
    name: `image-${index}`,
    url: getImageUrl(url)
  }))
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
      await deleteProduct(row.id)
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
    await updateProductStatus(row.id, row.status)
    ElMessage.success(row.status === 1 ? '已上架' : '已下架')
  } catch (error) {
    console.error('状态更新失败:', error)
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error(error.message || '状态更新失败')
  }
}

// 封面上传成功
const handleCoverSuccess = (response) => {
  if (response.code === 200) {
    formData.coverImg = response.data
    ElMessage.success('封面上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 商品图片上传成功
const handleImageSuccess = (response, file, fileList) => {
  if (response.code === 200) {
    formData.images = fileList.map(f => f.response?.data || f.url?.replace(process.env.VUE_APP_API_BASE_URL, ''))
  }
}

// 商品图片移除
const handleImageRemove = (file, fileList) => {
  formData.images = fileList.map(f => f.response?.data || f.url?.replace(process.env.VUE_APP_API_BASE_URL, ''))
}

// 上传前校验
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB!')
    return false
  }
  return true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const data = { ...formData }
      if (editingId.value) {
        await updateProduct(editingId.value, data)
        ElMessage.success('修改成功')
      } else {
        await createProduct(data)
        ElMessage.success('新增成功')
      }
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

// 重置表单数据
const resetFormData = () => {
  formData.categoryId = null
  formData.name = ''
  formData.coverImg = ''
  formData.images = []
  formData.description = ''
  formData.price = null
  formData.originalPrice = null
  formData.stock = 0
  formData.unit = ''
  formData.sort = 0
  formData.status = 1
  imageFileList.value = []
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  resetFormData()
  editingId.value = null
}

onMounted(() => {
  getList()
  loadCategoryTree()
})
</script>

<style scoped lang="scss">
.product-manage {
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
      transition: all 0.2s;
      
      &:hover {
        border-color: #D1D5DB;
        box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
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
        
        &.blue {
          background: #DBEAFE;
          color: #3B82F6;
        }
        
        &.emerald {
          background: #D1FAE5;
          color: #10B981;
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

  .price {
    color: #f56c6c;
    font-weight: 600;
  }

  .original-price {
    color: #999;
    text-decoration: line-through;
  }

  .cover-uploader {
    :deep(.el-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      width: 120px;
      height: 120px;
      display: flex;
      align-items: center;
      justify-content: center;
      &:hover {
        border-color: #409eff;
      }
    }
    .cover-image {
      width: 120px;
      height: 120px;
    }
    .cover-uploader-icon {
      font-size: 28px;
      color: #8c939d;
    }
  }
}
</style>
