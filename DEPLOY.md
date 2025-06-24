# 🚀 宿舍管理系统部署指南

本文档详细说明如何部署宿舍管理系统到生产环境。

---

## 📋 部署前准备

### 环境要求
- JDK 1.8+
- Maven 3.9.9+
- MySQL 8.4.5+
- Node.js 14.21.3+
- Nginx (可选，用于反向代理)

### 服务器配置建议
- CPU: 2核心以上
- 内存: 4GB以上
- 磁盘: 20GB以上
- 操作系统: CentOS 7+/Ubuntu 18+/Windows Server 2016+

---

## 🗄️ 数据库部署

### 1. 安装MySQL
```bash
# CentOS/RHEL
sudo yum install mysql-server

# Ubuntu/Debian
sudo apt-get install mysql-server

# 启动MySQL服务
sudo systemctl start mysqld
sudo systemctl enable mysqld
```

### 2. 创建数据库
```sql
CREATE DATABASE db1 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'dormuser'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON db1.* TO 'dormuser'@'%';
FLUSH PRIVILEGES;
```

### 3. 导入数据
```bash
mysql -u dormuser -p db1 < springboot/db/dormitory.sql
```

---

## 🔧 后端部署

### 1. 编译打包
```bash
cd springboot
mvn clean package -Dmaven.test.skip=true
```

### 2. 修改配置文件
编辑 `springboot/src/main/resources/application.yml`:
```yaml
server:
  port: 9090

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db1?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false
    username: dormuser
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

# 生产环境配置
spring:
  profiles:
    active: prod
```

### 3. 启动服务
```bash
# 方式一：直接运行
java -jar target/springboot-0.0.1-SNAPSHOT.jar

# 方式二：后台运行
nohup java -jar target/springboot-0.0.1-SNAPSHOT.jar > app.log 2>&1 &

# 方式三：使用systemd服务
sudo systemctl start dorm-manager
sudo systemctl enable dorm-manager
```

### 4. 创建systemd服务文件
```bash
sudo nano /etc/systemd/system/dorm-manager.service
```

```ini
[Unit]
Description=Dorm Manager Backend
After=network.target

[Service]
Type=simple
User=dormuser
WorkingDirectory=/opt/dorm-manager/springboot
ExecStart=/usr/bin/java -jar target/springboot-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

---

## 🎨 前端部署

### 1. 安装依赖
```bash
cd vue
npm install
```

### 2. 修改API地址
编辑 `vue/src/utils/request.js`:
```javascript
// 生产环境API地址
const baseURL = process.env.NODE_ENV === 'production' 
  ? 'http://your-domain.com:9090' 
  : 'http://localhost:9090'
```

### 3. 构建生产版本
```bash
npm run build
```

### 4. 部署到Web服务器

#### 使用Nginx
```bash
# 安装Nginx
sudo apt-get install nginx

# 配置Nginx
sudo nano /etc/nginx/sites-available/dorm-manager
```

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    location / {
        root /opt/dorm-manager/vue/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:9090/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
# 启用站点
sudo ln -s /etc/nginx/sites-available/dorm-manager /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

#### 使用Apache
```bash
# 安装Apache
sudo apt-get install apache2

# 配置虚拟主机
sudo nano /etc/apache2/sites-available/dorm-manager.conf
```

```apache
<VirtualHost *:80>
    ServerName your-domain.com
    DocumentRoot /opt/dorm-manager/vue/dist
    
    <Directory /opt/dorm-manager/vue/dist>
        AllowOverride All
        Require all granted
    </Directory>
    
    # 后端API代理
    ProxyPass /api/ http://localhost:9090/
    ProxyPassReverse /api/ http://localhost:9090/
</VirtualHost>
```

---

## 🔒 安全配置

### 1. 防火墙配置
```bash
# 开放必要端口
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 9090/tcp
sudo ufw enable
```

### 2. SSL证书配置
```bash
# 安装Let's Encrypt
sudo apt-get install certbot python3-certbot-nginx

# 获取SSL证书
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo crontab -e
# 添加：0 12 * * * /usr/bin/certbot renew --quiet
```

### 3. 数据库安全
```sql
-- 限制数据库访问
REVOKE ALL PRIVILEGES ON db1.* FROM 'dormuser'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON db1.* TO 'dormuser'@'localhost';
FLUSH PRIVILEGES;
```

---

## 📊 监控与日志

### 1. 应用日志
```bash
# 查看应用日志
tail -f /opt/dorm-manager/springboot/app.log

# 日志轮转
sudo nano /etc/logrotate.d/dorm-manager
```

```
/opt/dorm-manager/springboot/app.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 dormuser dormuser
    postrotate
        systemctl reload dorm-manager
    endscript
}
```

### 2. 系统监控
```bash
# 安装监控工具
sudo apt-get install htop iotop

# 监控系统资源
htop
iotop
```

---

## 🔄 更新部署

### 1. 备份数据
```bash
# 备份数据库
mysqldump -u dormuser -p db1 > backup_$(date +%Y%m%d_%H%M%S).sql

# 备份应用
tar -czf app_backup_$(date +%Y%m%d_%H%M%S).tar.gz /opt/dorm-manager/
```

### 2. 更新应用
```bash
# 停止服务
sudo systemctl stop dorm-manager

# 备份当前版本
cp /opt/dorm-manager/springboot/target/springboot-0.0.1-SNAPSHOT.jar /opt/dorm-manager/springboot/target/springboot-0.0.1-SNAPSHOT.jar.backup

# 部署新版本
cp new-springboot-0.0.1-SNAPSHOT.jar /opt/dorm-manager/springboot/target/

# 启动服务
sudo systemctl start dorm-manager
sudo systemctl status dorm-manager
```

---

## 🚨 故障排除

### 常见问题

#### 1. 应用无法启动
```bash
# 检查端口占用
netstat -tlnp | grep 9090

# 检查日志
tail -f /opt/dorm-manager/springboot/app.log

# 检查Java进程
ps aux | grep java
```

#### 2. 数据库连接失败
```bash
# 检查MySQL服务状态
sudo systemctl status mysqld

# 检查数据库连接
mysql -u dormuser -p -h localhost db1

# 检查防火墙
sudo ufw status
```

#### 3. 前端无法访问
```bash
# 检查Nginx状态
sudo systemctl status nginx

# 检查Nginx配置
sudo nginx -t

# 检查端口监听
netstat -tlnp | grep :80
```

---

## 📞 技术支持

如果遇到部署问题，请：

1. 查看应用日志：`tail -f /opt/dorm-manager/springboot/app.log`
2. 查看系统日志：`journalctl -u dorm-manager`
3. 检查网络连接：`ping your-domain.com`
4. 提交Issue：https://github.com/SunCaper/DormManager/issues

---

**🎉 部署完成后，访问 http://your-domain.com 即可使用系统！** 