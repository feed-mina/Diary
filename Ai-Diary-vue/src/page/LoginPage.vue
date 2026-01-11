<script>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import Cookies from "universal-cookie";
import axios from "axios";
import Swal from "sweetalert2";
import { Notyf } from "notyf";
import "notyf/notyf.min.css";
import axiosInstance from "@/unit/axiosInstance.js";
const notyf = new Notyf();

export default {
  name: "LoginPage",
  setup() {
    const router = useRouter();
    const cookies = new Cookies();
    const isLoginPg = ref(true);

    const loginData = ref({
      emailPrefix: "",
      emailDomain: "",
      customDomain: "",
      email: "",
      password: "",
    });

    const errorWarning = ref({
      email: false,
      password: false,
    });
    const idErrorMessage = ref("");
    const passwordErrorMessage = ref("");
    const showPassword = ref(false);

    const handlePasswordChange = () => {};

    const togglePasswordVisibility = () => {
      showPassword.value = !showPassword.value;
    };

    const updateFullEmail = () => {
      if (!loginData.value.emailPrefix) return;
      if (loginData.value.emailDomain === "custom" && loginData.value.customDomain) {
        loginData.value.email = `${loginData.value.emailPrefix}@${loginData.value.customDomain}`;
      } else if (loginData.value.emailDomain) {
        loginData.value.email = `${loginData.value.emailPrefix}@${loginData.value.emailDomain}`;
      } else {
        loginData.value.email = "";
      }
    };

    const onClickLoginButton = async () => {
      updateFullEmail(); // 먼저 email 조합

      if (!loginData.value.email || !loginData.value.password) {
        Swal.fire("입력 오류", "이메일과 비밀번호를 입력해주세요.", "warning");
        return;
      }

      try {
        const response = await  axiosInstance.post(`/api/auth/login`, {
          email: loginData.value.email,
          password: loginData.value.password,
        });

        const accessToken = response.data.accessToken;
        const refreshToken = response.data.refreshToken;

        console.log("accessToken", accessToken);
        console.log("refreshToken", refreshToken);
        console.log(" response.data",  response.data);

        if (accessToken || refreshToken) {
          localStorage.setItem("jwtToken", accessToken);
          localStorage.setItem("accessToken", accessToken);
          localStorage.setItem("refreshToken", refreshToken);
        } else {
          console.warn("토큰이 응답에 포함되지 않았습니다.");
        }

        const [userId] = loginData.value.email.split("@");
        localStorage.setItem("userId", userId);
        localStorage.setItem("email", loginData.value.email);
        localStorage.setItem("password", loginData.value.password);
        localStorage.setItem("sleepUsingType", "Y");
        localStorage.setItem("drugUsingType", "Y");

        Swal.fire("로그인 성공", "로그인을 완료했습니다", "success");
        router.push("/diary/common").then(() => {
          location.reload();
        });
      } catch (error) {
        const message =
            error.response?.data?.message || error.message || "로그인 실패";
        Swal.fire("로그인 실패", message, "error");
        console.error("로그인 실패:", error);
      }
    };

    const kakaoLogin = () => {
      console.log('kakaoLoginVUE')
      if (!window.Kakao || !window.Kakao.Auth || !window.Kakao.isInitialized()) {
        Swal.fire("카카오 SDK 로드 중입니다", "잠시 후 다시 시도해주세요", "info");
        return;
      }
      console.log("axios baseURL:", axios.defaults.baseURL); // 꼭 찍어보자
      console.log("axiosInstance 요청 URL:", axiosInstance);
      console.log("axiosInstance baseURL:", axiosInstance.defaults.baseURL);


      window.Kakao.Auth.login({
        scope: "profile_nickname, account_email, talk_message",
        success: async (authObj) => {
          try {
            console.log("로그인 요청 시도", loginData.value);

            const kakaoAccessToken = authObj.access_token;
            console.log("250527_로그인 요청 시도 kakaoAccessToken", kakaoAccessToken);

            const response = await  axiosInstance.post(`/api/kakao/login`, {
              accessToken: localStorage.getItem("kakaoAccessToken")
            });
            console.log('response:',response);
            const { accessToken, refreshToken, kakaoUserInfo } = response.data;

            console.log('userInfo:',userInfo);

            if (!kakaoUserInfo || typeof kakaoUserInfo.email !== 'string' || !kakaoUserInfo.email.includes("@")) {
              Swal.fire("로그인 실패", "이메일 정보가 없어 로그인할 수 없습니다.", "error");
              return;
            }

            const kakao_email = kakaoUserInfo.email;
            const kakao_nickname = kakaoUserInfo.nickname;
            const [userId] = kakao_email.split("@");


            localStorage.setItem("kakaoAccessToken", kakaoAccessToken);
            localStorage.setItem("userId", userId);
            localStorage.setItem("email", kakao_email);
            localStorage.setItem("nickname", kakao_nickname);
            localStorage.setItem("jwtToken", accessToken);
            localStorage.setItem("accessToken", accessToken);  // interceptor용

            Swal.fire("카카오 로그인 성공", "로그인을 완료했습니다", "success");
            router.push("/diary/common").then(() => {
              location.reload();
            });
          } catch (error) {
            Swal.fire("로그인 실패", error.response?.data?.message || "카카오 로그인 실패", "error");
          }
        },
        fail: (err) => {
          Swal.fire("로그인 실패", "카카오 로그인 실패", "error");
          console.error("카카오 로그인 실패", err);
        },
      });
    };

    onMounted(() => {
      console.log("  250527_로그인 페이지 진입");
      if (!window.Kakao || !window.Kakao.isInitialized()) {
        const kakaoScript = document.createElement("script");
        kakaoScript.src = "https://developers.kakao.com/sdk/js/kakao.js";
        kakaoScript.onload = () => {
          window.Kakao.init(import.meta.env.VITE_KAKAO_JS_KEY);
          console.log('VITE_KAKAO_JS_KEY', import.meta.env.VITE_KAKAO_JS_KEY);
          console.log('VITE_MODE_NAME', import.meta.env.VITE_MODE_NAME);
          console.log('VUE_APP_API_BASE_URL', import.meta.env.VUE_APP_API_BASE_URL);
          console.log('VITE_APP_API_BASE_URL', import.meta.env.VITE_APP_API_BASE_URL);
        };
        kakaoScript.onerror = () => {
          console.error("카카오 SDK 로드 실패");
        };
        document.head.appendChild(kakaoScript);
        console.log('kakaoScript', kakaoScript);
      }
    });

    return {
      isLoginPg,
      loginData,
      errorWarning,
      showPassword,
      updateFullEmail,
      idErrorMessage,
      passwordErrorMessage,
      togglePasswordVisibility,
      handlePasswordChange,
      onClickLoginButton,
      kakaoLogin,
    };
  },
};
</script>

