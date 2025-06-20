<template>
  <div class="container">
    <div style="display: flex; width: 700px; height: 350px; margin: auto;
                position: absolute;
	              top: 0;
	              left: 0;
	              right: 0;
	              bottom: 0;">
      <div style="flex: 1; background-color: #ffffff;">
        <img src="@/assets/宿舍.png" style="margin: auto; width: 100%; height: 100%;">
      </div>
      <div style="flex: 1; background-color: #ffffff;">
        <div style="text-align: center; font-weight: 600; font-size: 20px; margin: 15px; color: #000000">
          校园宿舍管理系统
        </div>
        <el-form style="margin: 20px" ref="form" :model="form" :rules="rules" size="large">
          <el-form-item prop="username">
            <el-input v-model="form.username" clearable prefix-icon="avatar"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" prefix-icon="lock" show-password></el-input>
          </el-form-item>
          <el-form-item :model="form" prop="identity" style="margin-left: 2px;font-size: 30px!important;">
            <el-radio v-model="form.identity" label="stu" style="color: grey">学生</el-radio>
            <el-radio v-model="form.identity" label="dormManager" style="color: grey">宿舍管理员</el-radio>
            <el-radio v-model="form.identity" label="admin" style="color: grey">系统管理员</el-radio>
          </el-form-item>
          <el-form-item>
            <el-button :disabled="!disabled" type="primary" @click="login">登 录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>
<script src="@/assets/js/Login.js"></script>
<style scoped>
/deep/ .el-input--large{
  margin-top: 13px;
}
/deep/ .el-button{
  width: 100%!important;
  color: white;
  border: none;
  font-size: 20px;
  margin-top: 10px;
}

</style>
