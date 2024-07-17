import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import VueRouter from 'vue-router'
import router from './router'
import axios from 'axios'
import VueAxios from 'vue-axios'



// Turn off Vue's production prompt
Vue.config.productionTip = false

// use plugin
Vue.use(ElementUI) // ElementUI plugin
Vue.use(VueRouter) // Routing plugin
Vue.use(VueAxios, axios) // Using Axios plugin


// Create Vue instance object
new Vue({
  // The render function will help parse the template, and the passed parameter h is a function that can be used to parse the App component
  render: h => h(App), 
  router
}).$mount('#app') // Mount the App. vue component onto the div tag with the ID app in index.html
