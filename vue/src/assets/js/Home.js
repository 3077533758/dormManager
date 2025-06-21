import weather from "@/components/weather";
import Calender from "@/components/Calendar";
import request from "@/utils/request";
import home_echarts from "@/components/home_echarts";
import { ElMessage } from "element-plus";

export default {
    name: "Home",
    components: {
        weather,
        Calender,
        home_echarts,
    },
    data() {
        return {
            studentNum: "",
            haveRoomStudentNum: "",
            repairOrderNum: "",
            noFullRoomNum: "",
            activities: [],
            hasRoom: true,
            isStudent: false,
        };
    },
    created() {
        this.checkUserStatus();
        this.getHomePageNotice();
        this.getStuNum();
        this.getHaveRoomNum();
        this.getOrderNum();
        this.getNoFullRoom();
    },
    methods: {
        checkUserStatus() {
            const user = JSON.parse(sessionStorage.getItem("user"));
            const identity = JSON.parse(sessionStorage.getItem("identity"));
            
            if (identity === 'stu') {
                this.isStudent = true;
                this.checkRoomStatus(user.username);
            }
        },
        checkRoomStatus(username) {
            request.get("/main/getStudentRoomStatus/" + username).then((res) => {
                if (res.code === "0") {
                    this.hasRoom = true;
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
        refreshRoomStatus() {
            const user = JSON.parse(sessionStorage.getItem("user"));
            if (user && user.username) {
                this.checkRoomStatus(user.username);
            }
        },
        async getStuNum() {
            request.get("/stu/stuNum").then((res) => {
                if (res.code === "0") {
                    this.studentNum = res.data;
                } else {
                    ElMessage({
                        message: res.msg,
                        type: "error",
                    });
                }
            });
        },
        async getHaveRoomNum() {
            request.get("/room/selectHaveRoomStuNum").then((res) => {
                if (res.code === "0") {
                    this.haveRoomStudentNum = res.data;
                } else {
                    ElMessage({
                        message: res.msg,
                        type: "error",
                    });
                }
            });
        },
        async getOrderNum() {
            request.get("/repair/orderNum").then((res) => {
                if (res.code === "0") {
                    this.repairOrderNum = res.data;
                } else {
                    ElMessage({
                        message: res.msg,
                        type: "error",
                    });
                }
            });
        },
        async getNoFullRoom() {
            request.get("/room/noFullRoom").then((res) => {
                if (res.code === "0") {
                    this.noFullRoomNum = res.data;
                } else {
                    ElMessage({
                        message: res.msg,
                        type: "error",
                    });
                }
            });
        },
        async getHomePageNotice() {
            request.get("/notice/homePageNotice").then((res) => {
                if (res.code === "0") {
                    this.activities = res.data;
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