<template>
  <div class="about-page">
    <div class="page-header">
      <div class="header-content">
        <h1>关于我们</h1>
        <p>品质生活，从一杯咖啡开始</p>
      </div>
    </div>
    <div class="content-wrapper">
      <!-- 品牌故事 -->
      <t-card :bordered="false" class="section-card">
        <div class="brand-section">
          <div class="brand-content">
            <h2>品牌故事</h2>
            <p>咖啡会员中心成立于2020年，致力于为每一位咖啡爱好者提供优质的咖啡体验和贴心的会员服务。</p>
            <p>我们精选全球优质咖啡豆，由专业烘焙师精心烘焙，确保每一杯咖啡都能带给您最纯正的风味。</p>
            <p>我们相信，一杯好咖啡不仅是味蕾的享受，更是生活品质的体现。</p>
          </div>
          <img src="https://picsum.photos/500/300?random=80" class="brand-img" />
        </div>
      </t-card>

      <!-- 我们的优势 -->
      <t-card :bordered="false" title="我们的优势" class="section-card">
        <div class="advantage-grid">
          <div class="advantage-item">
            <div class="advantage-icon"><t-icon name="verified" size="40" /></div>
            <h4>品质保证</h4>
            <p>精选全球优质咖啡豆，专业烘焙工艺</p>
          </div>
          <div class="advantage-item">
            <div class="advantage-icon"><t-icon name="heart" size="40" /></div>
            <h4>贴心服务</h4>
            <p>专业咖啡师团队，为您定制专属口味</p>
          </div>
          <div class="advantage-item">
            <div class="advantage-icon"><t-icon name="gift" size="40" /></div>
            <h4>会员福利</h4>
            <p>丰富的会员权益，积分兑换好礼</p>
          </div>
          <div class="advantage-item">
            <div class="advantage-icon"><t-icon name="location" size="40" /></div>
            <h4>便捷门店</h4>
            <p>覆盖全国主要城市，就近享受服务</p>
          </div>
        </div>
      </t-card>

      <!-- 联系我们 -->
      <t-card :bordered="false" title="联系我们" class="section-card">
        <div class="contact-section">
          <div class="contact-info">
            <div class="contact-item">
              <t-icon name="call" size="24" />
              <div>
                <h4>客服热线</h4>
                <p>400-888-8888</p>
              </div>
            </div>
            <div class="contact-item">
              <t-icon name="mail" size="24" />
              <div>
                <h4>电子邮箱</h4>
                <p>service@coffee.com</p>
              </div>
            </div>
            <div class="contact-item">
              <t-icon name="time" size="24" />
              <div>
                <h4>服务时间</h4>
                <p>周一至周日 9:00-21:00</p>
              </div>
            </div>
            <div class="contact-item">
              <t-icon name="location" size="24" />
              <div>
                <h4>总部地址</h4>
                <p>北京市朝阳区xxx大厦</p>
              </div>
            </div>
          </div>
          <div class="contact-form">
            <h4>意见反馈</h4>
            <t-form label-width="0">
              <t-form-item>
                <t-input placeholder="您的姓名" />
              </t-form-item>
              <t-form-item>
                <t-input placeholder="联系方式" />
              </t-form-item>
              <t-form-item>
                <t-textarea placeholder="请输入您的意见或建议" :rows="4" />
              </t-form-item>
              <t-form-item>
                <t-button theme="primary" block>提交反馈</t-button>
              </t-form-item>
            </t-form>
          </div>
        </div>
      </t-card>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.page-header {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  padding: 60px 0;
  color: #fff;
  text-align: center;
}
.header-content h1 { margin: 0 0 12px; font-size: 36px; }
.header-content p { margin: 0; font-size: 18px; opacity: 0.9; }
.content-wrapper { max-width: 1400px; margin: 0 auto; padding: 24px; }
.section-card { margin-bottom: 24px; }
.brand-section { display: flex; gap: 48px; align-items: center; }
.brand-content { flex: 1; }
.brand-content h2 { margin: 0 0 24px; font-size: 28px; }
.brand-content p { margin: 0 0 16px; font-size: 16px; color: #666; line-height: 1.8; }
.brand-img { width: 500px; border-radius: 12px; }
.advantage-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; }
.advantage-item { text-align: center; padding: 32px 24px; background: #fafafa; border-radius: 12px; }
.advantage-icon { color: #0052d9; margin-bottom: 16px; }
.advantage-item h4 { margin: 0 0 8px; font-size: 18px; }
.advantage-item p { margin: 0; font-size: 14px; color: #666; }
.contact-section { display: grid; grid-template-columns: 1fr 1fr; gap: 48px; }
.contact-info { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
.contact-item { display: flex; gap: 16px; padding: 20px; background: #fafafa; border-radius: 12px; }
.contact-item h4 { margin: 0 0 4px; font-size: 14px; color: #999; }
.contact-item p { margin: 0; font-size: 16px; }
.contact-form h4 { margin: 0 0 16px; }
</style>
