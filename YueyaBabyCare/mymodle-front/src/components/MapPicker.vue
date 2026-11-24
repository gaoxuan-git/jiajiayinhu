<template>
  <div class="map-picker">
    <!-- 搜索框 -->
    <div class="search-box">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索地址或地点"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 搜索结果列表 -->
    <div v-if="searchResults.length > 0" class="search-results">
      <div
        v-for="item in searchResults"
        :key="item.id"
        class="search-item"
        @click="selectSearchResult(item)"
      >
        <div class="search-item-name">{{ item.name }}</div>
        <div class="search-item-address">{{ item.address }}</div>
      </div>
    </div>

    <!-- 地图容器 -->
    <div ref="mapContainer" class="map-container"></div>

    <!-- 操作按钮 -->
    <div class="map-actions">
      <el-button type="primary" :loading="locating" @click="handleLocate">
        <el-icon><Aim /></el-icon>
        <span>{{ locating ? '定位中...' : '自动定位' }}</span>
      </el-button>
      <span v-if="selectedAddress" class="selected-address">
        已选择: {{ selectedAddress }}
      </span>
    </div>

    <!-- 当前选中的地址信息 -->
    <div v-if="addressInfo.province" class="address-info">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="省份">{{ addressInfo.province }}</el-descriptions-item>
        <el-descriptions-item label="城市">{{ addressInfo.city }}</el-descriptions-item>
        <el-descriptions-item label="区县">{{ addressInfo.district }}</el-descriptions-item>
        <el-descriptions-item label="详细地址">{{ addressInfo.detailAddress }}</el-descriptions-item>
        <el-descriptions-item label="经度">{{ addressInfo.longitude }}</el-descriptions-item>
        <el-descriptions-item label="纬度">{{ addressInfo.latitude }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Aim } from '@element-plus/icons-vue'
import { loadAMap, getAddressByLocation, searchPOI } from '@/utils/amap'

