<script  setup>
   import { ref, onMounted, provide, inject } from "vue";
   import axios from "axios";
   import Stopwatch from "./Stopwatch.vue";
   import PomodoroTimer from "./PomodoroTimer.vue"; 
   import CurrentTime from "./CurrentTime.vue"; 
 
  // 알람 라이브러리 notyf 사용

   import {Notyf} from 'notyf';
   import 'notyf/notyf.min.css';

   // import { apiUrl } from "@/api/index.js";
   // 알람객체 생성
   const notyf = new Notyf();
   // 다크모드, 시간표시,카카오 로그인 이후로 뽀모도로&타이머 페이지 진입 가능 활성화 변수 생성 
   const isDarkMode = ref(false);
   const isTimeVisible = ref(false);
   const kakaoButtonEnabled = ref(false);


   // 스탑워치 및 뽀모도로 변수 생성, 0초부터 시작
   const stopwatchSeconds = ref(0);
   const pomoSession = ref(0);

   // Mainview <= stopwatch,PomodoroTimer 로 값 관리
   // `provide`로 하위 컴포넌트에 데이터 제공
   provide("stopwatchSeconds", stopwatchSeconds);
   provide("pomoSession", pomoSession);
   
  const params = new URLSearchParams(window.location.search);

   // 서버 시간 체크, 서버랑 연결되는지 확인용 및 현재 시간 받음
   function checkServerTime() {
      axios.get(`/api/timer/now`)
        .then(() => {
        // console.log("서버 시간:", response.data);
           isTimeVisible.value = true;  
// 서버 연결 성공하면 보임
       })
       .catch(error => {
         console.error(error);
         isTimeVisible.value = false;  // 서버 안되면 숨김
         // this.nowTime = new Date().toLocaleTimeString(); 
// 서버가 실행 안되면 현재 시간을 브라우저에서 받는다
       });
    }
   
   // 기록 전송
   async function sendAllRecords() {
   // 인증값=> 카카오로 나에게 보내기 기능에서 토큰 필요
  const kakaoAccessToken = localStorage.getItem('kakaoAccessToken');
     console.log("카카오톡으로 기록 보냅니다!", kakaoAccessToken);

     if (!kakaoAccessToken) {
       notyf.error("로그인을 먼저 해주세요!");
       return;
     }

     console.log("pomoSession 값:", pomoSession.value);
     console.log("stopwatchSeconds 값:", stopwatchSeconds.value);
// 뽀모도로나 스탑워치
     if (!stopwatchSeconds.value && !pomoSession.value) {
       notyf.error("보낼 기록이 없어요!");
       setTimeout(() => notyf.dismissAll(), 2000);
       // return;
     }

     const requestData = {};
     if (stopwatchSeconds.value > 0) {
       requestData.stopwatchTime = stopwatchSeconds.value;
     }
     if (pomoSession.value > 0) {
       requestData.pomodoroCount = pomoSession.value;
       requestData.pomodoroTotalTime = pomoSession.value * 25;
     }

     console.log("보내는 데이터 확인:", requestData);

     if (Object.keys(requestData).length === 0) {
       notyf.error("보낼 기록이 없어요!");
       return;
     }

     try {
       const response = await axios.post(
           `/api/kakao/sendRecord`,
           requestData,

       );

       console.log("전송 성공!", response.data);
       notyf.success("카카오톡으로 기록을 보냈어요!");
     } catch (error) {
       console.error("전송 실패!", error);
       // handleSendError(error);
     }
   }


   // 오류 처리 함수
  function handleSendError(error) {
    if (error.response) {
      if (error.response.status === 401) {
        const loginUrl = error.response.data;
        notyf.error("카카오 로그인이 필요해요! 로그인 페이지로 이동할게요.");
      } else if (error.response.status === 500) {
        notyf.error("서버 환경설정 오류가 있어요. 관리자에게 알려주세요!");
      } else {
        notyf.error("알 수 없는 오류가 발생했어요.");
      }
    } else {
      notyf.error("서버 연결에 문제가 있어요.");
    }
    setTimeout(() => notyf.dismissAll(), 2000);
  }
   // 다크 모드 토글
    const toggleDarkMode = () => {
      isDarkMode.value = !isDarkMode.value;
      document.documentElement.classList.toggle("dark", isDarkMode.value);
    };
   
    // 서버 헬스 체크
    async function checkServer() {
      try {
        const response = await fetch(`/api/timer/health`); // 헬스 체크용 엔드포인트
        if (response.ok) {
          kakaoButtonEnabled.value = true; // 버튼 활성화
        } else {
          kakaoButtonEnabled.value = false; // 버튼 비활성화
        }
      } catch {
        kakaoButtonEnabled.value = false; // 서버 연결 안되면 비활성화
      }
   }
    
   
   onMounted(() => {
     document.title = "뽀모도로";
     checkServer();
     checkServerTime();
  const  kakaoAccessToken = localStorage.getItem('kakaoAccessToken');
    console.log('카카오 토큰:', kakaoAccessToken);
     if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
       isDarkMode.value = true;
       document.documentElement.classList.add("dark");
     }
   });
   
   provide('isTimeVisible', isTimeVisible);
</script>
<template>
   <div class="mainView">
      <CurrentTime/>
      <h1>🕒 스탑워치 & 뽀모도로</h1>
      <!-- 스탑워치 -->
      <Stopwatch />
      <!-- 뽀모도로 -->
      <PomodoroTimer />
      <!-- 카카오톡 전송 버튼 -->
      <div>
         <button class="kakaosendbutton" @click="sendAllRecords" :disabled="!kakaoButtonEnabled">
         카카오톡으로 기록 보내기
         </button>
      </div>
      <!-- 다크모드 버튼 -->
      <button @click="toggleDarkMode" class="toggleMode" >
      {{ isDarkMode ? "🌞 라이트 모드" : "🌙 다크 모드" }}
      </button>
      <!--알림-->
   </div>
</template>