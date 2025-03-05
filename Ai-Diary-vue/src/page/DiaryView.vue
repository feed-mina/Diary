
<script>
import { computed, ref, watchEffect, onMounted } from 'vue';
import {useRouter, useRoute } from "vue-router";
import axios from "axios";
import Cookies from "universal-cookie";
import Swal from "sweetalert2";

export default {
  name: 'DiaryView',
  setup() {
    const router = useRouter();
    const route = useRoute(); // 현재 라우트 정보 가져오기
    const cookies = new Cookies();
    const diaryData = ref(null);
    const diaryContentItem =  ref({
      date: '', // YYYY-MM-DD 형식으로 날짜 설정
      author:'',
      title:'',
      tags:({ tag1: '', tag2: '', tag3: '' }),
      emotion:'',
      content:" ",
      hidden:true,
      emotionItems: [
          { text: "😁 기분이 좋아요", value: "1" },
          { text: "😂 너무 웃겨요", value: "2" },
          { text: "😫 어떡해야 할까요?!", value: "3" },
          { text: "😒 불쾌하고 지루해요", value: "4" },
          { text: "😤 어떻게 이럴 수가", value: "5" },
          { text: "😡 화가 나요", value: "6" },
          { text: "🤯 여기서 벗어나고 싶어요...", value: "7" },
          { text: "💖 사랑이 넘쳐요", value: "8" },
          { text: "🤕 몸 상태가 좋지 않아요", value: "9" },
          { text: "💙 우울해요", value: "10" }
      ],
    });
    const showOnlyMine = ref(false); // 내가 쓴 일기만 보기 체크박스
    // URL에서 diaryId 추출
    // const diaryId = computed(() => {
    //   return route.params.id || cookies.get("diaryId"); // URL 없으면 쿠키에서 가져옴
    // });
    
    const diaryId = route.params.diaryId; 
    const userId = route.query.userId;
    const jwtToken = cookies.get("jwt")?.jwt; // 쿠키에서 jwt 속성 가져오기
    
    console.log("🛠 route.params: ", route.params);
    console.log("🛠 Extracted diaryId:", diaryId);
    console.log("🛠 Extracted userId:", userId);
      if (!diaryId) {
        console.warn("🚨 diaryId가 없음 (API 요청 중단)");
        return;
      }


      const fetchDiaryDetails = async () => {
      if (!diaryId) {
        console.warn("🚨 diaryId가 없음 (API 요청 중단)");
        return;
      }
      if (!userId) {
        console.warn("🚨 userId가 없음 (API 요청 중단)");
        return;
      }
      try {
        const response = await axios.get(`http://localhost:8080/api/diary/getDiaryItem/${diaryId}?userId=${userId}`, {
          headers: {
            Authorization: `Bearer ${jwtToken}`,
            'Content-Type': 'application/json',
          }
        });

        diaryData.value = response.data;
        console.log("📌 서버 응답 데이터:", diaryData.value);
      } catch (error) {
        console.error('Error fetching diary details:', error);
      }
    };

    

    const getDiaryItem = async () => {
      if (!diaryId) {
        console.warn("🚨 diaryId가 없음 (API 요청 중단)");
        return;
      }

      if (!userId) {
    console.warn("🚨 userId가 없음 (API 요청 중단)");
    return;
  }
  
      try {
        const response = await axios.get(`http://localhost:8080/api/diary/viewDiaryItem/${diaryId}`, {
          headers: {
            Authorization: `Bearer ${jwtToken}`,
            'Content-Type': 'application/json',
          },
          params: {
            userId: userId, // 체크박스 상태에 따라 userId필터링
          },
        });

        Object.assign(diaryContentItem.value, response.data);
        // 응답 데이터 설정
        const objectResponse = Object.assign(diaryContentItem.value, response.data);
        console.log(objectResponse);
        // 숨겨진 일기인지 확인
        if(diaryContentItem.value.hidden && diaryContentItem.value.userId!== userId){
          alert('접근 권한이 없습니다.');
          Swal.fire({
            title:"본인확인",
            text : '접근 권한이 없습니다.',
            icon:"warning",
            confirmButtonText :"취소",
            confirmButtonColor : "#FFA500",
            background : "#f5f5f5",
            color:"#999"
          });

        }
        return response.data;
      } catch (error) {
        console.error('Error fetching diary list: ', error);
      }
      
    };

    watchEffect(() => {
      if (diaryId) {
        console.log("✅ diaryId 감지됨:", diaryId);
        getDiaryItem();
        fetchDiaryDetails();
      }
    });

    onMounted(async () => {
  const requestUrl = `http://localhost:8080/api/diary/getDiaryItem/${diaryId}?userId=${userId}`;
  console.log("📌 상세 페이지 요청 URL:", requestUrl);

  await fetchDiaryDetails(diaryId, userId);
});

    const sendDiaryContentItem = async () => {

      try {
        // const { title, date, author, tags, emotion, content, hidden} = diaryContentData.value; 

        console.log("jwtToken: " , jwtToken);
        if (!jwtToken) {
          Swal.fire({
            title:"로그인 필요!",
            text : "로그인한 사람만 가능합니다.",
            icon:"warning",
            confirmButtonText :"로그인페이지이동",
            confirmButtonColor : "#FF5733",
            background : "#f5f5f5",
            color:"#999"
          }).then(() =>{
            router.push("/login");            
          })
          // alert("로그인한 사람만 가능합니다.");
          return;
        }

        const diaryDataToSave = {
          // pageNo: 1,
          // pageSize: 10,
          ...sendDiaryContentItem.value,
          diaryStatus: diaryContentItem.value.hidden ? 'true' : 'false', // Boolean을 문자열로 변환
          userId,
        };
        console.log('diaryDataToSave',diaryDataToSave);
        
        const response = await axios.post(`http://localhost:8080/api/diary/getDiaryList2/${diaryContentItem.value.diaryId}/${userId}`, diaryDataToSave,{
          headers: {
            Authorization: `Bearer ${jwtToken}`,
            "Content-Type": "application/json",
            "X-Forwarded-For": "127.0.0.1",
          },
          withCredentials: true, // 쿠키 인증 허용

        });
        
        console.log('response',response);
        // alert("일기장이 저장되었습니다");
        Swal.fire({
            title:"로그인 필요!",
            text : "로그인한 사람만 가능합니다.",
            icon:"success",
            confirmButtonText :"좋아요 !",
            confirmButtonColor : "#A5778F",
            background : "#f5f5f5",
            color:"#999"
          }).
        router.push("/diary/common").then(() => location.reload());
        return response.data;
      } catch (error) {
        console.error("API 호출 실패", error);
        // alert("일기장 저장 중 오류가 발생했습니다.");
        Swal.fire({
            title:"저장 실패!",
            text : "일기장 저장 중 오류가 발생했습니다.",
            icon:"error",
            confirmButtonText :"확인",
            confirmButtonColor : "#FF5733",
            background : "#f5f5f5",
            color:"#999"
          });
        if (error.response && error.response.status === 400) {
          // alert("일기장 저장이 되지 않았습니다. 다시 시도해주세요.");
          // alert(error.response.data); 
          Swal.fire({
            title:"저장 불가",
            text : error.response.data,
            icon:"warning",
            confirmButtonText :"다시시도.",
            confirmButtonColor : "#FFA500",
            background : "#f5f5f5",
            color:"#999"
          });
          // errorMessage.value.email = error.response.data;
        } else {
          console.error("API 호출 실패", error);
          // alert("일기장 저장이 되지 않았습니다. 다시 시도해주세요.");      
          Swal.fire({
            title:"API 호출 실패",
            text : error.response.data,
            icon:"warning",
            confirmButtonText :"다시 시도",
            confirmButtonColor : "#FFA500",
            background : "#f5f5f5",
            color:"#999"
          });
        }
      }
    };

    console.log(`📌 요청 URL1:  http://localhost:8080/api/diary/getDiaryItem/${diaryId}?userId=${userId}`);

    console.log(`📌 요청 URL2: http://localhost:8080/api/diary/viewDiaryItem/${diaryId}?userId=${userId}`);
  return {    
    diaryContent: diaryContentItem,
    showOnlyMine,
    diaryData,
  };
}
}
</script>

