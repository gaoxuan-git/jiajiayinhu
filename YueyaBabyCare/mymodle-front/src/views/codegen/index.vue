<template>
  <div class="codegen-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>代码生成器</span>
          <el-tag type="info">填入数据库信息，选择表名，一键生成代码</el-tag>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <!-- 数据库配置 -->
        <el-divider content-position="left">数据库配置</el-divider>
        
        <el-form-item label="数据库地址" prop="dbHost">
          <el-input v-model="form.dbHost" placeholder="例如: localhost:3306" />
        </el-form-item>

        <el-form-item label="数据库名" prop="dbName">
          <el-input v-model="form.dbName" placeholder="例如: mydb" />
        </el-form-item>

        <el-form-item label="用户名" prop="dbUsername">
          <el-input v-model="form.dbUsername" placeholder="例如: root" />
        </el-form-item>

        <el-form-item label="密码" prop="dbPassword">
          <el-input 
            v-model="form.dbPassword" 
            type="password" 
            placeholder="数据库密码"
            show-password 
          />
        </el-form-item>

        <el-form-item>
          <el-button @click="handleTestConnection" :loading="testing">
            测试连接
          </el-button>
          <el-button 
            @click="handleLoadTables" 
            :loading="loadingTables"
            :disabled="!connectionSuccess"
          >
            加载表列表
          </el-button>
        </el-form-item>

        <!-- 表选择 -->
        <el-divider content-position="left">表选择</el-divider>

        <el-form-item label="选择表" prop="tableNames">
          <el-select 
            v-model="form.tableNames" 
            multiple 
            filterable 
            placeholder="请先测试连接并加载表列表"
            style="width: 100%"
          >
            <el-option
              v-for="table in tables"
              :key="table"
              :label="table"
              :value="table"
            />
          </el-select>
          <div class="tips">支持多选，按住 Ctrl/Command 可选择多个表</div>
        </el-form-item>

        <!-- 生成配置 -->
        <el-divider content-position="left">生成配置</el-divider>

        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" placeholder="例如: 张三" />
        </el-form-item>

        <el-form-item label="包名" prop="packageName">
          <el-input v-model="form.packageName" placeholder="例如: com.lidexin.business" />
        </el-form-item>

        <el-form-item label="覆盖已有文件" prop="overwrite">
          <el-switch v-model="form.overwrite" />
          <span class="tips" style="margin-left: 10px">
            开启后将覆盖已存在的文件，请谨慎操作
          </span>
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleGenerate" :loading="generating">
            <el-icon><Document /></el-icon>
            生成代码
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 生成结果 -->
    <el-card class="box-card result-card" v-if="result">
      <template #header>
        <div class="card-header">
          <span>生成结果</span>
        </div>
      </template>
      <el-alert 
        :title="result" 
        :type="resultType" 
        :closable="false"
        show-icon
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Refresh } from '@element-plus/icons-vue'
import { testConnection, getTables, generateCode } from '@/api/codegen'

const formRef = ref(null)
const testing = ref(false)
const loadingTables = ref(false)
const generating = ref(false)
const connectionSuccess = ref(false)
const tables = ref([])
const result = ref('')
const resultType = ref('success')

const form = reactive({
  dbHost: 'localhost:3306',
  dbName: 'mydb',
  dbUsername: 'root',
  dbPassword: '123456',
  tableNames: [],
  author: '张三',
  packageName: 'com.lidexin.business',
  overwrite: false
})

const rules = {
  dbHost: [
    { required: true, message: '请输入数据库地址', trigger: 'blur' }
  ],
  dbName: [
    { required: true, message: '请输入数据库名', trigger: 'blur' }
  ],
  dbUsername: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  dbPassword: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  tableNames: [
    { required: true, message: '请选择要生成的表', trigger: 'change' }
  ]
}

// 测试数据库连接
const handleTestConnection = async () => {
  testing.value = true
  try {
    const res = await testConnection({
      dbHost: form.dbHost,
      dbName: form.dbName,
      dbUsername: form.dbUsername,
      dbPassword: form.dbPassword
    })
    
    if (res.code === 200 && res.data) {
      ElMessage.success('数据库连接成功！')
      connectionSuccess.value = true
    } else {
      ElMessage.error(res.message || '数据库连接失败')
      connectionSuccess.value = false
    }
  } catch (error) {
    ElMessage.error('数据库连接失败：' + (error.message || '网络错误'))
    connectionSuccess.value = false
  } finally {
    testing.value = false
  }
}

// 加载表列表
const handleLoadTables = async () => {
  loadingTables.value = true
  try {
    const res = await getTables({
      dbHost: form.dbHost,
      dbName: form.dbName,
      dbUsername: form.dbUsername,
      dbPassword: form.dbPassword
    })
    
    if (res.code === 200) {
      tables.value = res.data || []
      if (tables.value.length === 0) {
        ElMessage.warning('数据库中没有找到表')
      } else {
        ElMessage.success(`成功加载 ${tables.value.length} 个表`)
      }
    } else {
      ElMessage.error(res.message || '加载表列表失败')
    }
  } catch (error) {
    ElMessage.error('加载表列表失败：' + (error.message || '网络错误'))
  } finally {
    loadingTables.value = false
  }
}

// 生成代码
const handleGenerate = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  generating.value = true
  result.value = ''
  
  try {
    const res = await generateCode(form)
    
    if (res.code === 200) {
      result.value = res.data || '代码生成成功！'
      resultType.value = 'success'
      ElMessage.success('代码生成成功！')
    } else {
      result.value = res.message || '代码生成失败'
      resultType.value = 'error'
      ElMessage.error(res.message || '代码生成失败')
    }
  } catch (error) {
    result.value = '代码生成失败：' + (error.message || '网络错误')
    resultType.value = 'error'
    ElMessage.error('代码生成失败：' + (error.message || '网络错误'))
  } finally {
    generating.value = false
  }
}

// 重置表单
const handleReset = () => {
  formRef.value.resetFields()
  tables.value = []
  connectionSuccess.value = false
  result.value = ''
}
</script>

<style scoped lang="scss">
.codegen-container {
  padding: 20px;
}

.box-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tips {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.result-card {
  animation: slideIn 0.3s ease-in-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

