import { defineStore } from "pinia"
import { ref, computed } from "vue"
import { componentsData } from "@/api/components.js"

export const useInventoryStore = defineStore("inventory", () => {
  const stockRecords = ref([])

  const components = ref(componentsData)

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

  function restock(type, id, quantity) {
    const item = components.value[type].find((c) => c.id === id)
    if (item) {
      item.stock += quantity
      stockRecords.value.push({
        id: Date.now(),
        type: "入库",
        component: `${item.brand} ${item.model}`,
        quantity,
        time: new Date().toLocaleString(),
      })
    }
  }

  function reduceStock(type, id, quantity) {
    const item = components.value[type].find((c) => c.id === id)
    if (item && item.stock >= quantity) {
      item.stock -= quantity
      stockRecords.value.push({
        id: Date.now(),
        type: "出库",
        component: `${item.brand} ${item.model}`,
        quantity,
        time: new Date().toLocaleString(),
      })
      return true
    }
    return false
  }

  function addRecord(record) {
    stockRecords.value.unshift(record)
  }

  return {
    components,
    stockRecords,
    lowStockItems,
    alertCount,
    totalStockValue,
    totalItems,
    restock,
    reduceStock,
    addRecord,
  }
})
