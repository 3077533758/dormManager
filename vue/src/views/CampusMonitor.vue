<template>
  <div class="campus-monitor">
    <el-breadcrumb separator-icon="ArrowRight" style="margin: 16px">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>宿舍管理</el-breadcrumb-item>
      <el-breadcrumb-item>园区监控</el-breadcrumb-item>
    </el-breadcrumb>

    <div class="monitor-container">
      <!-- 顶部统计卡片 -->
      <div class="stats-cards">
        <el-card class="stat-card total-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="40" color="white"><House /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ totalStats.totalBuildings }}</div>
              <div class="stat-label">总楼栋数</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card room-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="40" color="white"><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ totalStats.totalRooms }}</div>
              <div class="stat-label">总房间数</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card bed-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="40" color="white"><Bed /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ totalStats.totalBeds }}</div>
              <div class="stat-label">总床位数</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card occupied-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="40" color="white"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ totalStats.occupiedBeds }}</div>
              <div class="stat-label">已入住</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card available-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="40" color="white"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ totalStats.availableBeds }}</div>
              <div class="stat-label">空床位</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card rate-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="40" color="white"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ totalStats.occupancyRate }}%</div>
              <div class="stat-label">入住率</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 园区详情 -->
      <div class="campus-details">
        <el-row :gutter="20">
          <el-col :span="12" v-for="campus in campusData" :key="campus.campusName">
            <el-card class="campus-card" shadow="hover">
              <template #header>
                <div class="campus-header">
                  <span class="campus-name">{{ campus.campusName }}</span>
                  <el-tag :type="getCampusTagType(campus.occupancyRate)" size="small">
                    入住率: {{ campus.occupancyRate }}%
                  </el-tag>
                </div>
              </template>

              <!-- 园区统计 -->
              <div class="campus-stats">
                <div class="stat-item">
                  <span class="label">楼栋:</span>
                  <span class="value">{{ campus.totalBuildings }}栋</span>
                </div>
                <div class="stat-item">
                  <span class="label">房间:</span>
                  <span class="value">{{ campus.totalRooms }}间</span>
                </div>
                <div class="stat-item">
                  <span class="label">床位:</span>
                  <span class="value">{{ campus.totalBeds }}个</span>
                </div>
                <div class="stat-item">
                  <span class="label">已住:</span>
                  <span class="value occupied">{{ campus.occupiedBeds }}人</span>
                </div>
                <div class="stat-item">
                  <span class="label">空床:</span>
                  <span class="value available">{{ campus.availableBeds }}个</span>
                </div>
              </div>

              <!-- 围合列表 -->
              <div class="compounds-section">
                <h4>围合详情</h4>
                <el-collapse>
                  <el-collapse-item 
                    v-for="compound in campus.compounds" 
                    :key="compound.compoundId"
                    :title="compound.compoundName"
                    :name="compound.compoundId"
                  >
                    <div class="compound-stats">
                      <div class="stat-item">
                        <span class="label">楼栋:</span>
                        <span class="value">{{ compound.buildings }}栋</span>
                      </div>
                      <div class="stat-item">
                        <span class="label">房间:</span>
                        <span class="value">{{ compound.rooms }}间</span>
                      </div>
                      <div class="stat-item">
                        <span class="label">床位:</span>
                        <span class="value">{{ compound.beds }}个</span>
                      </div>
                      <div class="stat-item">
                        <span class="label">入住率:</span>
                        <span class="value">{{ compound.occupancyRate }}%</span>
                      </div>
                    </div>

                    <!-- 楼栋详情 -->
                    <div class="buildings-list">
                      <el-table :data="compound.buildList" size="small" border>
                        <el-table-column prop="buildName" label="楼栋" width="80" />
                        <el-table-column prop="rooms" label="房间数" width="80" />
                        <el-table-column prop="beds" label="床位数" width="80" />
                        <el-table-column prop="occupiedBeds" label="已住" width="80" />
                        <el-table-column prop="availableBeds" label="空床" width="80" />
                        <el-table-column label="入住率" width="100">
                          <template #default="scope">
                            <el-progress 
                              :percentage="scope.row.occupancyRate" 
                              :color="getProgressColor(scope.row.occupancyRate)"
                              :stroke-width="8"
                            />
                          </template>
                        </el-table-column>
                        <el-table-column label="操作" width="120">
                          <template #default="scope">
                            <el-button 
                              type="primary" 
                              size="small" 
                              @click="showRoomDetails(scope.row)"
                            >
                              查看房间
                            </el-button>
                          </template>
                        </el-table-column>
                      </el-table>
                    </div>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 刷新按钮 -->
      <div class="refresh-section">
        <el-button type="primary" @click="loadData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 房间详情弹窗 -->
    <el-dialog 
      v-model="roomDialogVisible" 
      :title="`${selectedBuilding.buildName} - 房间详情`" 
      width="80%" 
      @close="roomDetails = []"
    >
      <div v-loading="roomDetailsLoading">
        <el-table :data="roomDetails" border style="width: 100%">
          <el-table-column prop="roomId" label="房间号" width="100" />
          <el-table-column prop="floorNum" label="楼层" width="80" />
          <el-table-column prop="maxCapacity" label="最大床位" width="100" />
          <el-table-column prop="currentCapacity" label="已住人数" width="100" />
          <el-table-column label="床位详情" min-width="400">
            <template #default="scope">
              <div class="bed-details">
                <div 
                  v-for="bed in scope.row.beds" 
                  :key="bed.bedNumber"
                  class="bed-item"
                  :class="{ 'occupied': bed.student, 'empty': !bed.student }"
                  @click="showStudentInfo(bed)"
                >
                  <span class="bed-number">{{ bed.bedNumber }}号床</span>
                  <span v-if="bed.student" class="student-info">
                    {{ bed.student.username }} - {{ bed.student.name || '未知姓名' }}
                  </span>
                  <span v-else class="empty-text">空床位</span>
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 学生信息弹窗 -->
    <el-dialog 
      v-model="studentDialogVisible" 
      title="学生信息" 
      width="500px"
    >
      <div v-if="selectedStudent" class="student-info-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="学号">{{ selectedStudent.username }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ selectedStudent.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ selectedStudent.gender }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ selectedStudent.age }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ selectedStudent.phoneNum }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ selectedStudent.email }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { House, OfficeBuilding, Bed, User, CircleCheck, TrendCharts, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'
