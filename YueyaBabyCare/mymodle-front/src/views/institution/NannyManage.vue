<template>
  <div class="nanny-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">员工管理</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="审核状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="待审核" :value="0" />
            <el-option label="已审核" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="nannyList"
        border
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="gender" label="性别" width="70">
          <template #default="{ row }">
            <el-tag v-if="row.gender === 1" type="info">男</el-tag>
            <el-tag v-else-if="row.gender === 2" type="success">女</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="workYears" label="工作年限" width="90">
          <template #default="{ row }">
            {{ row.workYears || 0 }}年
          </template>
        </el-table-column>
        <el-table-column prop="introduction" label="个人简介" show-overflow-tooltip min-width="150" />
        <el-table-column prop="status" label="审核状态" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已审核</el-tag>
            <el-tag v-else-if="row.status === 2" type="danger">已拒绝</el-tag>
            <el-tag v-else type="info">未知</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="170" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="success"
              size="small"
              @click="handleAudit(row.id, 1)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="danger"
              size="small"
              @click="handleAudit(row.id, 2)"
            >
              拒绝
            </el-button>
            <el-button
              type="primary"
              size="small"
              @click="handleView(row)"
            >
              详情
            </el-button>
            <el-button
              type="warning"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="info"
              size="small"
              @click="handlePerformance(row)"
            >
              绩效
            </el-button>
            <el-button
              type="danger"
              size="small"
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
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="保姆详情"
      width="600px"
    >
      <el-descriptions v-if="currentNanny" :column="2" border>
        <el-descriptions-item label="姓名">{{ currentNanny.realName }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          <el-tag v-if="currentNanny.gender === 1" type="info">男</el-tag>
          <el-tag v-else-if="currentNanny.gender === 2" type="success">女</el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentNanny.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentNanny.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="身份证号" :span="2">{{ currentNanny.idCard || '-' }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ currentNanny.birthDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工作年限">{{ currentNanny.workYears || 0 }}年</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag v-if="currentNanny.status === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="currentNanny.status === 1" type="success">已审核</el-tag>
          <el-tag v-else-if="currentNanny.status === 2" type="danger">已拒绝</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ currentNanny.createTime }}</el-descriptions-item>
        <el-descriptions-item label="个人简介" :span="2">{{ currentNanny.introduction || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资格证书" :span="2">
          <div v-if="certificates.length > 0" class="certificate-list">
            <el-image
              v-for="(cert, index) in certificates"
              :key="index"
              :src="cert"
              :preview-src-list="certificates"
              :initial-index="index"
              style="width: 80px; height: 80px; margin-right: 10px"
              fit="cover"
            />
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="editVisible"
      title="编辑保姆信息"
      width="600px"
    >
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="editForm.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="editForm.gender">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="工作年限" prop="workYears">
          <el-input-number v-model="editForm.workYears" :min="0" :max="50" />
        </el-form-item>
        <el-form-item label="个人简介" prop="introduction">
          <el-input v-model="editForm.introduction" type="textarea" :rows="3" placeholder="请输入个人简介" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="editLoading">保存</el-button>
      </template>
    </el-dialog>

    <!-- 绩效对话框 -->
    <el-dialog
      v-model="performanceVisible"
      title="员工绩效"
      width="720px"
      class="performance-dialog"
    >
      <div v-loading="performanceLoading">
        <div v-if="performanceData" class="performance-content">
          <!-- 基本信息 -->
          <div class="performance-header">
            <div class="avatar-wrapper">
              <div class="avatar-icon">
                <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                </svg>
              </div>
            </div>
            <h3 class="name">{{ performanceData.nannyName }}</h3>
            <p class="subtitle">员工绩效数据统计</p>
          </div>
          
          <!-- 统计卡片 -->
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon blue">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/>
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ performanceData.serviceFamilyCount }}</div>
                <div class="stat-label">服务家庭数</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon emerald">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ performanceData.completedPlanCount }}</div>
                <div class="stat-label">已完成计划</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon amber">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ performanceData.ongoingPlanCount }}</div>
                <div class="stat-label">进行中计划</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon purple">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ performanceData.totalEvaluations }}</div>
                <div class="stat-label">总评价数</div>
              </div>
            </div>
          </div>
          
          <!-- 评价统计 -->
          <div class="section-title">
            <span class="title-text">评价统计</span>
          </div>
          <div class="rating-section">
            <div class="rating-summary">
              <div class="rating-main-card">
                <div class="rating-score">{{ performanceData.averageScore }}</div>
                <div class="rating-stars">
                  <el-rate :model-value="Number(performanceData.averageScore)" disabled />
                </div>
                <div class="rating-label">平均评分</div>
              </div>
              <div class="rating-main-card positive">
                <div class="rating-score">{{ performanceData.positiveRate }}%</div>
                <div class="rating-badge">
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 10h4.764a2 2 0 011.789 2.894l-3.5 7A2 2 0 0115.263 21h-4.017c-.163 0-.326-.02-.485-.06L7 20m7-10V5a2 2 0 00-2-2h-.095c-.5 0-.905.405-.905.905 0 .714-.211 1.412-.608 2.006L7 11v9m7-10h-2M7 20H5a2 2 0 01-2-2v-6a2 2 0 012-2h2.5"/>
                  </svg>
                </div>
                <div class="rating-label">好评率</div>
              </div>
            </div>
            <div class="rating-distribution">
              <div class="dist-item">
                <span class="star-label">5星</span>
                <div class="progress-bar">
                  <div class="progress-fill excellent" :style="{ width: getStarPercentage(5) + '%' }"></div>
                </div>
                <span class="star-count">{{ performanceData.fiveStarCount }}</span>
              </div>
              <div class="dist-item">
                <span class="star-label">4星</span>
                <div class="progress-bar">
                  <div class="progress-fill good" :style="{ width: getStarPercentage(4) + '%' }"></div>
                </div>
                <span class="star-count">{{ performanceData.fourStarCount }}</span>
              </div>
              <div class="dist-item">
                <span class="star-label">3星</span>
                <div class="progress-bar">
                  <div class="progress-fill medium" :style="{ width: getStarPercentage(3) + '%' }"></div>
                </div>
                <span class="star-count">{{ performanceData.threeStarCount }}</span>
              </div>
              <div class="dist-item">
                <span class="star-label">2星</span>
                <div class="progress-bar">
                  <div class="progress-fill low" :style="{ width: getStarPercentage(2) + '%' }"></div>
                </div>
                <span class="star-count">{{ performanceData.twoStarCount }}</span>
              </div>
              <div class="dist-item">
                <span class="star-label">1星</span>
                <div class="progress-bar">
                  <div class="progress-fill poor" :style="{ width: getStarPercentage(1) + '%' }"></div>
                </div>
                <span class="star-count">{{ performanceData.oneStarCount }}</span>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无绩效数据" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNannyList, auditNanny, updateNannyInfo, deleteNanny, getNannyPerformance } from '@/api/institution'