<template>
  <div class="loginPage">
    <p class="login-guide">로그인 후 시작해볼까요? 😊</p>
    <div id="login_form" class="login_form">
      <!-- 로그인 또는 회원가입 폼 렌더링 -->
      <!-- 로그인 폼 -->
      <form @submit.prevent="onClickLoginButton" class="login_form_box">
        <!-- 이메일 입력 폼 -->
        <div class="login-session">
          <div class="login-label">
            <label for="emailPrefix" class="form-label">Email</label>
          </div>
          <div style="display: flex; gap: 10px; align-items: center;">
            <!-- 이메일 아이디 부분 -->
            <input size="20" type="text" v-model="loginData.emailPrefix" @input="updateFullEmail"
                   class="login_form-input" name="emailPrefix" id="emailPrefix" placeholder="이메일 앞부분"/>
            <span>@</span>
            <!-- 이메일 도메인 선택 -->
            <select v-model="loginData.emailDomain" @change="updateFullEmail" class="login_form-input">
              <option value="" disabled selected>이메일 선택</option>
              <option value="naver.com">naver.com</option>
              <option value="gmail.com">gmail.com</option>
              <option value="nate.com">nate.com</option>
              <option value="hanmail.net">hanmail.net</option>
              <option value="daum.net">daum.net</option>
              <option value="custom">직접 입력</option>
            </select>
            <!-- 직접 입력 -->
            <input size="30" type="text" v-if="loginData.emailDomain === 'custom'"
                   v-model="loginData.customDomain" @input="updateFullEmail"
                   class="login_form-input" name="customDomain" id="customDomain" placeholder="도메인 입력"/>
          </div>
        </div>
        <!-- 패스워드 -->
        <div class="login-session">
          <div class="login-label">
            <label for="password" class="form-label">Password</label>
          </div>
          <div>
            <input size="30" :type="showPassword ? 'text' : 'password'" v-model="loginData.password"
                   @input="handlePasswordChange" class="login_form-input" name="password" id="password" autocomplete="current-password"/>
            <button type="button" @click="togglePasswordVisibility" >
              {{ showPassword ? "숨기기" : "보이기" }}
            </button>
            <div class="login_form-oo" v-if="errorWarning.password"
                 :style="{ color: errorWarning.password ? 'red' : 'black' }">
              {{ passwordErrorMessage }}
            </div>
          </div>
        </div>
        <!-- 로그인 버튼 -->
        <div class="login_form_button_div">
          <button type="submit" class="login_form_button">로 그 인</button>
        </div>
      </form>
      <div>
        <button class="kakao-button" type="button" @click="() => { console.log('버튼 눌림'); kakaoLogin(); }">
          <img class="kakaoLoginImg" src="/img/kakao_login_large_narrow.png" alt="kakaoLogin"/>
        </button>
      </div>
    </div>
  </div>
</template>