# PC DIY 智能装机系统

> 一台属于你的理想电脑，从 DIY 开始

## 功能模块

### 用户端
- **首页** - 品牌介绍、热门方案推荐、使用流程
- **自定义装机** - 交互式配件选择、实时兼容性检测、价格计算
- **推荐配置** - 从入门到发烧 4 套精选方案
- **选购指南** - CPU/显卡/内存选购知识
- **订单查询** - 查看订单状态

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

## 数据

数据库包含 **240 个配件**（8 品类 × 30 件），价格参考京东/电商平台实价。
