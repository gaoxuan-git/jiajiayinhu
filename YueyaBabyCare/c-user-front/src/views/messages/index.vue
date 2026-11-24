
<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <!-- 页面标题 -->
    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-gray-800">消息中心</h1>
      <p class="text-sm text-gray-500 mt-1">查看系统公告、消息通知和与机构/保姆的对话</p>
    </div>

    <!-- 标签页切换 -->
    <div class="flex gap-4 mb-6">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        @click="activeTab = tab.key"
        :class="[
          'px-4 py-2 rounded-lg font-medium transition-colors duration-200',
          activeTab === tab.key
            ? 'bg-blue-500 text-white'
            : 'bg-white text-gray-600 hover:bg-gray-100 border border-gray-200'
        ]"
      >
        {{ tab.label }}
        <span
          v-if="tab.key === 'messages' && unreadCount > 0"
          class="ml-2 px-2 py-0.5 text-xs bg-red-500 text-white rounded-full"
        >
          {{ unreadCount > 99 ? '99+' : unreadCount }}
        </span>
        <span
          v-if="tab.key === 'conversations' && totalConversationUnread > 0"
          class="ml-2 px-2 py-0.5 text-xs bg-red-500 text-white rounded-full"
        >
          {{ totalConversationUnread > 99 ? '99+' : totalConversationUnread }}
        </span>
      </button>
    </div>

    <!-- 系统公告 -->
    <div v-if="activeTab === 'notices'" class="space-y-4">
      <div v-if="noticeLoading" class="flex justify-center py-12">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
      </div>
      <div v-else-if="noticeList.length > 0" class="space-y-4">
        <div
          v-for="notice in noticeList"
          :key="notice.id"
          @click="showNoticeDetail(notice)"
          class="bg-white rounded-xl shadow p-6 cursor-pointer hover:shadow-md transition-shadow duration-200"
        >
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-2">
                <span :class="getNoticeTypeClass(notice.type)">{{ notice.typeName }}</span>
                <h3 class="text-lg font-semibold text-gray-800">{{ notice.title }}</h3>
              </div>
              <p class="text-gray-600 text-sm line-clamp-2">{{ notice.content }}</p>
              <p class="text-gray-400 text-xs mt-3">{{ formatTime(notice.publishTime) }}</p>
            </div>
            <svg class="w-5 h-5 text-gray-400 shrink-0 ml-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
            </svg>
          </div>
        </div>
        <div v-if="noticeHasMore" class="text-center py-4">
          <button @click="loadMoreNotices" :disabled="noticeLoadingMore" class="px-6 py-2 text-blue-500 hover:text-blue-600 font-medium transition-colors">
            {{ noticeLoadingMore ? '加载中...' : '加载更多' }}
          </button>
        </div>
      </div>
      <div v-else class="bg-white rounded-xl shadow p-12 text-center">
        <svg class="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
        </svg>
        <p class="text-gray-500">暂无系统公告</p>
      </div>
    </div>

    <!-- 消息通知 -->
    <div v-if="activeTab === 'messages'" class="space-y-4">
      <div class="flex justify-between items-center">
        <div class="flex gap-2">
          <select v-model="msgTypeFilter" @change="loadMessages" class="px-3 py-2 text-sm bg-white border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
            <option :value="null">全部类型</option>
            <option :value="1">系统消息</option>
            <option :value="2">护理提醒</option>
            <option :value="3">留言反馈</option>
          </select>
          <select v-model="readFilter" @change="loadMessages" class="px-3 py-2 text-sm bg-white border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
            <option :value="null">全部状态</option>
            <option :value="0">未读</option>
            <option :value="1">已读</option>
          </select>
        </div>
        <button v-if="unreadCount > 0" @click="handleMarkAllRead" class="px-4 py-2 text-sm text-blue-500 hover:text-blue-600 font-medium transition-colors">
          全部标为已读
        </button>
      </div>
      <div v-if="messageLoading" class="flex justify-center py-12">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
      </div>
      <div v-else-if="messageList.length > 0" class="space-y-3">
        <div
          v-for="msg in messageList"
          :key="msg.id"
          @click="showMessageDetail(msg)"
          :class="['bg-white rounded-xl shadow p-4 cursor-pointer hover:shadow-md transition-shadow duration-200', msg.isRead === 0 ? 'border-l-4 border-blue-500' : '']"
        >
          <div class="flex items-start gap-4">
            <div :class="getMsgIconClass(msg.msgType)">
              <svg v-if="msg.msgType === 1" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              <svg v-else-if="msg.msgType === 2" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
              </svg>
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 mb-1">
                <span :class="getMsgTypeTagClass(msg.msgType)">{{ msg.msgTypeName }}</span>
                <span v-if="msg.isRead === 0" class="px-2 py-0.5 text-xs font-medium text-red-700 bg-red-50 rounded-full">未读</span>
              </div>
              <h4 :class="['font-medium mb-1', msg.isRead === 0 ? 'text-gray-800' : 'text-gray-600']">{{ msg.title || '无标题' }}</h4>
              <p class="text-gray-500 text-sm truncate">{{ msg.content }}</p>
              <p class="text-gray-400 text-xs mt-2">{{ formatTime(msg.createTime) }}</p>
            </div>
            <svg class="w-5 h-5 text-gray-400 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
            </svg>
          </div>
        </div>
        <div v-if="messageHasMore" class="text-center py-4">
          <button @click="loadMoreMessages" :disabled="messageLoadingMore" class="px-6 py-2 text-blue-500 hover:text-blue-600 font-medium transition-colors">
            {{ messageLoadingMore ? '加载中...' : '加载更多' }}
          </button>
        </div>
      </div>
      <div v-else class="bg-white rounded-xl shadow p-12 text-center">
        <svg class="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"/>
        </svg>
        <p class="text-gray-500">暂无消息</p>
      </div>
    </div>

    <!-- 对话列表 -->
    <div v-if="activeTab === 'conversations'" class="flex gap-6 h-[calc(100vh-220px)]">
      <!-- 左侧联系人列表 -->
      <div class="w-80 bg-white rounded-xl shadow flex flex-col">
        <div class="px-4 py-3 border-b border-gray-200 flex items-center justify-between">
          <h3 class="font-semibold text-gray-800">联系人</h3>
          <div class="flex items-center gap-2">
            <button @click="showContactDialog = true" class="w-7 h-7 flex items-center justify-center text-white bg-blue-500 rounded-full hover:bg-blue-600 transition-colors">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
              </svg>
            </button>
            <button @click="loadContacts" class="text-blue-500 hover:text-blue-600 text-sm font-medium">刷新</button>
          </div>
        </div>
        <div v-if="contactLoading" class="flex-1 flex items-center justify-center">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
        </div>
        <div v-else-if="contacts.length > 0" class="flex-1 overflow-y-auto">
          <div
            v-for="contact in contacts"
            :key="`${contact.contactType}-${contact.contactId}`"
            @click="selectContact(contact)"
            :class="['flex items-center gap-3 px-4 py-3 cursor-pointer transition-colors duration-200', isActiveContact(contact) ? 'bg-blue-50' : 'hover:bg-gray-50']"
          >
            <div class="relative">
              <div class="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center text-gray-600 font-medium">
                {{ contact.contactName?.charAt(0) || '?' }}
              </div>
              <span v-if="contact.unreadCount > 0" class="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                {{ contact.unreadCount > 9 ? '9+' : contact.unreadCount }}
              </span>
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2">
                <span class="font-medium text-gray-800 truncate">{{ contact.contactName }}</span>
                <span :class="getContactTypeClass(contact.contactType)">{{ contact.contactTypeName }}</span>
              </div>
              <p class="text-gray-500 text-sm truncate">{{ contact.lastMessage || '暂无消息' }}</p>
            </div>
            <span class="text-gray-400 text-xs shrink-0">{{ formatTime(contact.lastMessageTime) }}</span>
          </div>
        </div>
        <div v-else class="flex-1 flex items-center justify-center">
          <p class="text-gray-500">暂无联系人</p>
        </div>
      </div>

      <!-- 右侧对话区域 -->
      <div class="flex-1 bg-white rounded-xl shadow flex flex-col">
        <template v-if="currentContact">
          <!-- 对话头部 -->
          <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center text-gray-600 font-medium">
                {{ currentContact.contactName?.charAt(0) || '?' }}
              </div>
              <div>
                <div class="flex items-center gap-2">
                  <span class="font-semibold text-gray-800">{{ currentContact.contactName }}</span>
                  <span :class="getContactTypeClass(currentContact.contactType)">{{ currentContact.contactTypeName }}</span>
                </div>
              </div>
            </div>
            <button
              v-if="currentContact.unreadCount > 0"
              @click="markConversationRead"
              class="px-3 py-1.5 text-sm text-blue-500 border border-blue-300 rounded-lg hover:bg-blue-50 transition-colors"
            >
              标记已读
            </button>
          </div>

          <!-- 消息列表 -->
          <div ref="chatListRef" class="flex-1 overflow-y-auto p-6 bg-gray-50 space-y-4">
            <div v-if="chatLoading" class="flex justify-center py-8">
              <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
            </div>
            <template v-else>
              <div v-if="chatMessages.length === 0" class="flex items-center justify-center h-full">
                <p class="text-gray-500">暂无消息记录，发送第一条消息开始对话吧</p>
              </div>
              <div
                v-for="msg in chatMessages"
                :key="msg.id"
                :class="['flex gap-3', msg.isSelf ? 'flex-row-reverse' : '']"
              >
                <div class="w-9 h-9 rounded-full bg-gray-200 flex items-center justify-center text-gray-600 text-sm font-medium shrink-0">
                  {{ msg.isSelf ? '我' : (msg.senderName?.charAt(0) || '?') }}
                </div>
                <div :class="['max-w-[60%]', msg.isSelf ? 'items-end' : 'items-start']">
                  <div :class="['flex items-center gap-2 mb-1 text-xs text-gray-500', msg.isSelf ? 'flex-row-reverse' : '']">
                    <span>{{ msg.isSelf ? '我' : msg.senderName }}</span>
                    <span>{{ formatDateTime(msg.createTime) }}</span>
                  </div>
                  <div :class="['px-4 py-2.5 rounded-xl shadow-sm whitespace-pre-wrap break-words', msg.isSelf ? 'bg-blue-500 text-white' : 'bg-white text-gray-700']">
                    {{ msg.content }}
                  </div>
                </div>
              </div>
            </template>
          </div>

          <!-- 发送消息区域 -->
          <div class="px-6 py-4 border-t border-gray-200">
            <div class="flex gap-3">
              <textarea
                v-model="newMessage"
                @keydown.ctrl.enter="sendMessage"
                placeholder="输入消息内容... (Ctrl+Enter发送)"
                rows="2"
                class="flex-1 px-4 py-2 text-gray-700 bg-gray-50 border border-gray-300 rounded-lg resize-none focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200"
              ></textarea>
              <button
                @click="sendMessage"
                :disabled="!newMessage.trim() || sending"
                :class="['px-6 py-2 rounded-lg font-medium transition-colors duration-200', newMessage.trim() && !sending ? 'bg-blue-500 hover:bg-blue-600 text-white' : 'bg-gray-100 text-gray-400 cursor-not-allowed']"
              >
                {{ sending ? '发送中...' : '发送' }}
              </button>
            </div>
          </div>
        </template>
        <div v-else class="flex-1 flex items-center justify-center">
          <div class="text-center">
            <svg class="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
            </svg>
            <p class="text-gray-500 mb-4">选择左侧联系人开始对话</p>
            <button @click="showContactDialog = true" class="px-4 py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-600 transition-colors">
              <span class="flex items-center gap-2">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                </svg>
                新建对话
              </span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 选择联系人弹窗 -->
    <ContactSelectDialog
      v-model="showContactDialog"
      @select="handleContactSelect"
    />

    <!-- 公告详情弹窗 -->
    <div v-if="showNoticeModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-2xl max-h-[80vh] overflow-hidden">
        <div class="flex items-center justify-between px-6 py-4 border-b border-gray-200">
          <div class="flex items-center gap-2">
            <span :class="getNoticeTypeClass(currentNotice?.type)">{{ currentNotice?.typeName }}</span>
            <h3 class="text-lg font-semibold text-gray-800">{{ currentNotice?.title }}</h3>
          </div>
          <button @click="showNoticeModal = false" class="text-gray-400 hover:text-gray-600 transition-colors">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>
        <div class="px-6 py-4 overflow-y-auto max-h-[60vh]">
          <p class="text-gray-400 text-sm mb-4">发布时间：{{ formatTime(currentNotice?.publishTime) }}</p>
          <div class="text-gray-600 whitespace-pre-wrap leading-relaxed">{{ currentNotice?.content }}</div>
        </div>
        <div class="flex justify-end px-6 py-4 border-t border-gray-200">
          <button @click="showNoticeModal = false" class="px-4 py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-600 transition-colors">关闭</button>
        </div>
      </div>
    </div>

    <!-- 消息详情弹窗 -->
    <div v-if="showMessageModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-2xl max-h-[80vh] overflow-hidden">
        <div class="flex items-center justify-between px-6 py-4 border-b border-gray-200">
          <div class="flex items-center gap-2">
            <span :class="getMsgTypeTagClass(currentMessage?.msgType)">{{ currentMessage?.msgTypeName }}</span>
            <h3 class="text-lg font-semibold text-gray-800">{{ currentMessage?.title || '消息详情' }}</h3>
          </div>
          <button @click="showMessageModal = false" class="text-gray-400 hover:text-gray-600 transition-colors">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>
        <div class="px-6 py-4 overflow-y-auto max-h-[60vh]">
          <div class="flex items-center gap-4 mb-4 text-sm text-gray-500">
            <span>来自：{{ currentMessage?.senderTypeName }}</span>
            <span>时间：{{ formatTime(currentMessage?.createTime) }}</span>
          </div>
          <div class="text-gray-600 whitespace-pre-wrap leading-relaxed">{{ currentMessage?.content }}</div>
        </div>
        <div class="flex justify-end gap-3 px-6 py-4 border-t border-gray-200">
          <button @click="handleDeleteMessage" class="px-4 py-2 text-red-500 border border-red-300 rounded-lg hover:bg-red-50 transition-colors">删除</button>
          <button @click="showMessageModal = false" class="px-4 py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-600 transition-colors">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { getPublishedNoticeList } from '@/api/notice'
