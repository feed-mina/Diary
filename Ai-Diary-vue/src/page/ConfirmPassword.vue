<script>
import {onMounted, ref} from "vue";
import {useRouter} from "vue-router";
import Cookies from "universal-cookie";
import axios from "axios";
// import { apiUrl } from "@/unit/axiosInstance.js";
import Swal from "sweetalert2";

export default {
  name: 'ConfirmPassword', // 다중 단어 이름으로 변경
  setup() {
    const router = useRouter();
    const cookies = new Cookies();
    const isConfirmPg = ref(true);

    // 로그인 입력 데이터
    const confirmData = ref({
      checkId: "",
      checkPassword: "",
    });
    const errorWarning = ref({
      checkId: false,
      checkPassword: false,
    });
    const idErrorMessage = ref("");
    const passwordErrorMessage = ref("");

    // const idValid = ref(true);
    // const passwordValid = ref(true);
    const showPassword = ref(false);
    const togglePasswordVisibility = () => {
      showPassword.value = !showPassword.value;
    }
    // 로그인 상태 확인 후 리다이렉트
    onMounted(() => {
      const userId = localStorage.getItem("email");
      if (!userId) {
        confirmData.value = {
          checkId: "",
          checkPassword: "",
        };
        // router.push("/login");
      } else {
        // router.push("/diary/common");
      }
    });

    const handleConfirmData = (event) => {
      confirmData.value[event.target.name] = event.target.value;
    };


    const onClickConfirmButton = async () => {
      // console.log("로그인 데이터 :", confirmData.value);


      if (!confirmData.value.checkPassword) {
        // alert("비밀번호를 입력해주세요.");

        Swal.fire({
          title: "비밀번호 오류",
          text: "비밀번호를 입력해주세요",
          icon: "warning",
          confirmButtonText: "확인",
          confirmButtonColor: "#FF5733"
        });
        return;
      }

      try {
        // API 호출 및 응답 처리
        const jwtToken = await sendConfirmData();
        // console.log("로그인 성공, JWT:", jwtToken);

        // JWT 토큰을 쿠키 또는 localStorage에 저장
        // cookies.set("jwt", jwtToken, { path: "/" });
        // localStorage.setItem("userId", confirmData.value.userId);
        // alert("로그인을 완료했습니다.");

        Swal.fire({
          title: "비밀번호 확인",
          text: "비밀번호 인증을 성공했습니다",
          icon: "success",
          confirmButtonText: "확인",
          confirmButtonColor: "#357abd",
        });

        router.push("/edit/myInfo").then(() => {
          location.reload(); // 새로고침
        });
      } catch (error) {
        // 에러 처리
        console.error("실패:", error);
        // alert(error.response?.data?.message || "로그인에 실패했습니다.");

        Swal.fire({
          title: " 실패",
          text: "실패했습니다.",
          icon: "warning",
          confirmButtonText: "확인",
          confirmButtonColor: "#FF5733"
        });
      }
    };


    // 로그인 API 호출
    const sendConfirmData = async () => {
      try {
        const response = await  axiosInstance.post(`/api/auth/confirmData`, confirmData.value);
        return response.data; // 응답 데이터를 반환합니다.
      } catch (error) {
        console.error("API 호출 실패:", error.response?.data || error.message);
        throw error; // 예외를 던져서 상위에서 처리하도록 합니다.
      }
    };

    return {
      isConfirmPg,
      confirmData,
      errorWarning,
      showPassword,
      handleConfirmData,
      idErrorMessage,
      passwordErrorMessage,
      togglePasswordVisibility,
      onClickConfirmButton
    };

  },

};
</script>
<template>
  <header class="confirmHeader">
    <!-- <button class="prevBtn" title="이전 페이지" type="button">
       <svg width="24" height="24" viewBox="0 0 24 24" fill="none" direction="right" class="header_svg">
          <rect width="24" height="24"></rect>
          <path d="M8 3L16.2929 11.2929C16.6262 11.6262 16.7929 11.7929 16.7929 12C16.7929 12.2071 16.6262 12.3738 16.2929 12.7071L8 21" stroke="#1c1c1e" stroke-width="1.6" stroke-linecap="round"></path>
       </svg>
    </button> -->
    <h1>비밀번호 인증</h1>
  </header>

  <div class="layoutContainer">
    <div class="layoutContent">
      <h2 class="confirmTitle">개인정보 보호를 위해<br>비밀번호 확인이 필요해요.</h2>
      <div>
        <div class="checkId">
          <input type="text" disabled="" v-model="confirmData.checkId">
        </div>
      </div>
      <div class="gapCheckInput">
        <div class="password_field">
          <input type="password" placeholder="비밀번호"
                 v-model="confirmData.checkPassword"
          >
          <span class="inputInnerWrap">
                <button class="password_field_button" type="button" @click="togglePasswordVisibility"><span
                    class="inputInnerWrap">보기</span></button></span></div>
      </div>
      <div class="confirmButtonWrap">
        <button type="button" class="confirmButton">확인</button>
      </div>
    </div>
  </div>
</template>