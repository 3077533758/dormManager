import request from "@/utils/request";

const {ElMessage} = require("element-plus");
export default {
    name: "ApplyQuitRoom",
    data() {
        return {
            loading: true,
            currentPage: 1,
            pageSize: 10,
            total: 0,
            dialogVisible: false,
            detailDialog: false,
            tableData: [],
            form: {},
            hasRoom: true,
            rules: {
                reason: [
                    {required: true, message: '请输入退宿原因', trigger: 'blur'}
                ]
            }
        }
    },
    created() {
        this.checkRoomStatus()
        this.load()
    },
    methods: {
        checkRoomStatus() {
            const username = JSON.parse(sessionStorage.getItem("user")).username
            request.get("/main/getStudentRoomStatus/" + username).then((res) => {
                if (res.code === "0") {
                    this.hasRoom = true
                } else {
                    this.hasRoom = false
                    ElMessage({
                        message: "您当前没有宿舍。如需帮助请联系宿管。",
                        type: "warning",
                        duration: 5000
                    })
                }
            }).catch(() => {
                this.hasRoom = false
                ElMessage({
                    message: "您当前没有宿舍。如需帮助请联系宿管。",
                    type: "warning",
                    duration: 5000
                })
            })
        },
        load() {
            this.loading = true
            const username = JSON.parse(sessionStorage.getItem("user")).username
            request.get("/quitRoom/findByUsername/" + username, {
                params: {
                    pageNum: this.currentPage,
                    pageSize: this.pageSize
                }
            }).then(res => {
                this.loading = false
                this.tableData = res.data.records
                this.total = res.data.total
            })
        },
        add() {
            if (!this.hasRoom) {
                ElMessage({
                    message: "您当前没有宿舍。如需帮助请联系宿管。",
                    type: "warning"
                })
                return
            }
            const user = JSON.parse(sessionStorage.getItem("user"))
            // 新增：校验未处理调宿申请
            request.get(`/stu/apply-status/${user.username}`).then((res) => {
                if (res.data.hasPendingAdjust) {
                    ElMessage({
                        message: "您有未处理的调宿申请，不能再提交退宿申请！",
                        type: "warning"
                    })
                    return
                }
                this.dialogVisible = true
                this.$nextTick(() => {
                    this.$refs.form.resetFields()
                    this.form.username = user.username
                    this.form.name = user.name
                    // 获取学生当前宿舍信息
                    request.get("/room/getMyRoom/" + user.username).then((res) => {
                        if (res.code === "0") {
                            this.form.dormRoomId = res.data.dormRoomId
                            this.form.bedNumber = this.calBedNum(user.username, res.data)
                        }
                    })
                })
            })
        },
        save() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    request.post("/quitRoom/add", this.form).then((res) => {
                        if (res.code === "0") {
                            ElMessage({
                                message: "申请提交成功",
                                type: "success",
                            })
                            this.dialogVisible = false
                            this.load()
                        } else {
                            ElMessage({
                                message: res.msg,
                                type: "error",
                            })
                        }
                    })
                }
            })
        },
        showDetail(row) {
            this.detailDialog = true
            this.$nextTick(() => {
                this.form = JSON.parse(JSON.stringify(row))
            })
        },
        cancel() {
            this.dialogVisible = false
            this.detailDialog = false
            this.$refs.form.resetFields()
        },
        handleSizeChange(pageSize) {
            this.pageSize = pageSize
            this.load()
        },
        handleCurrentChange(pageNum) {
            this.currentPage = pageNum
            this.load()
        },
        filterTag(value, row) {
            return row.state === value
        },
        calBedNum(username, roomInfo) {
            if (roomInfo.firstBed === username) {
                return 1
            } else if (roomInfo.secondBed === username) {
                return 2
            } else if (roomInfo.thirdBed === username) {
                return 3
            } else if (roomInfo.fourthBed === username) {
                return 4
            }
            return 0
        }
    }
} 