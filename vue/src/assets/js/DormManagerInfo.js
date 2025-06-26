import request from "@/utils/request";

const {ElMessage} = require("element-plus");

export default {
    name: "DormManagerInfo",
    components: {},
    data() {
        // 手机号验证
        const checkPhone = (rule, value, callback) => {
            const phoneReg = /^1[3|4|5|6|7|8][0-9]{9}$/;
            if (!value) {
                return callback(new Error("电话号码不能为空"));
            }
            setTimeout(() => {
                if (!Number.isInteger(+value)) {
                    callback(new Error("请输入数字值"));
                } else {
                    if (phoneReg.test(value)) {
                        callback();
                    } else {
                        callback(new Error("电话号码格式不正确"));
                    }
                }
            }, 100);
        };
        const checkPass = (rule, value, callback) => {
            if (!this.editJudge) {
                console.log("验证");
                if (value == "") {
                    callback(new Error("请再次输入密码"));
                } else if (value !== this.form.password) {
                    callback(new Error("两次输入密码不一致!"));
                } else {
                    callback();
                }
            } else {
                console.log("不验证");
                callback();
            }
        };
        return {
            showpassword: true,
            editJudge: true,
            judgeAddOrEdit: true,
            loading: true,
            disabled: false,
            judge: false,
            dialogVisible: false,
            areaSelectorVisible: false,
            search: "",
            currentPage: 1,
            pageSize: 10,
            total: 0,
            pageNum: 1,
            tableData: [],
            form: {
                username: "",
                name: "",
                age: "",
                gender: "",
                phoneNum: "",
                email: "",
                dormbuildId: "",
                compoundId: null,
                managerType: "楼栋宿管",
                selectedCampus: "",
                selectedCompoundId: ""
            },
            rules: {
                username: [
                    {required: true, message: "请输入账号", trigger: "blur"},
                    {
                        pattern: /^[a-zA-Z0-9]{4,9}$/,
                        message: "必须由 4 到 9 个字母或数字组成",
                        trigger: "blur",
                    },
                ],
                name: [
                    {required: true, message: "请输入姓名", trigger: "blur"},
                    {
                        pattern: /^(?:[\u4E00-\u9FA5·]{2,10})$/,
                        message: "必须由 2 到 10 个汉字组成",
                        trigger: "blur",
                    },
                ],
                age: [
                    {required: true, message: "请输入年龄", trigger: "blur"},
                    {type: "number", message: "年龄必须为数字值", trigger: "blur"},
                    {
                        pattern: /^(1|[1-9]\d?|100)$/,
                        message: "范围：1-100",
                        trigger: "blur",
                    },
                ],
                gender: [{required: true, message: "请选择性别", trigger: "change"}],
                phoneNum: [{required: true, validator: checkPhone, trigger: "blur"}],
                email: [
                    {type: "email", message: "请输入正确的邮箱地址", trigger: "blur"},
                ],
                password: [
                    {required: true, message: "请输入密码", trigger: "blur"},
                    {
                        min: 6,
                        max: 32,
                        message: "长度在 6 到 16 个字符",
                        trigger: "blur",
                    },
                ],
                checkPass: [{validator: checkPass, trigger: "blur"}],
                dormbuildId: [],
                compoundId: [],
            },
            editDisplay: {
                display: "block",
            },
            display: {
                display: "none",
            },
            buildings: [],
            compounds: [],
            multipleSelection: [],
            campusList: [],
            compoundList: [],
            buildList: [],
        };
    },
    created() {
        this.load();
        this.loading = true;
        setTimeout(() => {
            this.loading = false;
        }, 1000);
        this.loadBuildings();
        this.loadCompounds();
        this.loadCampusList();
    },
    methods: {
        async load() {
            request.get("/dormManager/find", {
                params: {
                    pageNum: this.pageNum,
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
        loadBuildings() {
            request.get("/building/getAllWithCompound").then(res => {
                if (res.code === '0') {
                    this.buildings = res.data;
                } else {
                    this.$message.error("获取楼栋列表失败: " + res.msg);
                }
            });
        },
        loadCompounds() {
            request.get("/compound/getAll").then(res => {
                if (res.code === '0') {
                    this.compounds = res.data;
                } else {
                    this.$message.error("获取园区列表失败: " + res.msg);
                }
            });
        },
        loadCampusList() {
            request.get("/compound/getAllCampus").then(res => {
                if (res.code === '0') {
                    this.campusList = res.data;
                } else {
                    this.$message.error("获取校区列表失败: " + res.msg);
                }
            });
        },
        getBuildingName(buildingId) {
            const building = this.buildings.find(b => b.dormBuildId === buildingId);
            return building ? building.dormBuildName : `${buildingId}号楼`;
        },
        getBuildingFullPath(buildingId) {
            const building = this.buildings.find(b => b.dormBuildId === buildingId);
            if (building) {
                return `${building.campus || '未知校区'} - ${building.compoundName || '未知园区'} - ${building.dormBuildName}`;
            }
            return `${buildingId}号楼`;
        },
        getCompoundName(compoundId) {
            const compound = this.compounds.find(c => c.compoundId === compoundId);
            return compound ? compound.compoundName : `${compoundId}园区`;
        },
        showAreaSelector() {
            this.areaSelectorVisible = true;
        },
        selectBuilding(row) {
            this.form.dormbuildId = row.dormBuildId;
            this.form.compoundId = null;
            this.areaSelectorVisible = false;
            this.$message.success(`已选择：${row.dormBuildName}`);
        },
        selectCompound(row) {
            this.form.compoundId = row.compoundId;
            this.form.dormbuildId = null;
            this.areaSelectorVisible = false;
            this.$message.success(`已选择：${row.compoundName}`);
        },
        filterTag(value, row) {
            return row.gender === value;
        },
        save() {
            this.$refs.form.validate(async (valid) => {
                if (valid) {
                    if (this.form.username) {
                        // 修改
                        request.put("/dormManager/update", this.form).then((res) => {
                            console.log(res);
                            if (res.code === "0") {
                                this.$message.success("修改成功");
                                this.dialogVisible = false;
                                this.load();
                            } else {
                                this.$message.error(res.msg);
                            }
                        });
                    } else {
                        // 新增
                        request.post("/dormManager/add", this.form).then((res) => {
                            console.log(res);
                            if (res.code === "0") {
                                this.$message.success("新增成功");
                                this.dialogVisible = false;
                                this.load();
                            } else {
                                this.$message.error(res.msg);
                            }
                        });
                    }
                }
            });
        },
        cancel() {
            this.dialogVisible = false;
            this.$refs.form.resetFields();
            this.form = {
                username: "",
                password: "",
                name: "",
                age: "",
                gender: "",
                phoneNum: "",
                email: "",
                dormbuildId: "",
                compoundId: null,
                managerType: "楼栋宿管",
                selectedCampus: "",
                selectedCompoundId: ""
            };
            this.compoundList = [];
            this.buildList = [];
            this.editDisplay = {display: "block"};
            this.disabled = true;
            this.showpassword = true;
            this.display = {display: "none"};
            this.editJudge = true;
        },
        EditPass() {
            if (this.editJudge) {
                this.showpassword = false;
                this.display = {display: "flex"};
                this.disabled = false;
                this.editJudge = false;
            } else {
                this.showpassword = true;
                this.display = {display: "none"};
                this.editJudge = true;
                this.disabled = true;
            }
        },
        handleAdd() {
            this.judgeAddOrEdit = true;
            this.dialogVisible = true;
            this.$nextTick(() => {
                this.$refs.form.resetFields();
                this.form = {
                    username: "",
                    password: "",
                    name: "",
                    age: "",
                    gender: "男",
                    phoneNum: "",
                    email: "",
                    dormbuildId: "",
                    compoundId: null,
                    managerType: "楼栋宿管",
                    selectedCampus: "",
                    selectedCompoundId: ""
                };
                this.compoundList = [];
                this.buildList = [];
                this.editDisplay = {display: "block"};
                this.disabled = false;
                this.showpassword = true;
                this.display = {display: "none"};
                this.editJudge = true;
            });
        },
        handleEdit(row) {
            this.judgeAddOrEdit = false;
            this.dialogVisible = true;
            this.form = JSON.parse(JSON.stringify(row));
            
            // 根据楼栋ID反推校区和园区信息
            if (row.dormbuildId) {
                const building = this.buildings.find(b => b.dormBuildId === row.dormbuildId);
                if (building) {
                    this.form.selectedCampus = building.campus;
                    this.form.selectedCompoundId = building.compoundId;
                    
                    // 加载对应的园区和楼栋列表
                    this.handleCampusChange();
                    setTimeout(() => {
                        this.handleCompoundChange();
                    }, 100);
                }
            }
        },
        async handleDelete(username) {
            //删除
            console.log(username);
            request.delete("/dormManager/delete/" + username).then((res) => {
                if (res.code === "0") {
                    this.$message.success("删除成功");
                    this.load();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        handleSelectionChange(val) {
            console.log(val);
            this.multipleSelection = val;
        },
        reset() {
            this.search = '';
            this.load();
        },
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.load();
        },
        handleCurrentChange(pageNum) {
            this.pageNum = pageNum;
            this.load();
        },
        handleManagerTypeChange() {
            // 切换宿管类型时清空管辖区域
            this.form.dormbuildId = null;
            this.form.compoundId = null;
        },
        handleCampusChange() {
            // 清空后续选择
            this.form.selectedCompoundId = '';
            this.form.dormbuildId = '';
            this.compoundList = [];
            this.buildList = [];
            
            if (!this.form.selectedCampus) return;
            
            // 根据校区获取园区列表
            request.get(`/compound/getByCampus/${this.form.selectedCampus}`).then(res => {
                if (res.code === '0') {
                    this.compoundList = res.data;
                } else {
                    this.$message.error("获取园区列表失败: " + res.msg);
                }
            });
        },
        handleCompoundChange() {
            // 清空楼栋选择
            this.form.dormbuildId = '';
            this.buildList = [];
            
            if (!this.form.selectedCompoundId) return;
            
            // 根据园区获取楼栋列表
            request.get('/building/getByCompound', {
                params: { compoundId: this.form.selectedCompoundId }
            }).then(res => {
                if (res.code === '0') {
                    this.buildList = res.data;
                } else {
                    this.$message.error("获取楼栋列表失败: " + res.msg);
                }
            });
        },
    },
};