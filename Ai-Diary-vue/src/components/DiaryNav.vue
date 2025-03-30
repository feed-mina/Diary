<script>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";

export default {
  name: "DiaryNav",
  setup() {
    const router = useRouter();
    const loginPassword = localStorage.getItem('password')
    const loginToken = localStorage.getItem('jwtToken'); // 저장된 토큰 가져오기
    const loggedInUserId = localStorage.getItem('userId');
    const kakaoAccessToken = localStorage.getItem('kakaoAccessToken');
    const email = localStorage.getItem("email");

    console.log("loginPassword : ", loginPassword);
    console.log("loginToken : ", loginToken);
    console.log("loggedInUserId : ", loggedInUserId);

    // userId 또는 kakaoAccessToken이 있으면 로그인 상태
    const isLoggedIn = computed(() => {
      return !!email || !!kakaoAccessToken;
    });

    const isKakaoLogin = computed(()=>{
      return  !!kakaoAccessToken;
    })
    const isNormalUser = computed(() => {
      const email = localStorage.getItem("email");
      return !!email && !kakaoAccessToken;
    });
    const navigateTo = (route) => {
      router.push(route);
    };

    const logout = () => {
      // ✅ `localStorage`에서 로그인 정보 삭제
      localStorage.removeItem("userId");
      localStorage.removeItem("jwtToken");
      localStorage.removeItem("email");
      localStorage.removeItem("password");
      localStorage.removeItem("kakaoAccessToken");
      localStorage.removeItem("nickname");

      // ✅ 홈으로 이동 후 새로고침
      router.push("/").then(() => {
        location.reload();
      });
    };

    console.log('isKakaoLogin : ', isKakaoLogin.value);
    console.log('isNormalUser : ', isKakaoLogin.value);
    console.log("Saved kakaoToken:", localStorage.getItem("kakaoToken"));
    console.log("Saved jwtToken:", localStorage.getItem("jwtToken"));


    return {
      isLoggedIn,
      isKakaoLogin,
      isNormalUser,
      navigateTo,
      logout
    };
  }
};
</script>
<template>
  <div class="signup-button-wrap">
    <button class="signup-nav" v-if="!isLoggedIn" @click="navigateTo('/signup')">회원가입</button>
    <button class="nonuser-nav" v-if="isNormalUser" @click="navigateTo('/memberOut')">회원탈퇴</button>
  </div>
  <div class="nav-wrap">
    <nav>
      <div class="post-it-nav1">
        <button class="diary-nav1" @click="navigateTo('/')">메인페이지</button>
        <button class="diary-nav3" @click="navigateTo('/diary/tutorial')">튜토리얼</button>
        <button class="login-nav" v-if="!isLoggedIn" @click="navigateTo('/login')">로그인</button>
        <button class="login-nav" v-if="isLoggedIn" @click="logout">로그아웃</button>
      </div>
      <div class="post-it-nav2">
        <button class="diary-nav2" v-if="isLoggedIn" @click="navigateTo('/diary/write')">일기쓰기</button>
        <button class="diary-nav4" v-if="isLoggedIn" @click="navigateTo('/diary/common')">일기장보기</button>
      </div>
    </nav>
  </div>
</template>
<style>
</style>