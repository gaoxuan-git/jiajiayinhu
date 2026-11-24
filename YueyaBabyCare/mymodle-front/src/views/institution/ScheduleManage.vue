<template>
  <div class="schedule-manage">
    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon blue">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.todaySchedules }}</div>
          <div class="stat-label">今日排班</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon emerald">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.workingNannies }}</div>
          <div class="stat-label">今日在岗</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon amber">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pendingSwaps }}</div>
          <div class="stat-label">待审核调班</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon purple">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.monthSchedules }}</div>
          <div class="stat-label">本月总排班</div>
        </div>
      </div>
    </div>

    <!-- 主体区域 -->
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane label="日历视图" name="calendar" />
            <el-tab-pane label="排班列表" name="list" />
            <el-tab-pane label="调班申请" name="swap">
              <template #label>
                <span>调班申请</span>
                <el-badge v-if="stats.pendingSwaps > 0" :value="stats.pendingSwaps" class="swap-badge" />
              </template>
            </el-tab-pane>
          </el-tabs>
          <div class="header-actions">
            <el-button type="primary" icon="Plus" @click="handleAdd">新增排班</el-button>
            <el-button type="success" icon="Document" @click="batchDialogVisible = true">批量排班</el-button>
          </div>
        </div>
      </template>

      <!-- 日历视图 -->
      <div v-show="activeTab === 'calendar'" class="calendar-view">
        <div class="calendar-header">
          <el-button icon="ArrowLeft" @click="changeMonth(-1)" />
          <span class="current-month">{{ currentYear }}年{{ currentMonth }}月</span>
          <el-button icon="ArrowRight" @click="changeMonth(1)" />
          <el-button type="primary" link @click="goToToday">今天</el-button>
        </div>
        
        <div class="calendar-grid">
          <div class="calendar-weekdays">
            <div v-for="day in weekdays" :key="day" class="weekday">{{ day }}</div>
          </div>
          <div class="calendar-days" v-loading="calendarLoading">
            <div 
              v-for="(day, index) in calendarDays" 
              :key="index" 
              class="calendar-day"
              :class="{ 
                'other-month': !day.isCurrentMonth, 
                'today': day.isToday,
                'has-schedule': day.schedules && day.schedules.length > 0
              }"
              @click="handleDayClick(day)"
            >
              <div class="day-number">{{ day.day }}</div>
              <div class="day-schedules">
                <div 
                  v-for="(schedule, idx) in day.schedules.slice(0, 3)" 
                  :key="idx" 
                  class="schedule-item"
                  :class="getShiftClass(schedule.shiftType)"
                >
                  <span class="schedule-name">{{ schedule.nannyName }}</span>
                  <span class="schedule-shift">{{ schedule.shiftTypeName }}</span>
                </div>
                <div v-if="day.schedules && day.schedules.length > 3" class="more-schedules">
                  +{{ day.schedules.length - 3 }}更多
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 排班列表 -->
      <div v-show="activeTab === 'list'" class="list-view">
        <el-form :inline="true" :model="queryParams" class="search-form">
          <el-form-item label="保姆">
            <el-select v-model="queryParams.nannyId" placeholder="全部" clearable style="width: 150px">
              <el-option v-for="n in nannyOptions" :key="n.id" :label="n.realName" :value="n.id" />
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
              style="width: 240px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
            <el-button icon="Refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="listLoading" :data="scheduleList" border style="width: 100%">
          <el-table-column prop="scheduleDate" label="日期" width="120" />
          <el-table-column prop="nannyName" label="保姆姓名" width="100" />
          <el-table-column prop="nannyPhone" label="联系电话" width="120" />
          <el-table-column prop="shiftTypeName" label="班次" width="80">
            <template #default="{ row }">
              <el-tag :type="getShiftTagType(row.shiftType)">{{ row.shiftTypeName }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="时间" width="130">
            <template #default="{ row }">
              {{ row.startTime }} - {{ row.endTime }}
            </template>
          </el-table-column>
          <el-table-column prop="statusName" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.status)">{{ row.statusName }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" show-overflow-tooltip min-width="120" />
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="listTotal"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="getScheduleListData"
          @current-change="getScheduleListData"
          style="margin-top: 20px; justify-content: flex-end"
        />
      </div>

      <!-- 调班申请 -->
      <div v-show="activeTab === 'swap'" class="swap-view">
        <el-table v-loading="swapLoading" :data="swapList" border style="width: 100%">
          <el-table-column prop="scheduleDate" label="排班日期" width="120" />
          <el-table-column prop="nannyName" label="申请人" width="100" />
          <el-table-column prop="shiftTypeName" label="原班次" width="80">
            <template #default="{ row }">
              <el-tag :type="getShiftTagType(row.shiftType)">{{ row.shiftTypeName }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="swapTargetNannyName" label="调班对象" width="100" />
          <el-table-column prop="swapReason" label="调班原因" show-overflow-tooltip min-width="150" />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button type="success" size="small" @click="handleApproveSwap(row, true)">批准</el-button>
              <el-button type="danger" size="small" @click="handleApproveSwap(row, false)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="swapParams.pageNum"
          v-model:page-size="swapParams.pageSize"
          :total="swapTotal"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="getSwapListData"
          @current-change="getSwapListData"
          style="margin-top: 20px; justify-content: flex-end"
        />
      </div>
    </el-card>

    <!-- 新增/编辑排班对话框 -->
    <el-dialog
      v-model="formDialogVisible"
      :title="isEdit ? '编辑排班' : '新增排班'"
      width="500px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="保姆" prop="nannyId">
          <el-select v-model="formData.nannyId" placeholder="请选择保姆" style="width: 100%" @change="checkConflict">
            <el-option v-for="n in nannyOptions" :key="n.id" :label="n.realName" :value="n.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排班日期" prop="scheduleDate">
          <el-date-picker
            v-model="formData.scheduleDate"
            type="date"
            placeholder="请选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
            @change="checkConflict"
          />
        </el-form-item>
        <el-form-item label="班次" prop="shiftType">
          <el-radio-group v-model="formData.shiftType" @change="handleShiftChange">
            <el-radio :value="1">早班</el-radio>
            <el-radio :value="2">中班</el-radio>
            <el-radio :value="3">晚班</el-radio>
            <el-radio :value="4">全天</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-time-picker v-model="formData.startTime" placeholder="开始时间" format="HH:mm" value-format="HH:mm:ss" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-time-picker v-model="formData.endTime" placeholder="结束时间" format="HH:mm" value-format="HH:mm:ss" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div v-if="conflictWarning" class="conflict-warning">
        <el-alert type="warning" :title="conflictWarning" show-icon :closable="false" />
      </div>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量排班对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      title="批量排班"
      width="600px"
      @close="resetBatchForm"
    >
      <el-form ref="batchFormRef" :model="batchForm" :rules="batchRules" label-width="100px">
        <el-form-item label="选择保姆" prop="nannyIds">
          <el-select v-model="batchForm.nannyIds" multiple placeholder="请选择保姆" style="width: 100%">
            <el-option v-for="n in nannyOptions" :key="n.id" :label="n.realName" :value="n.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker
            v-model="batchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="班次" prop="shiftType">
          <el-radio-group v-model="batchForm.shiftType">
            <el-radio :value="1">早班</el-radio>
            <el-radio :value="2">中班</el-radio>
            <el-radio :value="3">晚班</el-radio>
            <el-radio :value="4">全天</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排除周末">
          <el-switch v-model="batchForm.excludeWeekend" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="batchForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBatch" :loading="batchLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 日期详情对话框 -->
    <el-dialog
      v-model="dayDetailVisible"
      :title="selectedDay ? `${selectedDay.fullDate} 排班详情` : '排班详情'"
      width="600px"
    >
      <div v-if="selectedDay && selectedDay.schedules.length > 0">
        <el-table :data="selectedDay.schedules" border>
          <el-table-column prop="nannyName" label="保姆" width="100" />
          <el-table-column prop="shiftTypeName" label="班次" width="80">
            <template #default="{ row }">
              <el-tag :type="getShiftTagType(row.shiftType)">{{ row.shiftTypeName }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="时间" width="130">
            <template #default="{ row }">
              {{ row.startTime }} - {{ row.endTime }}
            </template>
          </el-table-column>
          <el-table-column prop="statusName" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.status)">{{ row.statusName }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handleEdit(row); dayDetailVisible = false">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-empty v-else description="当日暂无排班" />
      <template #footer>
        <el-button @click="dayDetailVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleAddForDay">新增排班</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getNannyList,
  createSchedule,
  batchCreateSchedule,
  getScheduleList,
  updateSchedule,
  deleteSchedule,
  getCalendarView,
  approveSwap,
  getSwapRequests,
  checkScheduleConflicts
} from '@/api/institution'

