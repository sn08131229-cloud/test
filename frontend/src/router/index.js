import {createRouter,createWebHistory} from 'vue-router';
import Login from '../views/Login.vue';import Dashboard from '../views/Dashboard.vue';import Device from '../views/Device.vue';import Purchase from '../views/Purchase.vue';import Borrow from '../views/Borrow.vue';import Flows from '../views/Flows.vue';
export default createRouter({history:createWebHistory(),routes:[{path:'/',component:Login},{path:'/dashboard',component:Dashboard},{path:'/devices',component:Device},{path:'/purchase',component:Purchase},{path:'/borrow',component:Borrow},{path:'/flows',component:Flows}]})
