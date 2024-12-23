<script>
import {ref, onMounted} from "vue";
import {useRouter} from "vue-router";
import Cookies from "universal-cookie";
export default {
  name: 'HomePage',
  setup(){

    const router = useRouter();
    const cookies = new Cookies(); // 쿠키 객체 생성
    // 쿠키 읽기(디버깅 용도)
    onMounted(()=>{
      console.log('Home cookiess.getAll', cookies.getAll());
    });

    const navigateTo = (path) =>{
      router.push(path);
    };

    return{
      navigateTo,
    }
  }
};
</script>

<template>
  <div class="home">
    <div class="main-content">
      <!--Bubble Section-->
      <div id="HomeBubble" class="home-bubble">
        <div id="HomeText" class="home-text">
          <span>
            <strong>
              ai이미지 생성 기능을 이용한
            </strong>
            <br/><br/>
            감정다이어리
          </span>
            <button type="button" class="home-button" @click="navigateTo('/diary/tutorial')">
              튜토리얼 보러가기
            </button>
 
            <button type="button" class="home-button" @click="navigateTo('/login')">
              로그인 하러가기
            </button>
        </div>
      </div>
    </div>
  </div>

</template>



<style scoped>
.home{
  display: flex;
  justify-content: center;
  height: 100vh; /* 화면 전체 높이를 채움 */
  background-color: #f8f9fa; /* 배경색 */
  padding: 10px;
}
.main-content {
  display: flex;
  flex-direction: column; /* 수직 정렬 */
  max-width: 500px; /* 최대 너비 */
  align-items: center; /* 수평 중앙 정렬 */
 /*  justify-content: center; 세로 중앙 정렬 */
  width: 100%; /* 모바일에서는 전체 너비 사용 */
  padding: 1rem;
}

/* Bubble styling */
.home-bubble {
  width: 90%; /* 화면 크기 따라 자동 조정 */
  max-width: 500px; /* 최대 너비 설정 */
  display: flex;
  flex-direction: column; /* 수직 정렬 */
  justify-content: center;
  background: #e0f7fa;
  border-radius: 10px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.home-text {
  display: flex;
  flex-direction: column; /* 수직 정렬 */
  justify-content: center;
  font-size:1.5rem;
}

.home-text span {
  font-size: 3vmin;
}

.home-button {
  margin:10px;
  padding:10px 20px;
  background:#00796b;
  color:white;
  border-radius: 20px;
  box-sizing: border-box;
}

/* Button hover effects */
.home-button:hover {
  outline: 2px solid rgb(250, 250, 250);
  border: 2px solid rgb(255, 46, 46);
  background-color: rgb(249, 233, 233);
  color:black;
  border-radius: 20px;
  box-sizing: border-box;
  transition: 0.3s;
}

/* Jingu section styling */
.home-jingu {
  position: absolute;
  bottom: 10vmin;
  right: 10vmin;
  width: 70%;
  height: 70%;
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
}
</style>
