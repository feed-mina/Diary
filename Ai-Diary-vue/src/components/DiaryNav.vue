
<script>
// import.meta.env.VITE_KAKAO_OAUTH_URI
import {ref, onMounted, computed} from "vue";
import {useRouter} from "vue-router";
import Cookies from "universal-cookie";
// import axios from "axios";
export default {
name: "DiaryNav",
setup(){
  
  const router = useRouter();
    const cookies = new Cookies(); 

    // isLoggedIn을 computed로 구현
    const isLoggedIn = computed(() => !!cookies.get("userData"));
    console.log('isLoggedIn',isLoggedIn);
    console.log('cookies.get("userData")', cookies.get("userData"));
    const navigateTo = (route) => {
      router.push(route);
    };
    const logout = () => {
      cookies.remove("userData");
      router.push("/");
    };

    return{
      isLoggedIn,
      navigateTo, 
      logout,
    };
}
 };
</script>

<template>
     <div class="nav-wrap">
      
      <div class="signup-button-wrap">
          <button class="signup-nav" v-if="!isLoggedIn" @click="navigateTo('/signup')">회원가입</button>
       </div> 
      <nav>
        <div class="post-it-nav1">
          <button class="diary-nav1" @click="navigateTo('/')">Home</button>
          <button class="diary-nav3" @click="navigateTo('/diary/tutorial')">튜토리얼</button>
          <button class="login-nav" v-if="!isLoggedIn" @click="navigateTo('/login')">로그인</button>

          <button class="login-nav" v-if="isLoggedIn" @click="navigateTo('/logout')">로그아웃</button>

        </div>
        <div class="post-it-nav2">
          <button class="diary-nav2" v-if="isLoggedIn" @click="navigateTo('/diary/write')">일기쓰기</button>
          <button class="diary-nav4" v-if="isLoggedIn" @click="navigateTo('/diary/common')">일기장보기</button>
        </div>
    </nav>
  </div>
</template>


<style> 
.nav-wrap{
  width: 30vw; /* 네비게이션 너비 */
  height: 100%; /* 부모 컨테이너의 높이를 100% 채움 */
  background-color: #f4f4f4;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  justify-content: flex-start; /* 내용 상단 정렬 */
  padding: 1rem;
  z-index: 10;
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
