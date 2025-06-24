<template>
  <div class="checkin-container">
    <div class="header">
      <h2>学生入住管理</h2>
      <el-button type="primary" @click="showAddDialog = true; isEdit = false; resetForm();">
        <el-icon><Plus /></el-icon>
        新增入住记录
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchQuery"
        placeholder="请输入学生姓名或房间号"
        style="width: 300px"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">
            <el-icon><Search /></el-icon>
          </el-button>
        </template>
      </el-input>
    </div>

    <!-- 数据表格 -->
    <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="studentUsername" label="学号" width="100" />
      <el-table-column prop="studentName" label="姓名" width="100" />
      <el-table-column prop="actionTime" label="入住时间" width="160" />
      <el-table-column prop="dormbuildId" label="宿舍楼" width="80" />
      <el-table-column prop="dormroomId" label="房间号" width="80" />
      <el-table-column prop="bedNumber" label="床位号" width="80" />
      <el-table-column prop="remarks" label="备注" width="120" />
      <el-table-column prop="operator" label="操作人" width="100" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="scope">
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑入住记录' : '新增入住记录'"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="学号" prop="studentUsername">
          <el-input v-model="form.studentUsername" @blur="fetchStudentName" placeholder="请输入学生学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="studentName">
          <el-input v-model="form.studentName" placeholder="自动带出" readonly />
        </el-form-item>
        <el-form-item label="入住时间" prop="actionTime">
          <el-date-picker v-model="form.actionTime" type="datetime" placeholder="选择时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="宿舍楼" prop="dormbuildId">
          <el-input v-model="form.dormbuildId" placeholder="请输入宿舍楼号" />
        </el-form-item>
        <el-form-item label="房间号" prop="dormroomId">
          <el-input v-model="form.dormroomId" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="床位号" prop="bedNumber">
          <el-input v-model="form.bedNumber" placeholder="请输入床位号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="showVisualDialog = true">可视化选择寝室</el-button>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="form.remarks" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="操作人" prop="operator">
          <el-input v-model="form.operator" readonly placeholder="自动带出" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 房间详情弹窗 -->
    <el-dialog v-model="roomDialogVisible" title="房间入住详情" width="600px" @close="roomDetailList = []">
      <el-table :data="roomDetailList" border>
        <el-table-column label="学号" prop="studentUsername" />
        <el-table-column label="姓名" prop="studentName" />
        <el-table-column label="床位号" prop="bedNumber" />
        <el-table-column label="入住时间" prop="checkinDate" />
        <el-table-column label="状态" prop="status" />
      </el-table>
    </el-dialog>

    <!-- 可视化选择寝室弹窗 -->
    <el-dialog v-model="showVisualDialog" title="可视化选择寝室" width="900px" @close="resetVisualDialog">
      <div style="display: flex; gap: 24px;">
        <!-- 围合选择 -->
        <div style="width: 120px;">
          <el-radio-group v-model="selectedCompoundId" @change="handleCompoundChange">
            <el-radio-button v-for="c in compoundList" :key="c.compoundId" :label="c.compoundId">{{ c.compoundName }}</el-radio-button>
          </el-radio-group>
        </div>
        <!-- 楼栋选择 -->
        <div style="width: 120px;">
          <el-radio-group v-model="selectedBuildId" @change="handleBuildChange">
            <el-radio-button v-for="b in buildList" :key="b.buildId" :label="b.buildId">{{ b.buildName }}</el-radio-button>
          </el-radio-group>
        </div>
        <!-- 楼层选择 -->
        <div style="width: 120px;">
          <el-radio-group v-model="selectedFloorNum" @change="handleFloorChange">
            <el-radio-button v-for="f in floorList" :key="f.floorNum" :label="f.floorNum">{{ f.floorNum }}层</el-radio-button>
          </el-radio-group>
        </div>
        <!-- 房间卡片分布 -->
        <div style="flex: 1; display: flex; flex-wrap: wrap; gap: 16px;">
          <el-card v-for="room in roomList" :key="room.roomId" style="width: 180px; cursor: pointer;">
            <div><b>房间号：</b>{{ room.roomName }}</div>
            <div>床位：{{ room.currentCapacity }}/{{ room.maxCapacity }}</div>
            <div style="margin-top: 8px;">
              <el-button
                v-for="bed in room.beds"
                :key="bed.bedNumber"
                :type="bed.student ? 'info' : 'primary'"
                :disabled="!!bed.student"
                @click="chooseBed(room, bed)"
                size="small"
                style="margin: 2px;"
              >
                {{ bed.bedNumber }}号{{ bed.student ? '（已住）' : '（空）' }}
              </el-button>
            </div>
          </el-card>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

