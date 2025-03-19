<script setup>
import { useRouter } from 'vue-router';
import { onMounted } from 'vue';
import { Notyf } from "notyf";
import "notyf/notyf.min.css";
const notyf = new Notyf();
const router = useRouter();

const isElectron = !!(window && window.process && window.process.type);

onMounted(() => {
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

function kakaoLogin() {
  //  if (isElectron && ipcRenderer) {
  //    ipcRenderer.send('login-success');


  //    if (window.Kakao && window.Kakao.Auth) {
  //      window.Kakao.Auth.login({
  //        scope: "talk_message",
  //        success: function(authObj) {
  //          console.log('로그인 성공!', authObj);
  //          notyf.success("로그인 성공!");
  //          // 토큰 저장
  //          localStorage.setItem('kakaoAccessToken', authObj.access_token);
  //          window.location.href = '/main';
  //          // emit('loginSuccess');
  //        },
  //        fail: function(err) {
  //          console.error('❌ 로그인 실패', err);
  //          notyf.error("❌ 로그인 실패");
  //        }
  //      });
  //    } else {
  //     notyf.error("❌ 로그인 실패");
  //      console.error('카카오 SDK가 아직 로드되지 않았어요!');
  //    }

  //  } else {

  console.log('🌐 웹에서는 그냥 페이지 이동!');
  if (window.Kakao && window.Kakao.Auth) {
    window.Kakao.Auth.login({
      scope: "talk_message",
      success: function (authObj) {
        console.log('로그인 성공!', authObj);

        const token = authObj.access_token;
        console.log('로그인 성공!', token);
        notyf.success("로그인 성공!");

        // 토큰 저장
        localStorage.setItem('kakaoAccessToken', authObj.access_token);
        // emit('loginSuccess');

        // Vue Router를 이용한 페이지 이동
        // router.push('/pomoMain');
        //  window.location.href = '/project2/main';
        window.location.href = '/#/pomoMain';
        // 토큰을 URL에 같이 넣어서 페이지 이동
        //window.location.href = `/main?kakaoAccessToken=${token}`;

      },
      fail: function (err) {
        notyf.error("❌ 로그인 실패");
        console.error('❌ 로그인 실패', err);
      }
    });
  } else {
    notyf.error("❌ 로그인 실패");
    console.error('카카오 SDK가 아직 로드되지 않았어요!');
  }

}

</script>
<template>
  <div class="login-container">
    <button class="kakao-button" @click="kakaoLogin">
      <img alt="kakaoLogin" class="kakaoLogin_pomo" src="../assets/kakao_login_bt.png"/>
    </button>
    <p class="login-guide">로그인 후 시작해볼까요? 😊</p>
  </div>
</template>