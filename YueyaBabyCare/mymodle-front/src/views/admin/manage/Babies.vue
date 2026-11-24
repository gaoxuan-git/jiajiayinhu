<template>
  <div class="manage-page">
    <!-- 顶部区域 -->
    <el-card class="header-card">
      <div class="header-content">
        <el-button v-if="parentId" :icon="ArrowLeft" @click="goBack">返回家长列表</el-button>
        <span class="title">{{ parentName ? `${parentName}的宝宝` : '婴幼儿管理' }}</span>
      </div>
    </el-card>

    <!-- 搜索栏（仅全量模式） -->
    <el-card v-if="!parentId" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.keyword" placeholder="搜索宝宝姓名/昵称" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="searchForm.gender" placeholder="全部" clearable @change="handleSearch">
            <el-option label="男" :value="1" />
            <el-option label="女" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span class="title">宝宝列表</span>
          <span class="total">共 {{ filteredData.length }} 条</span>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="filteredData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar
              :size="50"
              :src="row.avatar"
              :icon="UserFilled"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="100" />
        <el-table-column label="性别" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.gender === 1" type="primary">男</el-tag>
            <el-tag v-else-if="row.gender === 2" type="danger">女</el-tag>
            <el-tag v-else type="info">未知</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="birthDate" label="出生日期" width="120" />
        <el-table-column label="月龄" width="80">
          <template #default="{ row }">
            {{ row.ageInMonths !== null && row.ageInMonths !== undefined ? row.ageInMonths + '个月' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="bloodType" label="血型" width="80" />
        <el-table-column prop="allergyInfo" label="过敏信息" width="150" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="editForm.id ? '编辑宝宝信息' : '新增宝宝'"
      width="600px"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入宝宝姓名" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="editForm.nickname" placeholder="请输入宝宝昵称" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="editForm.gender">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
            <el-radio :value="0">未知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker
            v-model="editForm.birthDate"
            type="date"
            placeholder="选择出生日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="血型" prop="bloodType">
          <el-select v-model="editForm.bloodType" placeholder="请选择血型" clearable>
            <el-option label="A型" value="A" />
            <el-option label="B型" value="B" />
            <el-option label="AB型" value="AB" />
            <el-option label="O型" value="O" />
          </el-select>
        </el-form-item>
        <el-form-item label="过敏信息" prop="allergyInfo">
          <el-input
            v-model="editForm.allergyInfo"
            type="textarea"
            :rows="2"
            placeholder="请输入过敏信息"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="editForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, UserFilled } from '@element-plus/icons-vue'
import { getAllBabies, getBabiesByParentId, updateBaby, deleteBaby } from '@/api/baby'

const route = useRoute()
const router = useRouter()

// 家长信息（从家长页面跳转时使用）
const parentId = ref(null)
const parentName = ref('')

// 搜索表单
const searchForm = ref({
  keyword: '',
  gender: null
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 前端搜索过滤
const filteredData = computed(() => {
  let data = tableData.value
  if (searchForm.value.keyword) {
    const kw = searchForm.value.keyword.toLowerCase()
    data = data.filter(item =>
      (item.name && item.name.toLowerCase().includes(kw)) ||
      (item.nickname && item.nickname.toLowerCase().includes(kw))
    )
  }
  if (searchForm.value.gender !== null && searchForm.value.gender !== '') {
    data = data.filter(item => item.gender === searchForm.value.gender)
  }
  return data
})

// 编辑表单
const editDialogVisible = ref(false)
const editFormRef = ref(null)
const submitLoading = ref(false)
const editForm = ref({
  id: null,
  name: '',
  nickname: '',
  gender: 0,
  birthDate: '',
  bloodType: '',
  allergyInfo: '',
  remark: ''
})

const editRules = {
  name: [{ required: true, message: '请输入宝宝姓名', trigger: 'blur' }],
  birthDate: [{ required: true, message: '请选择出生日期', trigger: 'change' }]
}

// 返回家长列表
const goBack = () => {
  router.push('/user-manage/parents')
}

// 搜索
const handleSearch = () => {
  // filteredData 是计算属性，自动响应
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = { keyword: '', gender: null }
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    let res
    if (parentId.value) {
      // 查看指定家长的宝宝
      res = await getBabiesByParentId(parentId.value)
    } else {
      // 查看所有宝宝
      res = await getAllBabies()
    }
    tableData.value = res.data || []
  } catch (error) {
    console.error('获取宝宝列表失败:', error)
    ElMessage.error('获取宝宝列表失败')
  } finally {
    loading.value = false
  }
}

// 编辑
const handleEdit = (row) => {
  editForm.value = {
    id: row.id,
    name: row.name,
    nickname: row.nickname,
    gender: row.gender,
    birthDate: row.birthDate,
    bloodType: row.bloodType,
    allergyInfo: row.allergyInfo,
    remark: row.remark
  }
  editDialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await updateBaby(editForm.value.id, editForm.value)
        ElMessage.success('保存成功')
        editDialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error('保存失败:', error)
        ElMessage.error('保存失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 删除
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该宝宝信息吗？此操作不可恢复！',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )

    await deleteBaby(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  parentId.value = route.query.parentId || null
  parentName.value = route.query.parentName || ''
  fetchData()
})
</script>

<style scoped lang="scss">
.manage-page {
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;

  .header-content {
    display: flex;
    align-items: center;
    gap: 20px;

    .title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
    }
  }
}

.search-card {
  margin-bottom: 20px;

  .search-form {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
  }
}

.table-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      font-size: 16px;
      font-weight: 600;
    }

    .total {
      font-size: 14px;
      color: #909399;
    }
  }
}
</style>
