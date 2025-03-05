<script>
import { ref, computed } from 'vue'; // 사용하지 않는 reactive, onMounted 삭제
import Home from '@/page/Home.vue';
import About from '@/page/About.vue';
import NotFound from '@/page/NotFound.vue';    
import ComponentB from '@/components/ComponentB.vue';
import ComponentC from '@/components/ComponentC.vue';
import DiaryList from "@/page/DiaryList.vue";

import DiaryHeader from "@/components/Header.vue";
import DiaryNav from "@/components/DiaryNav.vue";
import DiaryFooter from "@/components/Footer.vue"

export default {
  components: {
    DiaryHeader,
    DiaryNav,
    DiaryFooter, 
  },
  setup() {
    // 라우터 튜토리얼
    const routes = {
      '/': Home,
      '/about': About,
      '/notFound': NotFound, 
      '/diary/common': DiaryList,
      '/componentC': ComponentC, 
    };

    const currentPath = ref(window.location.hash);
    
    const currentView = computed(() => {
      return routes[currentPath.value.slice(1) || '/'] || NotFound;
    }); 
    return {
      currentView,
    };
  },
};
 

</script>

<template>
  <v-app>
    <div class="main-wrap">
      <div class="page-wrap">
        <DiaryNav/> <!--왼쪽 고정 네비게이션-->
        <div class="content-wrap">
          <header>
            <img alt="Vue logo" class="logo" src="@/assets/logo.svg" width="125" height="125" />
          </header>
          <!-- <DiaryHeader/> -->
          <RouterView /> <!-- 현재 경로에 맞는 컴포넌트 렌더링 -->
        </div>
        <DiaryFooter />

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

.main-wrap{
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
  width:100%;
  /**
  line-height: 1.5;
  max-height: 100vh; */
}
footer{
  width:100%;
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