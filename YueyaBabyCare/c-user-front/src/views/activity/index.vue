<template>
  <div class="activity-page">
    <div class="page-header">
      <div class="header-content">
        <h1>优惠活动</h1>
        <p>精彩活动，惊喜不断</p>
      </div>
    </div>
    <div class="content-wrapper">
      <!-- 热门活动 -->
      <t-card :bordered="false" title="热门活动" class="section-card">
        <div class="hot-activities">
          <div class="hot-item" v-for="item in hotActivities" :key="item.id">
            <img :src="item.image" class="hot-img" />
            <div class="hot-info">
              <t-tag :theme="item.status === '进行中' ? 'success' : 'default'" size="small">{{ item.status }}</t-tag>
              <h3>{{ item.title }}</h3>
              <p>{{ item.desc }}</p>
              <div class="hot-time">{{ item.time }}</div>
              <t-button theme="primary" :disabled="item.status !== '进行中'">立即参与</t-button>
            </div>
          </div>
        </div>
      </t-card>

      <!-- 会员专享 -->
      <t-card :bordered="false" title="会员专享" class="section-card">
        <div class="member-activities">
          <div class="member-item" v-for="item in memberActivities" :key="item.id">
            <div class="member-icon" :style="{ background: item.color }">
              <t-icon :name="item.icon" size="32" style="color: #fff;" />
            </div>
            <div class="member-info">
              <h4>{{ item.title }}</h4>
              <p>{{ item.desc }}</p>
            </div>
            <t-button variant="outline" size="small">了解详情</t-button>
          </div>
        </div>
      </t-card>

      <!-- 优惠券中心 -->
      <t-card :bordered="false" title="优惠券中心" class="section-card">
        <div class="coupon-list">
          <div class="coupon-item" v-for="item in coupons" :key="item.id">
            <div class="coupon-left" :style="{ background: item.color }">
              <div class="coupon-value">¥{{ item.value }}</div>
              <div class="coupon-condition">满{{ item.condition }}可用</div>
            </div>
            <div class="coupon-right">
              <div class="coupon-name">{{ item.name }}</div>
              <div class="coupon-time">{{ item.time }}</div>
              <t-button theme="primary" size="small" :disabled="!item.canGet">{{ item.canGet ? '立即领取' : '已领取' }}</t-button>
            </div>
          </div>
        </div>
      </t-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const hotActivities = ref([
  { id: 1, title: '新会员专享礼', desc: '新注册会员即送100积分，首单立减10元，开启您的咖啡之旅', time: '长期有效', status: '进行中', image: 'https://picsum.photos/400/200?random=70' },
  { id: 2, title: '周三双倍积分日', desc: '每周三全场消费享双倍积分，积分累计更快，升级更轻松', time: '每周三', status: '进行中', image: 'https://picsum.photos/400/200?random=71' },
  { id: 3, title: '圣诞狂欢季', desc: '圣诞期间消费满100元送限定马克杯一个，数量有限先到先得', time: '2025.12.20-12.31', status: '即将开始', image: 'https://picsum.photos/400/200?random=72' }
])

const memberActivities = ref([
  { id: 1, title: '生日礼遇', desc: '生日当月可领取免费饮品券一张', icon: 'gift', color: '#e34d59' },
  { id: 2, title: '升级礼包', desc: '会员等级升级即送专属礼品', icon: 'star', color: '#ed7b2f' },
  { id: 3, title: '邀请有礼', desc: '邀请好友注册双方各得50积分', icon: 'user-add', color: '#0052d9' },
  { id: 4, title: '积分兑换', desc: '积分可兑换饮品、周边等好礼', icon: 'swap', color: '#00a870' }
])

const coupons = ref([
  { id: 1, name: '新人专享券', value: 10, condition: 30, time: '领取后7天内有效', color: '#e34d59', canGet: true },
  { id: 2, name: '满减优惠券', value: 15, condition: 50, time: '领取后30天内有效', color: '#ed7b2f', canGet: true },
  { id: 3, name: '会员专属券', value: 20, condition: 80, time: '领取后30天内有效', color: '#0052d9', canGet: false }
])
</script>

<style scoped>
.page-header {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  padding: 60px 0;
  color: #fff;
  text-align: center;
}
.header-content h1 { margin: 0 0 12px; font-size: 36px; }
.header-content p { margin: 0; font-size: 18px; opacity: 0.9; }
.content-wrapper { max-width: 1400px; margin: 0 auto; padding: 24px; }
.section-card { margin-bottom: 24px; }
.hot-activities { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
.hot-item { border-radius: 12px; overflow: hidden; background: #fff; border: 1px solid #eee; }
.hot-img { width: 100%; height: 160px; object-fit: cover; }
.hot-info { padding: 16px; }
.hot-info h3 { margin: 8px 0; font-size: 18px; }
.hot-info p { margin: 0 0 12px; font-size: 14px; color: #666; line-height: 1.6; }
.hot-time { font-size: 12px; color: #999; margin-bottom: 12px; }
.member-activities { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.member-item { display: flex; flex-direction: column; align-items: center; text-align: center; padding: 24px 16px; background: #fafafa; border-radius: 12px; }
.member-icon { width: 64px; height: 64px; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-bottom: 16px; }
.member-info h4 { margin: 0 0 8px; }
.member-info p { margin: 0 0 16px; font-size: 14px; color: #666; }
.coupon-list { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.coupon-item { display: flex; border-radius: 12px; overflow: hidden; background: #fff; border: 1px solid #eee; }
.coupon-left { width: 120px; padding: 20px; color: #fff; text-align: center; display: flex; flex-direction: column; justify-content: center; }
.coupon-value { font-size: 32px; font-weight: 600; }
.coupon-condition { font-size: 12px; opacity: 0.9; }
.coupon-right { flex: 1; padding: 16px; display: flex; flex-direction: column; justify-content: center; }
.coupon-name { font-size: 16px; font-weight: 500; margin-bottom: 4px; }
.coupon-time { font-size: 12px; color: #999; margin-bottom: 12px; }
</style>
