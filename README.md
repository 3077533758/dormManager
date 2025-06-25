# 🏠 宿舍管理系统 SunCaper/dormManager

一个基于 Spring Boot + Vue.js 的高校宿舍管理系统，支持宿舍入住、调换、退宿、外出住宿、报修、公告、访客管理、卫生管理、违纪管理、寝室评比等功能，适用于教务处或学生事务中心推动宿舍管理的数字化和自动化改造。

---

## 🧩 功能模块

### 👮 用户管理
- 学生管理
- 宿管与管理员管理
- 角色权限管理

### 🏢 公寓管理
- 园区管理
- 楼栋管理
- 房间管理
- 床位管理

### 🏠 住宿管理
- 学生入住管理
- 调宿申请与审批
- 退宿申请与审批
- 校外住宿申请

### 🧹 寝室管理
- 卫生检查管理
- 违纪记录管理
- 寝室评比管理

### 🛠 维修管理
- 报修申请
- 维修处理
- 维修统计

### 📢 信息管理
- 公告通知
- 访客登记管理


---

## 🛠 技术栈

### 后端技术
- **框架**：Spring Boot 2.6.3
- **数据库操作**：MyBatis-Plus 3.5.1
- **数据库**：MySQL 8.4.5
- **依赖管理**：Maven 3.9.9
- **工具库**：Lombok、Hutool、FastJSON

### 前端技术
- **框架**：Vue 3.0
- **UI组件库**：Element Plus 2.0.1
- **路由**：Vue Router 4.0.13
- **状态管理**：Vuex 4.0.0
- **HTTP客户端**：Axios 0.25.0
- **图表库**：ECharts 5.3.0

---

## 🖥️ 开发环境

### ✅ 软件要求(Recommended)

- JDK 1.8
- Maven 3.9.9
- MySQL 8.4.5
- Node.js 14.21.3
- IDE 推荐：IntelliJ IDEA / VS Code

### ✅ 数据库初始化

1. 创建数据库：`db1`
2. 执行项目根目录下的 `springboot/db/dormitory.sql` 脚本，初始化所有表结构及测试数据
3. 配置数据库连接（推荐使用 `application-local.yml`，避免误提交敏感信息）：

   - 创建一个 `springboot/src/main/resources/application-local.yml` 文件，内容如下（仅供参考）：

   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://127.0.0.1:3306/db1?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false
       username: root
       password: your_password
  - 项目已默认激活 local 配置，Spring Boot 将自动加载此文件。
  - **请勿将此文件提交到 Git**（已在 .gitignore 中排除）。



### ✅ 快速启动

#### 方式一：使用启动脚本（推荐）
```bash
# Linux/Mac
./start.sh
```
该脚本会自动执行以下操作：

- 检查是否使用非 root 用户运行
- 检查 JDK 版本是否为 1.8
- 自动解析 application-local.yml 或 application.yml 中的数据库配置
- 使用 nc 测试数据库端口是否可连接
- 后台并发启动后端（Spring Boot）和前端（Vue）

按下 Ctrl+C 可安全终止两个进程。
#### 方式二：手动启动
```bash
# 1. 启动后端
cd springboot
mvn spring-boot:run

# 2. 启动前端（新终端）
cd vue
npm install
npm run serve
```

### ✅ 访问地址
- http://localhost:8080

### ✅ 默认账号
- 管理员：admin / 123456
- 宿管：dorm1 / 123456
- 学生：stu1 / 123456

---

## 📋 新增功能

### 🧹 卫生管理
- 卫生检查记录管理
- 评分标准：门窗、物品放置、被褥、清洁、整体效果
- 自动计算总分和等级（优/良/中/差）
- 支持按房间查询历史记录

### ⚠️ 违纪管理
- 违纪记录登记
- 违纪类型：使用大功率电器、宿舍吵闹、晚归、夜不归宿等
- 扣分管理
- 处理流程：待处理 → 已处理/已驳回

### ⭐ 寝室评比
- 星级寝室评选
- 评比指标：卫生得分、人均学分绩点
- 星级等级：二星、三星、四星、五星
- 评比周期管理

### 📊 统计报表(not implemented yet)

---

## 🗂️ 项目结构

```
dormManager/
├── springboot/                 # 后端项目
│   ├── src/main/java/
│   │   └── com/example/springboot/
│   │       ├── controller/     # 控制器层
│   │       ├── service/        # 服务层
│   │       ├── entity/         # 实体类
│   │       ├── mapper/         # 数据访问层
│   │       └── common/         # 公共组件
│   ├── src/main/resources/
│   │   ├── application.yml     # 配置文件
│   │   └── mapper/            # MyBatis映射文件
│   └── db/
│       └── dormitory.sql      # 数据库脚本
├── vue/                       # 前端项目
│   ├── src/
│   │   ├── views/             # 页面组件
│   │   ├── components/        # 公共组件
│   │   ├── router/            # 路由配置
│   │   ├── store/             # 状态管理
│   │   └── utils/             # 工具类
│   └── package.json
└── README.md
```

---

## 🔧 开发指南

### 后端开发
1. 创建实体类（Entity）
2. 创建Mapper接口
3. 创建Service接口和实现类
4. 创建Controller控制器
5. 配置路由和权限

### 前端开发
1. 创建Vue组件
2. 配置路由
3. 实现API调用
4. 添加菜单项

---

## 📞 联系方式

- 项目地址：https://github.com/3077533758/dormManager
- 问题反馈：https://github.com/3077533758/dormManager/issues
- 邮箱：harmfulski@gmail.com

---

**⭐ 如果这个项目对你有帮助，请给它一个星标！**
