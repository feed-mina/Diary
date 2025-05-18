import './assets/main.css';
import {createApp} from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import {createVuetify} from 'vuetify';
import 'vuetify/styles'; // Vuetify 스타일
import '@mdi/font/css/materialdesignicons.css'; // 아이콘 폰트
import router from "./router/index.js"; // 라우터 설정이 있다면
import * as components from "vuetify/components"; // Vuetify 컴포넌트 등록
import * as directives from "vuetify/directives"; // Vuetify 디렉티브 등록
import axios from "axios";

const vuetify = createVuetify({
    components,
    directives,
    theme: {
        defaultTheme: 'light', // 기본 테마는 light
        themes: {
            light: {
                dark: false, // 밝은 테마
                colors: {}
            },
            dark: {
                dark: true, // 어두운 테마
                colors: {}
            }
        }
    }
});
const app = createApp(App);
axios.defaults.withCredentials = true;

app.use(router); // 라우터 추가
app.use(vuetify); // Vuetify 등록
app.use(createPinia()); // Pinia 등록

app.mount("#app"); // #app에 마운트













