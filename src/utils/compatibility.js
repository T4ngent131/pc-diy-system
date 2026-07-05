// 兼容性检查工具
export function checkCompatibility(config) {
  const issues = []
  const warnings = []

  if (!config.cpu && !config.gpu && !config.motherboard) return { compatible: true, issues: [], warnings: [] }

  // 1. CPU 与主板插槽匹配
  if (config.cpu && config.motherboard) {
    if (config.cpu.socket !== config.motherboard.socket) {
      issues.push(`CPU 插槽 (${config.cpu.socket}) 与主板插槽 (${config.motherboard.socket}) 不兼容`)
    }
  }

  // 2. 主板与内存类型匹配
  if (config.motherboard && config.ram) {
    if (config.motherboard.ramType !== config.ram.type) {
      issues.push(`主板支持 ${config.motherboard.ramType} 内存，但选择了 ${config.ram.type}`)
    }
  }

  // 3. 散热器与 CPU 插槽支持
  if (config.cpu && config.cooler) {
    if (!config.cooler.socketSupport.includes(config.cpu.socket)) {
      issues.push(`散热器不支持当前 CPU 插槽 (${config.cpu.socket})`)
    }
  }

  // 4. 机箱与主板版型兼容
  if (config.case && config.motherboard) {
    if (!config.case.formFactor.includes(config.motherboard.formFactor)) {
      issues.push(`机箱不支持 ${config.motherboard.formFactor} 版型的主板`)
    }
  }

  // 5. 电源功率是否足够
  if (config.psu) {
    const estimatedPower = estimateTotalPower(config)
    if (config.psu.wattage < estimatedPower) {
      issues.push(`电源功率 (${config.psu.wattage}W) 不足，估算总功耗约 ${estimatedPower}W`)
    } else if (config.psu.wattage < estimatedPower * 1.15) {
      warnings.push(`电源功率 (${config.psu.wattage}W) 余量偏小，建议 ${Math.ceil(estimatedPower * 1.3)}W 以上`)
    }
  }

  // 6. 机箱显卡长度限制
  if (config.case && config.gpu) {
    if (config.gpu.length > config.case.maxGpuLength) {
      issues.push(`显卡长度 (${config.gpu.length}mm) 超过机箱限长 (${config.case.maxGpuLength}mm)`)
    }
  }

  // 7. 机箱散热器高度限制（风冷）
  if (config.case && config.cooler && config.cooler.type === "air") {
    const coolerHeight = config.cooler.height || 160
    if (coolerHeight > config.case.maxCoolerHeight) {
      issues.push(`散热器高度可能超过机箱限高 (${config.case.maxCoolerHeight}mm)`)
    }
  }

  return {
    compatible: issues.length === 0,
    issues,
    warnings,
  }
}

// 估算总功耗
export function estimateTotalPower(config) {
  let total = 0
  if (config.cpu) total += config.cpu.tdp || config.cpu.tdp || 100
  if (config.gpu) total += config.gpu.tdp || 200
  if (config.ram) total += 10
  if (config.storage) total += 10
  if (config.cooler) total += config.cooler.tdp ? Math.min(config.cooler.tdp * 0.15, 15) : 5
  total += 20 // 主板及其他
  return Math.ceil(total)
}

// 获取推荐电源功率
export function getRecommendedPSU(config) {
  const power = estimateTotalPower(config)
  const recommend = Math.ceil(power * 1.3)
  const tiers = [450, 550, 650, 750, 850, 1000, 1200]
  for (const tier of tiers) {
    if (tier >= recommend) return tier
  }
  return 1200
}
