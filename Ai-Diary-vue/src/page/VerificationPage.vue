
<script>
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import { ref, onMounted} from 'vue';
// import { apiUrl } from "@/api/index.js";
import { Notyf } from 'notyf';
import 'notyf/notyf.min.css';
export default {
  setup() {
    const route = useRoute();
    const router = useRouter();
    const email = route.query.email;  // 주소창에서 이메일 가져오기
    const codeDigits = ref(['','','','','','',''])// 7자리 코드 입력값 배열
    const message = ref('');
    const timer = ref(180); // 3분(180초)
    const resendEnabled = ref(false);  // 재전송 버튼 상태
    let countdown;
   // 알림, 소리 설정
   const notyf = new Notyf();

    const startTimer = () => {
      timer.value = 180; // 타이머 초기화
    
        countdown = setInterval(() => {
          if (timer.value > 0) {
            timer.value--;
          } else {
            clearInterval(countdown);
            message.value = '⏰ 시간이 다 됐어요! 코드를 다시 받아주세요.';
            resendEnabled.value = true; // 시간이 끝나면 재전송 가능하게
          }
        }, 1000);
      };

    // 다음 칸으로 커서 이동
    const handleInput = (event, index) =>{
      const value = event.target.value.replace(/[^0-9]/g, ''); // ✅ 숫자만 허용
      codeDigits.value[index] = value;  // ✅ 입력값 반영
      if(event.target.value.length === 1 && index < 6){
        document.getElementById(`code-${index + 1}`).focus();
      }
      checkAutoSubmit();
    };

    // 6자리 다 입력되면 자동 확인
    const checkAutoSubmit = () =>{
      if(codeDigits.value.every(digit => digit !== '')){
        checkCode();
      }
    };

    // 인증코드확인
    const checkCode = async () => {
      const code = codeDigits.value.join('');
      console.log("전송할 이메일:", email, "전송할 코드:", code);

      try {
        const response = await axios.post(`/api/auth/verify-code`, {email,code });
        console.log('response : ', response);

        message.value = '인증 성공 !';
        resendEnabled.value = false; // 인증 성공하면 재전송 버튼 숨김
        console.log('로그인페이지로 이동');
        router.push('/login').then(()=> location.reload());
      } catch {
        message.value = '인증 실패! 코드를 다시 확인해주세요.';
        resendEnabled.value = true; // 인증실패하면 재전송 가능하게
      }
    };

    const sendSignUpData = async () => {
      try {
        const {userId, email, password, username, phone} = signUpData.value;
        const signUpDataToSave = {
          userId,
          email: `${email.emailPrefix}@${email.emailDomain === 'custom' ? email.customDomain : email.emailDomain}`,
          password,
          phone: `${phone.first}${phone.middle}${phone.last}`,
          username,
        };

        console.log("회원가입 데이터:", signUpDataToSave);
        notify.success("회원가입이 완료되었습니다!");

        // router.push("/login").then(() => location.reload());
        return response.data;
      } catch (error) {
        console.error("API 호출 실패", error);
        if (error.response && error.response.status === 400) {
          notify.error("회원가입에 실패했습니다. 다시 시도해주세요.");
          console.log('error: ',error.response.data); // 서버에서 보낸 메시지: "이미 존재하는 이메일입니다."
          focusEmailField.value.focus();
          errorState.value.email = true;
          errorMessage.value.email = error.response.data;
        } else {
          console.error("API 호출 실패", error);
          notify.error("회원가입에 실패했습니다. 다시 시도해주세요.");
        }
      }
    };

    onMounted(()=>{
        startTimer();
      });
    const resendCode =async () =>{
      try{
        await axios.post(`/api/auth/resend-code`, {email});
        message.value = ' 새 코드가 전송되었어요 ! ';
        codeDigits.value = ['','','','','','',''];
        timer.value = 180;
        resendEnabled.value = false;
        startTimer();
        document.getElementById('code-0').focus();
      }catch{
        message.value = '코드 재전송 실패';
      }
    };
    return { email, codeDigits, message, timer, resendEnabled, checkCode, handleInput, resendCode};
  }
};
</script>

<template>
  <div class="email-verification">
    <h2>이메일 인증하기</h2>
    <p>
      인증 코드를 <span class="highlight">{{ email }}</span>로 보냈어요.<br />
      6자리 코드를 입력해주세요.   3분 안에 입력해주세요.
    </p>

    <p class="timer">
      남은 시간: {{ Math.floor(timer / 60) }}분 {{ timer % 60 }}초
    </p>

    <div class="code-inputs">
      <input v-for="(digit, index) in codeDigits" :key="index" :id="`code-${index}`" v-model="codeDigits[index]" maxlength="1"  type="text" class="code-box" @input="handleInput($event, index)"/>
    </div>
    <button class="checkButton" v-if="!resendEnabled"  @click="checkCode">확인</button>

    <button @click="resendCode" v-if="resendEnabled" >코드 재전송</button>

    <p>{{ message }}</p>
  </div>
</template>
<style scoped>
</style>