<template>
  <el-tag :type="type" :size="size" effect="light">
    <span class="status-dot" :style="{ background: dotColor }"></span>
    {{ label }}
  </el-tag>
</template>

<script setup>
import { computed } from "vue"

const props = defineProps({
  type: { type: String, default: "info" },
  label: { type: String, default: "" },
  size: { type: String, default: "small" },
})

const dotColor = computed(() => {
  const map = { success: "var(--color-success)", warning: "var(--color-warning)", danger: "var(--color-danger)", info: "var(--color-info)", primary: "var(--color-primary)" }
  return map[props.type] || "var(--color-text-muted)"
})
</script>
