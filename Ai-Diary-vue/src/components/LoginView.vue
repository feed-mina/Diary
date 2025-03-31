<script setup>
import { useRouter } from 'vue-router';
import { onMounted } from 'vue';
import { Notyf } from "notyf";
import "notyf/notyf.min.css";
import axios from "axios";
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
          console.log("@@@@ 카카오 로그인 응답 response: ", response);


          console.log("@@@@ 카카오 로그인 토큰 저장");
          // 🟢 받은 JWT를 저장

          if (jwtToken) {
            localStorage.setItem("jwtToken", jwtToken);
          } else {
            console.log("⚠️ 서버에서 JWT 토큰이 안 왔어!");
          }
          let kakao_email = response.data.kakaoUserInfo.email;
          let kakao_nickname = response.data.kakaoUserInfo.nickname;
          let kakao_userId = kakao_email.split("@");
          let kakao_token = response.data.jwtToken;
          let [userId] = response.data.kakaoUserInfo.email.split("@");
          console.log("userId:", userId);
          localStorage.setItem("kakaoAccessToken", kakaoAccessToken);
          localStorage.setItem("userId", userId);
          localStorage.setItem("email", kakao_email);
          localStorage.setItem("nickname", kakao_nickname);
          localStorage.setItem("jwtToken", kakao_token);
          Swal.fire("카카오 로그인 성공", "로그인을 완료했습니다", "success");

          router.push("/pomoMain").then(() => {
            location.reload();
          });
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
  <div class="login-container_pomo">
    <button class="kakao-button_pomo" @click="kakaoLogin">
      <img alt="kakaoLogin" class="kakaoLogin_pomo" src="../img/kakao_login_large_narrow.png"/>
    </button>
    <p class="login-guide_pomo">로그인 후 시작해볼까요? 😊</p>
  </div>
</template>