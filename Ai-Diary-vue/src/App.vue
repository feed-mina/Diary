<script>
import {computed, ref} from 'vue'; // 사용하지 않는 reactive, onMounted 삭제
import Home from '@/page/Home.vue';
import NotFound from '@/page/NotFound.vue';
import DiaryList from "@/page/DiaryList.vue";
import { useRoute } from 'vue-router';

import DiaryHeader from "@/components/Header.vue";
import DiaryNav from "@/components/DiaryNav.vue";
import DiaryFooter from "@/components/Footer.vue"
import axios from "axios";
import { apiUrl } from "@/api/index.js";

export default {
  components: {
    DiaryHeader,
    DiaryNav,
    DiaryFooter,
  },
  setup() {
    const route = useRoute(); // 현재 라우트 정보 가져오기
    console.log("@@@@App inerceptors");
    console.log(import.meta.env.VITE_API_URL);
    console.log(import.meta.env.VUE_API_URL);

    // axios.defaults.baseURL = import.meta.env.VITE_APP_API_BASE_URL;
    // 서버 주소 적용
    axios.defaults.baseURL = apiUrl;  // 서버 주소 적용


// 요청 인터셉터 추가: 모든 요청 전에 토큰을 헤더에 넣어줌
    axios.interceptors.request.use(
        config => {
          const excludeUrls = ["/api/timer/now", "/api/timer/health"]; // 제외할 API 목록
          const isExcluded = excludeUrls.some((url) => config.url.includes(url));
          let token = localStorage.getItem("jwtToken") || localStorage.getItem("kakaoAccessToken");
          console.log("🌐 현재 페이지:", window.location.href);
          console.log("🧪 로컬스토리지 토큰:", token);
          if (!isExcluded) {
            if (token) {
              console.log("📡 Axios 인터셉터 실행 로그인 전 - JWT Token:", token);
              if (!token.startsWith("Bearer ")) {
                token = `Bearer ${token}`;
              }
              config.headers["Authorization"] = token;
            }
          } else {
            console.log(`🛑 ${config.url} 요청에는 Authorization 헤더를 추가하지 않음.`);
          }

          return config;
        },
        error => {
          if (error.response && error.response.status === 401) {
            console.log("🚨 401 Unauthorized 발생 - 로그인 페이지로 리디렉트");
            alert("로그인이 필요합니다.");
          }
          return Promise.reject(error);
        }
    );
    // 라우터 튜토리얼
    const routes = {
      '/': Home,
      '/notFound': NotFound,
      '/diary/common': DiaryList,
    };

    const currentPath = ref(window.location.hash);

    // pomoLogin 또는 pomoMain 페이지인지 확인
    const isPomoPage = computed(() => {
      return route.path === '/pomoLogin' || route.path === '/pomoMain';
    });
    return {
      isPomoPage,
    };
  },
};


</script>

<template>
  <v-app>
    <div class="main-wrap">
      <div class="page-wrap">
        <!-- DiaryNav: pomoLogin, pomoMain 페이지가 아닐 때만 표시 -->
        <DiaryNav v-if="!isPomoPage"/> <!--왼쪽 고정 네비게이션-->
        <div class="content-wrap">
          <header v-if="!isPomoPage">
            <img alt="Vue logo" class="logo" src="@/assets/favicon.png" width="125" height="125"/>
          </header>
          <!-- <DiaryHeader/> -->
          <RouterView/> <!-- 현재 경로에 맞는 컴포넌트 렌더링 -->
        </div>
        <DiaryFooter v-if="!isPomoPage"/>

      </div>
    </div>
  </v-app>
</template>

<style>
@import "./assets/main.css";
</style>