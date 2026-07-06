# PC DIY 智能装机系统

> 一台属于你的理想电脑，从 DIY 开始

## 功能模块

### 用户端
- **首页** - 品牌介绍、热门方案推荐、使用流程
- **自定义装机** - 交互式配件选择、实时兼容性检测、价格计算
- **推荐配置** - 从入门到发烧 4 套精选方案
- **选购指南** - CPU/显卡/内存选购知识
- **订单查询** - 查看订单状态

### 管理后台 `/admin`
- **工作台** - 数据总览、快捷操作
- **配置管理** - 高级配置构建器
- **库存管理** - 入库/出库、低库存预警
- **订单管理** - 订单状态流转
- **知识库** - 文章管理

## 技术栈

| 前端 | 后端 | 数据库 |
|------|------|--------|
| Vue 3 + Vite | Spring Boot 3.2 | MySQL 5.7/8.0 |
| Element Plus | MyBatis-Plus | |
| Pinia + Vue Router | RESTful API | |

### 设计融合
Linear 深色侧边栏 × Stripe 白色画布 × Notion 彩色卡片

## 本地开发

```bash
# 1. MySQL 建库
mysql -u root < backend/src/main/resources/schema.sql

# 2. 启动后端 (端口 8080)
cd backend
mvn spring-boot:run

# 3. 启动前端 (端口 5173)
npm run dev
```

访问 http://localhost:5173

## 部署

### 纯静态托管

这个版本适合直接托管成静态站点，不再依赖后端。

1. 本地执行：

```bash
npm run build
```

2. 把生成的 `dist/` 上传到：
- `Cloudflare Pages`
- `Netlify`
- `GitHub Pages`

3. 如果用 `GitHub Pages`，当前项目已切换为 `hash` 路由，刷新不会 404。

### 推荐方式

- **最快**：`Cloudflare Pages`
- **最省事**：`GitHub Pages`
- **最适合 SPA**：`Netlify`

### GitHub Pages

1. 把代码推到 `main`
2. 到仓库 `Settings -> Pages`
3. `Build and deployment` 选择 `Deploy from a branch`
4. 分支选 `gh-pages`，目录选 `/ (root)`
5. 推送后自动发布到 Pages

## 数据

数据库包含 **240 个配件**（8 品类 × 30 件），价格参考京东/电商平台实价。
