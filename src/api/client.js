const API_BASE = import.meta.env.VITE_API_BASE_URL || ""

export class ApiError extends Error {
  constructor(message, response) {
    super(message)
    this.name = "ApiError"
    this.response = response
  }
}

export async function request(path, options = {}) {
  const token = readToken()
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {}),
    },
    ...options,
  })

  const payload = await response.json().catch(() => null)

  if (!response.ok) {
    throw new ApiError(payload?.message || `HTTP ${response.status}`, payload)
  }

  if (payload && typeof payload.code === "number" && payload.code !== 200) {
    throw new ApiError(payload.message || "请求失败", payload)
  }

  return payload?.data ?? payload
}

function readToken() {
  try {
    return JSON.parse(localStorage.getItem("pcdiy_auth") || "null")?.token || ""
  } catch {
    return ""
  }
}
