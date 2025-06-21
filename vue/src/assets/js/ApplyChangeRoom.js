import request from "@/utils/request";

const {ElMessage} = require("element-plus");
export default {
    name: "AdjustRoomInfo",
    data() {
        const checkRoomState = (rule, value, callback) => {
            this.dormRoomId = value
            if (typeof value === "number") {
                request.get("/room/checkRoomState/" + value).then((res) => {
                    request.get("/room/checkRoomExist/" + value).then((result) => {
                        if (result.code === "-1") {
                            callback(new Error(result.msg));
                        }
                        if (res.code === "-1") {
                            callback(new Error(res.msg));
                        }
                        callback();
                    })
                });
            } else {
                callback(new Error("请输入正确的数据"));
            }
        };
        const checkBedState = (rule, value, callback) => {
            request.get("/room/checkBedState/" + this.dormRoomId + '/' + value).then((res) => {
                if (res.code === "0") {
                    callback();
                } else {
                    callback(new Error(res.msg));
                }
            });
        };
        const checkApplyState = (rule, value, callback) => {
            console.log(this.form.finishTime)
            if (value === "通过" && this.form.finishTime !== null) {
                callback();
            } else if (value === "驳回" && this.form.finishTime !== null) {
                callback();
            } else {
                callback(new Error("请检查订单完成状态与选择时间是否匹配"));
            }
        };
        return {
            loading: true,
            dialogVisible: false,
            detailDialog: false,
            currentPage: 1,
            pageSize: 10,
            total: 0,
            tableData: [],
            form: {},
            dormRoomId: 0,
            orderState: false,
            judgeOption: false,
            hasRoom: true,
            rules: {
                username: [
                    {required: true, message: "请输入学号", trigger: "blur"},
                    {pattern: /^[a-zA-Z0-9]{4,9}$/, message: "必须由 2 到 5 个字母或数字组成", trigger: "blur",},
                ],
                name: [
                    {required: true, message: "请输入姓名", trigger: "blur"},
                    {pattern: /^(?:[\u4E00-\u9FA5·]{2,10})$/, message: "必须由 2 到 10 个汉字组成", trigger: "blur",},
                ],
                currentRoomId: [
                    {required: true, message: "请输入当前房间号", trigger: "blur"},
                ],
                currentBedId: [
                    {required: true, message: "请输入当前床位号", trigger: "blur"},
                ],
                towardsRoomId: [
                    {required: true, message: '请输入目标房间号', trigger: 'blur'},
                    {validator: checkRoomState, trigger: 'blur'}
                ],
                towardsBedId: [
                    {required: true, message: '请输入目标床位号', trigger: 'blur'},
                    {validator: checkBedState, trigger: 'blur'}
                ],
                applyTime: [
                    {required: true, message: '请选择申请时间', trigger: 'change'}
                ],
                state: [
                    {validator: checkApplyState, trigger: 'change'}
                ]
            },
        }
    },
    created() {
        this.checkRoomStatus()
        this.load();
        this.loading = true;
        setTimeout(() => {
            //设置延迟执行
            this.loading = false;
        }, 1000);
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
            request.get("/adjustRoom/findByUsername/" + username, {
                params: {
                    pageNum: this.currentPage,
                    pageSize: this.pageSize
                }
            }).then(res => {
                this.tableData = res.data.records;
                this.total = res.data.total;
                this.loading = false;
            });
        },
        filterTag(value, row) {
            return row.state === value;
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
                this.form.username = JSON.parse(sessionStorage.getItem("user")).username;
                this.form.name = JSON.parse(sessionStorage.getItem("user")).name;
                request.get("/room/getMyRoom/" + this.form.username).then((res) => {
                    this.form.currentRoomId = res.data.dormRoomId
                    this.form.currentBedId = this.calBedNum(this.form.username, res.data)
                });
                this.judgeOption = true;
            });
        },
        calBedNum(username, data) {
            if (data.firstBed === username) {
                return 1;
            } else if (data.secondBed === username) {
                return 2;
            } else if (data.thirdBed === username) {
                return 3;
            } else if (data.fourthBed === username) {
                return 4;
            }
        },
        judgeOrderState(state) {
            if (state === '通过') {
                this.orderState = true
            } else if (state === '驳回') {
                this.orderState = false
            } else if (state === '未处理') {
                this.orderState = false
            }
        },
        save() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    if (this.judgeOption === false) {
                        //修改
                        this.judgeOrderState(this.form.state)
                        request.put("/adjustRoom/update/" + this.orderState, this.form).then((res) => {
                            if (res.code === "0") {
                                ElMessage({
                                    message: "修改成功",
                                    type: "success",
                                });
                                this.load();
                                this.dialogVisible = false;
                            } else if (res.msg === "重复操作") {
                                ElMessage({
                                    message: res.msg,
                                    type: "error",
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
                    } else if (this.judgeOption === true) {
                        //添加
                        request.post("/adjustRoom/add", this.form).then((res) => {
                            if (res.code === "0") {
                                ElMessage({
                                    message: "申请提交成功",
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
                }
            });
        },
        cancel() {
            this.$refs.form.resetFields();
            this.dialogVisible = false;
            this.detailDialog = false;
        },
        showDetail(row) {
            // 查看详情
            this.detailDialog = true;
            this.$nextTick(() => {
                this.$refs.form.resetFields();
                this.form = JSON.parse(JSON.stringify(row));
            });
        },
        handleEdit(row) {
            //修改
            // 生拷贝
            this.dialogVisible = true;
            this.$nextTick(() => {
                this.$refs.form.resetFields();
                this.form = JSON.parse(JSON.stringify(row));
                this.judgeOption = false;
            });
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
    },
}