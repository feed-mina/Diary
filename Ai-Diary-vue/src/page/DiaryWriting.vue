<<<<<<< HEAD
<script>
import axios from "axios";
import {ref} from 'vue';
import {useRouter} from "vue-router";
import Cookies from 'universal-cookie'; // universal-cookie
import Datepicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';
import Swal from 'sweetalert2';

export default {
  name: 'DiaryWriting', // 다중 단어 이름으로 변경
  components: {
    Datepicker,
  },
  setup() {
    const router = useRouter();
    const loginToken = localStorage.getItem('jwtToken'); // 저장된 토큰 가져오기

    // 쿠키 객체 생성
    const cookies = new Cookies();
    const userId = localStorage.getItem("userId");
    console.log("userId : ", userId);
    const diaryContentData = ref({
      userId: userId || "",
      date: "",
      author: "",
      title: "",
      tags: ({tag1: "", tag2: "", tag3: ""}),
      emotion: "",
      content: "",
      hidden: true,
    });

    const emotionItems = [
      {text: "😁 기분이 좋아요", value: "1"},
      {text: "😂 너무 웃겨요", value: "2"},
      {text: "😫 어떡해야 할까요?!", value: "3"},
      {text: "😒 불쾌하고 지루해요", value: "4"},
      {text: "😤 어떻게 이럴 수가", value: "5"},
      {text: "😡 화가 나요", value: "6"},
      {text: "🤯 여기서 벗어나고 싶어요...", value: "7"},
      {text: "💖 사랑이 넘쳐요", value: "8"},
      {text: "🤕 몸 상태가 좋지 않아요", value: "9"},
      {text: "💙 우울해요", value: "10"}
    ];


    // axios.interceptors.request.use(
    //     (config) => {
    //       console.log("Axios 요청 설정:", config);
    //       return config;
    //     },
    //     (error) => {
    //       console.error("Axios 요청 에러:", error);
    //       return {success: false, error: error.response?.data || "오류가 발생했습니다."};
    //     }
    // );

    // axios.interceptors.response.use(
    //     (response) => {
    //       console.log("Axios 응답 데이터:", response);
    //       return response;
    //     },
    //     (error) => {
    //       console.error("Axios 응답 에러:", error);
    //       return Promise.reject(error);
    //     }
    // );

    const showValidationError = (message) => {
      Swal.fire({
        title: "입력 오류!",
        text: message,
        icon: "warning",
        confirmButtonText: "확인",
        confirmButtonColor: "#FF5733"
      });
    };

    const sendDiaryContentData = async () => {
      try {
        const {userId, title, date, author, tags, emotion, content, hidden} = diaryContentData.value;

        // 값 검증
        if (!date || !author || !title || !emotion || !content) {
          // alert("필수 필드를 채워주세요.");
          Swal.fire({
            title: "입력 오류!",
            text: "필수 필드를 모두 입력해주세요.",
            icon: "warning",
            confirmButtonText: "확인",
            confirmButtonColor: "#FF5733"
          });
          return false;
        }
        /*
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
            */
        if (!date) {
          showValidationError("날짜를 입력해주세요.");
          return;
        }
        if (!author) {
          showValidationError("작성자를 입력해주세요.");
          return;
        }
        if (!title) {
          showValidationError("제목을 입력해주세요.");
          return;
        }
        if (!emotion) {
          showValidationError("감정지수를 선택해주세요.");
          return;
        }
        if (!content) {
          showValidationError("내용을 입력해주세요.");
          return;
        }
        const diaryDataToSave = {
          title,
          author,
          emotion,
          userId,
          date,
          content,
          tag1: tags.tag1,
          tag2: tags.tag2,
          tag3: tags.tag3,
          diaryStatus: hidden ? "true" : "false", // Boolean을 문자열로 변환
        }
        console.log('diaryDataToSave', diaryDataToSave);

        // const jwtToken = cookies.get("jwt")?.jwt; // 쿠키에서 jwt 속성 가져오기
        console.log("jwtToken: ", loginToken);
        if (!loginToken) {
          // alert("JWT 토큰이 없습니다. 다시 로그인해주세요.");
          Swal.fire({
            title: "로그인 필요!",
            text: "JWT 토큰이 없습니다. 다시 로그인해주세요.",
            icon: "warning",
            confirmButtonText: "로그인 페이지 이동",
            confirmButtonColor: "#FF5733"
          }).then(() => {
            router.push("/login");
          })
          return;
        }

        const response = await axios.post("http://localhost:8080/api/diary/addDiaryList", diaryDataToSave
        // , {
        //   headers: {
        //     Authorization: `Bearer ${jwtToken}`,
        //     "Content-Type": "application/json",
        //     "X-Forwarded-For": "127.0.0.1",
        //   },
        //   withCredentials: true, // 쿠키 인증 허용
        // }
      );

        console.log("jwtToken: ", loginToken);
        console.log('response', response);

        // alert("일기가 저장되었습니다.");

        Swal.fire({
          title: "기록 완료!",
          text: "당신의 소중한 일기가 저장되었습니다!",
          icon: "success",
          confirmButtonText: "확인",
          confirmButtonColor: "#A5778F",
          background: "#f5f5f5", // 배경색 변경
          color: "#333", // 글자색 변경

        }).then(() => {
          router.push("/diary/common");
        });
        router.push("/diary/common");
        return response.data;
      } catch (error) {
        if (error.response?.status === 401) {
          // alert("세션이 만료되었습니다. 다시 로그인해주세요.");
          Swal.fire({
            title: "세션 만료",
            text: "세션이 만료되었습니다. 다시 로그인해주세요.",
            icon: "warning",
            confirmButtonText: "로그인 페이지 이동",
            confirmButtonColor: "#FFA500"
          }).then(() => {
            router.push("/login");
          });

        } else {
          console.error("API 호출 실패:", error);
          // alert("일기 저장에 실패했습니다. 관리자에게 문의해주세요.");
          Swal.fire({
            title: "저장 실패!",
            text: "일기 저장 중 오류가 발생했습니다.",
            icon: "error",
            confirmButtonText: "확인",
            confirmButtonColor: "#FF5733"
          });

        }
        return {success: false, error: "서버 오류가 발생했습니다."}; // 실패 메시지 반환
      }
    };


    const onClicksaveDiaryButton = async () => {
      const result = await sendDiaryContentData();
      if (result?.error) {
        Swal.fire({
          title: "저장 실패!",
          text: "저장 실패: " + result.error,
          icon: "error",
          confirmButtonText: "확인",
          confirmButtonColor: "#FF5733"
        }).then(() => {
          router.push("/").then(() => location.reload());
        });

      }
      console.log('일기저장결과: ', result);
      if (result == 'false') {
        location.reload();
      } else {
        //  alert("일기가 저장되었습니다.");
      }
    };
    return {
      emotionItems,
      diaryContentData,
      onClicksaveDiaryButton,
    };
  },
}
</script>
=======
>>>>>>> rebase-branch
<template>
  <div class="diaryWriting">
    <div class="diaryWriting_content">
      <div class="diaryTuto">
        <div class="diaryWriting_container">
          <form>
            <div class="diaryWritingTitle">✍ 감정 다이어리 작성</div>

            <!-- 날짜 입력 -->
            <div class="section">
              <label>일기 날짜</label>
              <Datepicker v-model="diaryContentData.date" :format="'yyyy-MM-dd'" :auto-apply="true" :locale="'ko'" />
            </div>

            <!-- 작성자 & 제목 입력 -->
            <div class="section">
              <label>작성자</label>
              <input type="text" v-model="diaryContentData.author" placeholder="작성자 이름 입력" />

              <label>제목</label>
              <input type="text" v-model="diaryContentData.title" placeholder="일기 제목 입력" />
            </div>

            <!-- 태그 입력 -->
            <div class="section">
              <label>오늘의 감정 태그</label>
              <input type="text" v-model="diaryContentData.tags.tag1" placeholder="태그1" />
              <input type="text" v-model="diaryContentData.tags.tag2" placeholder="태그2" />
              <input type="text" v-model="diaryContentData.tags.tag3" placeholder="태그3" />
            </div>

            <!-- 감정 선택 -->
            <div class="section">
              <label>감정지수</label>
              <select v-model="diaryContentData.emotion">
                <option v-for="emotion in emotionItems" :key="emotion.value" :value="emotion.value">
                  {{ emotion.text }}
                </option>
              </select>
            </div>

            <!-- 본문 입력 -->
            <div class="section">
              <label>일기 내용</label>
              <textarea v-model="diaryContentData.content" rows="5" placeholder="오늘의 감정을 기록하세요"></textarea>
            </div>

            <!-- 공개 여부 선택 -->
            <div class="section">
              <label>공개 설정</label>
              <button type="button" :class="{'active-button': diaryContentData.hidden}" @click.prevent="diaryContentData.hidden = true">비공개</button>
              <button type="button" :class="{'active-button': !diaryContentData.hidden}" @click.prevent="diaryContentData.hidden = false">공개</button>
            </div>

            <!-- 저장 버튼 -->
            <div class="section">
              <button type="button" class="save-button" @click="onClickSaveDiary">📝 기록하기</button>
            </div>

          </form>
        </div>
      </div>
    </div>
  </div>
</template>


<style scoped>
.section {
  margin-bottom: 15px;
}

input, textarea, select {
  width: 100%;
  padding: 8px;
  border-radius: 5px;
  border: 1px solid #ddd;
}

.save-button {
  width: 100%;
  padding: 10px;
  background: #00796b;
  color: white;
  border-radius: 5px;
  font-weight: bold;
}

.active-button {
  background-color: #a48f7a;
  color: white;
}
</style>
