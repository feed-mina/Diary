<script>
import { ref, watch } from "vue";
import { useRouter } from "vue-router";
import Cookies from "universal-cookie";
import axios from "axios";
import Swal from "sweetalert2";

export default {
  name: 'MemberOutPage',
  setup() {
    const router = useRouter();
    const cookies = new Cookies();
    const isChecked = ref(false);

    const isMemberOutPg = ref(true);

    const userId = ref(localStorage.getItem("userId")  || "");
    const token = ref(localStorage.getItem("jwtToken")  || "");
    const email = ref(localStorage.getItem("email")  || "");
    const memberOutData = ref({
      email: email,
      token: token,
    });

    const errorWarning = ref({
      email: false,
      password: false,
    });

    const idErrorMessage = ref("");
    const passwordErrorMessage = ref("");

    // 회원탈퇴 버튼 클릭 시 실행되는 함수
    const deleteAccount = () => {
      if (confirm("정말로 회원탈퇴 하시겠습니까?")) {
        localStorage.removeItem("userId");
        localStorage.removeItem("jwtToken");
        localStorage.removeItem("email");
        localStorage.removeItem("password");
        localStorage.removeItem("kakaoAccessToken");
        localStorage.removeItem("nickname");

        alert("회원탈퇴가 완료되었습니다.");
        router.push("/").then(() => {
          location.reload();
        });
      }
    };
    const signout = async () => {
      if (!isChecked.value) {
        Swal.fire({
          title: "확인 필요",
          text: "탈퇴 확인 체크박스를 체크해주세요.",
          icon: "warning",
          confirmButtonText: "확인"
        });
        return;
      }

      if (!memberOutData.value.email) {
        Swal.fire({
          title: "로그인이 필요합니다.",
          text: "로그인이 필요합니다",
          icon: "warning",
          confirmButtonText: "확인",
          confirmButtonColor: "#FF5733"
        }).then(() => {
          router.push("/").then(() => {
            location.reload(); // 새로고침
          });
        });
      }

      try {

        if (confirm("정말로 회원탈퇴 하시겠습니까?")) {
          console.log("회원탈퇴 요청 데이터:", memberOutData.value);
          // API 호출
          const response = await axios.post("http://localhost:8080/api/auth/non-user", memberOutData.value);
          console.log("회원탈퇴 응답: ", response);
          localStorage.removeItem("userId");
          localStorage.removeItem("jwtToken");
          localStorage.removeItem("email");
          localStorage.removeItem("password");
          localStorage.removeItem("kakaoAccessToken");
          localStorage.removeItem("nickname");

          Swal.fire({
            title: "회원탈퇴 성공",
            text: "회원탈퇴가 완료되었습니다.",
            icon: "success",
            confirmButtonText: "확인",
          });
          router.push("/").then(() => {
            location.reload();
          });
        }
      } catch (error) {
        console.error("API 호출 실패:", error.response?.data || error.message);
        Swal.fire({
          title: "회원탈퇴 실패",
          text: "회원탈퇴를 진행할 수 없습니다.",
          icon: "error",
          confirmButtonText: "확인",
        });
      }
    };

    const goToPage = (path) => {
      router.push(path);
    };

    // watch(() => cookies.get("jwt"), () => {});
    return {
      isChecked,
      signout,
      goToPage,
      isMemberOutPg,
      memberOutData,
      errorWarning,
      idErrorMessage,
      passwordErrorMessage
    };
  },
};
</script>

<template>
  <div class="memberOutPage">
    <div id="memberOut_form" class="memberOut_form">
      <section>
        <h2 class="title">불편함이 있으셨나요?</h2>
        <p class="subtitle">아래 방법을 통해 해결하실 수 있습니다.</p>
        <div class="button-group">
          <button type="button" class="button" @click="goToPage('/mypage/confirmPassword')">개인정보 수정</button>
          <button type="button" class="button" @click="goToPage('/edit/password')">비밀번호 변경</button>
        </div>
      </section>

      <section>
        <h2 class="title">꼭 확인해 주세요.</h2>
        <ul class="info-list">
          <li>등록된 게시물은 탈퇴 후에도 유지됩니다.</li>
          <li>관계법령에 따른 개인정보는 탈퇴 후 5년간 보존됩니다.</li>
          <li>5년간 기존 아이디를 재사용할 수 없습니다.</li>
        </ul>
      </section>

      <section>
        <label class="checkbox-container">
          <input type="checkbox" v-model="isChecked"/>
          <p>위 사항을 모두 확인했습니다.</p>
        </label>
      </section>

      <div class="button-container">
        <button type="button" :disabled="!isChecked" class="button warning" @click="signout">탈퇴 하기</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.memberOutPage {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f8f9fa;
  padding: 2rem;
  flex-direction: column;
}

.memberOut_form {
  display: flex;
  flex-direction: column;
  background-color: #fff;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 500px;
}

.title {
  color: #333;
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.subtitle {
  color: #555;
  font-size: 1rem;
  margin-bottom: 1.5rem;
}

.button-group {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.button {
  flex: 1;
  background-color: #4a90e2;
  color: white;
  border: none;
  padding: 0.8rem 1.2rem;
  border-radius: 5px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
}

.button:hover {
  background-color: #357abd;
}

.button.warning {
  background-color: #ff5733;
}

.button.warning:hover {
  background-color: #d43f00;
}

.info-list {
  list-style-type: none;
  padding: 0;
  color: #555;
}

.info-list li {
  margin-bottom: 1rem;
}

.checkbox-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1rem;
  margin-top: 1rem;
}

.button-container {
  margin-top: 1.5rem;
}
</style>
