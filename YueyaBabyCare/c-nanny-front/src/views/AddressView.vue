<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Header -->
    <div class="bg-white px-4 py-3 flex items-center border-b sticky top-0 z-10">
      <t-button theme="default" variant="text" @click="goBack">
        <chevron-left-icon size="24px" />
      </t-button>
      <h1 class="flex-1 text-center text-lg font-medium">地址管理</h1>
      <t-button theme="primary" variant="text" @click="handleAdd">
        <add-icon size="24px" />
      </t-button>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex items-center justify-center py-20">
      <t-loading size="large" />
    </div>

    <!-- Empty State -->
    <div v-else-if="addressList.length === 0" class="flex flex-col items-center justify-center py-20">
      <location-icon size="64px" class="text-gray-300 mb-4" />
      <p class="text-gray-500 mb-4">暂无地址，请添加</p>
      <t-button theme="primary" @click="handleAdd">添加地址</t-button>
    </div>

    <!-- Address List -->
    <div v-else class="p-4 space-y-3">
      <div
        v-for="item in addressList"
        :key="item.id"
        class="bg-white rounded-xl p-4 shadow-sm"
      >
        <div class="flex items-start justify-between">
          <div class="flex-1">
            <div class="flex items-center gap-2 mb-2">
              <span class="font-medium text-gray-800">{{ item.contactName }}</span>
              <span class="text-gray-500">{{ item.contactPhone }}</span>
              <t-tag v-if="item.isDefault === 1" theme="primary" size="small">默认</t-tag>
            </div>
            <p class="text-gray-600 text-sm leading-relaxed">
              {{ item.fullAddress || (item.province + item.city + item.district + item.detailAddress) }}
            </p>
          </div>
        </div>
        <div class="flex items-center justify-end gap-2 mt-3 pt-3 border-t border-gray-100">
          <t-button
            v-if="item.isDefault !== 1"
            theme="default"
            variant="text"
            size="small"
            @click="handleSetDefault(item)"
          >
            设为默认
          </t-button>
          <t-button theme="primary" variant="text" size="small" @click="handleEdit(item)">
            编辑
          </t-button>
          <t-button theme="danger" variant="text" size="small" @click="handleDelete(item)">
            删除
          </t-button>
        </div>
      </div>
    </div>

    <!-- Add/Edit Dialog -->
    <t-popup v-model="dialogVisible" placement="bottom" :close-on-overlay-click="false">
      <div class="bg-white rounded-t-2xl max-h-[90vh] overflow-y-auto">
        <div class="sticky top-0 bg-white px-4 py-3 flex items-center justify-between border-b z-10">
          <t-button theme="default" variant="text" @click="dialogVisible = false">取消</t-button>
          <span class="font-medium">{{ isEdit ? '编辑地址' : '新增地址' }}</span>
          <t-button theme="primary" variant="text" :loading="submitting" @click="handleSubmit">保存</t-button>
        </div>

        <div class="p-4 space-y-4">
          <!-- Contact Info -->
          <div class="space-y-3">
            <t-input
              v-model="formData.contactName"
              label="联系人"
              placeholder="请输入联系人姓名"
              :status="errors.contactName ? 'error' : 'default'"
              :tips="errors.contactName"
            />
            <t-input
              v-model="formData.contactPhone"
              label="联系电话"
              placeholder="请输入联系电话"
              type="tel"
              :status="errors.contactPhone ? 'error' : 'default'"
              :tips="errors.contactPhone"
            />
          </div>

          <!-- Map Picker -->
          <div class="mt-4">
            <div class="text-sm text-gray-600 mb-2">选择位置（点击地图或拖动标记）</div>
            <MapPicker
              ref="mapPickerRef"
              :longitude="formData.longitude"
              :latitude="formData.latitude"
              @select="handleMapSelect"
            />
          </div>

          <!-- Detail Address -->
          <t-textarea
            v-model="formData.detailAddress"
            label="详细地址"
            placeholder="请输入详细地址（街道、门牌号等）"
            :maxlength="200"
            :status="errors.detailAddress ? 'error' : 'default'"
            :tips="errors.detailAddress"
          />

          <!-- Default Switch -->
          <div class="flex items-center justify-between py-2">
            <span class="text-gray-700">设为默认地址</span>
            <t-switch v-model="formData.isDefault" :custom-value="[1, 0]" />
          </div>
        </div>
      </div>
    </t-popup>

    <!-- Delete Confirm Dialog -->
    <t-dialog
      v-model:visible="deleteDialogVisible"
      header="删除地址"
      body="确定要删除该地址吗？"
      confirm-btn="确定"
      cancel-btn="取消"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { ChevronLeftIcon, AddIcon, LocationIcon } from 'tdesign-icons-vue-next'
