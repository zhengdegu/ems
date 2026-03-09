import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import en from 'element-plus/dist/locale/en.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import i18n from './i18n'
import './style.css'

const app = createApp(App)

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// Element Plus 语言切换
const getElementLocale = () => {
  const locale = localStorage.getItem('ems_locale') || 'zh-CN'
  return locale === 'en-US' ? en : zhCn
}

app.use(createPinia())
app.use(router)
app.use(i18n)
app.use(ElementPlus, { locale: getElementLocale() })
app.mount('#app')
