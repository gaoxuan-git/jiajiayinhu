<template>
  <div class="shops-page">
    <div class="page-header">
      <div class="header-content">
        <h1>门店查询</h1>
        <p>查找您附近的咖啡门店</p>
      </div>
    </div>
    <div class="content-wrapper">
      <t-card :bordered="false">
        <div class="search-bar">
          <t-input v-model="keyword" placeholder="搜索门店名称或地址" style="width: 400px;">
            <template #prefix-icon><t-icon name="search" /></template>
          </t-input>
          <t-select v-model="city" placeholder="选择城市" style="width: 200px;">
            <t-option value="all" label="全部城市" />
            <t-option value="beijing" label="北京" />
            <t-option value="shanghai" label="上海" />
            <t-option value="guangzhou" label="广州" />
            <t-option value="shenzhen" label="深圳" />
          </t-select>
        </div>
        <div class="shops-list">
          <div class="shop-card" v-for="shop in shopList" :key="shop.id">
            <div class="shop-img">
              <img :src="shop.image" />
            </div>
            <div class="shop-info">
              <h3>{{ shop.name }}</h3>
              <p class="shop-address"><t-icon name="location" size="16" /> {{ shop.address }}</p>
              <p class="shop-phone"><t-icon name="call" size="16" /> {{ shop.phone }}</p>
              <p class="shop-time"><t-icon name="time" size="16" /> {{ shop.businessTime }}</p>
              <div class="shop-tags">
                <t-tag v-for="tag in shop.tags" :key="tag" size="small" variant="light">{{ tag }}</t-tag>
              </div>
            </div>
            <div class="shop-action">
              <t-button variant="outline">查看详情</t-button>
              <t-button theme="primary">导航前往</t-button>
            </div>
          </div>
        </div>
      </t-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const keyword = ref('')
const city = ref('all')

const shopList = ref([
  { id: 1, name: '星光店', address: '市中心商业广场B座1层101号', phone: '010-88888801', businessTime: '08:00-22:00', image: 'https://picsum.photos/200/150?random=60', tags: ['WiFi', '停车场', '外卖'] },
  { id: 2, name: '中心店', address: '金融街购物中心3层', phone: '010-88888802', businessTime: '09:00-21:00', image: 'https://picsum.photos/200/150?random=61', tags: ['WiFi', '会议室'] },
  { id: 3, name: '大学城店', address: '大学城商业街A区12号', phone: '010-88888803', businessTime: '07:30-23:00', image: 'https://picsum.photos/200/150?random=62', tags: ['WiFi', '24小时', '外卖'] },
  { id: 4, name: '科技园店', address: '科技园区创新大厦1层', phone: '010-88888804', businessTime: '08:00-20:00', image: 'https://picsum.photos/200/150?random=63', tags: ['WiFi', '停车场'] }
])
</script>

<style scoped>
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60px 0;
  color: #fff;
  text-align: center;
}
.header-content h1 { margin: 0 0 12px; font-size: 36px; }
.header-content p { margin: 0; font-size: 18px; opacity: 0.9; }
.content-wrapper { max-width: 1400px; margin: 0 auto; padding: 24px; }
.search-bar { display: flex; gap: 16px; margin-bottom: 24px; }
.shops-list { display: flex; flex-direction: column; gap: 16px; }
.shop-card { display: flex; gap: 24px; padding: 20px; background: #fafafa; border-radius: 12px; }
.shop-img { width: 200px; height: 150px; border-radius: 8px; overflow: hidden; flex-shrink: 0; }
.shop-img img { width: 100%; height: 100%; object-fit: cover; }
.shop-info { flex: 1; }
.shop-info h3 { margin: 0 0 12px; font-size: 20px; }
.shop-info p { margin: 0 0 8px; color: #666; font-size: 14px; display: flex; align-items: center; gap: 6px; }
.shop-tags { display: flex; gap: 8px; margin-top: 12px; }
.shop-action { display: flex; flex-direction: column; gap: 12px; justify-content: center; }
</style>