import {
  getMessageList,
  getUnreadCount,
  markAsRead,
  markAllAsRead,
  deleteMessage,
  getContacts,
  getConversation,
  sendMessage as sendMessageApi,
  markConversationAsRead
} from '@/api/message'
import ContactSelectDialog from '@/components/ContactSelectDialog.vue'

// 标签页
const tabs = [
  { key: 'notices', label: '系统公告' },
  { key: 'messages', label: '消息通知' },
  { key: 'conversations', label: '对话' }
]
const activeTab = ref('notices')

// 公告相关
const noticeList = ref([])
const noticeLoading = ref(false)
const noticeLoadingMore = ref(false)
const noticePageNum = ref(1)
const noticeTotal = ref(0)
const noticeHasMore = ref(false)
const showNoticeModal = ref(false)
const currentNotice = ref(null)

// 消息相关
const messageList = ref([])
const messageLoading = ref(false)
const messageLoadingMore = ref(false)
const messagePageNum = ref(1)
const messageTotal = ref(0)
const messageHasMore = ref(false)
const unreadCount = ref(0)
const msgTypeFilter = ref(null)
const readFilter = ref(null)
const showMessageModal = ref(false)
const currentMessage = ref(null)

// 对话相关
const contacts = ref([])
const contactLoading = ref(false)
const currentContact = ref(null)
const chatMessages = ref([])
const chatLoading = ref(false)
const newMessage = ref('')
const sending = ref(false)
const chatListRef = ref(null)
const showContactDialog = ref(false)

