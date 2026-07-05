// 功率估算
export function estimatePower(config) {
  let total = 0
  if (config.cpu?.tdp) total += config.cpu.tdp
  if (config.gpu?.tdp) total += config.gpu.tdp
  if (config.ram) total += 10
  if (config.storage) total += config.storage.type === "HDD" ? 10 : 5
  if (config.cooler?.tdp) total += config.cooler.type === "aio" ? 15 : 5
  total += 25 // 主板、风扇及其他
  return total
}

export function getPSUSuggestion(totalPower) {
  const recommended = Math.ceil(totalPower * 1.3)
  const tiers = [450, 550, 650, 750, 850, 1000, 1200, 1500]
  for (const t of tiers) {
    if (t >= recommended) return t
  }
  return 1500
}
