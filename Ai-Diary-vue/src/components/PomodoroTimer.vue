<script setup>
   import { ref, computed, inject } from "vue";
   import axios from "axios";
   import { Notyf } from "notyf";
   import "notyf/notyf.min.css";
   import { apiUrl } from "@/api/index.js";
   import alarmSound from "../assets/alarm.wav";
   import toySound from "../assets/toy.mp3";
   import birdSound from "../assets/bird.mp3";
   // 알림ui 라이브러리 설정
   const notyf = new Notyf();
   const pomoSession = inject('pomoSession', ref(0));
   // 뽀모도로 관련 상태 변수
   const isPomodoroRunning = ref(false); // 중복 방지용
   const pomodoroSeconds = ref(25 * 60); // 25분
   const kakaoToken = localStorage.getItem('kakaoPomoAccessToken');
   const isTimeVisible = inject('isTimeVisible');
   const Pomoalarm = new Audio(alarmSound);
   const Toyalarm = new Audio(toySound);
   const birdalarm = new Audio(birdSound);
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
       let interval = null;
       // 시간 포맷팅 (MM:SS)
       const formattedTime = computed(() => {
         const minutes = Math.floor(pomodoroSeconds.value / 60);
         const seconds = pomodoroSeconds.value % 60;
         return `${minutes}:${seconds < 10 ? "0" : ""}${seconds}`;
       });
   
       // 뽀모도로 시작
       function startPomodoro(){
         if (isPomodoroRunning.value) {
           notyf.error("이미 뽀모도로가 진행 중이에요!");
           return;
         }
         isPomodoroRunning.value = true;
         pomodoroSeconds.value = 25 * 60; // 혹시라도 재시작할 때 초기화
         if (!interval) {
           interval = setInterval(() => {
             if (pomodoroSeconds.value > 0) {
               pomodoroSeconds.value--;
             } else {
               stopPomodoro();
               pomoSession.value++; //  여기서 횟수 +1
               notyf.success("뽀모도로 완료! 횟수 +1");
             }
           }, 1000);
         }
         // playAlarm();
         playAlarm3();
       };
   
   
       // 뽀모도로 정지
       function stopPomodoro(){
         isPomodoroRunning.value = false; // 종료되면 다시 시작 가능하게
         clearInterval(interval);
         interval = null;
         notyf.success("뽀모도로를 정지했어요!");

        playAlarm2();
        //  Toyalarm.play();
       };
   
   
       // 초기화 (25분으로 리셋)
       function resetPomodoro () {
        // isPomodoroRunning.value = true;
        notyf.success("초기화 했습니다.");
         pomodoroSeconds.value = 25 * 60;
         console.log("초기화 종료!");
        //  pomodoroSeconds.value =  0;
        //  stopPomodoro();
         // playAlarm2();
       };
   
       // 5분 휴식 시작
       function startBreak(){
        //  stopPomodoro();
         isPomodoroRunning.value = true;
         pomodoroSeconds.value = 5 * 60; // 5분 휴식
         notyf.success("5분 휴식!");
         if (!interval) {
           interval = setInterval(() => {
             if (pomodoroSeconds.value > 0) {
               pomodoroSeconds.value--;
             } else {
                clearInterval(interval); // 타이머 정지
                interval = null; // 초기화
               console.log("5분 휴식 종료!");
               notyf.success("5분 휴식 종료!");
               playAlarm();
             }
           }, 1000);
           // playAlarm2();
         }
       };
    
   //   카카오톡으로 기록 보내기 함수
   async function sendPomodoroTimerRecord() {
     if (!pomoSession.value) {
       notyf.error("보낼 기록이 없어요!");
       setTimeout(() => notyf.dismissAll(), 1000);
      //  localStorage.clear();
       return;
     }
     if (!kakaoToken) {
       notyf.error("로그인을 먼저 해주세요!");
       return;
     }
     const totalMinutes = Math.floor((25 * 60 * pomoSession.value) / 60);
     try {
       const response = await axios.post(`${apiUrl}/api/kakao/sendRecord`,
         {
           pomodoroCount: pomoSession.value,
           pomodoroTotalTime: totalMinutes ,
         },
         {
           headers: {
             'Authorization': 'Bearer ' + kakaoToken, //  여기 중요!
             'Content-Type': 'application/json',
           },
         }
       );
       console.log("전송 성공!", response.data);
       notyf.success("카카오톡으로 기록을 보냈어요!");
     } catch (error) {
       console.error("전송 실패!", error);
      //  notyf.error("전송에 실패했어요!");
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
   <div class="pomodoro">
      <h2> 🍅 뽀모도로 모드</h2>
      <h1>뽀모도로 횟수: {{ pomoSession }}</h1>
      <h2 class="formattedTime">{{ formattedTime }}</h2>
      <button class="pomobutton" @click="startPomodoro" :disabled="isPomodoroRunning">25분 집중</button>
      <button class="pomobutton" @click="stopPomodoro">정지</button>
      <button class="pomobutton" @click="resetPomodoro">초기화</button>
      <button class="pomobutton" @click="startBreak" :disabled="isPomodoroRunning">5분 휴식</button>
      <!-- <div>
         <button class="kakaosendbutton" @click="sendPomodoroTimerRecord" v-if="isTimeVisible">카카오톡으로 기록 보내기</button>
      </div> -->
   </div>
</template>