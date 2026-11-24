<template>
  <div class="attendance-manage">
    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon blue">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.total || 0 }}</div>
          <div class="stat-label">总记录数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon emerald">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.normal || 0 }}</div>
          <div class="stat-label">正常出勤</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon amber">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.late || 0 }}</div>
          <div class="stat-label">迟到</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon rose">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.absent || 0 }}</div>
          <div class="stat-label">缺勤</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon purple">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.attendanceRate || 0 }}%</div>
          <div class="stat-label">出勤率</div>
        </div>
      </div>
    </div>

    <!-- 主体区域 -->
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <span class="title">考勤记录</span>
          <div class="header-actions">
            <el-button type="primary" icon="Plus" @click="checkInDialogVisible = true">签到</el-button>
            <el-button type="success" icon="Check" @click="checkOutDialogVisible = true">签退</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="员工">
          <el-select v-model="queryParams.nannyId" placeholder="全部" clearable style="width: 150px">
            <el-option v-for="nanny in nannyList" :key="nanny.id" :label="nanny.realName" :value="nanny.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="缺勤" :value="0" />
            <el-option label="正常" :value="1" />
            <el-option label="迟到" :value="2" />
            <el-option label="早退" :value="3" />
            <el-option label="请假" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="attendanceList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="nannyName" label="员工姓名" width="100" />
        <el-table-column prop="nannyPhone" label="手机号" width="130" />
        <el-table-column prop="attendanceDate" label="日期" width="120" />
        <el-table-column label="签到时间" width="170">
          <template #default="{ row }">
            <span v-if="row.checkInTime">{{ formatDateTime(row.checkInTime) }}</span>
            <el-tag v-else type="info" size="small">未签到</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="签退时间" width="170">
          <template #default="{ row }">
            <span v-if="row.checkOutTime">{{ formatDateTime(row.checkOutTime) }}</span>
            <el-tag v-else type="info" size="small">未签退</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip min-width="120" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="openEditDialog(row)">修改状态</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>

    <!-- 签到弹窗 -->
    <el-dialog v-model="checkInDialogVisible" title="员工签到" width="450px" destroy-on-close>
      <el-form :model="checkInForm" label-width="80px">
        <el-form-item label="选择员工" required>
          <el-select v-model="checkInForm.nannyId" placeholder="请选择员工" style="width: 100%">
            <el-option v-for="nanny in nannyList" :key="nanny.id" :label="nanny.realName" :value="nanny.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="checkInForm.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkInDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleCheckIn">确认签到</el-button>
      </template>
    </el-dialog>

    <!-- 签退弹窗 -->
    <el-dialog v-model="checkOutDialogVisible" title="员工签退" width="450px" destroy-on-close>
      <el-form :model="checkOutForm" label-width="80px">
        <el-form-item label="选择员工" required>
          <el-select v-model="checkOutForm.nannyId" placeholder="请选择员工" style="width: 100%">
            <el-option v-for="nanny in nannyList" :key="nanny.id" :label="nanny.realName" :value="nanny.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="checkOutForm.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkOutDialogVisible = false">取消</el-button>
        <el-button type="success" :loading="submitLoading" @click="handleCheckOut">确认签退</el-button>
      </template>
    </el-dialog>

    <!-- 修改状态弹窗 -->
    <el-dialog v-model="editDialogVisible" title="修改考勤状态" width="450px" destroy-on-close>
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="员工">
          <el-input :model-value="editForm.nannyName" disabled />
        </el-form-item>
        <el-form-item label="日期">
          <el-input :model-value="editForm.attendanceDate" disabled />
        </el-form-item>
        <el-form-item label="状态" required>
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option label="缺勤" :value="0" />
            <el-option label="正常" :value="1" />
            <el-option label="迟到" :value="2" />
            <el-option label="早退" :value="3" />
            <el-option label="请假" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleUpdateStatus">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAttendanceList, checkIn, checkOut, updateAttendanceStatus, getAttendanceStatistics } from '@/api/attendance'
import { getNannyList } from '@/api/institution'

const loading = ref(false)
const submitLoading = ref(false)
const attendanceList = ref([])
const nannyList = ref([])
const total = ref(0)
const dateRange = ref([])

const queryParams = reactive({
  nannyId: null,
  status: null,
  pageNum: 1,
  pageSize: 10
})

const stats = ref({})

const checkInDialogVisible = ref(false)
const checkOutDialogVisible = ref(false)
const editDialogVisible = ref(false)

