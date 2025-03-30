import './assets/main.css';

import {createApp} from 'vue';
import App from './App.vue';
import {createVuetify} from 'vuetify';
import 'vuetify/styles'; // Vuetify 스타일
import '@mdi/font/css/materialdesignicons.css'; // 아이콘 폰트
import router from "./router/index.js"; // 라우터 설정이 있다면
import store from "./store/store.js"; // 상태 관리 라이브러리
import * as components from "vuetify/components"; // Vuetify 컴포넌트 등록
import * as directives from "vuetify/directives"; // Vuetify 디렉티브 등록

const vuetify = createVuetify({
    components,
    directives,
});
const app = createApp(App);

app.use(router); // 라우터 추가
app.use(store); // 상태 관리 라이브러리 추가
app.use(vuetify); // Vuetify 등록

app.mount("#app"); // #app에 마운트













