<template>
  <el-menu :default-active="$route.path" router style="width: 200px; height:100%; min-height: calc(100vh - 40px)"
    unique-opened>
    <div style="display: flex;align-items: center;justify-content: center;padding: 11px 0;">
      <img alt="" :src="logo" style="width: 100px;height: 100px;border-radius: 50px;margin-left: -15%;">
    </div>
    <el-menu-item index="/home">
      <el-icon>
        <house />
      </el-icon>
      <span>首页</span>
    </el-menu-item>
    <el-sub-menu v-if="this.judgeIdentity() !== 0" index="2">
      <template #title>
        <el-icon>
          <user />
        </el-icon>
        <span>用户管理</span>
      </template>
      <el-menu-item v-if="this.judgeIdentity() !== 0" index="/stuInfo">学生信息</el-menu-item>
      <el-menu-item v-if="this.judgeIdentity() === 2" index="/dormManagerInfo">宿管信息</el-menu-item>
      <el-menu-item index="/studentImport">
        <el-icon><upload /></el-icon>
        <span>批量导入学生</span>
      </el-menu-item>
    </el-sub-menu>
    <el-sub-menu v-if="this.judgeIdentity() !== 0" index="3">
      <template #title>
        <el-icon>
          <coin />
        </el-icon>
        <span>宿舍管理</span>
      </template>
      <el-menu-item v-if="this.judgeIdentity() !== 0" index="/compoundInfo">园区信息</el-menu-item>
      <el-menu-item v-if="this.judgeIdentity() !== 0" index="/buildingInfo">楼宇信息</el-menu-item>
      <el-menu-item v-if="this.judgeIdentity() !== 0" index="/roomInfo">房间信息</el-menu-item>
      <el-menu-item v-if="this.judgeIdentity() !== 0" index="/campusMonitor">校区监控</el-menu-item>
    </el-sub-menu>
    <el-sub-menu v-if="this.judgeIdentity() !== 0" index="4">
      <template #title>
        <el-icon>
          <message />
        </el-icon>
        <span>信息管理</span>
      </template>
      <el-menu-item v-if="this.judgeIdentity() === 2" index="/noticeInfo">公告信息</el-menu-item>
      <el-menu-item v-if="this.judgeIdentity() !== 0" index="/repairInfo">报修信息</el-menu-item>
    </el-sub-menu>
    <el-sub-menu v-if="this.judgeIdentity() !== 0" index="5">
      <template #title>
        <el-icon>
          <pie-chart />
        </el-icon>
        <span>申请管理</span>
      </template>
      <el-menu-item v-if="this.judgeIdentity() !== 0" index="/adjustRoomInfo">调宿申请</el-menu-item>
      <el-menu-item v-if="this.judgeIdentity() !== 0" index="/quitRoomInfo">退宿申请</el-menu-item>
      <el-menu-item v-if="this.judgeIdentity() !== 0" index="/outLiveInfo">外宿申请</el-menu-item>
    </el-sub-menu>
    <el-sub-menu v-if="this.judgeIdentity() !== 0" index="6">
      <template #title>
        <el-icon>
          <school />
        </el-icon>
        <span>寝室管理</span>
      </template>
      <el-menu-item v-if="this.judgeIdentity() !== 0" index="/hygieneInfo">卫生管理</el-menu-item>
      <el-menu-item v-if="this.judgeIdentity() !== 0" index="/violationInfo">违纪管理</el-menu-item>
      <el-menu-item v-if="this.judgeIdentity() !== 0" index="/evaluationInfo">寝室评比</el-menu-item>
    </el-sub-menu>
    <el-sub-menu v-if="this.judgeIdentity() !== 0" index="7">
      <template #title>
        <el-icon>
          <user-filled />
        </el-icon>
        <span>住宿管理</span>
      </template>
      <el-menu-item v-if="this.judgeIdentity() !== 0" index="/checkinInfo">入住管理</el-menu-item>
      <el-menu-item v-if="this.judgeIdentity() !== 0" index="/batchCheckin">批量入住</el-menu-item>
    </el-sub-menu>
    <el-sub-menu v-if="this.judgeIdentity() === 2" index="8">
      <template #title>
        <el-icon>
          <data-analysis />
        </el-icon>
        <span>报表管理</span>
      </template>
      <el-menu-item v-if="this.judgeIdentity() === 2" index="/reportInfo">统计报表</el-menu-item>
    </el-sub-menu>
    <el-menu-item v-if="this.judgeIdentity() !== 0" index="/visitorInfo">
      <svg class="icon" data-v-042ca774="" style="height: 18px; margin-right: 11px;" viewBox="0 0 1024 1024"
        xmlns="http://www.w3.org/2000/svg">
        <path
          d="M512 160c320 0 512 352 512 352S832 864 512 864 0 512 0 512s192-352 512-352zm0 64c-225.28 0-384.128 208.064-436.8 288 52.608 79.872 211.456 288 436.8 288 225.28 0 384.128-208.064 436.8-288-52.608-79.872-211.456-288-436.8-288zm0 64a224 224 0 110 448 224 224 0 010-448zm0 64a160.192 160.192 0 00-160 160c0 88.192 71.744 160 160 160s160-71.808 160-160-71.744-160-160-160z"
          fill="currentColor"></path>
      </svg>
      <span>访客管理</span>
    </el-menu-item>
    <el-menu-item v-if="this.judgeIdentity() === 0" index="/myRoomInfo">
      <el-icon>
        <school />
      </el-icon>
      <span>我的宿舍</span>
    </el-menu-item>
    <el-menu-item v-if="this.judgeIdentity() === 0" index="/applyChangeRoom">
      <el-icon>
        <takeaway-box />
      </el-icon>
      <span>申请调宿</span>
    </el-menu-item>
    <el-menu-item v-if="this.judgeIdentity() === 0" index="/applyQuitRoom">
      <el-icon>
        <house />
      </el-icon>
      <span>申请退宿</span>
    </el-menu-item>
    <el-menu-item v-if="this.judgeIdentity() === 0" index="/applyOutLive">
      <el-icon>
        <location />
      </el-icon>
      <span>申请外宿</span>
    </el-menu-item>
    <el-menu-item v-if="this.judgeIdentity() === 0" index="/applyRepairInfo">
      <el-icon>
        <set-up />
      </el-icon>
      <span>报修申请</span>
    </el-menu-item>
    <el-menu-item index="/selfInfo">
      <el-icon>
        <setting />
      </el-icon>
      <span>个人信息</span>
    </el-menu-item>
  </el-menu>
