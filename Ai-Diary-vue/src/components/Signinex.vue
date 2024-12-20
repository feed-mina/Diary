<template>
    <div class="siginIn_form">
        <div>
            <form>
                <div>
                    <label for="email" class="form-label"></label>
                    <input size="30" placeholder="Email address" type="email" v-model="signInData.email"
                        class="siginIn_form-input" name="email" id="email" aria-describedby="emailHelp" />
                </div>
                <p></p>
                <div>
                    <label for="password" class="form-label"></label>
                    <input size="30" placeholder="Password" type="password" v-model="signInData.password"
                        name="password" class="siginIn_form-input" id="password" />
                </div>
                <div>
                    <span class="text-danger">{{ errorMessage }}</span>
                </div>
                <p></p>
                <button type="button" @click="onClickLoginButton" class="siginIn_form-button">
                    로그인
                </button>
            </form>
        </div>
    </div>
</template>


<script>
imprt {ref, reactive, onMounted } from "vue";
import port from './../data/port.json';
import Cookies from 'universal-cookie';
import axios from "axios";
import { useRouter } from "vue-router";

export default {
    setup(){
        const cookies = new Cookie();
        const router = userRouter();
        const signInData = reactive({
            email : "",
            password: "",
        });

        const errorMessage = ref("");

        // onMounted : react의 useEffect와 유사 onMounted는 컴포넌트 초기 로직을 처리하는 데 사용. React의 [] 의존성 배열은 Vue의 상태 관리와 반응형 시스템으로 대체.
        onMounted(()=>{
            if (!cookies.get("userData")) {
                // 쿠키가 없는 경우
                signInData.email = "";
                signInData.password = "";
                router.push("/");
            } else {
                // 쿠키가 있는 경우
                router.push("/diary/home");
            }
        });

        const onClickLoginButton = async ()=> {
            if(signInData.email === ""){
                alert("이메일을 입력해주세요");
                document.getElementById("email").focus();
            } else if(SignInData.password === ""){
                alert("비밀번호를 입력해주세요.");
                document.getElementById("password").focus();
            }

            try{
                const res = await sendSignInData();
                alert("로그인 성공");
                cookies.set("userData", res.data, {path :"/"});
                router.push("/diary/home");
            } catch(e){
                console.error(e);
                errorMessage.value = e.response? .data?.fail || "로그인 실패";
            }
        };

        const sendSignInData = async () => {
            return await axios.post(`${port.url}/user/login`, {
                email: signInData.email,
                password: signInData.password,
            });
        };
        return {
            signInData,
            errorMessage,
            onClickLoginButton,
        };
    },
};

</script>