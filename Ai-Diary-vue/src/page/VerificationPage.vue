<template>
  <div class="email-verification">
    <h2>이메일 인증하기</h2>
    <p>인증 코드를 입력해주세요</p>
    <div class="code-box">
      <input v-model="code" maxlength="6" placeholder="000000"/>
    </div>
    <button @click="checkCode">확인</button>
    <p>{{ message }}</p>
  </div>
</template>
<script>
import axios from 'axios';
import {useRoute} from 'vue-router';

export default {
  setup() {
    const route = useRoute();
    const email = route.query.email;  // 주소창에서 이메일 가져오기
    const code = ref('');
    const message = ref('');

    const checkCode = async () => {
      try {
        const response = await axios.post('http://localhost:8080/api/auth/verify-code', {
          email,
          code: code.value,
        });
        message.value = '인증 성공!';
        await sendSignUpData(); 


        console.log('response : ', response);
      } catch {
        message.value = '인증 실패! 코드를 다시 확인해주세요.';
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

        const response = await axios.post("http://localhost:8080/api/auth/register", signUpDataToSave);

        console.log('회원가입 response : ', response);
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

    return {code, message, checkCode};
  }
};
</script>
<style scoped>
.email-verification {
  max-width: 400px;
  margin: auto;
  padding: 20px;
  text-align: center;
}

.code-box input {
  font-size: 2rem;
  text-align: center;
  width: 100%;
  padding: 10px;
  margin: 20px 0;
}

button {
  padding: 10px 20px;
  font-size: 1.2rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
}
</style>