</template>

<script>
import request from "@/utils/request";
import { ElMessage } from "element-plus";

export default {
  name: "Aside",
  data() {
    return {
      user: {},
      identity: '',
      path: this.$route.path,
      hasRoom: true // 默认有宿舍
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      request.get("/main/loadIdentity").then((res) => {
        if (res.code !== "0") {
          ElMessage({
            message: '用户会话过期',
            type: 'error',
          });
          sessionStorage.clear()
          request.get("/main/signOut");

        }
        window.sessionStorage.setItem("identity", JSON.stringify(res.data));
        this.identity = res.data
      });
      request.get("/main/loadUserInfo").then((result) => {
        if (result.code !== "0") {
          ElMessage({
            message: '用户会话过期',
            type: 'error',
          });
          request.get("/main/signOut");
          sessionStorage.clear()
          this.$router.replace({ path: "/login" });
        }
        window.sessionStorage.setItem("user", JSON.stringify(result.data));
        this.user = result.data
        
        // 如果是学生，检查宿舍状态
        if (this.identity === 'stu') {
          this.checkRoomStatus()
        }
      });
    },
    checkRoomStatus() {
      const username = this.user.username
      request.get("/main/getStudentRoomStatus/" + username).then((res) => {
        if (res.code === "0") {
          this.hasRoom = true
        } else {
          this.hasRoom = false
        }
      }).catch(() => {
        this.hasRoom = false
      })
    },
    judgeIdentity() {
      if (this.identity === 'stu') {
        return 0
      } else if (this.identity === 'dormManager') {
        return 1
      } else
        return 2
    },
  },
  computed: {
    logo() {
      if (this.identity === 'stu') {
        return require("../assets/stulogo.png")
      } else if (this.identity === 'dormManager') {
        return require("../assets/dormlogo.png")
      } else return require("../assets/adminlogo.png")
    }
  }
}
</script>

<style scoped>
.icon {
  margin-right: 6px;
}

.el-sub-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  padding: 0 45px;
  min-width: 199px;
}
</style>
