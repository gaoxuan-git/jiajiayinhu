<template>
  <div class="addresses-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1>地址管理</h1>
      <t-button theme="primary" size="large" @click="openAddDialog">
        <t-icon name="add" /> 新增地址
      </t-button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <t-loading />
    </div>

    <!-- 空状态 -->
    <div v-else-if="addresses.length === 0" class="empty-container">
      <t-icon name="location" size="64" />
      <p>暂无收货地址</p>
      <t-button theme="primary" @click="openAddDialog">添加地址</t-button>
    </div>

    <!-- 地址列表 -->
    <div v-else class="addresses-grid">
      <div v-for="addr in addresses" :key="addr.id" class="address-card" :class="{ 'is-default': addr.isDefault === 1 }">
        <div class="card-header">
          <div class="tag-row">
            <t-tag v-if="addr.isDefault === 1" theme="success" size="small">默认地址</t-tag>
          </div>
          <div class="actions">
            <t-button variant="text" theme="primary" size="small" @click="openEditDialog(addr)">
              <t-icon name="edit" /> 编辑
            </t-button>
            <t-popconfirm content="确定删除该地址吗？" @confirm="handleDelete(addr.id)">
              <t-button variant="text" theme="danger" size="small">
                <t-icon name="delete" /> 删除
              </t-button>
            </t-popconfirm>
          </div>
        </div>
        <div class="card-body">
          <div class="contact-info">
            <span class="name">{{ addr.contactName }}</span>
            <span class="phone">{{ addr.contactPhone }}</span>
          </div>
          <p class="address-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}</p>
        </div>
        <div class="card-footer" v-if="addr.isDefault !== 1">
          <t-button variant="outline" size="small" @click="handleSetDefault(addr.id)">设为默认</t-button>
        </div>
      </div>

      <!-- 添加地址卡片 -->
      <div class="address-card add-card" @click="openAddDialog">
        <div class="add-content">
          <t-icon name="add" size="48" />
          <p>添加新地址</p>
        </div>
      </div>
    </div>

    <!-- 新增/编辑地址弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="isEdit ? '编辑地址' : '新增地址'"
      :footer="false"
      width="700px"
      destroy-on-close
    >
      <t-form
        ref="formRef"
        :data="formData"
        :rules="formRules"
        label-width="100px"
        @submit="handleSubmit"
      >
        <t-form-item label="联系人" name="contactName">
          <t-input v-model="formData.contactName" placeholder="请输入联系人姓名" />
        </t-form-item>
        <t-form-item label="联系电话" name="contactPhone">
          <t-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
        </t-form-item>
        <t-form-item label="所在地区" name="province">
          <div class="region-select">
            <t-select v-model="formData.province" placeholder="省份" @change="handleProvinceChange" filterable>
              <t-option v-for="p in provinces" :key="p.adcode" :value="p.name" :label="p.name" />
            </t-select>
            <t-select v-model="formData.city" placeholder="城市" @change="handleCityChange" filterable>
              <t-option v-for="c in cities" :key="c.adcode" :value="c.name" :label="c.name" />
            </t-select>
            <t-select v-model="formData.district" placeholder="区县" filterable>
              <t-option v-for="d in districts" :key="d.adcode" :value="d.name" :label="d.name" />
            </t-select>
          </div>
        </t-form-item>
        <t-form-item label="详细地址" name="detailAddress">
          <t-textarea v-model="formData.detailAddress" placeholder="请输入详细地址（街道、门牌号等）" :maxlength="200" />
        </t-form-item>
        <t-form-item label="地图选点">
          <div class="map-container">
            <div id="map-container" class="map-box"></div>
            <div class="map-search">
              <t-input v-model="searchKeyword" placeholder="搜索地址" @enter="handleSearchAddress">
                <template #suffix-icon>
                  <t-icon name="search" @click="handleSearchAddress" style="cursor: pointer;" />
                </template>
              </t-input>
            </div>
            <div class="map-actions">
              <t-button size="small" variant="outline" @click="getCurrentLocation" :loading="locating">
                <t-icon name="location" /> 定位当前位置
              </t-button>
            </div>
            <div v-if="formData.longitude && formData.latitude" class="location-info">
              <t-icon name="location" /> 已选择位置：{{ formData.longitude.toFixed(6) }}, {{ formData.latitude.toFixed(6) }}
            </div>
          </div>
        </t-form-item>
        <t-form-item label="设为默认">
          <t-switch v-model="formData.isDefault" :custom-value="[1, 0]" />
        </t-form-item>
        <t-form-item>
          <div class="form-actions">
            <t-button theme="default" @click="dialogVisible = false">取消</t-button>
            <t-button theme="primary" type="submit" :loading="submitting">{{ isEdit ? '保存' : '添加' }}</t-button>
          </div>
        </t-form-item>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup>
