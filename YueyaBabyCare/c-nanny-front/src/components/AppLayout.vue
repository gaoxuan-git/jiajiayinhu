<template>
  <div class="flex flex-col h-screen bg-gray-50">
    <!-- Main Content -->
    <div class="flex-1 overflow-y-auto pb-20">
      <router-view />
    </div>

    <!-- Bottom Navigation (Custom Implementation for Responsive/Mobile feel) -->
    <div class="fixed bottom-0 w-full bg-white border-t border-gray-200 flex justify-around items-center py-3 shadow-lg z-50">
      <div 
        v-for="item in list" 
        :key="item.value" 
        class="flex flex-col items-center gap-1 cursor-pointer w-full"
        :class="value === item.value ? 'text-blue-600' : 'text-gray-400'"
        @click="onChange(item.value)"
      >
        <component :is="item.icon" size="24px" />
        <span class="text-xs font-medium">{{ item.label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { CalendarIcon, Edit1Icon, UserIcon, TimeIcon, FileIcon, ChatIcon } from 'tdesign-icons-vue-next';

const router = useRouter();
const route = useRoute();
const value = ref('schedule');

const list = [
  { value: 'schedule', label: '排班', icon: CalendarIcon },
  { value: 'appointment', label: '预约', icon: TimeIcon },
  { value: 'carePlan', label: '计划', icon: FileIcon },
  { value: 'message', label: '消息', icon: ChatIcon },
  { value: 'profile', label: '我的', icon: UserIcon },
];

const onChange = (val) => {
  value.value = val;
  router.push({ name: val });
};

watch(
  () => route.name,
  (newName) => {
    if (list.some(item => item.value === newName)) {
      value.value = newName;
    }
  },
  { immediate: true }
);
</script>