// 基础数据
const weekdays = ['日', '一', '二', '三', '四', '五', '六']
const activeTab = ref('calendar')
const nannyOptions = ref([])

// 统计数据
const stats = reactive({
  todaySchedules: 0,
  workingNannies: 0,
  pendingSwaps: 0,
  monthSchedules: 0
})

// 日历相关
const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth() + 1)
const calendarData = ref({})
const calendarLoading = ref(false)
const dayDetailVisible = ref(false)
const selectedDay = ref(null)

// 列表相关
const listLoading = ref(false)
const scheduleList = ref([])
const listTotal = ref(0)
const dateRange = ref([])
const queryParams = reactive({
  nannyId: null,
  startDate: null,
  endDate: null,
  pageNum: 1,
  pageSize: 10
})

// 调班申请相关
const swapLoading = ref(false)
const swapList = ref([])
const swapTotal = ref(0)
const swapParams = reactive({
  pageNum: 1,
  pageSize: 10
})

// 表单相关
const formDialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const conflictWarning = ref('')
const formData = reactive({
  id: null,
  nannyId: null,
  scheduleDate: '',
  shiftType: 1,
  startTime: '08:00:00',
  endTime: '12:00:00',
  remark: ''
})
const formRules = {
  nannyId: [{ required: true, message: '请选择保姆', trigger: 'change' }],
  scheduleDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  shiftType: [{ required: true, message: '请选择班次', trigger: 'change' }]
}

