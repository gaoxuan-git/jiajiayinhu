<template>
  <div class="institution-profile">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">机构信息</span>
          <el-button v-if="!isEditing" type="primary" @click="startEdit">编辑信息</el-button>
          <div v-else>
            <el-button @click="cancelEdit">取消</el-button>
            <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
          </div>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="120px"
        :disabled="!isEditing"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="机构名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入机构名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名">
              <el-input v-model="formData.username" disabled />
            </el-form-item>
          </el-col>
        </el-row>

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

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="营业执照号" prop="licenseNo">
              <el-input v-model="formData.licenseNo" placeholder="请输入营业执照号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="机构Logo">
          <el-upload
            v-if="isEditing"
            class="logo-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleLogoSuccess"
            :before-upload="beforeLogoUpload"
            accept="image/*"
          >
            <img v-if="formData.logo" :src="formData.logo" class="logo" />
            <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <img v-else-if="formData.logo" :src="formData.logo" class="logo" />
          <span v-else class="text-gray-400">暂无Logo</span>
        </el-form-item>

        <el-form-item label="营业执照图片">
          <el-upload
            v-if="isEditing"
            class="license-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleLicenseSuccess"
            :before-upload="beforeLogoUpload"
            accept="image/*"
          >
            <img v-if="formData.licenseImg" :src="formData.licenseImg" class="license-img" />
            <el-icon v-else class="license-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <el-image
            v-else-if="formData.licenseImg"
            :src="formData.licenseImg"
            :preview-src-list="[formData.licenseImg]"
            style="width: 200px; height: 150px"
            fit="cover"
          />
          <span v-else class="text-gray-400">暂无营业执照</span>
        </el-form-item>

        <el-form-item label="机构简介">
          <el-input
            v-model="formData.introduction"
            type="textarea"
            :rows="5"
            placeholder="请输入机构简介"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="状态">
          <el-tag v-if="formData.status === 1" type="success">正常</el-tag>
          <el-tag v-else-if="formData.status === 0" type="info">禁用</el-tag>
          <el-tag v-else type="warning">待审核</el-tag>
        </el-form-item>

        <el-form-item label="注册时间">
          <span>{{ formData.createTime }}</span>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getInstitutionInfo, updateInstitutionInfo } from '@/api/institution'

const store = useStore()
const formRef = ref(null)
const isEditing = ref(false)
const saving = ref(false)
const originalData = ref({})

const formData = reactive({
  id: null,
  username: '',
  name: '',
  logo: '',
  contactName: '',
  contactPhone: '',
  email: '',
  licenseNo: '',
  licenseImg: '',
  introduction: '',
  status: null,
  createTime: ''
})

const rules = {
  name: [
    { required: true, message: '请输入机构名称', trigger: 'blur' }
  ],
  contactName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

// 上传地址和请求头
const uploadUrl = computed(() => '/api/file/upload')
const uploadHeaders = computed(() => {
  const token = store.getters.token
  return token ? { Authorization: 'Bearer ' + token } : {}
})

// 获取机构信息
const fetchData = async () => {
  try {
    const res = await getInstitutionInfo()
    const data = res.data || {}
    // 映射后端字段到表单字段
    Object.assign(formData, {
      id: data.id,
      username: data.username,
      name: data.name,
      logo: data.logo,
      contactName: data.contactName,
      contactPhone: data.contactPhone,
      email: data.email,
      licenseNo: data.licenseNo,
      licenseImg: data.licenseImg,
      introduction: data.introduction,
      status: data.status,
      createTime: data.createTime
    })
    // 保存原始数据用于取消编辑
    originalData.value = { ...formData }
  } catch (error) {
    console.error('获取机构信息失败:', error)
  }
}

// 开始编辑
const startEdit = () => {
  isEditing.value = true
}

// 取消编辑
const cancelEdit = () => {
  Object.assign(formData, originalData.value)
  isEditing.value = false
}

// 保存
const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    await updateInstitutionInfo(formData)
    ElMessage.success('保存成功')
    
    // 更新 store 中的用户信息
    store.dispatch('user/getUserInfo')
    
    // 刷新数据
    await fetchData()
    isEditing.value = false
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// Logo上传成功
const handleLogoSuccess = (response) => {
  console.log('Logo上传响应:', response)
  if (response.code === 200 && response.data) {
    // 处理文件URL - 如果是相对路径则保持相对路径，前端会通过代理访问
    formData.logo = response.data.fileUrl
    ElMessage.success('Logo上传成功')
  } else {
    ElMessage.error(response.message || 'Logo上传失败')
  }
}

// 营业执照上传成功
const handleLicenseSuccess = (response) => {
  console.log('营业执照上传响应:', response)
  if (response.code === 200 && response.data) {
    // 处理文件URL - 如果是相对路径则保持相对路径，前端会通过代理访问
    formData.licenseImg = response.data.fileUrl
    ElMessage.success('营业执照上传成功')
  } else {
    ElMessage.error(response.message || '营业执照上传失败')
  }
}

// 上传前校验
const beforeLogoUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.institution-profile {
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

  .logo-uploader,
  .license-uploader {
    :deep(.el-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: border-color 0.3s;

      &:hover {
        border-color: #409eff;
      }
    }
  }

  .logo-uploader :deep(.el-upload) {
    width: 100px;
    height: 100px;
  }

  .license-uploader :deep(.el-upload) {
    width: 200px;
    height: 150px;
  }

  .logo-uploader-icon,
  .license-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .logo {
    width: 100px;
    height: 100px;
    display: block;
    object-fit: cover;
  }

  .license-img {
    width: 200px;
    height: 150px;
    display: block;
    object-fit: cover;
  }
}
</style>
