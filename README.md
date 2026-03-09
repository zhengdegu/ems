# EMS - Equipment Management System
设备管理系统

## 项目简介
企业级设备管理系统，提供设备全生命周期管理、预防性维护、工单管理、实时告警监控等功能。

## 技术栈

### 后端
- Spring Boot 3.2.3
- Java 17
- MyBatis Plus 3.5.5
- SQLite
- Spring Security + JWT
- Maven

### 前端
- Vue 3
- Vite
- Element Plus
- Vue Router 4
- Pinia
- Axios
- ECharts
- Vue I18n (国际化)

## 项目结构
```
ems/
├── ems-prototype/      # HTML原型
├── ems-backend/        # Spring Boot后端
└── ems-frontend/       # Vue 3前端
```

## 功能模块

### 核心功能
- 🔐 用户认证与权限管理
- 📊 工作台数据概览
- 🖥️ 设备台账管理
- 🔧 维护计划管理
- 📋 工单流程管理
- 🚨 告警监控与规则
- 📦 备件库存管理
- 👥 用户与角色管理
- ⚙️ 系统设置
- 📈 数据报表统计
- 🔔 消息通知
- 🌍 国际化支持（中英文）

## 快速开始

### 后端启动
```bash
cd ems-backend
mvn spring-boot:run
```
访问: http://localhost:8080

### 前端启动
```bash
cd ems-frontend
npm install
npm run dev
```
访问: http://localhost:5173

## API文档
后端提供完整的RESTful API，包含：
- 认证模块 (`/api/auth`)
- 设备管理 (`/api/equipment`)
- 维护计划 (`/api/maintenance-plan`)
- 工单管理 (`/api/work-order`)
- 告警管理 (`/api/alarm`)
- 用户管理 (`/api/user`)
- 系统设置 (`/api/system`)
- 等...

详见 `ems-frontend/API_COMPARISON.md`

## 国际化
系统支持中英文切换，点击顶部导航栏地球图标即可切换语言。

详见 `ems-frontend/I18N_GUIDE.md`

## 默认账号
- 用户名: admin
- 密码: admin123

## 开发团队
EMS Development Team

## 许可证
MIT License
