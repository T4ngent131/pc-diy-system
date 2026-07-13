<template>
  <div class="auth-page">
    <section class="auth-shell">
      <div class="auth-copy">
        <span class="eyebrow">PCDIY Account</span>
        <h1>{{ isRegister ? "创建装机账号" : "欢迎回来" }}</h1>
        <p>{{ isRegister ? "保存配置、提交订单、跟踪装机进度，从一个账号开始。" : "登录后继续提交配置单，并随时查看订单进度。" }}</p>
      </div>

      <el-card class="auth-card">
        <template #header>
          <div class="card-header">
            <span>{{ isRegister ? "注册" : "登录" }}</span>
            <el-button link type="primary" @click="toggleMode">
              {{ isRegister ? "已有账号？登录" : "没有账号？注册" }}
            </el-button>
          </div>
        </template>

        <el-form :model="form" label-position="top" @keyup.enter="handleSubmit">
          <el-form-item label="用户名">
            <el-input v-model.trim="form.username" placeholder="至少 3 位字符" prefix-icon="User" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" show-password placeholder="至少 6 位密码" prefix-icon="Lock" />
          </el-form-item>
          <el-form-item v-if="isRegister" label="手机号">
            <el-input v-model.trim="form.phone" placeholder="选填，用于订单联系" prefix-icon="Phone" />
          </el-form-item>
          <el-button type="primary" class="submit-btn" :loading="authStore.loading" @click="handleSubmit">
            {{ isRegister ? "注册并登录" : "登录" }}
          </el-button>
        </el-form>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { computed, reactive } from "vue"
import { useRoute, useRouter } from "vue-router"
import { ElMessage } from "element-plus"
import { useAuthStore } from "@/stores/auth.js"

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const form = reactive({
  username: "",
  password: "",
  phone: "",
})

const mode = computed(() => route.query.mode)
const isRegister = computed(() => mode.value === "register")

function toggleMode() {
  router.replace({ path: "/auth", query: { mode: isRegister.value ? "login" : "register", redirect: route.query.redirect } })
}

async function handleSubmit() {
  if (form.username.length < 3) {
    ElMessage.warning("用户名至少 3 位")
    return
  }
  if (form.password.length < 6) {
    ElMessage.warning("密码至少 6 位")
    return
  }

  try {
    if (isRegister.value) {
      await authStore.register(form)
      ElMessage.success("注册成功")
    } else {
      await authStore.login(form)
      ElMessage.success("登录成功")
    }
    router.push(route.query.redirect || "/order")
  } catch (error) {
    ElMessage.error(error.message || "操作失败")
  }
}
</script>

<style scoped>
.auth-page {
  min-height: calc(100vh - 64px);
  padding: 56px 24px;
  background:
    linear-gradient(135deg, rgba(94, 106, 210, 0.08), rgba(38, 196, 133, 0.08)),
    var(--color-canvas);
}
.auth-shell {
  max-width: 980px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 420px;
  gap: 40px;
  align-items: center;
}
.auth-copy {
  padding: 24px 0;
}
.eyebrow {
  display: inline-flex;
  margin-bottom: 12px;
  font-size: 0.78rem;
  font-weight: 700;
  color: var(--color-primary);
}
.auth-copy h1 {
  font-size: 2.2rem;
  line-height: 1.15;
  margin-bottom: 12px;
  color: var(--color-text-primary);
}
.auth-copy p {
  max-width: 440px;
  color: var(--color-text-secondary);
  line-height: 1.8;
}
.auth-card {
  border-radius: var(--radius-lg);
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 700;
}
.submit-btn {
  width: 100%;
  margin-top: 4px;
}
@media (max-width: 768px) {
  .auth-shell {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  .auth-copy h1 {
    font-size: 1.7rem;
  }
}
</style>
