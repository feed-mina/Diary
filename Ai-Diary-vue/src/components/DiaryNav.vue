
<script>
// import.meta.env.VITE_KAKAO_OAUTH_URI
import {ref, onMounted, computed, watch} from "vue";
import {useRouter} from "vue-router";
import Cookies from "universal-cookie";
// import axios from "axios";
export default {
name: "DiaryNav",
setup(){
  
  const router = useRouter();
  const cookies = new Cookies(); 

  // JWT 쿠키 존재 여부로 로그인 상태 확인
  const isLoggedIn = ref(!!cookies.get("jwt"));

  // 로컬스토리지에 로그인할때 저장한 값
  const LoginStorage = localStorage.getItem("userId");
  console.log('LoginStorage: ',LoginStorage);
// 쿠키 변경 시 로그인 상태 업데이트
  const updateLoginStatus =  () => {
    isLoggedIn.value = !!cookies.get("jwt");
  };

    onMounted(()=>{
      console.log('Home cookiess.getAll', cookies.getAll());
      updateLoginStatus(); // 초기 상태 설정
    });

    // isLoggedIn을 computed로 구현
  //  const isLoggedIn = computed(() => !!cookies.get("jwt"));
    console.log('isLoggedIn',isLoggedIn);
    console.log('cookies.get("jwt")', cookies.get("jwt"));
    const navigateTo = (route) => {
      router.push(route);
    };
    const login = ()=>{
      updateLoginStatus();
    }
    const logout = () => {
      // 로컬스토로지 userId 삭제
      localStorage.removeItem('userId')
      // JWT 쿠키 삭제
      cookies.remove("jwt",{path : "/"});
      // 로그인 상태 업데이트 
      updateLoginStatus();
        // 홈으로 이동 후 새로고침
      router.push("/").then(() => {
        location.reload(); // 새로고침
      });

    };

    // JWT 쿠키 상태를 실시간으로 감지
    watch(
      () => cookies.get("jwt"), 
      ()=>{
      updateLoginStatus();
    });

    return{
      LoginStorage,
      isLoggedIn,
      navigateTo, 
      logout,
      login
    };
}
 };
</script>

<template>
     <div class="nav-wrap">
      
      <div class="signup-button-wrap">
          <button class="signup-nav" v-if="!LoginStorage" @click="navigateTo('/signup')">회원가입</button>
          <button class="nonuser-nav" v-if="LoginStorage" @click="navigateTo('/memberOut')">회원탈퇴</button>
       </div> 
      <nav>
        <div class="post-it-nav1">
          <button class="diary-nav1" @click="navigateTo('/')">메인페이지</button>
          <button class="diary-nav3" @click="navigateTo('/diary/tutorial')">튜토리얼</button>
          <button class="login-nav" v-if="!LoginStorage" @click="navigateTo('/login')">로그인</button>

          <button class="login-nav" v-if="LoginStorage" @click="logout">로그아웃</button>

        </div>
        <div class="post-it-nav2">
          <button class="diary-nav2" v-if="LoginStorage" @click="navigateTo('/diary/write')">일기쓰기</button>
          <button class="diary-nav4" v-if="LoginStorage" @click="navigateTo('/diary/common')">일기장보기</button>
        </div>
    </nav>
  </div>
</template>


<style> 
.nav-wrap{
  /*width: 30vw;  네비게이션 너비 */
  flex-shrink: 0; /* 크기 축소 방지 */
  height: 100%; /* 부모 컨테이너의 높이를 100% 채움 */
  background-color: beige;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  justify-content: flex-start; /* 내용 상단 정렬 */
  padding: 2rem;
}
.signup-button-wrap{
  position: absolute;
  top: 10px;
  right : 10px;

}
.signup-nav{
  width: 120px;
  height: 40px;
  font-size: 1rem;
  font-weight: bold;
  color: #fff;
  background: #A5778F;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s;
}

.nonuser-nav{
  width: 120px;
  height: 40px;
  font-size: 1rem;
  font-weight: bold;
  background: #604e2e;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s;
}

.post-it-nav1,
.post-it-nav2 {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem; /* 버튼 간격 */
}

.post-it-nav2{
  margin-top: 1rem;
}
.diary-nav1, .diary-nav2, .diary-nav3, .diary-nav4, .login-nav, .kakaoLogin-nav{
  width: 200px;
  height: 60px;
  font-size: 1.1rem;
  font-weight: bold;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  justify-content: center;
  align-items: center;
}


.diary-nav1:hover,
.diary-nav2:hover,
.diary-nav3:hover,
.diary-nav4:hover,
.login-nav:hover,
.signup-nav:hover {
  transform: scale(1.05);
}

.diary-nav1{
  background: linear-gradient(145deg, #89e73e, #73c234);
  box-shadow: 5px 5px 0px #7acd37, -5px -5px 0px #86e33d;
}

.diary-nav2{
  background: linear-gradient(145deg, #fe676f, #d5565e);
}


.diary-nav3{
  background: linear-gradient(145deg, #c841f7, #a837d0);
  box-shadow: 5px 5px 0px #b23adb, -5px -5px 0px #c440f3;
}
 .diary-nav4{
  background: linear-gradient(145deg, #5897f7, #4a7fd0);
  box-shadow: 5px 5px 0px #4e86db, -5px -5px 0px #5694f3;
}
 .diary-handle-container{
  background: linear-gradient(145deg, #774a20, #492a0d);
  box-shadow: 5px 5px 0px #3a2109, -5px -5px 0px #3a2109;
}

.login-nav{
  background: #7E8C79;
  box-shadow: 5px 5px 0px #172C08;
}

.kakaoLogin-nav{
  background: #FFCC49;
  box-shadow: 5px 5px 0px #B5AD68;
}

  .navbar {
    background-color: #444;
    padding: 10px 20px;
  }
  .navbar ul {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    justify-content: space-around;
  }
  .navbar li a {
    color: white;
    text-decoration: none;
    font-weight: bold;
  }
  .navbar li a:hover {
    text-decoration: underline;
  }



  .nav {
    background-color: #333;
    padding: 10px;
  }
  .nav ul {
    list-style: none;
    display: flex;
    justify-content: center;
    margin: 0;
    padding: 0;
  }
  .nav li {
    margin: 0 15px;
  }
  .nav a {
    color: white;
    text-decoration: none;
    font-size: 16px;
  }
  .nav a:hover {
    text-decoration: underline;
  }

  </style>