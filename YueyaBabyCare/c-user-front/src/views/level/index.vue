<template>
  <div class="level-page">
    <div class="level-layout">
      <!-- 当前等级卡片 -->
      <t-card :bordered="false" class="current-level-card">
        <div class="level-header">
          <div class="level-badge">
            <t-icon name="star-filled" size="48" style="color: #fff;" />
          </div>
          <div class="level-info">
            <h2>黄金会员</h2>
            <p>当前积分：1,280</p>
          </div>
        </div>
        <div class="level-progress">
          <div class="progress-labels">
            <span>黄金会员</span>
            <span>铂金会员 (2,000积分)</span>
          </div>
          <t-progress :percentage="64" theme="warning" />
          <p class="progress-tips">还需720积分升级至铂金会员</p>
        </div>
      </t-card>

      <!-- 当前权益 -->
      <t-card :bordered="false" title="我的权益">
        <div class="benefits-grid">
          <div class="benefit-item active">
            <div class="benefit-icon"><t-icon name="discount" size="32" /></div>
            <div class="benefit-name">9.5折优惠</div>
            <div class="benefit-desc">全场通用</div>
          </div>
          <div class="benefit-item active">
            <div class="benefit-icon"><t-icon name="gift" size="32" /></div>
            <div class="benefit-name">1.2倍积分</div>
            <div class="benefit-desc">消费加速</div>
          </div>
          <div class="benefit-item active">
            <div class="benefit-icon"><t-icon name="calendar" size="32" /></div>
            <div class="benefit-name">生日礼遇</div>
            <div class="benefit-desc">专属福利</div>
          </div>
          <div class="benefit-item">
            <div class="benefit-icon"><t-icon name="service" size="32" /></div>
            <div class="benefit-name">专属客服</div>
            <div class="benefit-desc">铂金解锁</div>
          </div>
          <div class="benefit-item">
            <div class="benefit-icon"><t-icon name="heart" size="32" /></div>
            <div class="benefit-name">优先体验</div>
            <div class="benefit-desc">钻石解锁</div>
          </div>
          <div class="benefit-item">
            <div class="benefit-icon"><t-icon name="crown" size="32" /></div>
            <div class="benefit-name">专属活动</div>
            <div class="benefit-desc">钻石解锁</div>
          </div>
        </div>
      </t-card>
    </div>

    <!-- 等级说明 -->
    <t-card :bordered="false" title="等级说明" style="margin-top: 24px;">
      <t-table :data="levels" :columns="levelColumns" row-key="id" hover stripe>
        <template #name="{ row }">
          <div class="level-name-cell">
            <div class="level-dot" :style="{ background: row.color }"></div>
            <span>{{ row.name }}</span>
            <t-tag v-if="row.id === 2" theme="warning" size="small">当前</t-tag>
          </div>
        </template>
      </t-table>
    </t-card>

    <!-- 升级攻略 -->
    <t-card :bordered="false" title="升级攻略" style="margin-top: 24px;">
      <t-list :split="true">
        <t-list-item>
          <t-list-item-meta title="消费获得积分" description="每消费1元获得1积分，会员等级越高积分倍率越高" />
        </t-list-item>
        <t-list-item>
          <t-list-item-meta title="参与活动" description="参与店铺活动可获得额外积分奖励" />
        </t-list-item>
        <t-list-item>
          <t-list-item-meta title="邀请好友" description="邀请好友注册成功可获得奖励积分" />
        </t-list-item>
      </t-list>
    </t-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const levels = ref([
  { id: 1, name: '普通会员', condition: '注册即享', discount: '无折扣', pointsRate: '1倍积分', color: '#999' },
  { id: 2, name: '黄金会员', condition: '累计500积分', discount: '9.5折', pointsRate: '1.2倍积分', color: '#ffd700' },
  { id: 3, name: '铂金会员', condition: '累计2000积分', discount: '9折', pointsRate: '1.5倍积分', color: '#e5e4e2' },
  { id: 4, name: '钻石会员', condition: '累计5000积分', discount: '8.5折', pointsRate: '2倍积分', color: '#b9f2ff' }
])

const levelColumns = [
  { colKey: 'name', title: '等级', width: 200 },
  { colKey: 'condition', title: '升级条件', width: 200 },
  { colKey: 'discount', title: '专属折扣', width: 150 },
  { colKey: 'pointsRate', title: '积分倍率', width: 150 }
]
</script>

<style scoped>
.level-layout { display: grid; grid-template-columns: 1fr 2fr; gap: 24px; }
.current-level-card { background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); color: #fff; }
.level-header { display: flex; align-items: center; gap: 20px; margin-bottom: 24px; }
.level-badge { width: 80px; height: 80px; background: linear-gradient(135deg, #ffd700 0%, #ffb800 100%); border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.level-info h2 { margin: 0 0 8px; font-size: 28px; }
.level-info p { margin: 0; opacity: 0.7; }
.progress-labels { display: flex; justify-content: space-between; font-size: 12px; opacity: 0.7; margin-bottom: 8px; }
.progress-tips { font-size: 12px; opacity: 0.7; margin-top: 8px; }
.benefits-grid { display: grid; grid-template-columns: repeat(6, 1fr); gap: 16px; }
.benefit-item { text-align: center; padding: 20px 12px; border-radius: 8px; background: #f5f5f5; opacity: 0.5; }
.benefit-item.active { opacity: 1; background: #e8f4ff; }
.benefit-icon { margin-bottom: 12px; color: #0052d9; }
.benefit-name { font-size: 14px; font-weight: 500; }
.benefit-desc { font-size: 12px; color: #999; margin-top: 4px; }
.level-name-cell { display: flex; align-items: center; gap: 8px; }
.level-dot { width: 12px; height: 12px; border-radius: 50%; }
</style>
