<template>
  <div class="points-page">
    <div class="top-section">
      <!-- 积分卡片 -->
      <t-card :bordered="false" class="points-card">
        <div class="points-header">
          <div class="points-info">
            <div class="points-label">当前积分</div>
            <div class="points-value">1,280</div>
            <div class="points-tips">积分可在消费时抵扣现金，10积分=1元</div>
          </div>
          <t-button theme="primary" @click="showRule = true">积分规则</t-button>
        </div>
      </t-card>
      <!-- 积分统计 -->
      <t-card :bordered="false" class="stats-card">
        <div class="stats-grid">
          <div class="stats-item">
            <div class="stats-value">2,580</div>
            <div class="stats-label">累计获得</div>
          </div>
          <t-divider layout="vertical" />
          <div class="stats-item">
            <div class="stats-value">1,300</div>
            <div class="stats-label">累计使用</div>
          </div>
          <t-divider layout="vertical" />
          <div class="stats-item">
            <div class="stats-value">0</div>
            <div class="stats-label">即将过期</div>
          </div>
        </div>
      </t-card>
    </div>

    <!-- 积分明细 -->
    <t-card :bordered="false">
      <template #header>
        <div class="card-header">
          <h3>积分明细</h3>
          <t-radio-group v-model="filterType" variant="default-filled">
            <t-radio-button value="all">全部</t-radio-button>
            <t-radio-button value="earn">获得</t-radio-button>
            <t-radio-button value="use">使用</t-radio-button>
          </t-radio-group>
        </div>
      </template>
      <t-table :data="filteredRecords" :columns="columns" row-key="id" hover stripe>
        <template #points="{ row }">
          <span :style="{ color: row.type === 'earn' ? '#00a870' : '#e34d59', fontWeight: 600 }">
            {{ row.type === 'earn' ? '+' : '-' }}{{ row.points }}
          </span>
        </template>
      </t-table>
    </t-card>

    <!-- 积分规则弹窗 -->
    <t-dialog v-model:visible="showRule" header="积分规则" :footer="false" width="500px">
      <div class="rule-content">
        <h4>如何获得积分</h4>
        <p>1. 消费获得：每消费1元可获得1积分</p>
        <p>2. 会员倍率：不同等级会员享有不同积分倍率</p>
        <p>3. 活动奖励：参与活动可获得额外积分</p>
        <h4>如何使用积分</h4>
        <p>1. 消费抵扣：10积分可抵扣1元</p>
        <p>2. 积分兑换：可在积分商城兑换礼品</p>
        <h4>积分有效期</h4>
        <p>积分自获得之日起，有效期为1年</p>
      </div>
    </t-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const showRule = ref(false)
const filterType = ref('all')

const records = ref([
  { id: 1, type: 'earn', title: '消费获得', points: 56, time: '2025-11-28 10:30', remark: '星光店消费' },
  { id: 2, type: 'use', title: '消费抵扣', points: 50, time: '2025-11-26 14:20', remark: '中心店消费抵扣' },
  { id: 3, type: 'earn', title: '消费获得', points: 32, time: '2025-11-26 14:20', remark: '中心店消费' },
  { id: 4, type: 'earn', title: '消费获得', points: 88, time: '2025-11-24 09:15', remark: '星光店消费' },
  { id: 5, type: 'use', title: '消费抵扣', points: 100, time: '2025-11-20 16:45', remark: '大学城店消费抵扣' },
  { id: 6, type: 'earn', title: '消费获得', points: 45, time: '2025-11-20 16:45', remark: '大学城店消费' },
  { id: 7, type: 'earn', title: '注册奖励', points: 100, time: '2025-11-01 10:00', remark: '新会员注册奖励' }
])

const filteredRecords = computed(() => {
  if (filterType.value === 'all') return records.value
  return records.value.filter(r => r.type === filterType.value)
})

const columns = [
  { colKey: 'title', title: '类型', width: 120 },
  { colKey: 'points', title: '积分', width: 100 },
  { colKey: 'time', title: '时间', width: 180 },
  { colKey: 'remark', title: '备注' }
]
</script>

<style scoped>
.points-page { display: flex; flex-direction: column; gap: 24px; }
.top-section { display: grid; grid-template-columns: 2fr 1fr; gap: 24px; }
.points-card { background: linear-gradient(135deg, #0052d9 0%, #0066ff 100%); color: #fff; }
.points-header { display: flex; justify-content: space-between; align-items: flex-start; }
.points-label { font-size: 14px; opacity: 0.8; }
.points-value { font-size: 48px; font-weight: 600; margin: 8px 0; }
.points-tips { font-size: 12px; opacity: 0.7; }
.stats-grid { display: flex; justify-content: space-around; padding: 16px 0; }
.stats-item { text-align: center; }
.stats-value { font-size: 24px; font-weight: 600; color: #333; }
.stats-label { font-size: 14px; color: #999; margin-top: 4px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-header h3 { margin: 0; }
.rule-content h4 { margin: 16px 0 8px; color: #333; }
.rule-content h4:first-child { margin-top: 0; }
.rule-content p { margin: 4px 0; color: #666; font-size: 14px; }
</style>