// 选择联系人后的处理
const handleContactSelect = (contact) => {
  // 检查是否已存在该联系人
  const existingContact = contacts.value.find(c =>
    c.contactType === contact.contactType && c.contactId === contact.contactId
  )
  
  if (existingContact) {
    // 如果已存在，直接选中
    selectContact(existingContact)
  } else {
    // 如果不存在，添加到联系人列表并选中
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

// 计算对话总未读数
const totalConversationUnread = computed(() => {
  return contacts.value.reduce((sum, c) => sum + (c.unreadCount || 0), 0)
})

// 加载公告列表
const loadNotices = async (append = false) => {
  if (append) {
    noticeLoadingMore.value = true
  } else {
    noticeLoading.value = true
    noticePageNum.value = 1
  }
  try {
    const res = await getPublishedNoticeList({ pageNum: noticePageNum.value, pageSize: 10 })
    const data = res.data || {}
    if (append) {
      noticeList.value = [...noticeList.value, ...(data.records || [])]
    } else {
      noticeList.value = data.records || []
    }
    noticeTotal.value = data.total || 0
    noticeHasMore.value = noticeList.value.length < noticeTotal.value
  } catch (error) {
    console.error('获取公告列表失败:', error)
  } finally {
    noticeLoading.value = false
    noticeLoadingMore.value = false
  }
}

const loadMoreNotices = () => {
  noticePageNum.value++
  loadNotices(true)
}

const showNoticeDetail = (notice) => {
  currentNotice.value = notice
  showNoticeModal.value = true
}

// 加载消息列表
const loadMessages = async (append = false) => {
  if (append) {
    messageLoadingMore.value = true
  } else {
    messageLoading.value = true
    messagePageNum.value = 1
  }
  try {
    const res = await getMessageList({
      msgType: msgTypeFilter.value,
      isRead: readFilter.value,
      pageNum: messagePageNum.value,
      pageSize: 10
    })
    const data = res.data || {}
    if (append) {
      messageList.value = [...messageList.value, ...(data.records || [])]
    } else {
      messageList.value = data.records || []
    }
    messageTotal.value = data.total || 0
    messageHasMore.value = messageList.value.length < messageTotal.value
  } catch (error) {
    console.error('获取消息列表失败:', error)
  } finally {
    messageLoading.value = false
    messageLoadingMore.value = false
  }
}

const loadMoreMessages = () => {
  messagePageNum.value++
  loadMessages(true)
}

const loadUnreadCount = async () => {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data || 0
  } catch (error) {
    console.error('获取未读消息数失败:', error)
  }
}

const showMessageDetail = async (msg) => {
  currentMessage.value = msg
  showMessageModal.value = true
  if (msg.isRead === 0) {
    try {
      await markAsRead(msg.id)
      msg.isRead = 1
      loadUnreadCount()
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }
}

const handleMarkAllRead = async () => {
  try {
    await markAllAsRead()
    messageList.value.forEach(msg => { msg.isRead = 1 })
    unreadCount.value = 0
  } catch (error) {
    console.error('全部标为已读失败:', error)
  }
}

const handleDeleteMessage = async () => {
  if (!currentMessage.value) return
  try {
    await deleteMessage(currentMessage.value.id)
    messageList.value = messageList.value.filter(m => m.id !== currentMessage.value.id)
    showMessageModal.value = false
    currentMessage.value = null
    loadUnreadCount()
  } catch (error) {
    console.error('删除消息失败:', error)
  }
}

// 联系人相关
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

const isActiveContact = (contact) => {
  return currentContact.value &&
    currentContact.value.contactType === contact.contactType &&
    currentContact.value.contactId === contact.contactId
}

const selectContact = async (contact) => {
  currentContact.value = contact
  await loadChatMessages()
}

const loadChatMessages = async () => {
  if (!currentContact.value) return
  chatLoading.value = true
  try {
    console.log('=== 开始加载对话记录 ===')
    console.log('当前联系人:', currentContact.value)
    console.log('请求参数:', {
      contactType: currentContact.value.contactType,
      contactId: currentContact.value.contactId,
      pageNum: 1,
      pageSize: 100
    })
    
    const res = await getConversation({
      contactType: currentContact.value.contactType,
      contactId: currentContact.value.contactId,
      pageNum: 1,
      pageSize: 100
    })
    
    console.log('API响应:', res)
    console.log('res.data:', res.data)
    console.log('res.data?.records:', res.data?.records)
    
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
    
    console.log('处理后的消息列表:', messageList)
    console.log('消息数量:', messageList.length)
    
    chatMessages.value = messageList
    nextTick(() => scrollToBottom())
  } catch (error) {
    console.error('获取对话记录失败:', error)
    chatMessages.value = []
  } finally {
    chatLoading.value = false
  }
}

const scrollToBottom = () => {
  if (chatListRef.value) {
    chatListRef.value.scrollTop = chatListRef.value.scrollHeight
  }
}

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
    await loadContacts()
  } catch (error) {
    console.error('发送消息失败:', error)
  } finally {
    sending.value = false
  }
}

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

