<script>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import Cookies from "universal-cookie";
import axios from "axios";
import Swal from "sweetalert2";
import { Notyf } from "notyf";
import "notyf/notyf.min.css";

const notyf = new Notyf();
const KAKAO_CLIENT_ID = import.meta.env.VITE_KAKAO_JS_KEY || "";
const REDIRECT_URI = window.location.origin + "/project1/oauth/callback";
console.log("Kakao API Key:", KAKAO_CLIENT_ID);
console.log("API URL:", import.meta.env.VITE_API_URL);

export default {
  name: "LoginPage",
  setup() {
    const router = useRouter();
    const cookies = new Cookies();
    const isLoginPg = ref(true);

    // 로그인 입력 데이터
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
    const togglePasswordVisibility = () => {
      showPassword.value = !showPassword.value;
    };

    // 이메일 주소 업데이트 함수
    const updateFullEmail = () => {
      if (loginData.value.emailDomain === "custom") {
        loginData.value.email = `${loginData.value.emailPrefix}@${loginData.value.customDomain}`;
      } else {
        loginData.value.email = `${loginData.value.emailPrefix}@${loginData.value.emailDomain}`;
      }
    };

    // 비밀번호 입력 시 유효성 검사 함수 추가
    const handlePasswordChange = () => {
      if (loginData.value.password.length < 6) {
        errorWarning.value.password = true;
        passwordErrorMessage.value = "비밀번호는 최소 6자리 이상이어야 합니다.";
      } else {
        errorWarning.value.password = false;
        passwordErrorMessage.value = "";
      }
    };

    // 로그인 버튼 클릭 시 호출되는 함수
    const onClickLoginButton = async () => {
      console.log("로그인 데이터 :", loginData.value);
      if (!loginData.value.email || !loginData.value.password) {
        Swal.fire("입력 오류", "이메일과 비밀번호를 입력해주세요.", "warning");
        return;
      }

      try {
        const response = await axios.post("http://localhost:8080/api/auth/login", {
          email: loginData.value.email,
          password: loginData.value.password,
        });

        // JWT 토큰을 response.data에서 가져옴
        const jwtToken = response.data;
        console.log("로그인 성공, JWT:", jwtToken);

        localStorage.setItem("jwt", jwtToken);
        localStorage.setItem("email",loginData.value.email);
        Swal.fire("로그인 성공", "로그인을 완료했습니다", "success");

        router.push("/diary/common").then(() => {
          location.reload(); // 새로고침
        });
      } catch (error) {
        console.error("로그인 실패:", error);
        Swal.fire("로그인 실패", error.response?.data?.message || "로그인 실패", "error");
      }
    };

    // 카카오 SDK 로딩 함수
    const loadKakaoSDK = () => {
      return new Promise((resolve, reject) => {
        if (window.Kakao && window.Kakao.isInitialized()) {
          console.log("✅ 카카오 SDK 이미 초기화됨");
          resolve();
          return;
        }
        const kakaoScript = document.createElement("script");
        kakaoScript.src = "https://developers.kakao.com/sdk/js/kakao.js";
        kakaoScript.onload = () => {
          if (!window.Kakao) {
            console.error("❌ 카카오 SDK 로드 실패");
            reject();
            return;
          }
          window.Kakao.init(KAKAO_CLIENT_ID);
          console.log("✅ 카카오 SDK 로드 완료");
          resolve();
        };
        kakaoScript.onerror = () => {
          console.error("❌ 카카오 SDK 로드 실패");
          reject();
        };
        document.head.appendChild(kakaoScript);
      });
    };

    // kakaoToken 변수 추가
    const kakaoToken = ref("");

    // 토큰 저장 함수
    const saveToken = (token) => {
      localStorage.setItem("kakaoAccessToken", token);
      kakaoToken.value = token;
    };

    // 카카오 로그인 함수 (하나로 통합)
    const kakaoLogin = async () => {
      try {
        await loadKakaoSDK();

        if (!window.Kakao || !window.Kakao.Auth) {
          notyf.error("❌ 카카오 SDK 로드 실패");
          return;
        }

        window.Kakao.Auth.login({
          scope: "profile_nickname, account_email, talk_message",
          success: async function (authObj) {
            const kakaoAccessToken = authObj.access_token;
            console.log("카카오 AccessToken:", kakaoAccessToken);

            try {
              const response = await axios.post("http://localhost:8080/api/kakao/login", {
                accessToken: kakaoAccessToken,
              });
              console.log("카카오 로그인 응답:", response);
              Swal.fire("카카오 로그인 성공", "로그인을 완료했습니다", "success");

              localStorage.setItem("kakaoAccessToken", kakaoAccessToken);
              router.push("/diary/common").then(() => {
                location.reload();
              });
            } catch (error) {
              Swal.fire("로그인 실패", "카카오 로그인 실패", "error");
              console.error("❌ 카카오 로그인 실패", error);
            }
          },
          fail: function (err) {
            Swal.fire("로그인 실패", "카카오 로그인 실패", "error");
            console.error("❌ 카카오 로그인 실패", err);
          },
        });
      } catch (error) {
        console.error("❌ 로그인 중 오류 발생:", error);
        notyf.error("❌ 로그인 실패");
      }
    };

    // 카카오 로그아웃 함수
    const kakaoLogout = () => {
      localStorage.removeItem("kakaoAccessToken");
      kakaoToken.value = "";
      notyf.success("로그아웃 되었습니다!");
    };

    onMounted(() => {
      console.log("로그인 페이지 진입");
      // 카카오 SDK가 초기화되지 않았다면 로드
      if (!window.Kakao || !window.Kakao.isInitialized()) {
        const kakaoScript = document.createElement("script");
        kakaoScript.src = "https://developers.kakao.com/sdk/js/kakao.js";
        kakaoScript.onload = () => {
          window.Kakao.init(KAKAO_CLIENT_ID);
          console.log("✅ 카카오 SDK 로드 완료");
        };
        kakaoScript.onerror = () => {
          console.error("❌ 카카오 SDK 로드 실패");
        };
        document.head.appendChild(kakaoScript);
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
      kakaoLogout,
    };
  },
};
</script>

<template>
  <div class="loginPage">
    <p class="login-guide">로그인 후 시작해볼까요? 😊</p>
    <div id="login_form" class="login_form">
      <!-- 로그인 폼 -->
      <form @submit.prevent="onClickLoginButton" class="login_form_box">
        <!-- 이메일 입력 폼 -->
        <div class="login-session">
          <div class="login-label">
            <label for="email" class="form-label">Email</label>
          </div>
          <div style="display: flex; gap: 10px; align-items: center;">
            <!-- 이메일 아이디 부분 -->
            <input size="20" type="text" v-model="loginData.emailPrefix" @input="updateFullEmail" class="login_form-input" name="emailPrefix" id="emailPrefix" placeholder="이메일 앞부분" />
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
            <input size="30" type="text" v-if="loginData.emailDomain === 'custom'" v-model="loginData.customDomain" @input="updateFullEmail" class="login_form-input" name="customDomain" id="customDomain" placeholder="도메인 입력" />
          </div>
        </div>
        <!-- 패스워드 -->
        <div class="login-session">
          <div class="login-label">
            <label for="password" class="form-label">Password</label>
          </div>
          <div>
            <input size="30" :type="showPassword ? 'text' : 'password'" v-model="loginData.password" @input="handlePasswordChange" class="login_form-input" name="password" id="password" />
            <button type="button" @click="togglePasswordVisibility">
              {{ showPassword ? '숨기기' : '보이기' }}
            </button>
            <div class="login_form-oo" v-if="errorWarning.password" :style="{ color: errorWarning.password ? 'red' : 'black' }">
              {{ passwordErrorMessage }}
            </div>
          </div>
        </div>
        <!-- 로그인 버튼 -->
        <button type="submit" class="login_form_button">로&nbsp;&nbsp;&nbsp;그&nbsp;&nbsp;&nbsp;인</button>
      </form>
      <div>
        <button class="kakao-button" @click="kakaoLogin">
          <img alt="kakaoLogin" class="logo" src="../assets/kakao_login_bt.png" />
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.loginPage {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  background-color: #f8f9fa;
  padding: 3rem;
  flex-direction: column;
}
.login_form {
  display: flex;
  height: 100%;
  width: 100%;
  padding: 3rem;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  justify-content: flex-start;
  flex-direction: column;
  margin-bottom: 1.5rem;
}
.login_form_box {
  height: 90%;
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
}
.login-label {
  font-size: 1rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}
.login_form-input {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 0.5rem;
  box-sizing: border-box;
}
.login_form-input:focus {
  border: 1px solid #4a90e2;
  outline: none;
}
.kakao-button,
.login_form_button {
  width: 100%;
  height: 50px;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}
.kakao-button img {
  width: 100%;
  height: 50px;
}
.login_form_button {
  background-color: #4a90e2;
  color: white;
  padding: 0.8rem 1.5rem;
  font-weight: 600;
  font-size: large;
}
.login_form_button:hover {
  background-color: #357abd;
}
.login_form-oo {
  font-size: 0.9rem;
  color: red;
  margin-top: 0.5rem;
}
</style>
