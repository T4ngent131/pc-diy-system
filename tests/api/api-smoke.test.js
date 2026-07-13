import assert from "node:assert/strict"

const baseUrl = process.env.API_BASE_URL || "http://localhost:8080"
const username = `qa_${Date.now()}`
const password = "123456"

async function request(path, options = {}) {
  const response = await fetch(`${baseUrl}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
    ...options,
  })
  const payload = await response.json().catch(() => null)
  assert.equal(response.ok, true, `${path} HTTP ${response.status}`)
  assert.equal(payload?.code, 200, `${path} business code ${payload?.code}: ${payload?.message}`)
  return payload.data
}

async function requestBusinessError(path, options = {}) {
  const response = await fetch(`${baseUrl}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
    ...options,
  })
  const payload = await response.json().catch(() => null)
  assert.equal(response.ok, true, `${path} HTTP ${response.status}`)
  assert.notEqual(payload?.code, 200, `${path} should return business error`)
  return payload
}

async function main() {
  console.log(`API smoke target: ${baseUrl}`)

  const registered = await request("/api/auth/register", {
    method: "POST",
    body: JSON.stringify({ username, password, phone: "13800000000" }),
  })
  assert.equal(registered.username, username)
  assert.ok(registered.token)

  const loggedIn = await request("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  })
  assert.equal(loggedIn.username, username)
  assert.ok(loggedIn.token)

  await requestBusinessError("/api/auth/register", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  })

  const components = await request("/api/components/all")
  assert.equal(components.cpu.length, 30)
  assert.equal(components.gpu.length, 30)

  const lowStock = await request("/api/components/low-stock")
  assert.equal(Array.isArray(lowStock), true)

  const articles = await request("/api/knowledge")
  assert.equal(articles.length >= 8, true)

  await requestBusinessError("/api/inventory/out", {
    method: "POST",
    body: JSON.stringify({ componentId: "cpu-003", quantity: -1, remark: "invalid quantity" }),
  })

  await requestBusinessError("/api/orders", {
    method: "POST",
    body: JSON.stringify({ customer: username, phone: "13800000000", totalPrice: 0, items: [] }),
  })

  const orderNo = await request("/api/orders", {
    method: "POST",
    body: JSON.stringify({
      customer: username,
      phone: "13800000000",
      totalPrice: 2199,
      items: [{ type: "cpu", componentId: "cpu-003" }],
    }),
  })
  assert.match(orderNo, /^ORD-\d{8}-\d{3}$/)

  const orders = await request("/api/orders")
  assert.equal(orders.some((order) => order.orderNo === orderNo), true)

  console.log("API smoke tests passed")
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
