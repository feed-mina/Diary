<script>
import { ref, onMounted } from 'vue'; 
import {useRouter} from "vue-router";
import axios from "axios";
import Cookies from "universal-cookie";

// import port from "@/data/port.json";

export default {
  name: 'SignupPage',
  setup(){
    const router = useRouter();
    // 쿠키 객체 생성
    const cookies = new Cookies();
    const focusEmailField = ref(null);

    const signUpData =  ref({
      email:{
        emailPrefix:"",
        emailDomain:"",
        customDomain:"",
      },
      userId:"",
      password:"",
      rePassword:"",
      username:"",
      phone:{
        first:"",
        middle:"",
        last:"",
      },
      message:"",
    });
const validateDate = ref({
  message:"",
  code:"",
})
    const errorState = ref({
      userId: false,
      password: false,
      rePassword: false,
      email: false,
      username: false,
      phone: false,
    });

    const errorMessage = ref({
      userId: "",
      password: "",
      rePassword: "",
      email: "",
      username: "",
      phone: "",
    });

    const visibility = ref({
      showPassword: false,
      showRePassword: false,
    });

    const toggleVisibility = (key) => {
      visibility.value[key] = !visibility.value[key];
    };



    const validateField = {
      
      email() {
        const fullEmail = signUpData.value.emailDomain === "custom"
          ? `${signUpData.value.emailPrefix}@${signUpData.value.customDomain}`
          : `${signUpData.value.emailPrefix}@${signUpData.value.emailDomain}`;
          console.log("Generated Email:", fullEmail); // 디버깅 로그 추가
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        errorState.value.email = !emailRegex.test(fullEmail);
        errorMessage.value.email = errorState.value.email ? "유효하지 않은 이메일 형식입니다." : "";

        signUpData.value.email = fullEmail ;
        console.log("signUpData.value.email:", signUpData.value.email); // 디버깅 로그 추가
      },
      userId(id) {
        const idRegex = /^[a-zA-Z0-9]{5,15}$/;
        errorState.value.userId = !idRegex.test(id);
        errorMessage.value.userId = errorState.value.userId ? "아이디는 5~15자의 영문 대소문자와 숫자만 허용됩니다." : "";
      },
      password(password) {
        const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*])[a-zA-Z\d!@#$%^&*]{8,}$/;
        errorState.value.password = !passwordRegex.test(password);
        errorMessage.value.password = errorState.value.password ? "비밀번호는 영어, 숫자, 특수문자를 포함하여 8자리 이상이어야 합니다." : "";
      },
      rePassword(rePassword) {
        errorState.value.rePassword = rePassword !== signUpData.value.password;
        errorMessage.value.rePassword = errorState.value.rePassword ? "비밀번호가 일치하지 않습니다." : "";
      },
      username(username) {
        const usernameRegex = /^[가-힣a-zA-Z\s]{2,20}$/;
        errorState.value.username = !usernameRegex.test(username);
        errorMessage.value.username = errorState.value.username ? "이름은 2~20자의 한글 또는 영문만 가능합니다." : "";
      },
      phone() {
        const { first, middle, last } = signUpData.value.phone;
        errorState.value.phone = !(first.length === 3 && middle.length === 4 && last.length === 4);
        errorMessage.value.phone = errorState.value.phone ? "휴대폰 번호를 정확히 입력해주세요." : "";
      },
    };

    const handleInput = (field, value) => {
      signUpData.value[field] = value;
        validateField[field]?.(value);

    };

    const isSignUpDataValid = () => {
      return Object.values(errorState.value).every((state) => !state);
    };

    const sendSignUpData = async () => {
      try {
        const { userId, email, password,  username, phone } = signUpData.value;
        const signUpDataToSave = {
          userId,
          email: `${email.emailPrefix}@${email.emailDomain === 'custom' ? email.customDomain : email.emailDomain}`,
          password,
          phone: `${phone.first}${phone.middle}${phone.last}`,
          username,
        };
 
        console.log("회원가입 데이터:", signUpDataToSave);

        const response = await axios.post("http://localhost:8080/api/auth/register", signUpDataToSave);

        alert("회원가입이 완료되었습니다!");

        router.push("/login").then(() => location.reload());
        return response.data;
      } catch (error) {
        console.error("API 호출 실패", error);
        if (error.response && error.response.status === 400) {
          alert("회원가입에 실패했습니다. 다시 시도해주세요.");
          alert(error.response.data); // 서버에서 보낸 메시지: "이미 존재하는 이메일입니다."
          focusEmailField.value.focus(); 
          errorState.value.email = true;
          errorMessage.value.email = error.response.data;
        } else {
          console.error("API 호출 실패", error);
          alert("회원가입에 실패했습니다. 다시 시도해주세요.");
        }
      }
    };


    // 컴포넌트가 로드될 때 오늘 날짜 자동 설정
    onMounted(() =>{
      const userData = cookies.get("userData");
      console.log('cookies.get("userData',cookies.get("userData"));
    if (!userData) {
      console.log("사용자가 로그인되어 있지 않음");
    // router.push("/"); // 필요하지 않다면 삭제
      } 
       });

  const onClickSignUpButton = async () => {
    if (!isSignUpDataValid()) {
        alert("빠진 정보 없이 입력해 주세요.");
        return;
      }
      await sendSignUpData();
      
      // 로컬스토로지 signUpDataToSave 삭제
      //localStorage.removeItem('signUpDataToSave')
      router.push("/login").then(() => {
        location.reload(); // 새로고침
      });  
    };

    const sendVerificationCode = async() =>{
      try{
        const response = await axios.post("http://localhost:8080/api/auth/signup", null, {
          params: { email: signUpData.email },
        });
        signUpData.message = response.data;

      }catch(error){
        console.error(error);
        this.message = "오류 발생!";

      }
    }

    return{
      signUpData,
      focusEmailField,
      errorState,
      errorMessage,
      visibility,
      toggleVisibility,
      handleInput,
      onClickSignUpButton,
      sendVerificationCode
    };
  },
};
</script>

