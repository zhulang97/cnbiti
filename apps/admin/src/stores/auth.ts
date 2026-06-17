import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { apiRequest, type LoginResponse } from '@/api/client'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const username = ref(localStorage.getItem('admin_user') || '')
  const role = ref(localStorage.getItem('admin_role') || '')

  const isLoggedIn = computed(() => !!token.value)

  async function login(user: string, pass: string): Promise<boolean> {
    try {
      const result = await apiRequest<LoginResponse>('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ username: user, password: pass }),
      })

      token.value = result.token
      username.value = result.user.name
      role.value = result.user.role
      localStorage.setItem('admin_token', result.token)
      localStorage.setItem('admin_user', result.user.name)
      localStorage.setItem('admin_role', result.user.role)
      return true
    } catch {
      return false
    }
  }

  function logout() {
    token.value = ''
    username.value = ''
    role.value = ''
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_user')
    localStorage.removeItem('admin_role')
  }

  return { token, username, role, isLoggedIn, login, logout }
})
