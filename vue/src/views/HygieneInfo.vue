<template>
  <div class="hygiene-container">
    <div class="header">
      <h2>卫生管理</h2>
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon>
        新增卫生检查
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchQuery"
        placeholder="请输入房间号或检查人员"
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
          {{ scope.row.dormroomId ? scope.row.dormroomId.toString().slice(-3) : '' }}
        </template>
      </el-table-column>
      <el-table-column prop="checkDate" label="检查日期" width="120" />
      <el-table-column prop="doorWindowScore" label="门窗得分" width="100" />
      <el-table-column prop="itemPlacementScore" label="物品放置得分" width="120" />
      <el-table-column prop="beddingScore" label="被褥得分" width="100" />
      <el-table-column prop="cleanlinessScore" label="清洁得分" width="100" />
      <el-table-column prop="overallScore" label="整体效果得分" width="120" />
      <el-table-column prop="totalScore" label="总分" width="80" />
      <el-table-column prop="grade" label="等级" width="80">
        <template #default="scope">
          <el-tag :type="getGradeType(scope.row.grade)">
            {{ scope.row.grade }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="checker" label="检查人员" width="100" />
      <el-table-column prop="remarks" label="备注" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
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
      :title="isEdit ? '编辑卫生检查' : '新增卫生检查'"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="房间号" prop="inputRoom">
          <el-input v-model="form.inputRoom" maxlength="3" placeholder="如101, 305" />
        </el-form-item>
        <el-form-item label="宿舍楼号" prop="dormbuildId">
          <el-input v-model="form.dormbuildId" placeholder="请输入宿舍楼号" />
        </el-form-item>
        <el-form-item label="检查日期" prop="checkDate">
          <el-date-picker
            v-model="form.checkDate"
            type="date"
            placeholder="选择检查日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="门窗得分" prop="doorWindowScore">
          <el-input-number
            v-model="form.doorWindowScore"
            :min="0"
            :max="20"
            placeholder="0-20分"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="物品放置得分" prop="itemPlacementScore">
          <el-input-number
            v-model="form.itemPlacementScore"
            :min="0"
            :max="20"
            placeholder="0-20分"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="被褥得分" prop="beddingScore">
          <el-input-number
            v-model="form.beddingScore"
            :min="0"
            :max="20"
            placeholder="0-20分"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="清洁得分" prop="cleanlinessScore">
          <el-input-number
            v-model="form.cleanlinessScore"
            :min="0"
            :max="20"
            placeholder="0-20分"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="整体效果得分" prop="overallScore">
          <el-input-number
            v-model="form.overallScore"
            :min="0"
            :max="20"
            placeholder="0-20分"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="检查人员" prop="checker">
          <el-input v-model="form.checker" placeholder="请输入检查人员姓名" />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input
            v-model="form.remarks"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
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
  name: 'HygieneInfo',
  components: {
    Plus,
    Search
  },
  setup() {
    const loading = ref(false)
    const tableData = ref([])
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const searchQuery = ref('')
    const showAddDialog = ref(false)
    const isEdit = ref(false)
    const formRef = ref()

    const form = reactive({
      id: null,
      inputRoom: '',
      dormbuildId: '',
      checkDate: '',
      doorWindowScore: 0,
      itemPlacementScore: 0,
      beddingScore: 0,
      cleanlinessScore: 0,
      overallScore: 0,
      checker: '',
      remarks: ''
    })

    const rules = {
      inputRoom: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
      dormbuildId: [{ required: true, message: '请输入宿舍楼号', trigger: 'blur' }],
      checkDate: [{ required: true, message: '请选择检查日期', trigger: 'change' }],
      checker: [{ required: true, message: '请输入检查人员', trigger: 'blur' }]
    }

    // 获取数据
    const fetchData = async () => {
      loading.value = true
      try {
        const response = await request.get('/hygiene/list', {
          params: {
            pageNum: currentPage.value,
            pageSize: pageSize.value,
            search: searchQuery.value
          }
        })
        if (response.code === '0') {
          tableData.value = response.data.records
          total.value = response.data.total
        }
      } catch (error) {
        ElMessage.error('获取数据失败')
      } finally {
        loading.value = false
      }
    }

    // 搜索
    const handleSearch = () => {
      currentPage.value = 1
      fetchData()
    }

    // 分页
    const handleSizeChange = (val) => {
      pageSize.value = val
      fetchData()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      fetchData()
    }

    // 编辑
    const handleEdit = (row) => {
      isEdit.value = true
      Object.assign(form, row)
      showAddDialog.value = true
    }

    // 删除
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const response = await request.delete(`/hygiene/delete/${row.id}`)
        if (response.code === '0') {
          ElMessage.success('删除成功')
          fetchData()
        } else {
          ElMessage.error(response.msg || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }

    // 提交表单
    const handleSubmit = async () => {
      try {
        await formRef.value.validate()
        
        const url = isEdit.value ? '/hygiene/update' : '/hygiene/add'
        const method = isEdit.value ? 'put' : 'post'
        
        const response = await request[method](url, form)
        if (response.code === '0') {
          ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
          showAddDialog.value = false
          resetForm()
          fetchData()
        } else {
          ElMessage.error(response.msg || '操作失败')
        }
      } catch (error) {
        ElMessage.error('表单验证失败')
      }
    }

    // 重置表单
    const resetForm = () => {
      Object.assign(form, {
        id: null,
        inputRoom: '',
        dormbuildId: '',
        checkDate: '',
        doorWindowScore: 0,
        itemPlacementScore: 0,
        beddingScore: 0,
        cleanlinessScore: 0,
        overallScore: 0,
        checker: '',
        remarks: ''
      })
      isEdit.value = false
    }

    // 获取等级标签类型
    const getGradeType = (grade) => {
      switch (grade) {
        case '优': return 'success'
        case '良': return 'warning'
        case '中': return 'info'
        case '差': return 'danger'
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
      handleSearch,
      handleSizeChange,
      handleCurrentChange,
      handleEdit,
      handleDelete,
      handleSubmit,
      getGradeType
    }
  }
}
</script>

<style scoped>
.hygiene-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  color: #303133;
}

.search-bar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 