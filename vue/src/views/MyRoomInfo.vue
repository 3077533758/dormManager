<template>
  <div>
    <el-breadcrumb separator-icon="ArrowRight" style="margin: 16px">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>我的宿舍</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card style="margin: 15px; min-height: calc(100vh - 111px)">
      <!-- 没有宿舍时的提示 -->
      <div v-if="!hasRoom" style="text-align: center; padding: 50px;">
        <el-empty description="您当前没有宿舍">
          <el-icon style="font-size: 60px; color: #909399; margin-bottom: 20px;">
            <house />
          </el-icon>
          <p style="color: #909399; font-size: 16px; margin: 20px 0;">
            如需帮助请联系宿管
          </p>
          <el-button type="primary" @click="checkRoomStatus">
            刷新状态
          </el-button>
        </el-empty>
      </div>
      
      <!-- 有宿舍时显示宿舍信息 -->
      <div v-else style="display: flex">
        <div style="margin-top: 55px">
          <div style="margin-left: 50px;margin-top: 20px">
            <!--      房间信息-->
            <el-descriptions :column="1" border style="width: 500px" title="房间信息">
              <el-descriptions-item>
                <template #label>
                  <div>
                    <el-icon>
                      <location/>
                    </el-icon>
                    校区
                  </div>
                </template>
                <span class="rightSpan">{{ room.campusName }}</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <div>
                    <el-icon>
                      <location/>
                    </el-icon>
                    园区
                  </div>
                </template>
                <span class="rightSpan">{{ room.compoundName }}</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <div>
                    <el-icon>
                      <user/>
                    </el-icon>
                    楼栋
                  </div>
                </template>
                <span class="rightSpan">{{ room.dormBuildName }}</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <div>
                    <el-icon>
                      <location/>
                    </el-icon>
                    房间号
                  </div>
                </template>
                <span class="rightSpan">{{ room.dormRoomId ? room.dormRoomId.toString().slice(-3) : '' }}</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <div>
                    <el-icon>
                      <tickets/>
                    </el-icon>
                    楼层
                  </div>
                </template>
                <span class="rightSpan">{{ room.floorNum }}</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <div>
                    <el-icon>
                      <office-building/>
                    </el-icon>
                    可住人数
                  </div>
                </template>
                <span class="rightSpan">{{ room.maxCapacity }}</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <div>
                    <el-icon>
                      <iphone/>
                    </el-icon>
                    已住人数
                  </div>
                </template>
                <span class="rightSpan">{{ room.currentCapacity }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </div>
          <!--      床位信息-->
          <div style="margin-left: 50px;margin-top: 20px">
            <el-descriptions :column="1" border style="width: 500px" title="床位信息">
              <el-descriptions-item>
                <template #label>
                  <div>
                    <el-icon>
                      <office-building/>
                    </el-icon>
                    一号床位
                  </div>
                </template>
                <el-tag
                    v-if="this.room.firstBed != null"
                    :type="this.name === this.room.firstBed ? 'primary':'info'"
                    disable-transitions
                >{{ this.room.firstBed }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <div>
                    <el-icon>
                      <office-building/>
                    </el-icon>
                    二号床位
                  </div>
                </template>
                <el-tag
                    v-if="this.room.secondBed != null"
                    :type="this.name === this.room.secondBed ? 'primary':'info'"
                    disable-transitions
                >{{ this.room.secondBed }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <div>
                    <el-icon>
                      <office-building/>
                    </el-icon>
                    三号床位
                  </div>
                </template>
                <el-tag
                    v-if="this.room.thirdBed != null"
                    :type="this.name === this.room.thirdBed ? 'primary':'info'"
                    disable-transitions
                >{{ this.room.thirdBed }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <div>
                    <el-icon>
                      <office-building/>
                    </el-icon>
                    四号床位
                  </div>
                </template>
                <el-tag
                    v-if="this.room.fourthBed != null"
                    :type="this.name === this.room.fourthBed ? 'primary':'info'"
                    disable-transitions
                >{{ this.room.fourthBed }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
        <div style="margin-left: 100px;margin-top: 85px">
          <img alt="" src="../../public/myRoom.png" style="width: 600px">
        </div>
      </div>
    </el-card>
  </div>
</template>
<script>
import request from "@/utils/request";
import { ElMessage } from "element-plus";
export default {
    name: "MyRoomInfo",
    data() {
        return {
            name: "",
            hasRoom: true,
            form: {
                username: "",
            },
            room: {
                dormRoomId: "",
                dormBuildId: "",
                floorNum: "",
                maxCapacity: "",
                currentCapacity: "",
                firstBed: "",
                secondBed: "",
                thirdBed: "",
                fourthBed: "",
            },
        };
    },
    created() {
        this.init();
        this.checkRoomStatus();
    },
    methods: {
        init() {
            this.form = JSON.parse(sessionStorage.getItem("user"));
            this.name = this.form.username;
        },
        checkRoomStatus() {
            request.get("/main/getStudentRoomStatus/" + this.name).then((res) => {
                if (res.code === "0") {
                    this.hasRoom = true;
                    this.getInfo();
                } else {
                    this.hasRoom = false;
                    ElMessage({
                        message: "您当前没有宿舍。如需帮助请联系宿管。",
                        type: "warning",
                        duration: 5000
                    });
                }
            }).catch(() => {
                this.hasRoom = false;
                ElMessage({
                    message: "您当前没有宿舍。如需帮助请联系宿管。",
                    type: "warning",
                    duration: 5000
                });
            });
        },
        getInfo() {
            if (!this.hasRoom) {
                return;
            }
            request.get("/room/getMyRoom/" + this.name).then((res) => {
                if (res.code === "0") {
                    this.room = res.data;
                } else {
                    ElMessage({
                        message: res.msg,
                        type: "error",
                    });
                }
            });
        },
    },
};
</script>
<style scoped>@import '../assets/css/MyRoomInfo.css';</style>