<template>
  <div class="category-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">商品分类管理</span>
          <el-button type="primary" icon="Plus" @click="handleAdd(null)">新增分类</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="queryStatus" placeholder="全部" clearable style="width: 120px" @change="loadCategoryTree">
            <el-option label="全部" :value="null" />
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button icon="Refresh" @click="handleRefresh">刷新</el-button>
        </el-form-item>
      </el-form>

      <!-- 分类树表格 -->
      <el-table
        v-loading="loading"
        :data="categoryTree"
        row-key="id"
        border
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        style="width: 100%"
      >
        <el-table-column prop="name" label="分类名称" min-width="200">
          <template #default="{ row }">
            <span>{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.icon"
              :src="getIconUrl(row.icon)"
              style="width: 32px; height: 32px"
              fit="contain"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center" />
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
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleAdd(row)">添加子分类</el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="父级分类">
          <el-tree-select
            v-model="formData.parentId"
            :data="parentOptions"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择父级分类（不选则为顶级分类）"
            clearable
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入分类名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="分类图标">
          <el-input v-model="formData.icon" placeholder="请输入图标路径" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sort" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
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
import {
  getCategoryTree,
  createCategory,
  updateCategory,
  deleteCategory,
  updateCategoryStatus
} from '@/api/productCategory'

const loading = ref(false)
const categoryTree = ref([])
const queryStatus = ref(null)
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const editingId = ref(null)

const formData = reactive({
  parentId: null,
  name: '',
  icon: '',
  sort: 0,
  status: 1
})

const formRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
  ]
}

// 对话框标题
const dialogTitle = computed(() => {
  return editingId.value ? '编辑分类' : '新增分类'
})

// 父级分类选项（添加顶级选项）
const parentOptions = computed(() => {
  return [{ id: 0, name: '顶级分类', children: categoryTree.value }]
})

// 获取图标完整URL
const getIconUrl = (icon) => {
  if (!icon) return ''
  if (icon.startsWith('http')) return icon
  return process.env.VUE_APP_API_BASE_URL + icon
}

// 加载分类树
const loadCategoryTree = async () => {
  loading.value = true
  try {
    const res = await getCategoryTree()
    categoryTree.value = res.data || []
  } catch (error) {
    console.error('获取分类树失败:', error)
    ElMessage.error('获取分类树失败')
  } finally {
    loading.value = false
  }
}

// 刷新
const handleRefresh = () => {
  queryStatus.value = null
  loadCategoryTree()
}

// 新增分类
const handleAdd = (parent) => {
  editingId.value = null
  formData.parentId = parent ? parent.id : 0
  formData.name = ''
  formData.icon = ''
  formData.sort = 0
  formData.status = 1
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row) => {
  editingId.value = row.id
  formData.parentId = row.parentId || 0
  formData.name = row.name
  formData.icon = row.icon || ''
  formData.sort = row.sort || 0
  formData.status = row.status
  dialogVisible.value = true
}

// 删除分类
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除分类"${row.name}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteCategory(row.id)
      ElMessage.success('删除成功')
      loadCategoryTree()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

// 状态变更
const handleStatusChange = async (row) => {
  try {
    await updateCategoryStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    console.error('状态更新失败:', error)
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error(error.message || '状态更新失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const data = {
        parentId: formData.parentId || 0,
        name: formData.name,
        icon: formData.icon || null,
        sort: formData.sort,
        status: formData.status
      }

      if (editingId.value) {
        await updateCategory(editingId.value, data)
        ElMessage.success('修改成功')
      } else {
        await createCategory(data)
        ElMessage.success('新增成功')
      }

      dialogVisible.value = false
      loadCategoryTree()
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
  loadCategoryTree()
})
</script>

<style scoped lang="scss">
.category-manage {
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
}
</style>
