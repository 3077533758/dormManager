<template>
  <div class="stats-section">
    <div class="stats-flex">
      <div class="stat-card gradient-blue" @click="goTo('MyRoomInfo')">
        <div class="stat-label">我的宿舍</div>
        <div class="stat-value">{{ dormInfo.displayName }}</div>
        <div class="stat-desc">{{ dormInfo.type }}</div>
      </div>
      <div class="stat-card gradient-green" @click="goTo('ApplyRepairInfo')">
        <div class="stat-label">报修申请</div>
        <div class="stat-value">{{ repairStats.total }}条</div>
        <div class="stat-desc">
          <template v-if="repairStats.pending > 0">
            <span class="pending">{{ repairStats.pending }}条待处理</span>
          </template>
          <template v-else>
            已完成
          </template>
        </div>
      </div>
      <div class="stat-card gradient-orange" @click="goTo('ApplyOutLive')">
        <div class="stat-label">外宿申请</div>
        <div class="stat-value">{{ outLiveStats.total }}条</div>
        <div class="stat-desc orange">
          <template v-if="outLiveStats.pending > 0">
            <span class="pending">{{ outLiveStats.pending }}条待处理</span>
          </template>
          <template v-else>
            {{ outLiveStats.status }}
          </template>
        </div>
      </div>
      <div class="stat-card gradient-purple" @click="goTo('ApplyChangeRoom')">
        <div class="stat-label">调宿申请</div>
        <div class="stat-value">{{ adjustStats.total }}条</div>
        <div class="stat-desc green">
          <template v-if="adjustStats.pending > 0">
            <span class="pending">{{ adjustStats.pending }}条待处理</span>
          </template>
          <template v-else>
            {{ adjustStats.status }}
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
export default {
  name: "StudentStats",
  data() {
    return {
      dormInfo: {
        displayName: "-",
        type: "-"
      },
      repairStats: {
        total: 0,
        pending: 0
      },
      outLiveStats: {
        total: 0,
        status: "-",
        pending: 0
      },
      adjustStats: {
        total: 0,
        status: "-",
        pending: 0
      },
      refreshTimer: null
    };
  },
  mounted() {
    this.fetchAllStats();
    this.refreshTimer = setInterval(this.fetchAllStats, 3000);
  },
  beforeUnmount() {
    if (this.refreshTimer) clearInterval(this.refreshTimer);
  },
  methods: {
    fetchAllStats() {
      this.fetchDormInfo();
      this.fetchRepairStats();
      this.fetchOutLiveStats();
      this.fetchAdjustStats();
    },
    fetchDormInfo() {
      const user = JSON.parse(sessionStorage.getItem("user"));
      if (!user || !user.username) return;
      request.get("/room/getMyRoom/" + user.username).then(res => {
        if (res.code === "0" && res.data) {
          const room = res.data;
          this.dormInfo.displayName = `${room.dormBuildName || ''}${room.dormRoomId ? room.dormRoomId.toString().slice(-3) : ''}`;
          this.dormInfo.type = room.maxCapacity ? `${room.maxCapacity}人间` : "-";
        }
      });
    },
    fetchRepairStats() {
      const user = JSON.parse(sessionStorage.getItem("user"));
      if (!user || !user.username) return;
      request.get("/repair/find/" + user.username, { params: { pageNum: 1, pageSize: 100 } }).then(res => {
        if (res.code === "0" && res.data && res.data.records) {
          this.repairStats.total = res.data.total || res.data.records.length;
          this.repairStats.pending = res.data.records.filter(r => r.state !== '完成' && r.state !== '已完成').length;
        }
      });
    },
    fetchOutLiveStats() {
      const user = JSON.parse(sessionStorage.getItem("user"));
      if (!user || !user.username) return;
      request.get(`/outLive/findByUsername/${user.username}`, { params: { pageNum: 1, pageSize: 100 } }).then(res => {
        if (res.code === "0" && res.data && res.data.records) {
          this.outLiveStats.total = res.data.total || res.data.records.length;
          // 只统计真正待处理的
          this.outLiveStats.pending = res.data.records.filter(r => ['待处理', '待审核', '未处理'].includes(r.state)).length;
          // 取最新一条的状态
          if (res.data.records.length > 0) {
            this.outLiveStats.status = res.data.records[0].state || "-";
          } else {
            this.outLiveStats.status = "-";
          }
        }
      });
    },
    fetchAdjustStats() {
      const user = JSON.parse(sessionStorage.getItem("user"));
      if (!user || !user.username) return;
      request.get(`/adjustRoom/findByUsername/${user.username}`, { params: { pageNum: 1, pageSize: 100 } }).then(res => {
        if (res.code === "0" && res.data && res.data.records) {
          this.adjustStats.total = res.data.total || res.data.records.length;
          // 只统计真正待处理的
          this.adjustStats.pending = res.data.records.filter(r => ['待处理', '待审核', '未处理'].includes(r.state)).length;
          // 取最新一条的状态
          if (res.data.records.length > 0) {
            this.adjustStats.status = res.data.records[0].state || "-";
          } else {
            this.adjustStats.status = "-";
          }
        }
      });
    },
    goTo(routeName) {
      this.$router.push({ name: routeName });
    }
  }
};
</script>

<style scoped>
.stats-section {
  margin-bottom: 48px;
}
.stats-flex {
  display: flex;
  flex-wrap: nowrap;
  justify-content: space-between;
  gap: 32px;
}
.stat-card {
  border-radius: 18px;
  box-shadow: 0 4px 24px 0 rgba(80,120,200,0.10);
  padding: 36px 0 28px 0;
  text-align: center;
  background: #fff;
  transition: box-shadow 0.3s, transform 0.3s;
  cursor: pointer;
  width: 100%;
  max-width: 370px;
  min-width: 270px;
  height: 220px;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
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
  font-size: 35px;
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
.stat-desc.small {
  font-size: 14px;
  color: #3575f6;
}
.stat-desc.orange {
  color: #ff9900;
}
.stat-desc.green {
  color: #1ecb8f;
}
.pending {
  color: #1ecb8f;
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
  background: linear-gradient(135deg, #f0e6ff 0%, #e6d2ff 100%);
}
@media (max-width: 1600px) {
  .stat-card {
    max-width: 260px;
    min-width: 180px;
    height: 170px;
    padding: 28px 0 18px 0;
  }
  .stat-label {
    font-size: 15px;
  }
  .stat-value {
    font-size: 32px;
  }
  .stat-desc {
    font-size: 13px;
  }
}
</style> 