const checkInForm = reactive({ nannyId: null, remark: '' })
const checkOutForm = reactive({ nannyId: null, remark: '' })
const editForm = reactive({ id: null, nannyName: '', attendanceDate: '', status: null, remark: '' })

onMounted(() => {
  loadNannyList()
  loadAttendance()
  loadStatistics()
})

const loadNannyList = async () => {
  try {
    const res = await getNannyList({ pageNum: 1, pageSize: 999, status: 1 })
    nannyList.value = res.data?.records || res.data || []
  } catch (e) {
    console.error('获取员工列表失败:', e)
  }
}

const loadAttendance = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getAttendanceList(params)
    attendanceList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('获取考勤列表失败:', e)
  } finally {
    loading.value = false
  }
}

const loadStatistics = async () => {
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getAttendanceStatistics(params)
    stats.value = res.data || {}
  } catch (e) {
    console.error('获取统计数据失败:', e)
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  loadAttendance()
  loadStatistics()
}

const handleReset = () => {
  queryParams.nannyId = null
  queryParams.status = null
  queryParams.pageNum = 1
  dateRange.value = []
  loadAttendance()
  loadStatistics()
}

const handleCheckIn = async () => {
  if (!checkInForm.nannyId) {
    ElMessage.warning('请选择员工')
    return
  }
  submitLoading.value = true
  try {
    await checkIn({ nannyId: checkInForm.nannyId, remark: checkInForm.remark })
    ElMessage.success('签到成功')
    checkInDialogVisible.value = false
    checkInForm.nannyId = null
    checkInForm.remark = ''
    loadAttendance()
    loadStatistics()
  } catch (e) {
    // 错误已由拦截器处理
  } finally {
    submitLoading.value = false
  }
}

const handleCheckOut = async () => {
  if (!checkOutForm.nannyId) {
    ElMessage.warning('请选择员工')
    return
  }
  submitLoading.value = true
  try {
    await checkOut({ nannyId: checkOutForm.nannyId, remark: checkOutForm.remark })
    ElMessage.success('签退成功')
    checkOutDialogVisible.value = false
    checkOutForm.nannyId = null
    checkOutForm.remark = ''
    loadAttendance()
    loadStatistics()
  } catch (e) {
    // 错误已由拦截器处理
  } finally {
    submitLoading.value = false
  }
}

const openEditDialog = (row) => {
  editForm.id = row.id
  editForm.nannyName = row.nannyName
  editForm.attendanceDate = row.attendanceDate
  editForm.status = row.status
  editForm.remark = row.remark || ''
  editDialogVisible.value = true
}

const handleUpdateStatus = async () => {
  if (editForm.status === null || editForm.status === undefined) {
    ElMessage.warning('请选择状态')
    return
  }
  submitLoading.value = true
  try {
    await updateAttendanceStatus(editForm.id, { status: editForm.status, remark: editForm.remark })
    ElMessage.success('状态更新成功')
    editDialogVisible.value = false
    loadAttendance()
    loadStatistics()
  } catch (e) {
    // 错误已由拦截器处理
  } finally {
    submitLoading.value = false
  }
}

const formatDateTime = (dt) => {
  if (!dt) return ''
  if (typeof dt === 'string') return dt.replace('T', ' ')
  return dt
}

const statusTagType = (status) => {
  const map = { 0: 'danger', 1: 'success', 2: 'warning', 3: 'warning', 4: 'info' }
  return map[status] || 'info'
}
</script>

<style scoped lang="scss">
.attendance-manage {
  padding: 0;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  margin-bottom: 20px;

  .stat-card {
    background: #fff;
    border: 1px solid #E5E7EB;
    border-radius: 12px;
    padding: 16px;
    display: flex;
    align-items: center;
    gap: 14px;
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

      svg { width: 22px; height: 22px; }

      &.blue { background: #DBEAFE; color: #3B82F6; }
      &.emerald { background: #D1FAE5; color: #10B981; }
      &.amber { background: #FEF3C7; color: #F59E0B; }
      &.rose { background: #FCE7F3; color: #EC4899; }
      &.purple { background: #EDE9FE; color: #8B5CF6; }
    }

    .stat-info {
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
      }
    }
  }
}

.main-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      font-size: 16px;
      font-weight: 600;
    }

    .header-actions {
      display: flex;
      gap: 10px;
    }
  }
}

.search-form {
  margin-bottom: 16px;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 1200px) {
  .stats-row { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 768px) {
  .stats-row { grid-template-columns: repeat(2, 1fr); }
}
</style>
