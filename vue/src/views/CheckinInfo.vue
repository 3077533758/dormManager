<template>
  <div class="checkin-container">
    <div class="header">
      <h2>学生入住管理</h2>
      <el-button type="primary" @click="handleAddClick">
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
      <el-table-column label="房间号" width="80">
        <template #default="scope">
          {{ scope.row.dormroomId ? scope.row.dormroomId.toString().slice(-3) : '' }}
        </template>
      </el-table-column>
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
        <!-- 学生已有寝室警告提示 -->
        <el-alert
          v-if="studentHasRoom"
          title="⚠️ 该学生已有宿舍"
          type="warning"
          :closable="false"
          show-icon
          style="margin-bottom: 16px;"
        >
          <template #default>
            <p>该学生已经分配了宿舍，不能重复入住。如需调整，请先办理退宿手续。</p>
          </template>
        </el-alert>
        <el-form-item label="姓名" prop="studentName">
          <el-input v-model="form.studentName" placeholder="自动带出" readonly />
        </el-form-item>
        <el-form-item label="入住时间" prop="actionTime">
          <el-date-picker v-model="form.actionTime" type="datetime" placeholder="选择时间" style="width: 100%" />
        </el-form-item>
        <el-form-item v-if="identity !== 'dormManager'" label="校区">
          <el-select v-model="form.selectedCampus" style="width: 100%" @change="handleCampusChange">
            <el-option v-for="campus in campusList" :key="campus" :label="campus" :value="campus" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="identity !== 'dormManager'" label="园区">
          <el-select v-model="form.selectedCompoundId" style="width: 100%" @change="handleFormCompoundChange">
            <el-option v-for="compound in formCompoundList" :key="compound.compoundId" :label="compound.compoundName" :value="compound.compoundId" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="identity !== 'dormManager'" label="楼栋">
          <el-select v-model="form.dormbuildId" style="width: 100%" @change="handleFormBuildChange">
            <el-option v-for="build in formBuildList" :key="build.dormBuildId" :label="build.dormBuildName" :value="build.dormBuildId" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" prop="dormroomId">
          <el-select v-model="form.dormroomId" placeholder="请选择房间" style="width: 100%" @change="handleFormRoomChange" :disabled="!form.dormbuildId">
            <el-option
              v-for="room in formRoomList"
              :key="room.dormRoomId"
              :label="`${room.dormRoomId.toString().slice(-3)} (${room.currentCapacity}/${room.maxCapacity}人)`"
              :value="room.dormRoomId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="床位号" prop="bedNumber">
          <el-select v-model="form.bedNumber" placeholder="请选择床位" style="width: 100%" :disabled="!form.dormroomId">
            <el-option
              v-for="bed in formBedList"
              :key="bed.bedNumber"
              :label="`${bed.bedNumber}号床位`"
              :value="bed.bedNumber"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="identity !== 'dormManager'">
          <el-button type="primary" @click="showVisualDialog = true">可视化选择寝室</el-button>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="form.remarks" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="操作人" prop="operator">
          <el-input v-model="form.operator" readonly placeholder="系统自动获取当前登录用户" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleSubmit"
            :disabled="studentHasRoom"
            :title="studentHasRoom ? '该学生已有宿舍，不能重复入住' : ''"
          >
            {{ studentHasRoom ? '学生已有宿舍' : '确定' }}
          </el-button>
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
    <el-dialog v-model="showVisualDialog" title="可视化选择寝室" width="1000px" @close="resetVisualDialog">
      <div style="display: flex; gap: 16px; flex-wrap: wrap;">
        <!-- 校区选择 -->
        <div style="width: 120px;">
          <div style="margin-bottom: 8px; font-weight: bold;">校区</div>
          <el-radio-group v-model="selectedCampusId" @change="handleVisualCampusChange">
            <el-radio-button v-for="campus in campusList" :key="campus" :label="campus">{{ campus }}</el-radio-button>
          </el-radio-group>
        </div>
        <!-- 园区选择 -->
        <div style="width: 120px;">
          <div style="margin-bottom: 8px; font-weight: bold;">园区</div>
          <el-radio-group v-model="selectedCompoundId" @change="handleCompoundChange">
            <el-radio-button v-for="c in compoundList" :key="c.compoundId" :label="c.compoundId">{{ c.compoundName }}</el-radio-button>
          </el-radio-group>
        </div>
        <!-- 楼栋选择 -->
        <div style="width: 120px;">
          <div style="margin-bottom: 8px; font-weight: bold;">楼栋</div>
          <el-radio-group v-model="selectedBuildId" @change="handleBuildChange">
            <el-radio-button v-for="b in buildList" :key="b.buildId" :label="b.buildId">{{ b.buildName }}</el-radio-button>
          </el-radio-group>
        </div>
        <!-- 楼层选择 -->
        <div style="width: 120px;">
          <div style="margin-bottom: 8px; font-weight: bold;">楼层</div>
          <el-radio-group v-model="selectedFloorNum" @change="handleFloorChange">
            <el-radio-button v-for="f in floorList" :key="f.floorNum" :label="f.floorNum">{{ f.floorNum }}层</el-radio-button>
          </el-radio-group>
        </div>
        <!-- 房间卡片分布 -->
        <div style="flex: 1; display: flex; flex-wrap: wrap; gap: 16px;">
          <el-card v-for="room in roomList" :key="room.roomId" style="width: 180px; cursor: pointer;">
            <div><b>房间号：</b>{{ room.roomId.toString().slice(-3) }}</div>
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
    const campusList = ref([])
    const formCompoundList = ref([])
    const formBuildList = ref([])
    const formRoomList = ref([])
    const formBedList = ref([])
    const selectedCampusId = ref(null)
    const studentHasRoom = ref(false)
    const identity = ref(JSON.parse(sessionStorage.getItem('identity') || '""'))
    const buildInfo = ref({ dormBuildName: '', compoundName: '', campusName: '' })

    const form = reactive({
      id: null,
      studentUsername: '',
      studentName: '',
      dormbuildId: '',
      dormroomId: '',
      bedNumber: '',
      actionTime: '',
      remarks: '',
      operator: '',
      selectedCampus: '',
      selectedCompoundId: ''
    })

    const rules = {
      studentUsername: [{ required: true, message: '请输入学生学号', trigger: 'blur' }],
      studentName: [{ required: true, message: '请输入学生姓名', trigger: 'blur' }],
      selectedCampus: [{ required: true, message: '请选择校区', trigger: 'change' }],
      selectedCompoundId: [{ required: true, message: '请选择园区', trigger: 'change' }],
      dormbuildId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
      dormroomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
      bedNumber: [{ required: true, message: '请选择床位', trigger: 'change' }]
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
        dormbuildId: identity.value === 'dormManager' ? user.value.dormbuildId : '',
        dormroomId: '',
        bedNumber: '',
        actionTime: '',
        remarks: '',
        operator: '',
        selectedCampus: identity.value === 'dormManager' ? (user.value.campus || '') : '',
        selectedCompoundId: identity.value === 'dormManager' ? (user.value.compoundId || '') : ''
      })
      studentHasRoom.value = false;
      if (identity.value === 'dormManager') {
        handleFormBuildChange();
      }
    }

    const handleEdit = async (row) => {
      isEdit.value = true
      Object.assign(form, row)
      showAddDialog.value = true
      
      // 编辑模式下也要检查学生是否已有寝室
      if (row.studentUsername) {
        try {
          const judgeRes = await request.get(`/room/judgeHadBed/${row.studentUsername}`);
          if (judgeRes.code === '-1') {
            studentHasRoom.value = true;
            ElMessage({
              message: `该学生已有宿舍！当前宿舍信息：${judgeRes.msg || '未知'}`,
              type: 'warning',
              duration: 5000,
              showClose: true
            });
          } else {
            studentHasRoom.value = false;
          }
        } catch (error) {
          console.error('检查学生宿舍状态失败:', error);
          studentHasRoom.value = false;
        }
      }
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
        studentHasRoom.value = false;
        return;
      }
      
      try {
        // 1. 检查学号是否存在并获取学生姓名
        const res = await request.get(`/stu/exist/${form.studentUsername}`);
        if (res.code === '0' && res.data) {
          form.studentName = res.data.name;
          
          // 2. 检查学生是否已有寝室
          const judgeRes = await request.get(`/room/judgeHadBed/${form.studentUsername}`);
          if (judgeRes.code === '-1') {
            // 学生已有寝室，显示警告信息
            studentHasRoom.value = true;
            ElMessage({
              message: `该学生已有宿舍，不能重复入住！当前宿舍信息：${judgeRes.msg || '未知'}`,
              type: 'warning',
              duration: 5000,
              showClose: true
            });
          } else {
            studentHasRoom.value = false;
          }
        } else {
          form.studentName = '';
          studentHasRoom.value = false;
          ElMessage.error('学号不存在，请检查输入');
        }
      } catch (error) {
        console.error('获取学生信息失败:', error);
        ElMessage.error('获取学生信息失败');
        studentHasRoom.value = false;
      }
    };

    const handleSubmit = async () => {
      if (!form.studentUsername || !form.studentName) {
        ElMessage.error('学号和姓名不能为空，且学号必须存在');
        return;
      }
      
      // 检查学生是否已有寝室
      if (studentHasRoom.value) {
        ElMessage.error('该学生已有宿舍，不能重复入住！');
        return;
      }
      
      // 检查床位是否被占用
      if (form.bedNumber && form.dormroomId) {
        const selectedBed = formBedList.value.find(bed => bed.bedNumber === form.bedNumber);
        if (selectedBed && selectedBed.occupied) {
          ElMessage.error('选择的床位已被占用，请重新选择');
          return;
        }
      }
      
      // 新增：校验是否已有宿舍（双重检查）
      const judgeRes = await request.get(`/room/judgeHadBed/${form.studentUsername}`);
      if (judgeRes.code === '-1') {
        ElMessage.error('该学生已有宿舍，不能重复入住');
        return;
      }
      if (!formRef.value) return
      await formRef.value.validate(async (valid) => {
        if (valid) {
          // 直接使用dormroomId，不需要转换
          const submitData = { ...form }
          
          const url = isEdit.value ? '/checkin/update' : '/checkin/add'
          const method = isEdit.value ? 'put' : 'post'
          const res = await request[method](url, submitData)
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
        // 不自动选择第一个园区，需要先选择校区
      }
    }

    const handleCompoundChange = async (compoundId) => {
      // 清空后续选择
      selectedBuildId.value = null
      selectedFloorNum.value = null
      buildList.value = []
      floorList.value = []
      roomList.value = []
      
      const compound = compoundList.value.find(c => c.compoundId === compoundId)
      if (compound && compound.builds && compound.builds.length > 0) {
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
      if (build && build.floors && build.floors.length > 0) {
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
      if (build && build.floors) {
        const floor = build.floors.find(f => f.floorNum === floorNum)
        roomList.value = floor ? floor.rooms : []
      } else {
        roomList.value = []
      }
    }

    const chooseBed = async (room, bed) => {
      // 1. 同步选中项
      form.selectedCampus = selectedCampusId.value
      form.selectedCompoundId = selectedCompoundId.value
      form.dormbuildId = selectedBuildId.value
      form.dormroomId = room.roomId
      form.bedNumber = bed.bedNumber.toString()

      // 2. 同步下拉选项 - 需要重新获取数据以确保一致性
      // 获取园区列表
      try {
        const compoundRes = await request.get(`/compound/getByCampus/${form.selectedCampus}`)
        if (compoundRes.code === '0') {
          formCompoundList.value = compoundRes.data
        }
      } catch (error) {
        console.error('获取园区列表失败:', error)
      }

      // 获取楼栋列表
      try {
        const buildRes = await request.get('/building/getByCompound', {
          params: { compoundId: form.selectedCompoundId }
        })
        if (buildRes.code === '0') {
          formBuildList.value = buildRes.data
        }
      } catch (error) {
        console.error('获取楼栋列表失败:', error)
      }

      // 获取房间列表
      try {
        const roomRes = await request.get('/room/getByBuild', {
          params: { buildId: Number(form.dormbuildId) }
        })
        if (roomRes.code === '0') {
          formRoomList.value = roomRes.data || []
        }
      } catch (error) {
        console.error('获取房间列表失败:', error)
      }

      // 获取床位列表
      try {
        const bedRes = await request.get('/room/getByRoom', {
          params: { roomId: Number(form.dormroomId) }
        })
        if (bedRes.code === '0') {
          formBedList.value = (bedRes.data || [])
            .filter(bed => bed && !bed.occupied)
            .map(bed => ({
              ...bed,
              bedNumber: bed.bedNumber.toString(),
              occupied: !!bed.occupied
            }))
        }
      } catch (error) {
        console.error('获取床位列表失败:', error)
      }

      // 3. 关闭弹窗
      showVisualDialog.value = false
    }

    const resetVisualDialog = () => {
      // 保留校区选择，只清空后续选择状态
      selectedCompoundId.value = null
      selectedBuildId.value = null
      selectedFloorNum.value = null
      compoundList.value = []
      buildList.value = []
      floorList.value = []
      roomList.value = []
    }

    // 可视化选择寝室弹窗相关限制
    const initVisualDialog = async () => {
      if (identity.value === 'dormManager') {
        // 只加载本宿管楼栋相关数据
        selectedCampusId.value = user.value.campus || ''
        selectedCompoundId.value = user.value.compoundId || ''
        selectedBuildId.value = user.value.dormbuildId || ''
        // 可选：可直接调用handleBuildChange(selectedBuildId.value)加载楼层和房间
        handleBuildChange(selectedBuildId.value)
      } else {
        // 管理员正常加载全部
        await loadCampusList()
      }
    }

    const handleCampusChange = async () => {
      form.selectedCompoundId = ''
      form.dormbuildId = ''
      form.dormroomId = ''
      formCompoundList.value = []
      formBuildList.value = []
      formRoomList.value = []
      formBedList.value = []
      
      if (!form.selectedCampus) return
      
      try {
        const res = await request.get(`/compound/getByCampus/${form.selectedCampus}`)
        if (res.code === '0') {
          formCompoundList.value = res.data
        } else {
          ElMessage.error(res.msg || '获取园区列表失败')
        }
      } catch (error) {
        ElMessage.error('获取园区列表失败')
      }
    }

    const handleFormCompoundChange = async () => {
      form.dormbuildId = ''
      form.dormroomId = ''
      formBuildList.value = []
      formRoomList.value = []
      formBedList.value = []
      
      if (!form.selectedCompoundId) return
      
      console.log('选择的园区ID:', form.selectedCompoundId)
      
      try {
        const res = await request.get('/building/getByCompound', {
          params: { compoundId: form.selectedCompoundId }
        })
        console.log('获取楼栋响应:', res)
        if (res.code === '0') {
          formBuildList.value = res.data
          console.log('楼栋列表:', formBuildList.value)
        } else {
          ElMessage.error(res.msg || '获取楼栋列表失败')
        }
      } catch (error) {
        console.error('获取楼栋失败:', error)
        ElMessage.error('获取楼栋列表失败')
      }
    }

    const handleFormBuildChange = async () => {
      form.dormroomId = ''
      formRoomList.value = []
      formBedList.value = []
      
      if (!form.dormbuildId) return
      
      console.log('选择的楼栋ID:', form.dormbuildId, '类型:', typeof form.dormbuildId)
      
      try {
        const res = await request.get('/room/getByBuild', {
          params: { buildId: Number(form.dormbuildId) }
        })
        console.log('获取房间响应:', res)
        if (res.code === '0') {
          formRoomList.value = res.data || []
          console.log('房间列表:', formRoomList.value)
        } else {
          ElMessage.error(res.msg || '获取房间列表失败')
        }
      } catch (error) {
        console.error('获取房间失败:', error)
        ElMessage.error('获取房间列表失败')
      }
    }

    const handleFormRoomChange = async () => {
      form.bedNumber = ''
      formBedList.value = []
      
      if (!form.dormroomId) return
      
      console.log('选择的房间ID:', form.dormroomId, '类型:', typeof form.dormroomId)
      
      try {
        const roomId = Number(form.dormroomId)
        console.log('发送请求到 /room/getByRoom，参数:', { roomId })
        
        const res = await request.get('/room/getByRoom', {
          params: { roomId }
        })
        console.log('获取床位响应:', res)
        if (res.code === '0') {
          // 只保留空床位
          formBedList.value = (res.data || [])
            .filter(bed => bed && !bed.occupied)
            .map(bed => ({
              ...bed,
              bedNumber: bed.bedNumber.toString(),
              occupied: !!bed.occupied
            }))
          console.log('最终可用床位列表:', formBedList.value)
        } else {
          ElMessage.error(res.msg || '获取床位列表失败')
        }
      } catch (error) {
        console.error('获取床位失败:', error)
        ElMessage.error('获取床位列表失败')
      }
    }

    const loadCampusList = async () => {
      try {
        const res = await request.get('/compound/getAllCampus')
        if (res.code === '0') {
          campusList.value = res.data
          // 自动选中第一个校区
          if (campusList.value.length > 0) {
            selectedCampusId.value = campusList.value[0]
            // 触发校区变化，加载对应的园区数据
            await handleVisualCampusChange(selectedCampusId.value)
          }
        } else {
          ElMessage.error(res.msg || '获取校区列表失败')
        }
      } catch (error) {
        ElMessage.error('获取校区列表失败')
      }
    }

    const handleVisualCampusChange = async (campus) => {
      // 清空后续选择
      selectedCompoundId.value = null
      selectedBuildId.value = null
      selectedFloorNum.value = null
      compoundList.value = []
      buildList.value = []
      floorList.value = []
      roomList.value = []
      
      if (!campus) return
      
      // 使用compound-distribution接口的数据，按校区过滤
      const res = await request.get('/room/compound-distribution')
      if (res.code === '0') {
        // 根据校区名称过滤园区
        // 东校区：园区1、2，西校区：园区3、4
        const campusCompounds = res.data.filter(compound => {
          if (campus === '东校区') {
            return compound.compoundId === 1 || compound.compoundId === 2
          } else if (campus === '西校区') {
            return compound.compoundId === 3 || compound.compoundId === 4
          }
          return false
        })
        
        compoundList.value = campusCompounds
        if (compoundList.value.length > 0) {
          selectedCompoundId.value = compoundList.value[0].compoundId
          await handleCompoundChange(selectedCompoundId.value)
        }
      } else {
        ElMessage.error(res.msg || '获取园区列表失败')
      }
    }

    const fetchBuildInfo = async () => {
      if (identity.value === 'dormManager' && user.value.dormbuildId) {
        const res = await request.get('/building/getAllWithCompound')
        if (res.code === '0' && Array.isArray(res.data)) {
          const found = res.data.find(b => b.dormBuildId === user.value.dormbuildId)
          if (found) {
            buildInfo.value = {
              dormBuildName: found.dormBuildName,
              compoundName: found.compoundName,
              campusName: found.campus
            }
          }
        }
      }
    }

    const handleAddClick = async () => {
      if (identity.value === 'dormManager') {
        await fetchBuildInfo()
      }
      showAddDialog.value = true
      isEdit.value = false
      resetForm()
    }

    onMounted(() => {
      fetchData()
      loadDistribution()
      loadCampusList()
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
      fetchStudentName,
      campusList,
      formCompoundList,
      formBuildList,
      formRoomList,
      formBedList,
      handleCampusChange,
      handleFormCompoundChange,
      handleFormBuildChange,
      handleFormRoomChange,
      loadCampusList,
      selectedCampusId,
      handleVisualCampusChange,
      initVisualDialog,
      studentHasRoom,
      identity,
      buildInfo,
      handleAddClick
    }
  },
  watch: {
    showVisualDialog(val) {
      if (val) {
        this.initVisualDialog()
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