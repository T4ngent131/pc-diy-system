/**
 * 前后端 API 连接层
 * 将现有前端改用后端 API 数据源
 * 使用方式: 修改 stores 中的导入, 从此文件获取数据
 */
const API_BASE = "http://localhost:8080/api"

// 通用请求封装
async function request(url, options = {}) {
  const { method = "GET", data, params } = options
  const query = params ? "?" + new URLSearchParams(params).toString() : ""

  const config = {
    method,
    headers: { "Content-Type": "application/json" },
  }

  if (data) {
    config.body = JSON.stringify(data)
  }

  try {
    const res = await fetch(`${API_BASE}${url}${query}`, config)
    const json = await res.json()
    if (json.code !== 200) {
      throw new Error(json.message || "请求失败")
    }
    return json.data
  } catch (err) {
    console.error(`[API Error] ${method} ${url}:`, err)
    throw err
  }
}

// ====== 配件 API ======
export const componentApi = {
  getByType: (type) => request(`/components/type/${type}`),
  getAll: () => request("/components/all"),
  getBrands: (type) => request(`/components/brands/${type}`),
  getLowStock: () => request("/components/low-stock"),
  getById: (id) => request(`/components/${id}`),
  search: (q, type, brand) => request("/components/search", { params: { q, type, brand } }),
  updateStock: (id, stock) => request(`/components/${id}/stock`, { method: "PUT", params: { stock } }),
}

// ====== 订单 API ======
export const orderApi = {
  list: () => request("/orders"),
  getById: (id) => request(`/orders/${id}`),
  create: (order) => request("/orders", { method: "POST", data: order }),
  updateStatus: (id, status) => request(`/orders/${id}/status`, { method: "PUT", params: { status } }),
  search: (keyword) => request("/orders/search", { params: { keyword } }),
  pendingCount: () => request("/orders/pending-count"),
}

// ====== 库存 API ======
export const inventoryApi = {
  stockIn: (componentId, quantity, remark) =>
    request("/inventory/in", { method: "POST", data: { componentId, quantity, remark } }),
  stockOut: (componentId, quantity, remark) =>
    request("/inventory/out", { method: "POST", data: { componentId, quantity, remark } }),
  records: (limit = 50) => request("/inventory/records", { params: { limit } }),
}

// ====== 知识库 API ======
export const knowledgeApi = {
  list: (category) => request("/knowledge", { params: { category } }),
  detail: (id) => request(`/knowledge/${id}`),
  categories: () => request("/knowledge/categories"),
}

// ====== 健康检查 ======
export const systemApi = {
  health: () => request("/health"),
}
