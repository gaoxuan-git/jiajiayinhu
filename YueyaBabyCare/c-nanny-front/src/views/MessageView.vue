<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 联系人列表视图 -->
    <div v-if="!currentContact" class="flex flex-col h-screen">
      <!-- 顶部标题栏 -->
      <div class="bg-white px-4 py-3 border-b border-gray-200 flex items-center justify-between">
        <h1 class="text-lg font-semibold text-gray-800">消息</h1>
        <div class="flex items-center gap-3">
          <span v-if="totalUnread > 0" class="px-2 py-0.5 text-xs bg-red-500 text-white rounded-full">
            {{ totalUnread > 99 ? '99+' : totalUnread }}
          </span>
          <button @click="goToContactSelect" class="w-8 h-8 flex items-center justify-center text-blue-500 bg-blue-50 rounded-full">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
            </svg>
          </button>
          <button @click="loadContacts" class="text-blue-500 text-sm">刷新</button>
        </div>
      </div>

      <!-- 联系人列表 -->
      <div class="flex-1 overflow-y-auto">
        <div v-if="contactLoading" class="flex justify-center py-12">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
        </div>
        <div v-else-if="contacts.length > 0">
          <div
            v-for="contact in contacts"
            :key="`${contact.contactType}-${contact.contactId}`"
            @click="selectContact(contact)"
            class="flex items-center gap-3 px-4 py-3 bg-white border-b border-gray-100 active:bg-gray-50"
          >
            <div class="relative">
              <div class="w-12 h-12 rounded-full bg-gray-200 flex items-center justify-center text-gray-600 font-medium text-lg">
                {{ contact.contactName?.charAt(0) || '?' }}
              </div>
              <span v-if="contact.unreadCount > 0" class="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                {{ contact.unreadCount > 9 ? '9+' : contact.unreadCount }}
              </span>
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 mb-1">
                <span class="font-medium text-gray-800 truncate">{{ contact.contactName }}</span>
                <span :class="getContactTypeClass(contact.contactType)">{{ contact.contactTypeName }}</span>
              </div>
              <p class="text-gray-500 text-sm truncate">{{ contact.lastMessage || '暂无消息' }}</p>
            </div>
            <div class="flex flex-col items-end gap-1">
              <span class="text-gray-400 text-xs">{{ formatTime(contact.lastMessageTime) }}</span>
              <svg class="w-4 h-4 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
              </svg>
            </div>
          </div>
        </div>
        <div v-else class="flex flex-col items-center justify-center py-16">
          <svg class="w-16 h-16 text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
          </svg>
          <p class="text-gray-500">暂无联系人</p>
          <p class="text-gray-400 text-sm mt-1">与家长或机构对话后会显示在这里</p>
        </div>
      </div>
    </div>

    <!-- 对话视图 -->
    <div v-else class="flex flex-col h-screen">
      <!-- 对话顶部栏 -->
      <div class="bg-white px-4 py-3 border-b border-gray-200 flex items-center gap-3">
        <button @click="backToList" class="text-gray-600">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
          </svg>
        </button>
        <div class="flex-1 min-w-0">
          <div class="flex items-center gap-2">
            <span class="font-semibold text-gray-800 truncate">{{ currentContact.contactName }}</span>
            <span :class="getContactTypeClass(currentContact.contactType)">{{ currentContact.contactTypeName }}</span>
          </div>
        </div>
        <button
          v-if="currentContact.unreadCount > 0"
          @click="markConversationRead"
          class="text-blue-500 text-sm"
        >
          已读
        </button>
      </div>

      <!-- 消息列表 -->
      <div ref="chatListRef" class="flex-1 overflow-y-auto p-4 bg-gray-50 space-y-3">
        <div v-if="chatLoading" class="flex justify-center py-8">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
        </div>
        <template v-else>
          <div v-if="chatMessages.length === 0" class="flex flex-col items-center justify-center h-full">
            <svg class="w-12 h-12 text-gray-300 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
            </svg>
            <p class="text-gray-500 text-sm">暂无消息记录</p>
            <p class="text-gray-400 text-xs mt-1">发送第一条消息开始对话</p>
          </div>
          <div
            v-for="msg in chatMessages"
            :key="msg.id"
            :class="['flex gap-2', msg.isSelf ? 'flex-row-reverse' : '']"
          >
            <div class="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center text-gray-600 text-sm font-medium shrink-0">
              {{ msg.isSelf ? '我' : (msg.senderName?.charAt(0) || '?') }}
            </div>
            <div :class="['max-w-[70%]', msg.isSelf ? 'items-end' : 'items-start']">
              <div :class="['text-xs text-gray-400 mb-1', msg.isSelf ? 'text-right' : 'text-left']">
                {{ formatDateTime(msg.createTime) }}
              </div>
              <div :class="['px-3 py-2 rounded-2xl shadow-sm whitespace-pre-wrap break-words text-sm', msg.isSelf ? 'bg-blue-500 text-white rounded-tr-sm' : 'bg-white text-gray-700 rounded-tl-sm']">
                {{ msg.content }}
              </div>
            </div>
          </div>
        </template>
      </div>

      <!-- 发送消息区域 -->
      <div class="bg-white px-4 py-3 border-t border-gray-200 safe-area-bottom">
        <div class="flex gap-2 items-end">
          <textarea
            v-model="newMessage"
            @keydown.enter.exact.prevent="sendMessage"
            placeholder="输入消息..."
            rows="1"
            class="flex-1 px-3 py-2 text-sm bg-gray-100 border-0 rounded-2xl resize-none focus:outline-none focus:ring-2 focus:ring-blue-500 max-h-24"
            style="min-height: 36px;"
          ></textarea>
          <button
            @click="sendMessage"
            :disabled="!newMessage.trim() || sending"
            :class="['w-10 h-10 rounded-full flex items-center justify-center transition-colors', newMessage.trim() && !sending ? 'bg-blue-500 text-white' : 'bg-gray-200 text-gray-400']"
          >
            <svg v-if="!sending" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"/>
            </svg>
            <div v-else class="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"></div>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onActivated, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  getContacts,
  getConversation,
  sendMessage as sendMessageApi,
  markConversationAsRead
} from '@/api/message'

