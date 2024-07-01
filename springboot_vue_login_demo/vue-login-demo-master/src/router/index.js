// 此文件专门负责项目的路由
import Vue from 'vue';
import VueRouter from "vue-router"
import CoEdit from '@/views/co-edit/Co-edit.vue'; // 导入Co-edit组件

// 引入组件
import Login from '../views/login/Login'
import Register from '../views/register/Register'
import Home from '../views/home/Home'
import HomeStackHolder from '../views/home/Home-StackHolder'
import { Message } from "element-ui";

Vue.use(VueRouter);

// 创建并暴露一个路由器
const router = new VueRouter({
    mode: 'history',    // 路由模式，该模式不会在地址中显示井号#
    base: process.env.BASE_URL,
    routes: [
        {
            path: '/pdf-detail/:pdfData', // 定义带参数的路由
            name: 'PdfDetail',
            component: CoEdit, // 修改为指向 Co-edit.vue 组件
            props: route => ({ user: route.params.user }) // 将路由参数作为props传递给组件
        },
        {
            path: '/',          // 路径
            redirect: '/login'  // 重定向
        },
        {
            path: '/login',     // 路径
            component: Login    // 跳转到的组件
        },
        {
            path: '/register',     // 路径
            component: Register    // 跳转到的组件
        },
        {
            path: '/home',     // 路径
            component: Home    // 跳转到的组件
        },
        {
            path: '/home-stackholder',
            component: HomeStackHolder
        }
    ]
})

// 导航守卫，前置处理
router.beforeEach((to, from, next) => {
    let isAuthenticated = !!sessionStorage.getItem('userInfo')
    // 如果路由要跳转到除了登录和注册的界面的话就判断是否已经登录，如果没有登录就强制跳到登录界面
    if (to.path !== '/login' && to.path !== '/register' && !isAuthenticated) {
        next({ path: '/login' })
        Message({
            message: 'Please login first!',
            type: "warning",
        });
    } else next()
})

export default router;