<template>
  <div class="diaryView">
    <div class="diaryView_content">
      <div class="diaryTuto">
        <div class="diaryView_container"  v-if="diaryData">
          <form>
            <div class="diaryViewTitle">
            </div>
            <div class="diaryView_noDalle">
              <div class="section0">
                <div><p>날짜 :  {{ new Date(diaryData.date).toLocaleDateString() }}</p></div>
              </div>
              <div class="section01">
                <div>
                    <label for="author">작성자&nbsp;&nbsp;&nbsp;</label>
                  <!-- <input type="text" class="author" id="author" name="author" :value="diaryData.author" placeholder=""  readonly :disabled="false"/> -->
                   <p class="author" id="author" name="author">작성자: {{ diaryData.author || '익명' }}</p>
                    <div class="titleSc">
                      <label for="title">제목&nbsp;&nbsp;&nbsp;</label>
                  <!-- <input type="text" class="title" id="title" name="title" :value="diaryData.title" placeholder="" readonly :disabled="false"/> -->
                   <p class="title" id="title" >
                    {{ diaryData.title }}
                   </p>
                  </div>
                </div>
              </div>
              <!--section2-->
              <div class="section2">
                <div class="tags">
                  <label class="tagSection">
                    태그 : 
                  </label>
                    <!-- <input type="text" id="tag1" name="tag1" v-model="diaryData.tags.tag1" placeholder="" disabled/> -->
               <p id="tag1" class="tag1">
                      {{ diaryData.tags.tag1 }}
                    </p>
                  
