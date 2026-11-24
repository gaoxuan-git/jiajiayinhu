<template>
  <div class="appointment-manage">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="4">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ stats.totalCount || 0 }}</div>
            <div class="stats-label">总预约数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card pending" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ stats.pendingCount || 0 }}</div>
            <div class="stats-label">待确认</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card confirmed" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ stats.confirmedCount || 0 }}</div>
            <div class="stats-label">已确认</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card progress" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ stats.inProgressCount || 0 }}</div>
            <div class="stats-label">进行中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card completed" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ stats.completedCount || 0 }}</div>
            <div class="stats-label">已完成</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card cancelled" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ stats.cancelledCount || 0 }}</div>
            <div class="stats-label">已取消</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">预约管理</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="全部" :value="null" />
            <el-option label="待确认" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="进行中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="服务类型">
          <el-select v-model="queryParams.serviceType" placeholder="全部" clearable style="width: 120px">
            <el-option label="全部" :value="null" />
            <el-option label="日常护理" :value="1" />
            <el-option label="月嫂服务" :value="2" />
            <el-option label="育儿指导" :value="3" />
            <el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="预约单号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="appointmentList"
        border
        style="width: 100%"
      >
        <el-table-column prop="appointmentNo" label="预约单号" width="180" />
        <el-table-column prop="parentName" label="家长" width="100" />
        <el-table-column prop="parentPhone" label="联系电话" width="120" />
        <el-table-column prop="babyName" label="宝宝" width="100">
          <template #default="{ row }">
            {{ row.babyName }}
            <span v-if="row.babyAgeMonths" class="baby-age">({{ row.babyAgeMonths }}月龄)</span>
          </template>
        </el-table-column>
        <el-table-column prop="serviceTypeName" label="服务类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getServiceTypeTag(row.serviceType)">{{ row.serviceTypeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="appointmentDate" label="预约日期" width="110" />
        <el-table-column label="服务时间" width="120">
          <template #default="{ row }">
            {{ formatTime(row.startTime) }} - {{ formatTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="nannyName" label="服务保姆" width="100">
          <template #default="{ row }">
            <span v-if="row.nannyName">{{ row.nannyName }}</span>
            <el-tag v-else type="info" size="small">待分配</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleView(row)">
              详情
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="success"
              size="small"
              link
              @click="handleConfirm(row)"
            >
              确认
            </el-button>
            <el-button
              v-if="row.status === 0 || row.status === 1"
              type="warning"
              size="small"
              link
              @click="handleAssign(row)"
            >
              分配保姆
            </el-button>
            <el-button
              v-if="row.status === 1"
              type="primary"
              size="small"
              link
              @click="handleStart(row)"
            >
              开始服务
            </el-button>
            <el-button
              v-if="row.status === 2"
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

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="预约详情"
      width="700px"
    >
      <el-descriptions v-if="currentAppointment" :column="2" border>
        <el-descriptions-item label="预约单号" :span="2">
          {{ currentAppointment.appointmentNo }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTag(currentAppointment.status)">
            {{ currentAppointment.statusName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="服务类型">
          <el-tag :type="getServiceTypeTag(currentAppointment.serviceType)">
            {{ currentAppointment.serviceTypeName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="家长姓名">{{ currentAppointment.parentName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentAppointment.parentPhone }}</el-descriptions-item>
        <el-descriptions-item label="宝宝姓名">{{ currentAppointment.babyName }}</el-descriptions-item>
        <el-descriptions-item label="宝宝月龄">{{ currentAppointment.babyAgeMonths || '-' }}个月</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{ currentAppointment.appointmentDate }}</el-descriptions-item>
        <el-descriptions-item label="服务时间">
          {{ formatTime(currentAppointment.startTime) }} - {{ formatTime(currentAppointment.endTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="服务保姆">
          {{ currentAppointment.nannyName || '待分配' }}
        </el-descriptions-item>
        <el-descriptions-item label="保姆电话">
          {{ currentAppointment.nannyPhone || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="服务需求" :span="2">
          {{ currentAppointment.requirement || '无' }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentAppointment.cancelReason" label="取消原因" :span="2">
          {{ currentAppointment.cancelReason }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentAppointment.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentAppointment.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 分配保姆对话框 -->
    <el-dialog
      v-model="assignVisible"
      title="分配保姆"
      width="500px"
    >
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="预约单号">
          {{ currentAppointment?.appointmentNo }}
        </el-form-item>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAppointmentList,
  updateAppointmentStatus,
  assignNanny,
  getAppointmentStats
} from '@/api/appointment'
import { getNannyList } from '@/api/institution'

const loading = ref(false)
const appointmentList = ref([])
const total = ref(0)
const detailVisible = ref(false)
const assignVisible = ref(false)
const assignLoading = ref(false)
const currentAppointment = ref(null)
const dateRange = ref(null)
const nannyOptions = ref([])
const stats = ref({})

const queryParams = reactive({
  status: null,
  serviceType: null,
  startDate: null,
  endDate: null,
  keyword: null,
  pageNum: 1,
  pageSize: 10
})

const assignForm = reactive({
  nannyId: null
})

// 状态标签类型
const getStatusTag = (status) => {
  const map = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: '',
    4: 'info'
  }
  return map[status] || 'info'
}

// 服务类型标签
const getServiceTypeTag = (type) => {
  const map = {
    1: '',
    2: 'success',
    3: 'warning',
    4: 'info'
  }
  return map[type] || 'info'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  // 处理 "HH:mm:ss" 格式，只显示 HH:mm
  if (time.length > 5) {
    return time.substring(0, 5)
  }
  return time
}

// 日期范围变化
const handleDateChange = (val) => {
  if (val && val.length === 2) {
    queryParams.startDate = val[0]
    queryParams.endDate = val[1]
  } else {
    queryParams.startDate = null
    queryParams.endDate = null
  }
}

// 获取预约列表
const getList = () => {
  loading.value = true
  getAppointmentList(queryParams)
    .then(response => {
      appointmentList.value = response.data.records || []
      total.value = response.data.total || 0
    })
    .catch(error => {
      console.error('获取列表失败:', error)
    })
    .finally(() => {
      loading.value = false
    })
}

// 获取统计数据
const getStats = () => {
  getAppointmentStats({
    startDate: queryParams.startDate,
    endDate: queryParams.endDate
  })
    .then(response => {
      stats.value = response.data || {}
    })
    .catch(error => {
      console.error('获取统计失败:', error)
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

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
  getStats()
}

// 重置
const handleReset = () => {
  queryParams.status = null
  queryParams.serviceType = null
  queryParams.startDate = null
  queryParams.endDate = null
  queryParams.keyword = null
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  dateRange.value = null
  getList()
  getStats()
}

// 查看详情
const handleView = (row) => {
  currentAppointment.value = row
  detailVisible.value = true
}

// 确认预约
const handleConfirm = (row) => {
  ElMessageBox.confirm('确定要确认该预约吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    updateAppointmentStatus(row.id, 1)
      .then(() => {
        ElMessage.success('预约已确认')
        getList()
        getStats()
      })
      .catch(error => {
        console.error('确认失败:', error)
      })
  }).catch(() => {})
}

// 开始服务
const handleStart = (row) => {
  if (!row.nannyId) {
    ElMessage.warning('请先分配保姆')
    return
  }
  ElMessageBox.confirm('确定要开始服务吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    updateAppointmentStatus(row.id, 2)
      .then(() => {
        ElMessage.success('服务已开始')
        getList()
        getStats()
      })
      .catch(error => {
        console.error('操作失败:', error)
      })
  }).catch(() => {})
}

// 完成服务
const handleComplete = (row) => {
  ElMessageBox.confirm('确定要完成该服务吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(() => {
    updateAppointmentStatus(row.id, 3)
      .then(() => {
        ElMessage.success('服务已完成')
        getList()
        getStats()
      })
      .catch(error => {
        console.error('操作失败:', error)
      })
  }).catch(() => {})
}

// 取消预约
const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该预约吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    updateAppointmentStatus(row.id, 4)
      .then(() => {
        ElMessage.success('预约已取消')
        getList()
        getStats()
      })
      .catch(error => {
        console.error('取消失败:', error)
      })
  }).catch(() => {})
}

// 打开分配保姆对话框
const handleAssign = (row) => {
  currentAppointment.value = row
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
  assignNanny(currentAppointment.value.id, assignForm.nannyId)
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
  getStats()
  getNannies()
})
</script>

<style scoped lang="scss">
.appointment-manage {
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

      &.confirmed .stats-value {
        color: #409eff;
      }

      &.progress .stats-value {
        color: #67c23a;
      }

      &.completed .stats-value {
        color: #909399;
      }

      &.cancelled .stats-value {
        color: #f56c6c;
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

  .baby-age {
    color: #909399;
    font-size: 12px;
  }
}
</style>
