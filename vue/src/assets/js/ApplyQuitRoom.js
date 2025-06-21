import request from "@/utils/request";

const {ElMessage} = require("element-plus");
export default {
    name: "ApplyQuitRoom",
    data() {
        return {
            loading: true,
            search: '',
            currentPage: 1,
            pageSize: 10,
            total: 0,
            dialogVisible: false,
            detailDialog: false,
            tableData: [],
            form: {},
            rules: {
                reason: [
                    {required: true, message: '请输入退宿原因', trigger: 'blur'}
                ]
            }
        }
    },
    created() {
        this.load()
    },
    methods: {
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
            this.dialogVisible = true
            this.$nextTick(() => {
                this.$refs.form.resetFields()
                const user = JSON.parse(sessionStorage.getItem("user"))
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