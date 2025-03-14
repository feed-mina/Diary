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

<style scoped>
.home {
  display: flex;
  justify-content: center;
  height: 100vh;
  background-color: #f8f9fa;
  padding: 1rem;
}

.main-content {
  height: 70%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.home-bubble {
  margin-top: 5rem;
  width: 30rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: #e0f7fa;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.home-button,
.login-button,
.diarywrite-button,
.diarycommon-button {
  margin: 10px;
  padding: 10px 20px;
  background: #00796b;
  color: white;
  border-radius: 20px;
}

button:hover {
  background-color: #f9e9e9;
  color: black;
  transition: 0.3s;
}
</style>
