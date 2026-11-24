<template>
  <div class="message-center">
    <div class="chat-container">
      <!-- 左侧联系人列表 -->
      <div class="contact-panel">
        <!-- 头部搜索和新建 -->
        <div class="panel-header">
          <div class="header-top">
            <h2 class="panel-title">消息</h2>
            <el-badge :value="totalUnread" :hidden="totalUnread === 0">
              <el-button type="primary" size="small" circle icon="Plus" @click="showContactDialog = true" />
            </el-badge>
          </div>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索联系人"
            prefix-icon="Search"
            clearable
            class="search-input"
          />
        </div>

        <!-- 联系人列表 -->
        <div v-loading="contactLoading" class="contact-list">
          <div v-if="filteredContacts.length === 0" class="empty-state">
            <el-empty description="暂无联系人" :image-size="80" />
          </div>
          <div
            v-for="contact in filteredContacts"
            :key="`${contact.contactType}-${contact.contactId}`"
            class="contact-item"
            :class="{ active: isActiveContact(contact) }"
            @click="selectContact(contact)"
          >
            <div class="contact-avatar">
              <el-avatar :size="48" :src="contact.contactAvatar">
                {{ contact.contactName?.charAt(0) }}
              </el-avatar>
              <span v-if="contact.unreadCount > 0" class="unread-badge">
                {{ contact.unreadCount > 99 ? '99+' : contact.unreadCount }}
              </span>
            </div>
            <div class="contact-info">
              <div class="contact-header">
                <div class="contact-name-row">
                  <span class="contact-name">{{ contact.contactName }}</span>
                  <el-tag size="small" :type="getContactTypeTag(contact.contactType)">
                    {{ contact.contactTypeName }}
                  </el-tag>
                </div>
                <span class="contact-time">{{ formatTime(contact.lastMessageTime) }}</span>
              </div>
              <p class="contact-last-msg">{{ contact.lastMessage || '暂无消息' }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧聊天区域 -->
      <div class="chat-panel">
        <!-- 聊天头部 -->
        <div class="chat-header" v-if="currentContact">
          <div class="chat-contact-info">
            <el-avatar :size="40" :src="currentContact.contactAvatar">
              {{ currentContact.contactName?.charAt(0) }}
            </el-avatar>
            <div class="chat-contact-detail">
              <div class="chat-contact-name">
                <span>{{ currentContact.contactName }}</span>
                <el-tag size="small" :type="getContactTypeTag(currentContact.contactType)">
                  {{ currentContact.contactTypeName }}
                </el-tag>
              </div>
              <span class="chat-contact-phone">{{ currentContact.contactPhone || '' }}</span>
            </div>
          </div>
          <div class="chat-actions">
            <el-button
              v-if="currentContact.unreadCount > 0"
              type="primary"
              text
              size="small"
              @click="markConversationRead"
            >
              标记已读
            </el-button>
            <el-dropdown trigger="click">
              <el-button type="info" text circle icon="MoreFilled" />
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="loadMessages">刷新消息</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
        <div class="chat-header empty" v-else>
          <span>请选择联系人开始对话</span>
        </div>

        <!-- 消息列表 -->
        <div ref="messageListRef" v-loading="messageLoading" class="message-list">
          <div v-if="!currentContact" class="empty-chat">
            <div class="empty-chat-content">
              <div class="empty-icon">
                <el-icon :size="48"><ChatDotRound /></el-icon>
              </div>
              <p>选择左侧联系人开始对话</p>
              <el-button type="primary" @click="showContactDialog = true">
                <el-icon class="btn-icon"><Plus /></el-icon>
                新建对话
              </el-button>
            </div>
          </div>
          <template v-else>
            <div v-if="messages.length === 0" class="empty-chat">
              <el-empty description="暂无消息记录" :image-size="100" />
            </div>
            <div v-else class="messages-container">
              <div
                v-for="(msg, index) in messages"
                :key="msg.id"
                class="message-wrapper"
              >
                <!-- 时间分割线 -->
                <div v-if="shouldShowTime(msg, index)" class="time-divider">
                  <span>{{ formatDateTime(msg.createTime) }}</span>
                </div>
                
                <!-- 消息气泡 -->
                <div class="message-item" :class="{ self: msg.isSelf }">
                  <el-avatar :size="36" :src="msg.senderAvatar" class="message-avatar">
                    {{ msg.senderName?.charAt(0) }}
                  </el-avatar>
                  <div class="message-content">
                    <div v-if="!msg.isSelf" class="message-sender">{{ msg.senderName }}</div>
                    <div class="message-bubble" :class="{ self: msg.isSelf }">
                      {{ msg.content || '[消息内容为空]' }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </div>

        <!-- 发送消息区域 -->
        <div v-if="currentContact" class="send-area">
          <div class="send-input-wrapper">
            <el-input
              v-model="newMessage"
              type="textarea"
              :rows="2"
              :autosize="{ minRows: 2, maxRows: 4 }"
              placeholder="输入消息内容..."
              resize="none"
              @keydown.ctrl.enter="sendMessage"
            />
            <el-button
              type="primary"
              :loading="sending"
              :disabled="!newMessage.trim()"
              @click="sendMessage"
            >
              发送
            </el-button>
          </div>
          <div class="send-tip">按 Ctrl + Enter 发送</div>
        </div>
      </div>
    </div>

    <!-- 选择联系人弹窗 -->
    <ContactSelectDialog
      v-model="showContactDialog"
      @select="handleContactSelect"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Plus } from '@element-plus/icons-vue'
import {
  getContacts,
  getConversation,
  sendMessage as sendMessageApi,
  markConversationAsRead
} from '@/api/message'
import ContactSelectDialog from '@/components/institution/ContactSelectDialog.vue'

const contactLoading = ref(false)
const messageLoading = ref(false)
const sending = ref(false)
const contacts = ref([])
const messages = ref([])
const currentContact = ref(null)
const newMessage = ref('')
const messageListRef = ref(null)
const searchKeyword = ref('')
const showContactDialog = ref(false)

// 计算总未读数
const totalUnread = computed(() => {
  return contacts.value.reduce((sum, c) => sum + (c.unreadCount || 0), 0)
})

// 过滤联系人
const filteredContacts = computed(() => {
  if (!searchKeyword.value) return contacts.value
  const keyword = searchKeyword.value.toLowerCase()
  return contacts.value.filter(c =>
    c.contactName?.toLowerCase().includes(keyword) ||
    c.contactPhone?.includes(keyword)
  )
})

// 获取联系人类型标签样式
const getContactTypeTag = (type) => {
  const types = { 1: 'success', 2: 'warning', 3: 'primary', 4: 'danger' }
  return types[type] || 'info'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60 * 1000) return '刚刚'
  if (diff < 60 * 60 * 1000) return Math.floor(diff / (60 * 1000)) + '分钟前'
  if (diff < 24 * 60 * 60 * 1000) return Math.floor(diff / (60 * 60 * 1000)) + '小时前'
  if (diff < 7 * 24 * 60 * 60 * 1000) return Math.floor(diff / (24 * 60 * 60 * 1000)) + '天前'
  
  return `${date.getMonth() + 1}/${date.getDate()}`
}