<!--      
                    <input type="text" id="tag2" name="tag2" v-model="diaryData.tags.tag2" placeholder="" disabled/> -->
                    <p id="tag2" class="tag2">
                      {{ diaryData.tags.tag2 }}
                    </p>
                  
                    <input type="text" id="tag3" name="tag3" v-model="diaryData.tags.tag3" placeholder="" disabled/>
                    <p id="tag3" class="tag3">
                      {{ diaryData.tags.tag3 }}
                    </p>
                  <!-- <button type="button" class="aiButton">
                     <div  v-tooltip="'사진이 생성되는 동안 일기를 작성할 수  없습니다.'">
                      <span>AI 이미지 생성</span>
                     </div>
                  </button> -->
                </div>
              </div>
              <!--section3-->
              <div class="section3">
                <div class="text">
                      <span>감정지수를 선택하세요.</span>
                </div>
                <div class="selectBox">
                  <!-- <v-select
                  v-model="diaryData.emotion"
                  :items="diaryData.emotionItems"
                  item-title="text"
                  item-value="value"
                  label="오늘의 감정지수는?"
                  class="emotion"
                  id="emotion"
                  required
                  ></v-select> -->
                  <label>
                    감정지수
                  </label>
                  <p class="emotion"
                  id="emotion">
                    {{ diaryData.emotion }}
                  </p>
                  <p>
                    {{ diaryData.emotionItems }}
                  </p>
                </div>
              </div>
              <!--section4-->
              <div class="section4">
                <div class="text">
                      <span>일기장</span>
                </div>
                <!-- <textarea v-model="diaryData.content" rows="3" class="content" name="content" id="content" disabled></textarea> -->
                 <p class="content" id="content">
                  {{ diaryData.content }}
                 </p>
              </div>
              <!--section05-->
              <div class="section05">
                      <span>🔎</span>
                    <!-- <select v-model="diaryData.hidden" id="hidden" required>
                      <option value="true">
                        숨기기
                      </option>
                      <option value="false">
                        보여주기
                      </option>
                    </select> -->
              </div>
              <div class="saveDiary">
                      <button type="button" @click="updateDiary">수정하기</button>
              </div>
              <!--diaryTuto-dalle-->
            </div>
          </form>
        </div>

        
        <div v-else>
          <p>일기를 불러오는 중입니다...</p>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>


.titleSc{
    padding-top:2rem;
  }
.diaryView{
  display: flex;
  justify-content: center;
 /* height: 100vh;  화면 전체 높이를 채움 */
  background-color: #f8f9fa; /* 배경색 */
  padding: 10px;
}

