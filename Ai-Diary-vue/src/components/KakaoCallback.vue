<script>
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import Swal from 'sweetalert2';

export default {
  name: 'KakaoCallback',
  setup() {
    const route = useRoute();
    const router = useRouter();

    onMounted(async () => {
      const code = route.query.code;
      if (!code) {
        Swal.fire('오류', '카카오 인증 코드가 없습니다.', 'error');
        return;
      }

      try {
        const response = await axios.get(`/api/kakao/callback?code=${code}`);
        const accessToken = response.data.accessToken;
        const refreshToken = response.data.refreshToken;
        const userInfo = response.data.kakaoUserInfo;

        localStorage.setItem('jwtToken', accessToken);
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
        localStorage.setItem('email', userInfo.email);
        localStorage.setItem('nickname', userInfo.nickname);

        Swal.fire('로그인 성공', '카카오 로그인 완료', 'success');
        router.push('/diary/common');
      } catch (error) {
        Swal.fire('로그인 실패', error.response?.data?.message || '로그인 처리 실패', 'error');
        console.error('카카오 콜백 오류:', error);
      }
    });
  }
};
</script>

<template>
  <div class="callback-container">
    <p>카카오 로그인 처리 중입니다... 잠시만 기다려주세요.</p>
  </div>
</template>

<style scoped>
.callback-container {
  text-align: center;
  margin-top: 100px;
  font-size: 1.2em;
}
</style>
