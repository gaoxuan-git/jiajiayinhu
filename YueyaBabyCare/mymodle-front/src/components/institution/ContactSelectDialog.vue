<template>
  <el-dialog
    v-model="dialogVisible"
    title="选择联系人"
    width="700px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索家长姓名或手机号"
        clearable
        style="width: 300px"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 家长列表 -->
    <div v-loading="loading" class="contact-list">
      <el-empty v-if="parentList.length === 0" description="暂无有业务关系的家长" />
      <div
        v-for="item in parentList"
        :key="item.contactId"
        class="contact-item"
        :class="{ selected: isSelected(item) }"
        @click="handleSelect(item)"
      >
        <el-avatar :size="48" :src="item.contactAvatar">
          {{ item.contactName?.charAt(0) }}
        </el-avatar>
        <div class="contact-info">
          <div class="contact-name">
            <span>{{ item.contactName }}</span>
            <el-tag size="small" type="success">家长</el-tag>
          </div>
          <div class="contact-phone">{{ item.contactPhone || '暂无手机号' }}</div>
          <div v-if="item.relationDesc" class="contact-relation">{{ item.relationDesc }}</div>
        </div>
        <el-icon v-if="isSelected(item)" class="selected-icon"><Check /></el-icon>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pageInfo.pageNum"
        v-model:page-size="pageInfo.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="pageInfo.total"
        layout="total, sizes, prev, pager, next"
        @size-change="loadParentList"
        @current-change="loadParentList"
      />
    </div>

    <template #footer>
      <div class="dialog-footer">
        <div class="selected-info" v-if="selectedContact">
          已选择: <el-tag>{{ selectedContact.contactName }}</el-tag>
        </div>
        <div class="footer-buttons">
          <el-button @click="handleClose">取消</el-button>
          <el-button type="primary" @click="handleConfirm" :disabled="!selectedContact">
            确定
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { Search, Check } from '@element-plus/icons-vue'
import { getInstitutionParents } from '@/api/message'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'select'])

const dialogVisible = ref(false)
const searchKeyword = ref('')
const selectedContact = ref(null)
const loading = ref(false)
const parentList = ref([])

const pageInfo = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 监听弹窗显示状态
watch(() => props.modelValue, (val) => {
  dialogVisible.value = val
  if (val) {
    loadParentList()
  }
})

watch(dialogVisible, (val) => {
  emit('update:modelValue', val)
})

// 加载家长列表
const loadParentList = async () => {
  loading.value = true
  try {
    const res = await getInstitutionParents(searchKeyword.value)
    // 前端分页处理
    const allData = res.data || []
    pageInfo.total = allData.length
    const start = (pageInfo.pageNum - 1) * pageInfo.pageSize
    const end = start + pageInfo.pageSize
    parentList.value = allData.slice(start, end).map(item => ({
      ...item,
      contactType: 1
    }))
  } catch (error) {
    console.error('获取家长列表失败:', error)
    parentList.value = []
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  pageInfo.pageNum = 1
  loadParentList()
}

// 重置搜索
const handleReset = () => {
  searchKeyword.value = ''
  pageInfo.pageNum = 1
  loadParentList()
}

// 判断是否选中
const isSelected = (item) => {
  return selectedContact.value &&
    selectedContact.value.contactType === item.contactType &&
    selectedContact.value.contactId === item.contactId
}

// 选择联系人
const handleSelect = (item) => {
  if (isSelected(item)) {
    selectedContact.value = null
  } else {
    selectedContact.value = item
  }
}

// 确认选择
const handleConfirm = () => {
  if (selectedContact.value) {
    emit('select', selectedContact.value)
    handleClose()
  }
}

// 关闭弹窗
const handleClose = () => {
  dialogVisible.value = false
  selectedContact.value = null
  searchKeyword.value = ''
}
</script>

<style scoped lang="scss">
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.contact-list {
  min-height: 300px;
  max-height: 400px;
  overflow-y: auto;
  padding: 10px 0;

  .contact-item {
    display: flex;
    align-items: center;
    padding: 12px 15px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;
    margin-bottom: 8px;
    border: 1px solid #ebeef5;

    &:hover {
      background-color: #f5f7fa;
    }

    &.selected {
      background-color: #ecf5ff;
      border-color: #409eff;
    }

    .contact-info {
      flex: 1;
      margin-left: 12px;
      overflow: hidden;

      .contact-name {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: 500;
        margin-bottom: 4px;
      }

      .contact-phone {
        font-size: 13px;
        color: #606266;
        margin-bottom: 2px;
      }

      .contact-relation {
        font-size: 12px;
        color: #909399;
      }
    }

    .selected-icon {
      color: #409eff;
      font-size: 20px;
    }
  }
}

.pagination-container {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .selected-info {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #606266;
  }

  .footer-buttons {
    display: flex;
    gap: 10px;
  }
}
</style>