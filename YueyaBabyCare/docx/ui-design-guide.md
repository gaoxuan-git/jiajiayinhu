# 悦芽婴护管理系统 UI设计规范

## 一、设计理念
- **简洁**：去除冗余装饰，突出核心内容
- **高级**：低饱和度配色，营造专业可信赖感
- **层次**：通过色彩深浅、阴影、间距构建视觉层级
- **温馨**：母婴主题，传递安全、舒适的情感

---

## 二、色彩系统

### 2.1 主色调（Primary）
用于品牌标识、主要按钮、重要操作

| 色值 | Tailwind类名 | 用途 |
|------|-------------|------|
| #3B82F6 | `bg-blue-500` | 主色 |
| #2563EB | `bg-blue-600` | 主色悬停 |
| #1D4ED8 | `bg-blue-700` | 主色激活 |
| #DBEAFE | `bg-blue-100` | 主色浅底 |
| #EFF6FF | `bg-blue-50` | 主色极浅底 |

### 2.2 辅助色（Secondary）
用于次要按钮、标签、辅助信息

| 色值 | Tailwind类名 | 用途 |
|------|-------------|------|
| #10B981 | `bg-emerald-500` | 成功/健康 |
| #F59E0B | `bg-amber-500` | 警告/提醒 |
| #EF4444 | `bg-red-500` | 错误/危险 |
| #6B7280 | `bg-gray-500` | 中性/禁用 |

### 2.3 中性色（Neutral）
用于背景、边框、文字

| 色值 | Tailwind类名 | 用途 |
|------|-------------|------|
| #FFFFFF | `bg-white` | 卡片背景 |
| #F9FAFB | `bg-gray-50` | 页面背景 |
| #F3F4F6 | `bg-gray-100` | 区块背景 |
| #E5E7EB | `bg-gray-200` | 分割线/边框 |
| #D1D5DB | `bg-gray-300` | 禁用边框 |
| #9CA3AF | `bg-gray-400` | 占位符文字 |
| #6B7280 | `bg-gray-500` | 次要文字 |
| #4B5563 | `bg-gray-600` | 正文文字 |
| #374151 | `bg-gray-700` | 重要文字 |
| #1F2937 | `bg-gray-800` | 标题文字 |
| #111827 | `bg-gray-900` | 深色标题 |

### 2.4 功能色（Semantic）

| 场景 | 背景色 | 文字色 | 边框色 |
|------|--------|--------|--------|
| 成功 | `bg-emerald-50` | `text-emerald-700` | `border-emerald-200` |
| 警告 | `bg-amber-50` | `text-amber-700` | `border-amber-200` |
| 错误 | `bg-red-50` | `text-red-700` | `border-red-200` |
| 信息 | `bg-blue-50` | `text-blue-700` | `border-blue-200` |

---

## 三、文字规范

### 3.1 字体
```css
font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
```
Tailwind配置：`font-sans`

### 3.2 字号层级

| 层级 | Tailwind类名 | 像素 | 用途 |
|------|-------------|------|------|
| H1 | `text-2xl` | 24px | 页面主标题 |
| H2 | `text-xl` | 20px | 卡片标题 |
| H3 | `text-lg` | 18px | 区块标题 |
| Body | `text-base` | 16px | 正文内容 |
| Small | `text-sm` | 14px | 辅助文字 |
| Mini | `text-xs` | 12px | 标签/提示 |

### 3.3 字重

| 类型 | Tailwind类名 | 用途 |
|------|-------------|------|
| 常规 | `font-normal` | 正文 |
| 中等 | `font-medium` | 强调文字 |
| 加粗 | `font-semibold` | 标题 |
| 粗体 | `font-bold` | 重要数据 |

### 3.4 行高

| 类型 | Tailwind类名 | 用途 |
|------|-------------|------|
| 紧凑 | `leading-tight` | 标题 |
| 正常 | `leading-normal` | 短文本 |
| 宽松 | `leading-relaxed` | 长段落 |

---

## 四、间距规范

### 4.1 内边距（Padding）