const loading = ref(false)
const nannyList = ref([])
const total = ref(0)
const detailVisible = ref(false)
const currentNanny = ref(null)

// 编辑相关
const editVisible = ref(false)
const editLoading = ref(false)
const editFormRef = ref(null)
const editForm = reactive({
  id: null,
  realName: '',
  gender: null,
  phone: '',
  email: '',
  workYears: 0,
  introduction: ''
})
const editRules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

// 绩效相关
const performanceVisible = ref(false)
const performanceLoading = ref(false)
const performanceData = ref(null)

const queryParams = reactive({
  status: null,
  pageNum: 1,
  pageSize: 10
})

// 解析证书列表
const certificates = computed(() => {
  if (!currentNanny.value || !currentNanny.value.certificate) {
    return []
  }
  try {
    const parsed = JSON.parse(currentNanny.value.certificate)
    // 拼接完整URL
    return Array.isArray(parsed) ? parsed.map(cert => {
      if (cert && cert.startsWith('/')) {
        return process.env.VUE_APP_API_BASE_URL + cert
      }
      return cert
    }) : []
  } catch (e) {
    return []
  }
})

// 获取保姆列表
const getList = () => {
  loading.value = true
  getNannyList(queryParams)
    .then(response => {
      nannyList.value = response.data.records
      total.value = response.data.total
    })
    .catch(error => {
      console.error('获取列表失败:', error)
    })
    .finally(() => {
      loading.value = false
    })
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
  queryParams.pageSize = 10
  getList()
}

