<template>
  <div class="evaluation-container">
    <div class="header">
      <h2>寝室评比管理</h2>
      <el-button type="primary" @click="showAddDialog = true; isEdit = false; resetForm();">
        <el-icon><Plus /></el-icon>
        新增评比记录
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchQuery"
        placeholder="请输入房间号或评比周期"
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
      <el-table-column label="房间号">
        <template #default="scope">
          {{ scope.row.dormroomId || '' }}
        </template>
      </el-table-column>
      <el-table-column prop="evaluationPeriod" label="评比周期" width="150" />
      <el-table-column prop="hygieneScore" label="卫生总分" width="100" />
      <el-table-column prop="gpaScore" label="绩点均分" width="100" />
      <el-table-column prop="totalScore" label="综合得分" width="100" />
      <el-table-column prop="starLevel" label="星级" width="100">
        <template #default="scope">
          <el-rate v-model="starMap[scope.row.starLevel]" disabled show-text :texts="['无', '一', '二', '三', '四', '五']" />
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
         <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="evaluator" label="评比人员" width="120" />
      <el-table-column prop="evaluationTime" label="评比时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)" :disabled="scope.row.status !== '待评选'">编辑</el-button>
          <el-button size="small" type="success" @click="handlePublish(scope.row)" :disabled="scope.row.status !== '待评选'">发布</el-button>
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
      :title="isEdit ? '编辑评比记录' : '新增评比记录'"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="房间号" prop="inputRoom">
          <el-input v-model="form.inputRoom" maxlength="3" placeholder="如101, 305" />
        </el-form-item>
        <el-form-item label="宿舍楼号" prop="dormbuildId">
          <el-input v-model="form.dormbuildId" placeholder="如1, 2, 3" />
        </el-form-item>
        <el-form-item label="评比周期" prop="evaluationPeriod">
          <el-input v-model="form.evaluationPeriod" placeholder="例如: 2024年第一学期" />
        </el-form-item>
        <el-form-item label="卫生总分" prop="hygieneScore">
          <el-input-number v-model="form.hygieneScore" :precision="2" :step="0.1" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="绩点均分" prop="gpaScore">
          <el-input-number v-model="form.gpaScore" :precision="2" :step="0.01" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="form.remarks" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

export default {
  name: 'EvaluationInfo',
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

    const form = reactive({
      id: null,
      inputRoom: '',
      dormbuildId: '',
      evaluationPeriod: '',
      hygieneScore: 0,
      gpaScore: 0,
      remarks: ''
    })

    const rules = {
      inputRoom: [
        { required: true, message: '请输入房间号', trigger: 'blur' },
        { pattern: /^\d{3}$/, message: '房间号必须是3位数字', trigger: 'blur' }
      ],
      dormbuildId: [
        { required: true, message: '请输入宿舍楼号', trigger: 'blur' },
        { pattern: /^\d+$/, message: '宿舍楼号必须是数字', trigger: 'blur' }
      ],
      evaluationPeriod: [{ required: true, message: '请输入评比周期', trigger: 'blur' }]
    }

    const starMap = {
      '无星': 0,
      '一星': 1,
      '二星': 2,
      '三星': 3,
      '四星': 4,
      '五星': 5
    }

    const fetchData = async () => {
      loading.value = true
      try {
        const res = await request.get('/evaluation/list', {
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
        inputRoom: '',
        dormbuildId: '',
        evaluationPeriod: '',
        hygieneScore: 0,
        gpaScore: 0,
        remarks: ''
      })
    }

    const handleEdit = (row) => {
      isEdit.value = true
      // 拆分房间号
      const editData = {
        ...row,
        inputRoom: row.dormroomId ? row.dormroomId.toString().slice(-3) : '',
        dormbuildId: row.dormbuildId ? row.dormbuildId.toString() : ''
      }
      Object.assign(form, editData)
      showAddDialog.value = true
    }

    const handleDelete = (row) => {
      ElMessageBox.confirm('确定要删除这条评比记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const res = await request.delete(`/evaluation/delete/${row.id}`)
        if (res.code === '0') {
          ElMessage.success('删除成功')
          fetchData()
        } else {
          ElMessage.error(res.msg || '删除失败')
        }
      }).catch(() => {})
    }

    const handlePublish = (row) => {
      ElMessageBox.confirm('确定要发布这条评比结果吗？发布后将无法修改。', '提示', {
        confirmButtonText: '确定发布',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const res = await request.put('/evaluation/publish', {
          id: row.id,
          evaluator: user.value.name
        })
        if (res.code === '0') {
          ElMessage.success('发布成功')
          fetchData()
        } else {
          ElMessage.error(res.msg || '发布失败')
        }
      }).catch(() => {})
    }

    const handleSubmit = async () => {
      if (!formRef.value) return
      await formRef.value.validate(async (valid) => {
        if (valid) {
          // 组装提交数据
          const submitData = {
            id: form.id,
            dormroomId: parseInt(form.dormbuildId + form.inputRoom),
            dormbuildId: parseInt(form.dormbuildId),
            evaluationPeriod: form.evaluationPeriod,
            hygieneScore: Number(form.hygieneScore),
            gpaScore: Number(form.gpaScore),
            remarks: form.remarks
          }
          const url = isEdit.value ? '/evaluation/update' : '/evaluation/add'
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
        case '待评选': return 'warning'
        case '已发布': return 'success'
        default: return 'info'
      }
    }

    onMounted(() => {
      fetchData()
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
      starMap,
      handleSearch,
      handleSizeChange,
      handleCurrentChange,
      handleEdit,
      handleDelete,
      handlePublish,
      handleSubmit,
      resetForm,
      getStatusType
    }
  }
}
</script>

<style scoped>
.evaluation-container { padding: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header h2 { margin: 0; color: #303133; }
.search-bar { margin-bottom: 20px; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; }
</style> 