const props = defineProps({
  // 初始经度
  longitude: {
    type: [Number, String],
    default: null
  },
  // 初始纬度
  latitude: {
    type: [Number, String],
    default: null
  },
  // 初始地址
  address: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['select', 'update:longitude', 'update:latitude'])

// 地图相关
const mapContainer = ref(null)
let map = null
let marker = null

// 搜索相关
const searchKeyword = ref('')
const searchResults = ref([])

// 定位状态
const locating = ref(false)
const selectedAddress = ref('')

// 地址信息
const addressInfo = reactive({
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  longitude: null,
  latitude: null
})

// 初始化地图
const initMap = async () => {
  try {
    const AMap = await loadAMap()

    // 默认中心点（北京）
    let center = [116.397428, 39.90923]

    // 如果有初始坐标，使用初始坐标
    if (props.longitude && props.latitude) {
      center = [Number(props.longitude), Number(props.latitude)]
    }

    // 创建地图
    map = new AMap.Map(mapContainer.value, {
      zoom: 15,
      center: center,
      resizeEnable: true
    })

    // 创建标记
    marker = new AMap.Marker({
      position: center,
      draggable: true
    })
    map.add(marker)

    // 如果有初始坐标，获取地址信息
    if (props.longitude && props.latitude) {
      await updateAddressInfo(Number(props.longitude), Number(props.latitude))
    }

    // 标记拖拽结束事件
    marker.on('dragend', async (e) => {
      const position = e.target.getPosition()
      await updateAddressInfo(position.lng, position.lat)
    })

    // 地图点击事件
    map.on('click', async (e) => {
      const lng = e.lnglat.lng
      const lat = e.lnglat.lat

      // 移动标记
      marker.setPosition([lng, lat])

      // 更新地址信息
      await updateAddressInfo(lng, lat)
    })

    // 如果没有初始坐标，自动定位
    if (!props.longitude || !props.latitude) {
      handleLocate()
    }
  } catch (error) {
    console.error('地图初始化失败:', error)
    ElMessage.error('地图加载失败，请刷新页面重试')
  }
}

// 更新地址信息
const updateAddressInfo = async (lng, lat) => {
  try {
    const result = await getAddressByLocation(lng, lat)

    addressInfo.province = result.province
    addressInfo.city = result.city
    addressInfo.district = result.district
    addressInfo.detailAddress = result.detailAddress
    addressInfo.longitude = lng
    addressInfo.latitude = lat

    selectedAddress.value = result.formattedAddress

    // 触发事件
    emit('select', {
      province: result.province,
      city: result.city,
      district: result.district,
      detailAddress: result.detailAddress,
      longitude: lng,
      latitude: lat,
      fullAddress: result.formattedAddress
    })

    emit('update:longitude', lng)
    emit('update:latitude', lat)
  } catch (error) {
    console.error('获取地址信息失败:', error)
    ElMessage.error('获取地址信息失败')
  }
}

// 自动定位
const handleLocate = async () => {
  if (!map) return

  locating.value = true
  try {
    const AMap = await loadAMap()
    const geolocation = new AMap.Geolocation({
      enableHighAccuracy: true,
      timeout: 10000,
      zoomToAccuracy: true
    })

    geolocation.getCurrentPosition((status, result) => {
      locating.value = false

      if (status === 'complete') {
        const lng = result.position.lng
        const lat = result.position.lat

        // 移动地图和标记
        map.setCenter([lng, lat])
        marker.setPosition([lng, lat])

        // 更新地址信息
        updateAddressInfo(lng, lat)

        ElMessage.success('定位成功')
      } else {
        ElMessage.error('定位失败: ' + (result.message || '请检查定位权限'))
      }
    })
  } catch (error) {
    locating.value = false
    console.error('定位失败:', error)
    ElMessage.error('定位失败，请手动选择位置')
  }
}

// 搜索地址
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    searchResults.value = []
    return
  }

  try {
    const results = await searchPOI(searchKeyword.value, addressInfo.city)
    searchResults.value = results
  } catch (error) {
    console.error('搜索失败:', error)
    searchResults.value = []
  }
}

// 选择搜索结果
const selectSearchResult = async (item) => {
  // 移动地图和标记
  map.setCenter([item.lng, item.lat])
  marker.setPosition([item.lng, item.lat])

  // 更新地址信息
  await updateAddressInfo(item.lng, item.lat)

  // 清空搜索结果
  searchResults.value = []
  searchKeyword.value = ''
}

// 监听 props 变化
watch(() => [props.longitude, props.latitude], ([lng, lat]) => {
  if (map && marker && lng && lat) {
    const position = [Number(lng), Number(lat)]
    map.setCenter(position)
    marker.setPosition(position)
  }
})

onMounted(() => {
  initMap()
})

onUnmounted(() => {
  if (map) {
    map.destroy()
    map = null
  }
})
</script>

<style scoped lang="scss">
.map-picker {
  width: 100%;

  .search-box {
    margin-bottom: 10px;
  }

  .search-results {
    position: absolute;
    z-index: 100;
    width: calc(100% - 40px);
    max-height: 200px;
    overflow-y: auto;
    background: #fff;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

    .search-item {
      padding: 10px 15px;
      cursor: pointer;
      border-bottom: 1px solid #ebeef5;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background: #f5f7fa;
      }

      .search-item-name {
        font-size: 14px;
        color: #303133;
        margin-bottom: 4px;
      }

      .search-item-address {
        font-size: 12px;
        color: #909399;
      }
    }
  }

  .map-container {
    width: 100%;
    height: 400px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
  }

  .map-actions {
    margin-top: 10px;
    display: flex;
    align-items: center;
    gap: 15px;

    .selected-address {
      font-size: 13px;
      color: #606266;
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .address-info {
    margin-top: 15px;
  }
}
</style>