// 批量排班相关
const batchDialogVisible = ref(false)
const batchLoading = ref(false)
const batchFormRef = ref(null)
const batchForm = reactive({
  nannyIds: [],
  dateRange: [],
  shiftType: 4,
  excludeWeekend: false,
  remark: ''
})
const batchRules = {
  nannyIds: [{ required: true, message: '请选择保姆', trigger: 'change', type: 'array', min: 1 }],
  dateRange: [{ required: true, message: '请选择日期范围', trigger: 'change', type: 'array' }],
  shiftType: [{ required: true, message: '请选择班次', trigger: 'change' }]
}

// 班次默认时间
const shiftTimes = {
  1: { start: '08:00:00', end: '12:00:00' },
  2: { start: '12:00:00', end: '18:00:00' },
  3: { start: '18:00:00', end: '22:00:00' },
  4: { start: '08:00:00', end: '22:00:00' }
}

// 计算日历天数
const calendarDays = computed(() => {
  const days = []
  const firstDay = new Date(currentYear.value, currentMonth.value - 1, 1)
  const lastDay = new Date(currentYear.value, currentMonth.value, 0)
  const startDayOfWeek = firstDay.getDay()
  const daysInMonth = lastDay.getDate()
  
  // 上个月的天数
  const prevLastDay = new Date(currentYear.value, currentMonth.value - 1, 0).getDate()
  for (let i = startDayOfWeek - 1; i >= 0; i--) {
    const day = prevLastDay - i
    const date = new Date(currentYear.value, currentMonth.value - 2, day)
    const dateStr = formatDate(date)
    days.push({
      day,
      fullDate: dateStr,
      isCurrentMonth: false,
      isToday: false,
      schedules: calendarData.value[dateStr] || []
    })
  }
  
  // 当月天数
  const today = new Date()
  const todayStr = formatDate(today)
  for (let i = 1; i <= daysInMonth; i++) {
    const date = new Date(currentYear.value, currentMonth.value - 1, i)
    const dateStr = formatDate(date)
    days.push({
      day: i,
      fullDate: dateStr,
      isCurrentMonth: true,
      isToday: dateStr === todayStr,
      schedules: calendarData.value[dateStr] || []
    })
  }
  
  // 下个月的天数（补齐6行）
  const remainingDays = 42 - days.length
  for (let i = 1; i <= remainingDays; i++) {
    const date = new Date(currentYear.value, currentMonth.value, i)
    const dateStr = formatDate(date)
    days.push({
      day: i,
      fullDate: dateStr,
      isCurrentMonth: false,
      isToday: false,
      schedules: calendarData.value[dateStr] || []
    })
  }
  
  return days
})

