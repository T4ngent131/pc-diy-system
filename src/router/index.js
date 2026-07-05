import { createRouter, createWebHistory } from "vue-router"

const routes = [
  // ── 用户端（主入口） ──
  {
    path: "/",
    component: () => import("@/components/user/UserLayout.vue"),
    children: [
      { path: "", name: "UserHome", component: () => import("@/views/user/UserHome.vue"), meta: { title: "PC DIY 智能装机" } },
      { path: "build", name: "UserBuild", component: () => import("@/views/user/UserBuild.vue"), meta: { title: "自定义装机" } },
      { path: "plans", name: "UserPlans", component: () => import("@/views/user/UserPlans.vue"), meta: { title: "推荐配置" } },
      { path: "guides", name: "UserGuides", component: () => import("@/views/user/UserGuides.vue"), meta: { title: "选购指南" } },
      { path: "guides/:id", name: "UserGuideDetail", component: () => import("@/views/user/UserGuideDetail.vue"), meta: { title: "指南详情" } },
      { path: "order", name: "UserOrder", component: () => import("@/views/user/UserOrder.vue"), meta: { title: "提交订单" } },
      { path: "track", name: "UserTrack", component: () => import("@/views/user/UserTrack.vue"), meta: { title: "订单查询" } },
    ],
  },

  // ── 管理后台 ──
  {
    path: "/admin",
    component: () => import("@/components/layout/AppLayout.vue"),
    redirect: "/admin/dashboard",
    children: [
      { path: "dashboard", name: "Dashboard", component: () => import("@/views/Dashboard.vue"), meta: { title: "工作台" } },
      { path: "config", name: "ConfigBuilder", component: () => import("@/views/config/ConfigBuilder.vue"), meta: { title: "配置方案" } },
      { path: "inventory", name: "Inventory", component: () => import("@/views/inventory/Inventory.vue"), meta: { title: "库存管理" } },
      { path: "orders", name: "Orders", component: () => import("@/views/orders/Orders.vue"), meta: { title: "订单管理" } },
      { path: "knowledge", name: "Knowledge", component: () => import("@/views/knowledge/Knowledge.vue"), meta: { title: "知识库" } },
      { path: "knowledge/:id", name: "ArticleDetail", component: () => import("@/views/knowledge/ArticleDetail.vue"), meta: { title: "文章详情" } },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
