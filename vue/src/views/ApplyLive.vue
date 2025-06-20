<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>申请住宿</span>
      </div>
      <el-form :model="form" ref="form" :rules="rules" label-width="100px">
        <el-form-item label="宿舍楼" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择宿舍楼" @change="getRooms">
            <el-option
              v-for="building in buildingList"
              :key="building.id"
              :label="building.name"
              :value="building.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="房间号" prop="roomId">
          <el-select v-model="form.roomId" placeholder="请选择房间号">
            <el-option
              v-for="room in roomList"
              :key="room.id"
              :label="room.name"
              :value="room.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="入住原因" prop="reason">
          <el-input
            type="textarea"
            v-model="form.reason"
            placeholder="请输入入住原因"
            :rows="3"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit">提交申请</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      form: {
        buildingId: '',
        roomId: '',
        reason: ''
      },
      buildingList: [],
      roomList: [],
      rules: {
        buildingId: [{ required: true, message: '请选择宿舍楼', trigger: 'change' }],
        roomId: [{ required: true, message: '请选择房间号', trigger: 'change' }],
        reason: [{ required: true, message: '请输入原因', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.getBuildings()
  },
  methods: {
    getBuildings() {
      axios.get('/api/building/list').then(res => {
        this.buildingList = res.data
      })
    },
    getRooms(buildingId) {
      this.form.roomId = ''
      axios.get('/api/room/listByBuilding', { params: { buildingId } }).then(res => {
        this.roomList = res.data
      })
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          axios.post('/api/live/apply', this.form).then(res => {
            this.$message.success('申请提交成功！')
            this.reset()
          }).catch(() => {
            this.$message.error('提交失败，请稍后重试')
          })
        }
      })
    },
    reset() {
      this.$refs.form.resetFields()
      this.roomList = []
    }
  }
}
</script>

<style scoped>
.box-card {
  max-width: 600px;
  margin: auto;
}
</style>
