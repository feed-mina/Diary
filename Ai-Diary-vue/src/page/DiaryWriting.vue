<script>
import axios from "axios";
import { ref, onMounted } from 'vue'; 
import {useRouter} from "vue-router";
import Cookies from 'universal-cookie'; // universal-cookie
import Datepicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';

export default {
  name: 'DiaryWriting', // 다중 단어 이름으로 변경
  components: {
    Datepicker,
  },
    setup() {
      const router = useRouter();
    // 쿠키 객체 생성
    const cookies = new Cookies();
    
    const diaryContentData =  ref({
      date:"",
      author:"",
      title:"",
      tags:({ tag1: "", tag2: "", tag3: "" }),
      emotion:"",
      content:"",
      hidden:true,
    });

    const diaryVisibility = ref({
      diaryContentData: false,
    });

    const emotionItems = [
      { text: "😁 I feel good", value: "1" },
      { text: "😂 Oh, That's so funny", value: "2" },
      { text: "😫 What should I do?!", value: "3" },
      { text: "😒 unpleasant, boring", value: "4" },
      { text: "😤 How dare you", value: "5" },
      { text: "😡 Angry", value: "6" },
      { text: "🤯 I wanna get outta here...", value: "7" },
      { text: "💖 Love", value: "8" },
      { text: "🤕 Not in a good condition", value: "9" },
      { text: "💙 I feel blue", value: "10" }
    ];

    axios.interceptors.request.use(
  (config) => {
    console.log("Axios 요청 설정:", config);
    return config;
  },
  (error) => {
    console.error("Axios 요청 에러:", error);
    return { success: false, error: error.response?.data || "오류가 발생했습니다." };
  }
);

axios.interceptors.response.use(
  (response) => {
    console.log("Axios 응답 데이터:", response);
    return response;
  },
  (error) => {
    console.error("Axios 응답 에러:", error);
    return Promise.reject(error);
  }
);

    const sendDiaryContentData = async()=>{
      try{
        const { title, date, author, tags, emotion, content, hidden} = diaryContentData.value; 
        
            // 값 검증
    if (!date) {
      alert("날짜를 입력해주세요.");
      return;
    }
    if (!author) {
      alert("작성자를 입력해주세요.");
      return;
    }
    if (!title) {
      alert("제목을 입력해주세요.");
      return;
    }
    if (!emotion) {
      alert("감정지수를 선택해주세요.");
      return;
    }
    if (!content) {
      alert("내용을 입력해주세요.");
      return;
    }
    // 값 검증
    if (!date || !author || !title || !emotion || !content) {
      alert("필수 필드를 채워주세요.");
      return;
    }
        const diaryDataToSave = {
          pageNo: 1,
          pageSize: 10,
          title,
          author,
          emotion,
          userId : localStorage.getItem("userId"),
          date,
          content,
          tag1 : tags.tag1,
          tag2 : tags.tag2,
          tag3 : tags.tag3,
          diaryStatus: hidden ? "true" : "false", // Boolean을 문자열로 변환
        }
        console.log('diaryDataToSave',diaryDataToSave);
        const jwtToken = cookies.get("jwt")?.jwt; // 쿠키에서 jwt 속성 가져오기
        console.log("jwtToken: " , jwtToken);
        if (!jwtToken) {
          alert("JWT 토큰이 없습니다. 다시 로그인해주세요.");
          router.push("/login");
          return;
      //throw new Error("JWT 토큰이 없습니다.");
    }

        const response = await axios.post("http://localhost:8080/api/diary/addDiaryList", diaryDataToSave,{
          headers: {
            Authorization: `Bearer ${jwtToken}`,
            "Content-Type": "application/json",
            "X-Forwarded-For": "127.0.0.1",
          },
          withCredentials: true, // 쿠키 인증 허용

        });
        
        console.log("jwtToken: " , jwtToken);
        console.log('response',response);
      return response.data;
      }catch(error) {
        if (error.response && error.response.status === 401) {
        alert("세션이 만료되었습니다. 다시 로그인해주세요.");
        router.push("/login"); // 로그인 페이지로 이동
      } else {
      console.error("API 호출 실패:", error);
      alert("일기 저장에 실패했습니다. 관리자에게 문의해주세요.");
      
      return { success: false, error: "서버 오류가 발생했습니다." }; // 실패 메시지 반환
        }
      }
    } ;
  

  const onClicksaveDiaryButton = async () => {
    const result =  await sendDiaryContentData();
    console.log(result)
    alert("일기가 저장되었습니다.");
    router.push("/diary/common").then(() => location.reload());
    if (result && result.error) {
      alert("저장 실패: " + result.error);
    
    router.push("/").then(() => location.reload());
  }  
  };
    return {
      emotionItems,
      diaryContentData,
      onClicksaveDiaryButton,
      diaryVisibility
    };
  },
}
</script>


