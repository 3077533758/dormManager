import request from "@/utils/request";

const {ElMessage} = require("element-plus");
export default {
    name: "ApplyOutLive",
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
            hasRoom: true,
            rules: {
                reason: [
                    {required: true, message: '请输入外宿原因', trigger: 'blur'}
                ],
                startDate: [
                    {required: true, message: '请选择开始日期', trigger: 'change'}
                ],
                endDate: [
                    {required: true, message: '请选择结束日期', trigger: 'change'}
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
            request.get("/outLive/findByUsername/" + username, {
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
            
            this.dialogVisible = true
            this.$nextTick(() => {
                this.$refs.form.resetFields()
                const user = JSON.parse(sessionStorage.getItem("user"))
                this.form.username = user.username
                this.form.name = user.name
            })
        },
        save() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    // 验证日期
                    if (this.form.startDate >= this.form.endDate) {
                        ElMessage({
                            message: "结束日期必须晚于开始日期",
                            type: "error",
                        })
                        return
                    }
                    
                    request.post("/outLive/add", this.form).then((res) => {
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
        }
    }
} 