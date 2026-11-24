<template>
  <div class="baby-edit-page">
    <div class="page-container">
      <!-- 返回按钮 -->
      <div class="back-nav">
        <t-button variant="text" @click="$router.back()">
          <template #icon><t-icon name="chevron-left" /></template>
          返回
        </t-button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-wrapper">
        <t-loading size="large" text="加载中..." />
      </div>

      <!-- 表单内容 -->
      <div v-else class="form-wrapper">
        <div class="form-card">
          <div class="card-header">
            <h2>{{ isEdit ? '编辑宝宝信息' : '添加宝宝' }}</h2>
            <p>{{ isEdit ? '修改宝宝的基本信息' : '填写宝宝的基本信息' }}</p>
          </div>

          <t-form
            ref="formRef"
            :data="form"
            :rules="rules"
            label-align="top"
            @submit="handleSubmit"
          >
            <div class="form-grid">
              <!-- 左侧列 -->
              <div class="form-column">
                <t-form-item label="宝宝姓名" name="name">
                  <t-input
                    v-model="form.name"
                    placeholder="请输入宝宝姓名"
                    clearable
                  />
                </t-form-item>

                <t-form-item label="宝宝昵称" name="nickname">
                  <t-input
                    v-model="form.nickname"
                    placeholder="请输入宝宝昵称（选填）"
                    clearable
                  />
                </t-form-item>

                <t-form-item label="性别" name="gender">
                  <div class="gender-selector">
                    <div
                      class="gender-option"
                      :class="{ active: form.gender === 1, male: form.gender === 1 }"
                      @click="form.gender = 1"
                    >
                      <t-icon name="user" />
                      <span>男宝</span>
                    </div>
                    <div
                      class="gender-option"
                      :class="{ active: form.gender === 2, female: form.gender === 2 }"
                      @click="form.gender = 2"
                    >
                      <t-icon name="user" />
                      <span>女宝</span>
                    </div>
                  </div>
                </t-form-item>

                <t-form-item label="出生日期" name="birthDate">
                  <t-date-picker
                    v-model="form.birthDate"
                    mode="date"
                    placeholder="请选择出生日期"
                    :disable-date="disableFutureDate"
                    style="width: 100%"
                  />
                </t-form-item>
              </div>

              <!-- 右侧列 -->
              <div class="form-column">
                <t-form-item label="血型" name="bloodType">
                  <div class="blood-type-selector">
                    <div
                      v-for="type in bloodTypes"
                      :key="type"
                      class="blood-type-option"
                      :class="{ active: form.bloodType === type }"
                      @click="form.bloodType = form.bloodType === type ? '' : type"
                    >
                      {{ type }}型
                    </div>
                  </div>
                </t-form-item>

                <t-form-item label="过敏信息" name="allergyInfo">
                  <t-textarea
                    v-model="form.allergyInfo"
                    placeholder="请输入过敏信息（选填）"
                    :maxlength="200"
                    :autosize="{ minRows: 2, maxRows: 4 }"
                  />
                </t-form-item>

                <t-form-item label="备注" name="remark">
                  <t-textarea
                    v-model="form.remark"
                    placeholder="请输入备注信息（选填）"
                    :maxlength="500"
                    :autosize="{ minRows: 3, maxRows: 5 }"
                  />
                </t-form-item>
              </div>
            </div>

            <!-- 提交按钮 -->
            <div class="form-actions">
              <t-button variant="outline" @click="$router.back()">取消</t-button>
              <t-button theme="primary" type="submit" :loading="submitting">
                {{ isEdit ? '保存修改' : '添加宝宝' }}
              </t-button>
            </div>
          </t-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { getBabyById, addBaby, updateBaby } from '@/api/baby'

const route = useRoute()
const router = useRouter()

const formRef = ref(null)
const loading = ref(false)
const submitting = ref(false)
const bloodTypes = ['A', 'B', 'AB', 'O', 'Rh']

// 判断是编辑还是新增
const isEdit = computed(() => !!route.params.id)

