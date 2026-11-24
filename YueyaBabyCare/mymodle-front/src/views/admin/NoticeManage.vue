<template>
  <div class="notice-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">公告管理</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">新增公告</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="公告类型">
          <el-select v-model="queryParams.type" placeholder="全部" clearable style="width: 120px" @change="loadNoticeList">
            <el-option label="全部" :value="null" />
            <el-option label="系统公告" :value="1" />
            <el-option label="活动通知" :value="2" />
            <el-option label="其他" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标类型">
          <el-select v-model="queryParams.targetType" placeholder="全部" clearable style="width: 120px" @change="loadNoticeList">
            <el-option label="全部用户" :value="0" />
            <el-option label="家长" :value="1" />
            <el-option label="保姆" :value="2" />
            <el-option label="机构" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px" @change="loadNoticeList">
            <el-option label="全部" :value="null" />
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下线" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="标题/内容" clearable style="width: 150px" @keyup.enter="loadNoticeList" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="loadNoticeList">搜索</el-button>
          <el-button icon="Refresh" @click="handleRefresh">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 公告列表 -->
      <el-table v-loading="loading" :data="noticeList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="title" label="公告标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="typeName" label="公告类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">{{ row.typeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetTypeName" label="目标用户" width="100" align="center" />
        <el-table-column prop="statusName" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" type="success" size="small" @click="handlePublish(row)">发布</el-button>
            <el-button v-if="row.status === 1" type="info" size="small" @click="handleOffline(row)">下线</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadNoticeList"
          @current-change="loadNoticeList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" @close="resetForm">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择公告类型" style="width: 100%">
            <el-option label="系统公告" :value="1" />
            <el-option label="活动通知" :value="2" />
            <el-option label="其他" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标用户" prop="targetType">
          <el-select v-model="formData.targetType" placeholder="请选择目标用户" style="width: 100%">
            <el-option label="全部用户" :value="0" />
            <el-option label="家长" :value="1" />
            <el-option label="保姆" :value="2" />
            <el-option label="机构" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="8" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="0">草稿</el-radio>
            <el-radio :value="1">立即发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看对话框 -->
    <el-dialog v-model="viewDialogVisible" title="公告详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="公告标题" :span="2">{{ viewData.title }}</el-descriptions-item>
        <el-descriptions-item label="公告类型">
          <el-tag :type="getTypeTagType(viewData.type)">{{ viewData.typeName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="目标用户">{{ viewData.targetTypeName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(viewData.status)">{{ viewData.statusName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ viewData.publishTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ viewData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ viewData.updateTime }}</el-descriptions-item>
        <el-descriptions-item label="公告内容" :span="2">
          <div class="notice-content">{{ viewData.content }}</div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getNoticeList,
  createNotice,
  updateNotice,
  deleteNotice,
  updateNoticeStatus
} from '@/api/notice'

const loading = ref(false)
const noticeList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const editingId = ref(null)

const queryParams = reactive({
  type: null,
  targetType: null,
  status: null,
  keyword: '',
  pageNum: 1,
  pageSize: 10
})

const formData = reactive({
  title: '',
  type: 1,
  targetType: 0,
  content: '',
  status: 0
})

const viewData = ref({})

const formRules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  type: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
  targetType: [{ required: true, message: '请选择目标用户', trigger: 'change' }]
}

// 对话框标题
const dialogTitle = computed(() => {
  return editingId.value ? '编辑公告' : '新增公告'
})

// 获取类型标签样式
const getTypeTagType = (type) => {
  const types = { 1: 'primary', 2: 'success', 3: 'info' }
  return types[type] || 'info'
}

// 获取状态标签样式
const getStatusTagType = (status) => {
  const types = { 0: 'info', 1: 'success', 2: 'warning' }
  return types[status] || 'info'
}

// 加载公告列表
const loadNoticeList = async () => {
  loading.value = true
  try {
    const res = await getNoticeList(queryParams)
    noticeList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取公告列表失败:', error)
    ElMessage.error('获取公告列表失败')
  } finally {
    loading.value = false
  }
}

// 刷新
const handleRefresh = () => {
  queryParams.type = null
  queryParams.targetType = null
  queryParams.status = null
  queryParams.keyword = ''
  queryParams.pageNum = 1
  loadNoticeList()
}

// 新增公告
const handleAdd = () => {
  editingId.value = null
  formData.title = ''
  formData.type = 1
  formData.targetType = 0
  formData.content = ''
  formData.status = 0
  dialogVisible.value = true
}

// 编辑公告
const handleEdit = (row) => {
  editingId.value = row.id
  formData.title = row.title
  formData.type = row.type
  formData.targetType = row.targetType
  formData.content = row.content || ''
  formData.status = row.status
  dialogVisible.value = true
}

// 查看公告
const handleView = (row) => {
  viewData.value = row
  viewDialogVisible.value = true
}

// 发布公告
const handlePublish = (row) => {
  ElMessageBox.confirm(`确定要发布公告"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateNoticeStatus(row.id, 1)
      ElMessage.success('发布成功')
      loadNoticeList()
    } catch (error) {
      console.error('发布失败:', error)
      ElMessage.error(error.message || '发布失败')
    }
  }).catch(() => {})
}

// 下线公告
const handleOffline = (row) => {
  ElMessageBox.confirm(`确定要下线公告"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateNoticeStatus(row.id, 2)
      ElMessage.success('下线成功')
      loadNoticeList()
    } catch (error) {
      console.error('下线失败:', error)
      ElMessage.error(error.message || '下线失败')
    }
  }).catch(() => {})
}

// 删除公告
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除公告"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteNotice(row.id)
      ElMessage.success('删除成功')
      loadNoticeList()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败')
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
      const data = {
        title: formData.title,
        type: formData.type,
        targetType: formData.targetType,
        content: formData.content,
        status: formData.status
      }

      if (editingId.value) {
        await updateNotice(editingId.value, data)
        ElMessage.success('修改成功')
      } else {
        await createNotice(data)
        ElMessage.success('新增成功')
      }

      dialogVisible.value = false
      loadNoticeList()
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
  loadNoticeList()
})
</script>

<style scoped lang="scss">
.notice-manage {
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

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }

  .notice-content {
    white-space: pre-wrap;
    word-break: break-all;
    max-height: 300px;
    overflow-y: auto;
  }
}
</style>