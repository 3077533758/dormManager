<template>
  <div class="login-page">
    <div class="login-card">
      <!-- 左侧背景图片 -->
      <div :style="{ backgroundImage: `url(${loginImage})` }" class="left-panel-img"></div>

      <!-- 右侧表单 -->
      <div class="right-panel-form">
        <div class="form-wrapper">
          <h1 class="form-title">高校宿舍管理系统</h1>
          <el-form ref="form" :model="form" :rules="rules" class="login-el-form">
            <el-form-item prop="username">
              <el-input v-model="form.username" required placeholder="账号" class="login-input"></el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input type="password" v-model="form.password" required placeholder="密码" show-password class="login-input"></el-input>
            </el-form-item>
            
            <div class="identity-selector">
              <span class="selector-label">选择端：</span>
              <div
                v-for="(role) in roles"
                :key="role.value"
                class="role-option"
                @click="setIdentity(role.value)"
              >
                <span :class="['role-dot', { active: form.identity === role.value }]"></span>
                <span :class="['role-label', { active: form.identity === role.value }]">{{ role.label }}</span>
              </div>
            </div>

            <el-button 
              type="primary" 
              class="login-submit-btn"
              @click="login" 
              :disabled="!isFormValid"
            >
              登录
            </el-button>
          </el-form>
          <div class="copyright-text">© 2025 高校宿舍管理系统</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import request from "@/utils/request";
import { ElMessage } from "element-plus";

export default {
    name: "Login",
    data() {
        return {
            form: {
                username: "",
                password: "",
                identity: "stu", // 默认选中学生
            },
            roles: [
                { label: '学生', value: 'stu' },
                { label: '宿管', value: 'dormManager' },
                { label: '管理员', value: 'admin' },
            ],
            rules: {
                username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
                password: [{ required: true, message: "请输入密码", trigger: "blur" }],
            },
            images: {
                stu: require('@/assets/images/student-login.jpg'),
                dormManager: require('@/assets/images/manager-login.jpg'),
                admin: require('@/assets/images/admin-login.jpg'),
            }
        };
    },
    computed: {
        isFormValid() {
            const { username, password, identity } = this.form;
            return Boolean(username && password && identity);
        },
        loginImage() {
            return this.images[this.form.identity] || this.images.stu;
        }
    },
    methods: {
        setIdentity(identity) {
            this.form.identity = identity;
        },
        login() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    request.post("/" + this.form.identity + "/login", this.form).then((res) => {
                        if (res.code === "0") {
                            ElMessage({
                                message: "登陆成功",
                                type: "success",
                            });
                            // 登陆成功跳转主页
                            window.sessionStorage.setItem("user", JSON.stringify(res.data));
                            window.sessionStorage.setItem("identity", JSON.stringify(this.form.identity));
                            this.$router.replace({ path: "/home" });
                        } else {
                            ElMessage({
                                message: res.msg,
                                type: "error",
                            });
                        }
                    });
                }
            });
        },
    },
};
</script>
<style scoped>
html, body {
    height: 100%;
    margin: 0;
    padding: 0;
    font-family: 'Inter', sans-serif;
}

.login-page {
    background-color: #f0f2f5;
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
}

.login-card {
    width: 100%;
    max-width: 896px; 
    height: 520px;
    margin: 0 auto;
    border-radius: 1rem;
    overflow: hidden;
    background-color: white;
    box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
    display: flex;
}

.left-panel-img {
    flex: 1 1 40%;
    min-width: 40%;
    height: 100%;
    background-size: cover;
    background-position: center center;
    transition: background-image 0.5s;
}

.right-panel-form {
    flex: 1 1 60%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 2.5rem 2rem;
    height: 100%;
    background-color: white;
}

.form-wrapper {
    width: 100%;
    max-width: 320px;
    margin: 0 auto;
}

.form-title {
    font-size: 1.5rem;
    font-weight: 700;
    color: #1f2937;
    margin-bottom: 1.5rem;
    text-align: center;
}

.login-el-form {
    width: 100%;
}

.login-el-form .el-form-item {
    margin-bottom: 1rem;
}

.login-input :deep(.el-input__wrapper) {
    background-color: #f9fafb !important;
    box-shadow: none !important;
    border: 1px solid #e5e7eb;
    border-radius: 0.375rem;
    padding: 0.1rem 0.75rem;
    height: 42px;
}

.login-input :deep(.el-input__wrapper.is-focus) {
    border-color: #3b82f6;
}

.login-input :deep(.el-input__inner) {
    font-size: 0.875rem;
    background-color: #f9fafb !important;
}

.identity-selector {
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 1.5rem 0;
}

.selector-label {
    color: #6b7280;
    font-size: 0.875rem;
    margin-right: 0.5rem;
    vertical-align: middle;
}

.role-option {
    display: inline-flex;
    align-items: center;
    cursor: pointer;
    margin: 0 4px;
}

.role-dot {
    width: 16px;
    height: 16px;
    border-radius: 50%;
    display: inline-block;
    border: 2px solid #fff;
    background: rgba(0,0,0,0.15);
    cursor: pointer;
    transition: box-shadow 0.2s, background 0.2s;
    vertical-align: middle;
}

.role-dot.active {
    background: #3b82f6;
    box-shadow: 0 0 0 2px #fff, 0 2px 8px 0 rgba(59, 130, 246, 0.27);
    border-color: #3b82f6;
}

.role-label {
    font-size: 14px;
    color: #6b7280;
    margin-left: 8px;
    cursor: pointer;
    vertical-align: middle;
    transition: color 0.2s;
}

.role-label.active {
    color: #3b82f6;
    font-weight: 600;
}

.login-submit-btn {
    width: 100%;
    padding: 12px 0;
    font-weight: 600;
    font-size: 1rem;
    border-radius: 0.375rem;
    transition: background-color 0.2s;
    height: 42px;
}

.copyright-text {
    margin-top: 1.5rem;
    text-align: center;
    font-size: 0.75rem;
    color: #d1d5db;
}

@media (max-width: 768px) {
    .login-card {
        flex-direction: column;
        height: auto;
        max-width: 90%;
    }
    .left-panel-img {
        min-height: 180px;
        height: 180px;
        border-radius: 0 0 1.5rem 1.5rem;
    }
    .right-panel-form {
        min-height: auto;
        padding: 2rem 1.5rem;
    }
}
</style>