// 工具函数
const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const getShiftClass = (type) => {
  const classes = { 1: 'morning', 2: 'afternoon', 3: 'evening', 4: 'fullday' }
  return classes[type] || ''
}

const getShiftTagType = (type) => {
  const types = { 1: 'success', 2: 'primary', 3: 'warning', 4: 'danger' }
  return types[type] || 'info'
}

const getStatusTagType = (status) => {
  const types = { 0: 'info', 1: 'success', 2: 'warning', 3: 'primary' }
  return types[status] || 'info'
}

// 获取保姆列表
const getNannyOptions = async () => {
  try {
    const res = await getNannyList({ status: 1, pageNum: 1, pageSize: 100 })
    nannyOptions.value = res.data.records || []
  } catch (error) {
    console.error('获取保姆列表失败:', error)
  }
}

// 获取日历数据
const getCalendarData = async () => {
  calendarLoading.value = true
  try {
    const res = await getCalendarView(currentYear.value, currentMonth.value)
    calendarData.value = res.data || {}
    updateStats()
  } catch (error) {
    console.error('获取日历数据失败:', error)
  } finally {
    calendarLoading.value = false
  }
}

// 获取排班列表
const getScheduleListData = async () => {
  listLoading.value = true
  try {
    const params = { ...queryParams }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getScheduleList(params)
    scheduleList.value = res.data.records || []
    listTotal.value = res.data.total || 0
  } catch (error) {
    console.error('获取排班列表失败:', error)
  } finally {
    listLoading.value = false
  }
}

// 获取调班申请列表
const getSwapListData = async () => {
  swapLoading.value = true
  try {
    const res = await getSwapRequests(swapParams)
    swapList.value = res.data.records || []
    swapTotal.value = res.data.total || 0
    stats.pendingSwaps = swapTotal.value
  } catch (error) {
    console.error('获取调班申请失败:', error)
  } finally {
    swapLoading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  const today = formatDate(new Date())
  const todaySchedules = calendarData.value[today] || []
  stats.todaySchedules = todaySchedules.length
  stats.workingNannies = new Set(todaySchedules.map(s => s.nannyId)).size
  
  let monthTotal = 0
  Object.values(calendarData.value).forEach(schedules => {
    monthTotal += schedules.length
  })
  stats.monthSchedules = monthTotal
}

// 切换月份
const changeMonth = (delta) => {
  let newMonth = currentMonth.value + delta
  let newYear = currentYear.value
  
  if (newMonth < 1) {
    newMonth = 12
    newYear--
  } else if (newMonth > 12) {
    newMonth = 1
    newYear++
  }
  
  currentYear.value = newYear
  currentMonth.value = newMonth
  getCalendarData()
}

const goToToday = () => {
  const today = new Date()
  currentYear.value = today.getFullYear()
  currentMonth.value = today.getMonth() + 1
  getCalendarData()
}

// 点击日期
const handleDayClick = (day) => {
  selectedDay.value = day
  dayDetailVisible.value = true
}

// Tab切换
const handleTabChange = (tab) => {
  if (tab === 'list') {
    getScheduleListData()
  } else if (tab === 'swap') {
    getSwapListData()
  }
}

// 查询与重置
const handleQuery = () => {
  queryParams.pageNum = 1
  getScheduleListData()
}

const handleReset = () => {
  queryParams.nannyId = null
  dateRange.value = []
  queryParams.pageNum = 1
  getScheduleListData()
}

// 班次变化时更新时间
const handleShiftChange = (val) => {
  const times = shiftTimes[val]
  if (times) {
    formData.startTime = times.start
    formData.endTime = times.end
  }
  checkConflict()
}

// 检测冲突
const checkConflict = async () => {
  conflictWarning.value = ''
  if (!formData.nannyId || !formData.scheduleDate || !formData.shiftType) return
  
  try {
    const res = await checkScheduleConflicts({
      nannyId: formData.nannyId,
      scheduleDate: formData.scheduleDate,
      shiftType: formData.shiftType,
      excludeId: formData.id
    })
    if (res.data && res.data.length > 0) {
      conflictWarning.value = `该保姆在此时间段已有排班（${res.data[0].shiftTypeName}），请确认是否继续`
    }
  } catch (error) {
    console.error('检测冲突失败:', error)
  }
}

// 新增排班
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  formDialogVisible.value = true
}

