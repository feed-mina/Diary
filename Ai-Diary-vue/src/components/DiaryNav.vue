<template>
  <div class="nav-wrap">
    <div class="signup-button-wrap">
      <button class="signup-nav" v-if="!isLoggedIn" @click="navigateTo('/signup')">회원가입</button>
      <button class="nonuser-nav" v-if="isLoggedIn" @click="navigateTo('/memberOut')">회원탈퇴</button>
    </div>

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

<script>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";

export default {
  name: "DiaryNav",
  setup() {
    const router = useRouter();
     const loginEmail = localStorage.getItem('email');
     const loginPassword = localStorage.getItem('password')
     const loginToken = localStorage.getItem('jwtToken'); // 저장된 토큰 가져오기
     const loggedInUserId = localStorage.getItem('userId');

console.log("loginPassword : ", loginPassword);
console.log("loginToken : ", loginToken);
console.log("loginEmail : ", loginEmail);
console.log("loggedInUserId : ", loggedInUserId);

    // ✅ `localStorage`에서 로그인 상태 확인 (JWT 없이)

    // ✅ `localStorage`에서 로그인 상태 확인 (JWT 없이)
    const isLoggedIn = computed(() => {
      const userId = localStorage.getItem("email");
      const kakaoAccessToken = localStorage.getItem("kakaoAccessToken");

      // userId 또는 kakaoAccessToken이 있으면 로그인 상태
      return !!userId || !!kakaoAccessToken;
    });

    const navigateTo = (route) => {
      router.push(route);
    };

    const logout = () => {
      // ✅ `localStorage`에서 로그인 정보 삭제
      localStorage.removeItem("userId");
      localStorage.removeItem("jwtToken");
   
      localStorage.removeItem("kakaoAccessToken");

      // ✅ 홈으로 이동 후 새로고침
      router.push("/").then(() => {
        location.reload();
      });
    };

    return {
      isLoggedIn,
      navigateTo,
      logout
    };
  }
};
</script>

<style>
.nav-wrap {
  flex-shrink: 0;
  height: 100%;
  background-color: beige;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  padding: 2rem;
}

.signup-button-wrap {
  position: absolute;
  top: 10px;
  right: 10px;
}

.signup-nav, .nonuser-nav, .diary-nav1, .diary-nav2, .diary-nav3, .diary-nav4, .login-nav {
  width: 120px;
  height: 40px;
  font-size: 1rem;
  font-weight: bold;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s;
}

.signup-nav { background: #A5778F; }
.nonuser-nav { background: #604e2e; }
.diary-nav1 { background: linear-gradient(145deg, #89e73e, #73c234); }
.diary-nav2 { background: linear-gradient(145deg, #fe676f, #d5565e); }
.diary-nav3 { background: linear-gradient(145deg, #c841f7, #a837d0); }
.diary-nav4 { background: linear-gradient(145deg, #5897f7, #4a7fd0); }
.login-nav { background: #7E8C79; }

button:hover {
  transform: scale(1.05);
}

.post-it-nav1,
.post-it-nav2 {
  margin-top: 1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem; /* 버튼 간격 */
}
</style>
