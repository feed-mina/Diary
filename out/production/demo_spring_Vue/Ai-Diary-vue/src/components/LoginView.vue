<script setup>
import { useRouter } from 'vue-router';
import { onMounted } from 'vue';
import { Notyf } from "notyf";
import "notyf/notyf.min.css";
import axios from "axios";
// import { apiUrl } from "@/api/index.js";
import Swal from "sweetalert2";
const notyf = new Notyf();
const router = useRouter();

const isElectron = !!(window && window.process && window.process.type);

onMounted(() => {
  document.title = "뽀모도로 로그인";
  if (!window.Kakao || !window.Kakao.isInitialized()) {
    const kakaoScript = document.createElement("script");
    kakaoScript.src = "https://developers.kakao.com/sdk/js/kakao.js";
    kakaoScript.onload = () => {
      window.Kakao.init(import.meta.env.VITE_KAKAO_JS_KEY);
      console.log(" 카카오 SDK 로드 완료");
    };
    kakaoScript.onerror = () => {
      console.error("❌ 카카오 SDK 로드 안됨");
    };
    document.head.appendChild(kakaoScript);
  }
});


// 카카오 로그인 함수
async function kakaoLogin()  {
  if (window.Kakao && window.Kakao.Auth) {
    console.log("🌐 웹에서는 그냥 페이지 이동!");
    window.Kakao.Auth.login({
      scope: "profile_nickname, account_email, talk_message",
      success: async function (authObj) {
        try {
          const kakaoAccessToken = authObj.access_token;
          const response = await axios.post(`/api/kakao/login`, {
            accessToken: kakaoAccessToken,
          });

          const jwtToken = response.data;
          // console.log("email: ", response.data.kakaoUserInfo.email);
          // console.log("nickname: ", response.data.kakaoUserInfo.nickname);
          // console.log("jwtToken: ", response.data.jwtToken);
          console.log("response: ", response);

          // const kakao_email = response.data.kakaoUserInfo.email;
          // const kakao_nickname = response.data.kakaoUserInfo.nickname;
          // const kakao_token = response.data.jwtToken;
          // const [userId] = response.data.kakaoUserInfo.email.split("@");
          // console.log("userId:", userId);

          // 🟢 받은 JWT를 저장
          localStorage.setItem("jwtToken", jwtToken);
          localStorage.setItem("kakaoAccessToken", kakaoAccessToken);
          // localStorage.setItem("userId", userId);
          // localStorage.setItem("email", kakao_email);
          Swal.fire("카카오 로그인 성공", "로그인을 완료했습니다", "success");

          window.location.href = "/#/pomoMain";
        } catch (error) {
          Swal.fire("로그인 실패", error.response?.data?.message || "카카오 로그인 실패", "error");
          console.error("❌ 카카오 로그인 실패", error);
        }
      },
      fail: function (err) {
        Swal.fire("로그인 실패", "카카오 로그인 실패", "error");
        console.error("❌ 로그인 실패", err);
      },
    });
  } else {
    Swal.fire("로그인 실패", "카카오 로그인 실패", "error");
    console.error("카카오 SDK가 아직 로드되지 않았어요!");
  }
};


</script>
<template>
  <div class="login-container">
    <button class="kakao-button" @click="kakaoLogin">
      <img alt="kakaoLogin" class="kakaoLogin_pomo" src="../assets/kakao_login_bt.png"/>
    </button>
    <p class="login-guide">로그인 후 시작해볼까요? 😊</p>
  </div>
</template>