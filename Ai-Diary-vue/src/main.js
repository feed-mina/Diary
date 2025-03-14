import './assets/main.css'
import {createApp} from 'vue';
import App from './App.vue';
import {createVuetify} from 'vuetify';
import 'vuetify/styles'; // Vuetify 스타일
import '@mdi/font/css/materialdesignicons.css'; // 아이콘 폰트
import router from "./router/index.js"; // 라우터 설정이 있다면
import store from "./store/store.js"; // 상태 관리 라이브러리
import * as components from "vuetify/components"; // Vuetify 컴포넌트 등록
import * as directives from "vuetify/directives"; // Vuetify 디렉티브 등록
import axios from 'axios';

// 요청 인터셉터 추가: 모든 요청 전에 토큰을 헤더에 넣어줌
axios.interceptors.request.use(
  config => {
    // 일반 로그인 토큰과 카카오 로그인 토큰 중 사용 가능한 토큰 선택하기
    const token = localStorage.getItem("jwtToken") || localStorage.getItem("kakaoToken");
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

const vuetify = createVuetify({
    components,
    directives,
});
const app = createApp(App);

app.use(router); // 라우터 추가
app.use(store); // 상태 관리 라이브러리 추가
app.use(vuetify); // Vuetify 등록

app.mount("#app"); // #app에 마운트













