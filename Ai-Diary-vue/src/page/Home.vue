<template>
  <div class="home">
    <div class="main-content">
      <div id="HomeBubble" class="home-bubble">
        <div id="HomeText" class="home-text">
          <span class="home-header">
            <strong>감정다이어리</strong>
          </span>

          <!-- 로그인 여부에 따라 버튼 표시 -->
          <button type="button" v-if="!isLoggedIn" class="home-button" @click="navigateTo('/diary/tutorial')">
            튜토리얼 보러가기
          </button>
          <button type="button" v-if="!isLoggedIn" class="login-button" @click="navigateTo('/login')">
            로그인 하러가기
          </button>
          <button type="button" v-if="isLoggedIn" class="diarywrite-button" @click="navigateTo('/diary/write')">
            일기 작성하기
          </button>
          <button type="button" v-if="isLoggedIn" class="diarycommon-button" @click="navigateTo('/diary/common')">
            일기장 보러가기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from "vue";
import { useRouter } from "vue-router";

export default {
  name: 'HomePage',
  setup() {
    const router = useRouter();

    // ✅ JWT 대신 localStorage에서 로그인 여부 확인
    const isLoggedIn = computed(() => {
      return !!localStorage.getItem("email") || !!localStorage.getItem("kakaoAccessToken");
    });

    const navigateTo = (path) => {
      router.push(path);
    };

    return {
      isLoggedIn,
      navigateTo
    };
  }
};
</script>


<style>
@import "@/assets/base.css";
</style>