import '@/assets/css/CampusMonitor.css'

export default {
  name: 'CampusMonitor',
  components: {
    House,
    OfficeBuilding,
    Bed,
    User,
    CircleCheck,
    TrendCharts,
    Refresh
  },
  setup() {
    const loading = ref(false)
    const campusData = ref([])
    const roomDialogVisible = ref(false)
    const studentDialogVisible = ref(false)
    const roomDetailsLoading = ref(false)
    const roomDetails = ref([])
    const selectedBuilding = ref({})
    const selectedStudent = ref(null)

    const totalStats = computed(() => {
      const stats = {
        totalBuildings: 0,
        totalRooms: 0,
        totalBeds: 0,
        occupiedBeds: 0,
        availableBeds: 0,
        occupancyRate: 0
      }
      
      campusData.value.forEach(campus => {
        stats.totalBuildings += campus.totalBuildings
        stats.totalRooms += campus.totalRooms
        stats.totalBeds += campus.totalBeds
        stats.occupiedBeds += campus.occupiedBeds
        stats.availableBeds += campus.availableBeds
      })
      
      if (stats.totalBeds > 0) {
        stats.occupancyRate = Math.round((stats.occupiedBeds / stats.totalBeds) * 10000) / 100
      }
      
      return stats
    })

    const loadData = async () => {
      loading.value = true
      try {
        const res = await request.get('/room/campus-monitor')
        if (res.code === '0') {
          campusData.value = res.data
        } else {
          ElMessage.error(res.msg || '获取数据失败')
        }
      } catch (error) {
        ElMessage.error('请求失败')
      } finally {
        loading.value = false
      }
    }

    const showRoomDetails = async (building) => {
      selectedBuilding.value = building
      roomDialogVisible.value = true
      roomDetailsLoading.value = true
      
      try {
        const res = await request.get(`/room/building-rooms/${building.buildId}`)
        if (res.code === '0') {
          roomDetails.value = res.data
        } else {
          ElMessage.error(res.msg || '获取房间详情失败')
        }
      } catch (error) {
        ElMessage.error('获取房间详情失败')
      } finally {
        roomDetailsLoading.value = false
      }
    }

    const showStudentInfo = async (bed) => {
      if (!bed.student) {
        ElMessage.info('该床位为空')
        return
      }
      
      try {
        const res = await request.get(`/stu/exist/${bed.student.username}`)
        if (res.code === '0') {
          selectedStudent.value = res.data
          studentDialogVisible.value = true
        } else {
          ElMessage.error('获取学生信息失败')
        }
      } catch (error) {
        ElMessage.error('获取学生信息失败')
      }
    }

    const getCampusTagType = (rate) => {
      if (rate >= 90) return 'danger'
      if (rate >= 70) return 'warning'
      if (rate >= 50) return 'success'
      return 'info'
    }

    const getProgressColor = (rate) => {
      if (rate >= 90) return '#F56C6C'
      if (rate >= 70) return '#E6A23C'
      if (rate >= 50) return '#67C23A'
      return '#909399'
    }

    onMounted(() => {
      loadData()
    })

    return {
      loading,
      campusData,
      totalStats,
      loadData,
      getCampusTagType,
      getProgressColor,
      showRoomDetails,
      showStudentInfo,
      roomDialogVisible,
      studentDialogVisible,
      roomDetailsLoading,
      roomDetails,
      selectedBuilding,
      selectedStudent
    }
  }
}
</script>

