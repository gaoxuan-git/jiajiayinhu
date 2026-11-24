<template>
  <div class="care-plan-manage">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="4">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.total || 0 }}</div>
            <div class="stats-label">总计划数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="5">
        <el-card class="stats-card pending" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.pending || 0 }}</div>
            <div class="stats-label">待执行</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="5">
        <el-card class="stats-card progress" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.inProgress || 0 }}</div>
            <div class="stats-label">进行中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="5">
        <el-card class="stats-card completed" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.completed || 0 }}</div>
            <div class="stats-label">已完成</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="5">
        <el-card class="stats-card cancelled" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.cancelled || 0 }}</div>
            <div class="stats-label">已取消</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">护理计划管理</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">新增计划</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="全部" :value="null" />
            <el-option label="待执行" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="计划名称/预约单号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="planList" border style="width: 100%">
        <el-table-column prop="appointmentNo" label="关联预约" width="180">
          <template #default="{ row }">
            <span v-if="row.appointmentNo">{{ row.appointmentNo }}</span>
            <el-tag v-else type="info" size="small">无</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="planName" label="计划名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="babyName" label="宝宝" width="100">
          <template #default="{ row }">
            {{ row.babyName }}
          </template>
        </el-table-column>
        <el-table-column prop="parentName" label="家长" width="100" />
        <el-table-column prop="nannyName" label="服务保姆" width="100">
          <template #default="{ row }">
            <span v-if="row.nannyName">{{ row.nannyName }}</span>
            <el-tag v-else type="info" size="small">待分配</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="计划周期" width="200">
          <template #default="{ row }">
            {{ row.startDate }} ~ {{ row.endDate }}
          </template>
        </el-table-column>
        <el-table-column label="进度" width="150">
          <template #default="{ row }">
            <el-progress :percentage="row.progressPercent || 0" :status="getProgressStatus(row.status)" />
            <span class="progress-text">{{ row.executionDays || 0 }}/{{ row.totalDays || 0 }}天</span>
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleView(row)">详情</el-button>
            <el-button
              v-if="row.status === 0"
              type="warning"
              size="small"
              link
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="row.status === 0 && !row.nannyId"
              type="warning"
              size="small"
              link
              @click="handleAssign(row)"
            >
              分配保姆
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="success"
              size="small"
              link
              @click="handleStart(row)"
            >
              开始执行
            </el-button>
            <el-button
              v-if="row.status === 1"
              type="success"
              size="small"
              link
              @click="handleComplete(row)"
            >
              完成
            </el-button>
            <el-button
              v-if="row.status === 0 || row.status === 1"
              type="danger"
              size="small"
              link
              @click="handleCancel(row)"
            >
              取消
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="danger"
              size="small"
              link
              @click="handleDelete(row)"
            >
              删除
            </el-button>
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
    <el-dialog
      v-model="formVisible"
      :title="isEdit ? '编辑护理计划' : '新增护理计划'"
      width="600px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <!-- 选择预约（新增时显示） -->
        <el-form-item v-if="!isEdit" label="关联预约" prop="appointmentId">
          <el-select
            v-model="form.appointmentId"
            placeholder="请选择预约"
            filterable
            style="width: 100%"
            @change="handleAppointmentChange"
          >
            <el-option
              v-for="item in availableAppointments"
              :key="item.id"
              :label="`${item.appointmentNo} - ${item.babyName} (${item.serviceTypeName})`"
              :value="item.id"
            />
          </el-select>
          <div v-if="!availableAppointments.length" class="no-data-tip">
            暂无可用预约，请先确认预约单
          </div>
        </el-form-item>

        <!-- 预约信息展示 -->
        <el-form-item v-if="selectedAppointment" label="预约信息">
          <div class="appointment-info">
            <el-descriptions :column="2" size="small" border>
              <el-descriptions-item label="宝宝">{{ selectedAppointment.babyName }}</el-descriptions-item>
              <el-descriptions-item label="家长">{{ selectedAppointment.parentName }}</el-descriptions-item>
              <el-descriptions-item label="服务类型">{{ selectedAppointment.serviceTypeName }}</el-descriptions-item>
              <el-descriptions-item label="保姆">
                {{ selectedAppointment.nannyName || '待分配' }}
              </el-descriptions-item>
              <el-descriptions-item label="预约日期" :span="2">{{ selectedAppointment.appointmentDate }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-form-item>

        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="form.planName" placeholder="请输入计划名称" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="计划内容" prop="planContent">
          <el-input
            v-model="form.planContent"
            type="textarea"
            :rows="4"
            placeholder="请输入护理计划详细内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="计划周期" prop="dateRange">
          <el-date-picker
            v-model="form.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="护理计划详情" width="700px">
      <el-descriptions v-if="currentPlan" :column="2" border>
        <el-descriptions-item label="关联预约" :span="2">
          {{ currentPlan.appointmentNo || '无' }}
        </el-descriptions-item>
        <el-descriptions-item label="计划名称" :span="2">
          {{ currentPlan.planName }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTag(currentPlan.status)">{{ currentPlan.statusName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="执行进度">
          <el-progress :percentage="currentPlan.progressPercent || 0" :status="getProgressStatus(currentPlan.status)" style="width: 150px" />
          <span style="margin-left: 10px">{{ currentPlan.executionDays || 0 }}/{{ currentPlan.totalDays || 0 }}天</span>
        </el-descriptions-item>
        <el-descriptions-item label="宝宝">{{ currentPlan.babyName }}</el-descriptions-item>
        <el-descriptions-item label="家长">{{ currentPlan.parentName }} ({{ currentPlan.parentPhone }})</el-descriptions-item>
        <el-descriptions-item label="服务保姆">
          {{ currentPlan.nannyName || '待分配' }}
          <span v-if="currentPlan.nannyPhone">({{ currentPlan.nannyPhone }})</span>
        </el-descriptions-item>
        <el-descriptions-item label="所属机构">{{ currentPlan.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="计划周期" :span="2">
          {{ currentPlan.startDate }} ~ {{ currentPlan.endDate }}
        </el-descriptions-item>
        <el-descriptions-item label="计划内容" :span="2">
          <div class="plan-content">{{ currentPlan.planContent }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentPlan.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentPlan.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 分配保姆对话框 -->
    <el-dialog v-model="assignVisible" title="分配保姆" width="500px">
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="计划名称">{{ currentPlan?.planName }}</el-form-item>
        <el-form-item label="选择保姆" required>
          <el-select
            v-model="assignForm.nannyId"
            placeholder="请选择保姆"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="nanny in nannyOptions"
              :key="nanny.id"
              :label="`${nanny.realName} (${nanny.phone})`"
              :value="nanny.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" :loading="assignLoading" @click="submitAssign">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getCarePlanList,
  createCarePlan,
  updateCarePlan,
  deleteCarePlan,
  updateCarePlanStatus,
  assignNanny,
  getAvailableAppointments
} from '@/api/carePlan'
import { getNannyList } from '@/api/institution'

const loading = ref(false)
const planList = ref([])
const total = ref(0)
const formVisible = ref(false)
const detailVisible = ref(false)
const assignVisible = ref(false)
const submitLoading = ref(false)
const assignLoading = ref(false)
const isEdit = ref(false)
const currentPlan = ref(null)
const formRef = ref(null)
const nannyOptions = ref([])
const availableAppointments = ref([])

const queryParams = reactive({
  status: null,
  keyword: null,
  pageNum: 1,
  pageSize: 10
})

const form = reactive({
  id: null,
  appointmentId: null,
  planName: '',
  planContent: '',
  dateRange: null
})

const assignForm = reactive({
  nannyId: null
})

const rules = {
  appointmentId: [{ required: true, message: '请选择预约', trigger: 'change' }],
  planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  planContent: [{ required: true, message: '请输入计划内容', trigger: 'blur' }],
  dateRange: [{ required: true, message: '请选择计划周期', trigger: 'change' }]
}

// 统计数据
const statsData = computed(() => {
  const list = planList.value
  return {
    total: total.value,
    pending: list.filter(item => item.status === 0).length,
    inProgress: list.filter(item => item.status === 1).length,
    completed: list.filter(item => item.status === 2).length,
    cancelled: list.filter(item => item.status === 3).length
  }
})

// 选中的预约信息
const selectedAppointment = computed(() => {
  if (!form.appointmentId) return null
  return availableAppointments.value.find(item => item.id === form.appointmentId)
})

// 状态标签类型
const getStatusTag = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
  return map[status] || 'info'
}

// 进度条状态
const getProgressStatus = (status) => {
  if (status === 2) return 'success'
  if (status === 3) return 'exception'
  return ''
}

// 获取护理计划列表
const getList = () => {
  loading.value = true
  getCarePlanList(queryParams)
    .then(response => {
      planList.value = response.data.records || []
      total.value = response.data.total || 0
    })
    .catch(error => {
      console.error('获取列表失败:', error)
    })
    .finally(() => {
      loading.value = false
    })
}

// 获取保姆列表
const getNannies = () => {
  getNannyList({ status: 1, pageNum: 1, pageSize: 100 })
    .then(response => {
      nannyOptions.value = response.data.records || []
    })
    .catch(error => {
      console.error('获取保姆列表失败:', error)
    })
}

// 获取可用预约列表
const getAvailableAppointmentList = () => {
  getAvailableAppointments()
    .then(response => {
      availableAppointments.value = response.data || []
    })
    .catch(error => {
      console.error('获取可用预约失败:', error)
    })
}

// 预约选择变化
const handleAppointmentChange = (val) => {
  // 选择预约后可以自动设置默认计划名称
  const appointment = availableAppointments.value.find(item => item.id === val)
  if (appointment && !form.planName) {
    form.planName = `${appointment.babyName}的护理计划`
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
  queryParams.keyword = null
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  getList()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  getAvailableAppointmentList()
  formVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.appointmentId = row.appointmentId
  form.planName = row.planName
  form.planContent = row.planContent
  form.dateRange = [row.startDate, row.endDate]
  formVisible.value = true
}

// 查看详情
const handleView = (row) => {
  currentPlan.value = row
  detailVisible.value = true
}

// 重置表单
const resetForm = () => {
  form.id = null
  form.appointmentId = null
  form.planName = ''
  form.planContent = ''
  form.dateRange = null
  formRef.value?.resetFields()
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate((valid) => {
    if (!valid) return

    submitLoading.value = true
    const data = {
      appointmentId: form.appointmentId,
      planName: form.planName,
      planContent: form.planContent,
      startDate: form.dateRange[0],
      endDate: form.dateRange[1]
    }

    const request = isEdit.value
      ? updateCarePlan(form.id, data)
      : createCarePlan(data)

    request
      .then(() => {
        ElMessage.success(isEdit.value ? '修改成功' : '创建成功')
        formVisible.value = false
        getList()
      })
      .catch(error => {
        console.error('操作失败:', error)
      })
      .finally(() => {
        submitLoading.value = false
      })
  })
}

// 开始执行
const handleStart = (row) => {
  if (!row.nannyId) {
    ElMessage.warning('请先分配保姆')
    return
  }
  ElMessageBox.confirm('确定要开始执行该计划吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    updateCarePlanStatus(row.id, 1)
      .then(() => {
        ElMessage.success('计划已开始执行')
        getList()
      })
      .catch(error => {
        console.error('操作失败:', error)
      })
  }).catch(() => {})
}

// 完成
const handleComplete = (row) => {
  ElMessageBox.confirm('确定要完成该计划吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(() => {
    updateCarePlanStatus(row.id, 2)
      .then(() => {
        ElMessage.success('计划已完成')
        getList()
      })
      .catch(error => {
        console.error('操作失败:', error)
      })
  }).catch(() => {})
}

// 取消
const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该计划吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    updateCarePlanStatus(row.id, 3)
      .then(() => {
        ElMessage.success('计划已取消')
        getList()
      })
      .catch(error => {
        console.error('操作失败:', error)
      })
  }).catch(() => {})
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该计划吗？删除后无法恢复。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(() => {
    deleteCarePlan(row.id)
      .then(() => {
        ElMessage.success('删除成功')
        getList()
      })
      .catch(error => {
        console.error('删除失败:', error)
      })
  }).catch(() => {})
}

