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

export default {
  components: {
    DiaryHeader,
    DiaryNav,
    DiaryFooter,
  },
  setup() {
    const route = useRoute(); // 현재 라우트 정보 가져오기
    console.log("@@@@App inerceptors");
// 요청 인터셉터 추가: 모든 요청 전에 토큰을 헤더에 넣어줌
    axios.interceptors.request.use(
        config => {
          const excludeUrls = ["/api/timer/now", "/api/timer/health"]; // 제외할 API 목록
          const isExcluded = excludeUrls.some((url) => config.url.includes(url));

          if (!isExcluded) {
            let token = localStorage.getItem("jwtToken");

            console.log("📡 Axios 인터셉터 실행 - JWT Token:", token);

            if (token) {
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
        (error) => {
          console.error("❌ Axios 인터셉터 에러:", error);
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
    // const currentView = computed(() => {
    //   return routes[currentPath.value.slice(1) || '/'] || NotFound;
    // });


    return {
      // currentView,
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
.page-wrap {
  display: flex;
  flex-direction: row;
  box-sizing: border-box;
  width: 100%;
  /* height: 100%; */
}

.main-wrap {
  display: flex;
  /* width: 100vw; */
  height: 100%;
  /*height: 100vh;  전체 높이 맞추기 */
  margin: 0; /* 혹시 있을 여백 제거 */
  background-color: #ffffff;
}

.content-wrap {
  flex: 1; /* 남은 공간을 본문이 차지 */
  height: 100%; /* 부모 컨테이너의 높이를 100% 채움 */
  padding: 20px; /* 본문에 여백 추가 */
  margin-bottom: 2rem;
  /* overflow-y: auto; 스크롤 가능 */
}

.DiaryNav {
  width: 20%; /* 네비게이션의 너비 */
  height: 100%;
  background-color: #f4f4f4; /* 배경색 */
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1); /* 그림자 효과 */
}

.text-decoration-line-through {
  text-decoration: line-through;
}

header {
  width: 100%;
  /**
  line-height: 1.5;
  max-height: 100vh; */
}

footer {
  width: 100%;
  margin-top: auto;

}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: 1rem;
    /* padding-right: calc(var(--section-gap) / 2); */
    justify-content: space-between;
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }

  .content-wrap {
    flex: 1; /* 남은 공간을 본문이 차지 */
    padding: 20px; /* 본문에 여백 추가 */
    margin-bottom: 2rem;
    /* overflow-y: auto; 스크롤 가능 */
  }

  .DiaryNav {
    width: 250px; /* 네비게이션의 너비 */
    height: 100%;
    background-color: #f4f4f4; /* 배경색 */
    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1); /* 그림자 효과 */
  }

}
</style>