const handleAddForDay = () => {
  dayDetailVisible.value = false
  isEdit.value = false
  resetForm()
  if (selectedDay.value) {
    formData.scheduleDate = selectedDay.value.fullDate
  }
  formDialogVisible.value = true
}

// 编辑排班
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, {
    id: row.id,
    nannyId: row.nannyId,
    scheduleDate: row.scheduleDate,
    shiftType: row.shiftType,
    startTime: row.startTime,
    endTime: row.endTime,
    remark: row.remark || ''
  })
  formDialogVisible.value = true
}

// 删除排班
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该排班吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteSchedule(row.id)
      ElMessage.success('删除成功')
      getScheduleListData()
      getCalendarData()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      const data = {
        nannyId: formData.nannyId,
        scheduleDate: formData.scheduleDate,
        shiftType: formData.shiftType,
        startTime: formData.startTime,
        endTime: formData.endTime,
        remark: formData.remark
      }
      
      if (isEdit.value) {
        await updateSchedule(formData.id, data)
        ElMessage.success('修改成功')
      } else {
        await createSchedule(data)
        ElMessage.success('创建成功')
      }
      
      formDialogVisible.value = false
      getCalendarData()
      if (activeTab.value === 'list') {
        getScheduleListData()
      }
    } catch (error) {
      console.error('提交失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  conflictWarning.value = ''
  Object.assign(formData, {
    id: null,
    nannyId: null,
    scheduleDate: '',
    shiftType: 1,
    startTime: '08:00:00',
    endTime: '12:00:00',
    remark: ''
  })
  formRef.value?.resetFields()
}

// 批量排班提交
const submitBatch = async () => {
  if (!batchFormRef.value) return
  
  await batchFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    batchLoading.value = true
    try {
      const { nannyIds, dateRange: range, shiftType, excludeWeekend, remark } = batchForm
      const schedules = []
      
      const startDate = new Date(range[0])
      const endDate = new Date(range[1])
      
      for (let date = new Date(startDate); date <= endDate; date.setDate(date.getDate() + 1)) {
        // 排除周末
        if (excludeWeekend && (date.getDay() === 0 || date.getDay() === 6)) {
          continue
        }
        
        const dateStr = formatDate(date)
        const times = shiftTimes[shiftType]
        
        for (const nannyId of nannyIds) {
          schedules.push({
            nannyId,
            scheduleDate: dateStr,
            shiftType,
            startTime: times.start,
            endTime: times.end,
            remark
          })
        }
      }
      
      if (schedules.length === 0) {
        ElMessage.warning('没有可创建的排班')
        batchLoading.value = false
        return
      }
      
      const res = await batchCreateSchedule(schedules)
      ElMessage.success(res.msg || `成功创建${res.data}条排班`)
      
      batchDialogVisible.value = false
      getCalendarData()
      if (activeTab.value === 'list') {
        getScheduleListData()
      }
    } catch (error) {
      console.error('批量创建失败:', error)
    } finally {
      batchLoading.value = false
    }
  })
}

