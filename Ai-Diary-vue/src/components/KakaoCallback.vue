<script setup>
import { useRoute, useRouter } from 'vue-router';
import { onMounted } from 'vue';
import Swal from 'sweetalert2';

const route = useRoute();
const router = useRouter();

onMounted(() => {
  const jwtToken = route.query.jwtToken;
  const email = route.query.email;
  const nickname = route.query.nickname;

  if (!jwtToken || !email) {
    Swal.fire("로그인 실패", "토큰 또는 사용자 정보가 없습니다", "error");
    return;
  }

  // 로컬스토리지 저장
  localStorage.setItem("jwtToken", jwtToken);
  localStorage.setItem("accessToken", jwtToken);
  localStorage.setItem("email", email);
  localStorage.setItem("nickname", nickname);

  Swal.fire("카카오 로그인 성공", "환영합니다!", "success");
  router.push("/diary/common");
});
</script>

<template>
  <div>로그인 중입니다... 잠시만 기다려 주세요.</div>
</template>
