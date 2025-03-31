<script>
import axios from "axios";
import { ref, computed, onMounted, reactive } from 'vue';
import { useRouter } from "vue-router";
// Datepicker 컴포넌트 등록
import Datepicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/vue-datepicker.css';

import Swal from 'sweetalert2';
// import {apiUrl} from "@/api/index.js";

export default {
  name: "DiaryWriting",
  components: { Datepicker },
  setup() {
    const router = useRouter();

    // ✅ 로그인 상태 체크 (localStorage 활용)
    const isLoggedIn = computed(() => {
      return !!localStorage.getItem("jwtToken") || !!localStorage.getItem("kakaoAccessToken");
    });

    // ✅ 로그인한 유저 ID 확인
    const userId = ref(localStorage.getItem("userId")  || "");
    const token = ref(localStorage.getItem("jwtToken")  || "");
    const nickname = ref(localStorage.getItem("nickname")  || "");
    const email = ref(localStorage.getItem("email")  || "");
    const kakaoToken = ref(localStorage.getItem("kakaoToken")  || "");

    // ✅ 초기 다이어리 데이터
    const diaryContentData = reactive({
      email : email.value,
      userId: userId.value,
      token : token.value,
      nickname : nickname.value,
      date: "",
      author: "",
      title: "",
      tags: { tag1: "", tag2: "", tag3: "" },
      emotion: "",
      content: "",
      hidden: true, // 기본적으로 비공개
    });

    const tagsAsMap = new Map(Object.entries(diaryContentData.tags));
    console.log("tagsAsMap :", tagsAsMap);
    const emotionItems = [
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
    ];

    // ✅ 로그인되지 않은 경우 로그인 페이지로 이동
    onMounted(() => {
      if (!isLoggedIn.value) {
        Swal.fire({
          title: "로그인이 필요합니다!",
          text: "로그인 후 일기를 작성할 수 있습니다.",
          icon: "warning",
          confirmButtonText: "로그인하기",
          confirmButtonColor: "#FF5733",
        }).then(() => {
          router.push("/").then(() => {
            location.reload(); // 새로고침
          });
        });
      }
    });

    // ✅ 일기 저장 함수
    const onClickSaveDiary = async () => {

      let headers = {
        "Content-Type": "application/json",
      };

      if (token) {
        headers["Authorization"] = `Bearer ${token}`;
      } else if (kakaoToken) {
        headers["Authorization"] = `Bearer ${kakaoToken}`;
      } else if (email) {
        headers["Authorization"] = `Bearer ${email}`;
      }

      // ✅ tags를 개별 필드로 변환하여 전송
      const diaryData = {
        email: diaryContentData.email,
        userId: diaryContentData.userId,
        token: diaryContentData.token,
        nickname: diaryContentData.nickname,
        date: diaryContentData.date,
        author: diaryContentData.author,
        title: diaryContentData.title,
        content: diaryContentData.content,
        emotion: diaryContentData.emotion,
        hidden: diaryContentData.hidden,
        tag1: diaryContentData.tags.tag1,
        tag2: diaryContentData.tags.tag2,
        tag3: diaryContentData.tags.tag3,
      };

      try {
        const response = await axios.post(`/api/diary/addDiaryList`,
            diaryData,
            {
              headers: {
                "Content-Type": "application/json",
              }
            }
        )
        console.log("diaryContentData.value:", diaryContentData.value);
        console.log("@@@일기 저장 응답:", response.data);
        console.log("JSON 데이터:", JSON.stringify(diaryContentData.value));

        if (response.data.success) {

          Swal.fire("기록 완료!", "일기가 저장되었습니다.", "success").then(() => {
            router.push("/diary/common");
          });
        } else {
          Swal.fire("저장 실패!", "일기 저장 중 오류가 발생했습니다.", "error");
        }
      } catch (error) {
        console.error("일기 저장 실패:", error);
        Swal.fire("저장 실패!", "일기 저장 중 오류가 발생했습니다.", "error");
      }
    };

    return {
      diaryContentData,
      emotionItems,
      onClickSaveDiary,
    };
  }
};
</script>
<template>
  <div class="diaryWriting">
    <div class="diaryWriting_content">
      <div class="diaryTuto">
        <div class="diaryWriting_container">
          <form>
            <div class="diaryWritingTitle">✍ 감정 다이어리 작성</div>
            <!-- 날짜 입력 -->
            <div class="write_section1">
              <Datepicker id="datepickerInput" v-model="diaryContentData.date" :format="'yyyy-MM-dd'" :auto-apply="true" :locale="'ko'"/>
              <label>일기 날짜</label>
            </div>
            <!-- 작성자 & 제목 입력 -->
            <div class="section">
              <label>작성자</label>
              <input type="text" v-model="diaryContentData.author" placeholder="작성자 이름 입력"/>
              <label>제목</label>
              <input type="text" v-model="diaryContentData.title" placeholder="일기 제목 입력"/>
            </div>
            <!-- 태그 입력 -->
            <div class="section">
              <label>오늘의 감정 태그</label>
              <input type="text" v-model="diaryContentData.tags.tag1" placeholder="태그1"/>
              <input type="text" v-model="diaryContentData.tags.tag2" placeholder="태그2"/>
              <input type="text" v-model="diaryContentData.tags.tag3" placeholder="태그3"/>
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
            <div class="section_status">
              <label>공개 설정</label>
              <button type="button" :class="{'active-button': diaryContentData.hidden}"
                      @click.prevent="diaryContentData.hidden = true">비공개
              </button>
              <button type="button" :class="{'active-button': !diaryContentData.hidden}"
                      @click.prevent="diaryContentData.hidden = false">공개
              </button>
            </div>
            <!-- 저장 버튼 -->
            <div class="write_button_section">
              <button type="button" class="save-button" @click="onClickSaveDiary">📝 기록하기</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>
