DROP DATABASE IF EXISTS pc_diy_dump;
CREATE DATABASE pc_diy_dump DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pc_diy_dump;
-- 创建数据库
CREATE DATABASE IF NOT EXISTS pc_diy DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pc_diy;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password_hash VARCHAR(128) NOT NULL COMMENT '密码哈希',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色: user/admin',
    status TINYINT DEFAULT 1 COMMENT '状态 1正常 0禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 配件表
CREATE TABLE IF NOT EXISTS components (
    id VARCHAR(32) PRIMARY KEY COMMENT '配件编号, 如 cpu-001',
    type VARCHAR(20) NOT NULL COMMENT '配件类型',
    brand VARCHAR(50) NOT NULL COMMENT '品牌',
    model VARCHAR(100) NOT NULL COMMENT '型号',
    specs JSON COMMENT '规格参数(JSON格式)',
    price DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '价格',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    image VARCHAR(255) DEFAULT NULL COMMENT '图片URL',
    status TINYINT DEFAULT 1 COMMENT '状态 1上架 0下架',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type (type),
    INDEX idx_brand (brand)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配件表';

-- 配置方案表
CREATE TABLE IF NOT EXISTS configurations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '方案名称',
    customer VARCHAR(50) DEFAULT NULL COMMENT '客户姓名',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    note TEXT COMMENT '备注',
    total_price DECIMAL(10,2) DEFAULT 0 COMMENT '总价',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态: draft/pending/ordered',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配置方案表';

-- 配置方案明细表
CREATE TABLE IF NOT EXISTS configuration_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_id BIGINT NOT NULL COMMENT '配置方案ID',
    component_type VARCHAR(20) NOT NULL COMMENT '配件类型',
    component_id VARCHAR(32) NOT NULL COMMENT '配件ID',
    component_name VARCHAR(150) COMMENT '配件名称快照',
    price DECIMAL(10,2) DEFAULT 0 COMMENT '当时价格',
    FOREIGN KEY (config_id) REFERENCES configurations(id) ON DELETE CASCADE,
    INDEX idx_config (config_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配置方案明细表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    customer VARCHAR(50) NOT NULL COMMENT '客户姓名',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    wechat VARCHAR(50) DEFAULT NULL COMMENT '微信号',
    note TEXT COMMENT '客户备注',
    config_id BIGINT DEFAULT NULL COMMENT '关联配置方案ID',
    total_price DECIMAL(10,2) DEFAULT 0 COMMENT '订单总价',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending/confirmed/preparing/assembling/completed/cancelled',
    admin_note TEXT COMMENT '管理员备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_no (order_no),
    INDEX idx_customer (customer),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 库存变更记录表
CREATE TABLE IF NOT EXISTS inventory_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    component_id VARCHAR(32) NOT NULL COMMENT '配件ID',
    component_name VARCHAR(150) COMMENT '配件名称快照',
    type VARCHAR(10) NOT NULL COMMENT '类型: in/out',
    quantity INT NOT NULL COMMENT '数量',
    before_stock INT DEFAULT 0 COMMENT '变更前库存',
    after_stock INT DEFAULT 0 COMMENT '变更后库存',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    operator VARCHAR(50) DEFAULT 'system' COMMENT '操作人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_component (component_id),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存变更记录表';

-- 知识库文章表
CREATE TABLE IF NOT EXISTS knowledge_articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '标题',
    summary TEXT COMMENT '摘要',
    content LONGTEXT COMMENT '文章内容(Markdown)',
    category VARCHAR(50) DEFAULT 'general' COMMENT '分类',
    tags VARCHAR(255) DEFAULT NULL COMMENT '标签(逗号分隔)',
    author VARCHAR(50) DEFAULT 'DIY老手' COMMENT '作者',
    read_time INT DEFAULT 5 COMMENT '阅读时长(分钟)',
    cover VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    views INT DEFAULT 0 COMMENT '浏览量',
    status TINYINT DEFAULT 1 COMMENT '状态 1发布 0草稿',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库文章表';


