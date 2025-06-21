import request from "@/utils/request";

const {ElMessage} = require("element-plus");
export default {
    name: "QuitRoomInfo",
    data() {
        return {
            loading: true,
            search: '',
            currentPage: 1,
            pageSize: 10,
            total: 0,
            detailDialog: false,
            tableData: [],
            form: {}
        }
    },
    created() {
        this.load()
    },
    methods: {
        load() {
            this.loading = true
            request.get("/quitRoom/find", {
                params: {
                    pageNum: this.currentPage,
                    pageSize: this.pageSize,
                    search: this.search
                }
            }).then(res => {
                this.loading = false
                this.tableData = res.data.records
                this.total = res.data.total
            })
        },
        showDetail(row) {
            this.detailDialog = true
            this.$nextTick(() => {
                this.form = JSON.parse(JSON.stringify(row))
            })
        },
        handleApprove(row) {
            this.$confirm('确认通过该退宿申请吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                row.state = '通过'
                request.put("/quitRoom/update/true", row).then((res) => {
                    if (res.code === "0") {
                        ElMessage({
                            message: "审批成功",
                            type: "success",
                        })
                        this.load()
                    } else {
                        ElMessage({
                            message: res.msg,
                            type: "error",
                        })
                    }
                })
            })
        },
        handleReject(row) {
            this.$confirm('确认驳回该退宿申请吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                row.state = '驳回'
                request.put("/quitRoom/update/false", row).then((res) => {
                    if (res.code === "0") {
                        ElMessage({
                            message: "审批成功",
                            type: "success",
                        })
                        this.load()
                    } else {
                        ElMessage({
                            message: res.msg,
                            type: "error",
                        })
                    }
                })
            })
        },
        cancel() {
            this.detailDialog = false
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