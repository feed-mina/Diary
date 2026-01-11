<script>
import { onMounted, watch, computed, ref } from 'vue';
import { useRoute } from 'vue-router';
import { useTheme } from 'vuetify';
import { useAppStore } from './store/useAppStore.js';

import Home from '@/page/Home.vue';
import NotFound from '@/page/NotFound.vue';
import DiaryList from '@/page/DiaryList.vue';
import DiaryHeader from '@/components/Header.vue';
import DiaryNav from '@/components/DiaryNav.vue';
import DiaryFooter from '@/components/Footer.vue';
import axiosInstance, { apiUrl } from '@/unit/axiosInstance.js';

import './assets/main.css';

export default {
  components: {
    DiaryHeader,
    DiaryNav,
    DiaryFooter,
  },
  setup() {
    const route = useRoute();
    const theme = useTheme();
    const store = useAppStore();

    // 바디 클래스 변경 함수
    function updateBodyClass(path) {
      const root = document.documentElement;
      if (path === '/pomoLogin' || path === '/pomoMain') {
        root.classList.add('pomo-mode');
      } else {
        root.classList.remove('pomo-mode');
      }
    }


    //  페이지 진입 시 실행
    onMounted(() => {
      updateBodyClass(route.path);
      console.log("jwtToken:", localStorage.getItem("jwtToken"));
      console.log("kakaoAccessToken:", localStorage.getItem("kakaoAccessToken"));
      console.log("현재 API 주소:", apiUrl);
    });

    //  라우트 변경 시 바디 클래스 조정
    watch(() => route.path, (newPath) => {
      updateBodyClass(newPath);
    });

    // Pomo 전용 페이지 여부
    const isPomoPage = computed(() => {
      return route.path === '/pomoLogin' || route.path === '/pomoMain';
    });

    return {
      store,
      isPomoPage,
    };
  }
};
</script>


<template>
  <v-app>
    <div :class="store.isDarkMode ? '' : 'main-wrap'">

    <div class="page-wrap">
        <!-- DiaryNav: pomoLogin, pomoMain 페이지가 아닐 때만 표시 -->
        <DiaryNav v-if="!isPomoPage"/> <!--왼쪽 고정 네비게이션-->
        <div class="content-wrap">
          <header v-if="!isPomoPage">
            <img alt="Vue logo" class="logo" src="/img/favicon.png" width="125" height="125"/>
          </header>
          <RouterView/> <!-- 현재 경로에 맞는 컴포넌트 렌더링 -->
        </div>
<!--        <DiaryFooter v-if="!isPomoPage"/>-->

      </div>
    </div>
  </v-app>
</template>