<style scoped>
.campus-monitor {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

.monitor-container {
  max-width: 1400px;
  margin: 0 auto;
}

/* 统计卡片样式 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: none;
  border-radius: 15px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  margin-right: 15px;
  padding: 10px;
  border-radius: 10px;
  background: rgba(64, 158, 255, 0.1);
}

.total-card .stat-icon {
  background: rgba(64, 158, 255, 0.1);
}

.room-card .stat-icon {
  background: rgba(103, 194, 58, 0.1);
}

.bed-card .stat-icon {
  background: rgba(230, 162, 60, 0.1);
}

.occupied-card .stat-icon {
  background: rgba(245, 108, 108, 0.1);
}

.available-card .stat-icon {
  background: rgba(144, 147, 153, 0.1);
}

.rate-card .stat-icon {
  background: rgba(156, 39, 176, 0.1);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

/* 园区卡片样式 */
.campus-details {
  margin-bottom: 30px;
}

.campus-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: none;
  border-radius: 15px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.campus-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.campus-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.campus-name {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.campus-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
  padding: 15px;
  background: rgba(64, 158, 255, 0.05);
  border-radius: 10px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-item .label {
  font-size: 14px;
  color: #606266;
}

.stat-item .value {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.stat-item .value.occupied {
  color: #F56C6C;
}

.stat-item .value.available {
  color: #67C23A;
}

/* 围合部分样式 */
.compounds-section {
  margin-top: 20px;
}

.compounds-section h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
}

.compound-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 10px;
  margin-bottom: 15px;
  padding: 10px;
  background: rgba(103, 194, 58, 0.05);
  border-radius: 8px;
}

.compound-stats .stat-item {
  font-size: 12px;
}

.compound-stats .stat-item .label {
  font-size: 12px;
}

.compound-stats .stat-item .value {
  font-size: 14px;
}

.buildings-list {
  margin-top: 15px;
}

/* 刷新按钮样式 */
.refresh-section {
  text-align: center;
  margin-top: 30px;
}

.refresh-section .el-button {
  padding: 12px 24px;
  font-size: 16px;
  border-radius: 25px;
  background: linear-gradient(45deg, #667eea, #764ba2);
  border: none;
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.refresh-section .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .campus-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .compound-stats {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .campus-stats {
    grid-template-columns: 1fr;
  }
  
  .compound-stats {
    grid-template-columns: 1fr;
  }
}

/* 床位详情样式 */
.bed-details {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.bed-item {
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e4e7ed;
  min-width: 120px;
  text-align: center;
}

.bed-item.occupied {
  background: linear-gradient(135deg, #f56c6c, #f78989);
  color: white;
  border-color: #f56c6c;
}

.bed-item.empty {
  background: linear-gradient(135deg, #67c23a, #85ce61);
  color: white;
  border-color: #67c23a;
}

.bed-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.bed-number {
  font-weight: bold;
  display: block;
  margin-bottom: 4px;
}

.student-info {
  font-size: 12px;
  display: block;
}

.empty-text {
  font-size: 12px;
  display: block;
}

/* 学生信息详情样式 */
.student-info-detail {
  padding: 20px;
}
</style> 