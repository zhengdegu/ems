import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

export const useTabStore = defineStore('tab', () => {
  const tabs = ref([{ title: '工作台', name: 'dashboard', closable: false }])
  const activeTab = ref('dashboard')

  function addTab(route) {
    const exists = tabs.value.find(t => t.name === route.name)
    if (!exists) {
      tabs.value.push({
        title: route.meta?.title || route.name,
        name: route.name,
        path: route.fullPath,
        closable: !route.meta?.affix
      })
    }
    activeTab.value = route.name
  }

  function removeTab(name) {
    const idx = tabs.value.findIndex(t => t.name === name)
    if (idx > -1) {
      tabs.value.splice(idx, 1)
      if (activeTab.value === name) {
        const prev = tabs.value[Math.min(idx, tabs.value.length - 1)]
        activeTab.value = prev ? prev.name : 'dashboard'
      }
    }
    return activeTab.value
  }

  return { tabs, activeTab, addTab, removeTab }
})