const router = useRouter()

// 联系人相关
const contacts = ref([])
const contactLoading = ref(false)
const currentContact = ref(null)

// 对话相关
const chatMessages = ref([])
const chatLoading = ref(false)
const newMessage = ref('')
const sending = ref(false)
const chatListRef = ref(null)

// 计算总未读数
const totalUnread = computed(() => {
  return contacts.value.reduce((sum, c) => sum + (c.unreadCount || 0), 0)
})

// 加载联系人列表
const loadContacts = async () => {
  contactLoading.value = true
  try {
    const res = await getContacts()
    contacts.value = res.data || []
  } catch (error) {
    console.error('获取联系人列表失败:', error)
  } finally {
    contactLoading.value = false
  }
}

// 选择联系人
const selectContact = async (contact) => {
  currentContact.value = contact
  await loadChatMessages()
}

// 返回列表
const backToList = () => {
  currentContact.value = null
  loadContacts()
}

// 跳转到选择联系人页面
const goToContactSelect = () => {
  router.push('/contact-select')
}

// 检查是否有选中的联系人（从选择联系人页面返回）
const checkSelectedContact = () => {
  const selectedContactStr = sessionStorage.getItem('selectedContact')
  if (selectedContactStr) {
    try {
      const selectedContact = JSON.parse(selectedContactStr)
      sessionStorage.removeItem('selectedContact')
      
      // 检查是否已存在该联系人
      const existingContact = contacts.value.find(c =>
        c.contactType === selectedContact.contactType &&
        c.contactId === selectedContact.contactId
      )
      
      if (existingContact) {
        selectContact(existingContact)
      } else {
        // 添加新联系人到列表
        const newContact = {
          contactType: selectedContact.contactType,
          contactTypeName: selectedContact.contactTypeName,
          contactId: selectedContact.contactId,
          contactName: selectedContact.contactName,
          contactAvatar: selectedContact.contactAvatar,
          contactPhone: selectedContact.contactPhone,
          lastMessage: '',
          lastMessageTime: null,
          unreadCount: 0
        }
        contacts.value.unshift(newContact)
        selectContact(newContact)
      }
    } catch (e) {
      console.error('解析选中联系人失败:', e)
    }
  }
}

