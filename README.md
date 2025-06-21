# 🏠 宿舍管理系统 DormManager

一个基于 Spring Boot 的高校宿舍管理系统后端服务，支持宿舍入住、调换、退宿、外出住宿、报修、公告、访客管理等功能，适用于教务处或学生事务中心推动宿舍管理的数字化和自动化改造。

---

## 🧩 功能模块

- 🎓 **学生管理**：学生信息的增删改查  
- 🏢 **宿舍楼与房间管理**：楼栋信息、房间容量、床位分配管理  
- 🔄 **调宿申请**：学生提交调宿申请，管理员审批后自动调整床位  
- 📤 **退宿申请**：学生发起退宿流程，审批后释放床位  
- 🏠 **外出住宿申请**：学生因特殊原因申请在校外住宿，支持审批记录  
- 🛠 **宿舍报修**：设施报修、进度跟踪、维修状态管理  
- 📢 **公告通知**：校园公告信息发布与查看  
- 👮 **宿管与管理员管理**：权限分级、人员信息维护  
- 🧾 **访客登记管理**：来访登记、事由记录（可选模块）  

---

## 🛠 技术栈

- **后端框架**：Spring Boot 2.6.3  
- **数据库操作**：MyBatis-Plus  
- **数据库**：MySQL 8.x  
- **依赖管理**：Maven  
- **工具库**：Lombok、PageHelper、FastJSON（如有使用）  
- **接口测试**：Swagger（如集成）、Postman、Insomnia  

---

## 🖥️ 开发环境

### ✅ 软件要求

- JDK 1.8+  
- Maven 3.6+  
- MySQL 8.0+  
- IDE 推荐：IntelliJ IDEA / VS Code  

### ✅ 数据库初始化

1. 创建数据库：`db1`  
2. 执行项目根目录下的 `dormitory.sql` 脚本，初始化所有表结构及测试数据  
3. 修改数据库配置（位于 `application.yml` 或 `application.properties`）：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/db1?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false
    username: root
    password: your_password
````
### ✅ 示例数据
- 默认管理员账号：admin / 123456
- 已包含测试学生、房间、公告、退宿、外宿、调宿、报修记录等数据

