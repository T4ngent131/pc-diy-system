import { defineStore } from "pinia"
import { ref, computed } from "vue"
import { request } from "@/api/client.js"

const statusToLabel = {
  pending: "待确认",
  confirmed: "待备货",
  preparing: "待备货",
  assembling: "组装中",
  completed: "已完成",
  cancelled: "已取消",
}

const labelToStatus = {
  "待确认": "pending",
  "待备货": "preparing",
  "组装中": "assembling",
  "已完成": "completed",
  "已取消": "cancelled",
}

const demoOrders = [
  {
    id: "ORD-20260701-001",
    customer: "张先生",
    config: { cpu: "Core i7-14700KF", gpu: "RTX 4070 Super", motherboard: "MAG Z790 TOMAHAWK", ram: "Trident Z5 32GB DDR5", storage: "990 Pro 1TB", psu: "RM750e", case: "LANCOOL 216", cooler: "AK620" },
    totalPrice: 10791,
    status: "已完成",
    createdAt: "2026-07-01 10:30",
    completedAt: "2026-07-03 16:20",
  },
  {
    id: "ORD-20260702-001",
    customer: "李先生",
    config: { cpu: "Ryzen 7 7800X3D", gpu: "RX 7900 XTX", motherboard: "X670E CARBON", ram: "Trident Z5 64GB DDR5", storage: "990 Pro 2TB", psu: "RM1000x", case: "NV5", cooler: "Kraken X73" },
    totalPrice: 23692,
    status: "组装中",
    createdAt: "2026-07-02 14:00",
  },
  {
    id: "ORD-20260703-001",
    customer: "王女士",
    config: { cpu: "Core i5-14600KF", gpu: "RTX 4060 Ti", motherboard: "TUF B760M-PLUS", ram: "FURY Beast DDR5 32GB", storage: "Black SN850X 1TB", psu: "Focus GX-850", case: "H5 Flow", cooler: "Freezer 34 eSports" },
    totalPrice: 8137,
    status: "待备货",
    createdAt: "2026-07-03 09:15",
  },
  {
    id: "ORD-20260704-001",
    customer: "赵先生",
    config: { cpu: "Ryzen 9 7950X", gpu: "RTX 4090 D", motherboard: "ROG STRIX Z790-E", ram: "Trident Z5 64GB DDR5", storage: "P41 Platinum 2TB", psu: "Prime TX-1000", case: "4000D Airflow", cooler: "NH-D15" },
    totalPrice: 34691,
    status: "待确认",
    createdAt: "2026-07-04 11:00",
  },
]

function formatDateTime(value) {
  if (!value) return ""
  return String(value).replace("T", " ").slice(0, 16)
}

function normalizeOrder(order) {
  return {
    ...order,
    id: order.orderNo || order.id,
    rawId: order.id,
    config: order.config || {},
    totalPrice: Number(order.totalPrice || 0),
    status: statusToLabel[order.status] || order.status || "待确认",
    createdAt: formatDateTime(order.createdAt),
  }
}

export const useOrderStore = defineStore("orders", () => {
  const orders = ref([...demoOrders])
  const loading = ref(false)
  const backendReady = ref(false)
  const lastError = ref("")

  const statusOptions = ["待确认", "待备货", "组装中", "已完成", "已取消"]
  const statusColorMap = { "待确认": "warning", "待备货": "info", "组装中": "primary", "已完成": "success", "已取消": "danger" }

  const pendingCount = computed(() => orders.value.filter((o) => o.status !== "已完成" && o.status !== "已取消").length)

  const todayOrders = computed(() => {
    const today = new Date().toISOString().slice(0, 10)
    return orders.value.filter((o) => o.createdAt?.startsWith(today))
  })

  const totalRevenue = computed(() => {
    return orders.value.filter((o) => o.status === "已完成").reduce((sum, o) => sum + o.totalPrice, 0)
  })

  async function fetchOrders() {
    loading.value = true
    lastError.value = ""
    try {
      const data = await request("/api/orders")
      orders.value = data.map(normalizeOrder)
      backendReady.value = true
    } catch (error) {
      backendReady.value = false
      lastError.value = error.message || "订单接口不可用"
    } finally {
      loading.value = false
    }
  }

  async function addOrder(order) {
    if (backendReady.value) {
      const orderNo = await request("/api/orders", {
        method: "POST",
        body: JSON.stringify({
          customer: order.customer,
          phone: order.phone,
          wechat: order.wechat,
          note: order.note,
          totalPrice: order.totalPrice,
          items: order.items || [],
        }),
      })
      await fetchOrders()
      return orderNo
    }

    const id = `ORD-${new Date().toISOString().slice(0, 10).replace(/-/g, "")}-${String(orders.value.length + 1).padStart(3, "0")}`
    orders.value.unshift({
      ...order,
      id,
      status: "待确认",
      createdAt: new Date().toLocaleString(),
    })
    return id
  }

  async function updateStatus(id, status) {
    const order = orders.value.find((item) => item.id === id || item.rawId === id)
    if (backendReady.value && order?.rawId) {
      await request(`/api/orders/${order.rawId}/status?status=${encodeURIComponent(labelToStatus[status] || status)}`, {
        method: "PUT",
      })
      await fetchOrders()
      return
    }

    if (order) {
      order.status = status
      if (status === "已完成") {
        order.completedAt = new Date().toLocaleString()
      }
    }
  }

  function deleteOrder(id) {
    orders.value = orders.value.filter((o) => o.id !== id)
  }

  return {
    orders,
    loading,
    backendReady,
    lastError,
    statusOptions,
    statusColorMap,
    pendingCount,
    todayOrders,
    totalRevenue,
    fetchOrders,
    addOrder,
    updateStatus,
    deleteOrder,
  }
})
