<template>
  <el-card class="main-card">
    <!-- 学生没有宿舍时的提示 -->
    <div v-if="isStudent && !hasRoom" class="no-room-alert">
      <el-alert
          title="宿舍状态提醒"
          description="您当前没有宿舍。如需帮助请联系宿管。"
          type="warning"
          :closable="false"
          show-icon
      >
        <template #default>
          <div class="alert-content">
            <span>您当前没有宿舍。如需帮助请联系宿管。</span>
            <el-button type="primary" size="small" @click="refreshRoomStatus">
              刷新状态
            </el-button>
          </div>
        </template>
      </el-alert>
    </div>
    <!-- 头部数据 -->
    <div class="stats-section">
      <el-row :gutter="30" class="topInfo">
        <el-col :span="6">
          <div class="stat-card gradient-blue">
            <div class="stat-label">学生统计</div>
            <div class="stat-value">{{ this.studentNum }}</div>
            <div class="stat-desc">当前分类总记录数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card gradient-green">
            <div class="stat-label">住宿人数</div>
            <div class="stat-value">{{ this.haveRoomStudentNum }}</div>
            <div class="stat-desc">当前分类总记录数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card gradient-orange">
            <div class="stat-label">报修统计</div>
            <div class="stat-value">{{ this.repairOrderNum }}</div>
            <div class="stat-desc">当前分类总记录数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card gradient-purple">
            <div class="stat-label">空宿舍统计</div>
            <div class="stat-value">{{ this.noFullRoomNum }}</div>
            <div class="stat-desc">当前分类总记录数</div>
          </div>
        </el-col>
      </el-row>
    </div>
    <!-- 下部 -->
    <div class="notice-chart-section">
      <div class="notice-section">
        <div class="notice-header">
          <div class="notice-title-section">
            <i class="el-icon-bell notice-icon"></i>
            <span class="notice-title-text">最新通告</span>
          </div>
          <div class="notice-count">{{ activities.length }} 条</div>
        </div>
        <div class="notice-list-container">
          <div
              v-for="(activity, index) in activities"
              :key="index"
              class="notice-card"
              @click="showNoticeDetail(activity)"
          >
            <div class="notice-card-header">
              <div class="notice-card-title">{{ getCleanContent(activity.title) }}</div>
              <div class="notice-card-badge" v-if="index < 3">NEW</div>
            </div>
            <div class="notice-card-preview" v-if="activity.content || activity.noticeContent">
              {{ getCleanContent(activity.content || activity.noticeContent).slice(0, 80) }}<span v-if="getCleanContent(activity.content || activity.noticeContent).length > 80">...</span>
            </div>
            <div class="notice-card-footer">
              <div class="notice-card-meta">
                <span class="notice-author" v-if="activity.author">
                  <i class="el-icon-user"></i>
                  {{ activity.author }}
                </span>
                <span class="notice-time">
                  <i class="el-icon-time"></i>
                  {{ formatTime(activity.releaseTime) }}
                </span>
              </div>
              <div class="notice-card-action">
                <i class="el-icon-arrow-right"></i>
              </div>
            </div>
          </div>
          <div v-if="activities.length === 0" class="empty-notice">
            <i class="el-icon-document"></i>
            <p>暂无通告</p>
          </div>
        </div>
      </div>
      <div class="chart-section">
        <div style="width:100%">
          <div class="chart-title">宿舍人数统计图</div>
          <home_echarts />
        </div>
      </div>
    </div>
    <el-dialog 
      v-model="noticeDialogVisible" 
      title="通告详情" 
      width="600px" 
      :close-on-click-modal="true" 
      class="notice-dialog"
      :show-close="true"
      :before-close="handleDialogClose"
    >
      <div v-if="currentNotice" class="dialog-content-wrapper">
        <div class="dialog-title">{{ getCleanContent(currentNotice.title) }}</div>
        <div class="dialog-meta">
          <span v-if="currentNotice.author" class="dialog-author">发布人：{{ currentNotice.author }}</span>
          <span class="dialog-time">发布时间：{{ currentNotice.releaseTime }}</span>
        </div>
        <div class="dialog-content" v-html="formatContent(currentNotice.content || currentNotice.noticeContent)"></div>
      </div>
      <template #footer>
        <el-button @click="noticeDialogVisible = false" type="primary">关闭</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