<template>

<div class="signupPage">
  <div class="signUp_form">
    <!-- <form @submit.prevent="onClickSignUpButton"> -->
      <form @submit.prevent="sendVerificationCode">
    <!--ID-->
    <div class="signUp-session">
      <div class="signUp-label">
        <label for="id" class="form-label">ID</label>
      </div>
      <div>
        <input size="30" type="text"  v-model="signUpData.userId" @input="(e) => handleInput('userId', e.target.value)" class="signUp_form-input" name="userId" id="userId"/>
        <div class="signUp_form-oo" :style="{ color: errorState.userId ? 'red' : 'black' }">
          {{ errorMessage.userId }}
        </div>
      </div>
     </div>

    <!--선택박스 사용해서 이메일 종류 선택이랑 input으로 직접입력하기 기능 -->
      <!-- 이메일 -->
      <div class="signUp-session">
        <div class="signUp-label">
          <label for="email" class="form-label">Emaill</label>
        </div>
      <div>
      <div style="display:flex; gap: 10px; align-items: center;">
        <!--이메일 앞부분-->
        <input size="20"  type="text" ref="focusEmailField" v-model="signUpData.email.emailPrefix"  @input="validateField.email" class="signUp_form-input" name="emailPrefix" id="emailPrefix" aria-describedby="emailHelp"/>
        <span>@</span>
        <!--이메일 도메인 선택-->
        <select v-model="signUpData.email.emailDomain" @change="validateField.email" class="signUp_form-input">
          <option value="" disabled selected>이메일선택</option>
          <option value="naver.com">naver.com</option>
          <option value="gmail.com">gmail.com</option>
          <option value="nate.com">nate.com</option>
          <option value="hanmail.net">hanmail.net</option>
          <option value="daum.net">daum.net</option>
          <option value="custom">직접입력</option>
        </select>
   
        <input size="30"  type="text" v-if="signUpData.email.emailDomain === 'custom'" v-model="signUpData.email.customDomain" @input="validateField.email" class="signUp_form-input" name="customDomain" id="customDomain" placeholder="도메인 입력" aria-describedby="emailHelp"/>
        <div class="signUp_form-oo" :style="{ color: errorState.email ? 'red' : 'black' }">
          {{ errorMessage.email }}
        </div>
      </div>
    </div>
    </div>
    <!--패스워드-->
    <div class="signUp-session">
      <div class="signUp-label">
        <label  for="password" class="form-label">Password</label>
      </div>
      <div>
        <input size="30" :type="visibility.showPassword ? 'text' : 'password'" v-model="signUpData.password" @input="(e) => handleInput('password', e.target.value)"  class="signUp_form-input" name="password"  id="password"/>
        <button type="button" @click="() => toggleVisibility('showPassword')">
          {{  visibility.showPassword ? '숨기기' : '보기' }}
        </button>
        <div class="signUp_form-oo" v-if="errorState.password" :style="{ color: errorState.password ? 'red' : 'black' }">>
        {{ errorMessage.password }}
      </div>
    </div>
  </div>
    <!--re패스워드-->
      <div class="signUp-session">
        <div class="signUp-label">
          <label for="rePassword" class="form-label">Repassword</label>
        </div>
        <div>
            <input size="30" :type="visibility.showRePassword ? 'text' : 'password'" v-model="signUpData.rePassword" @input="(e) => handleInput('rePassword', e.target.value)" class="signUp_form-input" name="rePassword" id="rePassword" placeholder="비밀번호를 한 번 더 입력해 주세요.">
            <button type="button" @click="() => toggleVisibility('showRePassword')">
              {{ visibility.showRePassword ? '숨기기' : '보기' }}
            </button>
            <div class="signUp_form-oo" :style="{ color: errorState.rePassword ? 'red' : 'black' }">
                {{ errorMessage.rePassword }}
              </div>
        </div>
    </div>
    <!--핸드폰 번호 3글자 +`-`+4글자  +`-`+4글자--->
    <div class="signUp-session">
       <div class="signUp-label">
        <label for="phone" class="form-label">휴대전화 *</label>
    </div>
    <div style="display: flex; gap: 10px;">
    <!-- 첫 번째 입력 필드 -->
      <input
        type="text"
        v-model="signUpData.phone.first"
        maxlength="3"
        placeholder="010"
        @input="(e) => handleInput('phone.first', e.target.value)"
        class="signUp_form-input phone-input"
      />
    <!-- 두 번째 입력 필드 -->
    <input
      type="text"
      v-model="signUpData.phone.middle"
      maxlength="4"
      placeholder="1234"
      @input="(e) => handleInput('phone.middle', e.target.value)"
      class="signUp_form-input phone-input"
    />
    <!-- 세 번째 입력 필드 -->
    <input
      type="text"
      v-model="signUpData.phone.last"
      maxlength="4"
      placeholder="5678"
      @input="(e) => handleInput('phone.last', e.target.value)"
      class="signUp_form-input phone-input"/>
  </div>
  <div class="signUp_form-oo" :style="{ color: errorState.phone ? 'red' : 'black' }">
    {{ errorMessage.phone }}
  </div>
