<template>
  <div>
    <el-breadcrumb separator-icon="ArrowRight" style="margin: 16px">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>用户管理</el-breadcrumb-item>
      <el-breadcrumb-item>宿管信息</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card style="margin: 15px; min-height: calc(100vh - 111px)">
      <div>
        <!--    功能区-->
        <div style="margin: 10px 0">
          <!--    搜索区-->
          <div style="margin: 10px 0">
            <el-input style="width: 200px" placeholder="请输入宿管用户名或姓名" suffix-icon="el-icon-search" v-model="search"></el-input>
            <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
            <el-button type="warning" @click="reset">重置</el-button>
          </div>
          <div style="float: right">
            <el-tooltip content="添加" placement="top">
              <el-button type="primary" @click="handleAdd">新增 <i class="el-icon-plus"></i></el-button>
            </el-tooltip>
          </div>
        </div>
        <!--    表格-->
        <el-table v-loading="loading" :data="tableData" border max-height="705" style="width: 100%"
                  @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column label="#" type="index"/>
          <el-table-column label="账号" prop="username" sortable/>
          <el-table-column label="姓名" prop="name"/>
          <el-table-column
              :filter-method="filterTag"
              :filters="[
              { text: '男', value: '男' },
              { text: '女', value: '女' },
            ]"
              filter-placement="bottom-end"
              label="性别"
              prop="gender"
          />
          <el-table-column label="年龄" prop="age" sortable/>
          <el-table-column label="手机号" prop="phoneNum"/>
          <el-table-column label="邮箱" prop="email"/>
          <el-table-column label="管辖区域" width="200">
            <template #default="scope">
              <span v-if="scope.row.managerType === '楼栋宿管' && scope.row.dormbuildId">
                {{ getBuildingName(scope.row.dormbuildId) }}
              </span>
              <span v-else-if="scope.row.managerType === '围合宿管' && scope.row.compoundId">
                {{ getCompoundName(scope.row.compoundId) }}
              </span>
              <span v-else class="text-muted">未分配</span>
            </template>
          </el-table-column>
          <el-table-column label="宿管类型" prop="managerType" width="140"></el-table-column>
          <!--      操作栏-->
          <el-table-column label="操作" width="130px">
            <template #default="scope">
              <el-button icon="Edit" type="primary" @click="handleEdit(scope.row)"></el-button>
              <el-popconfirm title="确认删除？" @confirm="handleDelete(scope.row.username)">
                <template #reference>
                  <el-button icon="Delete" type="danger"></el-button>
                </template>
              </el-popconfirm>
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
          <el-dialog v-model="dialogVisible" title="操作" width="30%" @close="cancel">
            <el-form ref="form" :model="form" :rules="rules" label-width="120px">
              <el-form-item label="账号" prop="username">
                <el-input v-model="form.username" :disabled="judgeAddOrEdit" style="width: 80%"></el-input>
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="form.password" :disabled="disabled" :show-password="showpassword"
                          style="width: 80%"></el-input>
                <el-tooltip content="修改密码" placement="right">
                  <el-icon :style="editDisplay" size="large" style="margin-left: 5px; cursor: pointer"
                           @click="EditPass">
                    <edit/>
                  </el-icon>
                </el-tooltip>
              </el-form-item>
              <el-form-item :style="display" label="确认密码" prop="checkPass">
                <el-input v-model="form.checkPass" :show-password="showpassword" style="width: 80%"></el-input>
              </el-form-item>
              <el-form-item label="姓名" prop="name">
                <el-input v-model="form.name" style="width: 80%"></el-input>
              </el-form-item>
              <el-form-item label="年龄" prop="age">
                <el-input v-model.number="form.age" style="width: 80%"></el-input>
              </el-form-item>
              <el-form-item label="性别" prop="gender">
                <el-radio v-model="form.gender" label="男">男</el-radio>
                <el-radio v-model="form.gender" label="女">女</el-radio>
              </el-form-item>
              <el-form-item label="手机号" prop="phoneNum">
                <el-input v-model.number="form.phoneNum" style="width: 80%"></el-input>
              </el-form-item>
              <el-form-item label="邮箱地址" prop="email">
                <el-input v-model="form.email" style="width: 80%"></el-input>
              </el-form-item>
              <el-form-item label="宿管类型" prop="managerType">
                <el-select v-model="form.managerType" placeholder="请选择宿管类型" @change="handleManagerTypeChange">
                  <el-option label="楼栋宿管" value="楼栋宿管"></el-option>
                  <el-option label="围合宿管" value="围合宿管"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="管辖区域" v-if="form.managerType">
                <el-button type="primary" @click="showAreaSelector">选择管辖区域</el-button>
                <div style="margin-top: 10px; padding: 10px; background-color: #f5f5f5; border-radius: 4px;">
                  <span v-if="form.managerType === '楼栋宿管' && form.dormbuildId">
                    已选择：{{ getBuildingName(form.dormbuildId) }}
                  </span>
                  <span v-else-if="form.managerType === '围合宿管' && form.compoundId">
                    已选择：{{ getCompoundName(form.compoundId) }}
                  </span>
                  <span v-else class="text-muted">请选择管辖区域</span>
                </div>
              </el-form-item>
            </el-form>
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="cancel">取 消</el-button>
                <el-button type="primary" @click="save">确 定</el-button>
              </span>
            </template>
          </el-dialog>
        </div>

        <!-- 管辖区域选择对话框 -->
        <el-dialog v-model="areaSelectorVisible" title="选择管辖区域" width="60%">
          <div v-if="form.managerType === '楼栋宿管'">
            <h4>选择管理的楼栋：</h4>
            <el-table :data="buildings" @row-click="selectBuilding" style="cursor: pointer;">
              <el-table-column prop="dormBuildId" label="楼栋号" width="100"></el-table-column>
              <el-table-column prop="dormBuildName" label="楼栋名称" width="150"></el-table-column>
              <el-table-column prop="campus" label="园区" width="100"></el-table-column>
              <el-table-column prop="compoundName" label="所属围合" width="150"></el-table-column>
              <el-table-column prop="dormBuildDetail" label="备注"></el-table-column>
            </el-table>
          </div>
          <div v-else-if="form.managerType === '围合宿管'">
            <h4>选择管理的围合：</h4>
            <el-table :data="compounds" @row-click="selectCompound" style="cursor: pointer;">
              <el-table-column prop="compoundId" label="围合ID" width="100"></el-table-column>
              <el-table-column prop="compoundName" label="围合名称" width="150"></el-table-column>
              <el-table-column prop="campus" label="园区" width="100"></el-table-column>
              <el-table-column prop="compoundDetail" label="备注"></el-table-column>
            </el-table>
          </div>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="areaSelectorVisible = false">取 消</el-button>
            </span>
          </template>
        </el-dialog>
      </div>
    </el-card>
  </div>
</template>

<script src="@/assets/js/DormManagerInfo.js"></script>

<style scoped>
.headerBg {
  background: #eee !important;
}
.text-muted {
  color: #999;
}
</style>