import MapPicker from '@/components/MapPicker.vue'
import {
  getAddressList,
  addAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress
} from '@/api/address'

const router = useRouter()

// 列表数据
const loading = ref(false)
const addressList = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const mapPickerRef = ref(null)

// 删除确认
const deleteDialogVisible = ref(false)
const deleteTarget = ref(null)

// 表单数据
const formData = reactive({
  id: null,
  contactName: '',
  contactPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  longitude: null,
  latitude: null,
  isDefault: 0
})

// 表单错误
const errors = reactive({
  contactName: '',
  contactPhone: '',
  detailAddress: ''
})

// 返回上一页
const goBack = () => {
  router.back()
}

// 获取地址列表
const fetchAddressList = async () => {
  loading.value = true
  try {
    const res = await getAddressList()
    addressList.value = res.data || []
  } catch (error) {
    console.error('获取地址列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  formData.id = null
  formData.contactName = ''
  formData.contactPhone = ''
  formData.province = ''
  formData.city = ''
  formData.district = ''
  formData.detailAddress = ''
  formData.longitude = null
  formData.latitude = null
  formData.isDefault = 0
  errors.contactName = ''
  errors.contactPhone = ''
  errors.detailAddress = ''
}

// 新增地址
const handleAdd = () => {
  resetForm()
  isEdit.value = false
  dialogVisible.value = true
}

// 编辑地址
const handleEdit = (item) => {
  isEdit.value = true
  formData.id = item.id
  formData.contactName = item.contactName
  formData.contactPhone = item.contactPhone
  formData.province = item.province
  formData.city = item.city
  formData.district = item.district
  formData.detailAddress = item.detailAddress
  formData.longitude = item.longitude
  formData.latitude = item.latitude
  formData.isDefault = item.isDefault
  dialogVisible.value = true
}

// 地图选择回调
const handleMapSelect = (data) => {
  formData.province = data.province
  formData.city = data.city
  formData.district = data.district
  formData.detailAddress = data.detailAddress
  formData.longitude = data.longitude
  formData.latitude = data.latitude
}

// 表单验证
const validateForm = () => {
  let valid = true
  errors.contactName = ''
  errors.contactPhone = ''
  errors.detailAddress = ''

  if (!formData.contactName.trim()) {
    errors.contactName = '请输入联系人姓名'
    valid = false
  }

  if (!formData.contactPhone.trim()) {
    errors.contactPhone = '请输入联系电话'
    valid = false
  } else if (!/^1[3-9]\d{9}$/.test(formData.contactPhone)) {
    errors.contactPhone = '手机号格式不正确'
    valid = false
  }

  if (!formData.province || !formData.city) {
    MessagePlugin.warning('请在地图上选择位置')
    valid = false
  }

  if (!formData.detailAddress.trim()) {
    errors.detailAddress = '请输入详细地址'
    valid = false
  }

  return valid
}

// 提交表单
const handleSubmit = async () => {
  if (!validateForm()) return

  submitting.value = true
  try {
    const data = {
      contactName: formData.contactName,
      contactPhone: formData.contactPhone,
      province: formData.province,
      city: formData.city,
      district: formData.district,
      detailAddress: formData.detailAddress,
      longitude: formData.longitude,
      latitude: formData.latitude,
      isDefault: formData.isDefault
    }

    if (isEdit.value) {
      await updateAddress(formData.id, data)
      MessagePlugin.success('修改成功')
    } else {
      await addAddress(data)
      MessagePlugin.success('新增成功')
    }

    dialogVisible.value = false
    fetchAddressList()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    submitting.value = false
  }
}

// 删除地址
const handleDelete = (item) => {
  deleteTarget.value = item
  deleteDialogVisible.value = true
}

// 确认删除
const confirmDelete = async () => {
  if (!deleteTarget.value) return

  try {
    await deleteAddress(deleteTarget.value.id)
    MessagePlugin.success('删除成功')
    fetchAddressList()
  } catch (error) {
    console.error('删除失败:', error)
  }
  deleteTarget.value = null
}

// 设置默认地址
const handleSetDefault = async (item) => {
  try {
    await setDefaultAddress(item.id)
    MessagePlugin.success('设置成功')
    fetchAddressList()
  } catch (error) {
    console.error('设置默认地址失败:', error)
  }
}

onMounted(() => {
  fetchAddressList()
})
</script>
