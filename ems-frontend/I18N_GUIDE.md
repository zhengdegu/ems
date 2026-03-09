# EMS 国际化使用指南

## 已完成的配置

### 1. 安装依赖
```bash
npm install vue-i18n@9
```

### 2. 目录结构
```
src/
├── i18n/
│   ├── index.js           # i18n 配置入口
│   └── locales/
│       ├── zh-CN.js       # 中文语言包
│       └── en-US.js       # 英文语言包
└── stores/
    └── locale.js          # 语言切换 store
```

### 3. 已集成到项目
- ✅ main.js 中已注册 i18n
- ✅ MainLayout.vue 已添加语言切换功能
- ✅ 支持 Element Plus 组件库国际化

## 使用方法

### 在 Vue 组件中使用

#### 1. 模板中使用
```vue
<template>
  <div>
    <!-- 基本用法 -->
    <h1>{{ $t('login.title') }}</h1>
    
    <!-- 带参数 -->
    <p>{{ $t('common.total', { count: 100 }) }}</p>
    
    <!-- 在属性中使用 -->
    <el-input :placeholder="$t('login.username')" />
  </div>
</template>
```

#### 2. Script 中使用
```vue
<script setup>
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()

// 使用翻译
const title = t('login.title')

// 切换语言
locale.value = 'en-US'
</script>
```

### 切换语言

#### 方法 1: 使用 Store（推荐）
```vue
<script setup>
import { useLocaleStore } from '@/stores/locale'

const localeStore = useLocaleStore()

// 切换到英文
localeStore.setLocale('en-US')

// 切换到中文
localeStore.setLocale('zh-CN')
</script>
```

#### 方法 2: 直接使用 i18n
```vue
<script setup>
import { useI18n } from 'vue-i18n'

const { locale } = useI18n()

locale.value = 'en-US'
</script>
```

## 语言包结构

### 中文 (zh-CN.js)
```javascript
export default {
  common: {
    search: '搜索',
    reset: '重置',
    // ...
  },
  menu: {
    dashboard: '工作台',
    equipmentList: '设备台账',
    // ...
  },
  // ...
}
```

### 英文 (en-US.js)
```javascript
export default {
  common: {
    search: 'Search',
    reset: 'Reset',
    // ...
  },
  menu: {
    dashboard: 'Dashboard',
    equipmentList: 'Equipment List',
    // ...
  },
  // ...
}
```

## 添加新的翻译

### 1. 在语言包中添加
```javascript
// zh-CN.js
export default {
  // 添加新的模块
  newModule: {
    title: '新模块标题',
    description: '新模块描述'
  }
}

// en-US.js
export default {
  newModule: {
    title: 'New Module Title',
    description: 'New Module Description'
  }
}
```

### 2. 在组件中使用
```vue
<template>
  <h1>{{ $t('newModule.title') }}</h1>
  <p>{{ $t('newModule.description') }}</p>
</template>
```

## 已翻译的模块

- ✅ common - 通用文本
- ✅ menu - 菜单项
- ✅ login - 登录/注册
- ✅ dashboard - 工作台
- ✅ equipment - 设备管理
- ✅ maintenance - 维护管理
- ✅ alarm - 告警
- ✅ system - 系统管理
- ✅ message - 提示信息

## 示例页面

查看 `src/views/Login_i18n_example.vue` 了解完整的国际化使用示例。

## 语言切换位置

已在以下位置添加语言切换：
1. **顶部导航栏** - MainLayout.vue 右上角地球图标
2. **登录页** - 可在登录页添加语言切换按钮

## 注意事项

1. **语言持久化**: 当前语言保存在 localStorage 的 `ems_locale` 键中
2. **Element Plus**: 切换语言会自动刷新页面以应用 Element Plus 的语言包
3. **默认语言**: 默认为中文 (zh-CN)
4. **支持的语言**: 
   - zh-CN (简体中文)
   - en-US (English)

## 扩展更多语言

### 1. 创建新语言包
```javascript
// src/i18n/locales/ja-JP.js
export default {
  common: {
    search: '検索',
    // ...
  }
}
```

### 2. 注册语言包
```javascript
// src/i18n/index.js
import jaJP from './locales/ja-JP'

const i18n = createI18n({
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS,
    'ja-JP': jaJP  // 添加日语
  }
})
```

### 3. 添加 Element Plus 语言包
```javascript
// src/main.js
import ja from 'element-plus/dist/locale/ja.mjs'

const getElementLocale = () => {
  const locale = localStorage.getItem('ems_locale') || 'zh-CN'
  if (locale === 'en-US') return en
  if (locale === 'ja-JP') return ja
  return zhCn
}
```

## 测试国际化

1. 启动开发服务器: `npm run dev`
2. 点击顶部导航栏的地球图标
3. 选择不同语言查看效果
4. 刷新页面，语言设置应该保持

## 下一步

建议将所有现有页面的硬编码文本替换为 `$t()` 函数调用，实现完整的国际化支持。
