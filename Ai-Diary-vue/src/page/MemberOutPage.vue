<script>
   import {ref, onMounted,watch} from "vue";
   import {useRouter} from "vue-router";
   import Cookies from "universal-cookie";
   import axios from "axios";
   import Swal from "sweetalert2";

   export default {
     name: 'MemberOutPage', 
     setup(){
       const router = useRouter();
       const cookies = new Cookies(); 
       const isChecked = ref(false);

       const isMemberOutPg = ref(true);   
       const memberOutData = ref({
         userId:"",
         password : "", 
       });

       const errorWarning = ref({
         userId:  false,
         password:  false,
       });

       const idErrorMessage = ref("");
       const passwordErrorMessage = ref("");
   
       const signout = async () => {
         if(!isChecked.value){
            Swal.fire({
               title: "확인 필요",
               text : "탈퇴 확인 체크박스를 체크해주세요.",
               icon : "warning",
               confirmButtonText : "확인"
            });
            return;
         }

         if(!memberOutData.value.userId){
           // alert("아이디를 입력해주세요.");
           Swal.fire({
              title: "아이디 오류",
              text: "아이디를 입력해주세요",
              icon: "warning",
              confirmButtonText: "확인",
              confirmButtonColor: "#FF5733"
            });
           return;
         }
   
         try {
            console.log("회원탈퇴 요청 데이터:", memberOutData.value);

            // API 호출
           const response = await axios.post("http://localhost:8080/api/auth/non-user", memberOutData.value);
         console.log("회원탈퇴 응답: ", response);
           // 성공 시 처리
           Swal.fire({
            title:"회원탈퇴 성공",
            text:"회원탈퇴가 완료되었습니다.",
            icon:"success",
            cancelButtonText:"확인",
           }).then(() =>{
            console.log("회원탈퇴 성공");
            localStorage.removeItem("userId"); //로컬스토리지에서 삭제
            cookies.remove("jwt",{path : "/"}); // JWT삭제
            router.push("/");
           });
         } catch (error) {
           console.error("API 호출 실패:", error.response?.data || error.message);
           Swal.fire({
            title:"회원탈퇴 실패",
            text:"회원탈퇴를 진행할 수 없습니다.",
            icon:"error",
            cancelButtonText:"확인",
           });
         }
       }
       
       const goToPage = (path) => {
        router.push(path);
      };

       const onClickMemberOutButton = async() => {
         console.log("회원 데이터 :", memberOutData.value);
         if(!memberOutData.value.password){
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
   };
   
    // JWT 쿠키 상태를 실시간으로 감지
    watch(
      () => cookies.get("jwt"), 
      ()=>{
    });

       return{
         isChecked,
         signout,
        goToPage,
         isMemberOutPg,
         memberOutData, 
         errorWarning, 
         idErrorMessage,
         passwordErrorMessage, 
         onClickMemberOutButton
       };
   
      },
   };
</script>
<template>
   <div class="memberOutPage">
      <div id="memberOut_form" class="memberOut_form">
         <!--회원탈퇴 페이지 -->
                <section>
                  <h2 class="sc-ceed69bd-1 iATcQ">불편함이 있으셨나요?</h2>
                  <p class="sc-ceed69bd-2 fBhHYS">아래 방법을 통해 해결하실 수 있습니다.</p>
                  <div class="sc-ceed69bd-3 isFuqM">
                     <!--           
                        <button type="button" class="base_ButtonRoot-sc-206d6909-0 dhVHxS button_StyledButtonBase3-sc-43279e9a-2 jCkVaj">이메일 수신거부</button>
                        
                        <button type="button" class="base_ButtonRoot-sc-206d6909-0 dhVHxS button_StyledButtonBase3-sc-43279e9a-2 jCkVaj">SMS 수신거부</button>
                         -->
                         <button type="button" class="base_ButtonRoot button_StyledButtonBase3 memberOut_form_button" @click="goToPage('/mypage/confirmPassword')">개인정보 수정</button>

                         <button type="button" class="base_ButtonRoot button_StyledButtonBase3 memberOut_form_button" @click="goToPage('/edit/password')">비밀번호 변경</button>
                        
                        <!-- <button type="button" class="base_ButtonRoot  button_StyledButtonBase3 memberOut_form_button" @click="goToPage('/edit/password')">비밀번호 변경</button> -->

                  </div>
               </section>
               <!-- <div role="separator" aria-orientation="horizontal" class="divider__Divider-sc-b8ad0d88-0 IAFTP"></div> -->
               <!-- 
                  <section>
                     <h2 class="sc-ceed69bd-1 iATcQ">탈퇴하면 아래 혜택이 모두 사라져요.</h2>
                  
                     <div class="sc-b53b0099-0 cyJDtp">
                        <div class="sc-b53b0099-1 jMfhZe">
                          <span>보유포인트</span>
                          <span class="sc-b53b0099-2 bAASHO">즉시 소멸</span>
                          <span class="sc-b53b0099-3 dLyBXk">0P</span>
                        </div>
                        
                        <div class="sc-b53b0099-1 jMfhZe">
                          <span>보유 쿠폰</span>
                          <span class="sc-b53b0099-2 bAASHO">즉시 소멸</span>
                          <span class="sc-b53b0099-3 dLyBXk">0매</span>
                        </div> 
                     </div>
                  
                  </section>
                  <div role="separator" aria-orientation="horizontal" class="divider__Divider-sc-b8ad0d88-0 IAFTP"></div>
                   -->
               <section>
                  <h2 class="sc-ceed69bd-1 iATcQ">꼭 확인해 주세요.</h2>
                  <ul class="sc-ceed69bd-4 hzNdZv">
                     <!-- <li class="sc-ceed69bd-5 edlgNH">적립금과 혜택은 모두 소멸되며 재가입시 복구되지 않습니다.</li> -->
                     <li class="sc-ceed69bd-5 cyJDtp"> 등록된 게시물은 탈퇴 후에도 유지됩니다. <br>삭제를 원하는 경우, 먼저 해당 게시글을 삭제하신 후 탈퇴해 주세요.</li>
                     <li class="sc-ceed69bd-5 cyJDtp">관계법령에 따른 개인정보는 탈퇴 후 5년간 보존됩니다.</li>
                     <li class="sc-ceed69bd-5 cyJDtp">5년간 기존 아이디를 재사용 하실 수 없습니다.</li>
                  </ul>
               </section>
               <section>
                  <label gap="8" class="checkbox_Label">
                     <div class="checkbox-base_CheckboxBaseRoot khGpkv">
                        <input type="checkbox" class="checkbox-base__CheckboxSquareInput" v-model="isChecked" />

                        <p class="text_Text">위 사항을 모두 확인했습니다.</p>
                     </div>
                  </label>
               </section>
               <div class="sc-eda81c26-0 isFuqM">
                  <!-- <button type="button" class="base_ButtonRoot button_StyledButtonBase3 memberOut_form_button" @click="navigateTo('/mypage')">
                  더 써볼래요
                  </button> -->
                  <button type="button" :disabled="!isChecked" class="base_ButtonRoot button_StyledButtonBase2 memberOut_form_button" @click="signout">
                  탈퇴 하기
                  </button>
               </div>
            </div>
         <!-- 회원탈퇴 페이지-->
      </div>
</template>
<style scoped> 
   /* 
   .memberOutPage {
   display: flex;
   justify-content: space-between;
   align-items: center;
   height: 100%;
   background-color: #f8f9fa;
   padding: 8rem;
   flex-direction: column;
   }
   .memberOut_form{
   display: flex;
   height: 100%;
   width: 100%;
   padding: 5rem;
   background-color: #f9f9f9;
   border-radius: 10px;
   box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
   justify-content: flex-start;
   flex-direction: column;
   margin-bottom: 1.5rem; 
   margin-bottom: 1.5rem;  
   } */
   .khGpkv {
   position: relative;
   display: flex;
   align-items: center;
   justify-content: center;
   padding-top: 2rem;
   padding-bottom: 2rem;
   font-size: 20px;
   }
   .gwUtMK {
   position: fixed;
   top: 0px;
   left: 0px;
   right: 0px;
   display: flex;
   justify-content: center;
   align-items: center;
   height: 5rem;
   background: var(--int-gray0);
   }
   .ctrbmY {
   height: 5rem;
   }
   .VpfJj {
   margin-left: 1rem;
   padding-bottom: 8rem;
   }
   .iATcQ {
   margin-top: 2rem;
   color: var(--int-gray900);
   font-size: 1.5rem;
   line-height: 1.5;
   font-weight: 700;
   }
   .isFuqM {
   margin-top: 1rem;
   display: flex;
   flex-wrap: wrap;
   gap: 2rem 0.8rem;
   }
   .isFuqM button {
   width: calc(50% - 0.4rem);
   padding: 1.2rem 0px;
   box-sizing: border-box;
   border-radius: 1.2rem;
   font-size: 1.6rem;
   line-height: 1.5;
   font-weight: 700;
   color: var(--int-gray900);
   cursor: pointer;
   }
   .IAFTP {
   flex-grow: 1;
   flex-shrink: 1;
   height: 8px;
   background-color: rgb(240, 244, 244);
   }
   .cyJDtp {
   display: flex;
   flex-direction: column;
   margin-top: 1rem;
   } 
   .coMdzT {
   position: absolute;
   inset: 0px;
   margin: auto;
   pointer-events: none;
   color: var(--int-gray0);
   opacity: 0;
   width: 14px;
   height: 14px;
   }
   .fsklAP {
   box-sizing: border-box;
   line-height: 1.5;
   letter-spacing: 0px;
   overflow-wrap: break-word;
   white-space: pre-line;
   word-break: normal;
   color: var(--int-gray900);
   font-size: 1.6rem;
   font-weight: 700;
   }
   .inayCG {
   /* position: fixed; */
   right: 0px;
   bottom: 0px;
   left: 0px;
   z-index: var(--int-zindex-floating);
   display: flex
   ;
   gap: 1rem;
   width: 100%;
   height: var(--floating-button-height);
   padding: 1.6rem 2rem;
   background-color: var(--int-gray0);
   box-sizing: border-box;
   }
   .inayCG > button, .inayCG > div {
   flex: 1 1 50%;
   }
   .jCkVaj {
   color: var(--int-gray900);
   background-color: var(--int-gray0);
   border: 1px solid var(--int-gray500);
   }
   .dhVHxS:disabled {
   color: var(--int-gray0);
   background: var(--int-gray500);
   border-color: transparent;
   }
   .inayCG > button, .inayCG > div {
   flex: 1 1 50%;
   } 
   .memberOut_form_box{
   height: 90%;
   display: flex;
   flex-direction: column;
   justify-content: space-evenly;
   }
   .memberOut-label{
   font-size: 1rem;
   font-weight: bold;
   margin-bottom: 0.5rem;
   } 
   .memberOut_form-input{
   width: 100%; /* 필드 너비가 컨테이너에 맞춰짐 */
   border: 1px solid #ddd;
   border-radius: 5px;
   padding: 0.5rem;
   box-sizing: border-box;
   }
   .memberOut_form-input:focus{
   border: 1px solid #4a90e2;
   outline: none;
   }
   .memberOut_form_button{
   width: 100%; /* 버튼이 컨테이너에 맞춰짐 */
   background-color: #4a90e2;
   color: white;
   border: none;
   padding: 0.8rem 1.5rem;
   border-radius: 5px;
   font-size: 1rem;
   cursor: pointer;
   transition: background-color 0.3s ease;
   }
   .memberOut_form_button:hover{
   background-color: #357abd;
   color: white;
   }
   .memberOut_form-oo{
   font-size: 0.9rem;
   color: red;
   margin-top: 0.5rem;
   }
</style>