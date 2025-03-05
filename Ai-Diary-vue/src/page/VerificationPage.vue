<script>
import axios from "axios";
export default {
  name: 'VerificationPage',
  setup(){
    const codeData= ref({
        email:"",
        code:"",
        message:"",
    });

    const verifyCode = async() =>{
        try{
            const response = await axios.post("http://localhost:8080/api/auth/verify", null, {
            params: { email: codeData.email, code: codeData.code },
            });
            codeData.message = response.data;
        } catch(error){
            console.error(error);
            this.message = "오류 발생!";
        }
    }
    return{
        codeData,
        verifyCode
    }
  }
};
</script>

<template>
  <div>
    <h1>이메일 인증</h1>
    <form @submit.prevent="verifyCode">
        <div>
            <label for="email">이메일 : </label>
            <input type="email" v-model="codeData.email" required/>
        </div>
        <div>
            <label for="code" class="code">
                인증 코드:
            </label>
            <input type="text" v-model="codeData.code" required/>
        </div>
        <button type="submit">인증</button>
    </form>
    <p v-if="codeData.message">{{ codeData.message }}</p>
  </div>
</template>
