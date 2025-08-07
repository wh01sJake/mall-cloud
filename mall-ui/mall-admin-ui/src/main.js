import { createApp } from 'vue'
import App from './App.vue'

import router from './router/index.js'
import {createPinia} from 'pinia'
const pinia = createPinia()

import piniaPluginPersistedstate from "pinia-plugin-persistedstate"

pinia.use(piniaPluginPersistedstate)

import ElementPlus from 'element-plus' //导入element-plus
import 'element-plus/dist/index.css' //导入element-plus样式
import './assets/main.css' // Import our custom styles
import './assets/admin-theme.css' // Import professional theme

createApp(App).use(router).use(pinia).use(ElementPlus).mount('#app')