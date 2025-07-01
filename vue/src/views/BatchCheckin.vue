<template>
  <div class="batch-checkin-container">
    <div class="header">
      <h2>批量入住管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="downloadTemplate">
          <el-icon><Download /></el-icon>
          下载Excel模板
        </el-button>
        <el-button type="success" @click="showUploadDialog = true">
          <el-icon><Upload /></el-icon>
          上传Excel文件
        </el-button>
      </div>
    </div>

    <!-- 批次列表 -->
    <div class="batch-list" v-if="batchList.length > 0">
      <h3>批次记录</h3>
      <el-table :data="batchList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="batchNo" label="批次号" width="200" />
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column prop="totalCount" label="总数量" width="80" />
        <el-table-column prop="successCount" label="成功数量" width="100" />
        <el-table-column prop="failCount" label="失败数量" width="100" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="scope">
            <div style="display: flex; gap: 6px;">
              <el-button
                size="small"
                type="primary"
                v-if="scope.row.status === 'PENDING' || scope.row.status === 'VALIDATED'"
                @click="revalidateBatch(scope.row)"
              >
                重新校验
              </el-button>
              <el-button
                size="small"
                type="primary"
                v-else
                @click="viewDetails(scope.row)"
              >
                查看详情
              </el-button>
              <el-button 
                size="small" 
                type="success" 
                @click="executeBatch(scope.row)"
                :disabled="scope.row.status !== 'PENDING'"
              >
                执行入住
              </el-button>
              <el-button
                size="small"
                type="danger"
                v-if="scope.row.status === 'PENDING'"
                @click="confirmDeleteBatch(scope.row)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 上传对话框 -->
    <el-dialog v-model="showUploadDialog" title="上传Excel文件" width="500px">
      <el-upload
        ref="uploadRef"
        :action="uploadUrl"
        :headers="uploadHeaders"
        :data="uploadData"
        :before-upload="beforeUpload"
        :on-success="onUploadSuccess"
        :on-error="onUploadError"
        :auto-upload="false"
        accept=".xlsx"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 xlsx 文件，且不超过 10MB
          </div>
        </template>
      </el-upload>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showUploadDialog = false">取消</el-button>
          <el-button type="primary" @click="submitUpload">上传</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog v-model="showPreviewDialog" title="数据预览" width="800px">
      <div class="preview-info">
        <p><strong>批次号：</strong>{{ currentBatch.batchNo }}</p>
        <p><strong>总数量：</strong>{{ currentBatch.totalCount }}</p>
      </div>
      
      <el-table :data="previewData" stripe style="width: 100%">
        <el-table-column prop="studentUsername" label="学号" width="120" />
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="dormroomId" label="房间号" width="100" />
        <el-table-column prop="bedNumber" label="床位号" width="80" />
        <el-table-column prop="remark" label="备注" />
      </el-table>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPreviewDialog = false">关闭</el-button>
          <el-button type="primary" @click="validateBatch">验证数据</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 验证结果对话框 -->
    <el-dialog v-model="showValidationDialog" title="验证结果" width="800px">
      <div class="validation-info">
        <p><strong>批次号：</strong>{{ currentBatch.batchNo }}</p>
        <p><strong>有效数据：</strong>{{ validationResult.validCount }} 条</p>
        <p><strong>无效数据：</strong>{{ validationResult.invalidCount }} 条</p>
      </div>
      
      <div v-if="validationResult.errors && validationResult.errors.length > 0">
        <h4>错误详情：</h4>
        <el-table :data="validationResult.errors" stripe style="width: 100%">
          <el-table-column prop="studentUsername" label="学号" width="120" />
          <el-table-column prop="studentName" label="学生姓名" width="100" />
          <el-table-column prop="roomId" label="房间号" width="100" />
          <el-table-column prop="bedNumber" label="床位号" width="80" />
          <el-table-column prop="errors" label="错误信息">
            <template #default="scope">
              <el-tag 
                v-for="error in scope.row.errors" 
                :key="error" 
                type="danger" 
                size="small" 
                style="margin-right: 4px;"
              >
                {{ error }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showValidationDialog = false">关闭</el-button>
          <el-button 
            type="primary" 
            @click="showStrategyDialog = true"
            :disabled="false"
          >
            执行入住
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 执行策略对话框 -->
    <el-dialog v-model="showStrategyDialog" title="选择执行策略" width="400px">
      <el-form :model="strategyForm" label-width="120px">
        <el-form-item label="处理策略">
          <el-radio-group v-model="strategyForm.strategy">
            <el-radio label="CONTINUE_ON_ERROR">继续处理（跳过错误）</el-radio>
            <el-radio label="ALL_OR_NOTHING">全部回滚（遇到错误停止）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-alert
            v-if="strategyForm.strategy === 'CONTINUE_ON_ERROR'"
            title="继续处理策略"
            type="info"
            :closable="false"
            show-icon
          >
            <p>遇到错误时会跳过该条记录，继续处理其他记录。适合大部分数据正确的情况。</p>
          </el-alert>
          <el-alert
            v-if="strategyForm.strategy === 'ALL_OR_NOTHING'"
            title="全部回滚策略"
            type="warning"
            :closable="false"
            show-icon
          >
            <p>遇到任何错误时，整个批次全部回滚。适合数据完整性要求高的情况。</p>
          </el-alert>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showStrategyDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmExecute">确认执行</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="批次入住详情" width="800px">
      <el-table :data="detailList" stripe style="width: 100%">
        <el-table-column prop="studentUsername" label="学号" width="120" />
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="dormroomId" label="房间号" width="100" />
        <el-table-column prop="bedNumber" label="床位号" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'SUCCESS' ? 'success' : (scope.row.status === 'FAILED' ? 'danger' : (scope.row.status === 'SKIPPED' ? 'info' : 'warning'))">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="errorMessage" label="错误信息" />
        <el-table-column prop="remark" label="备注" />
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showDetailDialog = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download, Upload, UploadFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'

export default {
  name: 'BatchCheckin',
  components: { Download, Upload, UploadFilled },
  setup() {
    const loading = ref(false)
    const batchList = ref([])
    const showUploadDialog = ref(false)
    const showPreviewDialog = ref(false)
    const showValidationDialog = ref(false)
    const showStrategyDialog = ref(false)
    const showDetailDialog = ref(false)
    const uploadRef = ref(null)
    const currentBatch = ref({})
    const previewData = ref([])
    const validationResult = ref({})
    const detailList = ref([])
    
    const strategyForm = reactive({
      strategy: 'CONTINUE_ON_ERROR'
    })

    const uploadUrl = 'api/batchCheckin/upload'
    const uploadHeaders = {}
    const uploadData = {}

    // 获取批次列表
    const fetchBatchList = async () => {
      loading.value = true
      try {
        const res = await request.get('/batchCheckin/list')
        if (res.code === '0') {
          batchList.value = res.data || []
        } else {
          ElMessage.error(res.msg || '获取批次列表失败')
        }
      } catch (error) {
        ElMessage.error('请求失败')
      } finally {
        loading.value = false
      }
    }

    // 下载模板
    const downloadTemplate = () => {
      request({
        url: '/batchCheckin/downloadTemplate',
        method: 'get',
        responseType: 'blob'
      }).then((response) => {
        const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const link = document.createElement('a')
        link.href = window.URL.createObjectURL(blob)
        link.download = '批量入住模板.xlsx'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
      }).catch(() => {
        ElMessage.error('下载失败')
      })
    }

    // 上传前检查
    const beforeUpload = (file) => {
      const isXLSX = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      const isLt10M = file.size / 1024 / 1024 < 10

      if (!isXLSX) {
        ElMessage.error('只能上传 xlsx 格式的文件!')
        return false
      }
      if (!isLt10M) {
        ElMessage.error('文件大小不能超过 10MB!')
        return false
      }
      return true
    }

    // 上传成功
    const onUploadSuccess = (response) => {
      if (response.code === '0') {
        ElMessage.success('文件上传成功')
        showUploadDialog.value = false
        currentBatch.value = response.data
        previewData.value = response.data.details
        showPreviewDialog.value = true
        fetchBatchList()
      } else {
        ElMessage.error(response.msg || '上传失败')
      }
    }

    // 上传失败
    const onUploadError = () => {
      ElMessage.error('上传失败')
    }

    // 提交上传
    const submitUpload = () => {
      uploadRef.value.submit()
    }

    // 验证批次
    const validateBatch = async () => {
      try {
        const res = await request.post('/batchCheckin/validate', null, {
          params: { batchNo: currentBatch.value.batchNo }
        })
        if (res.code === '0') {
          validationResult.value = res.data
          showPreviewDialog.value = false
          showValidationDialog.value = true
        } else {
          ElMessage.error(res.msg || '验证失败')
        }
      } catch (error) {
        ElMessage.error('验证失败')
      }
    }

    // 执行批次
    const executeBatch = async (batch) => {
      currentBatch.value = batch
      // 先校验
      try {
        const res = await request.post('/batchCheckin/validate', null, {
          params: { batchNo: batch.batchNo }
        })
        if (res.code === '0') {
          validationResult.value = res.data
          if (res.data.invalidCount > 0) {
            // 有错误，弹出校验结果弹窗，不允许执行
            showValidationDialog.value = true
          } else {
            // 全部通过，弹出执行策略弹窗
            showStrategyDialog.value = true
          }
        } else {
          ElMessage.error(res.msg || '校验失败')
        }
      } catch (error) {
        ElMessage.error('校验失败')
      }
    }

    // 确认执行
    const confirmExecute = async () => {
      try {
        const res = await request.post('/batchCheckin/execute', null, {
          params: { 
            batchNo: currentBatch.value.batchNo,
            strategy: strategyForm.strategy
          }
        })
        if (res.code === '0') {
          ElMessage.success('批量入住执行成功')
          showStrategyDialog.value = false
          showValidationDialog.value = false
          fetchBatchList()
        } else {
          ElMessage.error(res.msg || '执行失败')
        }
      } catch (error) {
        ElMessage.error('执行失败')
      }
    }

    // 查看详情
    const viewDetails = async (batch) => {
      currentBatch.value = batch
      try {
        const res = await request.get(`/batchCheckin/details/${batch.batchNo}`)
        if (res.code === '0') {
          detailList.value = res.data || []
          showDetailDialog.value = true
        } else {
          ElMessage.error(res.msg || '获取详情失败')
        }
      } catch (error) {
        ElMessage.error('获取详情失败')
      }
    }

    // 重新校验
    const revalidateBatch = async (batch) => {
      currentBatch.value = batch
      try {
        const res = await request.post('/batchCheckin/validate', null, {
          params: { batchNo: batch.batchNo }
        })
        if (res.code === '0') {
          validationResult.value = res.data
          showValidationDialog.value = true
        } else {
          ElMessage.error(res.msg || '校验失败')
        }
      } catch (error) {
        ElMessage.error('校验失败')
      }
    }

    // 获取状态类型
    const getStatusType = (status) => {
      switch (status) {
        case 'PENDING': return 'warning'
        case 'PROCESSING': return 'primary'
        case 'COMPLETED': return 'success'
        case 'FAILED': return 'danger'
        default: return 'info'
      }
    }

    // 获取状态文本
    const getStatusText = (status) => {
      switch (status) {
        case 'PENDING': return '待处理'
        case 'PROCESSING': return '处理中'
        case 'COMPLETED': return '已完成'
        case 'FAILED': return '失败'
        default: return '未知'
      }
    }

    // 确认删除批次
    const confirmDeleteBatch = async (batch) => {
      try {
        const res = await ElMessageBox.confirm(
          '确认要删除该批次吗？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        if (res) {
          await request.delete(`/batchCheckin/delete/${batch.batchNo}`)
          ElMessage.success('批次删除成功')
          fetchBatchList()
        }
      } catch (error) {
        ElMessage.error('删除失败')
      }
    }

    onMounted(() => {
      fetchBatchList()
    })

    return {
      loading,
      batchList,
      showUploadDialog,
      showPreviewDialog,
      showValidationDialog,
      showStrategyDialog,
      showDetailDialog,
      uploadRef,
      currentBatch,
      previewData,
      validationResult,
      strategyForm,
      uploadUrl,
      uploadHeaders,
      uploadData,
      detailList,
      fetchBatchList,
      downloadTemplate,
      beforeUpload,
      onUploadSuccess,
      onUploadError,
      submitUpload,
      validateBatch,
      executeBatch,
      confirmExecute,
      viewDetails,
      revalidateBatch,
      getStatusType,
      getStatusText,
      confirmDeleteBatch
    }
  }
}
</script>

<style scoped>
.batch-checkin-container {
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

.header-actions {
  display: flex;
  gap: 10px;
}

.batch-list {
  margin-top: 20px;
}

.batch-list h3 {
  margin-bottom: 16px;
  color: #303133;
}

.preview-info,
.validation-info {
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.preview-info p,
.validation-info p {
  margin: 4px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.el-upload__tip {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}
</style> 