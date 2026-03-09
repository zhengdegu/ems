import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

export const useLocaleStore = defineStore('locale', () => {
  const locale = ref(localStorage.getItem('ems_locale') || 'zh-CN')

  const setLocale = (newLocale) => {
    locale.value = newLocale
    localStorage.setItem('ems_locale', newLocale)
    window.location.reload() // 重新加载以应用 Element Plus 语言
  }

  return {
    locale,
    setLocale
  }
})
