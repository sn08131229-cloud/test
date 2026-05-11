<script setup>import {ref,onMounted} from 'vue';import http from '../api/http';const flows=ref([]);onMounted(async()=>flows.value=(await http.get('/flows')).data.data)</script>
<template><div><h3>流转记录</h3><pre>{{flows}}</pre></div></template>