// 样式辅助函数
const getNoticeTypeClass = (type) => {
  const classes = {
    1: 'px-2 py-1 text-xs font-medium text-blue-700 bg-blue-50 rounded-full',
    2: 'px-2 py-1 text-xs font-medium text-emerald-700 bg-emerald-50 rounded-full',
    3: 'px-2 py-1 text-xs font-medium text-gray-700 bg-gray-100 rounded-full'
  }
  return classes[type] || classes[3]
}

const getMsgIconClass = (msgType) => {
  const classes = {
    1: 'w-10 h-10 rounded-full bg-blue-100 text-blue-500 flex items-center justify-center shrink-0',
    2: 'w-10 h-10 rounded-full bg-amber-100 text-amber-500 flex items-center justify-center shrink-0',
    3: 'w-10 h-10 rounded-full bg-emerald-100 text-emerald-500 flex items-center justify-center shrink-0'
  }
  return classes[msgType] || classes[1]
}

const getMsgTypeTagClass = (msgType) => {
  const classes = {
    1: 'px-2 py-0.5 text-xs font-medium text-blue-700 bg-blue-50 rounded-full',
    2: 'px-2 py-0.5 text-xs font-medium text-amber-700 bg-amber-50 rounded-full',
    3: 'px-2 py-0.5 text-xs font-medium text-emerald-700 bg-emerald-50 rounded-full'
  }
  return classes[msgType] || classes[1]
}

