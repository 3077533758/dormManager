import request from "@/utils/request";

const {ElMessage} = require("element-plus");

export default {
    name: "BuildingInfo",
    components: {},
    data() {
        return {
            loading: true,
            disabled: false,
            judge: false,
            dialogVisible: false,
            search: "",
            currentPage: 1,
            pageSize: 10,
            total: 0,
            tableData: [],
            compoundOptions: [], // 园区选项
            campusOptions: [], // 校区选项
            form: {
                dormBuildId: "",
                dormBuildName: "",
                dormBuildDetail: "",
                campus: "",
                compoundId: "",
            },
            rules: {
                dormBuildId: [
                    {required: true, message: "请输入编号", trigger: "blur"},
                    {
                        pattern: /^(1|[1-9]\d?|100)$/,
                        message: "范围：1-100",
                        trigger: "blur",
                    },
                ],
                dormBuildName: [
                    {required: true, message: "请输入名称", trigger: "blur"},
                ],
                campus: [
                    {required: true, message: "请选择校区", trigger: "change"},
                ],
                compoundId: [
                    {required: true, message: "请选择园区", trigger: "change"},
                ],
                dormBuildDetail: [
                    {required: true, message: "请输入备注", trigger: "blur"},
                ],
            },
        };
    },
    created() {
        this.load();
        this.loadCampusOptions();
        this.loading = true;
        setTimeout(() => {
            //设置延迟执行
            this.loading = false;
        }, 1000);
    },
    methods: {
        async load() {
            request.get("/building/find", {
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
            });
        },
        reset() {
            this.search = ''
            request.get("/building/find", {
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
            return row.dormBuildDetail === value;
        },
        // 加载校区选项
        loadCampusOptions() {
            request.get("/compound/getAllCampus").then((res) => {
                if (res.code == 200 || res.code == 0) {
                    this.campusOptions = res.data;
                } else {
                    this.$message.error("获取校区列表失败: " + res.msg);
                    this.campusOptions = ['东校区', '西校区', '南校区', '北校区'];
                }
            }).catch(() => {
                // 如果请求失败，使用默认值
                this.campusOptions = ['东校区', '西校区', '南校区', '北校区'];
            });
        },
        // 加载园区选项
        loadCompoundOptions(campus) {
            if (campus) {
                request.get(`/compound/getByCampus/${campus}`).then((res) => {
                    if (res.code === "0") {
                        this.compoundOptions = res.data;
                    }
                });
            } else {
                this.compoundOptions = [];
            }
        },
        // 校区变化时更新园区选项
        onCampusChange(campus) {
            this.form.compoundId = ""; // 清空园区选择
            this.loadCompoundOptions(campus);
        },
        add() {
            this.dialogVisible = true;
            this.$nextTick(() => {
                this.$refs.form.resetFields();
                this.disabled = false;
                this.form = {};
                this.judge = false;
                this.compoundOptions = [];
            });
        },
        save() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    if (this.judge === false) {
                        //新增
                        request.post("/building/add", this.form).then((res) => {
                            console.log(res);
                            if (res.code === "0") {
                                ElMessage({
                                    message: "新增成功",
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
                    } else {
                        //修改
                        request.put("/building/update", this.form).then((res) => {
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
                }
            });
        },
        cancel() {
            this.$refs.form.resetFields();
            this.dialogVisible = false;
        },
        handleEdit(row) {
            //修改
            this.judge = true;
            this.dialogVisible = true;
            this.$nextTick(() => {
                this.$refs.form.resetFields();
                // 深拷贝
                this.form = JSON.parse(JSON.stringify(row));
                this.disabled = true;
                // 加载对应的园区选项
                this.loadCompoundOptions(this.form.campus);
            });
        },
        handleDelete(id) {
            console.log(id);
            request.delete("/building/delete/" + id).then((res) => {
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
    },
};