// 表单数据
const form = reactive({
  name: '',
  nickname: '',
  gender: null,
  birthDate: '',
  bloodType: '',
  allergyInfo: '',
  remark: ''
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入宝宝姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  birthDate: [{ required: true, message: '请选择出生日期', trigger: 'change' }]
}

// 禁用未来日期
const disableFutureDate = (date) => {
  return date > new Date()
}

// 获取宝宝详情（编辑模式）
const fetchBaby = async () => {
  const id = route.params.id
  if (!id) return

  loading.value = true
  try {
    const res = await getBabyById(id)
    if (res.code === 200 && res.data) {
      const baby = res.data
      form.name = baby.name || ''
      form.nickname = baby.nickname || ''
      form.gender = baby.gender
      form.birthDate = baby.birthDate || ''
      form.bloodType = baby.bloodType || ''
      form.allergyInfo = baby.allergyInfo || ''
      form.remark = baby.remark || ''
    } else {
      MessagePlugin.error(res.message || '获取宝宝信息失败')
      router.back()
    }
  } catch (error) {
    console.error('获取宝宝信息失败:', error)
    MessagePlugin.error('获取宝宝信息失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = async ({ validateResult }) => {
  if (validateResult !== true) return

  submitting.value = true
  try {
    const data = {
      name: form.name.trim(),
      nickname: form.nickname?.trim() || null,
      gender: form.gender,
      birthDate: form.birthDate,
      bloodType: form.bloodType || null,
      allergyInfo: form.allergyInfo?.trim() || null,
      remark: form.remark?.trim() || null
    }

    let res
    if (isEdit.value) {
      res = await updateBaby(route.params.id, data)
    } else {
      res = await addBaby(data)
    }

    if (res.code === 200) {
      MessagePlugin.success(isEdit.value ? '修改成功' : '添加成功')
      router.back()
    } else {
      MessagePlugin.error(res.message || (isEdit.value ? '修改失败' : '添加失败'))
    }
  } catch (error) {
    console.error('提交失败:', error)
    MessagePlugin.error(isEdit.value ? '修改失败' : '添加失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (isEdit.value) {
    fetchBaby()
  }
})
</script>

<style scoped>
.baby-edit-page {
  padding: 24px;
  background: #F9FAFB;
  min-height: calc(100vh - 120px);
}

.page-container {
  max-width: 900px;
  margin: 0 auto;
}

.back-nav {
  margin-bottom: 16px;
}

.loading-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

/* 表单卡片 */
.form-wrapper {
  display: flex;
  justify-content: center;
}

.form-card {
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  width: 100%;
}

.card-header {
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid #F3F4F6;
}

.card-header h2 {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 600;
  color: #1F2937;
}

.card-header p {
  margin: 0;
  font-size: 14px;
  color: #6B7280;
}

/* 表单网格布局 */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
}

.form-column {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 性别选择器 */
.gender-selector {
  display: flex;
  gap: 16px;
}

.gender-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 20px;
  border: 2px solid #E5E7EB;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  color: #6B7280;
}

.gender-option:hover {
  border-color: #D1D5DB;
  background: #F9FAFB;
}

.gender-option.active.male {
  border-color: #3B82F6;
  background: #EFF6FF;
  color: #3B82F6;
}

.gender-option.active.female {
  border-color: #EC4899;
  background: #FDF2F8;
  color: #EC4899;
}

/* 血型选择器 */
.blood-type-selector {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.blood-type-option {
  padding: 8px 16px;
  border: 2px solid #E5E7EB;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  color: #6B7280;
  font-weight: 500;
}

.blood-type-option:hover {
  border-color: #D1D5DB;
  background: #F9FAFB;
}

.blood-type-option.active {
  border-color: #EF4444;
  background: #FEF2F2;
  color: #EF4444;
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #F3F4F6;
}

.form-actions :deep(.t-button) {
  min-width: 100px;
}

/* 响应式 */
@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .form-card {
    padding: 24px;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions :deep(.t-button) {
    width: 100%;
  }
}
</style>
