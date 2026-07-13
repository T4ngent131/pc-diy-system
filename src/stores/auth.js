import { defineStore } from "pinia"
import { computed, ref } from "vue"
import { request } from "@/api/client.js"

const storageKey = "pcdiy_auth"

function readStoredAuth() {
  try {
    return JSON.parse(localStorage.getItem(storageKey) || "null")
  } catch {
    localStorage.removeItem(storageKey)
    return null
  }
}

export const useAuthStore = defineStore("auth", () => {
  const saved = readStoredAuth()
  const user = ref(saved?.user || null)
  const token = ref(saved?.token || "")
  const loading = ref(false)

  const isLoggedIn = computed(() => Boolean(token.value && user.value))

  async function login(payload) {
    return authenticate("/api/auth/login", payload)
  }

  async function register(payload) {
    return authenticate("/api/auth/register", payload)
  }

  async function authenticate(path, payload) {
    loading.value = true
    try {
      const data = await request(path, {
        method: "POST",
        body: JSON.stringify(payload),
      })
      user.value = {
        id: data.id,
        username: data.username,
        phone: data.phone,
        role: data.role,
      }
      token.value = data.token
      localStorage.setItem(storageKey, JSON.stringify({ user: user.value, token: token.value }))
      return data
    } finally {
      loading.value = false
    }
  }

  function logout() {
    user.value = null
    token.value = ""
    localStorage.removeItem(storageKey)
  }

  return {
    user,
    token,
    loading,
    isLoggedIn,
    login,
    register,
    logout,
  }
})
