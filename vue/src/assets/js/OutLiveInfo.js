import request from "@/utils/request";

const {ElMessage} = require("element-plus");
export default {
    name: "OutLiveInfo",
    data() {
        return {
            loading: true,
            search: '',
            currentPage: 1,
            pageSize: 10,
            total: 0,
            detailDialog: false,
            tableData: [],
            form: {},
            buildings: [], // 所有楼栋（含校区、园区）
            managedBuildingId: null, // 当前管辖楼栋id
            managedBuildingFullName: '', // 当前管辖楼栋全称
            currentIdentity: JSON.parse(sessionStorage.getItem('identity') || '""'), // 当前用户身份
            showManagedBuilding: false // 是否显示管辖楼栋信息
        }
    },
    created() {
        this.load();
        // 只有宿管才显示管辖楼栋信息
        if (this.currentIdentity === 'dormManager') {
            this.showManagedBuilding = true;
            this.loadBuildings();
        }
    },
    methods: {
        loadBuildings() {
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
                this.managedBuildingFullName = `${building.campus || '未知校区'}·${building.compoundName || '未知园区'}·${building.dormBuildName}`;
            } else {
                this.managedBuildingFullName = this.managedBuildingId + '号楼';
            }
        },
        load() {
            this.loading = true
            request.get("/outLive/find", {
                params: {
                    pageNum: this.currentPage,
                    pageSize: this.pageSize,
                    search: this.search
                }
            }).then(res => {
                this.loading = false
                this.tableData = res.data.records
                this.total = res.data.total
                // 只有宿管才自动获取管辖楼栋id
                if (this.showManagedBuilding && this.tableData.length && this.tableData[0].dormRoomId) {
                    const rid = this.tableData[0].dormRoomId;
                    this.managedBuildingId = parseInt(rid.toString().substring(0, rid.toString().length - 3));
                    this.updateManagedBuildingFullName();
                }
            })
        },
        showDetail(row) {
            this.detailDialog = true
            this.$nextTick(() => {
                this.form = JSON.parse(JSON.stringify(row))
            })
        },
        handleApprove(row) {
            this.$confirm('确认通过该外宿申请吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                row.state = '通过'
                request.put("/outLive/update/true", row).then((res) => {
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
            this.$confirm('确认驳回该外宿申请吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                row.state = '驳回'
                request.put("/outLive/update/false", row).then((res) => {
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