export default {
  name: 'CheckinInfo',
  components: { Plus, Search },
  setup() {
    const loading = ref(true)
    const tableData = ref([])
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const searchQuery = ref('')
    const showAddDialog = ref(false)
    const isEdit = ref(false)
    const formRef = ref(null)
    const user = ref(JSON.parse(sessionStorage.getItem('user') || '{}'))
    const roomDialogVisible = ref(false)
    const roomDetailList = ref([])
    const showVisualDialog = ref(false)
    const buildList = ref([])
    const floorList = ref([])
    const roomList = ref([])
    const selectedBuildId = ref(null)
    const selectedFloorNum = ref(null)
    const selectedCompoundId = ref(null)
    const compoundList = ref([])

    const form = reactive({
      id: null,
      studentUsername: '',
      studentName: '',
      dormbuildId: '',
      dormroomId: '',
      bedNumber: 1,
      actionTime: '',
      remarks: '',
      operator: user.value.name
    })

    const rules = {
      studentUsername: [{ required: true, message: '请输入学生学号', trigger: 'blur' }],
      studentName: [{ required: true, message: '请输入学生姓名', trigger: 'blur' }],
      dormbuildId: [{ required: true, message: '请输入宿舍楼号', trigger: 'blur' }],
      dormroomId: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
      bedNumber: [{ required: true, message: '请输入床位号', trigger: 'blur' }]
    }

    const fetchData = async () => {
      loading.value = true
      try {
        const res = await request.get('/checkin/list', {
          params: {
            pageNum: currentPage.value,
            pageSize: pageSize.value,
            search: searchQuery.value
          }
        })
        if (res.code === '0') {
          tableData.value = res.data.records
          total.value = res.data.total
        } else {
          ElMessage.error(res.msg || '获取数据失败')
        }
      } catch (error) {
        ElMessage.error('请求失败')
      } finally {
        loading.value = false
      }
    }

    const handleSearch = () => {
      currentPage.value = 1
      fetchData()
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      fetchData()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      fetchData()
    }

    const resetForm = () => {
      if (formRef.value) {
        formRef.value.resetFields()
      }
      Object.assign(form, {
        id: null,
        studentUsername: '',
        studentName: '',
        dormbuildId: '',
        dormroomId: '',
        bedNumber: 1,
        actionTime: '',
        remarks: '',
        operator: user.value.name
      })
    }

    const handleEdit = (row) => {
      isEdit.value = true
      Object.assign(form, row)
      showAddDialog.value = true
    }

    const handleDelete = (row) => {
      ElMessageBox.confirm('确定要删除这条入住记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const res = await request.delete(`/checkin/delete/${row.id}`)
        if (res.code === '0') {
          ElMessage.success('删除成功')
          fetchData()
        } else {
          ElMessage.error(res.msg || '删除失败')
        }
      }).catch(() => {})
    }

    const handleCheckin = (row) => {
      ElMessageBox.confirm('确定要为该学生办理入住吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const res = await request.put('/checkin/checkin', {
          id: row.id,
          operator: user.value.name
        })
        if (res.code === '0') {
          ElMessage.success('办理入住成功')
          fetchData()
        } else {
          ElMessage.error(res.msg || '办理入住失败')
        }
      }).catch(() => {})
    }

    const fetchStudentName = async () => {
      if (!form.studentUsername) {
        form.studentName = '';
        return;
      }
      const res = await request.get(`/stu/exist/${form.studentUsername}`);
      if (res.code === '0' && res.data) {
        form.studentName = res.data.name;
      } else {
        form.studentName = '';
        ElMessage.error('学号不存在，请检查输入');
      }
    };

    const handleSubmit = async () => {
      form.actionType = '入住';
      console.log('提交前form内容', JSON.stringify(form));
      if (!form.studentUsername || !form.studentName) {
        ElMessage.error('学号和姓名不能为空，且学号必须存在');
        return;
      }
      // 新增：校验是否已有宿舍
      const judgeRes = await request.get(`/room/judgeHadBed/${form.studentUsername}`);
      if (judgeRes.code === '-1') {
        ElMessage.error('该学生已有宿舍，不能重复入住');
        return;
      }
      if (!formRef.value) return
      await formRef.value.validate(async (valid) => {
        if (valid) {
          const url = isEdit.value ? '/checkin/update' : '/checkin/add'
          const method = isEdit.value ? 'put' : 'post'
          const res = await request[method](url, form)
          if (res.code === '0') {
            ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
            showAddDialog.value = false
            fetchData()
          } else {
            ElMessage.error(res.msg || '操作失败')
          }
        }
      })
    }

    const getStatusType = (status) => {
      switch (status) {
        case '待入住': return 'warning'
        case '已入住': return 'success'
        default: return 'info'
      }
    }

    const showRoomDetail = async (roomId) => {
      roomDialogVisible.value = true
      roomDetailList.value = []
      const res = await request.get(`/checkin/room/${roomId}`)
      if (res.code === '0') {
        roomDetailList.value = res.data
      }
    }

    const loadDistribution = async () => {
      const res = await request.get('/room/compound-distribution')
      console.log('compoundList', res.data);
      if (res.code === '0') {
        compoundList.value = res.data
        if (compoundList.value.length > 0) {
          selectedCompoundId.value = compoundList.value[0].compoundId
          handleCompoundChange(selectedCompoundId.value)
        }
      }
    }

    const handleCompoundChange = (compoundId) => {
      const compound = compoundList.value.find(c => c.compoundId === compoundId)
      if (compound && compound.builds.length > 0) {
        buildList.value = compound.builds
        selectedBuildId.value = buildList.value[0].buildId
        handleBuildChange(selectedBuildId.value)
      } else {
        buildList.value = []
        floorList.value = []
        roomList.value = []
      }
    }

    const handleBuildChange = (buildId) => {
      const build = buildList.value.find(b => b.buildId === buildId)
      if (build && build.floors.length > 0) {
        floorList.value = build.floors
        selectedFloorNum.value = build.floors[0].floorNum
        handleFloorChange(selectedFloorNum.value)
      } else {
        floorList.value = []
        roomList.value = []
      }
    }

    const handleFloorChange = (floorNum) => {
      const build = buildList.value.find(b => b.buildId === selectedBuildId.value)
      if (build) {
        const floor = build.floors.find(f => f.floorNum === floorNum)
        roomList.value = floor ? floor.rooms : []
      } else {
        roomList.value = []
      }
    }

    const chooseBed = (room, bed) => {
      form.dormbuildId = selectedBuildId.value
      form.dormroomId = room.roomId
      form.bedNumber = bed.bedNumber
      showVisualDialog.value = false
    }

    const resetVisualDialog = () => {
      // 清空选择状态
      // 可根据需要扩展
    }

    onMounted(() => {
      fetchData()
      loadDistribution()
    })

    return {
      loading,
      tableData,
      currentPage,
      pageSize,
      total,
      searchQuery,
      showAddDialog,
      isEdit,
      form,
      formRef,
      rules,
      handleSearch,
      handleSizeChange,
      handleCurrentChange,
      handleEdit,
      handleDelete,
      handleCheckin,
      handleSubmit,
      resetForm,
      getStatusType,
      roomDialogVisible,
      roomDetailList,
      showRoomDetail,
      showVisualDialog,
      buildList,
      floorList,
      roomList,
      selectedBuildId,
      selectedFloorNum,
      selectedCompoundId,
      compoundList,
      loadDistribution,
      handleCompoundChange,
      handleBuildChange,
      handleFloorChange,
      chooseBed,
      resetVisualDialog,
      fetchStudentName
    }
  },
  watch: {
    showVisualDialog(val) {
      if (val) {
        this.loadDistribution()
      }
    }
  }
}
</script>

<style scoped>
.checkin-container { padding: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header h2 { margin: 0; color: #303133; }
.search-bar { margin-bottom: 20px; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; }
</style> 