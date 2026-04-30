import { useUserStore } from '../stores/user'

export const permissionDirective = {
  mounted(el, binding) {
    const userStore = useUserStore()
    const permission = binding.value
    if (permission && !userStore.hasPermission(permission)) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
}
