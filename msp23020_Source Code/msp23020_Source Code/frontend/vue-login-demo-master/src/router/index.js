// This document is specifically responsible for the routing of the project
import Vue from 'vue';
import VueRouter from "vue-router"
import CoEdit from '@/views/co-edit/Co-edit.vue'; // Import Co-edit component


import Login from '../views/login/Login'
import Register from '../views/register/Register'
import Home from '../views/home/Home'
import HomeStackHolder from '../views/home-stakeholder/Home-StackHolder'
import { Message } from "element-ui";

Vue.use(VueRouter);

// Create and expose a router
const router = new VueRouter({
    mode: 'history',    // Routing mode, which does not display ‘#’ in the address
    base: process.env.BASE_URL,
    routes: [
        {
            path: '/pdf-detail/:pdfData', // Define routes with parameters
            name: 'PdfDetail',
            component: CoEdit, // Change to point to Co-edit.vue component
            props: route => ({
                user: route.params.user,
                fileinfo: route.params.fileinfo // Add fileinfo parameter
            })
        },
        {
            path: '/',          
            redirect: '/login'  
        },
        {
            path: '/login',     
            component: Login    // Jump to the component
        },
        {
            path: '/register',     
            component: Register    
        },
        {
            path: '/home',     
            component: Home
        },
        {
            path: '/home-stackholder',
            component: HomeStackHolder
        }
    ]
})

// Navigation guard, pre-processing
router.beforeEach((to, from, next) => {
    let isAuthenticated = !!sessionStorage.getItem('userInfo')
    // If the route needs to jump to interfaces other than login and registration,
    // check if it has already logged in. If it has not logged in, force it to jump to the login interface
    if (to.path !== '/login' && to.path !== '/register' && !isAuthenticated) {
        next({ path: '/login' })
        Message({
            message: 'Please login first!',
            type: "warning",
        });
    } else next()
})

export default router;