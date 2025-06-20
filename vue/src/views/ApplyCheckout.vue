<template>
  <div class="apply-leave-dorm">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>申请退宿</span>
      </div>

      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="学号" prop="studentNumber">
          <el-input v-model="form.studentNumber" disabled></el-input>
        </el-form-item>

        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" disabled></el-input>
        </el-form-item>

        <el-form-item label="原宿舍号" prop="currentRoom">
          <el-input v-model="form.currentRoom" disabled></el-input>
        </el-form-item>

        <el-form-item label="退宿原因" prop="reason">
          <el-input type="textarea" v-model="form.reason" placeholder="请输入退宿原因"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">提交申请</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'ApplyLeaveDorm',
  data() {
    return {
      form: {
        studentNumber: '',
        name: '',
        currentRoom: '',
        reason: '',
      },
      rules: {
        reason: [
          { required: true, message: '请输入退宿原因', trigger: 'blur' },
          { min: 5, message: '不少于5个字', trigger: 'blur' },
        ],
      },
    }
  },
  created() {
    // 模拟从本地或后台获取学生信息
    const userInfo = JSON.parse(localStorage.getItem('user')) || {};
    this.form.studentNumber = userInfo.studentNumber || '20210001';
    this.form.name = userInfo.name || '张三';
    this.form.currentRoom = userInfo.currentRoom || '1栋201';
  },
  methods: {
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          // 替换为你实际的API路径
          this.$axios.post('/api/leaveDorm/apply', this.form).then(() => {
            this.$message.success('退宿申请提交成功');
            this.resetForm();
          }).catch(() => {
            this.$message.error('提交失败，请稍后重试');
          });
        }
      });
    },
    resetForm() {
      this.$refs.form.resetFields();
    },
  },
}
</script>

<style scoped>
.box-card {
  max-width: 600px;
  margin: 0 auto;
  margin-top: 30px;
}
</style>