const getContactTypeClass = (type) => {
  const classes = {
    1: 'px-2 py-0.5 text-xs font-medium text-emerald-700 bg-emerald-50 rounded-full',
    2: 'px-2 py-0.5 text-xs font-medium text-amber-700 bg-amber-50 rounded-full',
    3: 'px-2 py-0.5 text-xs font-medium text-blue-700 bg-blue-50 rounded-full',
    4: 'px-2 py-0.5 text-xs font-medium text-red-700 bg-red-50 rounded-full'
  }
  return classes[type] || classes[1]
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60 * 1000) return '刚刚'
  if (diff < 60 * 60 * 1000) return Math.floor(diff / (60 * 1000)) + '分钟前'
  if (diff < 24 * 60 * 60 * 1000) return Math.floor(diff / (60 * 60 * 1000)) + '小时前'
  if (diff < 7 * 24 * 60 * 60 * 1000) return Math.floor(diff / (24 * 60 * 60 * 1000)) + '天前'
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const formatDateTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const pad = (n) => n.toString().padStart(2, '0')
  return `${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

// 监听对话消息变化，自动滚动到底部
watch(chatMessages, () => {
  nextTick(() => scrollToBottom())
})

onMounted(() => {
  loadNotices()
  loadMessages()
  loadUnreadCount()
  loadContacts()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
