<template>
  <el-dialog
    v-model="dialogVisible"
    title="选择联系人"
    width="800px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索联系人姓名或手机号"
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

    <!-- Tab切换 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 家长Tab -->
      <el-tab-pane label="家长" name="parent">
        <div v-loading="parentLoading" class="contact-list">
          <el-empty v-if="parentList.length === 0" description="暂无家长数据" />
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
            v-model:current-page="parentPage.pageNum"
            v-model:page-size="parentPage.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="parentPage.total"
            layout="total, sizes, prev, pager, next"
            @size-change="loadParentList"
            @current-change="loadParentList"
          />
        </div>
      </el-tab-pane>

      <!-- 保姆Tab -->
      <el-tab-pane label="保姆" name="nanny">
        <div v-loading="nannyLoading" class="contact-list">
          <el-empty v-if="nannyList.length === 0" description="暂无保姆数据" />
          <div
            v-for="item in nannyList"
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
                <el-tag size="small" type="warning">保姆</el-tag>
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
            v-model:current-page="nannyPage.pageNum"
            v-model:page-size="nannyPage.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="nannyPage.total"
            layout="total, sizes, prev, pager, next"
            @size-change="loadNannyList"
            @current-change="loadNannyList"
          />
        </div>
      </el-tab-pane>

      <!-- 机构Tab -->
      <el-tab-pane label="机构" name="institution">
        <div v-loading="institutionLoading" class="contact-list">
          <el-empty v-if="institutionList.length === 0" description="暂无机构数据" />
          <div
            v-for="item in institutionList"
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
                <el-tag size="small" type="primary">机构</el-tag>
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
            v-model:current-page="institutionPage.pageNum"
            v-model:page-size="institutionPage.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="institutionPage.total"
            layout="total, sizes, prev, pager, next"
            @size-change="loadInstitutionList"
            @current-change="loadInstitutionList"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

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
import { getAdminParents, getAdminNannies, getAdminInstitutions } from '@/api/message'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'select'])

const dialogVisible = ref(false)
const activeTab = ref('parent')
const searchKeyword = ref('')
const selectedContact = ref(null)

// 家长列表数据
const parentLoading = ref(false)
const parentList = ref([])
const parentPage = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 保姆列表数据
const nannyLoading = ref(false)
const nannyList = ref([])
const nannyPage = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 机构列表数据
const institutionLoading = ref(false)
const institutionList = ref([])
const institutionPage = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 监听弹窗显示状态
watch(() => props.modelValue, (val) => {
  dialogVisible.value = val
  if (val) {
    // 打开弹窗时加载当前Tab的数据
    loadCurrentTabData()
  }
})

watch(dialogVisible, (val) => {
  emit('update:modelValue', val)
})

// 加载当前Tab的数据
const loadCurrentTabData = () => {
  switch (activeTab.value) {
    case 'parent':
      loadParentList()
      break
    case 'nanny':
      loadNannyList()
      break
    case 'institution':
      loadInstitutionList()
      break
  }
}

// 加载家长列表
const loadParentList = async () => {
  parentLoading.value = true
  try {
    const res = await getAdminParents(searchKeyword.value)
    // 前端分页处理
    const allData = res.data || []
    parentPage.total = allData.length
    const start = (parentPage.pageNum - 1) * parentPage.pageSize
    const end = start + parentPage.pageSize
    parentList.value = allData.slice(start, end).map(item => ({
      ...item,
      contactType: 1
    }))
  } catch (error) {
    console.error('获取家长列表失败:', error)
    parentList.value = []
  } finally {
    parentLoading.value = false
  }
}

// 加载保姆列表
const loadNannyList = async () => {
  nannyLoading.value = true
  try {
    const res = await getAdminNannies(searchKeyword.value)
    // 前端分页处理
    const allData = res.data || []
    nannyPage.total = allData.length
    const start = (nannyPage.pageNum - 1) * nannyPage.pageSize
    const end = start + nannyPage.pageSize
    nannyList.value = allData.slice(start, end).map(item => ({
      ...item,
      contactType: 2
    }))
  } catch (error) {
    console.error('获取保姆列表失败:', error)
    nannyList.value = []
  } finally {
    nannyLoading.value = false
  }
}

// 加载机构列表
const loadInstitutionList = async () => {
  institutionLoading.value = true
  try {
    const res = await getAdminInstitutions(searchKeyword.value)
    // 前端分页处理
    const allData = res.data || []
    institutionPage.total = allData.length
    const start = (institutionPage.pageNum - 1) * institutionPage.pageSize
    const end = start + institutionPage.pageSize
    institutionList.value = allData.slice(start, end).map(item => ({
      ...item,
      contactType: 3
    }))
  } catch (error) {
    console.error('获取机构列表失败:', error)
    institutionList.value = []
  } finally {
    institutionLoading.value = false
  }
}

// Tab切换处理
const handleTabChange = () => {
  loadCurrentTabData()
}

// 搜索处理
const handleSearch = () => {
  // 重置分页
  parentPage.pageNum = 1
  nannyPage.pageNum = 1
  institutionPage.pageNum = 1
  loadCurrentTabData()
}

// 重置搜索
const handleReset = () => {
  searchKeyword.value = ''
  parentPage.pageNum = 1
  nannyPage.pageNum = 1
  institutionPage.pageNum = 1
  loadCurrentTabData()
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