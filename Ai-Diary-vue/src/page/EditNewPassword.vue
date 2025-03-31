<script>
import {ref} from "vue";
import {useRouter} from "vue-router";
import Cookies from "universal-cookie";
import axios from "axios";
// import { apiUrl } from "@/api/index.js";
import Swal from "sweetalert2";

export default {
  name: 'EditNewPassword',
  setup() {
    const router = useRouter();
    const cookies = new Cookies();

    const isEditPg = ref(true);

    const token = ref(localStorage.getItem("jwtToken")  || "");
    const email = ref(localStorage.getItem("email")  || "");
    //  입력 데이터
    const editData = ref({
      email: email,
      token: token,
      newPassword: "",
      checkNewPassword: "",
    });
    const errorWarning = ref({
      newPassword: false,
      checkNewPassword: false,
    });
    const idErrorMessage = ref("");
    const checkNewPasswordErrorMessage = ref("");

    const showPassword = ref(false);
    const togglePasswordVisibility = () => {
      showPassword.value = !showPassword.value;
    }

    const handleEditData = (event) => {
      editData.value[event.target.name] = event.target.value;
    };


    const onClickEditButton = async () => {
      console.log(" 데이터 :", editData.value);
      if (!editData.value.newPassword) {
        Swal.fire({
          title: "비밀번호 변경",
          text: "비밀번호 형식에 맞지 않습니다.",
          icon: "warning",
          confirmButtonText: "확인",
          confirmButtonColor: "#FF5733"
        });
        return;
      }

      if (!editData.value.checkNewPassword) {
        // alert("비밀번호를 입력해주세요.");

        Swal.fire({
          title: "비밀번호 변경",
          text: "비밀번호가 일치하지 않습니다.",
          icon: "warning",
          confirmButtonText: "확인",
          confirmButtonColor: "#FF5733"
        });
        return;
      }

      try {
        // API 호출 및 응답 처리
        const jwtToken = await sendEditData();
        console.log("성공, JWT:", jwtToken);

        Swal.fire({
          title: "성공",
          text: "비밀번호가 변경되었습니다.",
          icon: "success",
          confirmButtonText: "확인",
          confirmButtonColor: "#357abd",
        });

        router.push("/").then(() => {
          location.reload(); // 새로고침
        });
      } catch (error) {
        // 에러 처리
        console.error(" 실패:", error);

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

        if (confirm("비밀번호를 변경하시겠습니까?")) {
          console.log("회원탈퇴 요청 데이터:", editData.value);
          // API 호출
          const response = await axios.post(`/api/auth/editPassword`, editData.value);
          console.log("비밀번호 변경 응답: ", response);
          localStorage.removeItem("userId");
          localStorage.removeItem("jwtToken");
          localStorage.removeItem("email");
          localStorage.removeItem("password");
          localStorage.removeItem("kakaoAccessToken");
          localStorage.removeItem("nickname");

          Swal.fire({
            title: "비밀번호 변경 성공",
            text: "다시 로그인을 해주세요.",
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
          title: "비밀번호 변경 실패",
          text: "비밀번호 변경을 진행할 수 없습니다.",
          icon: "error",
          confirmButtonText: "확인",
        });
      }
    };

    return {
      isEditPg,
      editData,
      errorWarning,
      showPassword,
      handleEditData,
      idErrorMessage,
      checkNewPasswordErrorMessage,
      togglePasswordVisibility,
      onClickEditButton
    };

  },
};
</script>

<template>
  <div class="editPage">
    <div id="edit_form" class="edit_form">
      <!--폼 렌더링-->
      <!-- 폼-->
      <form @submit.prevent="onClickEditButton" class="edit_form_box">

        <!--ID-->
        <div class="edit-session">
          <div class="edit-label">
            <label for="id" class="form-label">새 비밀번호</label>
          </div>
          <div>
            <input size="30" type="text" v-model="editData.newPassword" @input="handleIdChange" class="edit_form-input"
                   name="newPassword" id="newPassword" placeholder="새 비밀번호"/>
            <div class="edit_form-oo" :style="{ color: errorWarning.newPassword ? 'red' : 'black' }">
              {{ idErrorMessage }}
            </div>
          </div>
        </div>


        <!--패스워드-->
        <div class="edit-session">

          <div>
            <input size="30" :type="showPassword ? 'text' : 'password'" v-model="editData.checkNewPassword"
                   @input="handlePasswordChange" class="edit_form-input" name="checkNewPassword" id="newpassword"
                   placeholder="새 비밀번호 확인"/>
            <button type="button" @click="togglePasswordVisibility">
              {{ showPassword ? '숨기기' : '보기' }}
            </button>
            <div class="edit_form-oo" v-if="errorWarning.checkNewPassword"
                 :style="{ color: errorWarning.checkNewPassword ? 'red' : 'black' }">
              {{ passwordErrorMessage }}
            </div>
            <span>
          영문/숫자/특수문자를 포함하여 8~12자로 입력해주세요.
        </span>
          </div>
        </div>

        <!--  버튼 -->
        <div>
          <button type="submit" class="edit_form_button">비밀번호 변경</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>


</style>