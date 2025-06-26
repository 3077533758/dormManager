import request from "@/utils/request";

const {ElMessage} = require("element-plus");
export default {
    name: "AdjustRoomInfo",
    data() {
        const checkRoomState = (rule, value, callback) => {
            this.dormRoomId = value
            request.get("/room/checkRoomState/" + value).then((res) => {
                if (res.code === "0") {
                    callback();
                } else {
                    callback(new Error(res.msg));
                }
            });
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
            // 只要选择了状态就通过
            if (value === "通过" || value === "驳回" || value === "未处理") {
                callback();
            } else {
                callback(new Error("请选择申请状态"));
            }
        };
        return {
            loading: true,
            dialogVisible: false,
            detailDialog: false,
            search: "",
            currentPage: 1,
            pageSize: 10,
            total: 0,
            tableData: [],
            form: {},
            dormRoomId: 0,
            orderState: false,
            buildings: [], // 所有楼栋（含校区、园区）
            managedBuildingId: null, // 当前管辖楼栋id
            managedBuildingFullName: '', // 当前管辖楼栋全称
            currentUsername: JSON.parse(sessionStorage.getItem('user') || '{}').username,
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
                state: [{validator: checkApplyState, trigger: "blur"},],
                towardsRoomId: [{validator: checkRoomState, trigger: "blur"}],
                towardsBedId: [{validator: checkBedState, trigger: "blur"}],
            },
        }
    },
    created() {
        this.load();
        this.loading = true;
        this.loadBuildings();
        setTimeout(() => {
            //设置延迟执行
            this.loading = false;
        }, 1000);
    },
    methods: {
        async loadBuildings() {
            // 获取所有楼栋（含校区、园区）
            request.get('/building/getAllWithCompound').then(res => {
                if (res.code === '0') {
                    this.buildings = res.data;
                    this.updateManagedBuildingFullName();
                }
            });
        },
        updateManagedBuildingFullName() {
            if (!this.managedBuildingId || !this.buildings.length) return;
            const building = this.buildings.find(b => b.dormBuildId === this.managedBuildingId);
            if (building) {
                this.managedBuildingFullName = `${building.campus || '未知校区'}-${building.compoundName || '未知园区'}-${building.dormBuildName}`;
            } else {
                this.managedBuildingFullName = this.managedBuildingId + '号楼';
            }
        },
        async load() {
            request.get("/adjustRoom/find", {
                params: {
                    pageNum: this.currentPage,
                    pageSize: this.pageSize,
                    search: this.search,
                },
            }).then((res) => {
                console.log(res);
                this.tableData = res.data.records;
                this.total = res.data.total;
                this.loading = false;
                // 自动获取管辖楼栋id（取第一页第一条数据的dormBuildId）
                if (this.tableData.length && this.tableData[0].currentRoomId) {
                    // 假设currentRoomId前几位为楼栋id
                    const rid = this.tableData[0].currentRoomId;
                    // 取楼栋id（如1101->1，2102->2，需根据实际编码规则调整）
                    // 这里假设楼栋id为rid的前1位
                    this.managedBuildingId = parseInt(rid.toString().substring(0, rid.toString().length - 3));
                    this.updateManagedBuildingFullName();
                }
            });
        },
        reset() {
            this.search = ''
            request.get("/adjustRoom/find", {
                params: {
                    pageNum: 1,
                    pageSize: this.pageSize,
                    search: this.search,
                },
            }).then((res) => {
                console.log(res);
                this.tableData = res.data.records;
                this.total = res.data.total;
                this.loading = false;
            });
        },
        filterTag(value, row) {
            return row.gender === value;
        },
        judgeOrderState(state) {
            this.form.state = state;
            if (state === '通过') {
                this.orderState = true
            } else if (state === '驳回') {
                this.orderState = false
            }
        },
        save() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    this.judgeOrderState(this.form.state)
                    //修改
                    request.put("/adjustRoom/update/" + this.orderState, this.form).then((res) => {
                        console.log(res);
                        if (res.code === "0") {
                            ElMessage({
                                message: "修改成功",
                                type: "success",
                            });
                            this.search = "";
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
            });
        },
        async handleDelete(id) {
            //删除
            request.delete("/adjustRoom/delete/" + id).then((res) => {
                if (res.code === "0") {
                    ElMessage({
                        message: "删除成功",
                        type: "success",
                    });
                    this.search = "";
                    this.load();
                } else {
                    ElMessage({
                        message: res.msg,
                        type: "error",
                    });
                }
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
        async handleRevoke(id, username) {
            // 撤销调宿申请
            request.delete(`/adjustRoom/studentDelete/${id}/${username}`).then((res) => {
                if (res.code === "0") {
                    ElMessage({
                        message: "撤销成功",
                        type: "success",
                    });
                    this.search = "";
                    this.load();
                } else {
                    ElMessage({
                        message: res.msg,
                        type: "error",
                    });
                }
            });
        },
    },
}