.diaryView_content {
  height: 100%;
  width: 100%; /* 모바일에서는 전체 너비 사용 */
  padding: 1rem;
  display: flex;
  flex-direction: column; /* 수직 정렬 */
  align-items: center; /* 수평 중앙 정렬 */
  }

  .diaryTuto {
    padding-top: 1.5em;
    width: 90%; /* 화면 크기 따라 자동 조정 */
    display: flex;
    flex-direction: column; /* 수직 정렬 */
    justify-content: center;
  
    /* overflow-y: auto; 스크롤 가능 */
    /* z-index: 9999; */
    font-size: 1rem;
  }

  .diaryTuto input,
  textarea{
    border-radius: 0.3125em;
    background: #eee7db;
    border: 0 solid black;
    font-size: 1rem;
  }
  .diaryView_container {
    /* border: 1px solid #00fa9a; */
    margin: 0;
    width: 100%;
     display: flex;
    flex-direction: row;
  }

  .diaryViewTitle {
    font-size: 2rem;
    text-align: center;
    font-weight: bold;

  }
  .diaryViewTitle {
    box-shadow: inset 0 -23px 0 #e89b3d;
    display: inline;
  }

  .diaryView_container form {
    width: 100%;
    height: 100%;
  }
  .diaryView_noDalle {
    width: 65%;
    height: 100%;
    margin: 0em;
    float: left;
  }
  /* .diaryView_noDalle input,
  select,
  button,
  option {
    height: 1.875em;
  } */

  .diaryView_container input:focus {
    outline: 2px solid #c1ab86;
    transition: 0.1s;
  }


  .diaryView_noDalle .section0 {
    width: 100%;
    display: flex;
    flex-direction: row;
    margin-bottom: 0.9375em;
  }
  .diaryView_noDalle .section01 {
    margin-top: 0.125em;
    width: 100%;
    display: flex;
    flex-direction: row;
    margin-bottom: 0.9375em;
  }

  .diaryView_noDalle .section01 input {
    width: 40%;
    text-align: center;
  }

 .diaryView_noDalle .section1 input:hover {
    transition: 0.3s;
    opacity: 1;
    outline: 2px solid #c1ab86;
    width: 42%;
  }

 .diaryView_noDalle .section2 {
    width: 100%;
    margin-bottom: 0.625em;
  }
 .diaryView_noDalle .section2 .tags {
    padding-top: 0.625em;
    width: 95%;
    display: flex;
    flex-direction: row;
    justify-content: space-evenly;
  }
 .diaryView_noDalle .section2 input {
    text-align: center;
    width: 25%;
    margin: 0.3125em;
    caret-color: #604e2e;
  }
 .diaryView_noDalle .section2 input:hover {
    transition: 0.3s;
    opacity: 1;
    outline: 2px solid #c1ab86;
    width: 27%;
  }
 /* .diaryView_noDalle .section2 button {
    width: 25%;
    margin: 0.3125em;
    height: 2em;
    background: #c1ab86;
  } */
 .diaryView_noDalle .section2 button:hover {
    transition: 0.3s;
    opacity: 1;
    background: #604e2e;
    color: white;
    width: 30%;
  }
 .diaryView_noDalle .section3 {
    display: flex;
    flex-direction: column;
    margin-bottom: 1.25em;
  }
 .diaryView_noDalle .section3 .text {
    margin-bottom: 0.625em;
  }
 .diaryView_noDalle .section3 select {
    margin-left: 0.625em;
    border-radius: 0.25em;
    border: 1px solid #c1ab86;
    outline: 0 none;
    text-align: center;
    width: 35%;
    height: 2.5em;
  }

 .diaryView_noDalle .section3 .selectBox .v-select {
    background: transparent;
    outline: 0 none;
    padding: 0 5px;
    position: relative;
   }
 .diaryView_noDalle .section3 .selectBox .v-select option {
    color: #c1ab86;
    padding: 0.1875em 0;
    font-size: 1em;
    border-radius: 0.25em;
    text-align: center;
  }
 .diaryView_noDalle .section3 .selectBox .icoArrow {
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

 .diaryView_noDalle .section3 .selectBox .icoArrow img {
    width: 50%;
    transition: 0.3s;
  }

 .diaryView_noDalle .section3 .selectBox .v-select:focus + .icoArrow img {
    transform: rotate(180deg);
  }

 .diaryView_noDalle .section4 {
    display: flex;
    flex-direction: column;
    margin-bottom: 1.25em;
    margin-left: 0.3125em;
    width: 95%;
  }
 .diaryView_noDalle .section4 .text {
    margin-bottom: 0.625em;
  }
 .diaryView_noDalle .section4 textarea {
    width: 100%;
    padding: 0.625em;
    font-size: 1rem;
    overflow: auto;
    resize: vertical;
  }
 .diaryView_noDalle .section4 textarea:focus {
    outline: 2px solid #c1ab86;
  }

 .diaryView_noDalle .section5 {
    width: 95%;
    display: flex;
    flex-direction: row;
    margin-left: 0.3125em;
  }
 .diaryView_noDalle .section5 .text {
    margin-bottom: 0.625em;
  }

 .diaryView_noDalle .section5 select {
    width: 35%;
    margin-left: 0.625em;
    border-radius: 0.25em;
    border: 1px solid #c1ab86;
    outline: 0 none;
    text-align: center;
    margin-right: 0.3125em;
  }

.saveDiary{
  display: flex;
  flex-direction: column;
  align-content: center;
  justify-content: center;
    margin-top:2rem;
    text-align: center;
    width: 120px;
    height: 40px;
    font-size: 1rem;
    font-weight: bold;
    color: #fff;
    background: #A5778F;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: all 0.3s;
}
</style>

