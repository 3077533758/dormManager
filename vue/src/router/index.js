import Layout from '../layout/Layout.vue'
import {createRouter, createWebHistory} from "vue-router";


export const constantRoutes = [
    {path: '/Login', name: 'Login', component: () => import("@/views/Login")},
    {
        path: '/Layout', name: 'Layout', component: Layout, children: [
            //
            {path: '/home', name: 'Home', component: () => import("@/views/Home")},
            {path: '/stuInfo', name: 'StuInfo', component: () => import("@/views/StuInfo")},
            {path: '/dormManagerInfo', name: 'DormManagerInfo', component: () => import("@/views/DormManagerInfo")},
            {path: '/buildingInfo', name: 'BuildingInfo', component: () => import("@/views/BuildingInfo")},
            {path: '/compoundInfo', name: 'CompoundInfo', component: () => import("@/views/CompoundInfo")},
            {path: '/roomInfo', name: 'RoomInfo', component: () => import("@/views/RoomInfo")},
            {path: '/noticeInfo', name: 'NoticeInfo', component: () => import("@/views/NoticeInfo")},
            {path: '/adjustRoomInfo', name: 'AdjustRoomInfo', component: () => import("@/views/AdjustRoomInfo")},
            {path: '/quitRoomInfo', name: 'QuitRoomInfo', component: () => import("@/views/QuitRoomInfo")},
            {path: '/outLiveInfo', name: 'OutLiveInfo', component: () => import("@/views/OutLiveInfo")},
            {path: '/repairInfo', name: 'RepairInfo', component: () => import("@/views/RepairInfo")},
            {path: '/visitorInfo', name: 'VisitorInfo', component: () => import("@/views/VisitorInfo")},
            // 新增功能模块
            {path: '/hygieneInfo', name: 'HygieneInfo', component: () => import("@/views/HygieneInfo")},
            {path: '/violationInfo', name: 'ViolationInfo', component: () => import("@/views/ViolationInfo")},
            {path: '/evaluationInfo', name: 'EvaluationInfo', component: () => import("@/views/EvaluationInfo")},
            {path: '/checkinInfo', name: 'CheckinInfo', component: () => import("@/views/CheckinInfo")},
            {path: '/reportInfo', name: 'ReportInfo', component: () => import("@/views/ReportInfo")},
            {path: '/campusMonitor', name: 'CampusMonitor', component: () => import("@/views/CampusMonitor")},
            //
            {path: '/myRoomInfo', name: 'MyRoomInfo', component: () => import("@/views/MyRoomInfo")},
            {path: '/applyRepairInfo', name: 'ApplyRepairInfo', component: () => import("@/views/ApplyRepairInfo")},
            {path: '/applyChangeRoom', name: 'ApplyChangeRoom', component: () => import("@/views/ApplyChangeRoom")},
            {path: '/applyQuitRoom', name: 'ApplyQuitRoom', component: () => import("@/views/ApplyQuitRoom")},
            {path: '/applyOutLive', name: 'ApplyOutLive', component: () => import("@/views/ApplyOutLive")},

            {path: '/selfInfo', name: 'SelfInfo', component: () => import("@/views/SelfInfo")},
        ]
    },

]
const router = createRouter({
    routes: constantRoutes,
    history: createWebHistory(process.env.BASE_URL)
})
//路由守卫
router.beforeEach((to, from, next) => {
    //to 要访问的路径
    //from 代表从哪个路径跳转而来
    // next 是函数，表示放行
    // next() 放行
    // next('/*') 强制跳转
    const user = window.sessionStorage.getItem('user')
    if (to.path === '/Login') {
        return next();
    }
    if (!user) {
        return next('/Login')
    }
    if (to.path === '/' && user) {
        return next('/home')
    }
    next()
})

export default router