<template>
  <div class="diaryWriting">
    <div class="diaryWriting_content">
      <div class="diaryTuto">
        <div class="diaryWriting_container">
          <form>
            <div class="diaryWritingTitle">
                <span class="diaryWritingHighlight">
                </span>
            </div>
            <div class="diaryWriting_noDalle">
              <div class="section0">
                <div><span>일기 날짜</span>
                  <div>
                    <Datepicker
                    v-model="diaryContentData.date"
                    :format="'yyyy-MM-dd'"
                    :auto-apply="true"
                    :locale="'ko'"
                  />
                  </div>
                </div>
              </div>
              <div class="section01">
                <div>
                    <div>
                  <label for="author">작성자&nbsp;&nbsp;&nbsp;</label>
                  <input type="text" class="author" id="author" name="author" v-model="diaryContentData.author" placeholder=""  />
                    </div>
                    <div class="titleSc">
                      <label for="title">제목&nbsp;&nbsp;&nbsp;</label>
                  <input type="text" class="title" id="title" name="title" v-model="diaryContentData.title"  />
                    </div>
                </div>
              </div>
              <!--section2-->
              <div class="section2">
                <div class="text">
                    <span>오늘의 감정을 태그로 입력하세요.</span>
                </div>
                <div class="tags">
                    <input type="text" id="tag1" name="tag1" v-model="diaryContentData.tags.tag1" placeholder="tag1" />

                    <input type="text" id="tag2" name="tag2" v-model="diaryContentData.tags.tag2" placeholder="tag2" />

                    <input type="text" id="tag3" name="tag3" v-model="diaryContentData.tags.tag3" placeholder="tag3" />
                  <!-- <button type="button" class="aiButton">
                      <span>AI 이미지 생성</span>
                  </button> -->
                </div>
              </div>
              <!--section3-->
              <div class="section3">
                <div class="text">
                      <span>감정지수를 선택하세요.</span>
                </div>
                <div class="selectBox">
                  <v-select
                  v-model="diaryContentData.emotion"
                  :items="emotionItems"
                  item-title="text"
                  item-value="value"
                  label="오늘의 감정지수는?"
                  class="emotion"
                  id="emotion"
                  required
                  ></v-select>
                </div>
              </div>
              <!--section4-->
              <div class="section4">
                <div class="text">
                      <span>본문</span>
                </div>
                <textarea v-model="diaryContentData.content" rows="3" class="content" name="content" id="content" ></textarea>
              </div>
              <!--section05-->
              <div class="section05">
                      <span>🔎</span>
                    <select v-model="diaryContentData.hidden" id="hidden" required>
                      <option value="true">
                        숨기기
                      </option>
                      <option value="false">
                        보여주기
                      </option>
                    </select>
                    <div>
                      <button type="button" @click="onClicksaveDiaryButton">일기장완료</button>
                    </div>
              </div>

              <!--diaryTuto-dalle-->
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.diaryWriting{
    width: 99%;
    /**height: 75%; */
    min-width: 25em;
    height: 100%;
    margin: 0 auto;
    border-radius: 0.625em;
    overflow: hidden;
}

