<template>
  <div class="address-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">地址管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增地址
          </el-button>
        </div>
      </template>

      <!-- 地址列表 -->
      <div v-loading="loading" class="address-list">
        <el-empty v-if="addressList.length === 0" description="暂无地址，请添加" />

        <div v-for="item in addressList" :key="item.id" class="address-item">
          <div class="address-content">
            <div class="address-header">
              <span class="contact-name">{{ item.contactName }}</span>
              <span class="contact-phone">{{ item.contactPhone }}</span>
              <el-tag v-if="item.isDefault === 1" type="success" size="small">默认</el-tag>
            </div>
            <div class="address-detail">
              {{ item.fullAddress || (item.province + item.city + item.district + item.detailAddress) }}
            </div>
          </div>
          <div class="address-actions">
            <el-button v-if="item.isDefault !== 1" type="primary" link @click="handleSetDefault(item)">
              设为默认
            </el-button>
            <el-button type="primary" link @click="handleEdit(item)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(item)">删除</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 新增/编辑地址弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑地址' : '新增地址'"
      width="800px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="formData.contactName" placeholder="请输入联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="选择位置">
          <MapPicker
            ref="mapPickerRef"
            :longitude="formData.longitude"
            :latitude="formData.latitude"
            @select="handleMapSelect"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="省份" prop="province">
              <el-input v-model="formData.province" placeholder="省份" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市" prop="city">
              <el-input v-model="formData.city" placeholder="城市" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="区县" prop="district">
              <el-input v-model="formData.district" placeholder="区县" readonly />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="详细地址" prop="detailAddress">
          <el-input
            v-model="formData.detailAddress"
            type="textarea"
            :rows="2"
            placeholder="请输入详细地址（街道、门牌号等）"
          />
        </el-form-item>

        <el-form-item label="设为默认">
          <el-switch v-model="formData.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import MapPicker from '@/components/MapPicker.vue'
import {
  getAddressList,
  addAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress
} from '@/api/address'

// 列表数据
const loading = ref(false)
const addressList = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const mapPickerRef = ref(null)

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

// 表单验证规则
const rules = {
  contactName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  province: [
    { required: true, message: '请在地图上选择位置', trigger: 'change' }
  ],
  city: [
    { required: true, message: '请在地图上选择位置', trigger: 'change' }
  ],
  district: [
    { required: true, message: '请在地图上选择位置', trigger: 'change' }
  ],
  detailAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
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

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

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
      ElMessage.success('修改成功')
    } else {
      await addAddress(data)
      ElMessage.success('新增成功')
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
  ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAddress(item.id)
      ElMessage.success('删除成功')
      fetchAddressList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

// 设置默认地址
const handleSetDefault = async (item) => {
  try {
    await setDefaultAddress(item.id)
    ElMessage.success('设置成功')
    fetchAddressList()
  } catch (error) {
    console.error('设置默认地址失败:', error)
  }
}

onMounted(() => {
  fetchAddressList()
})
</script>

<style scoped lang="scss">
.address-manage {
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

  .address-list {
    min-height: 200px;

    .address-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 15px;
      border: 1px solid #ebeef5;
      border-radius: 4px;
      margin-bottom: 10px;
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      }

      &:last-child {
        margin-bottom: 0;
      }

      .address-content {
        flex: 1;

        .address-header {
          margin-bottom: 8px;

          .contact-name {
            font-size: 16px;
            font-weight: 500;
            margin-right: 15px;
          }

          .contact-phone {
            color: #606266;
            margin-right: 10px;
          }
        }

        .address-detail {
          color: #909399;
          font-size: 14px;
        }
      }

      .address-actions {
        flex-shrink: 0;
        margin-left: 20px;
      }
    }
  }
}
</style>