</div>

    <!--이름-->
    <div class="signUp-session">
      <div class="signUp-label">
        <label for="username" class="form-label">Name</label>
      </div>
      <div>
        <input size="30" type="text"  v-model="signUpData.username" @input="(e) => handleInput('username', e.target.value)" class="signUp_form-input" name="username" id="username" placeholder="이름을 입력해 주세요."/>
        <div class="signUp_form-oo">
          <div v-if="errorState.username"  :style="{ color: errorState.username ? 'red' : 'black' }">
            {{ errorMessage.username }}
          </div>
        </div>
      </div>
    </div>
    <!--Submit -->
     <!-- <button type="button" @click="onClickSignUpButton"  class="signup_form_button">
      회원가입
     </button> -->
     <button type="button" @click="onClickVerificationButton"  class="verification_form_button">
      인증 코드 보내기
     </button>
    </form>
    <p v-if="signUpData.message">{{ signUpData.message }}</p>

  </div>
</div>
</template>

<style scoped>
.phone-input {
  width: 80px; /* 각 입력 필드 너비 */
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 0.5rem;
  text-align: center;
}

.phone-input:focus {
  border: 1px solid #4a90e2;
  outline: none;
}

.temp {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh; /* 화면 전체 높이를 채움 */
  background-color: #f8f9fa; /* 배경색 */
}

.signUp-session{
  display: flex;
  flex-direction: column; /* 수직 정렬 */
  margin-bottom: 1.5rem; /* 필드 간 여백 */
}
.signUp-label{
  font-size: 1rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}
.signUp_form{
  width: 90%; /* 화면 크기 따라 자동 조정 */
  max-width: 500px; /* 최대 너비 설정 */
  padding: 2rem;
  background-color: #f9f9f9;
  border-radius: 10px;
  margin: auto;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.signUp_form-input{
  width: 100%; /* 필드 너비가 컨테이너에 맞춰짐 */
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 0.5rem;
  box-sizing: border-box;
}

.signUp_form-input:focus{
  border: 1px solid #4a90e2;
  outline: none;

}

.signup_form_button{
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

.signup_form_button:hover{
  background-color: #357abd;

}

.verification_form_button{
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

.verification_form_button:hover{
  background-color: #357abd;
}
.signUp_form-oo{
  font-size: 1em;
  color: red;
  margin-top: 0.5rem;
}
</style>
