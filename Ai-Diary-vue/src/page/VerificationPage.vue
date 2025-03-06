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
      } catch {
        message.value = '인증 실패! 코드를 다시 확인해주세요.';
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