const resetBatchForm = () => {
  Object.assign(batchForm, {
    nannyIds: [],
    dateRange: [],
    shiftType: 4,
    excludeWeekend: false,
    remark: ''
  })
  batchFormRef.value?.resetFields()
}

// 审核调班
const handleApproveSwap = (row, approved) => {
  const action = approved ? '批准' : '拒绝'
  ElMessageBox.confirm(`确定要${action}该调班申请吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await approveSwap(row.id, approved)
      ElMessage.success(`${action}成功`)
      getSwapListData()
      getCalendarData()
    } catch (error) {
      console.error('审核失败:', error)
    }
  }).catch(() => {})
}

// 初始化
onMounted(() => {
  getNannyOptions()
  getCalendarData()
  getSwapListData()
})
</script>

<style scoped>
.schedule-manage {
  padding: 20px;
}

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border: 1px solid #E5E7EB;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s;
}

.stat-card:hover {
  border-color: #D1D5DB;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon svg {
  width: 24px;
  height: 24px;
}

.stat-icon.blue {
  background: #EFF6FF;
  color: #3B82F6;
}

.stat-icon.emerald {
  background: #ECFDF5;
  color: #10B981;
}

.stat-icon.amber {
  background: #FFFBEB;
  color: #F59E0B;
}

.stat-icon.purple {
  background: #F5F3FF;
  color: #8B5CF6;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #1F2937;
  line-height: 1;
}

.stat-label {
  font-size: 13px;
  color: #6B7280;
  margin-top: 4px;
}

/* 主卡片 */
.main-card {
  background: #fff;
}

.main-card :deep(.el-card__header) {
  padding: 0 20px;
  border-bottom: 1px solid #E5E7EB;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-header :deep(.el-tabs__header) {
  margin: 0;
}

.card-header :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.swap-badge {
  margin-left: 4px;
}

/* 日历视图 */
.calendar-view {
  padding: 20px;
}

.calendar-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
}

.current-month {
  font-size: 18px;
  font-weight: 600;
  color: #1F2937;
  min-width: 120px;
  text-align: center;
}

.calendar-grid {
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  overflow: hidden;
}

.calendar-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: #F9FAFB;
  border-bottom: 1px solid #E5E7EB;
}

.weekday {
  padding: 12px;
  text-align: center;
  font-weight: 500;
  color: #6B7280;
}

.calendar-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
}

.calendar-day {
  min-height: 100px;
  border-right: 1px solid #E5E7EB;
  border-bottom: 1px solid #E5E7EB;
  padding: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.calendar-day:nth-child(7n) {
  border-right: none;
}

.calendar-day:hover {
  background: #F9FAFB;
}

.calendar-day.other-month {
  background: #F9FAFB;
}

.calendar-day.other-month .day-number {
  color: #9CA3AF;
}

.calendar-day.today {
  background: #EFF6FF;
}

.calendar-day.today .day-number {
  background: #3B82F6;
  color: #fff;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.day-number {
  font-size: 14px;
  font-weight: 500;
  color: #1F2937;
  margin-bottom: 4px;
}

.day-schedules {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.schedule-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
}

.schedule-item.morning {
  background: #DCFCE7;
  color: #166534;
}

.schedule-item.afternoon {
  background: #DBEAFE;
  color: #1E40AF;
}

.schedule-item.evening {
  background: #FEF3C7;
  color: #92400E;
}

.schedule-item.fullday {
  background: #FEE2E2;
  color: #991B1B;
}

.schedule-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.schedule-shift {
  flex-shrink: 0;
  margin-left: 4px;
}

.more-schedules {
  font-size: 11px;
  color: #6B7280;
  text-align: center;
}

/* 列表视图 */
.list-view {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

/* 调班视图 */
.swap-view {
  padding: 20px;
}

/* 冲突警告 */
.conflict-warning {
  margin-top: 10px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: 1fr;
  }
  
  .calendar-day {
    min-height: 80px;
  }
  
  .schedule-item {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
