
<script>
import {ref, onMounted} from "vue";
import {useRouter} from "vue-router";
import Cookies from "universal-cookie";
import axios from "axios";
import Swal from "sweetalert2";

export default {
  name: 'confirmPassword2', 
  setup(){
    const router = useRouter();
    const cookies = new Cookies(); 

    const goToPage = (path) => {
        router.push(path);
      };
    const isEditPg = ref(true);   

    //  입력 데이터
    const editData = ref({
        checkId: localStorage.getItem("userId"),
        password : "", 
    });
    const errorWarning = ref({
        checkId:  false,
        password:  false,
    });

    const idErrorMessage = ref("");
    const passwordErrorMessage = ref("");
    
    // const passwordValid = ref(true);
    const showPassword = ref(false);
    const togglePasswordVisibility = () =>{
      showPassword.value = !showPassword.value;
    }
    
    const handleEditData = (event) =>{
      editData.value[event.target.name] = event.target.value;
    };
 

    const onClickEditButton = async() => {
      console.log(" 데이터 :", editData.value);
      if(!editData.value.userId){        
        Swal.fire({
              title: "아이디 오류",
              text: "아이디를 입력해주세요",
              icon: "warning",
              confirmButtonText: "확인",
              confirmButtonColor: "#FF5733"
            });
        return;
      }

      if(!editData.value.password){
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
        const userId = localStorage.getItem("userId");
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

            router.push("/").then(() => {
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
        const response = await axios.post("http://localhost:8080/api/auth/checkPassword", editData.value);
        return response.data; // 응답 데이터를 반환합니다.
      } catch (error) {
        console.error("API 호출 실패:", error.response?.data || error.message);
        throw error; // 예외를 던져서 상위에서 처리하도록 합니다.
      }
};

    return{
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
      <h1 class="confirmTitle">비밀번호 인증</h1>
    </header>
  <div  class="layoutContainer">
    <p class="confirmTitle2">개인정보 보호를 위해<br>비밀번호 확인이 필요해요.</p>
        <div class="checkId">
            <input type="text" v-model="editData.checkId" disabled />
        </div>

          <div class="gapCheckInput">
            <div class="password_field">
                <input :type="showPassword? 'text' : 'password'" v-model.trim="editData.checkPassword" @input="handleIdChange"  id="checkPassword" @keyup.enter="onClickConfirmButton" placeholder="비밀번호" />
                <button type="button"  class="password_toggle" @click="togglePasswordVisibility">
              {{  showPassword ? '숨기기' : '보기' }}
            </button>
            </div> 
          </div>

      <!--  버튼 -->
      <div>
        <button type="submit" class="confirmButton" @click="goToPage('/edit/newPassword')" :disabled="!editData.password">확인</button>
      </div>
   </div>
</div>
</template>

<style scoped> 

.confirmHeader {
  text-align: center;
  font-size: 2rem;
  font-weight: bold;
}

.layoutContainer {
  padding: 4rem 2rem;
}

.confirmTitle {
  margin-bottom: 2rem;
  font-size: 1.6rem;
}

.checkId input {
  width: 100%;
  height: 40px;
  font-size: 1.2rem;
  background: #f3f3f3;
  border: 1px solid #ccc;
  padding: 8px;
}

.password_field {
  position: relative;
  display: flex;
  align-items: center;
}

.password_field input {
    width: 100%;
  height: 40px;
  font-size: 1.2rem;
  background: #f3f3f3;
  border: 1px solid #ccc;
  padding: 8px;
}

.password_toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 5px;
  right: 10px;
}

.password_toggle img {
  width: 20px;
  height: 20px;
}

.error-message {
  color: red;
  font-size: 1rem;
  margin-top: 5px;
}

.confirmButton {
  width: 100%;
  height: 40px;
  font-size: 1.2rem;
  background: #357abd;
  color: white;
  border: none;
  cursor: pointer;
  margin-top: 10px;
  border-radius: 5px;
}

/* .confirmButton:disabled {
  background: #aaa;
  cursor: not-allowed;
} */
</style>