/**
 * 高德地图配置和加载工具
 * 使用 JS API 2.0
 */

// 高德地图配置
const AMAP_CONFIG = {
  key: '2485169badfddc8c22704294c0954f88',
  securityJsCode: 'a773abde427cfa6b967324f2e8373e5a',
  version: '2.0',
  plugins: [
    'AMap.Geolocation',
    'AMap.Geocoder',
    'AMap.PlaceSearch',
    'AMap.AutoComplete',
    'AMap.Marker'
  ]
}

// 是否已加载
let isLoaded = false
let loadPromise = null

/**
 * 设置安全密钥（必须在加载地图前调用）
 */
function setSecurityConfig() {
  window._AMapSecurityConfig = {
    securityJsCode: AMAP_CONFIG.securityJsCode
  }
}

/**
 * 动态加载高德地图 JS API
 * @returns {Promise} 加载完成的 Promise
 */
export function loadAMap() {
  // 如果已经加载，直接返回
  if (isLoaded && window.AMap) {
    return Promise.resolve(window.AMap)
  }

  // 如果正在加载，返回现有的 Promise
  if (loadPromise) {
    return loadPromise
  }

  // 设置安全配置
  setSecurityConfig()

  // 创建加载 Promise
  loadPromise = new Promise((resolve, reject) => {
    // 检查是否已经存在 AMap
    if (window.AMap) {
      isLoaded = true
      resolve(window.AMap)
      return
    }

    // 创建 script 标签
    const script = document.createElement('script')
    script.type = 'text/javascript'
    script.src = `https://webapi.amap.com/maps?v=${AMAP_CONFIG.version}&key=${AMAP_CONFIG.key}&plugin=${AMAP_CONFIG.plugins.join(',')}`
    script.async = true

    script.onload = () => {
      if (window.AMap) {
        isLoaded = true
        resolve(window.AMap)
      } else {
        reject(new Error('高德地图加载失败'))
      }
    }

    script.onerror = () => {
      loadPromise = null
      reject(new Error('高德地图脚本加载失败'))
    }

    document.head.appendChild(script)
  })

  return loadPromise
}

/**
 * 获取当前位置
 * @returns {Promise<{lng: number, lat: number, address: string}>}
 */
export function getCurrentPosition() {
  return new Promise((resolve, reject) => {
    loadAMap().then(AMap => {
      const geolocation = new AMap.Geolocation({
        enableHighAccuracy: true,
        timeout: 10000,
        zoomToAccuracy: true
      })

      geolocation.getCurrentPosition((status, result) => {
        if (status === 'complete') {
          resolve({
            lng: result.position.lng,
            lat: result.position.lat,
            address: result.formattedAddress || ''
          })
        } else {
          reject(new Error(result.message || '定位失败'))
        }
      })
    }).catch(reject)
  })
}

/**
 * 逆地理编码 - 坐标转地址
 * @param {number} lng 经度
 * @param {number} lat 纬度
 * @returns {Promise<Object>} 地址信息
 */
export function getAddressByLocation(lng, lat) {
  return new Promise((resolve, reject) => {
    loadAMap().then(AMap => {
      const geocoder = new AMap.Geocoder()

      geocoder.getAddress([lng, lat], (status, result) => {
        if (status === 'complete' && result.regeocode) {
          const regeocode = result.regeocode
          const addressComponent = regeocode.addressComponent

          resolve({
            province: addressComponent.province || '',
            city: addressComponent.city || addressComponent.province || '',
            district: addressComponent.district || '',
            street: addressComponent.street || '',
            streetNumber: addressComponent.streetNumber || '',
            formattedAddress: regeocode.formattedAddress || '',
            // 详细地址（去掉省市区）
            detailAddress: regeocode.formattedAddress
              .replace(addressComponent.province || '', '')
              .replace(addressComponent.city || '', '')
              .replace(addressComponent.district || '', '')
          })
        } else {
          reject(new Error('逆地理编码失败'))
        }
      })
    }).catch(reject)
  })
}

/**
 * 地理编码 - 地址转坐标
 * @param {string} address 地址
 * @param {string} city 城市（可选，提高精度）
 * @returns {Promise<{lng: number, lat: number}>}
 */
export function getLocationByAddress(address, city = '') {
  return new Promise((resolve, reject) => {
    loadAMap().then(AMap => {
      const geocoder = new AMap.Geocoder({
        city: city
      })

      geocoder.getLocation(address, (status, result) => {
        if (status === 'complete' && result.geocodes.length > 0) {
          const location = result.geocodes[0].location
          resolve({
            lng: location.lng,
            lat: location.lat
          })
        } else {
          reject(new Error('地理编码失败'))
        }
      })
    }).catch(reject)
  })
}

/**
 * POI 搜索
 * @param {string} keyword 关键词
 * @param {string} city 城市
 * @returns {Promise<Array>} POI 列表
 */
export function searchPOI(keyword, city = '') {
  return new Promise((resolve, reject) => {
    loadAMap().then(AMap => {
      const placeSearch = new AMap.PlaceSearch({
        city: city,
        pageSize: 10,
        pageIndex: 1
      })

      placeSearch.search(keyword, (status, result) => {
        if (status === 'complete' && result.poiList) {
          const pois = result.poiList.pois.map(poi => ({
            id: poi.id,
            name: poi.name,
            address: poi.address,
            lng: poi.location.lng,
            lat: poi.location.lat,
            district: poi.adname || ''
          }))
          resolve(pois)
        } else {
          resolve([])
        }
      })
    }).catch(reject)
  })
}

export default {
  loadAMap,
  getCurrentPosition,
  getAddressByLocation,
  getLocationByAddress,
  searchPOI,
  config: AMAP_CONFIG
}
