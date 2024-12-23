<script>
import {ref, onMounted} from "vue";
import {useRouter} from "vue-router";
import Cookies from "universal-cookie";
import axios from "axios";

export default {
  name: 'LoginPage', 
  setup(){
    const router = useRouter();
    const cookies = new Cookies(); 

    const isLoginPg = ref(true);   

    // 로그인 입력 데이터
    const loginData = ref({
      userId:"",
      password : "", 
    });
    const errorWarning = ref({
      userId:  false,
      password:  false,
    });
    const idErrorMessage = ref("");
    const passwordErrorMessage = ref("");
    
    // const idValid = ref(true);
    // const passwordValid = ref(true);
    const showPassword = ref(false);
    const togglePasswordVisibility = () =>{
      showPassword.value = !showPassword.value;
    }
    // 로그인 상태 확인 후 리다이렉트
    onMounted(() => {
      const userData = cookies.get("userData");
      if(!userData){
        console.log("로그인 필요");
        loginData.value = {
          userId : "",
          password : "",
        };
        router.push("/login");
      }  else{
        router.push("/diary/common");
      }
    });

    const handleLoginData = (event) =>{
      loginData.value[event.target.name] = event.target.value;
    };
 

    const onClickLoginButton = async() => {
      console.log("로그인 데이터 :", loginData.value);
      if(!loginData.value.userId){
        alert("아이디를 입력해주세요.");
        return;
      }

      if(!loginData.value.password){
        alert("비밀번호를 입력해주세요.");
        return;
      }


      try{
        const response = await sendLoginData();
        cookies.set("userData",response.data, { path: "/" });
        alert("로그인을 완료했습니다.");
        router.push("/diary/home");
      } catch(error){
        console.error(error);
        errorWarning.value = error.response?.data?.fail || "로그인에 실패했습니다.";

      }
    };


    // 로그인 API 호출
    const sendLoginData = async() => {
      return await axios.post("http://localhost:8080/api/auth/login", loginData.value)
      .then((response)=>{
        this.user = response.data;
      })
      .catch((error)=>{
        console.log("API 호출 실패",error);
      });
      
    };

    

    return{
      isLoginPg,
      loginData, 
      errorWarning,
      showPassword,
      handleLoginData,
      idErrorMessage,
      passwordErrorMessage,
      togglePasswordVisibility,
      onClickLoginButton
    };

   },
};
</script>

<template>
<div class="loginPage">
  <div id="login_form" class="login_form">
      <!--로그인 또는 회원가입 폼 렌더링-->
          <!--로그인 폼-->
          <form @submit.prevent="onClickLoginButton">
   
          <!--ID-->
          <div class="login-session">
            <div class="login-label">
              <label for="id" class="form-label">ID</label>
            </div>
            <div>
              <input size="30" type="text"  v-model="loginData.userId" @input="handleIdChange" class="login_form-input" name="userId" id="userId"/>
              <div class="login_form-oo" :style="{ color: errorWarning.userId ? 'red' : 'black' }">
                {{ idErrorMessage }}
              </div>
            </div>
          </div>
          
          
        <!--패스워드-->
        <div class="login-session">
          <div class="login-label">
            <label  for="password" class="form-label">Password</label>
          </div>
          <div>
            <input size="30" :type="showPassword ? 'text' : 'password'" v-model="loginData.password" @input="handlePasswordChange" class="login_form-input" name="password"  id="password"/>
            <button type="button" @click="togglePasswordVisibility">
              {{  showPassword ? '숨기기' : '보기' }}
            </button>
          <div class="login_form-oo" v-if="errorWarning.password" :style="{ color: errorWarning.password ? 'red' : 'black' }"> 
            {{ passwordErrorMessage }}
        </div>
      </div>
      </div>

      <!-- 로그인 버튼 -->
      <div>
        <button type="submit" class="login_form_button">로그인</button>
      </div>
    </form>
   </div>
</div>
</template>

<style scoped> 


.loginPage {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50%; /* 화면 전체 높이를 채움 */
  background-color: #f8f9fa; /* 배경색 */
}
.login_form{
  width: 90%; /* 화면 크기 따라 자동 조정 */
  max-width: 500px; /* 최대 너비 설정 */
  padding: 2rem;
  background-color: #f9f9f9;
  border-radius: 10px;
  margin: auto;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);

}

.login-session{
  display: flex;
  flex-direction: column; /* 수직 정렬 */
  margin-bottom: 1.5rem; /* 필드 간 여백 */
}
.login-label{
  font-size: 1rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
} 

.login_form-input{
  width: 100%; /* 필드 너비가 컨테이너에 맞춰짐 */
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 0.5rem;
  box-sizing: border-box;
}

.login_form-input:focus{
  border: 1px solid #4a90e2;
  outline: none;

}

.login_form_button{
  width: 100%; /* 버튼이 컨테이너에 맞춰짐 */
  background-color: #4a90e2;
  color: white;
  border: none;
  padding: 0.8rem 1.5rem;
  border-radius: 5px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.login_form_button:hover{
  background-color: #357abd;

}
.login_form-oo{
  font-size: 0.9rem;
  color: red;
  margin-top: 0.5rem;
}
 
</style>