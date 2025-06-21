<template>
  <div>
    <el-breadcrumb separator-icon="ArrowRight" style="margin: 16px">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>申请管理</el-breadcrumb-item>
      <el-breadcrumb-item>退宿申请</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card style="margin: 15px; min-height: calc(100vh - 111px)">
      <div>
        <!--    功能区-->
        <div style="margin: 10px 0">
          <div style="float: right">
            <el-tooltip content="添加" placement="top">
              <el-button icon="plus" style="width: 50px" type="primary" @click="add"></el-button>
            </el-tooltip>
          </div>
        </div>
        <!--    表格-->
        <el-table v-loading="loading" :data="tableData" border max-height="705" style="width: 100%">
          <el-table-column label="#" type="index"/>
          <el-table-column label="学号" prop="username" sortable width="100px"/>
          <el-table-column label="姓名" prop="name" width="100px"/>
          <el-table-column label="宿舍号" prop="dormRoomId" sortable/>
          <el-table-column label="床位号" prop="bedNumber" sortable/>
          <el-table-column label="退宿原因" prop="reason" width="200px"/>
          <el-table-column
              :filter-method="filterTag"
              :filters="[
              { text: '未处理', value: '未处理' },
              { text: '通过', value: '通过' },
              { text: '驳回', value: '驳回' },
            ]"
              filter-placement="bottom-end"
              label="申请状态"
              prop="state"
              sortable
              width="130px"
          >
            <template #default="scope">
              <el-tag :type="scope.row.state === '通过' ? 'success' : (scope.row.state === '驳回' ? 'danger' : 'info')"
                      disable-transitions
              >{{ scope.row.state }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="申请时间" prop="applyTime" sortable/>
          <el-table-column label="处理时间" prop="finishTime" sortable/>
          <!--      操作栏-->
          <el-table-column label="操作" width="130px">
            <template #default="scope">
              <el-button icon="more-filled" type="default" @click="showDetail(scope.row)"></el-button>
            </template>
          </el-table-column>
        </el-table>
        <!--分页-->
        <div style="margin: 10px 0">
          <el-pagination
              v-model:currentPage="currentPage"
              :page-size="pageSize"
              :page-sizes="[10, 20]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
          >
          </el-pagination>
        </div>
        <div>
          <!--      弹窗-->
          <el-dialog v-model="dialogVisible" title="申请退宿" width="30%" @close="cancel">
            <el-form ref="form" :model="form" :rules="rules" label-width="120px">
              <el-form-item label="学号" prop="username">
                <el-input v-model="form.username" disabled style="width: 80%"></el-input>
              </el-form-item>
              <el-form-item label="姓名" prop="name">
                <el-input v-model="form.name" disabled style="width: 80%"></el-input>
              </el-form-item>
              <el-form-item label="宿舍号" prop="dormRoomId">
                <el-input v-model="form.dormRoomId" disabled style="width: 80%"></el-input>
              </el-form-item>
              <el-form-item label="床位号" prop="bedNumber">
                <el-input v-model="form.bedNumber" disabled style="width: 80%"></el-input>
              </el-form-item>
              <el-form-item label="退宿原因" prop="reason">
                <el-input type="textarea" v-model="form.reason" placeholder="请输入退宿原因" style="width: 80%" :rows="3"></el-input>
              </el-form-item>
            </el-form>
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="cancel">取 消</el-button>
                <el-button type="primary" @click="save">确 定</el-button>
              </span>
            </template>
          </el-dialog>
          <!--详情信息弹窗-->
          <el-dialog v-model="detailDialog" title="申请详情" width="30%" @close="cancel">
            <el-form ref="form" :model="form" label-width="120px">
              <el-form-item label="学号：" prop="username">
                <template #default="scope">
                  <span>{{ form.username }}</span>
                </template>
              </el-form-item>
              <el-form-item label="姓名：" prop="name">
                <template #default="scope">
                  <span>{{ form.name }}</span>
                </template>
              </el-form-item>
              <el-form-item label="宿舍号：" prop="dormRoomId">
                <template #default="scope">
                  <span>{{ form.dormRoomId }}</span>
                </template>
              </el-form-item>
              <el-form-item label="床位号：" prop="bedNumber">
                <template #default="scope">
                  <span>{{ form.bedNumber }}</span>
                </template>
              </el-form-item>
              <el-form-item label="退宿原因：" prop="reason">
                <template #default="scope">
                  <span>{{ form.reason }}</span>
                </template>
              </el-form-item>
              <el-form-item label="申请时间：" prop="applyTime">
                <template #default="scope">
                  <span>{{ form.applyTime }}</span>
                </template>
              </el-form-item>
              <el-form-item label="申请状态：" prop="state">
                <template #default="scope">
                  <span>{{ form.state }}</span>
                </template>
              </el-form-item>
              <el-form-item label="处理时间：" prop="finishTime">
                <template #default="scope">
                  <span>{{ form.finishTime || '暂无' }}</span>
                </template>
              </el-form-item>
            </el-form>
          </el-dialog>
        </div>
      </div>
    </el-card>
  </div>
</template>
<script src="@/assets/js/ApplyQuitRoom.js"></script> 