<script>
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import home_echarts from "@/components/home_echarts.vue";
export default {
  name: "Home",
  components: { home_echarts },
  data() {
    return {
      studentNum: "",
      haveRoomStudentNum: "",
      repairOrderNum: "",
      noFullRoomNum: "",
      activities: [],
      hasRoom: true,
      isStudent: false,
      noticeDialogVisible: false,
      currentNotice: null,
      pollTimer: null,
    };
  },
  created() {
    this.checkUserStatus();
    this.getHomePageNotice();
    this.pollStats();
  },
  beforeUnmount() {
    if (this.pollTimer) clearInterval(this.pollTimer);
  },
  methods: {
    checkUserStatus() {
      const user = JSON.parse(sessionStorage.getItem("user"));
      const identity = JSON.parse(sessionStorage.getItem("identity"));
      if (identity === 'stu') {
        this.isStudent = true;
        this.checkRoomStatus(user.username);
      }
    },
    checkRoomStatus(username) {
      request.get("/main/getStudentRoomStatus/" + username).then((res) => {
        if (res.code === "0") {
          this.hasRoom = true;
        } else {
          this.hasRoom = false;
          ElMessage({
            message: "您当前没有宿舍。如需帮助请联系宿管。",
            type: "warning",
            duration: 5000
          });
        }
      }).catch(() => {
        this.hasRoom = false;
        ElMessage({
          message: "您当前没有宿舍。如需帮助请联系宿管。",
          type: "warning",
          duration: 5000
        });
      });
    },
    refreshRoomStatus() {
      const user = JSON.parse(sessionStorage.getItem("user"));
      if (user && user.username) {
        this.checkRoomStatus(user.username);
        // 重新获取统计信息和公告
        this.getStuNum();
        this.getHaveRoomNum();
        this.getOrderNum();
        this.getNoFullRoom();
        this.getHomePageNotice();
      }
    },
    getStuNum() {
      request.get("/stu/stuNum").then((res) => {
        if (res.code === "0") {
          this.studentNum = res.data;
        } else {
          ElMessage({ message: res.msg, type: "error" });
        }
      });
    },
    getHaveRoomNum() {
      request.get("/room/selectHaveRoomStuNum").then((res) => {
        if (res.code === "0") {
          this.haveRoomStudentNum = res.data;
        } else {
          ElMessage({ message: res.msg, type: "error" });
        }
      });
    },
    getOrderNum() {
      request.get("/repair/orderNum").then((res) => {
        if (res.code === "0") {
          this.repairOrderNum = res.data;
        } else {
          ElMessage({ message: res.msg, type: "error" });
        }
      });
    },
    getNoFullRoom() {
      request.get("/room/noFullRoom").then((res) => {
        if (res.code === "0") {
          this.noFullRoomNum = res.data;
        } else {
          ElMessage({ message: res.msg, type: "error" });
        }
      });
    },
    getHomePageNotice() {
      request.get("/notice/homePageNotice").then((res) => {
        if (res.code === "0") {
          this.activities = res.data;
        } else {
          ElMessage({ message: res.msg, type: "error" });
        }
      });
    },
    showNoticeDetail(activity) {
      this.currentNotice = activity;
      this.noticeDialogVisible = true;
    },
    pollStats() {
      // Initial fetch
      this.getStuNum();
      this.getHaveRoomNum();
      this.getOrderNum();
      this.getNoFullRoom();
      this.getHomePageNotice();
      // Poll every 5 seconds
      this.pollTimer = setInterval(() => {
        this.getStuNum();
        this.getHaveRoomNum();
        this.getOrderNum();
        this.getNoFullRoom();
        this.getHomePageNotice();
      }, 5000);
    },
    addRipple(e) {
      // 动态添加点击波纹动画
      const target = e.currentTarget;
      const ripple = document.createElement('span');
      ripple.className = 'ripple-effect';
      const rect = target.getBoundingClientRect();
      const size = Math.max(rect.width, rect.height);
      ripple.style.width = ripple.style.height = size + 'px';
      ripple.style.left = (e.clientX - rect.left - size / 2) + 'px';
      ripple.style.top = (e.clientY - rect.top - size / 2) + 'px';
      target.appendChild(ripple);
      setTimeout(() => {
        ripple.remove();
      }, 600);
    },
    getCleanContent(content) {
      if (!content) return '';
      // 先解码HTML实体字符，然后移除HTML标签
      const tempDiv = document.createElement('div');
      tempDiv.innerHTML = content;
      const decodedContent = tempDiv.textContent || tempDiv.innerText || '';
      // 移除HTML标签但保留文本内容
      return decodedContent.replace(/<[^>]*>/g, '');
    },
    formatContent(content) {
      if (!content) return '';
      // 安全地处理HTML内容，只允许基本的格式化标签
      const allowedTags = ['p', 'br', 'strong', 'b', 'em', 'i', 'u', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'ul', 'ol', 'li'];
      const cleanContent = content.replace(/<[^>]*>/g, (match) => {
        const tagName = match.match(/<\/(\w+)|<(\w+)/);
        if (tagName && allowedTags.includes(tagName[1] || tagName[2])) {
          return match;
        }
        return '';
      });
      return cleanContent;
    },
    handleDialogClose() {
      this.noticeDialogVisible = false;
    },
    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      const now = new Date();
      const diff = now - date;
      const days = Math.floor(diff / (1000 * 60 * 60 * 24));
      const hours = Math.floor(diff / (1000 * 60 * 60));
      const minutes = Math.floor(diff / (1000 * 60));
      
      if (days > 0) {
        return `${days}天前`;
      } else if (hours > 0) {
        return `${hours}小时前`;
      } else if (minutes > 0) {
        return `${minutes}分钟前`;
      } else {
        return '刚刚';
      }
    },
  },
};
</script>
<style scoped>
.main-card {
  margin: 24px auto;
  min-height: calc(100vh - 80px);
  max-width: 1400px;
  background: linear-gradient(135deg, #f8fafc 0%, #e9f0fb 100%);
  border-radius: 24px;
  box-shadow: 0 8px 40px 0 rgba(80,120,200,0.13);
  padding: 36px 36px 48px 36px;
  border: none;
}
.no-room-alert {
  margin-bottom: 28px;
}
.alert-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.stats-section {
  margin-bottom: 48px;
}
.stat-card {
  border-radius: 18px;
  box-shadow: 0 4px 24px 0 rgba(80,120,200,0.10);
  padding: 36px 0 28px 0;
  text-align: center;
  background: #fff;
  transition: box-shadow 0.3s, transform 0.3s;
  cursor: pointer;
  min-height: 170px;
  position: relative;
  overflow: hidden;
}
.stat-card:hover {
  box-shadow: 0 12px 36px rgba(80,120,200,0.22), 0 0 0 2px #4e8cff33;
  transform: translateY(-4px) scale(1.03);
}
.stat-label {
  font-size: 18px;
  font-weight: 600;
  color: #3a4a5d;
  margin-bottom: 18px;
  letter-spacing: 0.5px;
}
.stat-value {
  font-size: 48px;
  font-weight: 800;
  color: #1a237e;
  margin-bottom: 10px;
  letter-spacing: 1px;
  font-family: 'Segoe UI', 'Arial', sans-serif;
  line-height: 1.1;
}
.stat-desc {
  color: #8fa2b7;
  font-size: 15px;
  margin-top: 6px;
  letter-spacing: 0.5px;
}
.gradient-blue {
  background: linear-gradient(135deg, #e3f0ff 0%, #c9e7ff 100%);
}
.gradient-green {
  background: linear-gradient(135deg, #e6fff7 0%, #c2ffe7 100%);
}
.gradient-orange {
  background: linear-gradient(135deg, #fff5e6 0%, #ffe0c2 100%);
}
.gradient-purple {
  background: linear-gradient(135deg, #f3e6ff 0%, #e0c2ff 100%);
}
.notice-chart-section {
  display: flex;
  flex-direction: row;
  gap: 36px;
  justify-content: space-between;
  align-items: flex-start;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  margin-top: 40px;
}
.notice-section {
  flex: 1 1 0;
  min-width: 0;
  max-width: 600px;
}
.chart-section {
  flex: 1 1 0;
  min-width: 0;
  max-width: 700px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f8fafc 0%, #e9f0fb 100%);
  border-radius: 18px;
  box-shadow: 0 2px 24px 0 rgba(80,120,200,0.10);
  padding: 32px 24px 32px 24px;
  margin-left: 0;
}
.notice-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  padding: 0 4px;
}
.notice-title-section {
  display: flex;
  align-items: center;
  gap: 12px;
}
.notice-icon {
  font-size: 24px;
  color: #4e8cff;
}
.notice-title-text {
  font-size: 22px;
  font-weight: 700;
  color: #2d3a4b;
  letter-spacing: 0.5px;
}
.notice-count {
  background: linear-gradient(135deg, #4e8cff 0%, #3b7de9 100%);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(78,140,255,0.3);
}
.notice-list-container {
  max-height: 700px;
  min-height: 420px;
  overflow-y: auto;
  padding: 8px 4px;
  scrollbar-width: thin;
  scrollbar-color: #b3c6e0 #f8fafc;
}
.notice-list-container::-webkit-scrollbar {
  width: 6px;
  background: transparent;
}
.notice-list-container::-webkit-scrollbar-thumb {
  background: #b3c6e0;
  border-radius: 3px;
}
.notice-card {
  position: relative;
  padding: 24px;
  margin-bottom: 16px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  border: 1px solid #e8f0fe;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}
.notice-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #4e8cff, #3b7de9);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}
.notice-card:hover::before {
  transform: scaleX(1);
}
.notice-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(78,140,255,0.15);
  border-color: #4e8cff;
}
.notice-card:active {
  transform: translateY(0);
  transition: transform 0.1s ease;
}
.notice-card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 12px;
}
.notice-card-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a237e;
  line-height: 1.4;
  flex: 1;
  margin-right: 12px;
}
.notice-card-badge {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 10px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  box-shadow: 0 2px 6px rgba(255,107,107,0.3);
}
.notice-card-preview {
  color: #64748b;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.notice-card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}
.notice-card-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 12px;
  color: #94a3b8;
}
.notice-author, .notice-time {
  display: flex;
  align-items: center;
  gap: 4px;
}
.notice-author i, .notice-time i {
  font-size: 14px;
}
.notice-card-action {
  color: #4e8cff;
  font-size: 16px;
  transition: transform 0.2s ease;
}
.notice-card:hover .notice-card-action {
  transform: translateX(4px);
}
.empty-notice {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: #94a3b8;
}
.empty-notice i {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.6;
}
.empty-notice p {
  font-size: 16px;
  margin: 0;
}
.notice-dialog >>> .el-dialog__body {
  padding-top: 12px;
  padding-bottom: 12px;
  background: linear-gradient(135deg, #f8fafc 0%, #e9f0fb 100%);
  border-radius: 12px;
}
.dialog-content-wrapper {
  animation: dialogFadeIn 0.3s ease-out;
}
@keyframes dialogFadeIn {
  0% {
    opacity: 0;
    transform: translateY(10px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}
.dialog-title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #1a237e;
  letter-spacing: 1px;
}
.dialog-meta {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-bottom: 10px;
}
.dialog-author {
  color: #4e8cff;
  font-size: 15px;
  font-weight: 600;
}
.dialog-time {
  color: #8fa2b7;
  font-size: 15px;
}
.dialog-content {
  white-space: pre-line;
  max-height: 350px;
  overflow-y: auto;
  font-size: 17px;
  color: #3a4a5d;
  line-height: 1.85;
  background: #fff;
  border-radius: 8px;
  padding: 18px 18px 18px 18px;
  box-shadow: 0 2px 12px rgba(80,120,200,0.07);
  margin-top: 8px;
}
@media (max-width: 1200px) {
  .notice-chart-section {
    flex-direction: column;
    gap: 18px;
  }
  .chart-section, .notice-section {
    max-width: 100%;
    padding: 12px 2px 12px 2px;
  }
}
.chart-title {
  font-size: 26px;
  font-weight: 700;
  color: #1a237e;
  letter-spacing: 1px;
  margin-bottom: 18px;
  text-align: center;
  width: 100%;
}
</style>
