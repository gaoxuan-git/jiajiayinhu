<template>
  <el-dialog
    :model-value="visible"
    title="保姆详情"
    width="700px"
    @close="handleClose"
    @update:model-value="handleClose"
  >
    <div v-loading="loading" class="detail-content">
      <div v-if="detail" class="profile-section">
        <!-- 头像和基本信息 -->
        <div class="profile-header">
          <div class="avatar-section">
            <el-avatar
              :size="80"
              :src="detail.avatar"
              :icon="UserFilled"
              class="avatar"
            />
          </div>
          <div class="basic-info">
            <h3>{{ detail.realName || detail.username }}</h3>
            <p class="username">@{{ detail.username }}</p>
            <el-tag v-if="detail.status === 1" type="success">已审核</el-tag>
            <el-tag v-else-if="detail.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="detail.status === 2" type="danger">已拒绝</el-tag>
            <el-tag v-else type="info">未知</el-tag>
          </div>
        </div>

        <!-- 详细信息 -->
        <div class="detail-info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="保姆ID">
              {{ detail.id }}
            </el-descriptions-item>
            <el-descriptions-item label="用户名">
              {{ detail.username }}
            </el-descriptions-item>
            <el-descriptions-item label="真实姓名">
              {{ detail.realName || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="性别">
              <span v-if="detail.gender === 1">男</span>
              <span v-else-if="detail.gender === 2">女</span>
              <span v-else>未知</span>
            </el-descriptions-item>
            <el-descriptions-item label="手机号">
              {{ detail.phone || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ detail.email || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="身份证号" :span="2">
              {{ detail.idCard || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="出生日期">
              {{ detail.birthDate || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="工作年限">
              {{ detail.workYears ? detail.workYears + '年' : '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="所属机构ID">
              {{ detail.institutionId || '未分配' }}
            </el-descriptions-item>
            <el-descriptions-item label="资格证书">
              <div v-if="detail.certificate" class="certificate-info">
                {{ detail.certificate }}
              </div>
              <span v-else>未填写</span>
            </el-descriptions-item>
            <el-descriptions-item label="个人简介" :span="2">
              <div class="introduction">
                {{ detail.introduction || '未填写' }}
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">
              {{ detail.createTime }}
            </el-descriptions-item>
            <el-descriptions-item label="更新时间">
              {{ detail.updateTime }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import { getNannyById } from '@/api/admin'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  id: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['update:visible'])

const loading = ref(false)
const detail = ref(null)

// 监听显示状态变化，加载数据
watch([() => props.visible, () => props.id], async ([newVisible, newId]) => {
  if (newVisible && newId) {
    await fetchDetail(newId)
  }
}, { immediate: true })

// 获取详情数据
const fetchDetail = async (id) => {
  loading.value = true
  try {
    const res = await getNannyById(id)
    detail.value = res.data
  } catch (error) {
    console.error('获取保姆详情失败:', error)
    ElMessage.error('获取保姆详情失败')
  } finally {
    loading.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  emit('update:visible', false)
  detail.value = null
}
</script>

<style scoped lang="scss">
.detail-content {
  min-height: 300px;
}

.profile-section {
  .profile-header {
    display: flex;
    align-items: center;
    margin-bottom: 30px;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;

    .avatar-section {
      margin-right: 20px;

      .avatar {
        border: 3px solid #fff;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      }
    }

    .basic-info {
      h3 {
        margin: 0 0 8px 0;
        font-size: 20px;
        color: #303133;
      }

      .username {
        margin: 0 0 12px 0;
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .detail-info {
    :deep(.el-descriptions__label) {
      font-weight: 600;
    }

    .introduction {
      max-height: 100px;
      overflow-y: auto;
      line-height: 1.6;
    }

    .certificate-info {
      max-height: 80px;
      overflow-y: auto;
      line-height: 1.6;
    }
  }
}

.dialog-footer {
  .el-button {
    margin-left: 0;
  }
}
</style>