// 加载对话消息
const loadChatMessages = async () => {
  if (!currentContact.value) return
  chatLoading.value = true
  try {
    const res = await getConversation({
      contactType: currentContact.value.contactType,
      contactId: currentContact.value.contactId,
      pageNum: 1,
      pageSize: 100
    })
    // 处理分页数据，兼容不同的响应格式
    let messages = []
    if (res.data) {
      // 如果是分页对象，取 records
      if (res.data.records) {
        messages = res.data.records
      } else if (Array.isArray(res.data)) {
        // 如果直接是数组
        messages = res.data
      }
    }
    chatMessages.value = messages
    nextTick(() => scrollToBottom())
  } catch (error) {
    console.error('获取对话记录失败:', error)
    chatMessages.value = []
  } finally {
    chatLoading.value = false
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (chatListRef.value) {
    chatListRef.value.scrollTop = chatListRef.value.scrollHeight
  }
}

// 发送消息
const sendMessage = async () => {
  if (!newMessage.value.trim() || !currentContact.value || sending.value) return
  sending.value = true
  try {
    await sendMessageApi({
      receiverType: currentContact.value.contactType,
      receiverId: currentContact.value.contactId,
      msgType: 3,
      content: newMessage.value.trim()
    })
    newMessage.value = ''
    await loadChatMessages()
  } catch (error) {
    console.error('发送消息失败:', error)
  } finally {
    sending.value = false
  }
}

// 标记对话已读
const markConversationRead = async () => {
  if (!currentContact.value) return
  try {
    await markConversationAsRead(currentContact.value.contactType, currentContact.value.contactId)
    currentContact.value.unreadCount = 0
    const contact = contacts.value.find(c =>
      c.contactType === currentContact.value.contactType &&
      c.contactId === currentContact.value.contactId
    )
    if (contact) contact.unreadCount = 0
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

// 获取联系人类型样式
const getContactTypeClass = (type) => {
  const classes = {
    1: 'px-1.5 py-0.5 text-xs font-medium text-emerald-700 bg-emerald-50 rounded',
    2: 'px-1.5 py-0.5 text-xs font-medium text-amber-700 bg-amber-50 rounded',
    3: 'px-1.5 py-0.5 text-xs font-medium text-blue-700 bg-blue-50 rounded',
    4: 'px-1.5 py-0.5 text-xs font-medium text-red-700 bg-red-50 rounded'
  }
  return classes[type] || classes[1]
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
  const pad = (n) => n.toString().padStart(2, '0')
  return `${pad(date.getHours())}:${pad(date.getMinutes())}`
}

// 监听对话消息变化，自动滚动到底部
watch(chatMessages, () => {
  nextTick(() => scrollToBottom())
})

onMounted(async () => {
  await loadContacts()
  // 检查是否有从选择联系人页面返回的选中联系人
  checkSelectedContact()
})

// 页面激活时也检查（用于 keep-alive 场景）
onActivated(async () => {
  await loadContacts()
  checkSelectedContact()
})
</script>

<style scoped>
.safe-area-bottom {
  padding-bottom: max(12px, env(safe-area-inset-bottom));
}

textarea {
  line-height: 1.4;
}
</style>