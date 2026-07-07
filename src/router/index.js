import { createRouter, createWebHashHistory } from "vue-router"

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

  { path: "/:pathMatch(.*)*", redirect: "/" },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

export default router