| 场景 | Tailwind类名 | 数值 |
|------|-------------|------|
| 按钮-小 | `px-3 py-1.5` | 12px 6px |
| 按钮-中 | `px-4 py-2` | 16px 8px |
| 按钮-大 | `px-6 py-3` | 24px 12px |
| 卡片 | `p-4` 或 `p-6` | 16px 或 24px |
| 表单项 | `px-3 py-2` | 12px 8px |

### 4.2 外边距（Margin）

| 场景 | Tailwind类名 | 数值 |
|------|-------------|------|
| 元素间距-小 | `gap-2` | 8px |
| 元素间距-中 | `gap-4` | 16px |
| 元素间距-大 | `gap-6` | 24px |
| 区块间距 | `mb-6` 或 `mb-8` | 24px 或 32px |

---

## 五、圆角规范

| 场景 | Tailwind类名 | 数值 |
|------|-------------|------|
| 按钮 | `rounded-lg` | 8px |
| 卡片 | `rounded-xl` | 12px |
| 输入框 | `rounded-md` | 6px |
| 标签 | `rounded-full` | 全圆角 |
| 头像 | `rounded-full` | 圆形 |
| 弹窗 | `rounded-2xl` | 16px |

---

## 六、阴影规范

| 层级 | Tailwind类名 | 用途 |
|------|-------------|------|
| 无阴影 | `shadow-none` | 内嵌元素 |
| 微弱 | `shadow-sm` | 输入框 |
| 轻微 | `shadow` | 卡片默认 |
| 中等 | `shadow-md` | 卡片悬停 |
| 明显 | `shadow-lg` | 下拉菜单 |
| 强烈 | `shadow-xl` | 弹窗 |

---

## 七、组件样式

### 7.1 按钮（Button）

**主要按钮**
```html
<button class="bg-blue-500 hover:bg-blue-600 active:bg-blue-700 text-white font-medium px-4 py-2 rounded-lg shadow-sm transition-colors duration-200">
  确认
</button>
```

**次要按钮**
```html
<button class="bg-white hover:bg-gray-50 text-gray-700 font-medium px-4 py-2 rounded-lg border border-gray-300 shadow-sm transition-colors duration-200">
  取消
</button>
```

**危险按钮**
```html
<button class="bg-red-500 hover:bg-red-600 active:bg-red-700 text-white font-medium px-4 py-2 rounded-lg shadow-sm transition-colors duration-200">
  删除
</button>
```

**禁用按钮**
```html
<button class="bg-gray-100 text-gray-400 font-medium px-4 py-2 rounded-lg cursor-not-allowed" disabled>
  禁用
</button>
```

**文字按钮**
```html
<button class="text-blue-500 hover:text-blue-600 font-medium px-2 py-1 transition-colors duration-200">
  查看详情
</button>
```

### 7.2 输入框（Input）

**默认状态**
```html
<input class="w-full px-3 py-2 text-gray-700 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200" placeholder="请输入">
```

**错误状态**
```html
<input class="w-full px-3 py-2 text-gray-700 bg-white border border-red-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-red-500" placeholder="请输入">
<p class="mt-1 text-sm text-red-600">请输入正确的内容</p>
```

**禁用状态**
```html
<input class="w-full px-3 py-2 text-gray-400 bg-gray-100 border border-gray-200 rounded-md cursor-not-allowed" placeholder="禁用" disabled>
```

### 7.3 卡片（Card）

**基础卡片**
```html
<div class="bg-white rounded-xl shadow p-6">
  <h3 class="text-lg font-semibold text-gray-800 mb-4">卡片标题</h3>
  <p class="text-gray-600">卡片内容</p>
</div>
```

**可点击卡片**
```html
<div class="bg-white rounded-xl shadow p-6 cursor-pointer hover:shadow-md transition-shadow duration-200">
  <h3 class="text-lg font-semibold text-gray-800 mb-4">卡片标题</h3>
  <p class="text-gray-600">卡片内容</p>
</div>
```

**带边框卡片**
```html
<div class="bg-white rounded-xl border border-gray-200 p-6">
  <h3 class="text-lg font-semibold text-gray-800 mb-4">卡片标题</h3>
  <p class="text-gray-600">卡片内容</p>
</div>
```

### 7.4 表格（Table）