/* global AMap */
import { ref, reactive, onMounted, nextTick } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address'

// 高德地图配置
const AMAP_KEY = '2485169badfddc8c22704294c0954f88'
const AMAP_SECRET = 'a773abde427cfa6b967324f2e8373e5a'

// 状态
const loading = ref(false)
const addresses = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitting = ref(false)
const formRef = ref(null)
const searchKeyword = ref('')
const locating = ref(false)

// 地图相关
let map = null
let marker = null
let geocoder = null
let placeSearch = null
let districtSearch = null
let geolocation = null

// 省市区数据
const provinces = ref([])
const cities = ref([])
const districts = ref([])

// 表单数据
const formData = reactive({
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
const formRules = {
  contactName: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  province: [{ required: true, message: '请选择省份', trigger: 'change' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

// 加载地址列表
const loadAddresses = async () => {
  loading.value = true
  try {
    const res = await getAddressList()
    addresses.value = res.data || []
  } catch (error) {
    console.error('加载地址列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 打开新增弹窗
const openAddDialog = () => {
  isEdit.value = false
  editId.value = null
  resetForm()
  dialogVisible.value = true
  nextTick(() => {
    initMap()
  })
}

// 打开编辑弹窗
const openEditDialog = (addr) => {
  isEdit.value = true
  editId.value = addr.id
  Object.assign(formData, {
    contactName: addr.contactName,
    contactPhone: addr.contactPhone,
    province: addr.province,
    city: addr.city,
    district: addr.district,
    detailAddress: addr.detailAddress,
    longitude: addr.longitude,
    latitude: addr.latitude,
    isDefault: addr.isDefault
  })
  dialogVisible.value = true
  nextTick(() => {
    initMap()
    if (addr.longitude && addr.latitude) {
      setTimeout(() => setMapCenter(addr.longitude, addr.latitude), 500)
    }
  })
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
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
  cities.value = []
  districts.value = []
}

// 初始化地图
const initMap = () => {
  if (typeof AMap === 'undefined') {
    loadAmapScript().then(() => createMap())
  } else {
    createMap()
  }
}

// 加载高德地图脚本
const loadAmapScript = () => {
  return new Promise((resolve, reject) => {
    if (typeof AMap !== 'undefined') {
      resolve()
      return
    }
    window._AMapSecurityConfig = { securityJsCode: AMAP_SECRET }
    const script = document.createElement('script')
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${AMAP_KEY}&plugin=AMap.Geocoder,AMap.PlaceSearch,AMap.DistrictSearch,AMap.Geolocation`
    script.onload = resolve
    script.onerror = reject
    document.head.appendChild(script)
  })
}

// 创建地图
const createMap = () => {
  const container = document.getElementById('map-container')
  if (!container) return

  map = new AMap.Map('map-container', {
    zoom: 13,
    center: [116.397428, 39.90923]
  })

  geocoder = new AMap.Geocoder()
  placeSearch = new AMap.PlaceSearch({ map: map })
  districtSearch = new AMap.DistrictSearch({ level: 'country', subdistrict: 1 })

  // 初始化定位组件
  geolocation = new AMap.Geolocation({
    enableHighAccuracy: true, // 是否使用高精度定位
    timeout: 10000, // 超时时间
    buttonPosition: 'RB', // 定位按钮位置
    zoomToAccuracy: true, // 定位成功后是否自动调整地图视野
    showMarker: false, // 不显示默认标记，我们自己处理
    showCircle: false // 不显示精度圈
  })
  map.addControl(geolocation)

  // 加载省份数据
  loadProvinces()

  // 地图点击事件
  map.on('click', (e) => {
    const lng = e.lnglat.getLng()
    const lat = e.lnglat.getLat()
    setMarker(lng, lat)
    reverseGeocode(lng, lat)
  })

  // 如果是新增地址，自动定位
  if (!isEdit.value) {
    setTimeout(() => getCurrentLocation(), 500)
  }
}

// 加载省份数据
const loadProvinces = () => {
  districtSearch.search('中国', (status, result) => {
    if (status === 'complete') {
      provinces.value = result.districtList[0].districtList || []
    }
  })
}

// 省份变化
const handleProvinceChange = (val) => {
  formData.city = ''
  formData.district = ''
  cities.value = []
  districts.value = []
  if (val) {
    districtSearch.search(val, (status, result) => {
      if (status === 'complete') {
        cities.value = result.districtList[0].districtList || []
      }
    })
  }
}

// 城市变化
const handleCityChange = (val) => {
  formData.district = ''
  districts.value = []
  if (val) {
    districtSearch.search(val, (status, result) => {
      if (status === 'complete') {
        districts.value = result.districtList[0].districtList || []
      }
    })
  }
}

// 设置地图中心
const setMapCenter = (lng, lat) => {
  if (map) {
    map.setCenter([lng, lat])
    setMarker(lng, lat)
  }
}

// 设置标记点
const setMarker = (lng, lat) => {
  formData.longitude = lng
  formData.latitude = lat
  if (marker) {
    marker.setPosition([lng, lat])
  } else {
    marker = new AMap.Marker({ position: [lng, lat], map: map })
  }
}

// 逆地理编码
const reverseGeocode = (lng, lat) => {
  geocoder.getAddress([lng, lat], (status, result) => {
    if (status === 'complete' && result.regeocode) {
      const addr = result.regeocode.addressComponent
      const province = addr.province || ''
      const city = addr.city || addr.province || ''
      const district = addr.district || ''

      // 设置详细地址
      formData.detailAddress = result.regeocode.formattedAddress.replace(province + city + district, '')

      // 设置省份
      formData.province = province

      // 加载城市列表，完成后设置城市值
      if (province) {
        districtSearch.setLevel('province')
        districtSearch.setSubdistrict(1)
        districtSearch.search(province, (status1, result1) => {
          if (status1 === 'complete' && result1.districtList && result1.districtList.length > 0) {
            cities.value = result1.districtList[0].districtList || []
            formData.city = city

            // 加载区县列表，完成后设置区县值
            if (city) {
              districtSearch.setLevel('city')
              districtSearch.setSubdistrict(1)
              districtSearch.search(city, (status2, result2) => {
                if (status2 === 'complete' && result2.districtList && result2.districtList.length > 0) {
                  districts.value = result2.districtList[0].districtList || []
                  formData.district = district
                }
              })
            }
          }
        })
      }
    }
  })
}

// 搜索地址
const handleSearchAddress = () => {
  if (!searchKeyword.value) return
  placeSearch.search(searchKeyword.value, (status, result) => {
    if (status === 'complete' && result.poiList.pois.length > 0) {
      const poi = result.poiList.pois[0]
      const lng = poi.location.getLng()
      const lat = poi.location.getLat()
      setMapCenter(lng, lat)
      reverseGeocode(lng, lat)
    }
  })
}

// 获取当前位置（自动定位）
const getCurrentLocation = () => {
  if (!geolocation) {
    MessagePlugin.warning('定位组件未初始化')
    return
  }
  locating.value = true
  geolocation.getCurrentPosition((status, result) => {
    locating.value = false
    if (status === 'complete') {
      const lng = result.position.getLng()
      const lat = result.position.getLat()
      setMapCenter(lng, lat)
      reverseGeocode(lng, lat)
      MessagePlugin.success('定位成功')
    } else {
      console.error('定位失败:', result)
      MessagePlugin.warning('定位失败，请手动选择位置或检查浏览器定位权限')
    }
  })
}

// 提交表单
const handleSubmit = async ({ validateResult }) => {
  if (validateResult !== true) return
  submitting.value = true
  try {
    const data = { ...formData }
    if (isEdit.value) {
      await updateAddress(editId.value, data)
      MessagePlugin.success('修改成功')
    } else {
      await addAddress(data)
      MessagePlugin.success('添加成功')
    }
    dialogVisible.value = false
    loadAddresses()
  } catch (error) {
    console.error('保存地址失败:', error)
  } finally {
    submitting.value = false
  }
}

// 删除地址
const handleDelete = async (id) => {
  try {
    await deleteAddress(id)
    MessagePlugin.success('删除成功')
    loadAddresses()
  } catch (error) {
    console.error('删除地址失败:', error)
  }
}

// 设置默认地址
const handleSetDefault = async (id) => {
  try {
    await setDefaultAddress(id)
    MessagePlugin.success('设置成功')
    loadAddresses()
  } catch (error) {
    console.error('设置默认地址失败:', error)
  }
}

onMounted(() => {
  loadAddresses()
})
</script>

<style scoped>
.addresses-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.page-header h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #1F2937;
}

/* 加载和空状态 */
.loading-container,
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  color: #9CA3AF;
}

.empty-container p {
  margin: 16px 0 24px;
  font-size: 16px;
}

/* 地址网格 */
.addresses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
}

.address-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
  border: 2px solid transparent;
}

.address-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.address-card.is-default {
  border-color: #3B82F6;
  background: linear-gradient(135deg, #EFF6FF 0%, #FFFFFF 100%);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.tag-row {
  display: flex;
  gap: 8px;
  align-items: center;
  min-height: 24px;
}

.actions {
  display: flex;
  gap: 4px;
}

.card-body {
  margin-bottom: 16px;
}

.contact-info {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.contact-info .name {
  font-size: 18px;
  font-weight: 600;
  color: #1F2937;
}

.contact-info .phone {
  font-size: 16px;
  color: #6B7280;
}

.address-detail {
  margin: 0;
  font-size: 14px;
  color: #4B5563;
  line-height: 1.6;
}

.card-footer {
  padding-top: 16px;
  border-top: 1px solid #E5E7EB;
}

/* 添加卡片 */
.add-card {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  border: 2px dashed #D1D5DB;
  cursor: pointer;
  background: #F9FAFB;
}

.add-card:hover {
  border-color: #3B82F6;
  background: #EFF6FF;
}

.add-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #6B7280;
}

.add-content p {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.add-card:hover .add-content {
  color: #3B82F6;
}

/* 表单样式 */
.region-select {
  display: flex;
  gap: 12px;
  width: 100%;
}

.region-select .t-select {
  flex: 1;
}

/* 地图容器 */
.map-container {
  width: 100%;
  position: relative;
}

.map-box {
  width: 100%;
  height: 300px;
  border-radius: 8px;
  border: 1px solid #E5E7EB;
}

.map-search {
  position: absolute;
  top: 10px;
  left: 10px;
  width: 280px;
  z-index: 100;
}

.map-actions {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 100;
}

.map-actions .t-button {
  background: white;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

.location-info {
  margin-top: 8px;
  padding: 8px 12px;
  background: #F3F4F6;
  border-radius: 6px;
  font-size: 13px;
  color: #6B7280;
  display: flex;
  align-items: center;
  gap: 6px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  width: 100%;
}

/* 响应式 */
@media (max-width: 768px) {
  .addresses-page {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .addresses-grid {
    grid-template-columns: 1fr;
  }

  .region-select {
    flex-direction: column;
  }

  .map-search {
    width: calc(100% - 20px);
  }
}
</style>
