<template>
  <div>
    <el-breadcrumb separator-icon="ArrowRight" style="margin: 16px">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>申请外宿</el-breadcrumb-item>
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
        <h3 class="table-title">外宿申请记录</h3>
        <el-button v-if="hasRoom" type="primary" @click="add" icon="Plus">申请外宿</el-button>
      </div>
      <!--    表格-->
      <el-table v-loading="loading" :data="tableData" border max-height="705" style="width: 100%">
        <el-table-column label="#" type="index"/>
        <el-table-column label="学号" prop="username" sortable width="100px"/>
        <el-table-column label="姓名" prop="name" width="100px"/>
        <el-table-column label="外宿原因" prop="reason" width="200px"/>
        <el-table-column label="开始日期" prop="startDate" sortable/>
        <el-table-column label="结束日期" prop="endDate" sortable/>
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
        <el-dialog v-model="dialogVisible" title="申请外宿" width="30%" @close="cancel">
          <el-form ref="form" :model="form" :rules="rules" label-width="120px">
            <el-form-item label="学号" prop="username">
              <el-input v-model="form.username" disabled style="width: 80%"></el-input>
            </el-form-item>
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" disabled style="width: 80%"></el-input>
            </el-form-item>
            <el-form-item label="外宿原因" prop="reason">
              <el-input type="textarea" v-model="form.reason" placeholder="请输入外宿原因" style="width: 80%" :rows="3"></el-input>
            </el-form-item>
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                  v-model="form.startDate"
                  type="date"
                  placeholder="选择开始日期"
                  style="width: 80%"
                  value-format="YYYY-MM-DD"
              ></el-date-picker>
            </el-form-item>
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                  v-model="form.endDate"
                  type="date"
                  placeholder="选择结束日期"
                  style="width: 80%"
                  value-format="YYYY-MM-DD"
              ></el-date-picker>
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
            <el-form-item label="外宿原因：" prop="reason">
              <template #default="scope">
                <span>{{ form.reason }}</span>
              </template>
            </el-form-item>
            <el-form-item label="开始日期：" prop="startDate">
              <template #default="scope">
                <span>{{ form.startDate }}</span>
              </template>
            </el-form-item>
            <el-form-item label="结束日期：" prop="endDate">
              <template #default="scope">
                <span>{{ form.endDate }}</span>
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
    </el-card>
  </div>
</template>
<script src="@/assets/js/ApplyOutLive.js"></script> 
<style scoped>
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