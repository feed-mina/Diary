
<script>
import {ref, onMounted} from "vue";
import {useRouter} from "vue-router";
import Cookies from "universal-cookie";
import axios from "axios";
import Swal from "sweetalert2";

export default {
  name: 'EditNewPassword', 
  setup(){
    const router = useRouter();
    const cookies = new Cookies(); 

    const isEditPg = ref(true);   

    //  입력 데이터
    const editData = ref({ 
      newPassword : "", 
      checkNewPassword : "",
    });
    const errorWarning = ref({
      newPassword:  false,
      checkNewPassword:  false,
    });
    const idErrorMessage = ref("");
    const checkNewPasswordErrorMessage = ref("");
    
    // const idValid = ref(true);
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
      if(!editData.value.newPassword){        
        Swal.fire({
              title: "비밀번호 변경",
              text: "비밀번호 형식에 맞지 않습니다.",
              icon: "warning",
              confirmButtonText: "확인",
              confirmButtonColor: "#FF5733"
            });
        return;
      }

      if(!editData.value.checkNewPassword){
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
        const userId = localStorage.getItem("userId");
        const userPassword = localStorage.getItem("password");
        console.log("성공, JWT:", jwtToken);

        // JWT 토큰을 쿠키 또는 localStorage에 저장
        // cookies.set("jwt", jwtToken, { path: "/" });
        // alert("완료했습니다.");
        
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
        const response = await axios.post("http://localhost:8080/api/auth/editPassword", editData.value);
        return response.data; // 응답 데이터를 반환합니다.
      } catch (error) {
        console.error("API 호출 실패:", error.response?.data || error.message);
        throw error; // 예외를 던져서 상위에서 처리하도록 합니다.
      }
};


    

    return{
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
              <input size="30" type="text"  v-model="editData.newPassword" @input="handleIdChange" class="edit_form-input" name="newPassword" id="newPassword" placeholder="새 비밀번호"/>
              <div class="edit_form-oo" :style="{ color: errorWarning.newPassword ? 'red' : 'black' }">
                {{ idErrorMessage }}
              </div>
            </div> 
          </div>
          
          
        <!--패스워드-->
        <div class="edit-session">
           
          <div>
            <input size="30" :type="showPassword ? 'text' : 'password'" v-model="editData.checkNewPassword" @input="handlePasswordChange" class="edit_form-input" name="checkNewPassword"  id="newpassword" placeholder="새 비밀번호 확인"/>
            <button type="button" @click="togglePasswordVisibility">
              {{  showPassword ? '숨기기' : '보기' }}
            </button>
          <div class="edit_form-oo" v-if="errorWarning.checkNewPassword" :style="{ color: errorWarning.checkNewPassword ? 'red' : 'black' }"> 
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


.editPage {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 100%;
    background-color: #f8f9fa;
    padding: 8rem;
    flex-direction: column;
}
.edit_form{
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

.edit_form_box{
    height: 90%;
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
}
.edit-label{
  font-size: 1rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
} 

.edit_form-input{
  width: 100%; /* 필드 너비가 컨테이너에 맞춰짐 */
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 0.5rem;
  box-sizing: border-box;
}

.edit_form-input:focus{
  border: 1px solid #4a90e2;
  outline: none;

}

.edit_form_button{
  width: 100%; /* 버튼이 컨테이너에 맞춰짐 */
  color: var(--int-gray0);
  background: #ddd;
  padding: 0.8rem 1.5rem;
  border-radius: 5px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.edit_form_button:hover{
  background-color: #357abd;
  color : white;

}
.edit_form-oo{
  font-size: 0.9rem;
  color: red;
  margin-top: 0.5rem;
}
 
</style>