```html
<div class="bg-white rounded-xl shadow overflow-hidden">
  <table class="w-full">
    <thead class="bg-gray-50">
      <tr>
        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">姓名</th>
        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">状态</th>
        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">操作</th>
      </tr>
    </thead>
    <tbody class="divide-y divide-gray-200">
      <tr class="hover:bg-gray-50 transition-colors duration-150">
        <td class="px-6 py-4 text-sm text-gray-700">张三</td>
        <td class="px-6 py-4">
          <span class="px-2 py-1 text-xs font-medium text-emerald-700 bg-emerald-50 rounded-full">正常</span>
        </td>
        <td class="px-6 py-4 text-sm">
          <button class="text-blue-500 hover:text-blue-600">编辑</button>
        </td>
      </tr>
    </tbody>
  </table>
</div>
```

### 7.5 标签（Tag）

**状态标签**
```html
<!-- 成功 -->
<span class="px-2 py-1 text-xs font-medium text-emerald-700 bg-emerald-50 rounded-full">已完成</span>

<!-- 警告 -->
<span class="px-2 py-1 text-xs font-medium text-amber-700 bg-amber-50 rounded-full">进行中</span>

<!-- 错误 -->
<span class="px-2 py-1 text-xs font-medium text-red-700 bg-red-50 rounded-full">已取消</span>

<!-- 信息 -->
<span class="px-2 py-1 text-xs font-medium text-blue-700 bg-blue-50 rounded-full">待处理</span>

<!-- 默认 -->
<span class="px-2 py-1 text-xs font-medium text-gray-700 bg-gray-100 rounded-full">草稿</span>
```

### 7.6 提示框（Alert）

```html
<!-- 成功提示 -->
<div class="flex items-center gap-3 p-4 bg-emerald-50 border border-emerald-200 rounded-lg">
  <svg class="w-5 h-5 text-emerald-500 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
  </svg>
  <span class="text-sm text-emerald-700">操作成功</span>
</div>

<!-- 错误提示 -->
<div class="flex items-center gap-3 p-4 bg-red-50 border border-red-200 rounded-lg">
  <svg class="w-5 h-5 text-red-500 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
  </svg>
  <span class="text-sm text-red-700">操作失败</span>
</div>

<!-- 警告提示 -->
<div class="flex items-center gap-3 p-4 bg-amber-50 border border-amber-200 rounded-lg">
  <svg class="w-5 h-5 text-amber-500 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
  </svg>
  <span class="text-sm text-amber-700">请注意</span>
</div>

<!-- 信息提示 -->
<div class="flex items-center gap-3 p-4 bg-blue-50 border border-blue-200 rounded-lg">
  <svg class="w-5 h-5 text-blue-500 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
  </svg>
  <span class="text-sm text-blue-700">温馨提示</span>
</div>
```

### 7.7 弹窗（Modal）

```html
<!-- 遮罩层 -->
<div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
  <!-- 弹窗主体 -->
  <div class="bg-white rounded-2xl shadow-xl w-full max-w-md mx-4">
    <!-- 头部 -->
    <div class="flex items-center justify-between px-6 py-4 border-b border-gray-200">
      <h3 class="text-lg font-semibold text-gray-800">弹窗标题</h3>
      <button class="text-gray-400 hover:text-gray-600 transition-colors">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
        </svg>
      </button>
    </div>
    <!-- 内容 -->
    <div class="px-6 py-4">
      <p class="text-gray-600">弹窗内容区域</p>
    </div>
    <!-- 底部 -->
    <div class="flex justify-end gap-3 px-6 py-4 border-t border-gray-200">
      <button class="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">取消</button>
      <button class="px-4 py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-600 transition-colors">确认</button>
    </div>
  </div>
</div>
```

### 7.8 导航菜单（Sidebar）

