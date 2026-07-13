import test from "node:test"
import assert from "node:assert/strict"
import { checkCompatibility, estimateTotalPower, getRecommendedPSU } from "../../src/utils/compatibility.js"

const compatibleConfig = {
  cpu: { socket: "AM5", tdp: 120 },
  motherboard: { socket: "AM5", ramType: "DDR5", formFactor: "M-ATX" },
  ram: { type: "DDR5" },
  gpu: { length: 260, tdp: 220 },
  psu: { wattage: 650 },
  case: { formFactor: ["M-ATX", "ITX"], maxGpuLength: 320, maxCoolerHeight: 165 },
  cooler: { type: "air", socketSupport: ["AM5"], height: 155 },
  storage: {},
}

test("compatible build has no blocking issues", () => {
  const result = checkCompatibility(compatibleConfig)
  assert.equal(result.compatible, true)
  assert.deepEqual(result.issues, [])
})

test("detects CPU and motherboard socket mismatch", () => {
  const result = checkCompatibility({
    ...compatibleConfig,
    motherboard: { ...compatibleConfig.motherboard, socket: "LGA1851" },
  })
  assert.equal(result.compatible, false)
  assert.match(result.issues.join("\n"), /CPU 插槽/)
})

test("detects RAM type mismatch", () => {
  const result = checkCompatibility({
    ...compatibleConfig,
    ram: { type: "DDR4" },
  })
  assert.equal(result.compatible, false)
  assert.match(result.issues.join("\n"), /内存/)
})

test("detects insufficient PSU wattage", () => {
  const result = checkCompatibility({
    ...compatibleConfig,
    psu: { wattage: 300 },
  })
  assert.equal(result.compatible, false)
  assert.match(result.issues.join("\n"), /电源功率/)
})

test("detects GPU length over case limit", () => {
  const result = checkCompatibility({
    ...compatibleConfig,
    gpu: { length: 360, tdp: 220 },
  })
  assert.equal(result.compatible, false)
  assert.match(result.issues.join("\n"), /显卡长度/)
})

test("power estimation and recommended PSU are deterministic", () => {
  assert.equal(estimateTotalPower(compatibleConfig), 385)
  assert.equal(getRecommendedPSU(compatibleConfig), 550)
})
