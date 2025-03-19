<script setup>
   import { ref, computed, inject } from 'vue';
   import axios from "axios";
   import { apiUrl } from "@/api/index.js";

   import { Notyf } from 'notyf';
   import 'notyf/notyf.min.css';
   import alarmSound from "../assets/alarm.wav";
   import toySound from "../assets/toy.mp3";
   import birdSound from "../assets/bird.mp3";
   
   // 알림, 소리 설정
   const notyf = new Notyf();
   const Pomoalarm = new Audio(alarmSound);
   const Toyalarm = new Audio(toySound);
   const birdalarm = new Audio(birdSound);
   const isRunning = ref(false);
   const kakaoToken = localStorage.getItem('kakaoAccessToken');
   const stopwatchSeconds = inject('stopwatchSeconds', ref(0));
   const running = ref(false);
   let interval = null;

   function playAlarm() {
  Pomoalarm.play();
  setTimeout(() => {
    Pomoalarm.pause();
    Pomoalarm.currentTime = 0; // 처음으로 되돌리기
  }, 2000); // 2초만 재생
}

function playAlarm2() {
  Toyalarm.play();
  setTimeout(() => {
    Toyalarm.pause();
    Toyalarm.currentTime = 0;
  }, 2000);
}

function playAlarm3() {
  birdalarm.play();
  setTimeout(() => {
    birdalarm.pause();
    birdalarm.currentTime = 0;
  }, 2000);
}
   // 시간 표시
   const formattedTime = computed(() => {
     const totalTime = stopwatchSeconds.value;
     const hours = Math.floor(totalTime / 3600);
     const minutes = Math.floor((totalTime % 3600) / 60);
     const seconds = totalTime % 60;
   
       // return `${hours < 10 ? '0' : ''}${hours}:` +
     //        `${minutes < 10 ? '0' : ''}${minutes}:` +
     //        `${seconds < 10 ? '0' : ''}${seconds}`;
     return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
   });
   
   // 스탑워치 시작
   function startTimer() {
     running.value = true;
     if (!interval) {
       interval = setInterval(() => {
         stopwatchSeconds.value++;
       }, 1000);
       setTimeout(() => {
        playAlarm3();
       }, 1000);
     }
   }
   
   // 스탑워치 정지
   function stopTimer() {
     running.value = false;
   
     // 바로 알림(2초 후 자동 닫힘)
     setTimeout(() => notyf.dismissAll(), 1000);
   
     clearInterval(interval);
     interval = null;
     notyf.success("스탑워치를 정지했어요!");
     setTimeout(() => {
      playAlarm2();
     }, 1000);
   }
   // 초기화
   function resetTimer() {
    stopwatchSeconds.value = 0;
         clearInterval(interval);
         console.log("초기화!");
        notyf.success("스탑워치를 초기화 했어요!");
        setTimeout(() => {
      playAlarm2();
     }, 1000);
   }
   // 기록 전송
   async function sendStopwatchTimeRecord() {
   // 기록 버튼 누를때마다 최신토큰 가져오기
     const kakaoToken = localStorage.getItem('kakaoAccessToken'); 
   
     if (!stopwatchSeconds.value) {
       notyf.error("보낼 기록이 없어요!");
       setTimeout(() => notyf.dismissAll(), 1000);
       return;
     }
     if (!kakaoToken) {
       notyf.error("로그인을 먼저 해주세요!");
    //     localStorage.clear();
       return;
     }
     try {
       const response = await axios.post(`${apiUrl}/api/kakao/sendRecord`,
         { stopwatchTime: stopwatchSeconds.value },
         {
           headers: {
             'Authorization': 'Bearer ' + kakaoToken,
             'Content-Type': 'application/json',
           },
         }
       );
       console.log("전송 성공!", response.data);
       notyf.success("카카오톡으로 기록을 보냈어요!");
     } catch (error) {
       console.error("전송 실패!", error);
       handleSendError(error);
     }
   
     function handleSendError(error){
       if (error.response) {
           if (error.response.status === 401) {
             const loginUrl = error.response.data;
             notyf.error("카카오 로그인이 필요해요! 로그인 페이지로 이동할게요.");
             window.location.href = loginUrl;
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
   }
</script>
<template>
   <div class="stopwatch">
      <h2>🕒 스탑워치</h2>
      <div v-if="stopwatchSeconds> 0">
         <h2 class="formattedTime">{{ formattedTime }}</h2>
      </div>
      <div v-else>
         <h2 class="formattedTime">00:00:00</h2>
      </div>
      <button class="stopwatchButton" @click="startTimer" v-if="!running">시작</button>
      <button class="stopwatchButton" @click="stopTimer" v-if="running">정지</button>
      <button class="stopwatchButton" @click="resetTimer" v-if="stopwatchSeconds>  0">초기화</button>
   </div>
</template>