<script setup>import {onMounted,ref} from 'vue';import http from '../api/http';const s=ref({});onMounted(async()=>s.value=(await http.get('/dashboard/stats')).data.data)</script>
<template><div><h3>仪表盘</h3><pre>{{s}}</pre></div></template>
