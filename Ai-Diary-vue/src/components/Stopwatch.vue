<script setup>
   import { ref, computed, inject } from 'vue';

   import { Notyf } from 'notyf';
   import 'notyf/notyf.min.css';

   // 알림, 소리 설정
   const notyf = new Notyf();
   const Pomoalarm = new Audio("/sound/alarm.wav");
   const Toyalarm = new Audio("/sound/toy.mp3");
   const birdalarm = new Audio("/sound/bird.mp3");

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