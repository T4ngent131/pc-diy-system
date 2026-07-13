import { defineStore } from "pinia"
import { ref, computed } from "vue"
import { componentsData } from "@/api/components.js"
import { request } from "@/api/client.js"

function normalizeComponent(item) {
  const specs = typeof item.specs === "string" ? parseSpecs(item.specs) : item.specs || {}
  return {
    ...specs,
    ...item,
    price: Number(item.price || 0),
    stock: Number(item.stock || 0),
  }
}

function parseSpecs(specs) {
  try {
    return JSON.parse(specs)
  } catch {
    return {}
  }
}

function normalizeRecord(record) {
  return {
    ...record,
    type: record.type === "in" ? "入库" : "出库",
    component: record.componentName,
    remark: record.remark || "",
    time: String(record.createdAt || "").replace("T", " ").slice(0, 16),
  }
}

export const useInventoryStore = defineStore("inventory", () => {
  const stockRecords = ref([])
  const components = ref(componentsData)
  const loading = ref(false)
  const backendReady = ref(false)
  const lastError = ref("")

  const lowStockItems = computed(() => {
    const items = []
    Object.entries(components.value).forEach(([type, list]) => {
      list.forEach((item) => {
        if (item.stock <= 3) {
          items.push({ ...item, type })
        }
      })
    })
    return items
  })

  const alertCount = computed(() => lowStockItems.value.length)

  const totalStockValue = computed(() => {
    let total = 0
    Object.values(components.value).forEach((list) => {
      list.forEach((item) => {
        total += item.price * item.stock
      })
    })
    return total
  })

  const totalItems = computed(() => {
    let count = 0
    Object.values(components.value).forEach((list) => {
      count += list.length
    })
    return count
  })

  async function fetchComponents() {
    loading.value = true
    lastError.value = ""
    try {
      const data = await request("/api/components/all")
      const grouped = {}
      Object.entries(data).forEach(([type, list]) => {
        grouped[type] = list.map(normalizeComponent)
      })
      components.value = grouped
      backendReady.value = true
    } catch (error) {
      backendReady.value = false
      lastError.value = error.message || "配件接口不可用"
    } finally {
      loading.value = false
    }
  }

  async function fetchRecords(limit = 50) {
    try {
      const data = await request(`/api/inventory/records?limit=${limit}`)
      stockRecords.value = data.map(normalizeRecord)
      backendReady.value = true
    } catch (error) {
      lastError.value = error.message || "库存记录接口不可用"
    }
  }

  async function restock(type, id, quantity, remark = "手动入库") {
    if (backendReady.value) {
      await request("/api/inventory/in", {
        method: "POST",
        body: JSON.stringify({ componentId: id, quantity, remark }),
      })
      await fetchComponents()
      await fetchRecords()
      return true
    }

    const item = components.value[type]?.find((c) => c.id === id)
    if (item) {
      item.stock += quantity
      stockRecords.value.unshift({
        id: Date.now(),
        type: "入库",
        component: `${item.brand} ${item.model}`,
        quantity,
        remark,
        time: new Date().toLocaleString(),
      })
      return true
    }
    return false
  }

  async function reduceStock(type, id, quantity, remark = "手动出库") {
    if (backendReady.value) {
      await request("/api/inventory/out", {
        method: "POST",
        body: JSON.stringify({ componentId: id, quantity, remark }),
      })
      await fetchComponents()
      await fetchRecords()
      return true
    }

    const item = components.value[type]?.find((c) => c.id === id)
    if (item && item.stock >= quantity) {
      item.stock -= quantity
      stockRecords.value.unshift({
        id: Date.now(),
        type: "出库",
        component: `${item.brand} ${item.model}`,
        quantity,
        remark,
        time: new Date().toLocaleString(),
      })
      return true
    }
    return false
  }

  function findComponent(type, id) {
    return components.value[type]?.find((item) => item.id === id)
  }

  function addRecord(record) {
    stockRecords.value.unshift(record)
  }

  return {
    components,
    stockRecords,
    loading,
    backendReady,
    lastError,
    lowStockItems,
    alertCount,
    totalStockValue,
    totalItems,
    fetchComponents,
    fetchRecords,
    restock,
    reduceStock,
    findComponent,
    addRecord,
  }
})
