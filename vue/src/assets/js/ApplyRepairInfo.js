import request from "@/utils/request";

const {ElMessage} = require("element-plus");

export default {
    name: "ApplyRepairInfo",
    components: {},
    data() {
        return {
            loading: true,
            dialogVisible: false,
            detailDialog: false,
            currentPage: 1,
            pageSize: 10,
            total: 0,
            tableData: [],
            detail: {},
            name: '',
            username: '',
            form: {},
            hasRoom: true,
            room: {
                dormRoomId: '',
                dormBuildId: '',
            },
            rules: {
                title: [{required: true, message: "请输入标题", trigger: "blur"}],
                content: [{required: true, message: "请输入内容", trigger: "blur"}],
                orderBuildTime: [{required: true, message: "请选择时间", trigger: "blur"},],
            },
        };
    },
    created() {
        this.init()
        this.checkRoomStatus()
        this.getInfo()
        this.load()
        this.loading = true
        setTimeout(() => {
            //设置延迟执行
            this.loading = false
        }, 1000);
    },
    methods: {
        init() {
            this.form = JSON.parse(sessionStorage.getItem("user"));
            this.name = this.form.name;
            this.username = this.form.username;
        },
        checkRoomStatus() {
            request.get("/main/getStudentRoomStatus/" + this.username).then((res) => {
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
        async load() {
            if (!this.hasRoom) {
                return;
            }
            request.get("/repair/find/" + this.username, {
                params: {
                    pageNum: this.currentPage,
                    pageSize: this.pageSize,
                },
            }).then((res) => {
                console.log(res);
                this.tableData = res.data.records;
                this.total = res.data.total;
                this.loading = false;
            });
        },
        getInfo() {
            if (!this.hasRoom) {
                return;
            }
            request.get("/room/getMyRoom/" + this.username).then((res) => {
                if (res.code === "0") {
                    this.room = res.data;
                    console.log(this.room);
                } else {
                    ElMessage({
                        message: res.msg,
                        type: "error",
                    });
                }
            });
        },
        filterTag(value, row) {
            return row.state === value;
        },
        showDetail(row) {
            this.detailDialog = true;
            this.$nextTick(() => {
                this.detail = row;
                this.form = JSON.parse(JSON.stringify(row));
                this.form.displayRoomId = this.form.dormRoomId ? this.form.dormRoomId.toString().slice(-3) : '';
            });
        },
        closeDetails() {
            this.detailDialog = false;
        },
        add() {
            if (!this.hasRoom) {
                ElMessage({
                    message: "您当前没有宿舍。如需帮助请联系宿管。",
                    type: "warning"
                })
                return
            }
            
            this.dialogVisible = true;
            this.$nextTick(() => {
                this.$refs.form.resetFields();
                this.form.repairer = this.username
                this.form.dormBuildId = this.room.dormBuildId
                this.form.dormRoomId = this.room.dormRoomId
                this.form.displayRoomId = this.shortRoomId
            });
        },
        save() {
            this.$refs.form.validate(async (valid) => {
                if (valid) {                    //新增
                    console.log(this.form)
                    await request.post("/repair/add", this.form).then((res) => {
                        if (res.code === "0") {
                            ElMessage({
                                message: "新增成功",
                                type: "success",
                            });
                            this.load();
                            this.dialogVisible = false;
                        } else {
                            ElMessage({
                                message: res.msg,
                                type: "error",
                            });
                        }
                    });
                }
            })
        },
        cancel() {
            this.$refs.form.resetFields();
            this.dialogVisible = false;
        },
        handleSizeChange(pageSize) {
            //改变每页个数
            this.pageSize = pageSize;
            this.load();
        },
        handleCurrentChange(pageNum) {
            //改变页码
            this.currentPage = pageNum;
            this.load();
        },
        cancelRepair(row) {
            this.$confirm('确定要撤销该报修申请吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                request.delete(`/repair/delete/${row.id}`).then(res => {
                    if (res.code === '0') {
                        ElMessage({ message: '撤销成功', type: 'success' })
                        this.load()
                    } else {
                        ElMessage({ message: res.msg, type: 'error' })
                    }
                })
            }).catch(() => {})
        },
    },
    computed: {
        shortRoomId() {
            return this.room.dormRoomId ? this.room.dormRoomId.toString().slice(-3) : '';
        }
    },
};