```html
<aside class="w-64 bg-white border-r border-gray-200 h-screen">
  <!-- Logo -->
  <div class="flex items-center gap-3 px-6 py-5 border-b border-gray-200">
    <svg class="w-8 h-8 text-blue-500" fill="currentColor" viewBox="0 0 24 24">
      <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
    </svg>
    <span class="text-lg font-semibold text-gray-800">悦芽婴护</span>
  </div>
  
  <!-- 菜单列表 -->
  <nav class="px-3 py-4">
    <!-- 激活状态 -->
    <a href="#" class="flex items-center gap-3 px-3 py-2 text-blue-600 bg-blue-50 rounded-lg mb-1">
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
      </svg>
      <span class="font-medium">首页</span>
    </a>
    
    <!-- 默认状态 -->
    <a href="#" class="flex items-center gap-3 px-3 py-2 text-gray-600 hover:bg-gray-100 rounded-lg mb-1 transition-colors">
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z"/>
      </svg>
      <span>用户管理</span>
    </a>
  </nav>
</aside>
```

---

## 八、图标规范

### 8.1 图标来源
推荐使用以下SVG图标库：
- **Heroicons**：https://heroicons.com （Tailwind官方推荐）
- **Lucide**：https://lucide.dev
- **Tabler Icons**：https://tabler-icons.io

### 8.2 图标尺寸

| 场景 | Tailwind类名 | 尺寸 |
|------|-------------|------|
| 小图标 | `w-4 h-4` | 16px |
| 默认图标 | `w-5 h-5` | 20px |
| 中等图标 | `w-6 h-6` | 24px |
| 大图标 | `w-8 h-8` | 32px |
| 超大图标 | `w-12 h-12` | 48px |

### 8.3 常用图标示例

```html
<!-- 首页 -->
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
</svg>

<!-- 用户 -->
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
</svg>

<!-- 设置 -->
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"/>
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
</svg>

<!-- 搜索 -->
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
</svg>

<!-- 添加 -->
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
</svg>

<!-- 编辑 -->
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
</svg>

<!-- 删除 -->
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
</svg>

<!-- 关闭 -->
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
</svg>

<!-- 菜单 -->
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
</svg>

<!-- 通知 -->
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
</svg>

<!-- 日历 -->
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
</svg>

<!-- 购物车 -->
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"/>
</svg>

<!-- 订单 -->
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"/>
</svg>

<!-- 婴儿 -->
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
</svg>

<!-- 心形/健康 -->
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
</svg>

<!-- 统计图表 -->
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
</svg>
```

---

## 九、动画效果

### 9.1 过渡动画

```html
<!-- 颜色过渡 -->
<button class="transition-colors duration-200">按钮</button>

<!-- 阴影过渡 -->
<div class="transition-shadow duration-200 hover:shadow-md">卡片</div>

<!-- 全部属性过渡 -->
<div class="transition-all duration-300">元素</div>
```

### 9.2 常用动画时长

| 场景 | Tailwind类名 | 时长 |
|------|-------------|------|
| 快速 | `duration-150` | 150ms |
| 默认 | `duration-200` | 200ms |
| 适中 | `duration-300` | 300ms |
| 较慢 | `duration-500` | 500ms |

---

## 十、响应式断点

| 断点 | Tailwind前缀 | 最小宽度 |
|------|-------------|----------|
| 默认 | 无 | 0px |
| sm | `sm:` | 640px |
| md | `md:` | 768px |
| lg | `lg:` | 1024px |
| xl | `xl:` | 1280px |
| 2xl | `2xl:` | 1536px |

---

## 十一、Tailwind配置扩展

```javascript
// tailwind.config.js
module.exports = {
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#EFF6FF',
          100: '#DBEAFE',
          200: '#BFDBFE',
          300: '#93C5FD',
          400: '#60A5FA',
          500: '#3B82F6',
          600: '#2563EB',
          700: '#1D4ED8',
          800: '#1E40AF',
          900: '#1E3A8A',
        }
      },
      fontFamily: {
        sans: ['-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'sans-serif'],
      },
      boxShadow: {
        'card': '0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1)',
        'card-hover': '0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)',
      }
    }
  }
}
```

---

## 十二、设计原则总结

1. **一致性**：全系统使用统一的色彩、字体、间距规范
2. **对比度**：确保文字与背景有足够对比度，满足可访问性要求
3. **留白**：适当留白，避免信息过于密集
4. **反馈**：交互元素需有明确的状态反馈（悬停、点击、禁用）
5. **层次**：通过颜色深浅、字号大小、阴影强弱建立视觉层级