// 打开分配保姆对话框
const handleAssign = (row) => {
  currentPlan.value = row
  assignForm.nannyId = row.nannyId
  assignVisible.value = true
}

// 提交分配保姆
const submitAssign = () => {
  if (!assignForm.nannyId) {
    ElMessage.warning('请选择保姆')
    return
  }
  assignLoading.value = true
  assignNanny(currentPlan.value.id, assignForm.nannyId)
    .then(() => {
      ElMessage.success('分配成功')
      assignVisible.value = false
      getList()
    })
    .catch(error => {
      console.error('分配失败:', error)
    })
    .finally(() => {
      assignLoading.value = false
    })
}

onMounted(() => {
  getList()
  getNannies()
})
</script>

<style scoped lang="scss">
.care-plan-manage {
  padding: 20px;

  .stats-row {
    margin-bottom: 20px;

    .stats-card {
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-3px);
      }

      .stats-content {
        text-align: center;
        padding: 10px 0;

        .stats-value {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
        }

        .stats-label {
          font-size: 14px;
          color: #909399;
          margin-top: 8px;
        }
      }

      &.pending .stats-value {
        color: #e6a23c;
      }

      &.progress .stats-value {
        color: #409eff;
      }

      &.completed .stats-value {
        color: #67c23a;
      }

      &.cancelled .stats-value {
        color: #909399;
      }
    }
  }

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

  .progress-text {
    font-size: 12px;
    color: #909399;
    margin-left: 8px;
  }

  .no-data-tip {
    color: #909399;
    font-size: 12px;
    margin-top: 8px;
  }

  .appointment-info {
    width: 100%;
  }

  .plan-content {
    white-space: pre-wrap;
    line-height: 1.6;
  }
}
</style>