// 格式化日期时间
const formatDateTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const isToday = date.toDateString() === now.toDateString()
  const pad = (n) => n.toString().padStart(2, '0')
  
  if (isToday) {
    return `${pad(date.getHours())}:${pad(date.getMinutes())}`
  }
  return `${date.getMonth() + 1}月${date.getDate()}日 ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

// 判断是否显示时间
const shouldShowTime = (msg, index) => {
  if (index === 0) return true
  const prevMsg = messages.value[index - 1]
  const prevTime = new Date(prevMsg.createTime).getTime()
  const currTime = new Date(msg.createTime).getTime()
  return currTime - prevTime > 5 * 60 * 1000
}

// 判断是否是当前选中的联系人
const isActiveContact = (contact) => {
  return currentContact.value &&
    currentContact.value.contactType === contact.contactType &&
    currentContact.value.contactId === contact.contactId
}

// 加载联系人列表
const loadContacts = async () => {
  contactLoading.value = true
  try {
    const res = await getContacts()
    contacts.value = res.data || []
  } catch (error) {
    console.error('获取联系人列表失败:', error)
    ElMessage.error('获取联系人列表失败')
  } finally {
    contactLoading.value = false
  }
}

// 选择联系人
const selectContact = async (contact) => {
  currentContact.value = contact
  await loadMessages()
}

// 加载对话消息
const loadMessages = async () => {
  if (!currentContact.value) return
  
  messageLoading.value = true
  try {
    console.log('=== 机构端加载对话消息 ===')
    console.log('当前联系人:', currentContact.value)
    
    const res = await getConversation({
      contactType: currentContact.value.contactType,
      contactId: currentContact.value.contactId,
      pageNum: 1,
      pageSize: 100
    })
    
    console.log('API响应:', res)
    
    // 处理分页数据，兼容不同的响应格式
    let messageList = []
    if (res.data) {
      // 如果是分页对象，取 records
      if (res.data.records) {
        messageList = res.data.records
      } else if (Array.isArray(res.data)) {
        // 如果直接是数组
        messageList = res.data
      }
    }
    
    // 打印每条消息的详细信息
    console.log('获取到的消息列表:', messageList)
    messageList.forEach((msg, index) => {
      console.log(`消息${index + 1}:`, {
        id: msg.id,
        content: msg.content,
        isSelf: msg.isSelf,
        senderType: msg.senderType,
        senderId: msg.senderId,
        senderName: msg.senderName
      })
    })
    
    messages.value = messageList
    
    nextTick(() => {
      scrollToBottom()
    })
  } catch (error) {
    console.error('获取对话记录失败:', error)
    messages.value = []
    ElMessage.error('获取对话记录失败')
  } finally {
    messageLoading.value = false
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

// 发送消息
const sendMessage = async () => {
  if (!newMessage.value.trim() || !currentContact.value) return
  
  sending.value = true
  try {
    await sendMessageApi({
      receiverType: currentContact.value.contactType,
      receiverId: currentContact.value.contactId,
      msgType: 3,
      content: newMessage.value.trim()
    })
    
    newMessage.value = ''
    await loadMessages()
    await loadContacts()
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error(error.message || '发送消息失败')
  } finally {
    sending.value = false
  }
}

// 标记对话已读
const markConversationRead = async () => {
  if (!currentContact.value) return
  
  try {
    await markConversationAsRead(
      currentContact.value.contactType,
      currentContact.value.contactId
    )
    currentContact.value.unreadCount = 0
    
    const contact = contacts.value.find(c =>
      c.contactType === currentContact.value.contactType &&
      c.contactId === currentContact.value.contactId
    )
    if (contact) {
      contact.unreadCount = 0
    }
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

// 选择联系人后的处理
const handleContactSelect = (contact) => {
  const existingContact = contacts.value.find(c =>
    c.contactType === contact.contactType && c.contactId === contact.contactId
  )
  
  if (existingContact) {
    selectContact(existingContact)
  } else {
    const newContact = {
      contactType: contact.contactType,
      contactTypeName: contact.contactTypeName,
      contactId: contact.contactId,
      contactName: contact.contactName,
      contactAvatar: contact.contactAvatar,
      contactPhone: contact.contactPhone,
      lastMessage: '',
      lastMessageTime: null,
      unreadCount: 0
    }
    contacts.value.unshift(newContact)
    selectContact(newContact)
  }
}

// 监听消息变化，自动滚动到底部
watch(messages, () => {
  nextTick(() => {
    scrollToBottom()
  })
})

onMounted(() => {
  loadContacts()
})
</script>

<style scoped lang="scss">
.message-center {
  padding: 20px;
  height: calc(100vh - 100px);
  background-color: #f5f7fa;
}

.chat-container {
  display: flex;
  height: 100%;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

// 左侧联系人面板
.contact-panel {
  width: 320px;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}

.panel-header {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
  
  .header-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }
  
  .panel-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }
  
  .search-input {
    :deep(.el-input__wrapper) {
      border-radius: 20px;
      background-color: #f5f7fa;
    }
  }
}

.contact-list {
  flex: 1;
  overflow-y: auto;
  
  .empty-state {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 200px;
  }
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
  border-bottom: 1px solid #f5f7fa;
  
  &:hover {
    background-color: #f5f7fa;
  }
  
  &.active {
    background-color: #ecf5ff;
  }
  
  .contact-avatar {
    position: relative;
    flex-shrink: 0;
    
    .unread-badge {
      position: absolute;
      top: -4px;
      right: -4px;
      min-width: 18px;
      height: 18px;
      padding: 0 5px;
      font-size: 11px;
      font-weight: 500;
      color: #fff;
      background-color: #f56c6c;
      border-radius: 9px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
  
  .contact-info {
    flex: 1;
    margin-left: 12px;
    min-width: 0;
    
    .contact-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 4px;
    }
    
    .contact-name-row {
      display: flex;
      align-items: center;
      gap: 6px;
    }
    
    .contact-name {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
      max-width: 120px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .contact-time {
      font-size: 12px;
      color: #909399;
      flex-shrink: 0;
    }
    
    .contact-last-msg {
      font-size: 13px;
      color: #909399;
      margin: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

// 右侧聊天面板
.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}

.chat-header {
  height: 64px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e4e7ed;
  background-color: #fff;
  
  &.empty {
    justify-content: flex-start;
    color: #909399;
  }
  
  .chat-contact-info {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .chat-contact-detail {
    .chat-contact-name {
      display: flex;
      align-items: center;
      gap: 8px;
      
      span {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }
    
    .chat-contact-phone {
      font-size: 12px;
      color: #909399;
    }
  }
  
  .chat-actions {
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f7fa;
  
  .empty-chat {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    
    .empty-chat-content {
      text-align: center;
      
      .empty-icon {
        width: 80px;
        height: 80px;
        margin: 0 auto 16px;
        background-color: #e4e7ed;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #909399;
      }
      
      p {
        color: #909399;
        margin-bottom: 16px;
      }
      
      .btn-icon {
        margin-right: 4px;
      }
    }
  }
}

.messages-container {
  .message-wrapper {
    animation: fadeIn 0.2s ease-out;
  }
}

.time-divider {
  display: flex;
  justify-content: center;
  margin: 16px 0;
  
  span {
    padding: 4px 12px;
    font-size: 12px;
    color: #909399;
    background-color: rgba(0, 0, 0, 0.05);
    border-radius: 12px;
  }
}

.message-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 16px;
  
  &.self {
    flex-direction: row-reverse;
    
    .message-content {
      align-items: flex-end;
    }
  }
  
  .message-avatar {
    flex-shrink: 0;
  }
  
  .message-content {
    display: flex;
    flex-direction: column;
    max-width: 60%;
    
    .message-sender {
      font-size: 12px;
      color: #909399;
      margin-bottom: 4px;
    }
    
    .message-bubble {
      padding: 10px 14px;
      background-color: #fff;
      color: #303133;
      border-radius: 4px 16px 16px 16px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      font-size: 14px;
      line-height: 1.5;
      word-break: break-word;
      white-space: pre-wrap;
      
      &.self {
        background-color: #409eff;
        color: #fff;
        border-radius: 16px 4px 16px 16px;
      }
    }
  }
}

.send-area {
  padding: 16px 20px;
  border-top: 1px solid #e4e7ed;
  background-color: #fff;
  
  .send-input-wrapper {
    display: flex;
    gap: 12px;
    align-items: flex-end;
    
    .el-textarea {
      flex: 1;
    }
  }
  
  .send-tip {
    margin-top: 8px;
    font-size: 12px;
    color: #909399;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>