<script setup>
import http from '../api/http';import {ref} from 'vue';import {useRouter} from 'vue-router';
const u=ref('admin'),p=ref('admin123'); const r=useRouter(); const msg=ref('');
const login=async()=>{const res=await http.post('/login',{username:u.value,password:p.value}); if(res.data.code===200){localStorage.setItem('user',JSON.stringify(res.data.data)); r.push('/dashboard')} else msg.value=res.data.message}
</script>
<template><div><h3>登录</h3><input v-model='u'/><input v-model='p' type='password'/><button @click='login'>登录</button><p>{{msg}}</p></div></template>
