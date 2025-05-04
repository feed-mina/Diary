<script>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import Cookies from "universal-cookie";
import axios from "axios";
// import { apiUrl } from "@/api/index.js";
import Swal from "sweetalert2";
import { Notyf } from "notyf";
import "notyf/notyf.min.css";
import KakaoImg from '/img/kakao_login_large_narrow.png';

const notyf = new Notyf();

export default {
  name: "LoginPage",
  setup() {
    const router = useRouter();
    const cookies = new Cookies();
    const isLoginPg = ref(true);

    // 로그인 입력 데이터
    const loginData = ref({
      emailPrefix: "", // 이메일 앞부분
      emailDomain: "", // 이메일 도메인
      customDomain: "", // 직접 입력 도메인
      email: "", // 최종 이메일 주소
      password: "",
    });

    const errorWarning = ref({
      email: false,
      password: false,
    });
    const idErrorMessage = ref("");
    const passwordErrorMessage = ref("");
    const showPassword = ref(false);

    // 패스워드 입력 변화 처리 함수 (필요하면 유효성 검사 로직 추가)
    const handlePasswordChange = () => {
      // 예시: 현재는 아무 작업도 하지 않음
    };

    // 비밀번호 숨기기/보이기 토글 함수
    const togglePasswordVisibility = () => {
      showPassword.value = !showPassword.value;
    };

    // 이메일 주소 업데이트 로직
    const updateFullEmail = () => {
      if (loginData.value.emailDomain === "custom") {
        loginData.value.email = `${loginData.value.emailPrefix}@${loginData.value.customDomain}`;
      } else {
        loginData.value.email = `${loginData.value.emailPrefix}@${loginData.value.emailDomain}`;
      }
    };

    // 일반 로그인 버튼 클릭 시 함수
    const onClickLoginButton = async () => {
      console.log("로그인 데이터 :", loginData.value);
      if (!loginData.value.email || !loginData.value.password) {
        Swal.fire("입력 오류", "이메일과 비밀번호를 입력해주세요.", "warning");
        return;
      }

      updateFullEmail();
      try {
        let response = await axios.post(`/api/auth/login`, {
          email: loginData.value.email,
          password: loginData.value.password,
        });

        const { accessToken, refreshToken , sleepUsingType, drugUsingType} = response.data;
        let [userId] = loginData.value.email.split("@");
        console.log("@@@ 일반 로그인 응답 response:", response);

        console.log("@@@@ 일반 로그인 토큰 저장");
        localStorage.setItem("userId", userId);
        localStorage.setItem("jwtToken", accessToken);
        localStorage.setItem("accessToken", accessToken); //  오직 accessToken만!
        localStorage.setItem("refreshToken", refreshToken); //  refresh도 따로!
        localStorage.setItem("email", loginData.value.email);
        localStorage.setItem("password", loginData.value.password);

        // localStorage.setItem("sleepUsingType", sleepUsingType);
        // localStorage.setItem("drugUsingType", drugUsingType);

        localStorage.setItem("sleepUsingType", "Y");
        localStorage.setItem("drugUsingType", "Y");
        Swal.fire("로그인 성공", "로그인을 완료했습니다", "success");
        router.push("/diary/common").then(() => {
          location.reload(); // 페이지 새로고침
        });
      } catch (error) {
        console.error("로그인 실패:", error);
        Swal.fire("로그인 실패", error.response?.data?.message || "로그인 실패", "error");
      }
    };

    // 카카오 로그인 함수
    const kakaoLogin = () => {
      if (window.Kakao && window.Kakao.Auth) {
        console.log(" 웹에서는 그냥 페이지 이동!");
        window.Kakao.Auth.login({
          scope: "profile_nickname, account_email, talk_message",
          prompt: "consent",
          success: async function (authObj) {
            try {
              const kakaoAccessToken = authObj.access_token;
              console.log("카카오 AccessToken:", kakaoAccessToken);
              console.log("카카오 로그인 API 호출 시작");
              let response = await axios.post(`/api/kakao/login`, {
                accessToken: kakaoAccessToken,
              }, {
                timeout: 10000,
              });
              const userInfo = response.data.kakaoUserInfo;
              if (!userInfo || !userInfo.email || !userInfo.email.includes("@")) {
                console.warn("이메일 없음 또는 형식 이상", userInfo);
                Swal.fire("로그인 실패", "이메일 정보가 없어 로그인할 수 없습니다.", "error");
                return;
              }


              // console.log("email: ", response.data.kakaoUserInfo.email);
              console.log("nickname: ", response.data.kakaoUserInfo.nickname);
              console.log("jwtToken: ", response.data.jwtToken);
              console.log("response: ", response);
              const jwtToken = response.data.jwtToken;

              if (jwtToken) {
                localStorage.setItem("jwtToken", jwtToken);
              } else {
                console.log("⚠️ 서버에서 JWT 토큰이 안 왔어!");
              }
              let kakao_email = response.data.kakaoUserInfo.email;

              if (kakao_email) {
                console.log("kakao_email:", kakao_email);
              } else {
                console.warn("email이 없습니다.", response.data.kakaoUserInfo);
              }

              let kakao_nickname = response.data.kakaoUserInfo.nickname;
              let kakao_userId = kakao_email.split("@");
              let kakao_token = response.data.jwtToken;
              let [userId] = response.data.kakaoUserInfo.email.split("@");
              console.log("userId:", userId);
              localStorage.setItem("kakaoAccessToken", kakaoAccessToken);
              localStorage.setItem("userId", userId);
              localStorage.setItem("email", kakao_email);
              localStorage.setItem("nickname", kakao_nickname);
              localStorage.setItem("jwtToken", kakao_token);
              Swal.fire("카카오 로그인 성공", "로그인을 완료했습니다", "success");
              // 페이지 이동 (필요하면 추가)
              router.push("/diary/common").then(() => {
                location.reload();
              });
            } catch (error) {
              Swal.fire("로그인 실패", error.response?.data?.message || "카카오 로그인 실패", "error");
              console.error(" 카카오 로그인 실패", error);
            }
          },
          fail: function (err) {
            Swal.fire("로그인 실패", "카카오 로그인 실패", "error");
            console.error(" 로그인 실패", err);
          },
        });
      } else {
        Swal.fire("로그인 실패", "카카오 로그인 실패", "error");
        console.error("카카오 SDK가 아직 로드되지 않았어요!");
      }
    };

    onMounted(() => {
      console.log("로그인 페이지 진입");
      if (!window.Kakao || !window.Kakao.isInitialized()) {
        const kakaoScript = document.createElement("script");
        kakaoScript.src = "https://developers.kakao.com/sdk/js/kakao.js";
        kakaoScript.onload = () => {
          window.Kakao.init(import.meta.env.VITE_KAKAO_JS_KEY);
          console.log("카카오 SDK 로드 완료");
        };
        kakaoScript.onerror = () => {
          console.error(" 카카오 SDK 로드 실패");
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
                   @input="handlePasswordChange" class="login_form-input" name="password" id="password"/>
            <button type="button" @click="togglePasswordVisibility">
              {{ showPassword ? "숨기기" : "보이기" }}
            </button>
            <div class="login_form-oo" v-if="errorWarning.password"
                 :style="{ color: errorWarning.password ? 'red' : 'black' }">
              {{ passwordErrorMessage }}
            </div>
          </div>
        </div>
        <!-- 로그인 버튼 -->
        <div class="login_form_button_div">  <button type="submit" class="login_form_button">로 그 인</button></div>
      </form>
      <button class="kakao-button" @click="kakaoLogin">
        <img class="kakaoLoginImg" src="/img/kakao_login_large_narrow.png" alt="kakaoLogin" />
      </button>
    </div>
  </div>
</template>