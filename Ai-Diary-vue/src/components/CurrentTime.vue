<script setup>
   import { ref, onMounted } from 'vue';
   import axios from 'axios';
       

  // 서버 주소 설정
// const apiUrl = process.env.NODE_ENV === 'development'
//   ? 'http://localhost:8081'
//   : 'http://justsaying.co.kr';

       const currentTime = ref('');
       const isTimeVisible = ref(false);  
   
       function updateTime(){
         if (import.meta.env.VITE_APP_USE_API === 'true') {
     axios.get(`${apiUrl}/api/timer/now`)
       .then(response => {
         console.log("서버 시간:", response.data);
         currentTime.value = response.data;
         isTimeVisible.value = true;  
        // this.nowTime = response.data;
       })
       .catch(error => {
         console.error('에러:', error);
         isTimeVisible.value = false; 
       //   this.nowTime = new Date().toLocaleTimeString(); 서버 안되면 현재 시간
       });
   } else {
     currentTime.value  = new Date().toLocaleTimeString();
    //  this.nowTime = new Date().toLocaleTimeString();
   }
    
   }
   
     
       onMounted(() => {
           updateTime(); // 처음 화면에 들어왔을 때 시간 가져오기
           setInterval(updateTime, 1000);  // 1초마다 갱신
       });
</script>
<template>
   <div class="currentTime">
      <h1 class="currentTime" v-if="isTimeVisible">현재 시간</h1>
      <p class="currentTimetext">{{ currentTime }}</p>
   </div>
</template>