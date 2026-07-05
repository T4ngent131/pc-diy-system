<template>
  <div class="config-builder">
    <!-- 顶栏操作区 -->
    <div class="section-header">
      <h2>配置方案</h2>
      <div class="header-actions">
        <el-input v-model="configStore.configName" placeholder="输入方案名称..." size="small" style="width:200px" />
        <el-button type="primary" size="small" @click="handleSave" :disabled="!configStore.configName">
          <el-icon><FolderAdd /></el-icon> 保存方案
        </el-button>
        <el-button size="small" @click="configStore.clearConfig()">
          <el-icon><Refresh /></el-icon> 清空
        </el-button>
      </div>
    </div>

    <div class="builder-layout">
      <!-- 左侧兼容性 -->
      <div class="builder-sidebar">
        <Compatibility :compatibility="configStore.compatibility" />

        <!-- 已保存方案 -->
        <el-card class="saved-list">
          <template #header>
            <div style="font-weight:600;font-size:0.9rem">
              <el-icon><FolderOpened /></el-icon> 已保存方案 ({{ configStore.savedConfigs.length }})
            </div>
          </template>
          <div v-if="configStore.savedConfigs.length === 0" style="text-align:center;padding:12px;color:var(--color-text-muted);font-size:0.85rem">
            暂无已保存方案
          </div>
          <div v-else class="saved-items">
            <div v-for="cfg in configStore.savedConfigs" :key="cfg.id" class="saved-item" @click="configStore.loadConfig(cfg)">
              <div class="saved-item-info">
                <div class="saved-name">{{ cfg.name }}</div>
                <div class="saved-price">¥{{ cfg.totalPrice.toLocaleString() }}</div>
              </div>
              <el-button text type="danger" size="small" @click.stop="configStore.deleteConfig(cfg.id)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧配置表 -->
      <div class="builder-main">
        <el-card>
          <ConfigTable
            :config="configStore.currentConfig"
            :totalPrice="configStore.totalPrice"
            :totalPower="configStore.totalPower"
            :recommendedPSU="configStore.recommendedPSU"
            @select="handleSelect"
            @remove="configStore.removeComponent"
          />
        </el-card>
      </div>
    </div>

    <!-- 配件选择弹窗 -->
    <ComponentSelector
      v-model="configStore.selectorVisible"
      :type="configStore.selectedComponentType"
      @select="configStore.setComponent"
    />
  </div>
</template>

<script setup>
import { useConfigStore } from "@/stores/config.js"
import ConfigTable from "@/components/config/ConfigTable.vue"
import ComponentSelector from "@/components/config/ComponentSelector.vue"
import Compatibility from "@/components/config/Compatibility.vue"
import { ElMessage } from "element-plus"

const configStore = useConfigStore()

function handleSelect(type) {
  configStore.openSelector(type)
}

function handleSave() {
  configStore.saveConfig()
  ElMessage.success("方案已保存")
}
</script>

<style scoped>
.config-builder {
  animation: fadeIn 0.3s ease;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.builder-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 16px;
  align-items: start;
}
.builder-sidebar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.builder-main {
  min-width: 0;
}
.saved-items {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.saved-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 10px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background 0.15s;
}
.saved-item:hover {
  background: var(--color-surface);
}
.saved-item-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.saved-name {
  font-weight: 500;
  font-size: 0.85rem;
}
.saved-price {
  font-size: 0.78rem;
  color: var(--color-text-muted);
}
@media (max-width: 900px) {
  .builder-layout { grid-template-columns: 1fr; }
}
</style>
