<template>
  <div>
    <el-breadcrumb separator-icon="ArrowRight" style="margin: 16px">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>申请退宿</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card style="margin: 15px; min-height: calc(100vh - 111px)">
      <!-- 没有宿舍时的提示 -->
      <div v-if="!hasRoom" style="margin-bottom: 20px;">
        <el-alert
          title="宿舍状态提醒"
          description="您当前没有宿舍。如需帮助请联系宿管。"
          type="warning"
          :closable="false"
          show-icon
        />
      </div>
      
      <div class="table-toolbar">
        <h3 class="table-title">退宿申请记录</h3>
        <el-button v-if="hasRoom" type="primary" @click="add" icon="Plus">申请退宿</el-button>
      </div>

      <el-table :data="tableData" stripe border :header-cell-class-name="'headerBg'">
        <el-table-column type="index" label="#" width="60"></el-table-column>
        <el-table-column prop="username" label="用户名"></el-table-column>
        <el-table-column prop="name" label="姓名"></el-table-column>
        <el-table-column prop="dormRoomId" label="宿舍号">
          <template #default="scope">
            {{ scope.row.dormRoomId ? scope.row.dormRoomId.toString().slice(-3) : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="bedNumber" label="床位号"></el-table-column>
        <el-table-column prop="reason" label="退宿原因"></el-table-column>
        <el-table-column prop="state" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.state === '通过' ? 'success' : scope.row.state === '驳回' ? 'danger' : 'warning'">
              {{ scope.row.state }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间"></el-table-column>
        <el-table-column prop="finishTime" label="处理时间"></el-table-column>
        <el-table-column label="操作" width="130px">
          <template #default="scope">
            <el-button icon="more-filled" type="default" @click="showDetail(scope.row)"></el-button>
            <el-popconfirm v-if="scope.row.state==='未处理'" title="确认撤销该申请？" @confirm="cancelQuit(scope.row)">
              <template #reference>
                <el-button icon="Delete" type="danger"></el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div style="padding: 10px 0">
        <el-pagination
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-size="pageSize"
            layout="total, prev, pager, next, jumper"
            :total="total">
        </el-pagination>
      </div>
    </el-card>

    <!-- 新增对话框 -->
    <el-dialog v-model="dialogVisible" title="申请退宿" width="30%">
      <el-form label-width="80px" size="small" :model="form" :rules="rules" ref="form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" disabled></el-input>
        </el-form-item>
        <el-form-item label="宿舍号" prop="dormRoomId">
          <el-input :value="form.dormRoomId ? form.dormRoomId.toString().slice(-3) : ''" disabled></el-input>
        </el-form-item>
        <el-form-item label="床位号" prop="bedNumber">
          <el-input v-model="form.bedNumber" disabled></el-input>
        </el-form-item>
        <el-form-item label="退宿原因" prop="reason">
          <el-input type="textarea" v-model="form.reason" placeholder="请输入退宿原因"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialog" title="申请详情" width="30%">
      <el-form label-width="80px" size="small" :model="form">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" disabled></el-input>
        </el-form-item>
        <el-form-item label="宿舍号">
          <el-input :value="(form.dormRoomId !== undefined && form.dormRoomId !== null && form.dormRoomId !== '') ? form.dormRoomId.toString().slice(-3) : '—'" disabled></el-input>
        </el-form-item>
        <el-form-item label="床位号">
          <el-input v-model="form.bedNumber" disabled></el-input>
        </el-form-item>
        <el-form-item label="退宿原因">
          <el-input type="textarea" v-model="form.reason" disabled></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-tag :type="form.state === '通过' ? 'success' : form.state === '驳回' ? 'danger' : 'warning'">
            {{ form.state }}
          </el-tag>
        </el-form-item>
        <el-form-item label="申请时间">
          <el-input v-model="form.applyTime" disabled></el-input>
        </el-form-item>
        <el-form-item label="处理时间">
          <el-input v-model="form.finishTime" disabled></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancel">关 闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>
<script src="@/assets/js/ApplyQuitRoom.js"></script>
<style scoped>
.headerBg {
  background: #eee !important;
}
.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}
.table-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}
</style> 