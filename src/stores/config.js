import { defineStore } from "pinia"
import { ref, computed } from "vue"
import { componentsData } from "@/api/components.js"
import { checkCompatibility, estimateTotalPower, getRecommendedPSU } from "@/utils/compatibility.js"

export const useConfigStore = defineStore("config", () => {
  const currentConfig = ref({
    cpu: null,
    gpu: null,
    motherboard: null,
    ram: null,
    storage: null,
    psu: null,
    case: null,
    cooler: null,
  })
  const savedConfigs = ref([])
  const configName = ref("")
  const selectedComponentType = ref(null)
  const selectorVisible = ref(false)

  const totalPrice = computed(() => {
    return Object.values(currentConfig.value).reduce((sum, item) => sum + (item?.price || 0), 0)
  })

  const totalPower = computed(() => estimateTotalPower(currentConfig.value))

  const recommendedPSU = computed(() => getRecommendedPSU(currentConfig.value))

  const compatibility = computed(() => checkCompatibility(currentConfig.value))

  const filledCount = computed(() => {
    return Object.values(currentConfig.value).filter(Boolean).length
  })

  const componentCount = Object.keys(componentsData).length

  function setComponent(type, component) {
    currentConfig.value[type] = component
    selectorVisible.value = false
  }

  function removeComponent(type) {
    currentConfig.value[type] = null
  }

  function openSelector(type) {
    selectedComponentType.value = type
    selectorVisible.value = true
  }

  function closeSelector() {
    selectorVisible.value = false
    selectedComponentType.value = null
  }

  function saveConfig(name) {
    if (!name && !configName.value) return
    const entry = {
      id: Date.now(),
      name: name || configName.value,
      config: JSON.parse(JSON.stringify(currentConfig.value)),
      totalPrice: totalPrice.value,
      createdAt: new Date().toISOString(),
    }
    savedConfigs.value.push(entry)
    configName.value = ""
  }

  function loadConfig(entry) {
    currentConfig.value = { ...entry.config }
  }

  function deleteConfig(id) {
    savedConfigs.value = savedConfigs.value.filter((c) => c.id !== id)
  }

  function clearConfig() {
    Object.keys(currentConfig.value).forEach((k) => (currentConfig.value[k] = null))
  }

  return {
    currentConfig,
    savedConfigs,
    configName,
    selectedComponentType,
    selectorVisible,
    totalPrice,
    totalPower,
    recommendedPSU,
    compatibility,
    filledCount,
    componentCount,
    setComponent,
    removeComponent,
    openSelector,
    closeSelector,
    saveConfig,
    loadConfig,
    deleteConfig,
    clearConfig,
  }
})
