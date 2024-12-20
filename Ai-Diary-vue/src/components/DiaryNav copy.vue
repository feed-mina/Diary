<template>
     <div class="nav-wrap">
      <!--<div v-if="isLoggedIn"></div> <div v-else></div>-->
      <nav>
      <div class="post-it-nav1">
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/about">About</RouterLink>
      <!--  <RouterLink to="/notFound">잘못된 링크</RouterLink>-->
        <RouterLink to="/componentA">componentA</RouterLink>
        <RouterLink to="/componentB">componentB</RouterLink>
        <RouterLink to="/componentC">componentC</RouterLink>
        <RouterLink to="/tutorial">tutorial</RouterLink>
        <RouterLink to="/signup">signup</RouterLink>
      </div>
      <div class="post-it-nav1">
        <RouterLink to="/signin">signin</RouterLink>
        <RouterLink to="/kakaoLogin">kakaoLogin</RouterLink>
        <RouterLink to="/loginNew">loginNew</RouterLink>
      </div>
        <RouterLink to="/diaryView">diaryView</RouterLink>
  
        <RouterLink to="/diaryWriting">diaryWriting</RouterLink> 
      </nav>
  </div>
</template>
  
  <script>
export default {
    name: "DiaryNavTemp",
  };
  </script>
  
  <style scoped>
.nav-wrap{
  width: 20vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  font-size: 23px;
  font-weight: bold;
  color: white;
  z-index: 9995;
}

.post-it-nav1{
  margin-top: 20%;
  margin-left: -10px;
  height: 100%;
  width: 50%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  padding-left: 0px;
  padding-left: 0px;
  letter-spacing: 1.42px;
  text-decoration: none !important;
}

.post-it-nav2{
  margin-top: 20%;
  margin-left: -10px;
  height: 100%;
  width: 50%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  padding-left: 0px;
  padding-right: 20px;
  align-items: center;
  letter-spacing: 1.42px;
  text-decoration: none !important;  
}

.diary-nav1{
  width: 100%;
  height: 20%;
  border-radius: 0px;
  background: linear-gradient(145deg, #89e73e, #73c234);
  box-shadow: 5px 5px 0px #7acd37, -5px -5px 0px #86e33d;

  margin-bottom: 0.9375em;
  color: white;
  transition: all 0.2% ease 0.5s;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  /* &:hover {
    background: #afe783;
    opacity: 1;
  } */
}


.diary-nav2{
  width: 110%;
  height: 20%;
  margin-bottom: 0.9375em;
  border-radius: 0px;
  background: linear-gradient(145deg, #fe676f, #d5565e);
  box-shadow: 5px 5px 0px #e15b63, -5px -5px 0px #f9656d;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  /* &:hover {
    background: #ffb1ae;
    opacity: 1;
  } */
}


.diary-nav3{
  width: 100%;
  height: 20%;
  margin-bottom: 0.9375em;
  border-radius: 0px;
  background: linear-gradient(145deg, #c841f7, #a837d0);
  box-shadow: 5px 5px 0px #b23adb, -5px -5px 0px #c440f3;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  /**&:hover {
    background: #cd83e7;
    opacity: 1;
  } */
}

.diary-nav4{
  width: 110%;
  height: 20%;
  margin-bottom: 0.9375em;
  border-radius: 0px;
  background: linear-gradient(145deg, #5897f7, #4a7fd0);
  box-shadow: 5px 5px 0px #4e86db, -5px -5px 0px #5694f3;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  /*&:hover {
    background: #83abe7;
    opacity: 1;
  }*/
}
.diary-handle-container{
  border: 1px solid #000000; 
  width: 190%;
  height: 60%;
  margin-top: 1em;
  
    width: 70%;
    height: 70%;
    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
    background: #774a20;
    border-radius: 0px 50px 50px 0px;
    box-shadow: rgb(0 0 0/50%) 0px 0px 18px 0px;
    /**&:hover {
      background: #492a0d;
      opacity: 1;
    } */
}
.login-nav{
  width: 100%;
  height: 10%;
  margin-bottom: 0.9375em;
  border-radius: 0px;
  background: #7E8C79;
  box-shadow: 5px 5px 0px #172C08;

  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  border: none;
  /**&:hover {
    background: #172C08;
    opacity: 1;
  } */
}
.kakaoLogin-nav{
  border-radius: 0px;
  background: #FFCC49;
  box-shadow: 5px 5px 0px #B5AD68;
  width: 100%;
  height: 10%;
  margin-bottom: 0.9375em;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  margin-left: 0%;
  padding-left: 0%;
  /**&:hover {
    background: #B5AD68;
    opacity: 1;
  } */
}


.signup-nav{
  width: 100%;
  height: 10%;
  border-radius: 0px;
  background: #A5778F;
  box-shadow: 5px 5px 0px #4F2116;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  border: none;
  /**&:hover {
    background: #4F2116;
    opacity: 1;
  } */
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
   