.diaryWriting_content {
    /* height: 100%; */
    width: 99%;
    top: 1.875em;
    right: 0;
    bottom: 33.75em;
    left: 3.75em;
    background-size: 30px 30px;
  }

  .diaryTuto {
    padding-top: 1.5em;
    width: 100%;
    height: 100%;
    font-size: 2vmin;
  }
  .titleSc{
    padding-top:2rem;
  }
  .diaryTuto input,
  textarea,
  button {
    border-radius: 0.3125em;
    background: #eee7db;
    border: 0 solid black;
    font-size: 0.9375em;
  }
  .diaryWriting_container {
    /* border: 1px solid #00fa9a; */
    margin: 0;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: row;
  }

  .diaryWritingTitle {
    font-size: 2rem;
    text-align: center;
    font-weight: bold;

  }
  .diaryWritingTitle {
    box-shadow: inset 0 -23px 0 #e89b3d;
    display: inline;
  }

  .diaryWriting_container form {
    width: 100%;
    height: 100%;
  }
  .diaryWriting_noDalle {
    width: 65%;
    height: 100%;
    margin: 0em;
    float: left;
  }
  .diaryWriting_noDalle input,
  select,
  button,
  option {
    height: 1.875em;
  }

  .diaryWriting_container input:focus {
    outline: 2px solid #c1ab86;
    transition: 0.1s;
  }


  .diaryWriting_noDalle .section0 {
    width: 100%;
    display: flex;
    flex-direction: row;
    margin-bottom: 0.9375em;
  }
  .diaryWriting_noDalle .section01 {
    margin-top: 0.125em;
    width: 100%;
    display: flex;
    flex-direction: row;
    margin-bottom: 0.9375em;
  }

  .diaryWriting_noDalle .section01 input {
    width: 40%;
    text-align: center;
  }

 .diaryWriting_noDalle .section1 input:hover {
    transition: 0.3s;
    opacity: 1;
    outline: 2px solid #c1ab86;
    width: 42%;
  }

 .diaryWriting_noDalle .section2 {
    width: 100%;
    margin-bottom: 0.625em;
  }
 .diaryWriting_noDalle .section2 .tags {
    padding-top: 0.625em;
    width: 95%;
    display: flex;
    flex-direction: row;
    justify-content: space-evenly;
  }
 .diaryWriting_noDalle .section2 input {
    text-align: center;
    width: 25%;
    margin: 0.3125em;
    caret-color: #604e2e;
  }
 .diaryWriting_noDalle .section2 input:hover {
    transition: 0.3s;
    opacity: 1;
    outline: 2px solid #c1ab86;
    width: 27%;
  }
 .diaryWriting_noDalle .section2 button {
    width: 25%;
    margin: 0.3125em;
    height: 2em;
    background: #c1ab86;
  }
 .diaryWriting_noDalle .section2 button:hover {
    transition: 0.3s;
    opacity: 1;
    background: #604e2e;
    color: white;
    width: 30%;
  }
 .diaryWriting_noDalle .section3 {
    display: flex;
    flex-direction: column;
    margin-bottom: 1.25em;
  }
 .diaryWriting_noDalle .section3 .text {
    margin-bottom: 0.625em;
  }
 .diaryWriting_noDalle .section3 select {
    margin-left: 0.625em;
    border-radius: 0.25em;
    border: 1px solid #c1ab86;
    outline: 0 none;
    text-align: center;
    width: 35%;
    height: 2.5em;
  }

 .diaryWriting_noDalle .section3 .selectBox .v-select {
    background: transparent;
    outline: 0 none;
    padding: 0 5px;
    position: relative;
    z-index: 3;
  }
 .diaryWriting_noDalle .section3 .selectBox .v-select option {
    color: #c1ab86;
    padding: 0.1875em 0;
    font-size: 1em;
    border-radius: 0.25em;
    text-align: center;
  }
 .diaryWriting_noDalle .section3 .selectBox .icoArrow {
    position: absolute;
    top: 0;
    right: 0;
    z-index: 1;
    width: 2.1875em;
    height: inherit;
    display: flex;
    justify-content: center;
    align-items: center;
  }

 .diaryWriting_noDalle .section3 .selectBox .icoArrow img {
    width: 50%;
    transition: 0.3s;
  }

 .diaryWriting_noDalle .section3 .selectBox .v-select:focus + .icoArrow img {
    transform: rotate(180deg);
  }

 .diaryWriting_noDalle .section4 {
    display: flex;
    flex-direction: column;
    margin-bottom: 1.25em;
    margin-left: 0.3125em;
    width: 95%;
    height: 50%;
  }
 .diaryWriting_noDalle .section4 .text {
    margin-bottom: 0.625em;
  }
 .diaryWriting_noDalle .section4 textarea {
    width: 100%;
    height: 100%;
    padding: 0.625em;
    font-size: 1.25em;
    overflow: auto;
    resize: vertical;
  }
 .diaryWriting_noDalle .section4 textarea:focus {
    outline: 2px solid #c1ab86;
  }

 .diaryWriting_noDalle .section5 {
    width: 95%;
    display: flex;
    flex-direction: row;
    margin-bottom: 1.25em;
    margin-left: 0.3125em;
  }
 .diaryWriting_noDalle .section5 .text {
    margin-bottom: 0.625em;
  }

 .diaryWriting_noDalle .section5 select {
    width: 35%;
    height: 2.5em;
    margin-left: 0.625em;
    border-radius: 0.25em;
    border: 1px solid #c1ab86;
    outline: 0 none;
    text-align: center;
    margin-right: 0.3125em;
  }

 .diaryWriting_noDalle .section5 button {
    width: 35%;
    margin-right: 0.3125em;
    text-align: center;
  }

 .diaryWriting_noDalle .section5 button:hover {
    width: 40%;
    opacity: 1;
    background: #604e2e;
    color: white;
    transition: 0.3s;
  }

</style>