// 审核
const handleAudit = (nannyId, status) => {
  const statusText = status === 1 ? '通过' : '拒绝'
  ElMessageBox.confirm(
    `确定要${statusText}该保姆的审核吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    auditNanny(nannyId, status)
      .then(() => {
        ElMessage.success('审核成功')
        getList()
      })
      .catch(error => {
        console.error('审核失败:', error)
      })
  }).catch(() => {
    // 取消操作
  })
}

// 查看详情
const handleView = (row) => {
  currentNanny.value = row
  detailVisible.value = true
}

// 编辑保姆
const handleEdit = (row) => {
  editForm.id = row.id
  editForm.realName = row.realName
  editForm.gender = row.gender
  editForm.phone = row.phone
  editForm.email = row.email || ''
  editForm.workYears = row.workYears || 0
  editForm.introduction = row.introduction || ''
  editVisible.value = true
}

// 提交编辑
const submitEdit = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      editLoading.value = true
      try {
        await updateNannyInfo(editForm.id, {
          realName: editForm.realName,
          gender: editForm.gender,
          phone: editForm.phone,
          email: editForm.email,
          workYears: editForm.workYears,
          introduction: editForm.introduction
        })
        ElMessage.success('修改成功')
        editVisible.value = false
        getList()
      } catch (error) {
        console.error('修改失败:', error)
      } finally {
        editLoading.value = false
      }
    }
  })
}

// 删除保姆
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除保姆「${row.realName}」吗？删除后无法恢复。`,
    '警告',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteNanny(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {
    // 取消操作
  })
}

// 查看绩效
const handlePerformance = async (row) => {
  performanceVisible.value = true
  performanceLoading.value = true
  performanceData.value = null
  
  try {
    const response = await getNannyPerformance(row.id)
    performanceData.value = response.data
  } catch (error) {
    console.error('获取绩效失败:', error)
    ElMessage.error('获取绩效数据失败')
  } finally {
    performanceLoading.value = false
  }
}

// 计算星级百分比
const getStarPercentage = (star) => {
  if (!performanceData.value || performanceData.value.totalEvaluations === 0) return 0
  const count = {
    5: performanceData.value.fiveStarCount,
    4: performanceData.value.fourStarCount,
    3: performanceData.value.threeStarCount,
    2: performanceData.value.twoStarCount,
    1: performanceData.value.oneStarCount
  }[star] || 0
  return Math.round((count / performanceData.value.totalEvaluations) * 100)
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.nanny-manage {
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

  .certificate-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  // 绩效样式 - 参考UI设计规范优化
  .performance-content {
    .performance-header {
      text-align: center;
      padding: 24px 0;
      background: linear-gradient(180deg, #F9FAFB 0%, #FFFFFF 100%);
      border-radius: 12px;
      margin-bottom: 24px;
      
      .avatar-wrapper {
        margin-bottom: 12px;
        
        .avatar-icon {
          width: 64px;
          height: 64px;
          margin: 0 auto;
          background: #EFF6FF;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          
          svg {
            width: 32px;
            height: 32px;
            color: #3B82F6;
          }
        }
      }
      
      .name {
        margin: 0 0 4px;
        font-size: 20px;
        font-weight: 600;
        color: #1F2937;
      }
      
      .subtitle {
        margin: 0;
        font-size: 14px;
        color: #6B7280;
      }
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
          width: 40px;
          height: 40px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
          
          svg {
            width: 20px;
            height: 20px;
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
            font-size: 24px;
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
    
    .section-title {
      margin-bottom: 16px;
      
      .title-text {
        font-size: 16px;
        font-weight: 600;
        color: #374151;
        position: relative;
        padding-left: 12px;
        
        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 50%;
          transform: translateY(-50%);
          width: 4px;
          height: 16px;
          background: #3B82F6;
          border-radius: 2px;
        }
      }
    }
    
    .rating-section {
      display: flex;
      gap: 24px;
      
      .rating-summary {
        display: flex;
        gap: 16px;
        flex-shrink: 0;
        
        .rating-main-card {
          width: 140px;
          padding: 20px;
          background: #F9FAFB;
          border: 1px solid #E5E7EB;
          border-radius: 12px;
          text-align: center;
          
          .rating-score {
            font-size: 32px;
            font-weight: 700;
            color: #F59E0B;
            line-height: 1.2;
          }
          
          .rating-stars {
            margin: 8px 0;
            
            :deep(.el-rate) {
              justify-content: center;
              
              .el-rate__icon {
                font-size: 14px;
              }
            }
          }
          
          .rating-badge {
            margin: 8px 0;
            
            svg {
              width: 28px;
              height: 28px;
              color: #10B981;
            }
          }
          
          .rating-label {
            font-size: 13px;
            color: #6B7280;
          }
          
          &.positive {
            .rating-score {
              color: #10B981;
            }
          }
        }
      }
      
      .rating-distribution {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;
        gap: 10px;
        
        .dist-item {
          display: flex;
          align-items: center;
          gap: 12px;
          
          .star-label {
            width: 32px;
            font-size: 13px;
            font-weight: 500;
            color: #4B5563;
          }
          
          .progress-bar {
            flex: 1;
            height: 8px;
            background: #F3F4F6;
            border-radius: 4px;
            overflow: hidden;
            
            .progress-fill {
              height: 100%;
              border-radius: 4px;
              transition: width 0.3s ease;
              
              &.excellent {
                background: #10B981;
              }
              
              &.good {
                background: #3B82F6;
              }
              
              &.medium {
                background: #F59E0B;
              }
              
              &.low {
                background: #F97316;
              }
              
              &.poor {
                background: #EF4444;
              }
            }
          }
          
          .star-count {
            width: 28px;
            text-align: right;
            font-size: 13px;
            font-weight: 500;
            color: #6B7280;
          }
        }
      }
    }
  }
}

// 绩效弹窗样式
.performance-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
    
    .el-dialog__header {
      padding: 20px 24px;
      border-bottom: 1px solid #E5E7EB;
      
      .el-dialog__title {
        font-size: 18px;
        font-weight: 600;
        color: #1F2937;
      }
    }
    
    .el-dialog__body {
      padding: 24px;
    }
  }
}
</style>
