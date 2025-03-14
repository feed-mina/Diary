<script>
import {ref} from "vue";
import {useRouter} from "vue-router";
import Cookies from "universal-cookie";
import axios from "axios";
import Swal from "sweetalert2";

export default {
  name: 'EditPassword',
  setup() {
    const router = useRouter();
    const cookies = new Cookies();

    const goToPage = (path) => {
      router.push(path);
    };
    const isEditPg = ref(true);

    //  입력 데이터
    const editData = ref({
      userId: "",
      password: "",
    });
    const errorWarning = ref({
      userId: false,
      password: false,
    });
    const idErrorMessage = ref("");
    const passwordErrorMessage = ref("");

    // const idValid = ref(true);
    // const passwordValid = ref(true);
    const showPassword = ref(false);
    const togglePasswordVisibility = () => {
      showPassword.value = !showPassword.value;
    }

    const handleEditData = (event) => {
      editData.value[event.target.name] = event.target.value;
    };


    const onClickEditButton = async () => {
      console.log(" 데이터 :", editData.value);
      if (!editData.value.userId) {
        Swal.fire({
          title: "아이디 오류",
          text: "아이디를 입력해주세요",
          icon: "warning",
          confirmButtonText: "확인",
          confirmButtonColor: "#FF5733"
        });
        return;
      }

      if (!editData.value.password) {
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
        const jwtToken = await sendEditData();
        const userId = localStorage.getItem("email");
        const userPassword = localStorage.getItem("password");
        console.log("성공, JWT:", jwtToken);

        // JWT 토큰을 쿠키 또는 localStorage에 저장
        // cookies.set("jwt", jwtToken, { path: "/" });
        // alert("완료했습니다.");

        Swal.fire({
          title: "성공",
          text: "비밀번호가 맞습니다",
          icon: "success",
          confirmButtonText: "확인",
          confirmButtonColor: "#357abd",
        });

        router.push("/diary/common").then(() => {
          location.reload(); // 새로고침
        });
      } catch (error) {
        // 에러 처리
        console.error(" 실패:", error);
        // alert(error.response?.data?.message || "에 실패했습니다.");

        Swal.fire({
          title: " 실패",
          text: "비밀번호가 다릅니다.",
          icon: "warning",
          confirmButtonText: "확인",
          confirmButtonColor: "#FF5733"
        });
      }
    };


    //  API 호출
    const sendEditData = async () => {
      try {
        const response = await axios.post("http://localhost:8080/api/auth/edit", editData.value);
        return response.data; // 응답 데이터를 반환합니다.
      } catch (error) {
        console.error("API 호출 실패:", error.response?.data || error.message);
        throw error; // 예외를 던져서 상위에서 처리하도록 합니다.
      }
    };

    return {
      isEditPg,
      goToPage,
      editData,
      errorWarning,
      showPassword,
      handleEditData,
      idErrorMessage,
      passwordErrorMessage,
      togglePasswordVisibility,
      onClickEditButton
    };

  },
};
</script>

<template>
  <div class="editPage">
    <header class="confirmHeader">
      <h1>비밀번호 인증</h1>
    </header>
    <div id="edit_form" class="edit_form">
      <h2 class="confirmTitle">개인정보 보호를 위해<br>비밀번호 확인이 필요해요.</h2>

      <!--폼 렌더링-->
      <!-- 폼-->

      <div class="edit-session">
        <div class="edit-label">
          <label for="password" class="form-label">현재비밀번호</label>
        </div>
        <!-- <div>
          <input size="30" type="text"  v-model="editData.userId" @input="handleIdChange" class="edit_form-input" name="userId" id="userId"/>
          <div class="edit_form-oo" :style="{ color: errorWarning.userId ? 'red' : 'black' }">
            {{ idErrorMessage }}
          </div>
        </div> -->
      </div>


      <!--패스워드-->
      <div class="edit-session">
        <!-- <div class="edit-label">
          <label  for="password" class="form-label">Password</label>
        </div> -->
        <div>
          <input size="30" :type="showPassword ? 'text' : 'password'" v-model.trim="editData.password"
                 @input="handlePasswordChange" class="edit_form-input" name="password"
                 @keyup.enter="onClickConfirmButton" id="password" placeholder="현재 비밀번호"/>
          <button type="button" class="password_toggle" @click="togglePasswordVisibility">
            {{ showPassword ? '숨기기' : '보기' }}
          </button>
          <!-- <div class="edit_form-oo" v-if="errorWarning.password" :style="{ color: errorWarning.password ? 'red' : 'black' }"> 
            {{ passwordErrorMessage }}
        </div> -->
          <p v-if="passwordErrorMessage" class="error-message">
            {{ passwordErrorMessage }}
          </p>
        </div>
      </div>

      <!--  버튼 -->
      <div>
        <button type="submit" class="edit_form_button" @click="goToPage('/edit/newPassword')"
                :disabled="!editData.password">확인
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>


.editPage {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  background-color: #f8f9fa;
  padding: 8rem;
  flex-direction: column;
}

.edit_form {
  display: flex;
  height: 100%;
  width: 100%;
  padding: 5rem;
  background-color: #f9f9f9;
  border-radius: 10px;
  /* margin: auto; */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  justify-content: flex-start;
  flex-direction: column;
  margin-bottom: 1.5rem;
  margin-bottom: 1.5rem; /* 필드 간 여백 */
}

.edit_form_box {
  height: 90%;
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
}

.edit-label {
  font-size: 1rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.edit_form-input {
  width: 100%; /* 필드 너비가 컨테이너에 맞춰짐 */
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 0.5rem;
  box-sizing: border-box;
}

.edit_form-input:focus {
  border: 1px solid #4a90e2;
  outline: none;

}

.edit_form_button {
  width: 100%; /* 버튼이 컨테이너에 맞춰짐 */
  color: var(--int-gray0);
  background: #ddd;
  padding: 0.8rem 1.5rem;
  border-radius: 5px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.edit_form_button:hover {
  background-color: #357abd;
  color: white;

}

.edit_form-oo {
  font-size: 0.9rem;
  color: red;
  margin-top: 0.5rem;
}

</style>