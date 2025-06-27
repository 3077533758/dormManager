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
    <div class="notice-section">
      <div class="notice-header">宿舍通告</div>
      <div class="notice-list-scroll">
        <div
            v-for="(activity, index) in activities"
            :key="index"
            class="notice-item"
            @click="showNoticeDetail(activity)"
            @mousedown="addRipple($event)"
        >
          <div class="notice-title">{{ activity.title }}</div>
          <div class="notice-meta">
            <span class="notice-author" v-if="activity.author">{{ activity.author }}</span>
            <span class="notice-time">{{ activity.releaseTime }}</span>
          </div>
          <div class="notice-preview" v-if="activity.content || activity.noticeContent">
            {{ (activity.content || activity.noticeContent).replace(/<[^>]+>/g, '').slice(0, 60) }}<span v-if="(activity.content || activity.noticeContent).length > 60">...</span>
          </div>
        </div>
      </div>
    </div>
    <el-dialog v-model="noticeDialogVisible" title="通告详情" width="600px" :close-on-click-modal="true" class="notice-dialog">
      <div v-if="currentNotice">
        <div class="dialog-title">{{ currentNotice.title }}</div>
        <div class="dialog-meta">
          <span v-if="currentNotice.author" class="dialog-author">发布人：{{ currentNotice.author }}</span>
          <span class="dialog-time">发布时间：{{ currentNotice.releaseTime }}</span>
        </div>
        <div class="dialog-content">{{ currentNotice.content || currentNotice.noticeContent }}</div>
      </div>
      <template #footer>
        <el-button @click="noticeDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
<script>
import request from "@/utils/request";
import { ElMessage } from "element-plus";
export default {
  name: "Home",
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
.notice-section {
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  margin-top: 40px;
  background: linear-gradient(135deg, #f8fafc 0%, #e9f0fb 100%);
  border-radius: 18px;
  box-shadow: 0 2px 24px 0 rgba(80,120,200,0.10);
  padding: 32px 24px 32px 24px;
}
.notice-header {
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 30px;
  margin-left: 10px;
  color: #2d3a4b;
  letter-spacing: 1px;
}
.notice-list-scroll {
  max-height: 700px;
  min-height: 420px;
  width: 100%;
  border: none;
  border-radius: 18px;
  background: transparent;
  padding: 8px 0 8px 0;
  box-shadow: none;
  scrollbar-width: thin;
  scrollbar-color: #b3c6e0 #f8fafc;
  animation: fadeInWide 0.9s cubic-bezier(.23,1.01,.32,1) both;
  transition: box-shadow 0.4s;
  box-sizing: border-box;
  overflow-y: auto;
  overflow-x: hidden;
}
@keyframes fadeInWide {
  0% {
    opacity: 0;
    transform: translateY(40px) scaleX(0.7);
    box-shadow: 0 0 0 rgba(80,120,200,0.00);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scaleX(1);
    box-shadow: 0 2px 24px 0 rgba(80,120,200,0.10);
  }
}
.notice-list-scroll::-webkit-scrollbar {
  width: 8px;
  background: #f8fafc;
  border-radius: 8px;
}
.notice-list-scroll::-webkit-scrollbar-thumb {
  background: #b3c6e0;
  border-radius: 8px;
}
.notice-item {
  position: relative;
  padding: 28px 38px 20px 38px;
  margin: 0 18px 28px 18px;
  border-radius: 18px;
  background: linear-gradient(135deg, #fff 80%, #e9f0fb 100%);
  box-shadow: 0 4px 18px rgba(80,120,200,0.13);
  border: 1.5px solid #e3eaf5;
  cursor: pointer;
  transition: box-shadow 0.35s, border 0.25s, background 0.25s, transform 0.25s, filter 0.25s;
  display: flex;
  flex-direction: column;
  gap: 8px;
  opacity: 0;
  transform: translateY(30px) scale(0.97);
  animation: noticeFadeIn 0.7s cubic-bezier(.23,1.01,.32,1) forwards;
  filter: brightness(1) saturate(1);
  width: auto;
  min-width: 0;
  max-width: unset;
  box-sizing: border-box;
  overflow: hidden;
}
.notice-item:nth-child(1) { animation-delay: 0.05s; }
.notice-item:nth-child(2) { animation-delay: 0.12s; }
.notice-item:nth-child(3) { animation-delay: 0.19s; }
.notice-item:nth-child(4) { animation-delay: 0.26s; }
.notice-item:nth-child(5) { animation-delay: 0.33s; }
.notice-item:nth-child(6) { animation-delay: 0.40s; }
.notice-item:nth-child(7) { animation-delay: 0.47s; }
.notice-item:nth-child(8) { animation-delay: 0.54s; }
.notice-item:nth-child(9) { animation-delay: 0.61s; }
.notice-item:nth-child(10) { animation-delay: 0.68s; }
@keyframes noticeFadeIn {
  0% {
    opacity: 0;
    transform: translateY(30px) scale(0.97);
    filter: blur(2px) brightness(0.95);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
    filter: blur(0) brightness(1);
  }
}
.notice-item:last-child {
  margin-bottom: 0;
}
.notice-item:hover {
  box-shadow: 0 16px 48px rgba(80,120,200,0.22), 0 0 0 2px #4e8cff33;
  border: 2px solid #4e8cff;
  background: linear-gradient(135deg, #f4f9ff 80%, #e9f0fb 100%);
  transform: translateY(-5px) scale(1.045) rotateZ(-0.2deg);
  filter: brightness(1.06) saturate(1.12);
}
.notice-title {
  font-size: 22px;
  font-weight: 800;
  color: #1a237e;
  margin-bottom: 2px;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  letter-spacing: 0.5px;
}
.notice-meta {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-bottom: 2px;
}
.notice-author {
  color: #4e8cff;
  font-size: 15px;
  font-weight: 600;
}
.notice-time {
  color: #8fa2b7;
  font-size: 15px;
  align-self: flex-end;
  letter-spacing: 0.5px;
}
.notice-preview {
  color: #3a4a5d;
  font-size: 16px;
  margin-top: 4px;
  opacity: 0.85;
  line-height: 1.6;
  min-height: 22px;
}
.ripple-effect {
  position: absolute;
  border-radius: 50%;
  background: rgba(78,140,255,0.18);
  pointer-events: none;
  transform: scale(0);
  animation: ripple-animate 0.6s linear;
  z-index: 2;
}
@keyframes ripple-animate {
  to {
    transform: scale(2.5);
    opacity: 0;
  }
}
.notice-dialog >>> .el-dialog__body {
  padding-top: 12px;
  padding-bottom: 12px;
  background: linear-gradient(135deg, #f8fafc 0%, #e9f0fb 100%);
  border-radius: 12px;
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
  .main-card {
    padding: 16px 4px 24px 4px;
  }
  .notice-section {
    padding: 18px 2px 18px 2px;
    min-width: 0;
  }
  .notice-list-scroll {
    min-width: 0;
  }
}
</style>
